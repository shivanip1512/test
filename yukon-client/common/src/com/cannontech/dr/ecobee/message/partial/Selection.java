package com.cannontech.dr.ecobee.message.partial;

import java.util.Collection;
import java.util.Collections;

import com.cannontech.dr.JsonSerializers.FROM_BASIC_CSV;
import com.cannontech.dr.JsonSerializers.FROM_SELECTION_TYPE;
import com.cannontech.dr.JsonSerializers.TO_BASIC_CSV;
import com.cannontech.dr.JsonSerializers.TO_SELECTION_TYPE;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown=true)
public class Selection {
    private final SelectionType selectionType;
    private final Collection<String> selectionMatch;

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
            @JsonDeserialize(using=FROM_BASIC_CSV.class) @JsonProperty("selectionMatch") Collection<String> selectionMatch) {
        this.selectionType = selectionType;
        this.selectionMatch = ImmutableSet.copyOf(selectionMatch);
    }

    public Selection(SelectionType selectionType, String selectionMatch) {
        this(selectionType, Collections.singleton(selectionMatch));
    }

    public SelectionType getSelectionType() {
        return selectionType;
    }

    @JsonGetter("selectionMatch")
    @JsonSerialize(using=TO_BASIC_CSV.class)
    public Collection<String> getSelectionMatch() {
        return selectionMatch;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((selectionType == null) ? 0 : selectionType.hashCode());
        result = prime * result + ((selectionMatch == null) ? 0 : selectionMatch.hashCode());
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
        if (selectionMatch == null) {
            if (other.selectionMatch != null) {
                return false;
            }
        } else if (!selectionMatch.equals(other.selectionMatch)) {
            return false;
        }
        return true;
    }
}
