package com.cannontech.common.bulk.collection.device.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;

public enum CollectionActionDetail implements DisplayableEnum {
    SUCCESS(CollectionActionDetailSummary.SUCCESS, null, "#009933"),  //green
    FAILURE(CollectionActionDetailSummary.FAILURE, null, "#d14836"),  //red
    UNSUPPORTED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.UNSUPPORTED, "#888888"),  //grey
    CONNECTED(CollectionActionDetailSummary.SUCCESS, null, "#009933"),  //green
    ARMED(CollectionActionDetailSummary.SUCCESS, null, "#5cb85c"),  //light green
    DISCONNECTED(CollectionActionDetailSummary.SUCCESS, null, "#ec971f"),  //orange
    NOT_CONFIGURED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.NOT_CONFIGURED, "#4d90fe"),  //blue
    CANCELED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.CANCELED, "#849ddf"), //light blue
    CONFIRMED(CollectionActionDetailSummary.SUCCESS, null, "#009933"), //green
    UNCONFIRMED(CollectionActionDetailSummary.SUCCESS, null, "#d3d3d3");  //light grey

    private CollectionActionDetailSummary summary;
    private CommandRequestUnsupportedType unsupportedType;
    private String color;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;

    private CollectionActionDetail(CollectionActionDetailSummary summary, CommandRequestUnsupportedType unsupportedType, String color) {
        this.summary = summary;
        this.unsupportedType = unsupportedType;
        this.color = color;
    }

    public CollectionActionDetailSummary getSummary() {
        return summary;
    }
    
    public CommandRequestUnsupportedType getCreUnsupportedType() {
        return unsupportedType;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.bulk.collectionActionDetail." + name();
    }
}
