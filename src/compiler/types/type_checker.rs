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
            Expr::Logical { operator : _, left, right } => {
                self.visit_expr(*left);
                self.visit_expr(*right);
            },
            Expr::Binary { operator : _, left, right } => {
                self.visit_expr(*left);
                self.visit_expr(*right);
            },
            Expr::Unary { operator : _,  expr } => {
                self.visit_expr(*expr);
            },
            Expr::Group { expr } => {
                self.visit_expr(*expr);
            },
            Expr::Assignment { var: _, value : _ } => { self.insert_type(TypeInfo::Unit); },
            Expr::Double(_)  => { self.insert_type(TypeInfo::Float); },
            Expr::Integer(_) => { self.insert_type(TypeInfo::Int); },
            Expr::String(_) => { self.insert_type(TypeInfo::String); },
            Expr::Unit => { self.insert_type(TypeInfo::Unit); },
            Expr::Bool(_) => { self.insert_type(TypeInfo::Bool); },
            Expr::Char(_) => { self.insert_type(TypeInfo::Char); },
            Expr::Val(_,) => { self.insert_type(TypeInfo::Unknown); },
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