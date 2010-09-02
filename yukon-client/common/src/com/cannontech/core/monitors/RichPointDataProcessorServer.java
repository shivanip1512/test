package com.cannontech.core.monitors;

import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.util.IfNotRunningExecutor;
import com.google.common.collect.ImmutableList;

public class RichPointDataProcessorServer {

    private volatile ImmutableList<RichPointDataListener> listeners = ImmutableList.of();
    private volatile Instant lastListenerRefresh = new Instant(0);
    private Duration minimumTimeBetweenRefreshes = Duration.standardMinutes(3);
    private RichPointDataListenerFactory listenerFactory;
    private IfNotRunningExecutor lazyExecutor = new IfNotRunningExecutor();

    public void pointDataReceived(RichPointData pointData) {
        refreshListeners();
        for (RichPointDataListener listener : listeners) {
            listener.pointDataReceived(pointData);
        }
    }

    @PostConstruct
    private void refreshListeners() {
        Instant nextRefresh = lastListenerRefresh.plus(minimumTimeBetweenRefreshes);
        if (nextRefresh.isBeforeNow()) {
            lazyExecutor.runIfNotAlreadyRunning(new Runnable() {
                @Override
                public void run() {
                    List<RichPointDataListener> newListeners = listenerFactory.createListeners();
                    listeners = ImmutableList.copyOf(newListeners);
                    lastListenerRefresh = new Instant();
                }
            });
        }
    }
    
    @Required
    public void setListenerFactory(RichPointDataListenerFactory listenerFactory) {
        this.listenerFactory = listenerFactory;
    }

}
