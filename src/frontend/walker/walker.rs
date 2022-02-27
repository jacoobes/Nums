use crate::frontend::nodes::{ decl::Decl, expr::Expr, stmt::Stmt} ;
use crate::frontend::ast::AST;
pub trait Walker<T> {
    fn walk(&self) -> T; 
    fn visit_decl(&self, d : &Decl) -> T;
    fn visit_stmt(&self, s : &Stmt) -> T;
    fn visit_expr(&self, e : &Expr) -> T;

}

impl Walker<()> for AST {
    
    fn walk(&self) {
        for node in &self.0 {  self.visit_decl(node)   }
        println!(" EOF! ");
    }
    fn visit_decl(&self, d: &Decl) {

            match d {
                Decl::Use(..) => println!("{:?}", &d),
                Decl::Task(.., body)
                 | Decl::ExposedTask(.., body)
                 | Decl::Function(.., body)
                 | Decl::ExposedFn(.., body)
                 => {
                  println!("{:?}", &d);
                  for stmt in body {
                      print!("     "); 
                      self.visit_stmt(stmt)
                  }
                    
                 }
                Decl::Program(prog) => {
                  println!("{:?}", &d);
                  for stmt in prog {
                      print!("     "); 
                      self.visit_stmt(stmt)
                  }
                  println!(" END ]")
            } 
        }
    }

    fn visit_stmt(&self, s: &Stmt) {
        match s {
            Stmt::Mut(..) 
            | Stmt::Let(..) 
            | Stmt::Return(..)  
            | Stmt::ExprStatement(..) => println!("{:?}", &s),
            Stmt::Block(li) => {
                println!("{:?}", &s);
                for stmt in li {
                    print!("     ");
                    self.visit_stmt(stmt)
                }
            },
            Stmt::IfElse(_, if_block, else_block) => {
                println!("{:?}", &s);
                for stmt in if_block {
                    print!("     ");
                    self.visit_stmt(stmt)
                }
                if else_block.is_some() {
                    for stmt in if_block {
                        print!("     ");
                        self.visit_stmt(stmt)
                    }    
                } else {
                    println!(" NO ELSE ");
                }
                
            },
            Stmt::While(_, blok) => {
                println!("{:?}", &s);
                for stmt in blok {
                    print!("     ");
                    self.visit_stmt(stmt)
                } 
            }

        }
    }

    fn visit_expr(&self, _e: &Expr)  {}
    
}
