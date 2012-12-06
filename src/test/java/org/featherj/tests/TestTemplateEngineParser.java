package org.featherj.tests;

import junit.framework.Assert;
import org.featherj.tools.templates.TemplateEngineParseException;
import org.featherj.tools.templates.TemplateParser;
import org.junit.Test;

import java.nio.CharBuffer;

public class TestTemplateEngineParser {

    @Test
    public void testSingleImportDirective() throws TemplateEngineParseException {
        CharBuffer buffer = CharBuffer.wrap("<% import org.featherj.test; %>");
        TemplateParser parser = new TemplateParser(buffer, "org.test", "TestClassName");

        String classCode = parser.parse();
        //System.out.print(classCode);
        Assert.assertEquals(
                "package org.test;\n" +
                "\n" +
                "import org.featherj.View;\n" +
                "import org.featherj.test; \n" +
                "public class TestClassName implements View {\n" +
                "    \n" +
                "    public String render() {\n" +
                "        String newLine = System.getProperty(\"line.separator\");\n" +
                "        StringBuilder view = new StringBuilder();\n" +
                "        \n" +
                "        return view.toString();\n" +
                "    }\n" +
                "}",
                classCode);
    }

    @Test
    public void testMembersDirective() throws TemplateEngineParseException {
        CharBuffer buffer = CharBuffer.wrap(
                "<% members\n"+
                "    private int var;\n" +
                "    private Object param;\n" +
                "\n"+
                "    public TestClassName(int var, Object param) {\n" +
                "        this.var = var;\n" +
                "        this.param = param;\n" +
                "    }\n" +
                "%>"
            );
        TemplateParser parser = new TemplateParser(buffer, "org.test", "TestClassName");

        String classCode = parser.parse();
        //System.out.print(classCode);
        Assert.assertEquals(
                "package org.test;\n" +
                "\n" +
                "import org.featherj.View;\n" +
                "\n" +
                "public class TestClassName implements View {\n" +
                "    \n" +
                "    private int var;\n" +
                "    private Object param;\n" +
                "\n" +
                "    public TestClassName(int var, Object param) {\n" +
                "        this.var = var;\n" +
                "        this.param = param;\n" +
                "    }\n" +
                "    public String render() {\n" +
                "        String newLine = System.getProperty(\"line.separator\");\n" +
                "        StringBuilder view = new StringBuilder();\n" +
                "        \n" +
                "        return view.toString();\n" +
                "    }\n" +
                "}",
                classCode);
    }

    @Test
    public void testSimpleEcho() throws TemplateEngineParseException {
        CharBuffer buffer = CharBuffer.wrap("<%= 1 + 1 %>");
        TemplateParser parser = new TemplateParser(buffer, "org.test", "TestClassName");

        String classCode = parser.parse();
//        System.out.print(classCode);
        Assert.assertEquals("package org.test;\n" +
                "\n" +
                "import org.featherj.View;\n" +
                "\n" +
                "public class TestClassName implements View {\n" +
                "    \n" +
                "    public String render() {\n" +
                "        String newLine = System.getProperty(\"line.separator\");\n" +
                "        StringBuilder view = new StringBuilder();\n" +
                "        view.append(String.valueOf(  1 + 1  );\n" +
                "        \n" +
                "        return view.toString();\n" +
                "    }\n" +
                "}",
                classCode);
    }

    @Test
    public void testSimpleExtendsDirective() throws TemplateEngineParseException {
        CharBuffer buffer = CharBuffer.wrap(
                "<% import org.featherj.test.MyMasterView; %>\n" +
                "<% extends MyMasterView %>");
        TemplateParser parser = new TemplateParser(buffer, "org.test", "TestClassName");

        String classCode = parser.parse();
//        System.out.print(classCode);
        Assert.assertEquals(
                "package org.test;\n" +
                "\n" +
                "import org.featherj.View;\n" +
                "import org.featherj.test.MyMasterView; \n" +
                "public class TestClassName extends MyMasterView  {\n" +
                "    \n" +
                "    public String render() {\n" +
                "        String newLine = System.getProperty(\"line.separator\");\n" +
                "        StringBuilder view = new StringBuilder();\n" +
                "        \n" +
                "        return view.toString();\n" +
                "    }\n" +
                "}",
                classCode);
    }
}
