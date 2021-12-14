use smol_str::SmolStr;
use crate::Token;

///
/// Possible to refactor and use different data structure (small vec)? instead of Box to increase compile speed!
/// 
#[derive(Debug, Clone, PartialEq)]
pub enum Expr {
    Binary {
        operator: Token,
        left : Box<Expr>,
        right : Box<Expr>
    },
    Unary{
        operator: Token,
        expr : Box<Expr>,
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
