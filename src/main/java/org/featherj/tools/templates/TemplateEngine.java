package org.featherj.tools.templates;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class TemplateEngine {

    private class CodeBuilder {
        private static final String INDENT = "    ";
        private StringBuilder renderedTemplate;
        private String indent = "";

        public CodeBuilder(int capacity) {
            renderedTemplate = new StringBuilder(capacity);
        }

        public void append(StringBuilder sb) {
            renderedTemplate.append(sb);
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

    private final String basePackagePath;
    private final String basePackage;
    private CharBuffer buffer;
    private CodeBuilder codeBuilder;
    private TemplateLexer lexer;

    private StringBuilder currentLine;
    private int currentIndex;
    private char prevCh;
    private char currentCh;
    private char lookAhead;
    private boolean javaCodeMode = false;
    private boolean echoMode = false;

    public TemplateEngine(String basePackagePath) {
        this.basePackagePath = basePackagePath;
        this.basePackage = basePackagePath.replace(File.separatorChar, '.');
    }

    public String generateClassCode(File templateFile) throws Exception {
        buffer = readFile(templateFile);
        codeBuilder = new CodeBuilder(buffer.capacity());
        currentLine = new StringBuilder();

        String absPath = templateFile.getAbsolutePath();
        int index = absPath.indexOf(basePackagePath);
        if (index == -1) {
            throw new Exception("Cannot locate base views package within specific HTML file path.");
        }
        index += basePackagePath.length();

        String s = absPath.substring(index, absPath.length() - templateFile.getName().length());

        String packageName = basePackage + s.replace(File.separatorChar, '.');

        codeBuilder.appendLine("package " + packageName + "gen;");
        codeBuilder.appendLine();
        codeBuilder.appendLine("import org.featherj.View;");
        codeBuilder.appendLine();
        codeBuilder.appendLine("public class " + FilenameUtils.removeExtension(templateFile.getName()) + " implements View {");
        codeBuilder.appendLine();
        codeBuilder.indent();
        codeBuilder.appendLine("public String render() {");
        codeBuilder.indent();
        codeBuilder.appendLine("String newLine = System.getProperty(\"line.separator\");");
        codeBuilder.appendLine("StringBuilder view = new StringBuilder();");

        lexer = new TemplateLexer(buffer);
        parse();

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

    private void parse() throws TemplateEngineParseException {
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
        codeBlock();
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
        codeBlock();
        match(TemplateToken.TokenType.TagClose);
    }

    private void body() throws TemplateEngineParseException {
        while (lexer.hasNext()) {
            if (lexer.lookahead().getTokenType() == TemplateToken.TokenType.TagOpen ||
                lexer.lookahead().getTokenType() == TemplateToken.TokenType.Echo) {
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

    private void commitLine() {
        if (!javaCodeMode || echoMode) {
            codeBuilder.append("view.append(" + (echoMode ? "( " : "\""));
        }

        codeBuilder.append(currentLine);
        if (!javaCodeMode || echoMode) {
            codeBuilder.append((echoMode ? ") " : "\"") + ").append(newLine);");
        }
        codeBuilder.appendLine();
        currentLine = new StringBuilder();
    }

    private void process() {
        switch (currentCh) {
            case '\r':
                break;

            case '\\':
                currentLine.append("\\\\");
                break;
            case '"':
                currentLine.append("\\\"");
                break;

            case '\n':
                commitLine();
                break;

            case '<':
                if (lookAhead == '%') {
                    commitLine();
                    javaCodeMode = true;
                    currentIndex++;
                    if (currentIndex + 1 < buffer.limit()) {
                        char lookAhead2 = buffer.get(currentIndex + 1);
                        if (lookAhead2 == '=') {
                            currentIndex++;
                            echoMode = true;
                        }
                    }
                }
                else {
                    currentLine.append(currentCh);
                }
                break;

            case '%':
                if (prevCh == '\\') {
                    currentLine.append(currentCh);
                }
                else {
                    if (lookAhead == '>') {
                        commitLine();
                        javaCodeMode = false;
                        echoMode = false;
                        currentIndex++;
                    }
                }
                break;

            default:
                currentLine.append(currentCh);
                break;
        }
    }

    private static CharBuffer readFile(File templateFile) throws IOException {
        FileInputStream stream = new FileInputStream(templateFile);
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb);
        }
        finally {
            stream.close();
        }
    }

}
