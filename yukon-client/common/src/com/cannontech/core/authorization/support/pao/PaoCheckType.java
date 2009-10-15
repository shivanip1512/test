package com.cannontech.core.authorization.support.pao;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Checker;

/**
 * Class used to check a pao's type against a list of accepted types
 */
public class PaoCheckType implements Checker<YukonPao> {

    List<PaoType> paoTypeList = null;

    public void setPaoTypeList(List<PaoType> paoTypeList) {
        this.paoTypeList = paoTypeList;
    }

    /**
     * Method to check a pao
     * @param pao - Pao to check
     * @return True if the pao's type matches any of the accepted types
     */
    public boolean check(YukonPao pao) {

        for (PaoType type : this.paoTypeList) {
            if (type.equals(pao.getPaoIdentifier().getPaoType())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "pao " + paoTypeList + " type checker";
    }
}
