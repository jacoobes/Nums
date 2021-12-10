use logos::Logos;


#[derive(Logos, Debug, PartialEq)]
enum Token {

    


    #[error]
    #[regex(r"[ \t\n\f]+", logos::skip)]
    Error
}