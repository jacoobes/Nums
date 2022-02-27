use smol_str::SmolStr;
use crate::frontend::tokens::Token;

#[derive(Clone, PartialEq)]
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
    Val(Token),
    Call(Box<Expr>, Vec<Expr>),
    Get(Box<Expr>, Token)
}

impl std::fmt::Debug for Expr {
    
    fn fmt(&self, f : &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match &self {
           Expr::Logical { operator, left, right }  
           | Expr::Binary { operator, left, right }
           => write!(f, "({:?} {:?}, {:?})", operator, *left, *right),
           Expr::Unary { operator, expr } => write!(f, "({:?} {:?})", operator, *expr),
           Expr::Group { expr } => write!(f, "( {:?} )", *expr),
           Expr::Assignment { var, value } => write!(f, "( ASSIGN {:?} = {:?}", var, *value),
           Expr::Double(n) => write!(f, "( {:?} ) ", n),
           Expr::Bool(b) => write!(f, "( {:?} ) ", b),
           Expr::Integer(i) => write!(f, "( {i} )"),
           Expr::String(s) => write!(f, "( '{s}' ) "),
           Expr::Val(t) => write!(f, "( VARIABLE {:?} ) ", t),
           Expr::Get(expr, tok) => write!(f, "( {:?}:{:?} )", *expr, tok),
           Expr::Call(expr, args) => write!(f,"CALL ({:?} WITH {:?})", *expr, args)
        }

    }



}
