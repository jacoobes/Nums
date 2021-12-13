use logos::Logos;
use token::Token;

use crate::cli::read_file::read_file;

extern crate nums_vm;
mod token;
mod tests;
mod cli;
mod parser;

fn main() {
   let read_file = read_file(); 
   let mut iterator = Token::lexer(&read_file);
   println!("{:?}", read_file);
   while let Some(tok) = iterator.next() {
       println!("{:?}", &tok)
   }

}
