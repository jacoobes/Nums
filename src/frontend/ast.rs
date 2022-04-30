use std::ops::Deref;

use super::nodes::decl::Decl;

pub struct AST(pub Vec<Decl>);


impl AST {
    pub fn walk(&self) {

    }
}

impl Deref for AST {
    type Target = Vec<Decl>;

    fn deref(&self) -> &Self::Target {
        &self.0
    }
}

impl std::fmt::Debug for AST {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
       write!(f,"{:?}", self.0)
    }
}

