package org.featherj.actions;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;
import org.featherj.View;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//FIXME: extract ViewResult and change EntryServlet.ResponseBuilder implementation (remove view rendering logic from there)
public class SimpleResult implements ActionResult {

    private int status;
    private View view;
    private MimeType mimeType = MimeUtil2.getFirstMimeType("text/html");
    private int contentLength;

    public SimpleResult() {
    }

    public SimpleResult(int status) {
        setStatus(status);
    }

    public SimpleResult(int status, View view) {
        this(status);
        setView(view);
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public MimeType getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public void callBuilder(ResponseBuilder builder, HttpServletResponse response) throws IOException {
        builder.build(this, response);
    }
}
