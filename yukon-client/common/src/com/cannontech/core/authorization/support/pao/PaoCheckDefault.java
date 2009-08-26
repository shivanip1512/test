package com.cannontech.core.authorization.support.pao;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Checker;

/**
 * Class used to check that a pao is not in this class' excludeList.
 */
public class PaoCheckDefault implements Checker<YukonPao> {

    /**
     * List of pao checkers that should be excluded from PaoCheckDefault. If a
     * pao matches any of the checkers in this list, check will return false for
     * PaoCheckDefault
     */
    List<Checker<YukonPao>> excludeList = Collections.emptyList();

    public void setExcludeList(List<Checker<YukonPao>> excludeList) {
        this.excludeList = excludeList;
    }

    /**
     * Method to check a pao
     * @param pao - Pao to check
     * @return True if the pao is not in the exclude list
     */
    public boolean check(YukonPao pao) {

        for (Checker<YukonPao> excludeCheck : excludeList) {
            if (excludeCheck.check(pao)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "all but " + excludeList + " checker";
    }

}
