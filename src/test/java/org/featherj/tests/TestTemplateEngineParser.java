package org.featherj.tests;

import org.featherj.tools.templates.TemplateEngineParseException;
import org.featherj.tools.templates.TemplateParser;
import org.junit.Test;

import java.nio.CharBuffer;

public class TestTemplateEngineParser {

    @Test
    public void testSingleImportDirective() throws TemplateEngineParseException {
        CharBuffer buffer = CharBuffer.wrap("<% import org.featherj.test; %>");
        TemplateParser parser = new TemplateParser(buffer);

        String classCode = parser.parse("org.test", "TestClassName");
    }
}
