use logos::Logos;
use token::Token;

use crate::cli::read_file::read_file;

extern crate nums_vm;
mod token;
mod tests;
mod cli;
mod parser;
mod error_handling;

fn main() {
   let read_file = read_file(); 
   let iterator = Token::lexer(&read_file);
   let mut parser = parser::parse::Parser::new(iterator);

   match parser.parse() {
       Ok(vec) => println!("{:?}", &vec),
       Err(e) => println!("{:?}", e)
   }

}
