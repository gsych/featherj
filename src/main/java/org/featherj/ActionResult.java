package org.featherj;

public interface ActionResult {

    int getStatus();
    void setStatus(int status);

    View getView();
    void setView(View view);
}
