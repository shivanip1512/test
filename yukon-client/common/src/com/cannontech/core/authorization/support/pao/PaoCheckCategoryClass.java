package com.cannontech.core.authorization.support.pao;

import java.util.List;

import com.cannontech.common.util.Checker;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Class used to check a pao's category/paoClass against a list of accepted
 * category/paoClass pairs
 */
public class PaoCheckCategoryClass implements Checker<LiteYukonPAObject> {

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
    public boolean check(LiteYukonPAObject pao) {

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
        private Integer category = null;
        private Integer paoClass = null;

        public void setCategory(Integer category) {
            this.category = category;
        }

        public void setPaoClass(Integer paoClass) {
            this.paoClass = paoClass;
        }

        /**
         * Method to determine if a pao matches this PaoCategoryClass
         * @param pao - Pao to match
         * @return True if the pao's category and class are the same as the
         *         PaoCategoryClass
         */
        public boolean matches(LiteYukonPAObject pao) {
            return pao.getCategory() == this.category && pao.getPaoClass() == paoClass;
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
