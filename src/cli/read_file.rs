use std::fs;



pub fn read_file() -> String {
     fs::read_to_string("src/main.nums").expect("Unable to read file")
}