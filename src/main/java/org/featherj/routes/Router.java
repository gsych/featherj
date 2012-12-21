package org.featherj.routes;

import org.featherj.Request;
import org.featherj.RequestImpl;
import org.featherj.actions.Action;
import org.featherj.actions.ActionResult;
import org.featherj.routes.params.RouteParam;

import javax.servlet.http.HttpServletRequest;

public class Router {

    public class RouteNotFoundException extends Exception {
        public RouteNotFoundException(String message) {
            super(message);
        }
    }

    private final Route[] routes;

    public Router(Route[] routes) {
        this.routes = routes;
    }

    public ActionResult routeAndRun(final HttpServletRequest req) throws Exception {
        RequestImpl request = new RequestImpl(req);
        for (Route r : routes) {
            if (r.matches(request)) {
                r.fillParams(request);
                return r.runAction(request);
            }
        }

        throw new RouteNotFoundException("Cannot find route for \"" + request.getUrl() + "\"");
    }

    public static Route resourceRoute(String urlPattern) throws UrlParseException {
        return new StaticResourceFileRoute(urlPattern);
    }

    public static Route route(final Action action, String urlPattern, RouteParam... params) throws UrlParseException {
        return new Route(urlPattern, params) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request);
            }
        };
    }
}
