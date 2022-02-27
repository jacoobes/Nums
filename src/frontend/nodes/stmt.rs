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
            Stmt::Mut(n,e) => write!(f, "[ MUT {:?} = {:?} ]", n,e),
            Stmt::Let(n,e) => write!(f, "[ LET {:?} = {:?} ]", n,e),
            Stmt::Return(e)=> write!(f, "[ RETURN {:?} ]", e),
            Stmt::ExprStatement(ex) => write!(f, "{:?}", ex ), 
            Stmt::Block(..) => {
                write!(f, " BLOCK ")
            },
            Stmt::IfElse(ex, ..) => {
                write!(f, " [ IF ELSE: {:?}", ex ) 
            }
            Stmt::While(ex, ..) => {
                write!(f, " [ WHILE: {:?}, ", ex)
            }

      }
    } 
}
