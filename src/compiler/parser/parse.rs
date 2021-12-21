
use std::rc::Rc;

use super::ast::{Expr, Stmt, Decl};
use crate::{error_handling::{
    faults::{ErrTyp::*, Faults::*, *},
}, compiler::source::Source};
use crate::{match_adv, create_binexpr};
use crate::compiler::{parser::peekable_parser::Peekable as PeekerWrap, token::Token};
use codespan_reporting::diagnostic::Diagnostic;
use logos::Lexer;
use smol_str::SmolStr;
pub struct Parser<'source> {
    tokens: PeekerWrap<'source>,
    source: std::rc::Rc<Source>
}

///iterator methods
impl<'source> Parser<'source> {
    /// advances, expecting to there to be a token
    fn expect_token(&mut self, token: &Token) -> Result<Token, Diagnostic<()>> {
        match self.peek() {
            Some(tok) if tok == token => Ok(self.next().unwrap()),
            Some(_) => {
                let fault = self.next();
                Err(self.new_span(Error(Expected( token.clone(), fault?,)), ""))
            }
            None => Err(self.new_span(Error(UnexpectedEndOfParsing), "")),
        }
    }
    fn peek(&mut self) -> Option<&Token> { 
        self.tokens.peek().take()
    }

    fn next(&mut self) -> Result<Token, Diagnostic<()>> {
        self.tokens.reset_range();
        self.tokens
            .next()
            .ok_or(self.new_span(Error(UnexpectedEndOfParsing), ""))
    }
    fn check_peek(&mut self, token: &Token) -> bool { self.peek() == Some(token) }
    fn is_at_end(&mut self) -> bool { matches!(self.peek(), None) }

}

/// Basic recursive descent parsing
/// Errors are made using Spans, which will pretty print the error and its possible (not tested if accurate) location
/// First pass of the parser can detect basic unknown token errors, expected tokens, etc
impl<'source> Parser<'source> {
    pub fn new(tokens: Lexer<'source, Token>, source: Rc<Source>) -> Self {
        Self {
            tokens: PeekerWrap::new(tokens.source()),
            source
        }
    }

    pub fn parse(&mut self) -> Result<Vec<Decl>, Diagnostic<()>> {
        let mut temp_vec= Vec::new();
        loop {
            if self.is_at_end() {
                break Ok(temp_vec);
            }
            temp_vec.push(self.top_level()?)
        }
    }

    fn top_level(&mut self) -> Result<Decl, Diagnostic<()>> {
         match self.peek().unwrap() {
             tok => match tok {
                 &Token::Function => self.parse_fn(),
                 &Token::Package => self.parse_mod(),
                 _ => Err(self.new_span(Error(NoTopLevelDeclaration), "found either an out of place statement or no top level declaration"))
             }
         }
    }
    fn statements(&mut self) -> Result<Stmt, Diagnostic<()>> {
        match self.peek() {
            Some(tok) => match tok {
                &Token::While => self.while_loop(),
                &Token::Let => self.var_decl(),
                &Token::If => self.if_else(),
                &Token::LeftBrace => Ok(Stmt::Block(self.block()?)),              
                _ => self.stmt_expr()
            },
            None => Err(self.new_span(Error(UnexpectedEndOfParsing), "could not finish parsing"))
        }
    }

    fn if_else(&mut self) -> Result<Stmt, Diagnostic<()>> {
        self.next()?;
        let condition = self.expr();
        let block = self.block();
        let else_block = if let Some(_) = match_adv!(&mut self, &Token::Else) {
            Some(self.block()?)
        } else {
            None
        };
        Ok(Stmt::IfElse(condition?, block?, else_block))
    }

    fn while_loop(&mut self) -> Result<Stmt, Diagnostic<()>> {
        self.next()?;
        let cond = self.expr();
        let block = self.block();
        Ok(Stmt::While(cond?, block?))
    }

    fn var_decl(&mut self) -> Result<Stmt, Diagnostic<()>> {
        let name = self.get_name()?;
        self.expect_token(&Token::Colon)?;
        let typ_tok = self.get_type()?;
        let var_val = self.stmt_expr();
        Ok(Stmt::VarDecl(name, typ_tok, Box::new(var_val?)))
    }

    fn stmt_expr(&mut self) -> Result<Stmt, Diagnostic<()>> {
       let value_of_statement = self.expr();
       self.expect_token(&Token::Semi)?;
       Ok(Stmt::ExprStatement(value_of_statement?))
    }

    fn expr(&mut self) -> Result<Expr, Diagnostic<()>> {
        self.equality()
    }
    
    fn equality(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.compare();
        while let Some(operator) = match_adv!(&mut self, &Token::Eq | &Token::NotEq) {
            let right = self.compare();
            left = create_binexpr!(&mut self, operator, left, right);
        }
        left

    }

    fn compare(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.term();
        while let Some(operator) = match_adv!(&mut self, &Token::LessEq | &Token::GreaterEq | &Token::LeftArr | &Token::RightArr) {
            let right = self.term();
            left = create_binexpr!(&mut self, operator, left, right);
        }
        left
    }

    fn term(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.factor();
        while let Some(operator) = match_adv!(&mut self, &Token::Minus | &Token::Plus) {
            let right = self.factor();
            left = create_binexpr!(&mut self, operator, left, right);
        }
        left
    }

    fn factor(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.power();
        while let Some(operator) = match_adv!(&mut self, &Token::Star | &Token::FowardSlash) {
            let right = self.power();
            left = create_binexpr!(&mut self, operator, left, right);
        }
        left
    }
    ///right associative
    fn power(&mut self) -> Result<Expr, Diagnostic<()>> {
        let left = self.unary();
        if let Some(operator) = match_adv!(&mut self, &Token::Caret) {
            let right = self.power();
            create_binexpr!(&mut self, operator, left, right)
        } else {
            left
        }
        
    }
    /// right associative unary parser
    fn unary(&mut self) -> Result<Expr, Diagnostic<()>> {
        if let Some(operator) = match_adv!(&mut self, &Token::Bang | &Token::Minus | &Token::Plus) {
            let expr = self.unary();
            Ok(Expr::Unary{
                operator,
                expr: Box::new(expr?)
            })
        } else {
            self.grouping()
        }
    }

    fn grouping(&mut self) -> Result<Expr, Diagnostic<()>> {
        if let Some(_) = match_adv!(&mut self, &Token::LeftParen) {
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

        match self.peek().unwrap() {
            _ => match self.next()? {
                Token::Bool(val) => Ok(Expr::Bool(val)),
                Token::Double(s) => Ok(Expr::Double(s)),
                Token::Identifier(s) => Ok(Expr::Val(s)),
                Token::Integer(val) => Ok(Expr::Integer(val)),
                Token::String(val) => Ok(Expr::String(val)),
                Token::Char(c) => Ok(Expr::Char(c)),
                Token::Unit => Ok(Expr::Unit),
                Token::Error => {
                    let unknown_token = self.tokens.slice();
                    Err(self.new_span(Error(UnknownToken(unknown_token)),"unknown token"))
                }
                other => Err(self.new_span(Error(UnexpectedToken(other)), "found an unexpected token out of place")),
            },
        }
    }

    fn block(&mut self) -> Result<Vec<Stmt>, Diagnostic<()>> {
        let mut stmts_block = Vec::new();
        self.expect_token(&Token::LeftBrace)?;
        while !self.check_peek(&Token::RightBrace) {
            stmts_block.push(self.statements()?)
        }
        self.expect_token(&Token::RightBrace)?;
        Ok(stmts_block)
    }

    fn get_type(&mut self) -> Result<Token, Diagnostic<()>> {
        let typ = self.next()?;
        match typ {
             Token::Identifier(_) => Ok(Token::Type(self.tokens.slice())),
             Token::Type(_) => Ok(typ),
            other => Err(self.new_span(Error(UnknownType(other)), "Found a token that cannot be qualified as a type"))
        }
    }

    fn parse_fn(&mut self) -> Result<Decl, Diagnostic<()>> {
        self.next()?;
        let name = self.get_name()?;
        let args = self.parse_args()?;
        let fn_ret_type = if let Some(_) = match_adv!(&mut self, &Token::Assign) {
            self.get_type()?
        } else {
            Token::Unit
        };
        let block = self.block();
        Ok(Decl::Function(name,args, fn_ret_type, block?  ))
    }

    fn parse_args(&mut self, ) -> Result<Option<Vec<(SmolStr, Token)>>, Diagnostic<()>> {
        let mut fn_args = Vec::new();
        if let Some(_) = match_adv!(&mut self, &Token::Bar) {
            let first_arg = self.parse_single_arg()?;
            fn_args.push(first_arg);

            while !self.check_peek(&Token::Bar) {
                self.expect_token(&Token::Comma)?;
                let remaining_args = self.parse_single_arg()?;
                fn_args.push(remaining_args);
            }
            self.expect_token(&Token::Bar)?;
            Ok(Some(fn_args))
            
        } else {
            Ok(None)
        }
    }
    fn parse_single_arg(&mut self) -> Result<(SmolStr, Token), Diagnostic<()>> { 
        let name = self.get_name()?;
        self.expect_token(&Token::Colon)?;
        let typ = self.get_type()?; 
        Ok((name, typ))
    }

    fn parse_mod(&mut self) -> Result<Decl, Diagnostic<()>> {
        self.next().unwrap();
        let mod_name = self.get_name()?;
        self.expect_token(&Token::Semi)?;
        Ok(Decl::Module(mod_name))
    }

    fn get_name(&mut self) -> Result<SmolStr, Diagnostic<()>> {
        match self.peek().unwrap() {
            &Token::Identifier(_) => {
                self.next()?;
                Ok(self.tokens.slice())
            }
            _ => {
                let next = self.next()?;
                Err(self.new_span(Error(UnexpectedToken(next)), "Try to use an identifier "))
            }
            
        } 

    }
}

impl<'source> Parser<'source> {
    fn new_span(&mut self, fault: Faults, note: &'static str) -> Diagnostic<()> {
       self.source.create_diagnostic(
           format!("{:?}", fault),
           self.tokens.get_err_range(),
           note.to_string())
    }
}
