package org.featherj.routes.params;

import java.util.regex.Pattern;

public abstract class RouteParam<T> {
    private final String key;
    private final Pattern expr;

    public RouteParam(String key, Pattern expr) {
        this.key = key;
        this.expr = expr;
    }

    public String getKey() {
        return key;
    }

    public Pattern getExpr() {
        return expr;
    }

    public abstract void extractValue(String value);

    public abstract void clearValue();

    public abstract T getValue();
}
