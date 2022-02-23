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
macro_rules! create_expr {
    (&mut $self:ident, binary:  $token:expr, $left:expr, $right:expr) => {
        $self.resolve_node($crate::frontend::nodes::expr::Expr::Binary {
            operator: $token,
            left: Box::new($left?),
            right: Box::new($right?),
        })
    };
    (&mut $self:ident, logical: $token:expr, $left:expr, $right:expr) => {
        $self.resolve_node($crate::frontend::nodes::expr::Expr::Logical {
            operator: $token,
            left: Box::new($left?),
            right: Box::new($right?),
        })
    };

    (&mut $self:ident, unary: $token:expr, $operand:expr) => {
        $self.resolve_node($crate::frontend::nodes::expr::Expr::Unary {
            operator: $token,
            expr : Box::new($operand?)
        })
    };
}
