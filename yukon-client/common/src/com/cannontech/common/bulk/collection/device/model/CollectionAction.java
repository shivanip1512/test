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
import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.POINT_DATA;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.LAST_VALUE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.DEVICE_TYPE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.CONFIG_NAME;
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

/**
 * This enum defines Collection Action.
 * 1. Collection Action
 * 2. Possible collection action log file entries
 * 3. Possible buckets
 */
public enum CollectionAction implements DisplayableEnum {

    SEND_COMMAND(CRE, getLogEntries(POINT_DATA, LAST_VALUE), SUCCESS, FAILURE, CANCELED),
    READ_ATTRIBUTE(CRE, getLogEntries(POINT_DATA, LAST_VALUE), SUCCESS, FAILURE, CANCELED, UNSUPPORTED),
    LOCATE_ROUTE(CRE, getLogEntries(LAST_VALUE), SUCCESS, FAILURE, UNSUPPORTED, CANCELED),
    CONNECT(CRE, getLogEntries(POINT_DATA), CONNECTED, ARMED, DISCONNECTED, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    DISCONNECT(CRE, getLogEntries(POINT_DATA), CONNECTED, ARMED, DISCONNECTED, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    ARM(CRE, getLogEntries(POINT_DATA), CONNECTED, ARMED, DISCONNECTED, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    DEMAND_RESET(CRE, getLogEntries(POINT_DATA, LAST_VALUE), CONFIRMED, UNCONFIRMED, FAILURE, UNSUPPORTED, CANCELED),
    SEND_CONFIG(CRE, getLogEntries(LAST_VALUE), SUCCESS, FAILURE, UNSUPPORTED, CANCELED),
    READ_CONFIG(CRE, getLogEntries(LAST_VALUE), SUCCESS, FAILURE, UNSUPPORTED, CANCELED),
    VERIFY_CONFIG(CRE, getLogEntries(DEVICE_TYPE, CONFIG_NAME, LAST_VALUE), SUCCESS, FAILURE, UNSUPPORTED),
    CONFIGURE_DATA_STREAMING(CRE, null, SUCCESS, FAILURE, UNSUPPORTED, CANCELED),
    READ_DATA_STREAMING_CONFIG(CRE, null, SUCCESS, FAILURE, UNSUPPORTED, CANCELED),
    REMOVE_DATA_STREAMING(CRE, null, SUCCESS, FAILURE, NOT_CONFIGURED, UNSUPPORTED, CANCELED),
    MASS_CHANGE(DB, null, SUCCESS, FAILURE),
    CHANGE_TYPE(DB, null, SUCCESS, FAILURE),
    MASS_DELETE(DB, null, SUCCESS, FAILURE),
    ADD_POINTS(DB, null, SUCCESS, FAILURE),
    UPDATE_POINTS(DB, null, SUCCESS, FAILURE),
    REMOVE_POINTS(DB, null, SUCCESS, FAILURE),
    ASSIGN_CONFIG(DB, null, SUCCESS, FAILURE),
    UNASSIGN_CONFIG(DB, null, SUCCESS, FAILURE);

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
    
    public Set<CollectionActionOptionalLogEntry> getOptionalLogEntries() {
        return optionalLogEntries;
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
        return "yukon.web.modules.tools.collectionActions.collectionAction." + name();
    }
}
