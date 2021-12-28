use crate::compiler::types::types::{TypeInfo, Type};
use crate::compiler::visitor::Visitor;
use crate::compiler::nodes::{expr::Expr, decl::Decl, stmt::Stmt};
use crate::compiler::types::types::Engine;

use super::types::TypeId;
pub struct TypeChecker {
    engine: Engine
}

impl Visitor<()> for TypeChecker {
    fn visit_expr(&mut self, e: Expr) -> () {
        todo!()
    }

    fn visit_stmt(&mut self, s: Stmt) -> () {
        todo!()
    }

    fn visit_decl(&mut self , d: Decl) -> () {
        todo!()
    }
}
