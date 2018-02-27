package com.cannontech.common.bulk.collection.device.model;

import com.cannontech.common.device.commands.CommandRequestUnsupportedType;

public enum CollectionActionDetail {
    SUCCESS(CollectionActionDetailSummary.SUCCESS, null),
    FAILURE(CollectionActionDetailSummary.FAILURE, null),
    UNSUPPORTED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.UNSUPPORTED),
    CONNECTED(CollectionActionDetailSummary.SUCCESS, null),
    ARMED(CollectionActionDetailSummary.SUCCESS, null),
    DISCONNECTED(CollectionActionDetailSummary.SUCCESS, null),
    NOT_CONFIGURED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.NOT_CONFIGURED),
    CANCELED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.CANCELED),
    CONFIRMED(CollectionActionDetailSummary.SUCCESS, null),
    UNCONFIRMED(CollectionActionDetailSummary.SUCCESS, null);

    private CollectionActionDetailSummary summary;
    private CommandRequestUnsupportedType unsupportedType;

    private CollectionActionDetail(CollectionActionDetailSummary summary, CommandRequestUnsupportedType unsupportedType) {
        this.summary = summary;
        this.unsupportedType = unsupportedType;
    }

    public CollectionActionDetailSummary getSummary() {
        return summary;
    }
    
    public CommandRequestUnsupportedType getCreUnsupportedType() {
        return unsupportedType;
    }
}
