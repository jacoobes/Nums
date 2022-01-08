
use std::fmt::write;

use crate::compiler::{tokens::Token, nodes::expr::Expr};
use smol_str::SmolStr;
pub enum Faults {
    Error(ErrTyp),
}

pub enum ErrTyp {
    UnknownToken(SmolStr),
    UnexpectedEndOfParsing,
    UnexpectedToken(Token),
    Expected(Token, Token),
    NoTopLevelDeclaration,
    UnknownType(Token),
    UnclosedDelimiter,
    InvalidAssignmentTarget(Expr),
    FileNotAModule,
    DeclarationAlreadyFound(SmolStr)
}

#[derive(Debug)]
pub enum WarnTyp {}

impl std::fmt::Debug for Faults {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::Error(arg) => write!(f, "{} : {:?}","Error", &arg),
        }
    }
}

impl std::fmt::Debug for ErrTyp {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::UnknownToken(smol) => write!(f, "Unknown Token : {}", &smol),
            Self::UnexpectedEndOfParsing => write!(f, "UnexpectedEndOfParsing: Reached the end of token stream without completing a valid syntax tree"),
            Self::UnexpectedToken(arg0) => write!(f, "Unexpected Token : {:?}", arg0),
            Self::Expected(tok, other) => write!(f, "Expected {:?} got {:?}", tok, other),
            Self::NoTopLevelDeclaration => write!(f, "NoTopLevelDeclaration : In this file, expected a function or module declaration, found none"),
            Self::UnknownType(token) => write!(f, "Unknown type. The compiler couldn't resolve {:?} as a type ", token),
            Self::UnclosedDelimiter => write!(f, "Unclosed delimiter"),
            Self::InvalidAssignmentTarget(e) => write!(f, "{:?} is not a valid assignment target! ", &e ),
            Self::FileNotAModule => write!(f, "File is not included in module"),
            Self::DeclarationAlreadyFound(n) => write!(f, "Function or Record has been previously registered under the same name: `{}`", n )
        }
    }
}
