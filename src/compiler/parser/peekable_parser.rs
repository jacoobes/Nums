use logos::{Lexer, Logos};
use smol_str::SmolStr;
use std::ops::Range;
use crate::compiler::token::Token;

pub struct Peekable<'source> {
    lexer: Lexer<'source, Token>,
    peeked: Option<Option<Token>>,
    start_node_loc : usize,
}


impl<'source> Peekable<'source> {
    pub fn new(source: &'source str) -> Self {
        Self {
            lexer: Token::lexer(source),
            peeked: None,
            start_node_loc : 0,
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

    pub fn token_span(&mut self) -> std::ops::Range<usize> {
        self.lexer.span()
    }

    pub fn reset_range(&mut self) {
        self.start_node_loc = self.token_span().last().unwrap_or(self.start_node_loc)
    }

    pub fn get_err_range(&mut self) -> Range<usize> {
        self.start_node_loc..self.token_span().last().unwrap_or(self.start_node_loc + 1)
    }

    


}

impl<'source> Iterator for Peekable<'source> {
    type Item = Token;

    fn next(&mut self) -> Option<Token> {
        if let Some(peeked) = self.peeked.take() {
            peeked
        } else {
            self.lexer.next()
        }
    }
}

