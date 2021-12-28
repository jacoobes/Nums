

use crate::cli::read_file::read_file;

extern crate nums_vm;
mod tests;
mod cli;
mod error_handling;
mod compiler;

fn main() {
   let path = "src/main.nums"; 
   let read_file = compiler::source::Source::new(read_file(path),  path); 
   match compiler::nums_compiler::Compiler::new(read_file).compile() {
      Some(vec) => println!("{:?}", &vec),
      None => println!("error")
   }
}
