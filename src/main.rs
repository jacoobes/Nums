use std::fs;

use compiler::{nums_compiler::Compiler, source::Source};
extern crate nums_vm;
pub mod tests;
pub mod error_handling;
pub mod compiler;


pub fn main() {
    let source = Source::new(fs::read_to_string("src/test/t.nums").unwrap(), "test/t.nums");
    let compiler = Compiler::new(source);
    compiler.compile();
}