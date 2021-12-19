use core::ops::Range;
use logos::{Lexer, Logos, Source};

use crate::token::Token;

pub struct Peekable<'source> {
    lexer: Lexer<'source, Token>,
    peeked: Option<Option<Token>>,
    node_start: usize,
}


impl<'source> Peekable<'source> {
    pub fn new(source: &'source str) -> Self {
        Self {
            lexer: Token::lexer(source),
            peeked: None,
            node_start: 0,
        }
    }

    pub fn peek(&mut self) -> Option<&Token> {
        if self.peeked.is_none() {
            self.peeked = Some(self.lexer.next());
        }
        self.peeked.as_ref().unwrap().as_ref()
    }

    pub fn slice(&mut self) -> &'source str {
        self.lexer.slice()
    }

    pub fn cur_line(&mut self) -> usize {
        self.lexer.extras.line_breaks + 1
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

