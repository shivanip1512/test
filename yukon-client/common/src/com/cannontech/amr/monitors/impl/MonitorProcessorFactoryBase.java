package com.cannontech.amr.monitors.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.core.monitors.RichPointDataListenerFactory;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public abstract class MonitorProcessorFactoryBase<T extends PointMonitor> implements
        RichPointDataListenerFactory {

    @Override
    public List<RichPointDataListener> createListeners() {
        List<T> allMonitors = getAllMonitors();
        List<RichPointDataListener> result = Lists.newArrayListWithExpectedSize(allMonitors.size());
        for (T monitor : allMonitors) {
            if (monitor.getEvaluatorStatus() == MonitorEvaluatorStatus.ENABLED) {
                RichPointDataListener pointDataListner = createPointListener(monitor);
                result.add(pointDataListner);
            }
        }
        return result;
    }

    protected abstract List<T> getAllMonitors();

    protected abstract RichPointDataListener createPointListener(T monitor);

}
