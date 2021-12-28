use crate::compiler::types::types::TypeInfo;
use crate::compiler::visitor::Visitor;
use crate::compiler::nodes::{expr::Expr, decl::Decl, stmt::Stmt};
use crate::compiler::types::types::Engine;

use super::types::TypeId;
pub struct TypeChecker {
    engine: Engine
}


impl Visitor<()> for TypeChecker {
    fn visit_expr(&mut self, e: Expr) -> () {
        match e {
            Expr::Logical { operator, left, right } => {},
            Expr::Binary { operator, left, right } => todo!(),
            Expr::Unary { operator, expr } => todo!(),
            Expr::Group { expr } => {},
            Expr::Assignment { var, value } => todo!(),
            Expr::Double(_)
            | Expr::Integer(_)
            | Expr::String(_)
            | Expr::Bool(_)
            | Expr::Char(_) => (),
            Expr::Unit => (),
            Expr::Val(v,) => todo!(),
        }
    }

    fn visit_stmt(&mut self, s: crate::compiler::nodes::stmt::Stmt) -> () {
        match s {
            Stmt::ExprStatement(_) => todo!(),
            Stmt::VarDecl(_, _, _, _) => todo!(),
            Stmt::While(_, _) => todo!(),
            Stmt::Block(_) => todo!(),
            Stmt::IfElse(_, _, _) => todo!(),
        }
    }

    fn visit_decl(&mut self , d: crate::compiler::nodes::decl::Decl) -> () {
        match d {
            Decl::Function(_, _, _, _) => todo!(),
            Decl::Record(_, _) => todo!(),
            Decl::Module(_) => todo!(),
        }
    }
}

impl TypeChecker {
    fn insert_type(&mut self, typ: TypeInfo) -> TypeId {
        self.engine.insert(typ)
    }
}