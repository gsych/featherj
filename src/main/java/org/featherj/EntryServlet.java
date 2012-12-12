package org.featherj;

import org.apache.commons.io.IOUtils;
import org.featherj.actions.ActionResult;
import org.featherj.actions.ResourceFileResult;
import org.featherj.actions.ResponseBuilder;
import org.featherj.actions.SimpleResult;
import org.featherj.routes.Route;
import org.featherj.routes.Router;
import org.featherj.routes.UrlParseException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This is the main (and most likely the only) entry point of the web application.
 * Inherit it and override {@link org.featherj.EntryServlet#routes()} to define possible
 * application routes.
 * Usually (if you don't have really special cases) you just define all possible routes
 * for your application. If browser is navigated to URL that can't be matched by a particular route
 * it will get "not found".
 */
public abstract class EntryServlet extends HttpServlet {

    private ResponseBuilder responseBuilder = new ResponseBuilder() {
        @Override
        public void build(SimpleResult result, HttpServletResponse response) throws IOException {
            response.setStatus(result.getStatus());
            View view = result.getView();
            if (view != null) {
                response.setContentType(result.getMimeType().toString());
                response.getWriter().print(result.getView().render());
            }
        }

        @Override
        public void build(ResourceFileResult result, HttpServletResponse response) throws IOException {
            //FIXME: call build((SimpleResult) result, response) when ViewResult is extracted from SimpleResult
            response.setStatus(result.getStatus());
            response.setContentType(result.getMimeType().toString());
            response.setContentLength(result.getContentLength());

            FileInputStream in = null;
            ServletOutputStream out = null;
            try {
                in = new FileInputStream(result.getResourceFile());
                out = response.getOutputStream();

                IOUtils.copy(in, out);
                out.flush();
            }
            finally {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
        }
    };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            //FIXME: implement error rendering
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            //FIXME: implement error rendering
            e.printStackTrace();
        }
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Router router = new Router(routes());
        ActionResult result = router.routeAndRun(req);
        result.callBuilder(responseBuilder, resp);
    }

    protected abstract Route[] routes() throws UrlParseException;
}