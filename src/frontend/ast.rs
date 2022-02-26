use super::nodes::decl::Decl;

pub struct AST(pub Vec<Decl>);

impl std::fmt::Debug for AST {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
       write!(f,"{:?}", self.0)
    }
} 
