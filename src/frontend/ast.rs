use std::ops::Deref;

use numsc::structures::stack::Stack;
use super::nodes::{decl::Decl, stmt::Stmt, expr::Expr};

pub struct AST(pub Vec<Decl>);


impl AST {
    pub fn walk_decl(&self) {
        match self {
            Decl::ExposedFn(..) | Decl::Function(..) => {},
            Decl::Program( stmts) => {
                for stmt in stmts {
                    self.walk_stmt(stmt)
                }
            },
            Decl::Use(..) => {},
        }
        
    }
    pub fn walk_stmt(&self, stmt: &Stmt) {
        match stmt {
            Stmt::ExprStatement(expr) => self.walk_expr(expr),
            Stmt::Mut(name, expr)
            | Stmt::Let(name, expr)=> self.walk_expr(expr),
            Stmt::While(condition, stmts) => {
                for stmt in stmts {
                    self.walk_stmt(stmt)
                }
            }
            Stmt::Block(stmts) => {
                for stmt in stmts {
                    self.walk_stmt(stmt)
                }
            }
            Stmt::IfElse(condition, true_stmts, false_stmts) => {
                todo!()
            }
            Stmt::Return(expr) => {
                self.walk_expr(expr)
            }
        }
    }

    pub fn walk_expr(&self, expr: &Expr) {
        match expr {
            Expr::Logical { left,right,operator } => {

            }
            Expr::Binary { left,right,operator } => {}
            Expr::Unary { expr,operator } => {}
            Expr::Group { expr } => {}
            Expr::Assignment { var, value  } => {}
            Expr::Double(val) => {}
            Expr::Integer(val) => {}
            Expr::String(val) => {}
            Expr::Bool(val) => {}
            Expr::Val(lit) => {}
            Expr::Call(expr, arguments) => {}
            Expr::Get(expr, name) => {}
            Expr::CSE(values) => {}
        }
    }

}

/// Deref to make AST = first node
impl Deref for AST {
    type Target = Vec<Decl>;

    fn deref(&self) -> &Self::Target {
        &self.0
    }
}

impl std::fmt::Debug for AST {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
       write!(f,"{:?}", self.0)
    }
}

