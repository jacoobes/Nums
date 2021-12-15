use ansi_term::Colour::*;
use smol_str::SmolStr;
use crate::Token;
pub enum Faults {
    Error(ErrTyp),
    Warn(WarnTyp)
}

pub enum ErrTyp {
    UnknownToken(SmolStr),
    UnexpectedEndOfParsing,
    UnexpectedToken(Token),
    ExpectedClosingParen,
    Expected(Token, Token)
}

#[derive(Debug)]
pub enum WarnTyp {

}

impl std::fmt::Debug for Faults {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::Error(arg) => write!(f,"{} : {:?}", Red.bold().paint("Error"), &arg ),
            Self::Warn(arg) => write!(f,"{} : {:?}", Yellow.bold().paint("Warning"), &arg ),
        }
    }
}

impl std::fmt::Debug for ErrTyp {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::UnknownToken(smol) => write!(f, "UnknownToken : {}", &smol),
            Self::UnexpectedEndOfParsing => write!(f, "UnexpectedEndOfParsing"),
            Self::UnexpectedToken(arg0) => write!(f, "UnexpectedToken : {:?}", arg0),
            Self::ExpectedClosingParen => write!(f, "ExpectedClosingParen"),
            Self::Expected(tok, other) => write!(f, "Expected {:?} got {:?}", tok, other )
        }
    }
}