package com.cannontech.common.bulk.collection.device.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.common.YukonColorPallet;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;

/**
 * This enum defines collection action buckets.
 */
public enum CollectionActionDetail implements DisplayableEnum {
    SUCCESS(CollectionActionDetailSummary.SUCCESS, null, YukonColorPallet.GREEN.getHexValue()),  //green
    FAILURE(CollectionActionDetailSummary.FAILURE, null, YukonColorPallet.WINE.getHexValue()),  //red
    UNSUPPORTED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.UNSUPPORTED, YukonColorPallet.GRAY.getHexValue()),  //grey
    CONNECTED(CollectionActionDetailSummary.SUCCESS, null, YukonColorPallet.GREEN.getHexValue()),  //green
    ARMED(CollectionActionDetailSummary.SUCCESS, null, YukonColorPallet.SAGE.getHexValue()),  //light green
    DISCONNECTED(CollectionActionDetailSummary.SUCCESS, null, YukonColorPallet.ORANGE.getHexValue()),  //orange
    // NOT_CONFIGURED means the device was not in a proper state before the action was attempted so we were not able to perform the action.
    NOT_CONFIGURED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.NOT_CONFIGURED, YukonColorPallet.BLUE.getHexValue()),  //blue
    CANCELED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.CANCELED, YukonColorPallet.SKY.getHexValue()), //light blue
    CONFIRMED(CollectionActionDetailSummary.SUCCESS, null, YukonColorPallet.GREEN.getHexValue()), //green
    UNCONFIRMED(CollectionActionDetailSummary.SUCCESS, null, YukonColorPallet.LIGHT_GREY.getHexValue()),  //light grey
    ALREADY_CONFIGURED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.ALREADY_CONFIGURED, YukonColorPallet.SILVER.getHexValue()); //silver

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
    
    public static CollectionActionDetail getSuccessDetail(CollectionAction action) {
        CollectionActionDetail detail = CollectionActionDetail.SUCCESS;
        switch (action) {
        case CONNECT:
            detail = CollectionActionDetail.CONNECTED;
            break;
        case DISCONNECT:
            detail = CollectionActionDetail.DISCONNECTED;
            break;
        case ARM:
            detail = CollectionActionDetail.ARMED;
            break;
        default:
            break;
        }
        return detail;
    }
    
    public static CollectionActionDetail getDisconnectDetail(DisconnectCommand command){
        CollectionActionDetail detail = null;
        switch (command) {
        case CONNECT:
            detail = CollectionActionDetail.CONNECTED;
            break;
        case DISCONNECT:
            detail = CollectionActionDetail.DISCONNECTED;
            break;
        case ARM:
            detail = CollectionActionDetail.ARMED;
            break;
        default:
            break;
        }
        return detail;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.collectionActions.collectionActionDetail." + name();
    }
}
