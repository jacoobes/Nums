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
