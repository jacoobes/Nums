use super::{nodes::expr::Expr, nodes::decl::*, nodes::stmt::*};

trait Visitor {
    type Node;
    fn visit(&self, node : Self::Node ) -> Self::Node;
}


impl Visitor for Expr {
    type Node = Expr;
    fn visit(&self, node: Self::Node) -> Self::Node {
        match node {
            Expr::Logical { operator, left, right } => todo!(),
            Expr::Binary { operator, left, right } => todo!(),
            Expr::Unary { operator, expr } => todo!(),
            Expr::Group { expr } => todo!(),
            Expr::Assignment { var, value } => todo!(),
            Expr::Double(_) => todo!(),
            Expr::Integer(_) => todo!(),
            Expr::String(_) => todo!(),
            Expr::Unit => todo!(),
            Expr::Bool(_) => todo!(),
            Expr::Char(_) => todo!(),
            Expr::Val(_) => todo!(),
        }
    }
}

impl Visitor for Decl {
    type Node = Decl;
    fn visit(&self, node : Self::Node ) -> Self::Node {
        match node {
            Decl::Function(_, _, _, _) => todo!(),
            Decl::Record(_, _) => todo!(),
            Decl::Module(_) => todo!(),
        }
    }
}

impl Visitor for Stmt {
    type Node = Stmt;

    fn visit(&self, node : Self::Node ) -> Self::Node {
        match node {
            Stmt::ExprStatement(_) => todo!(),
            Stmt::VarDecl(_, _, _, _) => todo!(),
            Stmt::While(_, _) => todo!(),
            Stmt::Block(_) => todo!(),
            Stmt::IfElse(_, _, _) => todo!(),
        }
    }
}