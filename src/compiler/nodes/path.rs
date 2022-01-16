use smol_str::SmolStr;

#[derive(Debug, Clone, PartialEq)]
pub struct PackagePath {
    path: Vec<Path>,
}

impl PackagePath {
    pub fn join(&mut self, path: Path) {
        self.path.push(path);
    }

    pub fn to_string(&self) -> String {
        self.path
            .iter()
            .map(|s| s.get_name())
            .collect::<Vec<&str>>()
            .join(":")
    }

    pub fn path(&self) -> &Vec<Path> {
        &self.path
    }
}

impl From<Path> for PackagePath {
    fn from(p: Path) -> Self {
        PackagePath { path: vec![p] }
    }
}

impl From<Vec<Path>> for PackagePath {
    fn from(it: Vec<Path>) -> Self {
        PackagePath { path: it }
    }
}

#[derive(Debug, Clone, PartialEq)]
pub enum Path {
    Ident(smol_str::SmolStr),
    All,
}

impl Path {
    pub fn is_ident(&self) -> Option<smol_str::SmolStr> {
        match self {
            Path::Ident(item) => Some(item.clone()),
            Path::All => None,
        }
    }

    pub fn get_name(&self) -> &str {
        match self {
            Path::Ident(s) => s,
            Path::All => "ALL",
        }
    }
}

impl From<&SmolStr> for Path {
    fn from(string: &SmolStr) -> Self {
        match string {
            tilde if tilde == "~" => Path::All,
            other => Path::Ident(other.clone()),
        }
    }
}
