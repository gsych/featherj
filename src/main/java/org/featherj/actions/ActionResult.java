package org.featherj.actions;

import org.featherj.View;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ActionResult {

    int getStatus();
    void setStatus(int status);

    View getView();
    void setView(View view);

    String getMimeType();
    void setMimeType(String contentType);

    int getContentLength();
    void setContentLength(int len);

    /**
     * Simply calls corresponding {@link ResponseBuilder#build} method of specified builder.
     * (A part of visitor pattern).
     */
    void callBuilder(ResponseBuilder builder, HttpServletResponse response) throws IOException;
}
