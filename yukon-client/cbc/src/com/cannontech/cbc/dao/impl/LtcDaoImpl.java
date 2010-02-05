package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.cbc.dao.LtcDao;
import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.cbc.model.LoadTapChanger;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.impl.YukonPaoRowMapper;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.pao.CapControlType;
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
        persistentLtc.setPaoClass(PAOGroups.STRING_CAT_CAPCONTROL);
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
    
    @Override
    public void unassignBus(int id) {
        String sql = "delete from CCSubstationBusToLTC where SubstationBusId = ?";
        yukonJdbcTemplate.update(sql, id);
    }
    
    @Override
    public void unassignLtc(int id) {
        String sql = "delete from CCSubstationBusToLTC where ltcId = ?";
        yukonJdbcTemplate.update(sql, id);
    }
    
    @Override
    public String getLtcName(int subBusId) {
        SqlStatementBuilder sql = new SqlStatementBuilder("select paoName from YukonPAObject pao");
        sql.append("join CCSubstationBusToLTC ltc on ltc.ltcId = pao.PAObjectID");
        sql.append("where ltc.substationBusId = ").appendArgument(subBusId);
        try {
            String name = yukonJdbcTemplate.queryForObject(sql.getSql(), String.class, sql.getArguments());
            return name;
        } catch (EmptyResultDataAccessException e){
            return CtiUtilities.STRING_NONE;
        }
    }
    
    @Override
    public void assign(int substationBusID, int ltcId) {
        if(getLtcIdForSub(substationBusID) > 0) {
            SqlStatementBuilder sql = new SqlStatementBuilder("update CCSubstationBusToLTC set ltcId = ").appendArgument(ltcId);
            sql.append("where substationbusId = ").appendArgument(substationBusID);
            yukonJdbcTemplate.update(sql.getSql(), sql.getArguments());
        } else {
            SqlStatementBuilder sql = new SqlStatementBuilder("insert into CCSubstationBusToLTC values(");
            sql.appendArgument(substationBusID).append(",").appendArgument(ltcId).append(")");
            yukonJdbcTemplate.update(sql.getSql());
        }
    }
    
    @Override
    public int getLtcIdForSub(int subBusId) {
        try {
            String sql = "select ltcId from CCSubstationBusToLTC where substationBusId = ?";
            return yukonJdbcTemplate.queryForInt(sql, subBusId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    @Override
    public List<Integer> getUnassignedLtcIds() {
        SqlStatementBuilder sql = new SqlStatementBuilder("SELECT PAObjectID FROM YukonPAObject");
        sql.append("where Category = ").appendArgument(PaoCategory.CAPCONTROL);
        sql.append("and PAOClass = ").appendArgument(PaoClass.CAPCONTROL);
        sql.append("and type = ").appendArgument(PaoType.LOAD_TAP_CHANGER);
        sql.append("and PAObjectID not in (SELECT ltcId FROM CCSubstationBusToLTC) ORDER BY PAOName");
    
        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("PAObjectID") );
                return i;
            }
        };
        
        List<Integer> ltcIds = yukonJdbcTemplate.query(sql.getSql(), mapper, sql.getArguments());
        
        return ltcIds;
    }
    
    @Override
    public List<LiteCapControlObject> getOrphans() {
        ParameterizedRowMapper<LiteCapControlObject> rowMapper = new ParameterizedRowMapper<LiteCapControlObject>() {
            public LiteCapControlObject mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                LiteCapControlObject lco = new LiteCapControlObject();
                lco.setId(rs.getInt("PAObjectID"));
                lco.setType(rs.getString("TYPE"));
                lco.setDescription(rs.getString("Description"));
                lco.setName(rs.getString("PAOName"));
                lco.setParentId(0);
                return lco;
            }
        };
        
        List<Integer> ltcIds = getUnassignedLtcIds();
        
        ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(yukonJdbcTemplate);
        final List<LiteCapControlObject> unassignedLtcs = template.query(new SqlGenerator<Integer>() {
            @Override
            public String generate(List<Integer> subList) {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append("SELECT PAObjectID,PAOName,TYPE,Description FROM YukonPAObject WHERE PAObjectID IN (");
                sqlBuilder.append(subList);
                sqlBuilder.append(")");
                String sql = sqlBuilder.toString();
                return sql;
            }
        }, ltcIds, rowMapper);
        
        return unassignedLtcs;
    }
    
    @Override
    public PaoIdentifier getLtcPaoIdentifierForSubBus(int busId) {
        SqlStatementBuilder sql = new SqlStatementBuilder("select ypo.PaobjectId, ypo.type from CCSubstationBusToLTC ltc");
        sql.append("join YukonPaobject ypo on ypo.PaobjectId = ltc.LtcId");
        sql.append("where ltc.SubstationBusId = ").append(busId);
        return yukonJdbcTemplate.queryForObject(sql, new YukonPaoRowMapper());
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