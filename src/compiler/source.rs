use std::path::PathBuf;
use codespan_reporting::diagnostic::{Diagnostic, Label};
use codespan_reporting::files::{SimpleFiles, SimpleFile};
use codespan_reporting::term::termcolor::{ColorChoice, StandardStream};

#[derive(Debug)]
pub struct Source {
    pub source : String,
    pub path : PathBuf,
    pub simple_file: SimpleFile<String, String>
}


impl Source {
    pub fn new (source: String, path: &'static str ) -> Self {
        let simple_file = SimpleFile::new(path.to_string(), source.clone());
        Self {
            source,
            path: PathBuf::from(path),
            simple_file
        }
    }

    pub fn create_diagnostic(&self, message: String, location_of_err: std::ops::Range<usize>, notes: String) -> Diagnostic<()> {
        Diagnostic::error()
        .with_message(message)
        .with_labels(vec![
            Label::primary((), location_of_err)


        ]).with_notes(vec![notes])
    }
}
