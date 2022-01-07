use smol_str::SmolStr;

use crate::compiler::tokens::Token;

use super::stmt::Stmt;


#[derive(Debug, Clone, PartialEq)]
pub enum Decl {
    //function  name    args:      name     type      ret_typ   body
    Function(SmolStr, Option<Vec<(SmolStr, Token)>>, Token, Vec<Stmt>),
    Get,
    Record(SmolStr, Vec<(SmolStr, Token)>),
    Module(SmolStr, fnv::FnvHashMap<SmolStr, Decl>),
}
