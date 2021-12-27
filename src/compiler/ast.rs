use crate::compiler::nodes::*;
#[derive(Debug)]
pub enum AST {
    Decl(decl::Decl),
    Stmt(stmt::Stmt),
    Expr(expr::Expr),
}

impl From<decl::Decl> for AST {
    fn from(node: decl::Decl) -> Self {
        AST::Decl(node)
    }
}

impl From<stmt::Stmt> for AST {
    fn from(node: stmt::Stmt) -> Self {
        AST::Stmt(node)
    }
}

impl From<expr::Expr> for AST {
    fn from(node: expr::Expr) -> Self {
        AST::Expr(node)
    }
}


