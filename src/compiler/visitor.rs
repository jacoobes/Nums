use super::{nodes::expr::Expr, nodes::decl::*, nodes::stmt::*};

pub trait Visitor<T> {
    fn visit_expr(&mut self, e: Expr) -> T;
    fn visit_stmt(&mut self, s: Stmt) -> T;
    fn visit_decl(&mut self , d: Decl) -> T;
}
