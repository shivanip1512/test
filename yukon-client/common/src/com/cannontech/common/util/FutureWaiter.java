package com.cannontech.common.util;

import java.util.concurrent.FutureTask;


public class FutureWaiter extends FutureTask<Object> {

    public FutureWaiter() {
        super(new Runnable() {
            @Override
            public void run() {
            }
        }, new Object());
    }
}
