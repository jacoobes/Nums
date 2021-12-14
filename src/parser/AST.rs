use smol_str::SmolStr;


#[derive(Debug, Clone, PartialEq)]
pub enum Expr {
    Term(Vec<Expr>),
    Factor(Vec<Expr>),
    Unary(Box<Expr>),
    Power(Vec<Expr>),
    Group(Box<Expr>),
    Double(f32),
    Integer(i32),
    String(SmolStr),
    F64(f64),
    I64(i64),
    Bool(bool),
    Char(char)

}

impl Expr {
    pub fn traverse(&self) {
        match self {
            Expr::Term(children)
          | Expr::Factor(children) => {
            for node in children {
                node.traverse();
            }
          }
           Expr::Unary(expr)  => println!("{:?}", *expr),
           _ => println!("{:?}", &self)
        }
    }
}
