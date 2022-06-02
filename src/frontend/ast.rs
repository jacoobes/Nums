use core::panicking::panic;
use std::ops::Deref;
use std::rc::Rc;
use numsc::structures::frame::Frame;
use numsc::structures::frame_builder::FrameBuilder;
use numsc::structures::opcode::OpCode;

use numsc::structures::stack::Stack;
use numsc::structures::value::Value;
use numsc::vm::numsc::NumsC;
use smol_str::SmolStr;
use crate::frontend::tokens::Token;
use super::nodes::{decl::Decl, stmt::Stmt, expr::Expr};

pub struct AST {
    pub tree: Vec<Decl>,
    bc_emitter: FrameBuilder
}

fn extract(n: Token) -> SmolStr {
    if let Token::Identifier(s) = n {
      return s
    }
    panic!("Incorrectly bound a non identifier token to a name");
}

impl AST {
    pub fn new(tree : Vec<Decl>) -> Self {
        AST { tree, bc_emitter : FrameBuilder::new("expressions".into()) }
    }
    pub fn walk(self) {
        match self {
            Decl::ExposedFn(name, args_name, block)
            | Decl::Function(name, args_name, block) => {
                FrameBuilder::new(extract(name));
                for stmt in block {
                    self.walk_stmt(stmt)
                }
            },
            Decl::Program( stmts) => {
                for stmt in stmts {
                    self.walk_stmt(stmt)
                }
            },
            Decl::Use(..) => {
                todo!()
            },
        }
        
    }
    pub fn walk_stmt(self, stmt: Stmt) {
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

    pub fn walk_expr(self, expr: Expr) {
        match expr {
            Expr::Logical { left,right,operator } => {
                todo!()
            }
            Expr::Binary { left,right,operator } => {}
            Expr::Unary { expr,operator } => {
                match operator {
                    Token::Bang => {
                        self.walk_expr(*expr);
                        self.bc_emitter.push_opcode(OpCode::Not)
                    },
                    Token::Minus => {
                        self.walk_expr(*expr);
                        self.bc_emitter.push_opcode(OpCode::Negate)
                    },
                    Token::Plus => {
                        //no use yet but it exists
                        self.walk_expr(*expr);
                    },
                    _ => panic("aaaaaa not a valid operator for unary")
                }
            }
            Expr::Group { expr } => {
                self.walk_expr(*expr)
            }
            Expr::Assignment { var, value  } => todo!(),
            Expr::Number(val) => {
                self.bc_emitter.push_const(Rc::new(Value::Number(val)))
            }
            Expr::String(val) => {
                self.bc_emitter.push_const(Rc::new(Value::Str(val)))
            }
            Expr::Bool(val) => {
                self.bc_emitter.push_const(Rc::new(Value::Boolean(val)))
            }
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
        &self.tree
    }
}

impl std::fmt::Debug for AST {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
       write!(f,"{:?}", self.0)
    }
}

