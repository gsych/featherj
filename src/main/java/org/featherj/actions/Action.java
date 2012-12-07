package org.featherj.actions;

import org.featherj.Request;

public abstract class Action {

    public class NotImplementedActionException extends Exception {

    }

    public ActionResult run(Request request) throws Exception {
        throw new NotImplementedActionException();
    }

    public <P1> ActionResult run(Request request, P1 p1) throws Exception {
        throw new NotImplementedActionException();
    }

    public <P1, P2> ActionResult run(Request request, P1 p1, P2 p2) throws Exception {
        throw new NotImplementedActionException();
    }

    public <P1, P2, P3> ActionResult run(Request request, P1 p1, P2 p2, P3 p3) throws Exception {
        throw new NotImplementedActionException();
    }

    public <P1, P2, P3, P4> ActionResult run(Request request, P1 p1, P2 p2, P3 p3, P4 p4) throws Exception {
        throw new NotImplementedActionException();
    }

    public <P1, P2, P3, P4, P5> ActionResult run(Request request, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) throws Exception {
        throw new NotImplementedActionException();
    }
}
