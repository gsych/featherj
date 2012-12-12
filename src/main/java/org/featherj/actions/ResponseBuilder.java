package org.featherj.actions;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * (Visitor pattern) An interface of a class that can build response body
 * or in any other way translate {@link ActionResult} implementations into
 * a response to a client.
 */
public interface ResponseBuilder {
    void build(SimpleResult result, HttpServletResponse response) throws IOException;
    void build(ResourceFileResult result, HttpServletResponse response) throws IOException;
}
