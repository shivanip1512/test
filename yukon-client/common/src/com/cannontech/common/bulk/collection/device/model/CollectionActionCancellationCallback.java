package com.cannontech.common.bulk.collection.device.model;

import com.cannontech.common.device.commands.CommandCompletionCallback;

public class CollectionActionCancellationCallback {
    
    private StrategyType strategy;
    private CollectionActionStrategyCompletionCallback callback;
    private CommandCompletionCallback<?> commandCompletionCallback;
    
    public CollectionActionCancellationCallback(StrategyType strategy,
            CollectionActionStrategyCompletionCallback callback,
            CommandCompletionCallback<?> commandCompletionCallback) {
        this.strategy = strategy;
        this.callback = callback;
        this.commandCompletionCallback = commandCompletionCallback;
    }

    public CollectionActionCancellationCallback(StrategyType strategy,
            CollectionActionStrategyCompletionCallback callback) {
        this(strategy, callback, null);
    }

    public void cancel() {
        callback.complete(strategy);
    } 
    
    public StrategyType getStrategy() {
        return strategy;
    }

    public CollectionActionStrategyCompletionCallback getCallback() {
        return callback;
    }

    public CommandCompletionCallback<?> getCommandCompletionCallback() {
        return commandCompletionCallback;
    }
}
