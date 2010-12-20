package com.cannontech.common.device.commands;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;

public class WaitableCommandCompletionCallbackFactory {
    private int betweenResultsMaxDelay = 60;
    private int totalMaxDelay = 180;

    private ConfigurationSource configurationSource;

    @PostConstruct
    public void initialize() {
        betweenResultsMaxDelay =
            configurationSource.getInteger("COMMAND_REQUEST_EXECUTOR_BETWEEN_RESULTS_MAX_DELAY",
                                           betweenResultsMaxDelay);
        totalMaxDelay = configurationSource.getInteger("COMMAND_REQUEST_EXECUTOR_TOTAL_MAX_DELAY",
                                                       totalMaxDelay);
    }

    public <T> WaitableCommandCompletionCallback<T> createWaitable(CommandCompletionCallback<T> delegate) {
        return new WaitableCommandCompletionCallback<T>(delegate, betweenResultsMaxDelay, totalMaxDelay);
    }

    @ManagedAttribute
    public int getBetweenResultsMaxDelay() {
        return betweenResultsMaxDelay;
    }

    @ManagedAttribute
    public void setBetweenResultsMaxDelay(int betweenResultsMaxDelay) {
        this.betweenResultsMaxDelay = betweenResultsMaxDelay;
    }

    @ManagedAttribute
    public int getTotalMaxDelay() {
        return totalMaxDelay;
    }

    @ManagedAttribute
    public void setTotalMaxDelay(int totalMaxDelay) {
        this.totalMaxDelay = totalMaxDelay;
    }

    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
}
