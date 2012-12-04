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
        return null;
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
}
