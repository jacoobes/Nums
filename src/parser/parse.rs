use std::{borrow::Cow, iter::Peekable};

use logos::Lexer;
use crate::token::Token;

use super::ast::Expr;
pub struct Parser<'a> {
    tokens : Peekable<logos::Lexer<'a, Token>>,
}

///iterator methods
impl <'a> Parser <'a> {
    /// advances, expecting to there to be a token
    fn expect_token (&mut self, token: &Token) -> Result<Token, Cow<'a, str>> {
        match self.tokens.next() {
           Some(tok) if &tok == token => Ok(tok),
           Some(tok) => Err(Cow::Owned(format!("Got {:?} token when expecting {:?}", &tok, token))),
           None => Err(Cow::Owned(format!("Parsed until end of token stream. Expected {:?}", token))) 
        }
    }

    fn next (&mut self) -> Result<Token, Cow<'a, str>> {
           self.tokens.next().ok_or(Cow::Borrowed("Unexpected end of parsing"))
    }

    fn peek(&mut self) -> Result<&Token, ()> {
            self.tokens.peek().ok_or(())
    }

    fn check_peek (&mut self, token: &Token) -> bool {
        match self.tokens.peek() {
            Some(tok) => tok == token,
            None => false
        }
    }

    fn is_at_end(&mut self) -> bool { self.tokens.peek() == None }

    fn match_advance (&mut self, token: &Token ) -> bool  {
        match self.tokens.peek() {
            Some(_) => {
                if self.check_peek(token) {
                    self.next().unwrap();
                    return true
                };
                    false
                
            },
            None => false

        }
    }
}


/// Basic recursive descent parsing
impl <'a> Parser <'a> {
    pub fn new (tokens: Lexer<'a, Token>) -> Self {
        Self { tokens: tokens.peekable() }
    }

    pub fn parse(&mut self) -> Result<Expr, Cow<'a, str>> {
        loop {
            if !self.is_at_end() {
                match self.expr() {
                    Ok(c) => println!("{:?}", &c),
                    Err(e) => break Err(e)
                }
            }
            
        }

    }
    fn expr (&mut self)-> Result<Expr, Cow<'a, str>>  {
        self.term()
    }

    fn term (&mut self) -> Result<Expr, Cow<'a, str>> {
       self.factor() 
    }

    fn factor (&mut self)-> Result<Expr, Cow<'a, str>> {
        self.power()
    }

    fn power (&mut self)-> Result<Expr, Cow<'a, str>> {
        self.unary()
    }

    fn unary (&mut self)-> Result<Expr, Cow<'a, str>> {
        self.primary()
    }

    fn primary (&mut self) -> Result<Expr, Cow<'a, str>> {

            match self.peek().map_err(|_| Cow::Borrowed("End of parsing occured!")) {
                Ok(_) => {
                    match self.next()? {
                        Token::Bool(val) => return Ok(Expr::Bool(val)),
                        Token::Double(s) =>  return Ok(Expr::Double(s)),
                        Token::Integer(val) => return Ok(Expr::Integer(val)),
                        Token::String(val) => return Ok(Expr::String(val)),
                        Token::Char(c) => return Ok(Expr::Char(c)),
                        other => return Err(Cow::Owned(format!("{:?} found while parsing.", other )))
                    }
                }
                Err(_) => return Err(Cow::Borrowed("DFs"))
               
            }

        
        }
    }
    

