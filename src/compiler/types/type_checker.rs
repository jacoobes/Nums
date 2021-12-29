use crate::compiler::types::types::{TypeInfo, Type};
use crate::compiler::visitor::Visitor;
use crate::compiler::nodes::{expr::Expr, decl::Decl, stmt::Stmt};
pub struct TypeChecker; 

impl Visitor<Result<TypeInfo, String>> for TypeChecker {
    fn visit_expr(&mut self, expr: Expr) -> Result<TypeInfo, String> {
        match expr {
            Expr::Logical { operator, left, right } => {
               todo!("implementing type checking for logical ops")
            } ,
            Expr::Binary { operator, left, right } => {
               todo!("Implementing type checking for binarys")
            },
            Expr::Unary { operator: _, expr } => { self.visit_expr(*expr) },
            Expr::Group { expr } => self.visit_expr(*expr),
            Expr::Assignment { var : _, value: _ } => Ok(TypeInfo::Unit),
            Expr::Double(_) => Ok(TypeInfo::Float),
            Expr::Integer(_) => Ok(TypeInfo::Int),
            Expr::String(_) => Ok(TypeInfo::String),
            Expr::Unit =>   Ok(TypeInfo::Unit),
            Expr::Bool(_) => Ok(TypeInfo::Bool),
            Expr::Char(_) => Ok(TypeInfo::Char),
            Expr::Val(_) => Ok(TypeInfo::Unknown),
        }
    }

    fn visit_stmt(&mut self, stmt: Stmt) -> Result<TypeInfo, std::string::String>{
        match stmt {
            Stmt::ExprStatement(expr) => self.visit_expr(expr),
            Stmt::VarDecl(_, _, _, _) => Ok(TypeInfo::Unit),
            Stmt::While(_, _) => Ok(TypeInfo::Unit),
            Stmt::Block(_) => Ok(TypeInfo::Unit),
            Stmt::IfElse(_, _, _) => Ok(TypeInfo::Unit),
        }
    }

    fn visit_decl(&mut self , d: Decl) -> Result<TypeInfo, std::string::String> {
        match d {
            Decl::Function(_, args, ret_type, block) => {
                for stmt in block {
                  println!("{:?}",  self.visit_stmt(stmt))
                };
                Ok(TypeInfo::Int)
            },
            Decl::Record(_, _) => Ok(TypeInfo::Unit),
            Decl::Module(_) => Ok(TypeInfo::Unit),
        }
    }
}
