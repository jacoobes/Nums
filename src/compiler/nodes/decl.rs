
use crate::compiler::tokens::Token;

use super::expr::Expr;
use super::stmt::Stmt;

#[derive(Debug, Clone, PartialEq)]
pub enum Decl {
    //function  name    args:      name     type      ret_typ   body
    Function(Token, Option<Vec<Token>>, Vec<Stmt>),
    ExposedFn(Token, Option<Vec<Token>>, Vec<Stmt>),
    Program(Vec<Stmt>),
    Use(Expr),
}
