package org.featherj.tools.templates;

public class TemplateEngineParseException extends Exception {
    public TemplateEngineParseException() {
    }

    public TemplateEngineParseException(TemplateLexer lexer, String message) {
        super("Line: " + lexer.getLine() + ", col: " + lexer.getCol() + " " + message);
    }
}
