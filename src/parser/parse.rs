use super::ast::Expr;
use crate::match_adv;
use crate::parser::peekable_lexer::Peekable as PeekerWrap;
use crate::token::Token;
use logos::Lexer;
use std::borrow::Cow;
pub struct Parser<'a> {
    tokens: PeekerWrap<'a>,
}

///iterator methods
impl<'a> Parser<'a> {
    /// advances, expecting to there to be a token
    fn expect_token(&mut self, token: &Token) -> Result<Token, Cow<'a, str>> {
        match self.tokens.peek() {
            Some(tok) if tok == token => Ok(self.tokens.next().unwrap()),
            Some(tok) => Err(Cow::Owned(format!(
                "Got {:?} token when expecting {:?}",
                &tok, token
            ))),
            None => Err(Cow::Owned(format!(
                "Parsed until end of token stream. Expected {:?}",
                token
            ))),
        }
    }
    fn peek(&mut self) -> Option<&Token> {
        self.tokens.peek().take()
    }

    fn next(&mut self) -> Result<Token, &'static str> {
        self.tokens.next().ok_or("Unexpected end of parsing!")
    }

    fn check_peek(&mut self, token: &Token) -> bool {
        match self.peek() {
            Some(tok) => tok == token,
            None => false,
        }
    }

    fn is_at_end(&mut self) -> bool {
        matches!(self.peek(), None)
    }
}

/// Basic recursive descent parsing
impl<'a> Parser<'a> {
    pub fn new(tokens: Lexer<'a, Token>) -> Self {
        Self {
            tokens: PeekerWrap::new(tokens.source()),
        }
    }

    pub fn parse(&mut self) -> Result<Vec<Expr>, Cow<'a, str>> {
        let mut temp_vec: Vec<Expr> = Vec::new();
        loop {
            if self.is_at_end() {
                break Ok(temp_vec);
            }
            temp_vec.push(self.expr()?)
        }
    }
    fn expr(&mut self) -> Result<Expr, Cow<'a, str>> {
        self.term()
    }



    fn term(&mut self) -> Result<Expr, Cow<'a, str>> {
        let mut left = self.factor();
        while let Some(tok) = match_adv!(&mut self, &Token::Minus | &Token::Plus) {
            let right = self.factor();
            left = Ok(Expr::Binary {
                operator: tok,
                left: Box::new(left?),
                right: Box::new(right?),
            });
        }
        left
    }

    fn factor(&mut self) -> Result<Expr, Cow<'a, str>> {
        let mut left = self.power();
        while let Some(tok) = match_adv!(&mut self, &Token::Star | &Token::FowardSlash) {
            let right = self.power();
            left = Ok(Expr::Binary {
                operator: tok,
                left: Box::new(left?),
                right: Box::new(right?),
            });
        }
        left
    }
    ///right associative
    fn power(&mut self) -> Result<Expr, Cow<'a, str>> {
        let left = self.unary();
        match self.peek() {
            Some(token) if matches!(token, &Token::Caret) => {
                let operator = self.next()?;
                let right = self.power();
                Ok(Expr::Binary {
                    operator,
                    left: Box::new(left?),
                    right: Box::new(right?),
                })
            }
            _ => left,
        }
    }
    /// right associative unary parser
    fn unary(&mut self) -> Result<Expr, Cow<'a, str>> {
        match self.peek() {
            Some(token) if matches!(token, &Token::Bang | &Token::Minus | &Token::Plus ) => {
                let operator = self.next()?;
                let expr = self.unary();
                Ok(Expr::Unary {
                    operator,
                    expr: Box::new(expr?),
                })
            }
            _ => self.grouping(),
        }
    }

    fn grouping(&mut self) -> Result<Expr, Cow<'a, str>> {
        if self.check_peek(&Token::LeftParen) {
            self.next()?;
            let expr = self.expr()?;
            self.expect_token(&Token::RightParen)?;
            Ok(Expr::Group {
                expr: Box::new(expr),
            })
        } else {
            self.primary()
        }
    }

    fn primary(&mut self) -> Result<Expr, Cow<'a, str>> {
        if self.is_at_end() {
            return Err(Cow::Borrowed("Reached end of parsing!"));
        }
        match self.peek().unwrap() {
            _ => match self.next()? {
                Token::Bool(val) => Ok(Expr::Bool(val)),
                Token::Double(s) => Ok(Expr::Double(s)),
                Token::Integer(val) => Ok(Expr::Integer(val)),
                Token::String(val) => Ok(Expr::String(val)),
                Token::Char(c) => Ok(Expr::Char(c)),
                other => Err(Cow::Owned(format!("Invalid token {:?} ", other))),
            },
        }
    }
}
