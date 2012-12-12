package org.featherj.routes;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;
import org.featherj.Request;
import org.featherj.actions.ActionResult;
import org.featherj.actions.ResourceFileResult;
import org.featherj.routes.params.Param;

import java.io.File;
import java.net.URL;
import java.util.Collection;

public class StaticResourceFileRoute extends Route {

    public StaticResourceFileRoute(String urlPattern) throws UrlParseException {
        super(urlPattern, new Param<?>[0]);
    }

    @Override
    public ActionResult runAction(Request request) throws Exception {
        URL url = getClass().getClassLoader().getResource("");
        File file = new File("");
        return new ResourceFileResult(getMimeType(file), file);
    }

    private MimeType getMimeType(File file) {
        MimeUtil2 util = new MimeUtil2();
        util.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
        util.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");

        Collection<MimeType> mimeTypes = util.getMimeTypes(file);
        for (MimeType t : mimeTypes) {
            return t;
        }

        return MimeUtil2.UNKNOWN_MIME_TYPE;
    }
}
