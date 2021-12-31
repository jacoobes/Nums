use crate::compiler::tokens::Token::*;
use crate::compiler::visitor::Visitor;
use crate::compiler::nodes::{expr::Expr, decl::Decl, stmt::Stmt};
pub struct TypeChecker; 