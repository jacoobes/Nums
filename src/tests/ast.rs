#[cfg(test)]
mod ast {
    use std::rc::Rc;
    use crate::frontend::{tokens::Token, parser::parse, source::Source, ast::AST};
    use codespan_reporting::diagnostic::Diagnostic;
    use logos::Logos;

    fn create_tree<'a>(text: &'a str) -> Result<AST, Vec<Diagnostic<()>>> {
        let iterator = Token::lexer(text);
        let mut parser = parse::Parser::new(iterator, Rc::new(Source::new(String::from(text), "text")));
        parser.parse()
    }

    #[test]
    fn simple_exprs() {
      let text = " '\n' 'r' 123213.2321312 420%  ();";
      let tree= create_tree(text);
      match tree {
        Ok(e) => println!("{:?}", &e),
        Err(e) => return println!("{:?}", &e)
        }
    }
    #[test]
    fn grouping() {
        let text = r#"unit a = ((()));"#;
        let tree= create_tree(text);
        match &tree {
            Ok(e) => println!("{:?}", &e),
            Err(e) => println!("{:?}", &e)
        }
        
    }
    #[test]
    fn unary() {
        let text = "!true !false +5 -5;";
        let tree= create_tree(text);
        
        println!("{:?}", &tree)

    }
    #[test]
    fn power() {
        let text = "5 ^ 10 + 16 + 3;";
        let tree= create_tree(text);
        match tree {
            Ok(e) => println!("{:?}", &e),
            Err(span) => println!("{:?}", span)
        }
    }
    #[test]
    fn factor() {
        let text = "10 + 1 / 10;";
        let tree = create_tree(text);
        match tree {
            Ok(e) => println!("{:?}", &e),
            Err(span) => println!("{:?}", span)
        }
    }
    #[test]
    fn compare() {
        let text = "1 != 1;";
        let tree = create_tree(text);
        match tree {
            Ok(e) => println!("{:?}", &e),
            Err(span) => println!("{:?}", &span)
        }
    }
    #[test]
    fn  equality() {
        let text = "(1 <= 1) + (1 == 5);";
        let tree = create_tree(text);
        match tree {
            Ok(e) => println!("{:?}", &e),
            Err(span) => println!("{:?}", &span)
        }
    }
    #[test]
    fn stmt_expr() {
        let text = " 1 + 1;";
        let tree = create_tree(text);
        match tree {
            Ok(e) => println!("{:?}", &e),
            Err(span) => println!("{:?}", &span)
        }
    }
    #[test]
    fn var_decl() {
        let text = " *_ a = 1 + 1;";
        let _text2 = "str a =\" 13221 \" ";
        let tree = create_tree(text);
        match tree {
            Ok(e) => println!("{:?}", &e),
            Err(span) => println!("{:?}", &span)
        }
    }
    #[test]
    fn while_loop() {
        let text = " while 1 < 10 { 
            ~~ a random comment
            1 + 1;
         }";

         let tree = create_tree(text);
         match tree {
            Ok(e) => println!("{:?}", &e),
            Err(span) => println!("{:?}", &span)
        }
    }
    #[test]
    fn if_else() {
        let text = " if 1 == 1 {
            1 + 1;
        } ";
        let tree = create_tree(text);
        match tree {
           Ok(e) => println!("{:?}", &e),
           Err(span) => println!("{:?}", &span)
       }
    }
    #[test]
    fn function() {
        let text = " 
        ~~ commas are explicit, cannot add trailing commas to args
        fun hello_world | abc: str, adc: a | = int {
            1 + 1;
        }
        
        ";
        let tree = create_tree(text);
        match tree {
           Ok(e) => println!("{:?}", &e),
           Err(span) => println!("{:?}", &span)
       } 
    }

}
