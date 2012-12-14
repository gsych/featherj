package org.featherj.templates;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class TemplateEngine {

    private final String basePackagePath;
    private final String basePackage;

    public TemplateEngine(String basePackagePath) {
        this.basePackagePath = basePackagePath;
        this.basePackage = basePackagePath.replace(File.separatorChar, '.');
    }

    public String generateClassCode(File templateFile) throws Exception {
        CharBuffer buffer = readFile(templateFile);

        String absPath = templateFile.getAbsolutePath();
        int index = absPath.indexOf(basePackagePath);
        if (index == -1) {
            throw new Exception("Cannot locate base views package within specific HTML file path.");
        }
        index += basePackagePath.length();

        String s = absPath.substring(index, absPath.length() - templateFile.getName().length());

        String packageName = basePackage + s.replace(File.separatorChar, '.');
        if (packageName != null && packageName.charAt(packageName.length() - 1) != '.') {
            packageName += '.';
        }
        packageName += "gen";

        TemplateParser parser = new TemplateParser(buffer, packageName, FilenameUtils.removeExtension(templateFile.getName()));
        return parser.parse();
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
