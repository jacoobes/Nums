use std::rc::Rc;
use crate::compiler::types::type_checker::TypeChecker;
use codespan_reporting::term::{
    self,
    termcolor::{ColorChoice, StandardStream},
};
use logos::Logos;

use super::{
    parser::parse::Parser,
    source::Source,
    tokens::Token,
    nodes::decl::Decl, visitor::Visitor
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

    pub fn compile(&self) -> () {
        let source = &self.source.source;
        let tokenizer = Token::lexer(source);
        let mut parser = Parser::new(tokenizer, Rc::clone(&self.source));
        match parser.parse() {
            Ok(res) => {
                 let mut type_engine = TypeChecker;
                for dec in res {
                  println!("{:?}",  type_engine.visit_decl(dec))

                };
                    
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
