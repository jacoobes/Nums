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

pub enum TypeInfo {
    Never,
    Prim(Primitives),
    Func(Box<TypeInfo>, Box<TypeInfo>),
    Record( BTreeMap<Token, Box<TypeInfo>>),
    Unknown,
}