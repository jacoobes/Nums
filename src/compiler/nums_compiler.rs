use std::{rc::Rc};

use codespan_reporting::term::{termcolor::{StandardStream, ColorChoice}, self};
use logos::Logos;

use super::{source::Source, token::Token, parser::{parse::Parser, ast::Decl}};

pub struct Compiler {
    source: Rc<Source>,
}



impl Compiler {
    pub fn new( source: Source) -> Self {
        Self {
            source: Rc::new(source)
        }
    }

    pub fn compile(&self) -> Option<Vec<Decl>>{
        let source = &self.source.source;
        let tokenizer =  Token::lexer(source);
        let mut parser = Parser::new(tokenizer, Rc::clone(&self.source));
        match parser.parse() {
            Ok(res) => Some(res),
            Err(diagnostic) => {
                let src = &self.source.as_ref().simple_file;
                let writer = StandardStream::stderr(ColorChoice::Always);
                let config = codespan_reporting::term::Config::default();
                term::emit(&mut writer.lock(), &config, src, &diagnostic).expect("Could not emit code_span");
                None
            }
        }
    }
    
}
