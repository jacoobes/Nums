#[cfg(test)]
mod ast {
    use std::vec;

    use crate::parser::ast::Expr;

    #[test]
    fn traverse () {
       let new_expr = Expr::Term( 
           vec![ 
               Expr::Double(1.2),
               Expr::Term( 
                   vec![
                       Expr::Double(10f32),
                       Expr::Double(23213f32)
                   ]
               )
           ]
       );
       new_expr.traverse(); 
    }
}