package org.featherj.tools;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class TemplateEngine {

    private class CodeBuilder {
        private static final String INDENT = "    ";
        private StringBuilder renderedTemplate;
        private String indent = "";

        public CodeBuilder(int capacity) {
            renderedTemplate = new StringBuilder(capacity);
        }

        public void append(StringBuilder sb) {
            renderedTemplate.append(sb);
        }

        public void append(String s) {
            renderedTemplate.append(s);
        }

        public void appendLine(String s) {
            append(s + "\n" + indent);
        }

        public void appendLine() {
            appendLine("");
        }

        public void indent() {
            indent += INDENT;
            append(INDENT);
        }

        public void outdent() {
            indent = indent.substring(INDENT.length());
            renderedTemplate.delete(renderedTemplate.length() - 4, renderedTemplate.length());
        }

        @Override
        public String toString() {
            return renderedTemplate.toString();
        }
    }

    private final String basePackagePath;
    private final String basePackage;
    private CharBuffer buffer;
    private CodeBuilder codeBuilder;
    private StringBuilder currentLine;
    private int currentIndex;
    private char prevCh;
    private char currentCh;
    private char lookAhead;
    private boolean javaCodeMode = false;
    private boolean echoMode = false;

    public TemplateEngine(String basePackagePath) {
        this.basePackagePath = basePackagePath;
        this.basePackage = basePackagePath.replace(File.separatorChar, '.');
    }

    public String generateClassCode(File templateFile) throws Exception {
        buffer = readFile(templateFile);
        codeBuilder = new CodeBuilder(buffer.capacity());
        currentLine = new StringBuilder();

        String absPath = templateFile.getAbsolutePath();
        int index = absPath.indexOf(basePackagePath);
        if (index == -1) {
            throw new Exception("Cannot locate base views package within specific HTML file path.");
        }
        index += basePackagePath.length();

        String s = absPath.substring(index, absPath.length() - templateFile.getName().length());

        String packageName = basePackage + s.replace(File.separatorChar, '.');

        codeBuilder.appendLine("package " + packageName + ".gen;");
        codeBuilder.appendLine();
        codeBuilder.appendLine("import org.featherj.View;");
        codeBuilder.appendLine();
        codeBuilder.appendLine("public class " + FilenameUtils.removeExtension(templateFile.getName()) + " extends View {");
        codeBuilder.appendLine();
        codeBuilder.indent();
        codeBuilder.appendLine("public String getView() {");
        codeBuilder.indent();
        codeBuilder.appendLine("StringBuilder view = new StringBuilder();");

        int limit = buffer.limit();
        for (currentIndex = 0; currentIndex < limit; currentIndex++) {
            currentCh = buffer.get(currentIndex);
            if (currentIndex + 1 < limit) {
                lookAhead = buffer.get(currentIndex + 1);
            }
            process();
            prevCh = currentCh;
        }
        codeBuilder.outdent();
        codeBuilder.appendLine("}");
        codeBuilder.outdent();
        codeBuilder.append("}");

        return codeBuilder.toString();
    }

    private void commitLine() {
        if (!javaCodeMode || echoMode) {
            codeBuilder.append("view.append(" + (echoMode ? "( " : "\""));
        }
        codeBuilder.append(currentLine);
        if (!javaCodeMode || echoMode) {
            codeBuilder.append((echoMode ? ").toString() " : "\"") + ");");
        }
        codeBuilder.appendLine();
        currentLine = new StringBuilder();
    }

    private void process() {
        switch (currentCh) {
            case '\r':
                break;

            case '\\':
                currentLine.append("\\\\");
                break;
            case '"':
                currentLine.append("\\\"");
                break;

            case '\n':
                commitLine();
                break;

            case '<':
                if (lookAhead == '%') {
                    commitLine();
                    javaCodeMode = true;
                    currentIndex++;
                    if (currentIndex + 1 < buffer.limit()) {
                        char lookAhead2 = buffer.get(currentIndex + 1);
                        if (lookAhead2 == '=') {
                            currentIndex++;
                            echoMode = true;
                        }
                    }
                }
                else {
                    currentLine.append(currentCh);
                }
                break;

            case '%':
                if (prevCh == '\\') {
                    currentLine.append(currentCh);
                }
                else {
                    if (lookAhead == '>') {
                        commitLine();
                        javaCodeMode = false;
                        echoMode = false;
                        currentIndex++;
                    }
                }
                break;

            default:
                currentLine.append(currentCh);
                break;
        }
    }

    private static CharBuffer readFile(File templateFile) throws IOException {
        FileInputStream stream = new FileInputStream(templateFile);
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb);
        }
        finally {
            stream.close();
        }
    }

}
