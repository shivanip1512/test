package com.cannontech.common.requests.runnable;

import org.jdom.Element;

public interface YukonJobRunnable extends Runnable {
    public boolean isFinished();
    public double getProgress();
    public void reportResults(Element element);
}
