
use super::expr::Expr;
use super::stmt::Stmt;
use crate::compiler::tokens::Token;

#[derive(Debug, Clone, PartialEq)]
pub enum Decl {
    //function  name    args:      name     type      ret_typ   body
    Function(Token, Option<Vec<Token>>, Vec<Stmt>),
    ExposedFn(Token, Option<Vec<Token>>, Vec<Stmt>),
    Use(Expr),
}
