package org.featherj.routes.params;

import org.featherj.Request;

import java.util.regex.Pattern;

public abstract class Param<T> {
    private final String key;
    private final Pattern expr;

    protected Param(String key) {
        this.key = key;
        expr = null;
    }

    public Param(String key, Pattern expr) {
        this.key = key;
        this.expr = expr;
    }

    public abstract T extractValue(Request request);
}
