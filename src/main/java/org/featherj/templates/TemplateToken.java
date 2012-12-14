package org.featherj.templates;

public class TemplateToken {
    public enum TokenType {
        TagOpen,
        TagClose,
        NewLine,
        Import,
        Extends,
        Members,
        Echo,
        TextSpan
    }

    private TokenType tokenType;
    private int start;
    private int end;

    public TemplateToken(TokenType tokenType, int start, int end) {
        this.tokenType = tokenType;
        this.start = start;
        this.end = end;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return getStart() + ":" + getEnd() + ", " + getTokenType();
    }
}
