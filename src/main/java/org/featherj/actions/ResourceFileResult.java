package org.featherj.actions;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class ResourceFileResult extends SimpleResult {
    private final File resourceFile;

    public ResourceFileResult(String mimeType, File resourceFile) {
        this.resourceFile = resourceFile;
        setMimeType(mimeType);
    }

    public File getResourceFile() {
        return resourceFile;
    }

    @Override
    public void callBuilder(ResponseBuilder builder, HttpServletResponse response) throws IOException {
        builder.build(this, response);
    }
}
