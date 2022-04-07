
use std::ops::Range;
use crate::frontend::source::Source;

pub struct Spanner {
    source : std::rc::Rc<Source>,
    cur : Range<usize>,
}

impl Spanner {
    pub fn new(source: std::rc::Rc<Source>) -> Self { 
        Self { source,  cur : 0..0 } 
    }
    
    pub fn get_span(&self) -> Range<usize> {
        Range { start: self.cur.start, end: self.cur.end }        
    }

    pub fn next(&mut self, last_tok : &Range<usize>) {
        self.cur.end = last_tok.end;     
    }

    pub fn converge(&mut self) {
        self.cur.start = self.cur.end
    }

    
    

}




