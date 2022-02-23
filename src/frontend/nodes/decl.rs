
use crate::frontend::tokens::Token;

use super::expr::Expr;
use super::stmt::Stmt;

#[derive(Debug, Clone, PartialEq)]
pub enum Decl {
    //function  name    args:      name     type      ret_typ   body
    Function(Token, Vec<Token>, Vec<Stmt>),
    ExposedFn(Token, Vec<Token>, Vec<Stmt>),
    Task(Token, Vec<Token>, Vec<Stmt>),
    ExposedTask(Token,Vec<Token>, Vec<Stmt>),
    Program(Vec<Stmt>),
    Use(Expr),
}