use crate::compiler::tokens::Token;
use logos::{Lexer, Logos};
use smol_str::SmolStr;
use std::ops::Range;

pub struct Peekable<'source> {
    lexer: Lexer<'source ,Token>,
    peeked: Option<Option<Token>>,
}

impl<'source> Peekable<'source> {
    pub fn new(source: &'source str) -> Self {
        Self {
            lexer: Token::lexer(source),
            peeked: None,
        }
    }
    pub fn peek(&mut self) -> Option<&Token> {
        if self.peeked.is_none() {
            self.peeked = Some(self.lexer.next());
        }
        self.peeked.as_ref().unwrap().as_ref()
    }

    pub fn slice(&mut self) -> SmolStr {
        SmolStr::from(self.lexer.slice())
    }

    pub fn token_span(&mut self) -> usize {
        self.lexer.span().end
    }
}

impl<'source> Iterator for Peekable<'source> {
    type Item = Token;

    fn next(&mut self) -> Option<Self::Item> {
        if let Some(peeked) = self.peeked.take() {
            peeked
        } else {
            self.lexer.next()
        }
    }
}
