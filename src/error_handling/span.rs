
use std::fmt::Debug;
use smol_str::SmolStr;

use super::faults::Faults;
pub struct Span {
   source : SmolStr,
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
    pub fn new( source: SmolStr,  line: usize,  typ: Faults) -> Self {
        Self {
            source,
            line,
            typ
        }
    }

    
}