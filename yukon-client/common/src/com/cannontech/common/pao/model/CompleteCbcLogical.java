package com.cannontech.common.pao.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.google.common.base.Objects;

@YukonPao(tableBacked=false, paoTypes={PaoType.CBC_LOGICAL})
public class CompleteCbcLogical extends CompleteCbcBase {
    private CompleteDeviceParent completeDeviceParent = new CompleteDeviceParent();
    
    @YukonPaoField
    public CompleteDeviceParent getCompleteDeviceParent() {
        return completeDeviceParent;
    }

    public void setCompleteDeviceParent(CompleteDeviceParent completeDeviceParent) {
        this.completeDeviceParent = completeDeviceParent;
    }
    
    public int getParentDeviceId() {
        return completeDeviceParent.getParentId();
    }

    public void setParentDeviceId(int id) {
        completeDeviceParent.setParentId(id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), completeDeviceParent);
    }

    @Override
    protected boolean localEquals(Object object) {
        if (object instanceof CompleteCbcLogical) {
            if (!super.localEquals(object)) {
                return false;
            }
            CompleteCbcLogical that = (CompleteCbcLogical) object;
            return Objects.equal(completeDeviceParent, that.completeDeviceParent);
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + " CompleteCbcLogical [completeDeviceParent=" + completeDeviceParent + "]";
    }
}
