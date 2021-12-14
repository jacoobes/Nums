use std::borrow::Cow;
use crate::parser::peekable_lexer::Peekable as PeekerWrap;
use logos::Lexer;
use crate::token::Token;

use super::ast::Expr;
pub struct Parser<'a> {
    tokens : PeekerWrap<'a>,
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

    fn peek (&mut self) -> Result<&Token, ()> {
        self.tokens.peek().ok_or(())
    }

    fn next (&mut self) -> Result<Token, Cow<'a, str>> {
           self.tokens.next().ok_or(Cow::Borrowed("Unexpected end of parsing"))
    }

    fn check_peek (&mut self, token: &Token) -> bool {
        match self.peek() {
            Ok(tok) => tok == token,
            Err(_) => false
        }
    }

    fn is_at_end(&mut self) -> bool { matches!(self.peek(), Err(_)) }

    fn match_advance (&mut self, token: &Token ) -> bool  {
        match self.peek() {
            Ok(_) => {
                if self.check_peek(token) {
                    self.next().unwrap();
                    return true
                };
                    false
                
            },
            Err(_) => false

        }
    }
}


/// Basic recursive descent parsing
impl <'a> Parser <'a> {
    pub fn new (tokens: Lexer<'a, Token>) -> Self {
        Self { 
            tokens: PeekerWrap::new(tokens.source()),
        }
    }

    pub fn parse(&mut self) -> Result<Vec<Expr>, Cow<'a, str>> {
        let mut temp_vec : Vec<Expr> = Vec::new();
        loop {
            if !self.is_at_end() {
                match self.expr() {
                    Ok(c) => temp_vec.push(c),
                    Err(e) => break Err(e)
                }
            } else {
                break Ok(temp_vec)
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

            match self.peek().map_err(|_| Cow::Borrowed("End of parsing occured!"))? {
                _ => {
                    match self.next()? {
                        Token::LeftParen => {
                            let expr = self.expr();
                            self.expect_token(&Token::RightParen)?;
                            Ok(Expr::Group(Box::new(expr?)))
                        }
                        Token::Bool(val) => Ok(Expr::Bool(val)),
                        Token::Double(s) =>  Ok(Expr::Double(s)),
                        Token::Integer(val) => Ok(Expr::Integer(val)),
                        Token::String(val) => Ok(Expr::String(val)),
                        Token::Char(c) => Ok(Expr::Char(c)),
                        other => Err(Cow::Owned(format!("Invalid token {:?} ", other )))
                    }
                }
                            
            }

        
        }
    }
    