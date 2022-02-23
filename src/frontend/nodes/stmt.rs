
use crate::frontend::tokens::Token;

use super::expr::Expr;

#[derive(Debug, Clone, PartialEq)]
pub enum Stmt {
    //an expression, but has semicolon at end
    ExprStatement(Expr),
    //Mut or const //ident  //typ //expr stmt
    Mut(Token, Expr),

    Let(Token, Expr),

    While(Expr, Vec<Stmt>),

    Block(Vec<Stmt>),

    IfElse(Expr, Vec<Stmt>, Option<Vec<Stmt>>),
    
    Return(Expr)
}