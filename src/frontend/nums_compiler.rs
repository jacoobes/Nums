use std::{rc::Rc, alloc::System};
use codespan_reporting::{term::{
    self,
    termcolor::{ColorChoice, StandardStream},
}, files::SimpleFile};
use logos::Logos;

use super::{
    parser::parse::Parser,
    source::Source,
    tokens::Token,
    visitor::walker::Walker
};


pub struct Compiler {
    source: Rc<Source>,
}

impl Compiler {
    pub fn new(source: Source) -> Self {
        Self {
            source: Rc::new(source),
        }
    }

    pub fn compile(&self) {
        let source = &self.source.source;
        let tokenizer = Token::lexer(source);
        let mut parser = Parser::new(tokenizer, Rc::clone(&self.source));
        match parser.parse() {
            Ok(ast) => {
                println!("{}", ast.walk());
            },
            Err(diagnostics) => {
                let _src = SimpleFile::new("test", source);
                let _writer = StandardStream::stderr(ColorChoice::Always);
                let _config = codespan_reporting::term::Config::default();
                for di in diagnostics {
                    println!("{:?}",&di);
                }
           }
        }
    }
}
