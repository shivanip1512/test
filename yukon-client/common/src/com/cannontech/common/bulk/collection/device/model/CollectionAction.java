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

import static com.cannontech.common.bulk.collection.device.model.CollectionActionProcess.DB;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionProcess.CRE;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.google.common.collect.Sets;

public enum CollectionAction {
    
    SEND_COMMAND(CRE, true, SUCCESS, FAILURE),
    READ_ATTRIBUTE(CRE, false, SUCCESS, FAILURE, UNSUPPORTED),
    LOCAL_ROUTE(CRE, true, SUCCESS, FAILURE),
    CONNECT(CRE, true, CONNECTED, ARMED, DISCONNECTED, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    DISCONNECT(CRE, true, CONNECTED, ARMED, DISCONNECTED, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    ARM(CRE, true, CONNECTED, ARMED, DISCONNECTED, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    DEMAND_RESET(CRE, true, CONFIRMED, UNCONFIRMED, FAILURE, UNSUPPORTED, CANCELED),
    SEND_CONFIG(CRE, true, SUCCESS, FAILURE, UNSUPPORTED),
    READ_CONFIG(CRE, true, SUCCESS, FAILURE, UNSUPPORTED),
    ARCHIVE_DATA_ANALISYS(DB, false, SUCCESS, FAILURE),
    MASS_CHANGE(DB, false, SUCCESS, FAILURE),
    CHANGE_TYPE(DB, false, SUCCESS, FAILURE),
    MASS_DELETE(DB, false, SUCCESS, FAILURE),
    ADD_POINTS(DB, false, SUCCESS, FAILURE),
    UPDATE_POINTS(DB, false, SUCCESS, FAILURE),
    REMOVE_POINTS(DB, false, SUCCESS, FAILURE),
    ASSIGN_CONFIG(DB, false, SUCCESS, FAILURE),
    UNNASSGN_CONFIG(DB, false, SUCCESS, FAILURE);
    
    private boolean cancelable;
    private CollectionActionProcess process;
    
    private Set<CollectionActionDetail> details;

    private CollectionAction(CollectionActionProcess process, boolean cancelable, CollectionActionDetail... details) {
        this.process = process;
        this.cancelable = cancelable;
        this.details = Collections.unmodifiableSet(Sets.newHashSet(details));
    }

    public Set<CollectionActionDetail> getDetails() {
        return details;
    }

    public boolean isCancelable() {
        return cancelable;
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
    
    public CollectionActionDetail getSuccessDetail() {
        CollectionActionDetail detail = CollectionActionDetail.SUCCESS;
        switch (this) {
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
}
