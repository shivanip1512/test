package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.cbc.dao.LtcDao;
import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.cbc.model.LoadTapChanger;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;

public class LtcDaoImpl implements LtcDao {
    
    private YukonJdbcTemplate yukonJdbcTemplate;

    private NextValueHelper nextValueHelper;

    @Override
    public int add(LoadTapChanger ltc) {
        int newId = nextValueHelper.getNextValue("YukonPaObject");
        
        YukonPAObject persistentLtc = new YukonPAObject();
        persistentLtc.setPaObjectID(newId);
        persistentLtc.setCategory(PAOGroups.STRING_CAT_CAPCONTROL);
        persistentLtc.setPaoClass(DeviceClasses.STRING_CLASS_VOLTAGEREGULATOR);
        persistentLtc.setPaoName(ltc.getName());
        persistentLtc.setType(CapControlType.LTC.getDisplayValue());
        persistentLtc.setDescription(ltc.getDescription());
        persistentLtc.setDisableFlag(ltc.getDisabled()?'Y':'N');
        
        try {
            Transaction.createTransaction(com.cannontech.database.Transaction.INSERT, persistentLtc).execute();
            ltc.setId(persistentLtc.getPaObjectID());
        } catch (TransactionException e) {
            throw new DataIntegrityViolationException("Insert of LTC, " + ltc.getName() + ", failed.", e);
        }
        
        return newId;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count) {
        ParameterizedRowMapper<LiteCapControlObject> rowMapper = new ParameterizedRowMapper<LiteCapControlObject>() {
            public LiteCapControlObject mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                LiteCapControlObject lco = new LiteCapControlObject();
                lco.setId(rs.getInt("PAObjectID"));
                lco.setType(rs.getString("Type"));
                lco.setDescription(rs.getString("Description"));
                lco.setName(rs.getString("PAOName"));
                lco.setParentId(0);
                return lco;
            }
        };
        
        /* Get the unordered total count */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Category").eq(PaoCategory.CAPCONTROL);
        sql.append("    AND PAOClass").eq(DeviceClasses.STRING_CLASS_VOLTAGEREGULATOR);
        sql.append("    AND type").eq(PaoType.LOAD_TAP_CHANGER);
        //TODO !!!!
        //Change to use Zones
        //sql.append("    AND PAObjectID not in (SELECT ltcId FROM CCSubstationBusToLTC)");
        
        int orphanCount = yukonJdbcTemplate.queryForInt(sql);
        
        /* Get the paged subset of cc objects */
        sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Category").eq(PaoCategory.CAPCONTROL);
        sql.append("    AND PAOClass").eq(DeviceClasses.STRING_CLASS_VOLTAGEREGULATOR);
        sql.append("    AND type").eq(PaoType.LOAD_TAP_CHANGER);
        //TODO !!!!
        //Change to use Zones
        //sql.append("    AND PAObjectID not in (SELECT ltcId FROM CCSubstationBusToLTC)");
        sql.append("ORDER BY PAOName");
        
        PagingResultSetExtractor orphanExtractor = new PagingResultSetExtractor(start, count, rowMapper);
        yukonJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), orphanExtractor);
        
        List<LiteCapControlObject> unassignedLtcs = (List<LiteCapControlObject>) orphanExtractor.getResultList();
        
        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedLtcs);
        searchResult.setBounds(start, count, orphanCount);
        
        return searchResult;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

}