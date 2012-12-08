package org.featherj.routes;

import org.featherj.Request;
import org.featherj.actions.Action;
import org.featherj.actions.ActionResult;
import org.featherj.routes.params.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class Router {

    public Router(Route[] routes) {

    }

    public ActionResult routeAndRun(HttpServletRequest req) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public static Route route(String urlPattern, final Action action) {
        return new Route(urlPattern) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request);
            }
        };
    }

    public static <P1> Route route(String urlPattern, final Param<P1> p, final Action action) {
        return new Route(urlPattern, p) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request, p.extractValue(request));
            }
        };
    }

    public static <P1, P2> Route route(String urlPattern, final Param<P1> p1, final Param<P2> p2, final Action action) {
        return new Route(urlPattern, p1, p2) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request, p1.extractValue(request), p2.extractValue(request));
            }
        };
    }

    public static <P1, P2, P3> Route route(String urlPattern, final Param<P1> p1, final Param<P2> p2, final Param<P3> p3, final Action action) {
        return new Route(urlPattern, p1, p2, p3) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request, p1.extractValue(request), p2.extractValue(request), p3.extractValue(request));
            }
        };
    }

    public static <P1, P2, P3, P4> Route route(String urlPattern, final Param<P1> p1, final Param<P2> p2, final Param<P3> p3, final Param<P4> p4, final Action action) {
        return new Route(urlPattern, p1, p2, p3, p4) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request, p1.extractValue(request), p2.extractValue(request), p3.extractValue(request), p4.extractValue(request));
            }
        };
    }

    public static <P1, P2, P3, P4, P5> Route route(String urlPattern, final Param<P1> p1, final Param<P2> p2, final Param<P3> p3, final Param<P4> p4, final Param<P5> p5, final Action action) {
        return new Route(urlPattern, p1, p2, p3, p4, p5) {
            @Override
            public ActionResult runAction(Request request) throws Exception {
                return action.run(request, p1.extractValue(request), p2.extractValue(request), p3.extractValue(request), p4.extractValue(request), p5.extractValue(request));
            }
        };
    }
}
