pub mod nums_compiler;
pub mod parser;
pub mod source;
pub mod tokens;
pub mod nodes;
pub mod visitor;
pub mod types;

#[macro_export]
macro_rules! fnv_map {
    ($($key:expr => $value:expr),*) => 
    {

        let map = ::fnv::FnvHashMap::default();

        $(
            _map.insert($key, $value); 
        )*;
        _map
    };
}