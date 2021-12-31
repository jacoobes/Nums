use std::collections::BTreeMap;

use crate::compiler::tokens::Token;




#[derive(Debug, Clone, PartialEq, Eq)]

pub enum Primitives {
    Unit,
    Char,
    String,
    Int,
    Float,
    Bool
}

pub enum Type {
    Never,
    Prim(Primitives),
    Func(Box<Type>, Box<Type>),
    Record( BTreeMap<Token, Box<Type>>)
}
