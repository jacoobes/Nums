
use crate::Token;
use smol_str::SmolStr;
pub enum Faults {
    Error(ErrTyp),
    Warn(WarnTyp),
}

pub enum ErrTyp {
    UnknownToken(SmolStr),
    UnexpectedEndOfParsing,
    UnexpectedToken(Token),
    ExpectedClosingParen,
    Expected(Token, Token),
    NoTopLevelDeclaration,
    UnknownType(Token),
}

#[derive(Debug)]
pub enum WarnTyp {}

impl std::fmt::Debug for Faults {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::Error(arg) => write!(f, "{} : {:?}","Error", &arg),
            Self::Warn(arg) => write!(f, "{} : {:?}", "Warning", &arg),
        }
    }
}

impl std::fmt::Debug for ErrTyp {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::UnknownToken(smol) => write!(f, "UnknownToken : {}", &smol),
            Self::UnexpectedEndOfParsing => write!(f, "UnexpectedEndOfParsing: Reached the end of token stream without completing a valid syntax tree"),
            Self::UnexpectedToken(arg0) => write!(f, "UnexpectedToken : {:?}", arg0),
            Self::ExpectedClosingParen => write!(f, "ExpectedClosingParen"),
            Self::Expected(tok, other) => write!(f, "Expected {:?} got {:?}", tok, other),
            Self::NoTopLevelDeclaration => write!(f, "NoTopLevelDeclaration : In this file, expected a function or module declaration, found none"),
            Self::UnknownType(token) => write!(f, "Unknown type. The compiler couldn't resolve {:?} as a type ", token),
        }
    }
}
