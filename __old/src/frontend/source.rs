use codespan_reporting::diagnostic::{Diagnostic, Label};

#[derive(Debug)]
pub struct Source {
    pub source: String,
}

impl Source {
    pub fn new(source: String) -> Self {
        Self {
            source
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
