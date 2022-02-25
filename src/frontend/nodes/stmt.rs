use crate::frontend::tokens::Token;

use super::expr::Expr;

#[derive( Clone, PartialEq)]
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





impl std::fmt::Debug for Stmt {

    fn fmt(&self, f : &mut std::fmt::Formatter) -> std::fmt::Result {
        match &self {
            Stmt::Mut(n,e) => write!(f, "[ mut {:?} {:?} ]", n,e),
            Stmt::Let(n,e) => write!(f, "[ let {:?} {:?} ]", n,e),
            Stmt::Return(e)=> write!(f, "[ return {:?} ]", e),
            Stmt::Block(li) => {
                write!(f, " block {:?}", li)
            },
            _other => {
                write!(f, "unimplemented") 
            }

    }
    } }
