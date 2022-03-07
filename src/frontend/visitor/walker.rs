use std::borrow::Cow;

use crate::frontend::nodes::{ decl::Decl, expr::Expr, stmt::Stmt} ;
use crate::frontend::ast::AST;
pub trait Walker<T> {
    fn walk(&self) -> T; 
    fn visit_decl(&self, d : &Decl) -> T;
    fn visit_stmt(&self, s : &Stmt) -> T;
    fn visit_expr(&self, e : &Expr) -> T;

}

impl<'a> Walker<Cow<'a, str>> for AST {
    
    fn walk(&self) -> Cow<'a, str> {
        let mut ast_str = String::new(); 
        for node in &self.0 {  ast_str.push_str(self.visit_decl(node).as_ref());   }
        ast_str.push_str(" EOF! ");
        Cow::Owned(ast_str)
    }
    fn visit_decl(&self, d: &Decl) -> Cow<'a, str> {

            match d {
                Decl::Use(..) => Cow::Owned(format!("{:?}\n", &d)),
                Decl::Task(.., body)
                 | Decl::ExposedTask(.., body)
                 | Decl::Function(.., body)
                 | Decl::ExposedFn(.., body)
                 => {
                  let mut body_to_str = format!("{:?}\n", &d);
                  for stmt in body {
                      body_to_str.push_str("     ");
                      body_to_str.push_str(&format!("{:10}",self.visit_stmt(stmt).as_ref()));
                      body_to_str.push('\n');
                  }
                   Cow::from(body_to_str) 
                 }
                Decl::Program(prog) => {
                let mut body_to_str = format!("{:?}\n", &d);
                  for stmt in prog {
                      body_to_str.push_str("     ");
                      body_to_str.push_str(&format!("{:10}",self.visit_stmt(stmt).as_ref()));
                      body_to_str.push('\n');
                  }
                   body_to_str.push_str(" END ]\n");
                   Cow::from(body_to_str)
            } 
        }
    }

    fn visit_stmt(&self, s: &Stmt) -> Cow<'a, str> {
        match s { Stmt::Mut(..) | Stmt::Let(..) 
            | Stmt::Return(..)  
            | Stmt::ExprStatement(..) => Cow::from(format!("{:?}", &s)),
            Stmt::Block(li) => {
                let mut body_to_str = format!("{:?}\n", &s);
                  for stmt in li {
                      body_to_str.push_str("     ");
                      body_to_str.push_str(&format!("{:10}",self.visit_stmt(stmt).as_ref()));
                      body_to_str.push('\n');
                  }
                   Cow::from(body_to_str)
            },
            Stmt::IfElse(_, if_block, else_block) => {
                let mut body_to_str = format!("{:?}\n", &s);
                  for stmt in if_block {
                      body_to_str.push_str("     ");
                      body_to_str.push_str(&format!("{:10}",self.visit_stmt(stmt).as_ref()));
                      body_to_str.push('\n');
                  };
                match else_block {
                    Some(v) => {
                        for stmt in v {
                            body_to_str.push_str("     ");
                            body_to_str.push_str(&format!("{:10}",self.visit_stmt(stmt).as_ref()));
                        }

                    }
                    None => body_to_str.push_str("NO ELSE")

                };
                Cow::from(body_to_str)
                
            },
            Stmt::While(_, blok) => {
                let mut body_to_str = String::from(format!("{:?}\n", &s));
                  for stmt in blok{
                      body_to_str.push_str("     ");
                      body_to_str.push_str(self.visit_stmt(stmt).as_ref());
                      body_to_str.push('\n');
                  };
                Cow::from(body_to_str)
                }
           

        }
    }

    fn visit_expr(&self, _e: &Expr)-> Cow<'a, str>  { Cow::from(" ")}
    
}
