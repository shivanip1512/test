package com.cannontech.common.device.commands;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.pao.PaoType;

public class WaitableCommandCompletionCallbackFactory {
    private int betweenResultsMaxDelay;
    private int totalMaxDelay;
    
    private static final int BETWEEN_RESULTS_MAX_DELAY_DEFAULT = 60;
    private static final int TOTAL_MAX_DELAY_DEFAULT = 180;
    private static final int RFN_DELAY_MULTIPLIER = 7;

    private ConfigurationSource configurationSource;

    @PostConstruct
    public void initialize() {
        betweenResultsMaxDelay =
            configurationSource.getInteger(
                "COMMAND_REQUEST_EXECUTOR_BETWEEN_RESULTS_MAX_DELAY",
                BETWEEN_RESULTS_MAX_DELAY_DEFAULT);
        
        totalMaxDelay = 
            configurationSource.getInteger(
                "COMMAND_REQUEST_EXECUTOR_TOTAL_MAX_DELAY",
                TOTAL_MAX_DELAY_DEFAULT);
    }

    private <T> WaitableCommandCompletionCallback<T> createWaitable(CommandCompletionCallback<T> delegate, int resultsDelay, int totalDelay) {
        return new WaitableCommandCompletionCallback<T>(delegate, resultsDelay, totalDelay);
    }
    
    public <T> WaitableCommandCompletionCallback<T> createWaitable(CommandCompletionCallback<T> delegate, PaoType paoType) {
        int resultsDelay = betweenResultsMaxDelay;
        int totalDelay = totalMaxDelay;

        if (paoType != null && paoType.isRfn()) {
            resultsDelay = RFN_DELAY_MULTIPLIER * BETWEEN_RESULTS_MAX_DELAY_DEFAULT;
            totalDelay = RFN_DELAY_MULTIPLIER * TOTAL_MAX_DELAY_DEFAULT;
        }

        return createWaitable(delegate, resultsDelay, totalDelay);
    }
    
    public <T> WaitableCommandCompletionCallback<T> createWaitable(CommandCompletionCallback<T> delegate) {
        return createWaitable(delegate, betweenResultsMaxDelay, totalMaxDelay);
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
