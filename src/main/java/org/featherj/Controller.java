package org.featherj;

import org.featherj.actions.ActionResult;

import javax.servlet.http.HttpServletResponse;

public class Controller {

    protected ActionResult ok() {
        return new SimpleResult(HttpServletResponse.SC_OK);
    }
    protected ActionResult ok(View view) {
        return new SimpleResult(HttpServletResponse.SC_OK, view);
    }
}
