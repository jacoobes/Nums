pub mod parse;
pub mod peekable_parser;
pub use smol_str::SmolStr;

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
macro_rules! create_binexpr {
    (&mut $self:ident, $token:expr, $left:expr, $right:expr ) => {
        $self.resolve_node($crate::compiler::nodes::expr::Expr::Binary {
            operator: $token,
            left: Box::new($left?),
            right: Box::new($right?),
        })
    };
}
