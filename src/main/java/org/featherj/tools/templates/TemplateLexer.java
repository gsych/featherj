package org.featherj.tools.templates;

import java.nio.CharBuffer;

public class TemplateLexer {

    private CharBuffer buffer;

    public TemplateLexer(CharBuffer buffer) {
        this.buffer = buffer;
    }

    public boolean hasNext() {
        return hasNext(0);
    }

    public boolean hasNext(int i) {
        return buffer.position() + i < buffer.limit();
    }

    public TemplateToken getNextToken() throws TemplateEngineParseException {
        int start = buffer.position();
        TemplateToken.TokenType type = start();
        int end = buffer.position();

        return new TemplateToken(type, start, end);
    }

    public TemplateToken lookahead() throws TemplateEngineParseException {
        return lookahead(1);
    }

    public TemplateToken lookahead(int num) throws TemplateEngineParseException {
        int pos = buffer.position();
        TemplateToken token = null;
        for (int i = 0; i < num && hasNext(); i++) {
            token = getNextToken();
        }
        buffer.position(pos);
        return token;
    }

    private char peek() {
        return peek(0);
    }

    private char peek(int i) {
        return buffer.charAt(i);
    }

    private char read() {
        return buffer.get();
    }

    private TemplateToken.TokenType start() throws TemplateEngineParseException {
        ws();

        if (!hasNext()) {
            throw new TemplateEngineParseException("Reached end of the input buffer.");
        }

        switch (peek()) {
            case '<':
                read();
                if (hasNext() && peek() == '%') {
                    read();
                    if (hasNext() && peek() == '=') {
                        read();
                        return TemplateToken.TokenType.Echo;
                    }
                    return TemplateToken.TokenType.TagOpen;
                }
                break;

            case '%':
                read();
                if (hasNext() && peek() == '>') {
                    read();
                    return TemplateToken.TokenType.TagClose;
                }
                break;

            case '\\':
                read();
                return TemplateToken.TokenType.Slash;

            case '"':
                read();
                return TemplateToken.TokenType.DoubleQuote;

            default:
                break;
        }

        return rest();
    }

    private void ws() {
        if (hasNext()) {
            char lookahead = peek();
            while ((Character.isWhitespace(lookahead) || lookahead == '\r')) {
                read();
                if (!hasNext()) {
                    break;
                }
                lookahead = peek();
            }
        }
    }

    private TemplateToken.TokenType rest() {
        ws();

        if (!hasNext()) {
            return TemplateToken.TokenType.TextSpan;
        }

        char lookahead = peek();
        String id = "";
        while (Character.isAlphabetic(lookahead)) {
            id += read();
            if (!hasNext()) {
                break;
            }
            lookahead = peek();
        }

        if (id.length() > 0) {
            if (id.equals("import")) {
                return TemplateToken.TokenType.Import;
            }
            if (id.equals("extends")) {
                return TemplateToken.TokenType.Extends;
            }
            if (id.equals("members")) {
                return TemplateToken.TokenType.Members;
            }
        }

        if (!hasNext()) {
            return TemplateToken.TokenType.TextSpan;
        }
        lookahead = peek();
        while (
            lookahead != '<' &&
            lookahead != '%' &&
            lookahead != '"' &&
            lookahead != '\\' &&
            lookahead != '\n'
        ) {
            read();
            if (!hasNext()) {
                break;
            }
            lookahead = peek();
        }

        return TemplateToken.TokenType.TextSpan;
    }
}
