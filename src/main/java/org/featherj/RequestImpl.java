package org.featherj;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class RequestImpl implements Request {

    private final HttpServletRequest req;
    private HashMap<String, Object> params = new HashMap<String, Object>();

    public RequestImpl(HttpServletRequest req) {
        this.req = req;
    }

    @Override
    public String getUrl() {
        String uri = req.getRequestURI();
        String queryStr = req.getQueryString();
        if (queryStr != null) {
            return uri + "?" + queryStr;
        }
        return uri;
    }

    public <T> T param(String name) {
        Object val = params.get(name);
        if (val == null) {
            throw new IllegalArgumentException(name);
        }
        return (T) val;
    }

    public void param(String name, Object value) {
        params.put(name, value);
    }
}
