use std::borrow::Cow;
use nums_vm::bytecode::chunk::Chunk;
use peekmore::{PeekMore, PeekMoreIterator};
use nums_vm::bytecode::data::{Data, Type};
use nums_vm::bytecode::op_code::OpCode::{self, *};

use logos::Lexer;
use crate::token::Token;
struct Parser<'a> {
    tokens : PeekMoreIterator<logos::Lexer<'a, Token>>,
    chunk : Chunk
}

///chunk
impl <'a> Parser<'a> {

    pub fn add_const(&mut self, data : Data) {
       let loc_of_const =  self.chunk.add_const( data);
       self.chunk.write(&LOAD_CONST);
       self.chunk.write_const(loc_of_const)
    }
    
    pub fn emit_op(chunk: &mut Chunk, code: &OpCode) {
        chunk.write(&code);
    }
}

///iterator methods
impl <'a> Parser <'a> {
    /// advances, expecting to there to be a token
    fn expect_token (&mut self, token: &Token) -> Result<Token, Cow<'a, str>> {
        match self.tokens.next() {
           Some(tok) if &tok == token => Ok(tok),
           Some(tok) => Err(Cow::Owned(format!("Got {:?} token when expecting {:?}", &tok, token))),
           None => Err(Cow::Owned(format!("Parsed until end of token stream. Expected {:?}", token))) 
        }
    }

    fn next (&mut self) -> Result<Token, Cow<'a, str>> {
           self.tokens.next().ok_or(Cow::Borrowed("Unexpected end of parsing"))
    }


    fn check_peek (&mut self, token: &Token) -> bool {
        match self.tokens.peek() {
            Some(tok) => tok == token,
            None => false
        }
    }
    
    fn check_peek_next (&mut self, token: &Token) -> bool {
        match self.tokens.peek_next() {
            Some(t) =>  t == token,
            None => false,
        }
    }

    fn match_advance (&mut self, token: &Token )  {
        match self.tokens.peek() {
            Some(_) => {
                if self.check_peek(token) {
                    self.next().unwrap();
                }
            },
            None => ()

        }
    }
}


/// Basic recursive descent parsing
impl <'a> Parser <'a> {
    fn new (tokens: Lexer<'a, Token>) -> Self {
        Self { tokens: tokens.peekmore(), chunk: Chunk::new() }
    }

    fn parse(&mut self, chunk : Chunk) -> Result<Chunk, Cow<'a, str>> {
        loop {
            self.expr()?;
        }

    }
    fn expr (&mut self)-> Result<Chunk, Cow<'a, str>>  {
        self.term()
    }

    fn term (&mut self) -> Result<Chunk, Cow<'a, str>> {
       self.factor() 
    }

    fn factor (&mut self)-> Result<Chunk, Cow<'a, str>> {
        self.power()
    }

    fn power (&mut self)-> Result<Chunk, Cow<'a, str>> {
        self.unary()
    }

    fn unary (&mut self)-> Result<Chunk, Cow<'a, str>> {
        self.primary()
    }

    fn primary (&mut self) -> Result<Chunk, Cow<'a, str>> {
        Err(Cow::Borrowed("a"))
    }
    

}

