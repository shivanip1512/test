package com.cannontech.common.bulk.collection.device.model;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;

/**
 * This enum defines collection action buckets.
 */
public enum CollectionActionDetail implements DisplayableEnum {
    SUCCESS(CollectionActionDetailSummary.SUCCESS, null, YukonColorPalette.GREEN),
    FAILURE(CollectionActionDetailSummary.FAILURE, null, YukonColorPalette.RED),
    UNSUPPORTED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.UNSUPPORTED, YukonColorPalette.GRAY),
    INVALID_STATE(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.INVALID_STATE, YukonColorPalette.YELLOW),
    CONNECTED(CollectionActionDetailSummary.SUCCESS, null, YukonColorPalette.GREEN),
    ARMED(CollectionActionDetailSummary.SUCCESS, null, YukonColorPalette.SAGE),
    DISCONNECTED(CollectionActionDetailSummary.SUCCESS, null, YukonColorPalette.ORANGE),
    // NOT_CONFIGURED means the device was not in a proper state before the action was attempted so we were not able to perform the action.
    NOT_CONFIGURED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.NOT_CONFIGURED, YukonColorPalette.BLUE),
    CANCELED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.CANCELED, YukonColorPalette.SKY),
    CONFIRMED(CollectionActionDetailSummary.SUCCESS, null, YukonColorPalette.GREEN),
    UNCONFIRMED(CollectionActionDetailSummary.SUCCESS, null, YukonColorPalette.GRAY_LIGHT),
    ALREADY_CONFIGURED(CollectionActionDetailSummary.NOT_ATTEMPTED, CommandRequestUnsupportedType.ALREADY_CONFIGURED, YukonColorPalette.GRAY_LIGHT);

    private CollectionActionDetailSummary summary;
    private CommandRequestUnsupportedType unsupportedType;
    private YukonColorPalette color;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;

    private CollectionActionDetail(CollectionActionDetailSummary summary, CommandRequestUnsupportedType unsupportedType, YukonColorPalette color) {
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

    public YukonColorPalette getColor() {
        return color;
    }
    
    public String getColorHex() {
        return color.getHexValue();
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.collectionActions.collectionActionDetail." + name();
    }
}
