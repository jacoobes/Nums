use smol_str::SmolStr;

use crate::compiler::tokens::Token;

use super::expr::Expr;

#[derive(Debug, Clone, PartialEq)]
pub enum Stmt {
    //an expression, but has semicolon at end
    ExprStatement(Expr),
    //Mut or const //ident  //typ //expr stmt
    Mut(SmolStr, Option<Token>, Box<Stmt>),

    Let(SmolStr, Option<Token>, Box<Stmt>),

    While(Expr, Vec<Stmt>),

    Block(Vec<Stmt>),

    IfElse(Expr, Vec<Stmt>, Option<Vec<Stmt>>),
}