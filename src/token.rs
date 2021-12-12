use logos::{Logos, Lexer, Skip};
use smol_str::SmolStr;

///
/// Regular grammar for Nums
/// 
#[derive(Logos, Debug, PartialEq)]
pub enum Token {

    #[token("when")]
    When,
    #[token("to")]
    To,
    #[token("while")]
    While,
    #[token("and")] 
    And,
    #[token("or")]
    Or,
    #[token("fn")]
    Function,
    #[token("by")]
    By,
    #[token("none")]
    NoneOf,
    #[token("of")]
    Of,
    #[token("get")]
    Get,
    #[token("from")]
    From,
    #[token("contain")]
    Container,
    #[token("expose")]
    Expose,
    #[token("<")]
    LeftArr,
    #[token(">")]
    RightArr,

    /// double must have a number before the '.' faulty: .12123, correct: 0.12123
    /// can also come in form of a percent!
    #[regex(r"\d+(?:\.\d+)+", parse_dub)]
    #[regex(r"\d+(?:\.\d+)?%", percent_float)] 
    Double(f32),
    /// standard 4 byte numbers 
    #[regex(r"\d+", parse_num)]
    Integer(i32),
    ///only ascii!
    ///smolstr is heap allocated if 23 bytes + ofc
    #[regex(r#""([^"\\]|\\t|\\u|\\n|\\")*""#, make_str)]
    String(SmolStr),

    #[regex(r"[a-zA-Z_]+\d?", priority = 2)]
    Identifier,

    /// token types and expr to denote the conversion of one type to another.
    ///  can be used as expression to convert or type declaration
    #[token("f64")]
    F64,
    #[token("f32")]
    F32,
    #[token("i64")]
    I64,
    #[token("i32")]
    I32,
    #[token("str")]
    Str,
    #[token("boolean")]
    Boolean,

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
    #[error]
    //multiline comments :> (anything) <:
    #[regex(r":>[^<]*(?:[^<:]*)<:", logos::skip)]
    //single line comments
    #[regex(r"~[^\n\r]*\n", logos::skip)]
    //skip
    #[regex(r"[ \t\n\f]+", logos::skip)]
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
