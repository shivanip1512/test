package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.cbc.dao.LtcDao;
import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.definition.service.DeviceDefinitionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.LoadTapChanger;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.incrementer.NextValueHelper;

public class LtcDaoImpl implements LtcDao {
    
    private Logger log = YukonLogManager.getLogger(LtcDao.class);
    
    private DeviceDefinitionService deviceDefinitionService;
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private NextValueHelper nextValueHelper;

    @Override
    public int add(String name, boolean disable, int portId) throws TransactionException {
        /* TODO Add the ltc and ltc points as one multidbpersistent so its all in one transaction? */
        int newId = nextValueHelper.getNextValue("YukonPaObject");
        PaoType type = PaoType.LOAD_TAP_CHANGER;
        
        LoadTapChanger persistentLCT = (LoadTapChanger) DeviceFactory.createDevice(type.getDeviceTypeId());
        persistentLCT.setDisabled(disable);
        persistentLCT.setPAOName(name);
        persistentLCT.setDeviceID(newId);
        persistentLCT.setPAOCategory(PAOGroups.STRING_CAT_DEVICE);
        persistentLCT.getDeviceDirectCommSettings().setPortID(portId);

        Transaction.createTransaction(com.cannontech.database.Transaction.INSERT, persistentLCT).execute();
        
        List<PointBase> points = deviceDefinitionService.createAllPointsForDevice(new SimpleDevice(persistentLCT.getPAObjectID(), PAOGroups.getDeviceType(persistentLCT.getPAOType())));
        MultiDBPersistent pointMulti = new MultiDBPersistent();
        pointMulti.getDBPersistentVector().addAll(points);
        try {
            PointUtil.insertIntoDB(pointMulti);
        } catch (TransactionException e) {
            log.error("Failed on Inserting Points for Load Tap Changer, " + name +".");
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
        simpleJdbcTemplate.update(sql, id);
    }
    
    @Override
    public void unassignLtc(int id) {
        String sql = "delete from CCSubstationBusToLTC where ltcId = ?";
        simpleJdbcTemplate.update(sql, id);
    }
    
    @Override
    public String getLtcName(int subBusId) {
        SqlStatementBuilder sql = new SqlStatementBuilder("select paoName from YukonPAObject pao");
        sql.append("join CCSubstationBusToLTC ltc on ltc.ltcId = pao.PAObjectID");
        sql.append("where ltc.substationBusId = ").appendArgument(subBusId);
        try {
            String name = simpleJdbcTemplate.queryForObject(sql.getSql(), String.class, sql.getArguments());
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
            simpleJdbcTemplate.update(sql.getSql(), sql.getArguments());
        } else {
            SqlStatementBuilder sql = new SqlStatementBuilder("insert into CCSubstationBusToLTC values(");
            sql.appendArgument(substationBusID).append(",").appendArgument(ltcId).append(")");
            simpleJdbcTemplate.update(sql.getSql(), substationBusID, ltcId);
        }
    }
    
    @Override
    public int getLtcIdForSub(int subBusId) {
        try {
            String sql = "select ltcId from CCSubstationBusToLTC where substationBusId = ?";
            return simpleJdbcTemplate.queryForInt(sql, subBusId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    @Override
    public List<Integer> getUnassignedLtcIds() {
        SqlStatementBuilder sql = new SqlStatementBuilder("SELECT PAObjectID FROM YukonPAObject");
        sql.append("where Category = ").appendArgument(PaoCategory.DEVICE);
        sql.append("and PAOClass = ").appendArgument(PaoClass.RTU);
        sql.append("and type = ").appendArgument(PaoType.LOAD_TAP_CHANGER);
        sql.append("and PAObjectID not in (SELECT ltcId FROM CCSubstationBusToLTC) ORDER BY PAOName");
    
        ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int num) throws SQLException{
                Integer i = new Integer ( rs.getInt("PAObjectID") );
                return i;
            }
        };
        
        List<Integer> ltcIds = simpleJdbcTemplate.query(sql.getSql(), mapper, sql.getArguments());
        
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
        
        ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
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
    
    @Autowired
    public void setDeviceDefinitionService(DeviceDefinitionService deviceDefinitionService) {
        this.deviceDefinitionService = deviceDefinitionService;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

}