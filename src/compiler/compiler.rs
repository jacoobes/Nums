use std::{rc::Rc};

use logos::{Logos, Lexer};

use super::{source::Source, token::Token, parser::parse::Parser};

struct Compiler {
    source: Rc<Source>,
}



impl Compiler {
    fn new(&mut self, source: Source) -> Self {
        Self {
            source: Rc::new(source)
        }
    }

    fn tokenizer (&self) -> Lexer<Token> {
        Token::lexer(&self.source.src())
    }
    fn compile(&self) {
        let tokenizer = self.tokenizer();
        let parser = Parser::new( tokenizer);
    }
    
}
