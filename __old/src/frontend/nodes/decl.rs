
use numsc::structures::tokens::Token;
use super::expr::Expr;
use super::stmt::Stmt;

#[derive(Clone, PartialEq)]
pub enum Decl {
    //function  name    args:      name     type      ret_typ   
    Function(Token, Vec<Token>, Vec<Stmt>),
    ExposedFn(Token, Vec<Token>, Vec<Stmt>),
    Program(Vec<Stmt>),
    Use(Expr),
}

impl Decl {
    fn ty_fn (&self) -> &str {
        match self {
            Decl::Function(..) => "FN",
            Decl::ExposedFn(..) => "EXPOSED FN",
            Decl::Program(..) => "PROGRAM",
            Decl::Use(..) => "USE",
        }
    }
}

impl std::fmt::Debug for Decl {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Decl::Function(na, args, _ )
                | Decl::ExposedFn(na, args,_ )
                    => write!(f, "[ {} {:?} PARAMS {:?} ", self.ty_fn(), na, args, ),
                Decl::Program(_) | Decl::Use(_) => write!(f, "[ {} ", self.ty_fn()),
        }
    }
}
