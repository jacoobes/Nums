#[cfg(test)]
mod lexer {
    use crate::token::Token;
    use logos::Logos;
    use smol_str::SmolStr;
    use crate::tests::lexer::lexer::Token::*;

    #[test]
    fn test_single_keyword () {
        let string = "!:<>*^@()+-~";
        let lexer = Token::lexer(string);

        let stream_of_toks = lexer.collect::<Vec<_>>();

        assert_eq!(
            vec![Bang, Colon, LeftArr, RightArr, Star, Caret, Error, LeftParen, RightParen, Plus, Minus,Error],
            stream_of_toks)

    }
    #[test]
    fn lex_float() {
        let f1 = "0.231232";
        let mut tokens = Token::lexer(f1);
        assert_eq!(Some(Double(0.231232)), tokens.next())
    }
    #[test]
    fn lex_int() {
        let f1 = "8 + 10";
        let mut tokens = Token::lexer(f1);
        assert_eq!(Some(Integer(8)), tokens.next());
        assert_eq!(Some(Plus), tokens.next());
        assert_eq!(Some(Integer(10)), tokens.next())
    }
    #[test]
    fn percent_float() {
        let f1 = "8%";
        let mut tokens = Token::lexer(f1);
        assert_eq!(Some(Double(0.08)), tokens.next() )
    }
    #[test]
    fn float_vs_float_percent() {
        let text = "8% * 8";
        let mut tokens = Token::lexer(text);
        assert_eq!(Some(Double(0.08)), tokens.next() );
        assert_eq!(Some(Star), tokens.next());
        assert_eq!(Some(Integer(8)), tokens.next());
    }
    #[test]
    fn string() {
        let text = "\"a string\" \"\" \"i ate noodles noodles\nwere\nsexy\nmy\ngirlfriend\nis\na\nnoob\"";
        let mut tokens = Token::lexer(text);
        assert_eq!(Some(String(SmolStr::new("a string"))), tokens.next());
        assert_eq!(Some(String(SmolStr::new(""))), tokens.next());
        assert_eq!(Some(String(SmolStr::new("i ate noodles noodles\nwere\nsexy\nmy\ngirlfriend\nis\na\nnoob"))), tokens.next());
    }
    #[test]
    fn keywords() {
        let text = "expose fn when by by try none and or while cont contain f64 f32 i32 i64 boolean str to";
        let mut tokens = Token::lexer(text);
        assert_eq!(Some(Expose), tokens.next());
        assert_eq!(Some(Function), tokens.next());
        assert_eq!(Some(When), tokens.next());
        assert_eq!(Some(By), tokens.next());
        assert_eq!(Some(By), tokens.next());
        assert_eq!(Some(Identifier), tokens.next());
        assert_eq!(Some(NoneOf), tokens.next());
        assert_eq!(Some(And), tokens.next());
        assert_eq!(Some(Or), tokens.next());
        assert_eq!(Some(While), tokens.next());
        assert_eq!(Some(Identifier), tokens.next());
        assert_eq!(Some(Container), tokens.next());
        assert_eq!(Some(F64), tokens.next());
        assert_eq!(Some(F32), tokens.next());
        assert_eq!(Some(I32), tokens.next());
        assert_eq!(Some(I64), tokens.next());
        assert_eq!(Some(Boolean), tokens.next());
        assert_eq!(Some(Str), tokens.next());
        assert_eq!(Some(To), tokens.next());
    }

    #[test]
    fn single_ln_comments() {
        let text = "~ a single line comment";
        let token = Token::lexer(text);
        assert_eq!(token.slice(), "")
    }
    #[test]
    fn multiln_comments() {
        let text = ":>  
        once upon a time i pooped.
        The pooop was veryyyyy big!
        I ate a LOT of it.
        the end
        <:";
        let token = Token::lexer(text);
        assert_eq!("", token.slice())
    }
    #[test]
    fn pseudo_code() {
        let text = " 
        contain;
            
            expose fn hello_world | foo, bar | (\" asdf \") : str;

            expose anon_fn = | c, e ,f  | (  c  ) : f64

        ";

        let tokens = Token::lexer(text);
        for token in tokens {
            println!("{:?}", &token)
        }
    }

}