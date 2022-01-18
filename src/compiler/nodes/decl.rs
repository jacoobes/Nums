use smol_str::SmolStr;

use super::decl::Decl::*;
use super::path::PackagePath;
use super::stmt::Stmt;
use crate::compiler::tokens::Token;

#[derive(Debug, Clone, PartialEq)]
pub enum Decl {
    //function  name    args:      name     type      ret_typ   body
    Function(SmolStr, Option<Vec<(SmolStr, Token)>>, Token, Vec<Stmt>),
    ExposedFn(SmolStr, Option<Vec<(SmolStr, Token)>>, Token, Vec<Stmt>),
    Get(PackagePath),
    Record(SmolStr, Vec<(SmolStr, Token)>),
    ExposedRec(SmolStr, Vec<(SmolStr, Token)>),
    Module(SmolStr, fnv::FnvHashMap<SmolStr, Decl>),
    ExposedModule(SmolStr, fnv::FnvHashMap<SmolStr, Decl>),
}

impl Decl {
    pub fn get_name(&self) -> SmolStr {
        match &self {
            &Module(n, ..)
            | &Function(n, ..)
            | &ExposedFn(n, ..)
            | &Record(n, ..)
            | &ExposedRec(n, ..)
            | &ExposedModule(n, ..) => n.clone(),
            &Get(path) => path
                .path()
                .iter()
                .rev()
                .find_map(|path| path.get_ident())
                .unwrap_or(SmolStr::from("ALL")),
        }
    }
}
