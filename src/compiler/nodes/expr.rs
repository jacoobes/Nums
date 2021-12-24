use smol_str::SmolStr;

use crate::compiler::tokens::Token;

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