
pub enum Faults {
    Error(ErrTyp),
    Warn(WarnTyp)
}


pub enum ErrTyp {
    UnknownToken,
    UnexpectedEndOfParsing
}


pub enum WarnTyp {

}

