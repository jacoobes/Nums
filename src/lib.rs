extern crate nums_vm;
pub mod tests;
pub mod error_handling;
pub mod compiler;
use std::fs;

fn main() {

}

fn _read_file(path: &'static str) -> String {
   fs::read_to_string(path).expect("Unable to read file")
}