use std::fs;

use frontend::{nums_compiler::Compiler, source::Source};
extern crate nums_vm;
pub mod tests;
pub mod error_handling;
pub mod frontend;


pub fn main() {
    let source = Source::new(fs::read_to_string("src/test/t.nums").unwrap());
    let compiler = Compiler::new(source);
    compiler.compile();
}
