
use crate::cli::read_file::read_file;

extern crate nums_vm;
mod tests;
mod cli;
mod error_handling;
mod compiler;

fn main() {
   let path ="src/main.nums"; 
   let read_file = compiler::source::Source::new(read_file(path),  path); 



}
