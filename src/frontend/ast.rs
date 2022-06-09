use std::borrow::Borrow;
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
    frames: Vec<Frame>
}

fn extract(n: Token) -> SmolStr {
    if let Token::Identifier(s) = n {
      return s;
    }
    panic!("Incorrectly bound a non identifier token to a name");
}

impl AST {
    pub fn new(tree : Vec<Decl>) -> Self {
        AST { tree, frames : Vec::new()  }
    }
    pub fn walk(mut self) {
        for node in self.tree {
            match node {
                Decl::ExposedFn(name, args_name, block)
                | Decl::Function(name, args_name, block) => {
                    let mut builder = FrameBuilder::new(extract(name));
                    for stmt in block {
                        AST::walk_stmt(stmt, &mut builder);
                    }
                    self.frames.push(builder.build());
                },
                Decl::Program( stmts) => {
                    let mut builder = FrameBuilder::new("Start".into());
                    for stmt in stmts {
                         AST::walk_stmt(stmt, &mut builder);
                    }
                    self.frames.push(builder.build());
                },
                Decl::Use(..) => {
                    todo!()
                },
            }
        }

        
    }
    pub fn walk_stmt(stmt: Stmt, builder: &mut FrameBuilder) {
        match stmt {
            Stmt::ExprStatement(expr) => AST::walk_expr(expr, builder ),
            Stmt::Mut(name, expr)
            | Stmt::Let(name, expr)=> AST::walk_expr(expr, builder ),
            Stmt::While(condition, stmts) => {
                for stmt in stmts {
                    AST::walk_stmt(stmt, builder)
                }
            }
            Stmt::Block(stmts) => {
                for stmt in stmts {
                    AST::walk_stmt(stmt, builder)
                }
            }
            Stmt::IfElse(condition, true_stmts, false_stmts) => {
                todo!()
            }
            Stmt::Return(expr) => {
                AST::walk_expr(expr, builder)
            }
        }
    }

    pub fn walk_expr(expr: Expr, builder: &mut FrameBuilder) {
        match expr {
            Expr::Logical { left,right,operator } => {
                todo!()
            }
            Expr::Binary { left,right,operator } => {}
            Expr::Unary { expr,operator } => {
                match operator {
                    Token::Bang => {
                        AST::walk_expr(*expr, builder);
                        builder.with_opcode(OpCode::Not);
                    },
                    Token::Minus => {
                        AST::walk_expr(*expr, builder);
                        builder.with_opcode(OpCode::Negate);
                    },
                    Token::Plus => {
                        //no use yet but it exists
                        AST::walk_expr(*expr, builder);
                    },
                    _ => panic!("aaaaaa not a valid operator for unary")
                }
            }
            Expr::Group { expr } => {
               AST::walk_expr(*expr, builder)
            }
            Expr::Assignment { var, value  } => todo!(),
            Expr::Number(val) => {
                builder.with_const(Rc::new(Value::Number(val)));
            }
            Expr::String(val) => {
                builder.with_const(Rc::new(Value::Str(val /*clone!!!!!*/)));
            }
            Expr::Bool(val) => {
                builder.with_const(Rc::new(Value::Boolean(val)));
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
       write!(f,"{:?}", self.tree)
    }
}

