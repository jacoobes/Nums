#[cfg(test)]
mod ast {
    use std::vec;
    use logos::Logos;
    use crate::{parser::{parse, ast::Expr}, token::Token};

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
    #[test]
    fn simple_exprs() {
      let text = " '\n' 'r' 123213.2321312 420 %  ";
      let iterator = Token::lexer(text);
      let mut parser = parse::Parser::new(iterator);
        
      match parser.parse() {
        Ok(e) => e.traverse(),
        Err(e) => return println!("{}", &e)
    }
    }
}
