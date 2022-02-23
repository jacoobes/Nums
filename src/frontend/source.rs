use codespan_reporting::diagnostic::{Diagnostic, Label};
use codespan_reporting::files::SimpleFile;

#[derive(Debug)]
pub struct Source {
    pub source: String,
    pub simple_file: SimpleFile<String, String>,
}

impl Source {
    pub fn new(source: String, path: &str) -> Self {
        let simple_file = SimpleFile::new(path.to_string(), source.clone());
        Self {
            source,
            simple_file,
        }
    }

    pub fn create_diagnostic(
        &self,
        message: String,
        location_of_err: std::ops::Range<usize>,
        notes: String,
    ) -> Diagnostic<()> {
        Diagnostic::error()
            .with_message(message)
            .with_labels(vec![Label::primary((), location_of_err)])
            .with_notes(vec![notes])
    }
}
