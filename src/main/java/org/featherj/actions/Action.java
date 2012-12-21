package org.featherj.actions;

import org.featherj.Request;

public abstract class Action {

    public class NotImplementedActionException extends Exception {

    }

    public ActionResult run(Request request) throws Exception {
        throw new NotImplementedActionException();
    }
}
