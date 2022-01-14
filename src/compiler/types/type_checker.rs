use crate::compiler::tokens::Token;
use crate::compiler::visitor::Visitor;
use crate::compiler::nodes::{expr::Expr, decl::Decl, stmt::Stmt};
use crate::compiler::types::types::Primitives::*;
use super::types::{TypeInfo, Primitives};
pub struct TypeAnalyzer; 



impl Visitor<Result<TypeInfo, std::string::String>> for TypeAnalyzer {
    fn visit_expr(&mut self, e: Expr) -> Result<TypeInfo, std::string::String> {
        match e {
            Expr::Logical { operator, left, right } => self.combine_type(operator, *left, *right),
            Expr::Binary { operator, left, right } => self.combine_type(operator, *left, *right),
            Expr::Unary { operator, expr } => {
                match (operator, *expr)  {
                    (Token::Plus | Token::Minus,  Expr::Integer(_) ) => Ok(TypeInfo::Prim(Int)),
                    (Token::Plus | Token::Minus,  Expr::Double(_) ) => Ok(TypeInfo::Prim(Float)),
                    (Token::Bang, Expr::Bool(_)) => Ok(TypeInfo::Prim(Bool)),
                    (_, e) => self.visit_expr(e)   
                }
            },
            Expr::Group { expr } => self.visit_expr(*expr),
            Expr::Assignment { var : _, value : _ } => Ok(TypeInfo::Prim(Unit)),
            Expr::Double(_) => Ok(TypeInfo::Prim(Float)),
            Expr::Integer(_) => Ok(TypeInfo::Prim(Int)),
            Expr::String(_) => Ok(TypeInfo::Prim(String)),
            Expr::Unit => Ok(TypeInfo::Prim(Unit)),
            Expr::Bool(_) => Ok(TypeInfo::Prim(Bool)),
            Expr::Char(_) => Ok(TypeInfo::Prim(Char)),
            Expr::Val(_) => Ok(TypeInfo::Unknown),
        }
    }



    fn visit_stmt(&mut self, s: Stmt) -> Result<TypeInfo, std::string::String> {
        todo!()
    }

    fn visit_decl(&mut self , d: Decl) -> Result<TypeInfo, std::string::String> {
        match d {
            Decl::Function(name, args, ret, block) => todo!(),
            Decl::Record(_, _) => todo!(),
            Decl::Module(name, modules) => todo!(),
            Decl::Get(..) => todo!(),
            Decl::ExposedFn(_, _, _, _) => todo!(),
            Decl::ExposedRec(_, _) => todo!(),
            Decl::ExposedModule(_,_) => todo!()
        }
    }
}

impl TypeAnalyzer {
    fn combine_type(&mut self, operator: Token, lhs : Expr, rhs : Expr) -> Result<TypeInfo, std::string::String> {
        match (operator, lhs, rhs) {
            (t, Expr::Double(_), Expr::Double(_)) if matches!(t, Token::Plus | Token::Minus | Token::Star | Token::FowardSlash | Token::Caret ) => Ok(TypeInfo::Prim(Primitives::Float)),

            (t, Expr::Integer(_), Expr::Integer(_))if matches!(t, Token::Plus | Token::Minus | Token::Star | Token::FowardSlash | Token::Caret ) => Ok(TypeInfo::Prim(Primitives::Int)),

            (_, Expr::Group{expr}, _ ) |  (_,  _, Expr::Group{expr},) => { self.visit_expr(*expr) }

              (t, Expr::String(_), Expr::String(_)) 
            | (t, Expr::Char(_), Expr::Char(_))
            | (t, Expr::Integer(_), Expr::Integer(_)) 
            | (t, Expr::Double(_), Expr::Double(_)) if matches!(t,  Token::Eq | Token::NotEq | Token::GreaterEq | Token::LessEq ) => 
            Ok(TypeInfo::Prim(Primitives::Bool)),

            (_, Expr::Binary{operator , left, right}, _ ) => {
                self.combine_type(operator, *left, *right)
            }
            (_,  _, Expr::Binary{operator , left, right} ) => {
                self.combine_type(operator, *left, *right)
            }
            (_, Expr::Logical{operator , left, right}, _ ) => {
                self.combine_type(operator, *left, *right)
            }
            (_,  _, Expr::Logical{operator , left, right} ) => {
                self.combine_type(operator, *left, *right)
            }
            (other, l, r) => Err(format!("TypeError: {:?} {:?} {:?} cannot be deduced as a type", &l, other, &r))


        }
    } 
}