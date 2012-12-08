package org.featherj.routes.params;

import org.featherj.Request;

import java.util.regex.Pattern;

public class IntParam extends Param<Integer> {

    public IntParam(String key) {
        super(key);
    }

    public IntParam(String key, Pattern expr) {
        super(key, expr);
    }

    @Override
    public Integer extractValue(Request request) {
        return null;
    }
}
