package org.featherj.routes;

import org.featherj.Request;
import org.featherj.actions.ActionResult;
import org.featherj.actions.ResourceFileResult;
import org.featherj.routes.params.RouteParam;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class StaticResourceFileRoute extends Route {

    public StaticResourceFileRoute(String urlPattern) throws UrlParseException {
        super(urlPattern, new RouteParam<?>[0]);
    }

    @Override
    public boolean matches(Request request) {
        if (!super.matches(request)) {
            return false;
        }

        try {
            URL url = getClass().getResource(request.getUrl());
            if (url == null) {
                return false;
            }
            File file = new File(url.toURI());
            return file.exists();
        }
        catch (Exception ignored) {
        }

        return false;
    }

    @Override
    public ActionResult runAction(Request request) throws Exception {
        URL url = getClass().getResource(request.getUrl());
        File file = new File(url.toURI());
        return new ResourceFileResult(getMimeType(url), file);
    }

    private String getMimeType(URL url) throws IOException {
//        URLConnection uc = url.openConnection();
//        return uc.gue
        return URLConnection.guessContentTypeFromName(url.getFile());
    }
}
