package com.cannontech.common.requests.runnable;

import org.jdom2.Element;
import com.cannontech.common.token.TokenStatus;

public interface YukonJob extends Runnable, TokenStatus {
    public double getProgress();
    public void reportResults(Element element);
}
