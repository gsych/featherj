package org.featherj.tests;

import junit.framework.Assert;
import org.featherj.Request;
import org.featherj.routes.Route;
import org.featherj.routes.Router;
import org.featherj.routes.UrlParseException;
import org.featherj.routes.params.IntParam;
import org.junit.Test;

public class TestRoutes {

    @Test
    public void testOneParamRouteMatch() throws UrlParseException {
        Route r = Router.route("/users/get/:id", new IntParam(":id"), null);

        Request request = new Request() {
            @Override
            public String getCompleteUrl() {
                return "/users/get/10";
            }
        };

        Assert.assertTrue(r.matches(request));
    }

    @Test
    public void testParameterlessRouteMatch() throws UrlParseException {
        Route r = Router.route("/users/get", null);

        Request request = new Request() {
            @Override
            public String getCompleteUrl() {
                return "/users/get";
            }
        };

        Assert.assertTrue(r.matches(request));
    }

    @Test
    public void testGETStyleParameterRouteMatch() throws UrlParseException {
        Route r = Router.route("/users/get?id=:id", new IntParam(":id"), null);

        Request request = new Request() {
            @Override
            public String getCompleteUrl() {
                return "/users/get?id=10";
            }
        };

        Assert.assertTrue(r.matches(request));
    }

    @Test
    public void test2GETStyleParameterRouteMatch() throws UrlParseException {
        Route r = Router.route("/users/get?id=:id&limit=:limit", new IntParam(":id"), new IntParam(":limit"), null);

        Request request = new Request() {
            @Override
            public String getCompleteUrl() {
                return "/users/get?id=10&limit=5";
            }
        };

        Assert.assertTrue(r.matches(request));
    }

    @Test
    public void test2ParamsRouteMatch() throws UrlParseException {
        Route r1 = Router.route("/users/get/:id", new IntParam(":id"), null);
        Route r2 = Router.route("/users/get/:id/limit/:limit", new IntParam(":id"), new IntParam(":limit"), null);

        Request request = new Request() {
            @Override
            public String getCompleteUrl() {
                return "/users/get/10/limit/5";
            }
        };

        Assert.assertFalse(r1.matches(request));
        Assert.assertTrue(r2.matches(request));
    }

    public void test2RoutesMatch() throws UrlParseException {
        Route r = Router.route("/users/get/:id/limit/:limit", new IntParam(":id"), new IntParam(":limit"), null);

        Request request = new Request() {
            @Override
            public String getCompleteUrl() {
                return "/users/get/10/limit/5";
            }
        };

        Assert.assertTrue(r.matches(request));
    }

    @Test
    public void testUnmatchedRoute() throws UrlParseException {
        Route r = Router.route("/users/get/:id", new IntParam(":id"), null);

        Request request = new Request() {
            @Override
            public String getCompleteUrl() {
                return "/users/store/";
            }
        };

        Assert.assertFalse(r.matches(request));
    }
}
