use crate::compiler::nodes::path::{Path, PackagePath};
use crate::compiler::nodes::{decl::Decl, expr::Expr, stmt::Stmt};
use crate::compiler::{parser::peekable_parser::Peekable as PeekerWrap, tokens::Token};
use crate::{
    compiler::source::Source,
    error_handling::faults::{ErrTyp::*, Faults::*, *},
};
use crate::{create_binexpr, match_adv};
use codespan_reporting::diagnostic::Diagnostic;
use logos::Lexer;
use smol_str::SmolStr;
use std::ops::Range;
use std::rc::Rc;
pub struct Parser<'source> {
    tokens: PeekerWrap<'source>,
    source: std::rc::Rc<Source>,
    start: usize,
    end: usize,
}

///iterator methods
impl<'source> Parser<'source> {
    /// advances, expecting to there to be a token
    fn expect_token(&mut self, token: &Token) -> Result<Token, Diagnostic<()>> {
        match self.peek() {
            Some(tok) if tok == token => Ok(self.next().unwrap()),
            Some(_) => {
                let fault = self.next();
                Err(self.new_span(Error(Expected(token.clone(), fault?)), ""))
            }
            None => Err(self.new_span(Error(UnexpectedEndOfParsing), "")),
        }
    }
    fn peek(&mut self) -> Option<&Token> {
        self.tokens.peek().take()
    }

    fn next(&mut self) -> Result<Token, Diagnostic<()>> {
        self.end = self.tokens.token_span();
        self.tokens
            .next()
            .ok_or(self.new_span(Error(UnexpectedEndOfParsing), ""))
    }
    fn resolve_node<T>(&mut self, node: T) -> Result<T, Diagnostic<()>> {
        self.start = self.end;
        Ok(node)
    }
    /// check if token matches the current peeked token in the iterator
    fn check_peek(&mut self, token: &Token) -> bool {
        self.peek() == Some(token)
    }
    fn is_at_end(&mut self) -> bool {
        matches!(self.peek(), None)
    }
    fn span(&mut self) -> Range<usize> {
        self.start..self.end
    }
    fn check_uniq_module(module: &fnv::FnvHashMap<SmolStr, Decl>, key: &SmolStr) -> bool {
        if let Some(_) = module.get(key) {
            false
        } else {
            true
        }
    }
}

/// Basic recursive descent parsing
/// Errors are made using Spans, which will pretty print the error and its possible (not tested if accurate) location
/// First pass of the parser can detect basic unknown token errors, expected tokens, etc
impl<'source> Parser<'source> {
    pub fn new(tokens: Lexer<'source, Token>, source: Rc<Source>) -> Self {
        Self {
            tokens: PeekerWrap::new(tokens.source()),
            source,
            start: 0,
            end: 0,
        }
    }

    pub fn parse(&mut self) -> Result<Decl, Vec<Diagnostic<()>>> {
        let mut diagnostic_vec = Vec::new();
        let mut had_parse_err = false;
        if self.is_at_end() {
            return Err(diagnostic_vec);
        }
        let get_name = match self.peek().unwrap() {
            &Token::Package => {
                self.next().unwrap();
                self.get_name().map_err(|error| {
                    had_parse_err = true;
                    self.synchronize();
                    diagnostic_vec.push(error)
                })
            }
            _ => Err({
                had_parse_err = true;
                diagnostic_vec.push(self.new_span(
                    Error(FileNotAModule),
                    "This file is not registered as a module",
                ));
            }),
        };

        if let Ok(_) = get_name {
            let _ = self.expect_token(&Token::Semi).map_err(|e| {
                had_parse_err = true;
                diagnostic_vec.push(e)
            });
            self.start = self.end;
        }
        // getting the file's modules if there is no semicolon after attempting to get the name of the package
        let mut modules = fnv::FnvHashMap::<SmolStr, Decl>::default();
        loop {
            if self.is_at_end() {
                if had_parse_err {
                    break Err(diagnostic_vec);
                }
                break Ok(Decl::Module(get_name.unwrap(), modules));
            }
            match self.top_level() {
                Ok(decl) => match &decl {
                    Decl::Function(nombre, ..)
                    | Decl::ExposedFn(nombre, ..)
                    | Decl::Record(nombre, ..)
                    | Decl::ExposedRec(nombre, ..) => {
                        if Parser::check_uniq_module(&modules, nombre) {
                            modules.insert(nombre.clone(), decl);
                        } else {
                            had_parse_err = true;
                            diagnostic_vec.push(
                                self.new_span(Error(DeclarationAlreadyFound(nombre.clone())), ""),
                            );
                            self.synchronize();
                        }
                    }
                    Decl::Module(..) | Decl::ExposedModule(..) => (),
                    Decl::Get(..) => {
                        modules.insert(decl.get_name(), decl);
                    }
                },
                Err(e) => {
                    had_parse_err = true;
                    diagnostic_vec.push(e);
                    self.synchronize();
                }
            }
        }
    }

    fn synchronize(&mut self) {
        while let Some(token) = self.peek() {
            match token {
                &Token::Function | &Token::Record | &Token::Package => break,
                _ => {
                    let is_semi = self
                        .next()
                        .and_then(|t| Ok(t == Token::Semi))
                        .unwrap();
                    if is_semi { break } else { continue }
                }
            }
        }
    }

    /// top level declarations. If it finds anything that
    fn top_level(&mut self) -> Result<Decl, Diagnostic<()>> {
        match self.peek().unwrap() {
            tok => match tok {
                &Token::Expose => {
                    self.next().unwrap();
                    match self.peek() {
                        Some(tok) if tok == &Token::Function => self.parse_fn(true),
                        Some(tok) if tok == &Token::Record => self.parse_rec(true),
                        Some(tok) if tok == &Token::Get => self.parse_get(),
                        _ => {
                            let other = self.next()?;
                            Err(self.new_span(
                                Error(UnexpectedToken(other)),
                                "Expected `fun` or `record` after visibility modifier",
                            ))
                        }
                    }
                }
                &Token::Function => self.parse_fn(false),
                &Token::Record => self.parse_rec(false),
                &Token::Package => {
                    let name = self.next().and_then(|_| Ok(self.get_name()?))?;
                    Err(self.new_span(
                        Error(MultiplePackageDeclInFile(name)),
                        "Remove one of the package declarations",
                    ))
                }
                &Token::Get => self.parse_get(),
                _ => Err(self.new_span(Error(NoTopLevelDeclaration), "")),
            },
        }
    }

    fn parse_get(&mut self) -> Result<Decl, Diagnostic<()>> {
        let mut vec_path = Vec::new();
        self.next().and_then(|_| {
            while let Some(tok) = match_adv!(&mut self, &Token::Identifier(..) | &Token::Squiggly) {
                match tok {
                    Token::Identifier(s) => vec_path.push(Path::Ident(s)),
                    Token::Squiggly => vec_path.push(Path::All),
                    other => {
                        return Err(self.new_span(
                            Error(UnexpectedToken(other)),
                            "Failed to parse a `get` declaration",
                        ))
                    }
                };
                if let Some(_) = match_adv!(&mut self, Token::Semi) {
                    break;
                } else {
                    self.expect_token(&Token::Colon)?;
                    match self.peek() {
                        Some(tok) if matches!(tok, &Token::Identifier(_) | &Token::Squiggly) => {
                            continue
                        }
                        None => {
                            return Err(self.new_span(
                                Error(UnexpectedEndOfParsing),
                                "Reached end of parsing while trying to parse get declaration",
                            ))
                        }
                        Some(_) => return self.next().and_then(|tok| Err(self.new_span(Error(UnexpectedToken(tok)), "")))
                    }
                }
            }
            Ok(Decl::Get(PackagePath::from(vec_path)))
        })
    }

    fn statements(&mut self) -> Result<Stmt, Diagnostic<()>> {
        match self.peek() {
            Some(tok) => match tok {
                &Token::While => self.while_loop(),
                &Token::Let | &Token::Mut => self.var_decl(),
                &Token::If => self.if_else(),
                &Token::Return => {
                    let ret_expr = self.next().and_then(|_| self.expr())?;
                    self.expect_token(&Token::Semi)
                        .and_then(|_| self.resolve_node(Stmt::Return(ret_expr)))
                }
                &Token::LeftBrace => {
                    let block = self.block()?;
                    self.resolve_node(Stmt::Block(block))
                }
                _ => self.stmt_expr(),
            },
            None => Err(self.new_span(Error(UnexpectedEndOfParsing), "")),
        }
    }

    fn if_else(&mut self) -> Result<Stmt, Diagnostic<()>> {
        let condition = self.next().and_then(|_| self.expr())?;
        let block = self.block()?;
        let else_block = if let Some(_) = match_adv!(&mut self, &Token::Else) {
            Some(self.block()?)
        } else {
            None
        };
        Ok(Stmt::IfElse(condition, block, else_block))
    }

    fn while_loop(&mut self) -> Result<Stmt, Diagnostic<()>> {
        let condition = self.next().and_then(|_| self.expr());
        let block = self.block();
        self.resolve_node(Stmt::While(condition?, block?))
    }

    fn var_decl(&mut self) -> Result<Stmt, Diagnostic<()>> {
        let mut_state = self.next()?;
        let name = self.get_name()?;
        let typ_tok = if let Some(_) = match_adv!(&mut self, &Token::Colon) {
            Some(self.get_type()?)
        } else {
            None
        };
        self.expect_token(&Token::Assign)?;
        let var_val = self.stmt_expr();
        if mut_state == Token::Mut {
            self.resolve_node(Stmt::Mut(name, typ_tok, Box::new(var_val?)))
        } else {
            self.resolve_node(Stmt::Let(name, typ_tok, Box::new(var_val?)))
        }
    }

    fn stmt_expr(&mut self) -> Result<Stmt, Diagnostic<()>> {
        let value_of_statement = self.expr();
        self.expect_token(&Token::Semi)?;
        self.resolve_node(Stmt::ExprStatement(value_of_statement?))
    }

    fn expr(&mut self) -> Result<Expr, Diagnostic<()>> {
        self.assignment()
    }

    fn assignment(&mut self) -> Result<Expr, Diagnostic<()>> {
        let asignee = self.or();
        if let Some(_) = match_adv!(&mut self, &Token::Assign) {
            let assignment = self.assignment();
            match asignee {
                Ok(e) if matches!(e, Expr::Val(_)) => {
                    let var = SmolStr::from(self.tokens.slice());
                    self.resolve_node(Expr::Assignment {
                        var,
                        value: Box::new(assignment?),
                    })
                }
                other => return Err(self.new_span(Error(InvalidAssignmentTarget(other?)), "")),
            }
        } else {
            asignee
        }
    }

    fn or(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.and();
        while let Some(operator) = match_adv!(&mut self, &Token::Or) {
            let right = self.and();
            left = self.resolve_node(Expr::Logical {
                operator,
                left: Box::new(left?),
                right: Box::new(right?),
            })
        }
        left
    }

    fn and(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.equality();
        while let Some(operator) = match_adv!(&mut self, &Token::And) {
            let right = self.equality();
            left = self.resolve_node(Expr::Logical {
                operator,
                left: Box::new(left?),
                right: Box::new(right?),
            })
        }
        left
    }

    fn equality(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.compare();
        while let Some(operator) = match_adv!(&mut self, &Token::Eq | &Token::NotEq) {
            let right = self.compare();
            left = create_binexpr!(&mut self, operator, left, right);
        }
        left
    }

    fn compare(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.term();
        while let Some(operator) = match_adv!(
            &mut self,
            &Token::LessEq | &Token::GreaterEq | &Token::LeftArr | &Token::RightArr
        ) {
            let right = self.term();
            left = create_binexpr!(&mut self, operator, left, right);
        }
        left
    }

    fn term(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.factor();
        while let Some(operator) = match_adv!(&mut self, &Token::Minus | &Token::Plus) {
            let right = self.factor();
            left = create_binexpr!(&mut self, operator, left, right);
        }
        left
    }

    fn factor(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut left = self.power();
        while let Some(operator) = match_adv!(&mut self, &Token::Star | &Token::FowardSlash) {
            let right = self.power();
            left = create_binexpr!(&mut self, operator, left, right);
        }
        left
    }
    ///right associative
    fn power(&mut self) -> Result<Expr, Diagnostic<()>> {
        let left = self.unary();
        if let Some(operator) = match_adv!(&mut self, &Token::Caret) {
            let right = self.power();
            create_binexpr!(&mut self, operator, left, right)
        } else {
            left
        }
    }
    /// right associative unary parser
    fn unary(&mut self) -> Result<Expr, Diagnostic<()>> {
        if let Some(operator) = match_adv!(&mut self, &Token::Bang | &Token::Minus | &Token::Plus) {
            let expr = self.unary();
            Ok(Expr::Unary {
                operator,
                expr: Box::new(expr?),
            })
        } else {
            self.callee()
        }
    }

    fn callee(&mut self) -> Result<Expr, Diagnostic<()>> {
        let mut expr = self.grouping();
        loop {
            expr = if let Some(_) = match_adv!(&mut self, &Token::LeftBrack) {
                let call = self.finish_call(expr)?;
                self.expect_token(&Token::RightBrack)?;
                self.resolve_node(call)
            } else if let Some(_) = match_adv!(&mut self, &Token::Period) {
                let name = self.get_name()?;
                self.resolve_node(Expr::Get(Box::new(expr?), name))
            } else {
                break expr;
            }
        }
    }

    fn finish_call(&mut self, expr: Result<Expr, Diagnostic<()>>) -> Result<Expr, Diagnostic<()>> {
        let mut list_of_args = Vec::new();
        while !self.check_peek(&Token::RightBrack) {
            list_of_args.push(self.expr()?);
            match_adv!(&mut self, Token::Comma);
        }
        Ok(Expr::Call(Box::new(expr?), list_of_args))
    }

    fn grouping(&mut self) -> Result<Expr, Diagnostic<()>> {
        if let Some(_) = match_adv!(&mut self, &Token::LeftParen) {
            let expr = self.expr()?;
            self.expect_token(&Token::RightParen)?;
            Ok(Expr::Group {
                expr: Box::new(expr),
            })
        } else {
            self.primary()
        }
    }

    fn primary(&mut self) -> Result<Expr, Diagnostic<()>> {
        match self.peek().unwrap() {
            _ => match self.next()? {
                Token::Bool(val) => self.resolve_node(Expr::Bool(val)),
                Token::Double(s) => self.resolve_node(Expr::Double(s)),
                Token::Identifier(s) => self.resolve_node(Expr::Val(s)),
                Token::Integer(val) => self.resolve_node(Expr::Integer(val)),
                Token::String(val) => self.resolve_node(Expr::String(val)),
                Token::Char(c) => self.resolve_node(Expr::Char(c)),
                Token::Unit => self.resolve_node(Expr::Unit),
                Token::Error => {
                    let unknown_token = self.tokens.slice();
                    Err(self.new_span(Error(UnknownToken(unknown_token)), "unknown token"))
                }
                other => Err(self.new_span(
                    Error(UnexpectedToken(other)),
                    "found an unexpected token out of place",
                )),
            },
        }
    }

    fn block(&mut self) -> Result<Vec<Stmt>, Diagnostic<()>> {
        let mut stmts_block = Vec::new();
        self.expect_token(&Token::LeftBrace)?;
        while !self.check_peek(&Token::RightBrace) {
            stmts_block.push(self.statements()?)
        }
        self.expect_token(&Token::RightBrace)
            .map_err(|_| self.new_span(Error(UnclosedDelimiter), "Check closing brace `}` "))?;
        Ok(stmts_block)
    }

    fn get_type(&mut self) -> Result<Token, Diagnostic<()>> {
        let typ = self.next()?;
        match typ {
            Token::Identifier(_) => Ok(Token::Type(self.tokens.slice())),
            Token::Type(_) => Ok(typ),
            other => Err(self.new_span(
                Error(UnknownType(other)),
                "Found a token that cannot be qualified as a type",
            )),
        }
    }

    fn parse_fn(&mut self, exposed: bool) -> Result<Decl, Diagnostic<()>> {
        let name = self.next().and_then(|_| self.get_name())?;
        let args = self.parse_args()?;
        let fn_ret_type = if let Some(_) = match_adv!(&mut self, &Token::Assign) {
            self.get_type()?
        } else {
            Token::Unit
        };
        let block = self.block();
        if exposed {
            Ok(Decl::ExposedFn(name, args, fn_ret_type, block?))
        } else {
            Ok(Decl::Function(name, args, fn_ret_type, block?))
        }
    }

    fn parse_args(&mut self) -> Result<Option<Vec<(SmolStr, Token)>>, Diagnostic<()>> {
        let mut fn_args = Vec::new();
        if let Some(_) = match_adv!(&mut self, &Token::LeftBrack) {
            if self.check_peek(&Token::RightBrack) {
                self.next().unwrap();
                return Ok(None)
            } 
            let first_arg = self.parse_single_arg()?;
            fn_args.push(first_arg);

            while !self.check_peek(&Token::RightBrack) {
                self.expect_token(&Token::Comma)?;
                let remaining_args = self.parse_single_arg()?;
                fn_args.push(remaining_args);
            }
            self.expect_token(&Token::RightBrack)?;
            Ok(Some(fn_args))
        } else {
            Ok(None)
        }
    }

    fn parse_single_arg(&mut self) -> Result<(SmolStr, Token), Diagnostic<()>> {
        let name = self.get_name()?;
        self.expect_token(&Token::Colon)?;
        let typ = self.get_type()?;
        Ok((name, typ))
    }

    fn parse_rec(&mut self, exposed: bool) -> Result<Decl, Diagnostic<()>> {
        let name = self.next().and_then(|_| self.get_name())?;
        self.expect_token(&Token::LeftBrace)?;
        let args = {
            let mut fields = Vec::new();
            while !self.check_peek(&Token::RightBrace) {
                fields.push(self.parse_single_arg()?);
                if self.check_peek(&Token::Comma) {
                    self.expect_token(&Token::Comma).unwrap();
                    continue;
                } else {
                    break;
                }
            }
            self.expect_token(&Token::RightBrace)?;
            fields
        };
        if exposed {
            Ok(Decl::ExposedRec(name, args))
        } else {
            Ok(Decl::Record(name, args))
        }
    }

    fn get_name(&mut self) -> Result<SmolStr, Diagnostic<()>> {
        match self.peek().unwrap() {
            &Token::Identifier(_) => self.next().and_then(|_| Ok(self.tokens.slice())),
            _ => self.next().and_then(|next| {
                Err(self.new_span(
                    Error(UnexpectedToken(next)),
                    "note : Try to use an identifier",
                ))
            }),
        }
    }
}

impl<'source> Parser<'source> {
    fn new_span(&mut self, fault: Faults, note: &'source str) -> Diagnostic<()> {
        let span = self.span();
        self.source
            .create_diagnostic(format!("{:?}", fault), span, note.to_string())
    }
}
