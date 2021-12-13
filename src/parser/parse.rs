use logos::Lexer;

use crate::token::Token;

struct Parser<'a> {
    tokens : Lexer<'a, Token>
}


///
/// Basic recursive descent parsing
/// 
impl <'a> Parser <'a> {
    fn new (tokens: Lexer<'a, Token>) -> Self {
        Self { tokens }
    }

    fn expr()  {

    }

    fn term () {

    }

    fn factor () {

    }

    fn power () {

    }

    fn unary () {

    }

    fn primary () {

    }

}