


pub type TypeId = usize;

pub enum Type {
    Never,
    Unit,   
    Char,   
    String, 
    Int,    
    Float,  
    Bool,
    Func(Box<Type>, Box<Type>),
    Record( Box<Type>, Vec<Box<Type>> )
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum TypeInfo {
    Unknown,
    Unit,   
    Char,   
    String, 
    Int,    
    Float,  
    Bool,
    Ref(TypeId),
    Record ( TypeId, Vec<Box<TypeId>> ),
    Func(TypeId, TypeId),
}

pub struct Engine {
    vars: Vec<TypeInfo>
}

impl Engine {
    pub fn insert(&mut self, info: TypeInfo) -> TypeId {
        let id = self.vars.len();
        self.vars.push(info);
        id
    }
    pub fn unify(&mut self, a : TypeId, b: TypeId) -> Result<(), String> {
        use TypeInfo::*;
        match (self.vars[a].clone(), self.vars[b].clone()) {
            // Follow any references
            (Ref(a), _) => self.unify(a, b),
            (_, Ref(b)) => self.unify(a, b),
            
            // When we don't know anything about either term, assume that
            // they match and make the one we know nothing about reference the
            // one we may know something about
            (Unknown, _) => { self.vars[a] = TypeInfo::Ref(b); Ok(()) },
            (_, Unknown) => { self.vars[b] = TypeInfo::Ref(a); Ok(()) },
            
            // Primitives are trivial to unify
            (Bool, Bool) 
            | (String, String)
            | (Char, Char)
            | (Float, Float)
            | (Unit, Unit)
            | (Int, Int) => Ok(()),
            // When unifying complex types, we must check their sub-types. This
            // can be trivially implemented for tuples, sum types, etc.
            (Func(a_i, a_o), Func(b_i, b_o)) => self.unify(a_i, b_i)
                .and_then(|_| self.unify(a_o, b_o)),
            
            // If no previous attempts to unify were successful, raise an error
            (a, b) => Err(format!("Type mismatch between {:?} and {:?}", a, b)),
        }
    }
    pub fn reconstruct(&self, id: TypeId) -> Result<Type, String> {
        use TypeInfo::*;
        match &self.vars[id] {
            Unknown => Err(format!("Cannot infer")),
            Ref(id) => self.reconstruct(*id),
            Float  => Ok(Type::Float),
            Bool => Ok(Type::Bool),
            Int => Ok(Type::Int),
            String => Ok(Type::String),
            Unit => Ok(Type::Unit),
            Char => Ok(Type::Char),
            Func(i, o) => Ok(Type::Func(
                Box::new(self.reconstruct(*i)?),
                Box::new(self.reconstruct(*o)?),
            )),
            _ => Err(format!("Cannot infer")),
        }
    }
}