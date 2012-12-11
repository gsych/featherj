package org.featherj;

import org.featherj.actions.ActionResult;

public class SimpleResult implements ActionResult {

    private int status;
    private View view;
    private String contentType = "text/html";

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
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
