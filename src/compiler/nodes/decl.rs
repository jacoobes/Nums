use smol_str::SmolStr;

use crate::compiler::tokens::Token;
use super::stmt::Stmt;
use super::decl::Decl::*;

#[derive(Debug, Clone, PartialEq)]
pub enum Decl {
    //function  name    args:      name     type      ret_typ   body
    Function(SmolStr, Option<Vec<(SmolStr, Token)>>, Token, Vec<Stmt>),
    ExposedFn(SmolStr, Option<Vec<(SmolStr, Token)>>, Token, Vec<Stmt>),
    Get(Vec<Path>),
    Record(SmolStr, Vec<(SmolStr, Token)>),
    ExposedRec(SmolStr, Vec<(SmolStr, Token)>),
    Module(SmolStr, fnv::FnvHashMap<SmolStr,  Decl>),
    ExposedModule(SmolStr, fnv::FnvHashMap<SmolStr, Decl>)
}
#[derive(Debug, Clone, PartialEq)]
pub enum Path {
    Ident(SmolStr),
    All
}

impl Decl {
    pub fn get_name(&self) -> SmolStr {
        match &self {
            &Module(n, ..) 
            | &Function(n, ..) 
            | &ExposedFn(n, .. ) 
            | &Record(n, ..) 
            | &ExposedRec(n, .. ) 
            | &ExposedModule(n,.. ) => n.clone(),
            &Get(..) => SmolStr::from("Get")

        }   
    }
}