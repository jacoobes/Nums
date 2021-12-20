
use std::fmt::Debug;
use smol_str::SmolStr;
use std::ops::Range;
use crate::compiler::source::Source;

use super::faults::Faults;
#[derive(Debug)]
pub struct Span {
   range: Range<usize>, 
   typ:   Faults
}

impl Span {
    pub fn new(range : Range<usize>, typ: Faults) -> Self {
        Self {
            range,
            typ
        }
    }




}