package com.cannontech.common.util;

import java.util.Date;

public interface TimeSource {
    public Date getCurrentTime();
    public long getCurrentMillis();
}
