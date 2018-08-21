package com.cannontech.amr.monitors;

import com.cannontech.amr.MonitorEvaluatorStatus;

public interface PointMonitor {
    public MonitorEvaluatorStatus getEvaluatorStatus();
    
    public String getName();
    
    public String getGroupName();
}
