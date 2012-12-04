package org.featherj.tools.templates;

import java.nio.CharBuffer;

public class TemplateParser {

    private class CodeBuilder {
        private static final String INDENT = "    ";
        private StringBuilder renderedTemplate;
        private String indent = "";

        public CodeBuilder(int capacity) {
            renderedTemplate = new StringBuilder(capacity);
        }

        public void append(String s) {
            renderedTemplate.append(s);
        }

        public void appendLine(String s) {
            append(s + "\n" + indent);
        }

        public void appendLine() {
            appendLine("");
        }

        public void indent() {
            indent += INDENT;
            append(INDENT);
        }

        public void outdent() {
            indent = indent.substring(INDENT.length());
            renderedTemplate.delete(renderedTemplate.length() - 4, renderedTemplate.length());
        }

        @Override
        public String toString() {
            return renderedTemplate.toString();
        }
    }

    private CodeBuilder codeBuilder;
    private TemplateLexer lexer;

    public TemplateParser(CharBuffer buffer) {
        codeBuilder = new CodeBuilder(buffer.capacity());
        lexer = new TemplateLexer(buffer);
    }

    public String parse(String packageName, String className) throws TemplateEngineParseException {
        codeBuilder.appendLine("package " + packageName + "gen;");
        codeBuilder.appendLine();
        codeBuilder.appendLine("import org.featherj.View;");
        codeBuilder.appendLine();
        codeBuilder.appendLine("public class " + className + " implements View {");
        codeBuilder.appendLine();
        codeBuilder.indent();
        codeBuilder.appendLine("public String render() {");
        codeBuilder.indent();
        codeBuilder.appendLine("String newLine = System.getProperty(\"line.separator\");");
        codeBuilder.appendLine("StringBuilder view = new StringBuilder();");

        start();

        codeBuilder.appendLine();
        codeBuilder.appendLine("return view.toString();");
        codeBuilder.outdent();
        codeBuilder.appendLine("}");
        codeBuilder.outdent();
        codeBuilder.append("}");

        return codeBuilder.toString();
    }
    private TemplateToken match(TemplateToken.TokenType tokenType) throws TemplateEngineParseException {
        TemplateToken token = lexer.getNextToken();
        if (token == null || token.getTokenType() != tokenType) {
            throw new TemplateEngineParseException(tokenType + " expected.");
        }

        return token;
    }

    private void start() throws TemplateEngineParseException {
        importDirectives();
        constructorDirectives();
        body();
    }

    private void importDirectives() throws TemplateEngineParseException {
        TemplateToken lookahead1 = lexer.lookahead();
        if (lookahead1.getTokenType() == TemplateToken.TokenType.TagOpen) {
            TemplateToken lookahead2 = lexer.lookahead(2);
            if (lookahead2.getTokenType() == TemplateToken.TokenType.Import) {
                importDirective();
                importDirectives();
            }
        }
    }

    private void importDirective() throws TemplateEngineParseException {
        match(TemplateToken.TokenType.TagOpen);
        match(TemplateToken.TokenType.Import);
        text();
        match(TemplateToken.TokenType.TagClose);
    }

    private void constructorDirectives() throws TemplateEngineParseException {
        TemplateToken lookahead1 = lexer.lookahead();
        if (lookahead1.getTokenType() == TemplateToken.TokenType.TagOpen) {
            TemplateToken lookahead2 = lexer.lookahead(2);
            if (lookahead2.getTokenType() == TemplateToken.TokenType.Constructor) {
                constructorDirective();
                constructorDirectives();
            }
        }
    }

    private void constructorDirective() throws TemplateEngineParseException {
        match(TemplateToken.TokenType.TagOpen);
        match(TemplateToken.TokenType.Constructor);
        text();
        match(TemplateToken.TokenType.TagClose);
    }

    private void body() throws TemplateEngineParseException {
        while (lexer.hasNext()) {
            TemplateToken lookahead = lexer.lookahead();
            if (lookahead.getTokenType() == TemplateToken.TokenType.TagOpen ||
                lookahead.getTokenType() == TemplateToken.TokenType.Echo) {
                codeBlock();
            }
            else {
                text();
            }
        }
    }

    private void codeBlock() throws TemplateEngineParseException {
        TemplateToken lookahead = lexer.lookahead();
        if (lookahead.getTokenType() == TemplateToken.TokenType.TagOpen) {
            match(TemplateToken.TokenType.TagOpen);
        }
        else if (lookahead.getTokenType() == TemplateToken.TokenType.Echo) {
            match(TemplateToken.TokenType.Echo);
        }
        else {
            throw new TemplateEngineParseException(
                    TemplateToken.TokenType.TagOpen + " or " + TemplateToken.TokenType.Echo + " expected.");
        }
        text();
        match(TemplateToken.TokenType.TagClose);
    }

    private void text() throws TemplateEngineParseException {
        line();
        while (lexer.hasNext() && lexer.lookahead().getTokenType() == TemplateToken.TokenType.NewLine) {
            match(TemplateToken.TokenType.NewLine);
            line();
        }
    }

    private void line() {
        TemplateToken lookahead = lexer.lookahead();
        while (
            lookahead.getTokenType() == TemplateToken.TokenType.DoubleQuote ||
            lookahead.getTokenType() == TemplateToken.TokenType.Slash ||
            lookahead.getTokenType() == TemplateToken.TokenType.TextSpan
            ) {
            if (lookahead.getTokenType() == TemplateToken.TokenType.DoubleQuote) {
                doubleQuote();
            }
            else if (lookahead.getTokenType() == TemplateToken.TokenType.Slash) {
                slash();
            }
            else {
                textSpan();
            }
        }
    }

    private void doubleQuote() {
        lexer.getNextToken();
    }

    private void slash() {
        lexer.getNextToken();
    }

    private void textSpan() {
        lexer.getNextToken();
    }
}
