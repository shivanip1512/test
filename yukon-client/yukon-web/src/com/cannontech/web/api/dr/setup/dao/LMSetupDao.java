package com.cannontech.web.api.dr.setup.dao;

import java.util.List;

import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.search.FilterCriteria;

public interface LMSetupDao <T> {

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

    public enum GearSortBy {
        GEARNAME("GearName"),
        GEARTYPE("ControlMethod"),
        GEARNUMBER("GearNumber"),
        PROGRAM("PAONAME");

        private final String dbString;

        private GearSortBy(String dbString) {
            this.dbString = dbString;
        }

        public String getDbString() {
            return dbString;
        }
    }

    public enum ProgramConstraintSortBy {
        CONSTRAINTNAME("constraintName");

        private final String dbString;

        private ProgramConstraintSortBy(String dbString) {
            this.dbString = dbString;
        }

        public String getDbString() {
            return dbString;
        }
    }

    public enum LoadProgramSortBy {
        PROGRAMNAME("ypo.PAOName"),
        OPERATIONALSTATE("lp.ControlType"),
        CONSTRAINT("lpc.ConstraintName"),
        PROGRAMTYPE("ypo.Type");

        private final String dbString;

        private LoadProgramSortBy(String dbString) {
            this.dbString = dbString;
        }

        public String getDbString() {
            return dbString;
        }
    }

    /**
     * Retrieves program constraints based on filter criteria.
     */
    List<T> getDetails(FilterCriteria<LMSetupFilter> criteria);

    /**
     * Retrieves total count based on filter criteria.
     */
    
    Integer getTotalCount(FilterCriteria<LMSetupFilter> criteria);

}
