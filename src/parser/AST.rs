use smol_str::SmolStr;


#[derive(Debug, Clone)]
pub enum GrammarItem {
    Term,
    Factor,
    Unary,
    Double(f32),
    Integer(i32),
    String(SmolStr),
    F64(f64),
    I64(i64),
    Bool(bool),
    Char(char)

}

pub struct Node {
    value: GrammarItem,
    children: Vec<GrammarItem>
}

impl Node {
    pub fn new(value: GrammarItem) -> Self {
        Self {
            value,
            children : Vec::new()
        }
    }   

}
