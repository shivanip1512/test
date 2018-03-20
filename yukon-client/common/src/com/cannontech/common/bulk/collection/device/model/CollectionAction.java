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
import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.DEVICE_ERROR_TEXT;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.POINT_DATA;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.TIMESTAMP;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionProcess.CRE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionProcess.DB;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public enum CollectionAction implements DisplayableEnum {

    SEND_COMMAND(CRE, getLogEntries(POINT_DATA, DEVICE_ERROR_TEXT), SUCCESS, FAILURE, CANCELED),
    READ_ATTRIBUTE(CRE, getLogEntries(POINT_DATA, DEVICE_ERROR_TEXT), SUCCESS, FAILURE, CANCELED, UNSUPPORTED),
    LOCATE_ROUTE(CRE, null, SUCCESS, FAILURE, CANCELED),
    CONNECT(CRE, getLogEntries(TIMESTAMP), CONNECTED, ARMED, DISCONNECTED, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    DISCONNECT(CRE, getLogEntries(TIMESTAMP), CONNECTED, ARMED, DISCONNECTED, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    ARM(CRE, getLogEntries(TIMESTAMP), CONNECTED, ARMED, DISCONNECTED, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    DEMAND_RESET(CRE, null, CONFIRMED, UNCONFIRMED, FAILURE, UNSUPPORTED, CANCELED),
    SEND_CONFIG(CRE, null, SUCCESS, FAILURE, UNSUPPORTED, CANCELED),
    READ_CONFIG(CRE, null, SUCCESS, FAILURE, UNSUPPORTED, CANCELED),
    ARCHIVE_DATA_ANALYSIS(DB, null, SUCCESS, FAILURE),
    MASS_CHANGE(DB, getLogEntries(DEVICE_ERROR_TEXT), SUCCESS, FAILURE),
    CHANGE_TYPE(DB, getLogEntries(DEVICE_ERROR_TEXT), SUCCESS, FAILURE),
    MASS_DELETE(DB, getLogEntries(DEVICE_ERROR_TEXT), SUCCESS, FAILURE),
    ADD_POINTS(DB, getLogEntries(DEVICE_ERROR_TEXT), SUCCESS, FAILURE),
    UPDATE_POINTS(DB, getLogEntries(DEVICE_ERROR_TEXT), SUCCESS, FAILURE),
    REMOVE_POINTS(DB, getLogEntries(DEVICE_ERROR_TEXT), SUCCESS, FAILURE),
    ASSIGN_CONFIG(DB, getLogEntries(DEVICE_ERROR_TEXT), SUCCESS, FAILURE),
    UNASSIGN_CONFIG(DB, getLogEntries(DEVICE_ERROR_TEXT), SUCCESS, FAILURE);

    private CollectionActionProcess process;

    private List<CollectionActionDetail> details;
    private Set<CollectionActionOptionalLogEntry> optionalLogEntries;

    private CollectionAction(CollectionActionProcess process, Set<CollectionActionOptionalLogEntry> optionalLogEntries,
            CollectionActionDetail... details) {
        this.process = process;
        this.details = Collections.unmodifiableList(Lists.newArrayList(details));
        this.optionalLogEntries = optionalLogEntries;
    }

    private static Set<CollectionActionOptionalLogEntry> getLogEntries(CollectionActionOptionalLogEntry... entries) {
        return Collections.unmodifiableSet(Sets.newHashSet(entries));
    }

    public List<CollectionActionDetail> getDetails() {
        return details;
    }

    public boolean contains(CollectionActionOptionalLogEntry entry) {
        return optionalLogEntries != null && optionalLogEntries.contains(entry);
    }

    public boolean isCancelable() {
        return details.contains(CANCELED);
    }

    public CollectionActionProcess getProcess() {
        return process;
    }

    public List<CommandRequestUnsupportedType> getCreUnsupportedTypes() {
        return this.getDetails().stream().filter(
            detail -> detail.getSummary() == CollectionActionDetailSummary.NOT_ATTEMPTED).map(
                detail -> detail.getCreUnsupportedType()).collect(Collectors.toList());
    }

    public CollectionActionDetail getDetail(CommandRequestUnsupportedType unsupportedType) {
        return this.getDetails().stream().filter(d -> d.getCreUnsupportedType() == unsupportedType).findFirst().get();
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.bulk.collectionAction." + name();
    }
}
