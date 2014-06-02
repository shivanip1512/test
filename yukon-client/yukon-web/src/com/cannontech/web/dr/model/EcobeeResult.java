package com.cannontech.web.dr.model;

import org.joda.time.DateTime;

import com.cannontech.common.util.Completable;

public class EcobeeResult implements Completable {
    private boolean complete;
    private DateTime start;
    private DateTime finish;

    public boolean isComplete() {
        return complete;
    }
}
