use std::rc::Rc;
use numsc::Logos;
use codespan_reporting::{term::
    termcolor::{ColorChoice, StandardStream},
    files::SimpleFile};
use numsc::structures::disassembler::Disassembler;
use numsc::structures::tokens::Token;
use numsc::vm::frame_reader;
use super::{
    parser::parse::Parser,
    source::Source,
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
            Ok(mut ast) => {
                //println!("{:?}", &ast);
                let vec = ast.walk();
                //println!("{:?}", &vec);
                for frame in vec {
                    Disassembler::disassemble(&frame);
                    let value = frame_reader::read_frame(frame);
                    if let Ok( v ) = value {
                        println!("{v:?}")
                    } else {
                        println!("{:?}", value.err())
                    }
                }
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
