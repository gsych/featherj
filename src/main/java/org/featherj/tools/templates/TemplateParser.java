package org.featherj.tools.templates;

import java.nio.CharBuffer;

public class TemplateParser {

    private class CodeBuilder {
        private static final String INDENT = "    ";
        private StringBuilder sb;
        private String indent = "";

        public CodeBuilder() {
            sb = new StringBuilder();
        }

        public CodeBuilder(int capacity) {
            sb = new StringBuilder(capacity);
        }

        public CodeBuilder append(String s) {
            sb.append(s);
            return this;
        }

        public CodeBuilder append(char c) {
            sb.append(c);
            return this;
        }

        public CodeBuilder append(CodeBuilder builder) {
            sb.append(builder.sb);
            return this;
        }

        public CodeBuilder appendLine() {
            appendLine("");
            return this;
        }

        public CodeBuilder appendLine(String s) {
            append(s + "\n" + indent);
            return this;
        }

        public CodeBuilder appendToken(TemplateToken token) {
            for (int i = token.getStart(); i < token.getEnd(); i++) {
                char ch = buffer.get(i);
                switch (ch) {
                    case '\r':
                        break;
                    case '"':
                        append(javaCodeMode ? "\"" : "\\\"");
                        break;
                    case '\\':
                        append(javaCodeMode ? "\\" : "\\\\");
                        break;
                    default:
                        append(ch);
                        break;
                }
            }
            return this;
        }

        public int length() {
            return sb.length();
        }

        public CodeBuilder indent() {
            indent += INDENT;
            append(INDENT);
            return this;
        }

        public CodeBuilder outdent() {
            indent = indent.substring(INDENT.length());
            sb.delete(sb.length() - 4, sb.length());
            return this;
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

    private boolean javaCodeMode;
    private CharBuffer buffer;
    private final String packageName;
    private final String className;
    private CodeBuilder activeCodeBuilder;
    private CodeBuilder extendsClauseBuilder;
    private CodeBuilder classCodeBuilder;
    private TemplateLexer lexer;

    public TemplateParser(CharBuffer buffer, String packageName, String className) {
        this.buffer = buffer;
        this.packageName = packageName;
        this.className = className;
        classCodeBuilder = new CodeBuilder(buffer.capacity());
        lexer = new TemplateLexer(buffer);
    }

    public String parse() throws TemplateEngineParseException {
        outputTo(classCodeBuilder);

        activeCodeBuilder.append("package ").append(packageName).appendLine(";");
        activeCodeBuilder.appendLine();
        activeCodeBuilder.appendLine("import org.featherj.View;");

        start();

        return activeCodeBuilder.toString();
    }

    private void outputTo(CodeBuilder builder) {
        activeCodeBuilder = builder;
    }

    private void renderClassStart() {
        activeCodeBuilder.appendLine();
        activeCodeBuilder.append("public class ").append(className).append(" ");
        if (extendsClauseBuilder != null && extendsClauseBuilder.length() > 0) {
            activeCodeBuilder.append(extendsClauseBuilder);
        }
        else {
            activeCodeBuilder.append("implements View");
        }
        activeCodeBuilder.appendLine(" {");
        activeCodeBuilder.indent();
    }

    private void renderRenderInheritedMethod() {
        activeCodeBuilder.appendLine();

        activeCodeBuilder.appendLine("@Override");
        activeCodeBuilder.appendLine("public String renderInherited() {");
        activeCodeBuilder.indent();
        activeCodeBuilder.appendLine("return \"\";");
        activeCodeBuilder.outdent();
        activeCodeBuilder.appendLine("}");
    }

    private void renderRenderMethodStart() {
        activeCodeBuilder.appendLine();

        activeCodeBuilder.appendLine("public String render() {");
        activeCodeBuilder.indent();
        activeCodeBuilder.appendLine("String newLine = System.getProperty(\"line.separator\");");
        activeCodeBuilder.appendLine("StringBuilder view = new StringBuilder();");
    }

    private void renderRenderMethodAndClassEnd() {
        activeCodeBuilder.appendLine();
        activeCodeBuilder.appendLine("return view.toString();");
        activeCodeBuilder.outdent();
        activeCodeBuilder.appendLine("}");
        activeCodeBuilder.outdent();
        activeCodeBuilder.append("}");
    }

    private TemplateToken match(TemplateToken.TokenType tokenType) throws TemplateEngineParseException {
        TemplateToken token = lexer.getNextToken();
        if (token == null || token.getTokenType() != tokenType) {
            throw new TemplateEngineParseException(lexer, tokenType + " expected.");
        }

        return token;
    }

    private void start() throws TemplateEngineParseException {
        importDirectives();
        extendsDirective();
        renderClassStart();
        membersDirectives();
        renderRenderInheritedMethod();
        renderRenderMethodStart();
        body();
        renderRenderMethodAndClassEnd();
    }

    private void lts() throws TemplateEngineParseException {
        while (lexer.hasNext() && lexer.lookahead().getTokenType() == TemplateToken.TokenType.NewLine) {
            match(TemplateToken.TokenType.NewLine);
        }
    }

    private void extendsDirective() throws TemplateEngineParseException {
        if (!lexer.hasNext()) {
            return;
        }
        TemplateToken lookahead1 = lexer.lookahead();
        if (lookahead1.getTokenType() == TemplateToken.TokenType.TagOpen && lexer.hasNext(1)) {
            TemplateToken lookahead2 = lexer.lookahead(2);
            if (lookahead2.getTokenType() == TemplateToken.TokenType.Extends) {
                match(TemplateToken.TokenType.TagOpen);
                match(TemplateToken.TokenType.Extends);

                extendsClauseBuilder = new CodeBuilder();
                outputTo(extendsClauseBuilder);

                javaCodeMode = true;
                activeCodeBuilder.append("extends");
                text();
                javaCodeMode = false;

                outputTo(classCodeBuilder);

                match(TemplateToken.TokenType.TagClose);
            }
        }
    }

    private void importDirectives() throws TemplateEngineParseException {
        lts();
        if (lexer.hasNext()) {
            TemplateToken lookahead1 = lexer.lookahead();
            if (lookahead1.getTokenType() == TemplateToken.TokenType.TagOpen && lexer.hasNext(1)) {
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

        activeCodeBuilder.append("import");

        javaCodeMode = true;
        text();
        javaCodeMode = false;

        match(TemplateToken.TokenType.TagClose);
    }

    private void membersDirectives() throws TemplateEngineParseException {
        lts();
        if (lexer.hasNext()) {
            TemplateToken lookahead1 = lexer.lookahead();
            if (lookahead1.getTokenType() == TemplateToken.TokenType.TagOpen  && lexer.hasNext(1)) {
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
                return;
            }
        }
        throw new TemplateEngineParseException(lexer,
                TemplateToken.TokenType.TagOpen + " or " + TemplateToken.TokenType.Echo + " expected.");
    }

    private void echo() throws TemplateEngineParseException {
        match(TemplateToken.TokenType.Echo);

        activeCodeBuilder.append("view.append(String.valueOf( ");

        javaCodeMode = true;
        text();
        javaCodeMode = false;

        activeCodeBuilder.append(") );");

        match(TemplateToken.TokenType.TagClose);
    }

    private void text() throws TemplateEngineParseException {
        line();
        while (lexer.hasNext() && lexer.lookahead().getTokenType() == TemplateToken.TokenType.NewLine) {
            match(TemplateToken.TokenType.NewLine);
            activeCodeBuilder.appendLine(javaCodeMode ? "" : "view.append(newLine);");
            line();
        }
    }


    private void line() throws TemplateEngineParseException {
        while (lexer.hasNext() && lexer.lookahead().getTokenType() == TemplateToken.TokenType.TextSpan) {
            textSpan();
        }
    }

    private void textSpan() throws TemplateEngineParseException {
        TemplateToken token = match(TemplateToken.TokenType.TextSpan);
        if (!javaCodeMode) {
            activeCodeBuilder.append("view.append(\"");
        }
        activeCodeBuilder.appendToken(token);
        if (!javaCodeMode) {
            activeCodeBuilder.append("\");");
        }
    }
}
