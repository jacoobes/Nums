use crate::frontend::ast::AST;
use crate::frontend::nodes::{decl::Decl, expr::Expr, stmt::Stmt};
use crate::frontend::{parser::peekable_parser::Peekable as PeekerWrap, tokens::Token};
use crate::{
    frontend::source::Source,
    error_handling::faults::{ErrTyp::*, Faults::*, *},
};
use crate::{create_expr, match_adv};
use codespan_reporting::diagnostic::Diagnostic;
use logos::Lexer;
use std::ops::Range;
use std::rc::Rc;
pub struct Parser<'source> {
    tokens: PeekerWrap<'source>,
    source: std::rc::Rc<Source>,
    start: usize,
    end: usize,
}

///iterator methods
impl<'source> Parser<'source> {
    /// advances, expecting to there to be a token
    fn expect_token(&mut self, token: &Token) -> Result<Token, Diagnostic<()>> {
        match self.peek() {
            Some(tok) if tok == token => Ok(self.next().unwrap()),
            Some(_) => self.next().and_then(|tok|  Err(self.new_span(Error(Expected(token.clone(), tok)), "")) ),
            None => Err(self.new_span(Error(UnexpectedEndOfParsing), "")),
        }
    }
    fn peek(&mut self) -> Option<&Token> {
        self.tokens.peek().map(|s| &s.0)
    }

    fn next(&mut self) -> Result<Token, Diagnostic<()>> {
        {
            let this = self.tokens
                .next()
                .map(|s|s.0);
            let err = self.new_span(Error(UnexpectedEndOfParsing), "");
            match this {
                Some(v) => Ok(v),
                None => Err(err),
            }
        }
    }
    fn resolve_node<T>(&mut self, node: T) -> Result<T, Diagnostic<()>> {
        self.start = self.end;
        Ok(node)
    }
    /// check if token matches the current peeked token in the iterator
    fn check_peek(&mut self, token: &Token) -> bool {
        self.peek() == Some(token)
    }
    fn is_at_end(&mut self) -> bool {
        matches!(self.peek(), None)
    }
    fn span(&mut self) -> Range<usize> {
        self.start..self.end
    }
}

/// Basic recursive descent parsing
/// Errors are made using Spans, which will pretty print the error and its possible (not tested if accurate) location
/// First pass of the parser can detect basic unknown token errors, expected tokens, etc
impl<'source> Parser<'source> {
    pub fn new(tokens: Lexer<'source, Token>, source: Rc<Source>) -> Self {
        Self {
            tokens: PeekerWrap::new(tokens),
            source,
            start: 0,
            end: 0,
        }
    }

    pub fn parse(&mut self) -> Result<AST, Vec<Diagnostic<()>>> {
        let mut diagnostic_vec = Vec::new();
        let mut decls = Vec::new();
        let mut had_parse_err = false;
        loop {
            if self.is_at_end() {
                if had_parse_err {
                    break Err(diagnostic_vec);
                }
                break Ok(AST(decls));
            }
            match self.top_level() {
                Ok(decl) => match &decl {
                    Decl::Function(..)
                    | Decl::Use(..)
                    | Decl::Task(..)
                    | Decl::ExposedTask(..)
                    | Decl::Program(..) 
                    | Decl::ExposedFn(..) =>{ 
                        decls.push(decl)
                    }
                },
                Err(e) => {
                    had_parse_err = true;
                    diagnostic_vec.push(e);
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
    fn top_level(&mut self) -> Result<Decl, Diagnostic<()>> {
        let tok = self.peek().unwrap();
        match &tok {
            Token::Expose => {
                self.next().unwrap();
                match self.peek() {
                    Some(tok) if tok == &Token::Function => self.parse_fn(true),
                    _ => {
                        let other = self.next()?;
                        Err(self.new_span(
                            Error(UnexpectedToken(other)),
                            "Expected token(`fn`) after visibility modifier",
                        ))
                    }
                }
            }
            Token::Start => self.parse_main(),
            Token::Function => self.parse_fn(false),
            Token::Use => self.parse_get(),
            _ => Err(self.new_span(Error(NoTopLevelDeclaration), "")),
        }
    }

    fn parse_get(&mut self) -> Result<Decl, Diagnostic<()>> {
        let expr = self.next().and_then(|_| self.expr());
        self.expect_token(&Token::Semi)?;
        Ok(Decl::Use(expr?))
    }

    fn statements(&mut self) -> Result<Stmt, Diagnostic<()>> {
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
            None => Err(self.new_span(Error(UnexpectedEndOfParsing), "")),
        }
    }

    fn if_else(&mut self) -> Result<Stmt, Diagnostic<()>> {
        let condition = self.next().and_then(|_| self.expr())?;
        let block = self.block()?;
        let else_block = if (match_adv!(&mut self, &Token::Else)).is_some() {
            Some(self.block()?)
        } else {
            None
        };
        Ok(Stmt::IfElse(condition, block, else_block))
    }

    fn while_loop(&mut self) -> Result<Stmt, Diagnostic<()>> {
        let condition = self.next().and_then(|_| self.expr());
        let block = self.block();
        self.resolve_node(Stmt::While(condition?, block?))
    }

    fn var_decl(&mut self) -> Result<Stmt, Diagnostic<()>> {
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

    fn stmt_expr(&mut self) -> Result<Stmt, Diagnostic<()>> {
        let value_of_statement = self.expr();
        self.expect_token(&Token::Semi).and_then(|_|{
            self.resolve_node(Stmt::ExprStatement(value_of_statement?))
        })
    }

    fn expr(&mut self) -> Result<Expr, Diagnostic<()>> {
        self.assignment()
    }

    fn assignment(&mut self) -> Result<Expr, Diagnostic<()>> {
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
                other => Err(self.new_span(Error(InvalidAssignmentTarget(other?)), "")),
            }
        } else {
            asignee
        }
    }

    fn or(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.and();
        while let Some(operator) = match_adv!(&mut self, &Token::Or) {
            let right = self.and();
            left = create_expr!(&mut self, logical: operator, left, right)
        }
        left
    }

    fn and(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.equality();
        while let Some(operator) = match_adv!(&mut self, &Token::And) {
            let right = self.equality();
            left = create_expr!(&mut self, logical: operator, left, right)
        }
        left
    }

    fn equality(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.compare();
        while let Some(operator) = match_adv!(&mut self, &Token::Eq | &Token::NotEq) {
            let right = self.compare();
            left = create_expr!(&mut self, binary: operator, left, right);
        }
        left
    }

    fn compare(&mut self) -> Result<Expr, Diagnostic<()>> {
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

    fn term(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.factor();
        while let Some(operator) = match_adv!(&mut self, &Token::Minus | &Token::Plus) {
            let right = self.factor();
            left = create_expr!(&mut self, binary: operator, left, right);
        }
        left
    }

    fn factor(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.power();
        while let Some(operator) = match_adv!(&mut self, &Token::Star | &Token::FowardSlash) {
            let right = self.power();
            left = create_expr!(&mut self, binary: operator, left, right);
        }
        left
    }
    ///right associative
    fn power(&mut self) -> Result<Expr, Diagnostic<()>> {
        let left = self.unary();
        if let Some(operator) = match_adv!(&mut self, &Token::Caret) {
            let right = self.power();
            create_expr!(&mut self, binary: operator, left, right)
        } else {
            left
        }
    }
    /// right associative unary parser
    fn unary(&mut self) -> Result<Expr, Diagnostic<()>> {
        if let Some(operator) = match_adv!(&mut self, &Token::Bang | &Token::Minus | &Token::Plus) {
            let expr = self.unary();
            create_expr!(&mut self, unary: operator, expr)
        } else {
            self.callee()
        }
    }

    fn callee(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut expr = self.grouping();
        loop {
            expr = if (match_adv!(&mut self, &Token::LeftBrack)).is_some() {
                let call = self.finish_call(expr)?;
                self.expect_token(&Token::RightBrack)?;
                self.resolve_node(call)
            } else if (match_adv!(&mut self, &Token::Colon)).is_some() {
                let name = self.get_name()?;
                self.resolve_node(Expr::Get(Box::new(expr?), name))
            } else {
                break expr;
            }
        }
    }

    fn finish_call(&mut self, expr: Result<Expr, Diagnostic<()>>) -> Result<Expr, Diagnostic<()>> {
        let mut list_of_args = Vec::new();
        while !self.check_peek(&Token::RightBrack) {
            list_of_args.push(self.expr()?);
            match_adv!(&mut self, Token::Comma);
        }
        Ok(Expr::Call(Box::new(expr?), list_of_args))
    }

    fn grouping(&mut self) -> Result<Expr, Diagnostic<()>> {
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

    fn primary(&mut self) -> Result<Expr, Diagnostic<()>> {
        self.peek().unwrap();
        {
             let n = self.next()?;
             match n {
                 Token::Bool(val) => self.resolve_node(Expr::Bool(val)),
                 Token::Double(s) => self.resolve_node(Expr::Double(s)),
                 Token::Identifier(_) => self.resolve_node(Expr::Val(n)),    
                 Token::Integer(val) => self.resolve_node(Expr::Integer(val)),
                 Token::String(val) => self.resolve_node(Expr::String(val)),
                 Token::Error => {
                     // todo!("Handle error with unknown token");
                     Err(self.new_span(Error(UnexpectedEndOfParsing), "unknown token"))
                 }
                 other => Err(self.new_span(
                     Error(UnexpectedToken(other)),
                     "found an unexpected token out of place",
                 )),
             }
        }
    }

    fn block(&mut self) -> Result<Vec<Stmt>, Diagnostic<()>> {
        let mut stmts_block = Vec::new();
        self.expect_token(&Token::LeftBrace)?;
        while !self.check_peek(&Token::RightBrace) {
            stmts_block.push(self.statements()?)
        }
        self.expect_token(&Token::RightBrace)
            .map_err(|_| self.new_span(Error(UnclosedDelimiter), "Check closing brace `}` "))?;
        Ok(stmts_block)
    }


    fn parse_fn(&mut self, exposed: bool) -> Result<Decl, Diagnostic<()>> {
        let name = self.next().and_then(|_| self.get_name())?;
        let args = self.parse_args()?;
        let block = self.block();
        if exposed {
            Ok(Decl::ExposedFn(name, args, block?))
        } else {
            Ok(Decl::Function(name, args, block?))
        }
    }

    fn parse_main(&mut self) -> Result<Decl, Diagnostic<()>> {
        self.next()?;
        let mut li = Vec::new();
        while !self.check_peek(&Token::End) {
            li.push(self.statements()?);
        }
        self.expect_token(&Token::End)?;
        Ok(Decl::Program(li))
    } 

    fn parse_args(&mut self) -> Result<Vec<Token>, Diagnostic<()>> {
        let mut fn_args = Vec::new();
        if (match_adv!(&mut self, &Token::LeftBrack)).is_some() {
            if self.check_peek(&Token::RightBrack) {
                self.next().unwrap();
                return Ok(fn_args);
            }
            let first_arg = self.parse_single_arg()?;
            fn_args.push(first_arg);

            while !self.check_peek(&Token::RightBrack) {
                self.expect_token(&Token::Comma)?;
                let remaining_args = self.parse_single_arg()?;
                fn_args.push(remaining_args);
            }
            self.expect_token(&Token::RightBrack)?;
            Ok(fn_args)
        } else {
            let next = self.next()?;
            Err(self.new_span(
             Error(Expected(Token::LeftBrack, next)), 
             ""
            ))
        }
    }

    fn parse_single_arg(&mut self) -> Result<Token, Diagnostic<()>> {
        let name = self.get_name()?;
        Ok(name)
    }

    fn get_name(&mut self) -> Result<Token, Diagnostic<()>> {
        match self.peek().unwrap() {
            &Token::Identifier(_) => self.next(),
            _ => self.next().and_then(|next| {
                Err(self.new_span(
                    Error(UnexpectedToken(next)),
                    "note : Try to use an identifier",
                ))
            }),
        }
    }
}

impl<'source> Parser<'source> {
    fn new_span(&mut self, fault: Faults, note: &'source str) -> Diagnostic<()> {
        let span = self.span();
        self.source
            .create_diagnostic(format!("{:?}", fault), span, note.to_string())
    }
}
