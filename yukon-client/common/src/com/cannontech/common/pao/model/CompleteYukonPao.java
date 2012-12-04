package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.util.CtiUtilities;
import com.google.common.base.Objects;

@YukonPao(tableName="YukonPaObject", idColumnName="PaObjectId")
public class CompleteYukonPao implements com.cannontech.common.pao.YukonPao {
    private PaoIdentifier paoIdentifier;

    private String paoName;
    private String description = CtiUtilities.STRING_DASH_LINE;
    private String statistics = CtiUtilities.STRING_DASH_LINE;
    private boolean disabled = false;

    @Override
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
        return this.getClass().getSimpleName() + " [ CompleteYukonPao [paoIdentifier="
                + paoIdentifier + ", paoName=" + paoName + ", description=" + description
                + ", statistics=" + statistics + ", disabled=" + disabled + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), paoIdentifier, paoName, description, statistics, disabled);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof CompleteYukonPao) {
            if (!super.equals(object)) {
                return false;
            }
            CompleteYukonPao that = (CompleteYukonPao) object;
            return Objects.equal(this.paoIdentifier, that.paoIdentifier)
                    && Objects.equal(this.paoName, that.paoName)
                    && Objects.equal(this.description, that.description)
                    && Objects.equal(this.statistics, that.statistics)
                    && Objects.equal(this.disabled, that.disabled);
        }
        return false;
    }
}