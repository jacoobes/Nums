use logos::{Logos, Source, Lexer};
use smol_str::SmolStr;


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
    None,
    #[token("of")]
    Of,
    #[token("get")]
    Get,
    #[token("from")]
    From,
    #[token("contain")]
    Container,
    #[token("pub")]
    Public,
    
    #[token("<")]
    LeftArr,
    #[token(">")]
    RightArr,
    ///default values, written directly in bytecode (smolstr is heap allocated if 23 bytes + ofc)
    /// double must have a number before the '.' faulty: .12123, correct: 0.12123 
    #[regex(r"\d+\.\d+", parse_dub)]
    Double(f32),
    #[regex(r"[0-9]+", parse_num)]
    Integer(i32),
    //only ascii!
    #[regex("\"[[:ascii:]]+\"", make_str)]
    String(SmolStr),

    #[regex("[[:word:]]", priority = 2)]
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
    //multiline comments :>(anything) <:
    #[regex(r":>[^*]*\*+(?:[^/*][^*]*\*+)<:")]
    //single line comments
    #[regex(r"~[.^\t^\f]*(\n|~)", logos::skip)]
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
    let slice = lex.slice().trim_matches('"');
    Some(SmolStr::new(slice))
}