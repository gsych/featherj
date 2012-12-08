package org.featherj.routes;

import org.featherj.Request;
import org.featherj.actions.ActionResult;
import org.featherj.routes.params.Param;

public abstract class Route {

    private final String urlPattern;
    private final Param<?>[] params;

    public Route(String urlPattern, Param<?>...params) {
        this.urlPattern = urlPattern;
        this.params = params;
    }

    public boolean matches(Request request) {
        return false;
    }

    public abstract ActionResult runAction(Request request) throws Exception;
}
