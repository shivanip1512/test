package com.cannontech.dr.ecobee.message.partial;

import java.util.Collection;
import java.util.Collections;

import com.cannontech.dr.ecobee.message.EcobeeJsonSerializers.FROM_SELECTION_TYPE;
import com.cannontech.dr.ecobee.message.EcobeeJsonSerializers.TO_SELECTION_TYPE;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * Describes a selection of Ecobee thermostats.
 * 
 * https://www.ecobee.com/home/developer/api/documentation/v1/objects/Selection.shtml
 */
public class Selection {
    private final SelectionType selectionType;
    private final Collection<String> serialNumbers;

    @JsonSerialize(using = TO_SELECTION_TYPE.class)
    @JsonDeserialize(using = FROM_SELECTION_TYPE.class)
    public static enum SelectionType {
        REGISTERED("registered"), // selects all registered thermostats
        THERMOSTATS("thermostats"), // select individual thermostats by csv string
        MANAGEMENT_SET("managementSet"); // select all thermostats in a management set

        private String ecobeeString;
        private static final ImmutableMap<String, SelectionType> ecobeeStrings;

        static {
            final Builder<String, SelectionType> b = ImmutableMap.builder();
            for (SelectionType selectionTpe : values()) {
                b.put(selectionTpe.ecobeeString, selectionTpe);
            }
            ecobeeStrings = b.build();
        }

        private SelectionType(String ecobeeString) {
            this.ecobeeString = ecobeeString;
        }

        public static SelectionType fromEcobeeString(String ecobeeString) {
            return ecobeeStrings.get(ecobeeString);
        }

        public String getEcobeeString() {
            return ecobeeString;
        }
    }

    /**
     * @param selectionType The type of selection being used. This determines what values must be present in
     *        selectionMatch.
     * @param selectionMatch Contains the criteria that ecobee uses to determine what thermostats match the
     *        selection.
     */
    @JsonCreator
    public Selection(@JsonProperty("selectionType") SelectionType selectionType,
            @JsonProperty("selectionMatch") Collection<String> serialNumbers) {
        this.selectionType = selectionType;
        this.serialNumbers = ImmutableSet.copyOf(serialNumbers);
    }

    public Selection(SelectionType selectionType, String serialNumber) {
        this(selectionType, Collections.singleton(serialNumber));
    }

    public SelectionType getSelectionType() {
        return selectionType;
    }

    @JsonGetter("selectionMatch")
    public Collection<String> getSerialNumbers() {
        return serialNumbers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((selectionType == null) ? 0 : selectionType.hashCode());
        result = prime * result + ((serialNumbers == null) ? 0 : serialNumbers.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Selection other = (Selection) obj;
        if (selectionType != other.selectionType) {
            return false;
        }
        if (serialNumbers == null) {
            if (other.serialNumbers != null) {
                return false;
            }
        } else if (!serialNumbers.equals(other.serialNumbers)) {
            return false;
        }
        return true;
    }
}
