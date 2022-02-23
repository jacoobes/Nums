
use std::fmt::{Debug};

use crate::frontend::tokens::Token;

use super::expr::Expr;
use super::stmt::Stmt;

#[derive(Clone, PartialEq)]
pub enum Decl {
    //function  name    args:      name     type      ret_typ   body
    Function(Token, Vec<Token>, Vec<Stmt>),
    ExposedFn(Token, Vec<Token>, Vec<Stmt>),
    Task(Token, Vec<Token>, Vec<Stmt>),
    ExposedTask(Token,Vec<Token>, Vec<Stmt>),
    Program(Vec<Stmt>),
    Use(Expr),
}

impl Decl {
    fn ty_fn (&self) -> &str {
        match self {
            Decl::Function(..) => "fn",
            Decl::ExposedFn(..) => "exposed fn",
            Decl::Task(..) => "task",
            Decl::ExposedTask(..) => "exposed task",
            Decl::Program(..) => "program",
            Decl::Use(..) => "use",
        }
    }
}

impl Debug for Decl {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Decl::Function(na, args, block)
                | Decl::ExposedFn(na, args, block)
                | Decl::Task(na, args, block)
                | Decl::ExposedTask(na, args, block) 
                    => write!(f, " {} {:?} {:?} {{
                          {:?}  
                        }} ", self.ty_fn(), na, args, block),
                Decl::Program(s) => write!(f, "{} {:?}", self.ty_fn(), s),
                Decl::Use(path) => write!(f, "{}, {:?}", self.ty_fn(), &path),
        }
    }
}