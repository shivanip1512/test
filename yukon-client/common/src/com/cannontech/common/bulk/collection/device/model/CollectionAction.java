package com.cannontech.common.bulk.collection.device.model;

import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.ARMED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.CANCELED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.CONFIRMED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.CONNECTED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.DISCONNECTED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.FAILURE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.NOT_CONFIGURED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.SUCCESS;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.UNCONFIRMED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.UNSUPPORTED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionProcess.CRE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionProcess.DB;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.Lists;

public enum CollectionAction implements DisplayableEnum {
    
    SEND_COMMAND(CRE, SUCCESS, FAILURE, CANCELED),
    READ_ATTRIBUTE(CRE, SUCCESS, FAILURE, UNSUPPORTED),
    LOCATE_ROUTE(CRE, SUCCESS, FAILURE, CANCELED),
    CONNECT(CRE, CONNECTED, ARMED, DISCONNECTED, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    DISCONNECT(CRE, CONNECTED, ARMED, DISCONNECTED, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    ARM(CRE, CONNECTED, ARMED, DISCONNECTED, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    DEMAND_RESET(CRE, CONFIRMED, UNCONFIRMED, FAILURE, UNSUPPORTED, CANCELED),
    SEND_CONFIG(CRE, SUCCESS, FAILURE, UNSUPPORTED, CANCELED),
    READ_CONFIG(CRE, SUCCESS, FAILURE, UNSUPPORTED, CANCELED),
    ARCHIVE_DATA_ANALYSIS(DB, SUCCESS, FAILURE),
    MASS_CHANGE(DB, SUCCESS, FAILURE),
    CHANGE_TYPE(DB, SUCCESS, FAILURE),
    MASS_DELETE(DB, SUCCESS, FAILURE),
    ADD_POINTS(DB, SUCCESS, FAILURE),
    UPDATE_POINTS(DB, SUCCESS, FAILURE),
    REMOVE_POINTS(DB,SUCCESS, FAILURE),
    ASSIGN_CONFIG(DB, SUCCESS, FAILURE),
    UNASSIGN_CONFIG(DB, SUCCESS, FAILURE);
    
    private CollectionActionProcess process;
    
    private List<CollectionActionDetail> details;

    private CollectionAction(CollectionActionProcess process, CollectionActionDetail... details) {
        this.process = process;
        this.details = Collections.unmodifiableList(Lists.newArrayList(details));
    }

    public List<CollectionActionDetail> getDetails() {
        return details;
    }

    public boolean isCancelable() {
        return details.contains(CANCELED);
    }

    public CollectionActionProcess getProcess() {
        return process;
    } 
    
    public List<CommandRequestUnsupportedType> getCreUnsupportedTypes() {
        return this.getDetails().stream()
            .filter(detail -> detail.getSummary() == CollectionActionDetailSummary.NOT_ATTEMPTED)
            .map(detail -> detail.getCreUnsupportedType())
            .collect(Collectors.toList());
    }
    
    public CollectionActionDetail getDetail(CommandRequestUnsupportedType unsupportedType) {
        return this.getDetails().stream()
                .filter(d -> d.getCreUnsupportedType() == unsupportedType)
                .findFirst().get();
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.bulk.collectionAction." + name();
    }
}
