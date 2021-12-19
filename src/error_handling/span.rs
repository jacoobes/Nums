
use std::fmt::Debug;
use smol_str::SmolStr;
use super::faults::Faults;
pub struct Span {
   //todo : for the file thats being parsed, detect its file path 
   filename : SmolStr,
   source : String,
   line: usize,
   typ:  Faults
}

impl Debug for Span {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        writeln!(f, "{: >10?}    ", self.typ)?;
        writeln!(f, "||  {}  {: >7}", "@", format!("{}{}", "src\\main.nums:", self.line))?;
        writeln!(f, "||")?;
        writeln!(f, "||  \"{}\"", &self.source)?;    
        writeln!(f, "||   {}", "^".repeat(self.source.len()))
    }
}

impl Span {
    pub fn new(filename: String, source: String,  line: usize,  typ: Faults) -> Self {
        Self {
            filename: SmolStr::from(filename),
            source,
            line,
            typ
        }
    }

    
}