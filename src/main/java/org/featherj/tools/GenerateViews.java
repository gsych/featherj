package org.featherj.tools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.featherj.templates.TemplateEngine;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

public class GenerateViews {

    public void generate(String srcBasePath, String relativeViewsPath) throws Exception {
        File baseViewsPath = new File(srcBasePath + File.separatorChar + relativeViewsPath);
        if (!baseViewsPath.exists()) {
            throw new IllegalArgumentException("Specified base path doesn't exist: " + baseViewsPath.getAbsolutePath());
        }

        Collection<File> viewFiles = FileUtils.listFiles(baseViewsPath,
                FileFilterUtils.suffixFileFilter(".html", IOCase.INSENSITIVE), FileFilterUtils.trueFileFilter());

        for (File viewFile : viewFiles) {
            TemplateEngine engine = new TemplateEngine(relativeViewsPath);
            String str = engine.generateClassCode(viewFile);

            File file = FileUtils.getFile(viewFile.getParentFile(), "gen");
            file.mkdirs();
            file = new File(file, FilenameUtils.removeExtension(viewFile.getName()) + ".java");
            FileWriter writer = new FileWriter(file);
            try {
                writer.write(str);
            }
            finally {
                writer.close();
            }
        }
    }

    public static void main(String[] args) throws Exception{
        if (args.length != 2) {
            throw new IllegalArgumentException(
                "Please specify a base path to the sources (e.g. \"src/main/java\") and a (package) path to the views base directory (e.g. \"com/app/views\").");
        }

        String srcBasePath = args[0];
        String relativeViewsPath = args[1];

        GenerateViews gen = new GenerateViews();
        gen.generate(srcBasePath, relativeViewsPath);

    }
}
