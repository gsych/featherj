package org.featherj.routes.params;

import java.util.regex.Pattern;

public class IntParam extends Param<Integer> {

    private Integer value;

    public IntParam(String key) {
        super(key, Pattern.compile("[1-9][0-9]*"));
    }

    @Override
    public void extractValue(String value) {
        this.value = Integer.parseInt(value);
    }

    @Override
    public void clearValue() {
        value = null;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
