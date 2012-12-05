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

        public void append(char c) {
            renderedTemplate.append(c);
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

        public void appendToken(TemplateToken token) {
            for (int i = token.getStart(); i < token.getEnd(); i++) {
                append(buffer.get(i));
            }
        }

        @Override
        public String toString() {
            return renderedTemplate.toString();
        }
    }

    private boolean javaCodeMode;
    private CharBuffer buffer;
    private final String packageName;
    private final String className;
    private CodeBuilder codeBuilder;
    private TemplateLexer lexer;

    public TemplateParser(CharBuffer buffer, String packageName, String className) {
        if (packageName != null && packageName.charAt(packageName.length() - 1) != '.') {
            packageName += '.';
        }

        this.buffer = buffer;
        this.packageName = packageName;
        this.className = className;
        codeBuilder = new CodeBuilder(buffer.capacity());
        lexer = new TemplateLexer(buffer);
    }

    public String parse() throws TemplateEngineParseException {
        codeBuilder.appendLine("package " + packageName + "gen;");
        codeBuilder.appendLine();
        codeBuilder.appendLine("import org.featherj.View;");

        start();

        return codeBuilder.toString();
    }

    private void renderClassStart() {
        codeBuilder.appendLine();
        codeBuilder.appendLine("public class " + className + " implements View {");
        codeBuilder.indent();
    }

    private void renderRenderMethodStart() {
        codeBuilder.appendLine();
        codeBuilder.appendLine("public String render() {");
        codeBuilder.indent();
        codeBuilder.appendLine("String newLine = System.getProperty(\"line.separator\");");
        codeBuilder.appendLine("StringBuilder view = new StringBuilder();");
    }

    private void renderRenderMethodAndClassEnd() {
        codeBuilder.appendLine();
        codeBuilder.appendLine("return view.toString();");
        codeBuilder.outdent();
        codeBuilder.appendLine("}");
        codeBuilder.outdent();
        codeBuilder.append("}");
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

        renderClassStart();

        membersDirectives();

        renderRenderMethodStart();

        body();

        renderRenderMethodAndClassEnd();
    }

    private void importDirectives() throws TemplateEngineParseException {
        if (lexer.hasNext()) {
            TemplateToken lookahead1 = lexer.lookahead();
            if (lookahead1.getTokenType() == TemplateToken.TokenType.TagOpen) {
                TemplateToken lookahead2 = lexer.lookahead(2);
                if (lookahead2.getTokenType() == TemplateToken.TokenType.Import) {
                    importDirective();
                    importDirectives();
                }
            }
        }
    }

    private void importDirective() throws TemplateEngineParseException {
        match(TemplateToken.TokenType.TagOpen);
        match(TemplateToken.TokenType.Import);

        codeBuilder.append("import");

        text();
        match(TemplateToken.TokenType.TagClose);
    }

    private void membersDirectives() throws TemplateEngineParseException {
        if (lexer.hasNext()) {
            TemplateToken lookahead1 = lexer.lookahead();
            if (lookahead1.getTokenType() == TemplateToken.TokenType.TagOpen) {
                TemplateToken lookahead2 = lexer.lookahead(2);
                if (lookahead2.getTokenType() == TemplateToken.TokenType.Members) {
                    membersDirective();
                    membersDirectives();
                }
            }
        }
    }

    private void membersDirective() throws TemplateEngineParseException {
        match(TemplateToken.TokenType.TagOpen);
        match(TemplateToken.TokenType.Members);
        javaCodeMode = true;
        text();
        javaCodeMode = false;
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
        if (lexer.hasNext()) {
            TemplateToken lookahead = lexer.lookahead();
            if (lookahead.getTokenType() == TemplateToken.TokenType.Echo) {
                echo();
                return;
            }
            else if (lookahead.getTokenType() == TemplateToken.TokenType.TagOpen) {
                match(TemplateToken.TokenType.TagOpen);
                javaCodeMode = true;
                text();
                javaCodeMode = false;
                match(TemplateToken.TokenType.TagClose);
            }
        }
        throw new TemplateEngineParseException(
                TemplateToken.TokenType.TagOpen + " or " + TemplateToken.TokenType.Echo + " expected.");
    }

    private void echo() throws TemplateEngineParseException {
        match(TemplateToken.TokenType.Echo);

        codeBuilder.append("view.append(String.valueOf( ");

        javaCodeMode = true;
        text();
        javaCodeMode = false;

        codeBuilder.appendLine(" );");

        match(TemplateToken.TokenType.TagClose);
    }

    private void text() throws TemplateEngineParseException {
        line();
        while (lexer.hasNext() && lexer.lookahead().getTokenType() == TemplateToken.TokenType.NewLine) {
            match(TemplateToken.TokenType.NewLine);
            codeBuilder.appendLine();
            line();
        }
    }

    private void line() throws TemplateEngineParseException {
        TemplateToken lookahead = lexer.lookahead();
        while (
            lexer.hasNext() && (
                lookahead.getTokenType() == TemplateToken.TokenType.DoubleQuote ||
                lookahead.getTokenType() == TemplateToken.TokenType.Slash ||
                lookahead.getTokenType() == TemplateToken.TokenType.TextSpan
            )) {
            if (lookahead.getTokenType() == TemplateToken.TokenType.DoubleQuote) {
                doubleQuote();
            }
            else if (lookahead.getTokenType() == TemplateToken.TokenType.Slash) {
                slash();
            }
            else {
                textSpan();
            }
            lookahead = lexer.lookahead();
        }
    }

    private void doubleQuote() throws TemplateEngineParseException {
        match(TemplateToken.TokenType.DoubleQuote);
        if (!javaCodeMode) {
            codeBuilder.append("\\\"");
        }
        else {
            codeBuilder.append('"');
        }
    }

    private void slash() throws TemplateEngineParseException {
        match(TemplateToken.TokenType.Slash);
        if (!javaCodeMode) {
            codeBuilder.append("\\\\");
        }
        else {
            codeBuilder.append('\\');
        }
    }

    private void textSpan() throws TemplateEngineParseException {
        TemplateToken token = match(TemplateToken.TokenType.TextSpan);
        codeBuilder.appendToken(token);
        if (!javaCodeMode) {
            codeBuilder.appendLine();
        }
    }
}
