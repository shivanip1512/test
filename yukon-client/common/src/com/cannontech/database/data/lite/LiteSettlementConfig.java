package com.cannontech.database.data.lite;

import com.cannontech.common.util.CtiUtilities;

public class LiteSettlementConfig extends LiteBase {

    private String fieldName = CtiUtilities.STRING_NONE;
    private String fieldValue = CtiUtilities.STRING_NONE;
    private String desc = CtiUtilities.STRING_NONE;
    private int refEntryID = 0;

    public LiteSettlementConfig() {
        super();
        setLiteType(LiteTypes.SETTLEMENT);
    }

    public LiteSettlementConfig(int configID_) {
        this();
        setLiteID(configID_);
    }

    public LiteSettlementConfig(int configID_, String fieldName_, String fieldValue_, String desc_, int refEntryID_) {
        this(configID_);
        setFieldName(fieldName_);
        setFieldValue(fieldValue_);
        setDescription(desc_);
        setRefEntryID(refEntryID_);
    }

    public int getConfigID() {
        return getLiteID();
    }

    public void setConfigID(int configID_) {
        setLiteID(configID_);
    }

    public int getRefEntryID() {
        return refEntryID;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setRefEntryID(int i) {
        refEntryID = i;
    }

    public void setFieldName(String string) {
        fieldName = string;
    }

    public void setFieldValue(String string) {
        fieldValue = string;
    }

    public String getDescription() {
        return desc;
    }

    public void setDescription(String desc_) {
        desc = desc_;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((desc == null) ? 0 : desc.hashCode());
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        result = prime * result + ((fieldValue == null) ? 0 : fieldValue.hashCode());
        result = prime * result + refEntryID;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        LiteSettlementConfig other = (LiteSettlementConfig) obj;
        if (desc == null) {
            if (other.desc != null)
                return false;
        } else if (!desc.equals(other.desc))
            return false;
        if (fieldName == null) {
            if (other.fieldName != null)
                return false;
        } else if (!fieldName.equals(other.fieldName))
            return false;
        if (fieldValue == null) {
            if (other.fieldValue != null)
                return false;
        } else if (!fieldValue.equals(other.fieldValue))
            return false;
        if (refEntryID != other.refEntryID)
            return false;
        return true;
    }
}