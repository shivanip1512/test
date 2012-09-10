package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.util.CtiUtilities;

@YukonPao(tableName="YukonPaObject", idColumnName="PaObjectId")
public class CompleteYukonPao implements com.cannontech.common.pao.YukonPao {
    private PaoIdentifier paoIdentifier;

    private String paoName;
    private String description = CtiUtilities.STRING_DASH_LINE;
    private String statistics = CtiUtilities.STRING_DASH_LINE;
    private boolean disabled = false;

    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }

    public int getPaObjectId() {
        return paoIdentifier.getPaoId();
    }
    
    @YukonPaoField(columnName="Category")
    public PaoCategory getPaoCategory() {
        return paoIdentifier.getPaoType().getPaoCategory();
    }
    
    @YukonPaoField(columnName="PaoClass")
    public PaoClass getPaoClass() {
        return paoIdentifier.getPaoType().getPaoClass();
    }
    
    @YukonPaoField(columnName="Type")
    public PaoType getPaoType() {
        return paoIdentifier.getPaoType();
    }

    @YukonPaoField
    public String getPaoName() {
        return paoName;
    }

    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }

    @YukonPaoField
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @YukonPaoField(columnName="DisableFlag")
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @YukonPaoField(columnName="PaoStatistics")
    public String getStatistics() {
        return statistics;
    }

    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }

    @Override
    public String toString() {
        return "CompleteYukonPao [paoIdentifier=" + paoIdentifier + ", paoName=" + paoName
               + ", description=" + description + ", statistics=" + statistics + ", disabled="
               + disabled + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + (disabled ? 1231 : 1237);
        result = prime * result + ((paoIdentifier == null) ? 0 : paoIdentifier.hashCode());
        result = prime * result + ((paoName == null) ? 0 : paoName.hashCode());
        result = prime * result + ((statistics == null) ? 0 : statistics.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompleteYukonPao other = (CompleteYukonPao) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (disabled != other.disabled)
            return false;
        if (paoIdentifier == null) {
            if (other.paoIdentifier != null)
                return false;
        } else if (!paoIdentifier.equals(other.paoIdentifier))
            return false;
        if (paoName == null) {
            if (other.paoName != null)
                return false;
        } else if (!paoName.equals(other.paoName))
            return false;
        if (statistics == null) {
            if (other.statistics != null)
                return false;
        } else if (!statistics.equals(other.statistics))
            return false;
        return true;
    }
}
