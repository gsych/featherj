package org.featherj.actions;

import org.featherj.View;

public interface ActionResult {

    int getStatus();
    void setStatus(int status);

    View getView();
    void setView(View view);
}
