package org.featherj;

import org.featherj.actions.ActionResult;
import org.featherj.routes.Route;
import org.featherj.routes.Router;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * This is the main (and most likely the only) entry point of the web application.
 * Inherit it and override {@link org.featherj.EntryServlet#routes()} to define possible
 * application routes.
 * Usually (if you don't have really special cases) you just define all possible routes
 * for your application. If browser is navigated to URL that can't be matched by a particular route
 * it will get "not found".
 */
public abstract class EntryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) {
        Router router = new Router(routes());
        ActionResult result = router.routeAndRun(req);
    }

    protected abstract Route[] routes();
}
