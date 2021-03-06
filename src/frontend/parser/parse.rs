use numsc::Lexer;
use crate::frontend::ast::AST;
use crate::frontend::nodes::{decl::Decl, expr::Expr, stmt::Stmt};
use crate::{
    frontend::source::Source,
    error_handling::faults::{ErrTyp::*, ParseError},
};
use numsc::structures::tokens::Token;
use super::peekable_parser::Peekable;
use crate::{create_expr, match_adv};
use super::spanner::Spanner;

pub struct Parser<'source> {
    tokens: Peekable<'source>,
    source: std::rc::Rc<Source>,
    spanner : Spanner
}

///iterator methods
impl<'source> Parser<'source> {
    /// advances, expecting to there to be a token
    fn expect_token(&mut self, token: &Token) -> Result<Token, ParseError> {
        match self.peek() {
            Some(tok) if tok == token => Ok(self.next().unwrap()),
            Some(_) => { 
                Err(self
               .next()
               .map(|tok| ParseError::new( UnexpectedToken(tok),None,None))
               .unwrap_err())
            },
            None => Err(ParseError::new(UnexpectedEndOfParsing,None,None))
        }
    }
    fn peek(&mut self) -> Option<&Token> {
        self.tokens.peek().map(|s| &s.0)
    }

    fn next(&mut self) -> Result<Token, ParseError> {
        {
            let this = self.tokens
                .next()
                .map(|s| { self.spanner.next(&s.1); s.0 } );
            let err = ParseError::new(UnexpectedEndOfParsing, None, None);
            match this {
                Some(v) => Ok(v),
                None => Err(err),
            }
        }
    }
    fn resolve_node<T>(&mut self, node: T) -> Result<T, ParseError> {
        self.spanner.converge();
        Ok(node)
    }
    /// check if token matches the current peeked token in the iterator
    fn check_peek(&mut self, token: &Token) -> bool {
        self.peek() == Some(token)
    }
    fn is_at_end(&mut self) -> bool {
        matches!(self.peek(), None)
    }
}


/// Basic recursive descent parsing
/// Errors are made using Spans, which will pretty print the error and its possible (not tested if accurate) location
impl<'source> Parser<'source> {
    pub fn new(tokens: Lexer<'source, Token>, source: std::rc::Rc<Source>) -> Self {
        Self {
            tokens: Peekable::new(tokens),
            source: source.clone(),
            spanner : Spanner::new(source)
        }
    }

    pub fn parse(&mut self) -> Result<AST, Vec<ParseError>> {
        let mut diagnostic_vec = Vec::new();
        let mut tree = Vec::new();
        let mut had_parse_err = false;
        loop {
            if self.is_at_end() {
                if had_parse_err {
                    break Err(diagnostic_vec);
                }
                break Ok(AST::new(tree));
            }
            match self.top_level() {
                Ok(decl) => tree.push(decl),
                Err(e) => {
                    had_parse_err = true;
                    diagnostic_vec.push(e);
                    self.spanner.converge();
                    self.synchronize();
                }
            }
        }
    }

    fn synchronize(&mut self) {
        while let Some(token) = self.peek() {
            match token {
                &Token::Function => break,
                _ => {
                    let is_semi = self.next().map(|t| t == Token::Semi).unwrap();
                    if is_semi {
                        break;
                    } else {
                        continue;
                    }
                }
            }
        }
    }

    /// top level declarations. If it finds anything that
    fn top_level(&mut self) -> Result<Decl, ParseError> {
        let tok = self.peek().unwrap();
        match &tok {
            Token::Expose => {
                self.next().unwrap();
                match self.peek() {
                    Some(tok) if tok == &Token::Function => self.parse_fn(true),
                    _ => {
                        let other = self.next()?;
                        Err(ParseError::new(
                            UnexpectedToken(other),
                            None,
                            None,
                        ))
                    }
                }
            }
            Token::Start => self.parse_main(),
            Token::Function => self.parse_fn(false),
            Token::Use => self.parse_get(),
            _ => Err(ParseError::new(UnexpectedEndOfParsing,None, None))
        }
    }

    fn parse_get(&mut self) -> Result<Decl, ParseError> {
        let expr = self.next().and_then(|_| self.expr());
        self.expect_token(&Token::Semi)?;
        Ok(Decl::Use(expr?))
    }

    fn statements(&mut self) -> Result<Stmt, ParseError> {
        match self.peek() {
            Some(tok) => match tok {
                &Token::While => self.while_loop(),
                &Token::Let | &Token::Mut => self.var_decl(),
                &Token::If => self.if_else(),
                &Token::Return => self.next().and_then(|_| self.expr()).and_then(|e| {
                    self.expect_token(&Token::Semi)?;
                    self.resolve_node(Stmt::Return(e))
                }),
                &Token::LeftBrace => self
                    .block()
                    .and_then(|it| self.resolve_node(Stmt::Block(it))),
                _ => self.stmt_expr(),
            },
            None => Err(ParseError::new(UnexpectedEndOfParsing,None, None))
        }
    }

    fn if_else(&mut self) -> Result<Stmt, ParseError> {
        let condition = self.next().and_then(|_| self.expr())?;
        let block = self.block()?;
        let else_block = if (match_adv!(&mut self, &Token::Else)).is_some() {
            Some(self.block()?)
        } else {
            None
        };
        Ok(Stmt::IfElse(condition, block, else_block))
    }

    fn while_loop(&mut self) -> Result<Stmt, ParseError> {
        let condition = self.next().and_then(|_| self.expr());
        let block = self.block();
        self.resolve_node(Stmt::While(condition?, block?))
    }

    fn var_decl(&mut self) -> Result<Stmt, ParseError> {
        let mut_state = self.next()?;
        let name = self.get_name()?;

        self.expect_token(&Token::Assign)?;
        let var_val = self.expr().and_then(|e| {self.expect_token(&Token::Semi)?; Ok(e)});
        if mut_state == Token::Mut {
            self.resolve_node(Stmt::Mut(name, var_val?))
        } else {
            self.resolve_node(Stmt::Let(name, var_val?))
        }
    }

    fn stmt_expr(&mut self) -> Result<Stmt, ParseError> {
        let value_of_statement = self.expr();
        self.expect_token(&Token::Semi).and_then(|_|{
            self.resolve_node(Stmt::ExprStatement(value_of_statement?))
        })
    }

    fn expr(&mut self) -> Result<Expr, ParseError> {
        self.assignment()
    }

    fn assignment(&mut self) -> Result<Expr, ParseError> {
        let asignee = self.or();
        if (match_adv!(&mut self, &Token::Assign)).is_some() {
            let assignment = self.assignment();
            match asignee {
                Ok(e) if matches!(e, Expr::Val(_)) => {
                    let var = self.get_name()?;
                    self.resolve_node(Expr::Assignment {
                        var,
                        value: Box::new(assignment?),
                    })
                }
                other => Err(ParseError::new(InvalidAssignmentTarget(other?),None, None))
            }
        } else {
            asignee
        }
    }
    

    fn or(&mut self) -> Result<Expr, ParseError> {
        let mut left = self.and();
        while let Some(operator) = match_adv!(&mut self, &Token::Or) {
            let right = self.and();
            left = create_expr!(&mut self, logical: operator, left, right)
        }
        left
    }

    fn and(&mut self) -> Result<Expr, ParseError> {
        let mut left = self.equality();
        while let Some(operator) = match_adv!(&mut self, &Token::And) {
            let right = self.equality();
            left = create_expr!(&mut self, logical: operator, left, right)
        }
        left
    }

    fn equality(&mut self) -> Result<Expr, ParseError> {
        let mut left = self.compare();
        while let Some(operator) = match_adv!(&mut self, &Token::Eq | &Token::NotEq) {
            let right = self.compare();
            left = create_expr!(&mut self, binary: operator, left, right);
        }
        left
    }

    fn compare(&mut self) -> Result<Expr, ParseError> {
        let mut left = self.term();
        while let Some(operator) = match_adv!(
            &mut self,
            &Token::LessEq | &Token::GreaterEq | &Token::LeftArr | &Token::RightArr
        ) {
            let right = self.term();
            left = create_expr!(&mut self, binary: operator, left, right);
        }
        left
    }

    fn term(&mut self) -> Result<Expr, ParseError> {
        let mut left = self.factor();
        while let Some(operator) = match_adv!(&mut self, &Token::Minus | &Token::Plus) {
            let right = self.factor();
            left = create_expr!(&mut self, binary: operator, left, right);
        }
        left
    }

    fn factor(&mut self) -> Result<Expr, ParseError> {
        let mut left = self.power();
        while let Some(operator) = match_adv!(&mut self, &Token::Star | &Token::FowardSlash) {
            let right = self.power();
            left = create_expr!(&mut self, binary: operator, left, right);
        }
        left
    }
    ///right associative
    fn power(&mut self) -> Result<Expr, ParseError> {
        let left = self.unary();
        if let Some(operator) = match_adv!(&mut self, &Token::Caret) {
            let right = self.power();
            create_expr!(&mut self, binary: operator, left, right)
       } else {
            left
        }
    }
    /// right associative unary parser
    fn unary(&mut self) -> Result<Expr, ParseError> {
        if let Some(operator) = match_adv!(&mut self, &Token::Bang | &Token::Minus | &Token::Plus) {
            let expr = self.unary();
            create_expr!(&mut self, unary: operator, expr)
        } else {
            self.callee()
        }
    }

    

    fn callee(&mut self) -> Result<Expr, ParseError> {
        let mut expr = self.grouping();
        loop {
            expr = if (match_adv!(&mut self, &Token::LeftParen)).is_some() {
                let call = self.finish_call(expr)?;
                self.expect_token(&Token::RightParen)?;
                self.resolve_node(call)
            } else if (match_adv!(&mut self, &Token::Colon)).is_some() {
                let name = self.get_name()?;
                self.resolve_node(Expr::Get(Box::new(expr?), name))
            } else {
                break expr;
            }
        }
    }

    fn finish_call(&mut self, expr: Result<Expr, ParseError>) -> Result<Expr, ParseError> {
        let mut list_of_args = Vec::new();
        loop {
            if self.check_peek(&Token::RightParen){ break };
            list_of_args.push(self.expr()?);
            if match_adv!(&mut self, &Token::Comma).is_none() { break } 
        }

        Ok(Expr::Call(Box::new(expr?), list_of_args))
    }

    fn grouping(&mut self) -> Result<Expr, ParseError> {
        if (match_adv!(&mut self, &Token::LeftParen)).is_some() {
            let expr = self.expr()?;
            self.expect_token(&Token::RightParen)?;
            Ok(Expr::Group {
                expr: Box::new(expr),
            })
        } else {
            self.primary()
        }
    }

    

    fn primary(&mut self) -> Result<Expr, ParseError> {
        self.peek().unwrap();
        {
             let n = self.next()?;
             match n {
                 Token::LeftBrack => {
                    let mut vals = Vec::new();
                    loop {
                        if self.check_peek(&Token::RightBrack){ break }
                        vals.push(self.expr()?);
                        if match_adv!(&mut self, &Token::Comma).is_none() { break }
                     }
                    self.expect_token(&Token::RightBrack)?;
                    //Comma separated expression
                    Ok(Expr::CSE(vals))
                 },
                 Token::Bool(val) => self.resolve_node(Expr::Bool(val)),
                 Token::Number(s) => self.resolve_node(Expr::Number(s)),
                 Token::Identifier(_) => self.resolve_node(Expr::Val(n)),    
                 Token::String(val) => self.resolve_node(Expr::String(val)),
                 Token::Error => {
                     // todo!("Handle error with unknown token");
                     Err(ParseError::new(UnexpectedEndOfParsing, None, None))
                 }
                 other => Err(ParseError::new(
                     UnexpectedToken(other),
                     None,
                     None
                 )),
             }
        }
    }

    fn block(&mut self) -> Result<Vec<Stmt>, ParseError> {
        let mut stmts_block = Vec::new();
        self.expect_token(&Token::LeftBrace)?;
        while !self.check_peek(&Token::RightBrace) {
            stmts_block.push(self.statements()?)
        }
        self.expect_token(&Token::RightBrace)
            .map_err(|_| ParseError::new(UnclosedDelimiter, None, None))?;
        Ok(stmts_block)
    }


    fn parse_fn(&mut self, exposed: bool) -> Result<Decl, ParseError> {
        let name = self.next().and_then(|_| self.get_name())?;
        let args = self.parse_args()?;
        let block = self.block();
        if exposed {
            Ok(Decl::ExposedFn(name, args, block?))
        } else {
            Ok(Decl::Function(name, args, block?))
        }
    }

    fn parse_main(&mut self) -> Result<Decl, ParseError> {
        self.next()?;
        let mut li = Vec::new();
        while !self.check_peek(&Token::End) {
            li.push(self.statements()?);
        }
        self.expect_token(&Token::End)?;
        Ok(Decl::Program(li))
    } 

    fn parse_args(&mut self) -> Result<Vec<Token>, ParseError> {
        self.expect_token(&Token::Colon)?;
        let mut fn_args = Vec::new();
        loop {
            if self.check_peek(&Token::LeftBrace) { break }
            fn_args.push(self.parse_single_arg()?);
            if match_adv!(&mut self, &Token::Comma).is_none() { break }
        }
        Ok(fn_args)
    }


    fn parse_single_arg(&mut self) -> Result<Token, ParseError> {
        let name = self.get_name()?;
        Ok(name)
    }

    fn get_name(&mut self) -> Result<Token, ParseError> {
        match self.peek().unwrap() {
            &Token::Identifier(_) => self.next(),
            _ => self.next().and_then(|next| {
                Err(ParseError::new(
                    UnexpectedToken(next),
                    Some(0..0),
                    None
                ))
            })
        }
    }
}
    



