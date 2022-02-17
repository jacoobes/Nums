use std::rc::Rc;
use codespan_reporting::term::{
    self,
    termcolor::{ColorChoice, StandardStream},
};
use logos::Logos;
use smol_str::SmolStr;

use super::{
    parser::parse::Parser,
    source::Source,
    tokens::Token,
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

    pub fn compile(&self, _base_pkg: SmolStr) {
        let source = &self.source.source;
        let tokenizer = Token::lexer(source);
        let mut parser = Parser::new(tokenizer, Rc::clone(&self.source));
        match parser.parse() {
            Ok(res) => {
                 println!("{:?}", res)
            },
            Err(diagnostics) => {
                let src = &self.source.as_ref().simple_file;
                let writer = StandardStream::stderr(ColorChoice::Always);
                let config = codespan_reporting::term::Config::default();
                for error in diagnostics {
                    term::emit(&mut writer.lock(), &config, src, &error)
                        .expect("Could not emit code_span");
                }
            }
        }
    }
}
