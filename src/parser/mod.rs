pub mod parse;
pub mod ast;
pub mod peekable_lexer;

#[macro_export]
macro_rules! match_adv {
    (&mut $self: ident,  $(|)? $( $pattern:pat_param )|+) => {
        match $self.peek() {
            $(Some( $pattern ))|+ => Some($self.next().unwrap()),
            _ => None
        }
    };
}

#[macro_export]
macro_rules! lexer_span {
    (&mut $self:ident, $err_type:ident, $file_name:expr) => {
        $crate::error_handling::span::Span::new( 
            String::from($file_name),
            $self.tokens.slice().to_string(),
            $self.tokens.cur_line(),
        Faults::Error($err_type))
    };
}