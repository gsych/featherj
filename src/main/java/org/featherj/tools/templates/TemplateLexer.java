package org.featherj.tools.templates;

import java.nio.CharBuffer;

public class TemplateLexer {

    private CharBuffer buffer;

    public TemplateLexer(CharBuffer buffer) {
        this.buffer = buffer;
    }

    public boolean hasNext() {
        return buffer.position() + 1 < buffer.limit();
    }

    public TemplateToken getNextToken() {
        int start = buffer.position();
        TemplateToken.TokenType type = start();
        int end = buffer.position();

        return new TemplateToken(type, start, end);
    }

    public TemplateToken lookahead() {
        return lookahead(1);
    }

    public TemplateToken lookahead(int num) {
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

    private TemplateToken.TokenType start() {
        ws();

        char lookahead = peek();
        switch (lookahead) {
            case '<':
                read();
                lookahead = peek();
                if (lookahead == '%') {
                    read();
                    lookahead = peek();
                    if (lookahead == '=') {
                        return TemplateToken.TokenType.Echo;
                    }
                    return TemplateToken.TokenType.TagOpen;
                }
                break;

            case '%':
                read();
                lookahead = peek();
                if (lookahead == '>') {
                    return TemplateToken.TokenType.TagClose;
                }
                break;

            case '\\':
                return TemplateToken.TokenType.Slash;

            case '"':
                return TemplateToken.TokenType.DoubleQuote;

            default:
                break;
        }

        return rest();
    }

    private void ws() {
        char lookahead = peek();
        while (hasNext() && (Character.isWhitespace(lookahead) || lookahead == '\r')) {
            read();
            lookahead = peek();
        }
    }

    private TemplateToken.TokenType rest() {
        ws();

        char lookahead = peek();
        String id = "";
        while (Character.isAlphabetic(lookahead)) {
            id += read();
            lookahead = peek();
        }

        if (id.length() > 0) {
            if (id.equals("import")) {
                return TemplateToken.TokenType.Import;
            }
            if (id.equals("constructor")) {
                return TemplateToken.TokenType.Constructor;
            }
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
            lookahead = peek();
        }

        return TemplateToken.TokenType.TextSpan;
    }

    private boolean equal(CharBuffer b1, CharBuffer b2) {
        return b1.compareTo(b2) == 0;
    }
}
