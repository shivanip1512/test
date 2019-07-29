package com.cannontech.web.api.dr.setup.dao;

import java.util.List;

import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;

public interface LMSetupDao {

    public enum SortBy {
        NAME("PAONAME"),
        TYPE("TYPE");

        private final String dbString;

        private SortBy(String dbString) {
            this.dbString = dbString;
        }

        public String getDbString() {
            return dbString;
        }
    }

    /**
     * Retrieves pao details based on filter criteria and pao types.
     */
    SearchResults<LMPaoDto> getPaoDetails(FilterCriteria<LMSetupFilter> criteria, List<PaoType> paoTypes);

    /**
     * Retrieves program constraints based on filter criteria.
     */
    SearchResults<LMPaoDto> getProgramConstraint(FilterCriteria<LMSetupFilter> criteria);
}
