use std::ops::Range;
use crate::frontend::{tokens::Token, nodes::expr::Expr};
use smol_str::SmolStr;

#[derive(Debug)]
pub enum ErrTyp {
    UnknownToken(SmolStr),
    UnexpectedEndOfParsing,
    UnexpectedToken(Token),
    UnknownType(Token),
    UnclosedDelimiter,
    InvalidAssignmentTarget(Expr),
}

#[derive(Debug)]
pub struct ParseError { 
    ty: ErrTyp,
    span: Option<Range<usize>>,
    message : Option<String>
}


impl ParseError {

    pub fn new( ty : ErrTyp, span : Option<Range<usize>>, message: Option<String> ) -> Self {
        Self { ty, span, message }
    }
    

}

