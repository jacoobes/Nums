
use std::{ops::Range, fmt::{Display, Debug}};
use pretty::{RcDoc, Doc};
use smol_str::SmolStr;

use super::faults::Faults;
struct Span {
   loc_of_err: Range<usize>, 
   filename : SmolStr,
   source : &'static str,
   typ:  Faults
}

impl Span {

    fn display(&self) -> RcDoc<()> {
        todo!()
    }
}
