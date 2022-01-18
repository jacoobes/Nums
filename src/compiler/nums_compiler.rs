use std::rc::Rc;
use crate::compiler::nodes::{decl::Decl, path::PackagePath};
use crate::compiler::nodes::path::Path;
use codespan_reporting::{term::{
    self,
    termcolor::{ColorChoice, StandardStream},
}, diagnostic::Diagnostic};
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

    pub fn compile(&self, base_pkg: SmolStr) {
        let source = &self.source.source;
        let tokenizer = Token::lexer(source);
        let mut parser = Parser::new(tokenizer, Rc::clone(&self.source));
        match parser.parse() {
            Ok(res) => {
                 let mod_name = res.get_name();
                 let mut pack_name = PackagePath::from(Path::from(&base_pkg)); 
                 pack_name.join(Path::from(&mod_name));
                 println!("{}", pack_name.to_string());
                 let mut mods = fnv::FnvHashMap::default();
                 mods.insert(mod_name, res);
                 let package = Decl::Module(base_pkg, mods);   
                 println!("{:?}", package)
                 
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