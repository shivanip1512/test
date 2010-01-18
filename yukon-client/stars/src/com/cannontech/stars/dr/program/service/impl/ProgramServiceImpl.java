package com.cannontech.stars.dr.program.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.dao.ProgramRowMapper;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramService;
import com.cannontech.stars.util.ECUtils;

public class ProgramServiceImpl implements ProgramService {
    private ApplianceDao applianceDao;
    private ProgramDao programDao;
    private RolePropertyDao rolePropertyDao;
    private FilterService filterService;
    private SimpleJdbcTemplate simpleJdbcTemplate;

    @Override
    public boolean hasProgramAccess(final CustomerAccount customerAccount, final Program program) {
        final List<Appliance> appliances = applianceDao.getByAccountId(customerAccount.getAccountId());
        return hasProgramAccess(customerAccount, appliances, program);
    }
    
    @Override
    public boolean hasProgramAccess(final CustomerAccount customerAccount, final List<Appliance> appliances,
        final Program program) {
        
        final int programId = program.getProgramId();
        
        for (final Appliance appliance : appliances) {
            int applianceProgramId = appliance.getProgramId(); 
            if (programId == applianceProgramId) return true;
        }
        
        return false;
    }
    
    @Override
    public Program getByProgramName(String programName, LiteStarsEnergyCompany energyCompany) {
        /*
         * This part of the method will get all the energy company ids that can
         * have an appliance category this energy company can use.
         */
        List<Integer> energyCompanyIds = new ArrayList<Integer>();
        if (rolePropertyDao.checkProperty(YukonRoleProperty.INHERIT_PARENT_APP_CATS,
                                          energyCompany.getUser())) {
            List<LiteStarsEnergyCompany> allAscendants = ECUtils.getAllAscendants(energyCompany);

            for (LiteStarsEnergyCompany ec : allAscendants) {
                energyCompanyIds.add(ec.getEnergyCompanyID());
            }
        } else {
            energyCompanyIds.add(energyCompany.getEnergyCompanyID());
        }
        Program program = null;
        try {
            program = programDao.getByProgramName(programName, energyCompanyIds);
        } catch (ProgramNotFoundException e) {
            /*
             * Since we couldn't find the program by the program name lets try
             * finding the program by its alternate name.
             */
            program = programDao.getByAlternateProgramName(programName, energyCompanyIds);
        }
        return program;
    }

    @Override
    public SearchResult<Program> filterPrograms(Integer applianceCategoryId) {
        UiFilter<Program> filter = null;
        if (applianceCategoryId != null) {
            filter = new ForApplianceCategoryFilter(applianceCategoryId);
        }
        Comparator<Program> sorter = null;
        SearchResult<Program> filteredPrograms =
            filterService.filter(filter, sorter, 0, Integer.MAX_VALUE,
                                 new ProgramRowMapper(simpleJdbcTemplate));
        return filteredPrograms;
    }

    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }

    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    public static class ForApplianceCategoryFilter implements UiFilter<Program> {
        private int applianceCategoryId;

        public ForApplianceCategoryFilter(int applianceCategoryId) {
            this.applianceCategoryId = applianceCategoryId;
        }

        @Override
        public List<PostProcessingFilter<Program>> getPostProcessingFilters() {
            return null;
        }

        @Override
        public List<SqlFilter> getSqlFilters() {
            List<SqlFilter> retVal = new ArrayList<SqlFilter>(1);
            retVal.add(new SqlFilter(){

                @Override
                public SqlFragmentSource getWhereClauseFragment() {
                    SqlStatementBuilder retVal = new SqlStatementBuilder(
                        "applianceCategoryId =");
                    retVal.appendArgument(applianceCategoryId);
                    return retVal;
                }});

            return retVal;
        }
    }
}
