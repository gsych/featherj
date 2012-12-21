package org.featherj.routes.params;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

public class StringRouteParam extends RouteParam<String> {

    private String value;

    public StringRouteParam(String key) {
        super(key, Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"));
    }

    @Override
    public void extractValue(String value) {
        try {
            this.value = URLDecoder.decode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException ignore) {
            // theoretically this is not possible, because regex above should catch
            // everything invalid
        }
    }

    @Override
    public void clearValue() {
        value = null;
    }

    @Override
    public String getValue() {
        return value;
    }
}