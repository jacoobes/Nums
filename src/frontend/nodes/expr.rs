use smol_str::SmolStr;
use crate::frontend::tokens::Token;

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
        var : Token,
        value: Box<Expr>
    },
    Double(f32),
    Integer(isize),
    String(SmolStr),
    Bool(bool),
    Val(SmolStr),
    Call(Box<Expr>, Vec<Expr>),
    Get(Box<Expr>, Token)
}


