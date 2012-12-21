package org.featherj;

public interface Request {

    /**
     * Gets request URL but without protocol, domain names, etc.
     * @return Request URL starting first "/" after domains (includes GET parameters)
     */
    String getUrl();

    /**
     * Returns request parameter (typed) value that was obtained during routing matching
     * procedure.
     *
     * @param name Name of a request parameter
     * @param <T> Parameter value type
     * @return
     */
    <T> T param(String name);
}