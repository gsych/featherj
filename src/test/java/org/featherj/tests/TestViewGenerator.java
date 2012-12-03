package org.featherj.tests;

import org.featherj.tools.GenerateViews;
import org.junit.Test;

public class TestViewGenerator {

    @Test
    public void thisAlwaysPasses() throws Exception {
        GenerateViews gen = new GenerateViews();
        gen.generate("src\\test\\java", "org\\featherj\\test\\views");
    }
}