#[cfg(test)]
mod ast {
    use logos::Logos;
    use crate::{parser::{parse, ast::Expr}, token::Token};
    use std::borrow::Cow;

    fn create_tree<'a>(text: &'a str) -> Result<Vec<Expr>, Cow<'a, str>> {
        let iterator = Token::lexer(text);
        let mut parser = parse::Parser::new(iterator);
        parser.parse()
    }

    #[test]
    fn simple_exprs() {
      let text = " '\n' 'r' 123213.2321312 420%  ";
      let tree= create_tree(text);
      match tree {
        Ok(e) => println!("{:?}", &e),
        Err(e) => return println!("{}", &e)
        }
    }
    #[test]
    fn grouping() {
        let text = r#"(((1)))"#;
        let tree= create_tree(text);
        match &tree {
            Ok(e) => println!("{:?}", &e),
            Err(_) => ()
        }
        println!("{:?}", &tree);
        
    }
    #[test]
    fn unary() {
        let text = "!true !false +5 -5";
        let tree= create_tree(text);
        
        println!("{:?}", &tree)

    }
    #[test]
    fn power() {
        let text = " 5 ^ 10    16 ^ 12 ^ 1";
        let tree= create_tree(text);
        println!("{:?}", &tree)
    }
    #[test]
    fn factor() {
        let text = "10 + 1 / 5";
        let tree = create_tree(text);
        println!("{:?}", &tree)
    }

}
