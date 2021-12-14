use smol_str::SmolStr;
use crate::Token;

#[derive(Debug, Clone, PartialEq)]
pub enum Expr {
    Term {
        operator: Token,
        left : Box<Expr>,
        right : Box<Expr>
    },
    Factor {
        operator: Token,
        left : Box<Expr>,
        right : Box<Expr>
    },
    Unary{
        operator: Token,
        expr : Box<Expr>,
    },
    Power {
        left : Box<Expr>,
        right : Box<Expr>
    },
    Group {
        expr : Box<Expr>,
    },
    Double(f32),
    Integer(i32),
    String(SmolStr),
    F64(f64),
    I64(i64),
    Bool(bool),
    Char(char)

}

impl Expr {
    pub fn traverse(&self) {
    
    }
}
