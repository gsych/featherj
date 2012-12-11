package org.featherj.routes;

import org.featherj.Request;
import org.featherj.actions.Action;
import org.featherj.actions.ActionResult;
import org.featherj.routes.params.Param;

import javax.servlet.http.HttpServletRequest;

public class Router {

    private final Route[] routes;

    public Router(Route[] routes) {
        this.routes = routes;
    }

    public ActionResult routeAndRun(final HttpServletRequest req) {
        Request request = new Request() {
            @Override
            public String getCompleteUrl() {
                return req.getRequestURL().append("?").append(req.getQueryString()).toString();
            }
        };

        
    }

    public static Route route(String urlPattern, final Action action) throws UrlParseException {
        return new Route(urlPattern) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request);
            }
        };
    }

    public static <P1> Route route(String urlPattern, final Param<P1> p, final Action action) throws UrlParseException {
        return new Route(urlPattern, p) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request, p.getValue());
            }
        };
    }

    public static <P1, P2> Route route(String urlPattern, final Param<P1> p1, final Param<P2> p2, final Action action) throws UrlParseException {
        return new Route(urlPattern, p1, p2) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request, p1.getValue(), p2.getValue());
            }
        };
    }

    public static <P1, P2, P3> Route route(String urlPattern, final Param<P1> p1, final Param<P2> p2, final Param<P3> p3, final Action action) throws UrlParseException {
        return new Route(urlPattern, p1, p2, p3) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request, p1.getValue(), p2.getValue(), p3.getValue());
            }
        };
    }

    public static <P1, P2, P3, P4> Route route(String urlPattern, final Param<P1> p1, final Param<P2> p2, final Param<P3> p3, final Param<P4> p4, final Action action) throws UrlParseException {
        return new Route(urlPattern, p1, p2, p3, p4) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request, p1.getValue(), p2.getValue(), p3.getValue(), p4.getValue());
            }
        };
    }

    public static <P1, P2, P3, P4, P5> Route route(String urlPattern, final Param<P1> p1, final Param<P2> p2, final Param<P3> p3, final Param<P4> p4, final Param<P5> p5, final Action action) throws UrlParseException {
        return new Route(urlPattern, p1, p2, p3, p4, p5) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request, p1.getValue(), p2.getValue(), p3.getValue(), p4.getValue(), p5.getValue());
            }
        };
    }
}
