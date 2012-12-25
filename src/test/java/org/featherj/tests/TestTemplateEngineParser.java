package org.featherj.tests;

import junit.framework.Assert;
import org.featherj.templates.TemplateEngineParseException;
import org.featherj.templates.TemplateParser;
import org.junit.Test;

import java.nio.CharBuffer;

public class TestTemplateEngineParser {

    @Test
    public void testSingleImportDirective() throws TemplateEngineParseException {
        CharBuffer buffer = CharBuffer.wrap("<% import org.featherj.test; %>");
        TemplateParser parser = new TemplateParser(buffer, "org.test", "TestClassName");

        String classCode = parser.parse();
//        System.out.print(classCode);
        Assert.assertEquals(
                "package org.test;\n" +
                "\n" +
                "import org.featherj.View;\n" +
                "import org.featherj.test; \n" +
                "public class TestClassName implements View {\n" +
                "    \n" +
                "    public String render() {\n" +
                "        return render(\"\");\n" +
                "    }\n" +
                "    \n" +
                "    public String render(String inheritedContent) {\n" +
                "        String newLine = System.getProperty(\"line.separator\");\n" +
                "        StringBuilder view = new StringBuilder();\n" +
                "        \n" +
                "        return view.toString();\n" +
                "    }\n" +
                "}",
                classCode);
    }

    @Test
    public void testMultipleImportDirectives() throws TemplateEngineParseException {
        CharBuffer buffer = CharBuffer.wrap(
                "<% import org.test1; %>\n" +
                "<% import org.test2; %>");
        TemplateParser parser = new TemplateParser(buffer, "org.test", "TestClassName");

        String classCode = parser.parse();
//        System.out.print(classCode);
        Assert.assertEquals(
                "package org.test;\n" +
                        "\n" +
                        "import org.featherj.View;\n" +
                        "import org.test1; \n" +
                        "import org.test2; \n" +
                        "public class TestClassName implements View {\n" +
                        "    \n" +
                        "    public String render() {\n" +
                        "        return render(\"\");\n" +
                        "    }\n" +
                        "    \n" +
                        "    public String render(String inheritedContent) {\n" +
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
                        "        private int var;\n" +
                        "        private Object param;\n" +
                        "    \n" +
                        "        public TestClassName(int var, Object param) {\n" +
                        "            this.var = var;\n" +
                        "            this.param = param;\n" +
                        "        }\n" +
                        "    \n" +
                        "    public String render() {\n" +
                        "        return render(\"\");\n" +
                        "    }\n" +
                        "    \n" +
                        "    public String render(String inheritedContent) {\n" +
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
                "        return render(\"\");\n" +
                "    }\n" +
                "    \n" +
                "    public String render(String inheritedContent) {\n" +
                "        String newLine = System.getProperty(\"line.separator\");\n" +
                "        StringBuilder view = new StringBuilder();\n" +
                "        view.append(String.valueOf(  1 + 1 ) );\n" +
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
                        "\n" +
                        "public class TestClassName extends MyMasterView  {\n" +
                        "    \n" +
                        "    public String render() {\n" +
                        "        return render(\"\");\n" +
                        "    }\n" +
                        "    \n" +
                        "    public String render(String inheritedContent) {\n" +
                        "        String newLine = System.getProperty(\"line.separator\");\n" +
                        "        StringBuilder view = new StringBuilder();\n" +
                        "        \n" +
                        "        return super.render(view.toString());\n" +
                        "    }\n" +
                        "}",
                classCode);
    }

    @Test
    public void testInvalidTagUsage() throws TemplateEngineParseException {
        CharBuffer buffer = CharBuffer.wrap(
                "<div>\n" +
                "        Escaped tag <\\%\n" +
                "        Escaped tag \\%>" +
                "</div>");
        TemplateParser parser = new TemplateParser(buffer, "org.test", "TestClassName");


        try {
            String classCode = parser.parse();
        }
        catch (TemplateEngineParseException exc) {
            Assert.assertEquals("Line: 2, col: 24 TextSpan expected.", exc.getMessage());
            return;
        }
        Assert.fail("Expected exception wasn't thrown.");
//        System.out.print(classCode);

    }

    @Test
    public void testSimpleHtmlTemplate() throws TemplateEngineParseException {
        CharBuffer buffer = CharBuffer.wrap(
                "<div class=\"test\">\n" +
                "    <p>Test!</p>\n" +
                "</div>");
        TemplateParser parser = new TemplateParser(buffer, "org.test", "TestClassName");

        String classCode = parser.parse();
//        System.out.print(classCode);
        Assert.assertEquals(
                "package org.test;\n" +
                        "\n" +
                        "import org.featherj.View;\n" +
                        "\n" +
                        "public class TestClassName implements View {\n" +
                        "    \n" +
                        "    public String render() {\n" +
                        "        return render(\"\");\n" +
                        "    }\n" +
                        "    \n" +
                        "    public String render(String inheritedContent) {\n" +
                        "        String newLine = System.getProperty(\"line.separator\");\n" +
                        "        StringBuilder view = new StringBuilder();\n" +
                        "        view.append(\"<div class=\\\"test\\\">\"); view.append(newLine);\n" +
                        "        view.append(\"    <p>Test!\");view.append(\"</p>\"); view.append(newLine);\n" +
                        "        view.append(\"</div>\");\n" +
                        "        return view.toString();\n" +
                        "    }\n" +
                        "}",
                classCode);
    }
}
