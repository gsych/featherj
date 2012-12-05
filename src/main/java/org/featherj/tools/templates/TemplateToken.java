package org.featherj.tools.templates;

public class TemplateToken {
    public enum TokenType {
        DirectiveOpen,
        TagOpen,
        TagClose,
        NewLine,
        DoubleQuote,
        Slash,
        Import, Members, Echo, TextSpan, CodeRange
    }

    private TokenType tokenType;
    private int start;
    private int end;

    public TemplateToken(TokenType tokenType, int start, int end) {
        this.tokenType = tokenType;
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
}
