use crate::Token;
use smol_str::SmolStr;

///
/// Possible to refactor and use different data structure (small vec)? instead of Box to increase compile speed!
///
#[derive(Debug, Clone, PartialEq)]
pub enum Expr {
    Binary {
        operator: Token,
        left: Box<Expr>,
        right: Box<Expr>,
    },
    Unary {
        operator: Token,
        expr: Box<Expr>,
    },
    Group {
        expr: Box<Expr>,
    },
    Double(f32),
    Integer(i32),
    String(SmolStr),
    Unit,
    //    F64(f64), maybe add float64 and i64 literals in the future?
    //    I64(i64),
    Bool(bool),
    Char(char),
}

#[derive(Debug, Clone, PartialEq)]
pub enum Stmt {
    //an expression, but has semicolon at end
     ExprStatement(Expr),
     
     VarDecl(Token, SmolStr, Box<Stmt>),

     While(Expr, Vec<Stmt>),

     IfElse(Expr, Vec<Stmt>, Option<Vec<Stmt>>),

     Block(Vec<Stmt>),
   //function  name    args: name : type       Body         
     Function(SmolStr, Option<Vec<(Token, Token)>>,  Vec<Stmt>, Token),

     Module(SmolStr)
}