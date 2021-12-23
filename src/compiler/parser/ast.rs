use crate::compiler::tokens::Token;
use smol_str::SmolStr;

pub enum AST {
    Decl(Decl),
    Stmt(Stmt),
    Expr(Expr),
}

impl From<Decl> for AST {
    fn from(node: Decl) -> Self {
        AST::Decl(node)
    }
}

impl From<Stmt> for AST {
    fn from(node: Stmt) -> Self {
        AST::Stmt(node)
    }
}

impl From<Expr> for AST {
    fn from(node: Expr) -> Self {
        AST::Expr(node)
    }
}
///
/// Possible to refactor and use different data structure (small vec)? instead of Box to increase compile speed!
///
#[derive(Debug, Clone, PartialEq)]
pub enum Expr {
    Logical {
        operator: Token,
        left: Box<Expr>,
        right: Box<Expr>,
    },
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
    Assignment {
        var : SmolStr,
        value: Box<Expr>
    },
    Double(f32),
    Integer(i16),
    String(SmolStr),
    Unit,
    //    F64(f64), maybe add float64 and i64 literals in the future?
    //    I64(i64),
    Bool(bool),
    Char(char),
    Val(SmolStr),
}

#[derive(Debug, Clone, PartialEq)]
pub enum Stmt {
    //an expression, but has semicolon at end
    ExprStatement(Expr),
    //Mut or const//ident  //typ //expr stmt
    VarDecl(Token, SmolStr, Token, Box<Stmt>),

    While(Expr, Vec<Stmt>),

    Block(Vec<Stmt>),

    IfElse(Expr, Vec<Stmt>, Option<Vec<Stmt>>),
}
#[derive(Debug, Clone, PartialEq)]
pub enum Decl {
    //function  name    args:      name     type      ret_typ   body
    Function(SmolStr, Option<Vec<(SmolStr, Token)>>, Token, Vec<Stmt>),
    Record(SmolStr, Vec<(SmolStr, Token)>),
    Module(SmolStr),
}
