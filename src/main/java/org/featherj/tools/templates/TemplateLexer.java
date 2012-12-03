package org.featherj.tools.templates;

import java.nio.CharBuffer;

public class TemplateLexer {

    private CharBuffer buffer;

    public TemplateLexer(CharBuffer buffer) {
        this.buffer = buffer;
    }

    public TemplateToken getNextToken() {
        return null;
    }
}
