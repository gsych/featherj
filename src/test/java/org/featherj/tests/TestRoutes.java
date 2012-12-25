package org.featherj.tests;

import junit.framework.Assert;
import org.featherj.Request;
import org.featherj.RequestImpl;
import org.featherj.routes.Route;
import org.featherj.routes.Router;
import org.featherj.routes.UrlParseException;
import org.featherj.routes.params.IntRouteParam;
import org.featherj.routes.params.StringRouteParam;
import org.junit.Test;

public class TestRoutes {

    @Test
    public void testRootMatch() throws UrlParseException {
        Route r = Router.route(null, "/");

        Request request = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/";
            }
        };

        Assert.assertTrue(r.matches(request));
    }

    @Test
    public void testOneParamRouteMatch() throws UrlParseException {
        Route r = Router.route(null, "/users/get/:id", new IntRouteParam(":id"));

        Request request = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/users/get/10";
            }
        };

        Assert.assertTrue(r.matches(request));
    }

    @Test
    public void testParameterlessRouteMatch() throws UrlParseException {
        Route r = Router.route(null, "/users/get");

        Request request = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/users/get";
            }
        };

        Assert.assertTrue(r.matches(request));
    }

    @Test
    public void testGETStyleParameterRouteMatch() throws UrlParseException {
        Route r = Router.route(null, "/users/get?id=:id", new IntRouteParam(":id"));

        Request request = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/users/get?id=10";
            }
        };

        Assert.assertTrue(r.matches(request));
    }

    @Test
    public void test2GETStyleParameterRouteMatch() throws UrlParseException {
        Route r = Router.route(null, "/users/get?id=:id&limit=:limit", new IntRouteParam(":id"), new IntRouteParam(":limit"));

        Request request = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/users/get?id=10&limit=5";
            }
        };

        Assert.assertTrue(r.matches(request));
    }

    @Test
    public void test2ParamsRouteMatch() throws UrlParseException {
        Route r1 = Router.route(null, "/users/get/:id", new IntRouteParam(":id"));
        Route r2 = Router.route(null, "/users/get/:id/limit/:limit", new IntRouteParam(":id"), new IntRouteParam(":limit"));

        Request request = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/users/get/10/limit/5";
            }
        };

        Assert.assertFalse(r1.matches(request));
        Assert.assertTrue(r2.matches(request));
    }

    @Test
    public void test2RoutesMatch() throws UrlParseException {
        Route r = Router.route(null, "/users/get/:id/limit/:limit", new IntRouteParam(":id"), new IntRouteParam(":limit"));

        Request request = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/users/get/10/limit/5";
            }
        };

        Assert.assertTrue(r.matches(request));
    }

    @Test
    public void testUnmatchedRoute() throws UrlParseException {
        Route r = Router.route(null, "/users/get/:id", new IntRouteParam(":id"));

        Request request = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/users/store/";
            }
        };

        Assert.assertFalse(r.matches(request));
    }

    @Test
    public void testSimpleAsteriskMatch() throws UrlParseException {
        Route route = Router.route(null, "/css/*");

        Request request1 = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/css/my.css";
            }
        };
        Request request2 = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/css/lib/lib.css";
            }
        };
        Request request3 = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/js/my.js";
            }
        };

        Assert.assertTrue(route.matches(request1));
        Assert.assertTrue(route.matches(request2));
        Assert.assertFalse(route.matches(request3));
    }

    @Test
    public void testStringRouteParam() throws UrlParseException {
        Route r = Router.route(null, "/search/:query", new StringRouteParam(":query"));

        Request request = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/search/test,%20.net,%20windows%20phone,%20play!";
            }
        };

        Assert.assertTrue(r.matches(request));
    }

    @Test
    public void testStringRouteParamAsPart() throws UrlParseException {
        Route r = Router.route(null, "/search/:query/remainder-fixed-part/", new StringRouteParam(":query"));

        Request request = new RequestImpl(null) {
            @Override
            public String getUrl() {
                return "/search/test,%20.net,%20windows%20phone,%20play!/remainder-fixed-part/";
            }
        };

        Assert.assertTrue(r.matches(request));
    }
}
