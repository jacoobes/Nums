#[cfg(test)]
mod lexer {


    use crate::token::Token;
    use logos::Logos;
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
        let f1 = "12341221";
        let mut tokens = Token::lexer(f1);
        assert_eq!(Some(Integer(12341221)), tokens.next())
    }

}