use ansi_term::Colour::*;
pub enum Faults {
    Error(ErrTyp),
    Warn(WarnTyp)
}

#[derive(Debug)]
pub enum ErrTyp {
    UnknownToken,
    UnexpectedEndOfParsing,
    UnexpectedToken,
    ExpectedClosingParen
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