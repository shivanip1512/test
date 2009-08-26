package com.cannontech.core.authorization.support.pao;

import java.util.List;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Checker;

/**
 * Class used to check a pao's category/paoClass against a list of accepted
 * category/paoClass pairs
 */
public class PaoCheckCategoryClass implements Checker<YukonPao> {

    List<PaoCategoryClass> paoCategoryClassList = null;

    public void setPaoCategoryClassList(List<PaoCategoryClass> paoCategoryClassList) {
        this.paoCategoryClassList = paoCategoryClassList;
    }

    /**
     * Method to check a pao
     * @param pao - Pao to check
     * @return True if the pao's category/class match any of the accepted
     *         category/class pairs
     */
    public boolean check(YukonPao pao) {

        for (PaoCategoryClass catClass : this.paoCategoryClassList) {
            if (catClass.matches(pao)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Static inner class which represents a category/paoClass pair
     */
    public static class PaoCategoryClass {
        private PaoCategory category = null;
        private PaoClass paoClass = null;

        public void setCategory(PaoCategory category) {
            this.category = category;
        }

        public void setPaoClass(PaoClass paoClass) {
            this.paoClass = paoClass;
        }

        /**
         * Method to determine if a pao matches this PaoCategoryClass
         * @param pao - Pao to match
         * @return True if the pao's category and class are the same as the
         *         PaoCategoryClass
         */
        public boolean matches(YukonPao pao) {
            PaoType paoType = pao.getPaoIdentifier().getPaoType();
            return paoType != null
                && paoType.getPaoCategory() == category
                && paoType.getPaoClass() == paoClass;
        }

        @Override
        public String toString() {
            return "(category=" + category + ", class=" + paoClass + ")";
        }
    }
    
    @Override
    public String toString() {
        return "pao " + paoCategoryClassList + " category checker";
    }
}
