

use std::fs;


extern crate nums_vm;
mod tests;
mod error_handling;
mod compiler;

fn main() {
   let path = "src/main.nums"; 
   let read_file = compiler::source::Source::new(read_file(path),  path); 
   compiler::nums_compiler::Compiler::new(read_file).compile() 
}


fn read_file(path: &'static str) -> String {
   fs::read_to_string(path).expect("Unable to read file")
}