use logos::{Logos, Lexer};
use smol_str::SmolStr;


#[derive(Default)]
pub struct Source {
    pub line_breaks: usize,
}

/// Regular grammar for Nums 
#[derive(Logos, Debug, PartialEq, Clone)]
#[logos(extras = Source)]
#[logos(subpattern typ = r"float|int|str|boolean|unit")]
#[logos(subpattern int = r"\d+")]

pub enum Token {

    #[token("when")]
    When,
    #[token("to")]
    To,
    #[token("if")]
    If,
    #[token("else")]
    Else, 
    #[token("while")]
    While,
    #[token("and")] 
    And,
    #[token("or")]
    Or,
    #[token("fun")]
    Function,
    #[token("by")]
    By,
    #[token("let")]
    Let,
    #[token("none")]
    NoneOf,
    #[token("of")]
    Of,
    #[token("get")]
    Get,
    #[token("from")]
    From,
    #[token("package")]
    Package,
    #[token("expose")]
    Expose,
    #[token("->")]
    SmallPointer,
    
    #[token("<")]
    LeftArr,
    #[token(">")]
    RightArr,
    #[token("/")]
    FowardSlash,

    /// double must have a number before the '.' faulty: .12123, correct: 0.12123
    /// can also come in form of a percent!
    #[regex(r"\d+(?:\.\d+)+", parse_dub)]
    #[regex(r"\d+(?:\.\d+)?%", percent_float)] 
    Double(f32),
    /// standard 4 byte numbers 
    #[regex(r"(?&int)", parse_num)]
    Integer(i32),
    ///only ascii!
    ///smolstr is heap allocated if 23 bytes + ofc
    #[regex(r#""([^"\\]|\\t|\\u|\\n|\\")*""#, make_str)]
    String(SmolStr),

    #[regex(r"'[a-zA-Z0-9\n\r\t \f]'", parse_char)]
    Char(char),
    #[regex(r"true|false", parse_bool)]
    Bool(bool),
    #[token("()", priority = 3)]
    Unit,

    #[regex(r"[a-zA-Z][_0-9a-zA-Z]*", priority = 2,  callback = |lex| SmolStr::from(lex.slice()))]
    Identifier(SmolStr),

    /// token types and expr to denote the conversion of one type to another.
    ///  can be used as expression to convert or type declaration
    // #[token("f64")]
    // F64,
    #[regex(r"(?&typ)", priority = 3, callback = |lex| SmolStr::from(lex.slice()))]
    Type(SmolStr),
    // #[token("i64")]
    // I64,
    #[token(",")]
    Comma,
    #[token(".")]
    Period,
    #[token("!")]
    Bang,
    #[token("+")]
    Plus,
    #[token("-")]
    Minus,
    #[token("?")]
    Question,
    #[token(";")]
    Semi,
    #[token("..")]
    Elipsis,
    #[token("(")]
    LeftParen,
    #[token(")")]
    RightParen,
    #[token("|")]
    Bar,
    #[token(":")]
    Colon,
    #[token("*")]
    Star,
    #[token("^")]
    Caret,
    #[token("[")]
    LeftBrack,
    #[token("]")]
    RightBrack,
    #[token("{")]
    LeftBrace,
    #[token("}")]
    RightBrace,
    #[token("==")]
    Eq,
    #[token("!=")]
    NotEq,
    #[token("=")]
    Assign,
    #[token(">=")]
    GreaterEq,
    #[token("<=")]
    LessEq,
    //multiline comments :> (anything) <:
    #[regex(r":>[^<]*(?:[^<:]*)<:", logos::skip)]
    //single line comments
    #[regex(r"~~[^\n]*\n", logos::skip)]
    //skip
    #[token("\n", |lex| lex.extras.line_breaks += 1; logos::Skip)]
    #[regex(r"[ \t\f\r]", logos::skip)]
    #[error]
    Error
}

fn parse_num(lex: &mut Lexer<Token>) -> Option<i32> {
    let slice = lex.slice();
    let n : i32 = slice.parse().ok()?;
    Some(n)
}

fn parse_dub(lex: &mut Lexer<Token>) -> Option<f32> {
    let slice = lex.slice();
    let n : f32 = slice.parse().ok()?;
    Some(n)
}

fn make_str(lex: &mut Lexer<Token>) -> Option<SmolStr> {
    let slice = lex.slice();
    Some(SmolStr::new(&slice[1..slice.len()-1]))
}

fn percent_float(lex: &mut Lexer<Token>) -> Option<f32> {
    let slice = lex.slice();
    let n: f32 = slice[..slice.len() - 1].parse().ok()?;
    Some( n / 100.0 )
}

fn parse_char(lex: &mut Lexer<Token>) -> Option<char> {
    let slice = lex.slice().chars().nth(1);
    slice
}
fn parse_bool(lex :  &mut Lexer<Token>) -> Option<bool> {
    let slice = lex.slice();
    slice.parse::<bool>().ok()
}