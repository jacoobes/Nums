use std::path::PathBuf;

#[derive(Debug)]
pub struct Source {
    source : String,
    path : PathBuf
}


impl Source {
    pub fn new (source:String, path: &'static str ) -> Self {
        Self {
            source,
            path: PathBuf::from(path)
        }
    }

    pub fn src (&self) -> String {
        self.source
    }
    pub fn path(&self) -> PathBuf {
        self.path
    }
}
