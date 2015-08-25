package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;

public class CapbankControllerDaoImpl implements CapbankControllerDao {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private PaoDao paoDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private AttributeService attributeService;
    private final ParameterizedRowMapper<YukonPAObject> rowMapper = new YukonPAObjectRowMapper();
    
    @Override
    public boolean assignController(int controllerId, String capbankName) {
        YukonPao pao = paoDao.findYukonPao(capbankName, PaoType.CAPBANK);
        return (pao == null) ? false : assignController(pao.getPaoIdentifier().getPaoId(), controllerId);
    }

    @Override
    public boolean assignController(int capbankId, int controllerId) {
        YukonPao controller = paoDao.getYukonPao(controllerId);

        LitePoint pointForAttribute = attributeService.getPointForAttribute(controller, BuiltInAttribute.CONTROL_POINT);
        int pointId = pointForAttribute.getPointID();

        unassignController(controllerId);

        SqlStatementBuilder sql = new SqlStatementBuilder();

        SqlParameterSink params = sql.update("CapBank");
        params.addValue("ControlDeviceID", controllerId);
        params.addValue("ControlPointID", pointId);

        sql.append("WHERE DeviceID").eq(capbankId);

        int rowsAffected = yukonJdbcTemplate.update(sql);

        boolean result = (rowsAffected == 1);

        if (result) {
            YukonPao capBank = paoDao.getYukonPao(capbankId);
            dbChangeManager.processPaoDbChange(controller, DbChangeType.UPDATE);
            dbChangeManager.processPaoDbChange(capBank, DbChangeType.UPDATE);
        }

        return result;
    }

    @Override
    public boolean unassignController(int controllerId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        SqlParameterSink params = sql.update("CapBank");
        params.addValue("ControlDeviceID", 0);
        params.addValue("ControlPointID", 0);

        sql.append("WHERE ControlDeviceID").eq(controllerId);

        int rowsAffected = yukonJdbcTemplate.update(sql);

        boolean result = (rowsAffected == 1);

        return result;
    }

    @Override
    @Transactional
    public List<LiteCapControlObject> getOrphans() {
        
        SqlStatementBuilder controlSql = new SqlStatementBuilder();
        controlSql.append("SELECT DISTINCT ControlDeviceId");
        controlSql.append("FROM CapBank");
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE Type").in(PaoType.getCbcTypes());
        sql.append("    AND PAObjectID").notIn(controlSql);
        sql.append("ORDER BY PAOName");
        
        List<LiteCapControlObject> orphans = yukonJdbcTemplate.query(sql, CapbankControllerDao.LITE_ORPHAN_MAPPER);
        
        return orphans;
    }
    
    @Override
    public boolean isSerialNumberValid(int serialNumber) {
        return isSerialNumberValid(null, serialNumber);
    }
    
    @Override
    public boolean isSerialNumberValid(Integer deviceId, int serialNumber) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM YukonPaObject PAO JOIN DeviceCBC D ON PAO.PaObjectId = D.DeviceId");
        sql.append("WHERE D.SerialNumber").eq(serialNumber);
        
        if (deviceId != null) {
            sql.append("AND PAO.PaobjectId").neq(deviceId);
        }
        
        return yukonJdbcTemplate.queryForInt(sql) == 0;
    }
    
    @Override
    public PaoIdentifier getParentBus(int cbcPaoId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT YPO2.PaObjectId, YPO2.Type");          
        sql.append("FROM YukonPaObject YPO");
        sql.append("JOIN CAPBANK CB ON YPO.PaObjectId = CB.CONTROLDEVICEID");
        sql.append("JOIN CCFeederBankList FBL ON FBL.DeviceID = CB.DEVICEID");
        sql.append("JOIN CCFeederSubAssignment FSA ON FSA.FeederID = FBL.FeederID");
        sql.append("JOIN YukonPAObject YPO2 ON YPO2.PAObjectID = FSA.SubStationBusID");
        sql.append("WHERE CB.CONTROLDEVICEID").eq(cbcPaoId);
        
        try {
            PaoIdentifier paoIdentifier = yukonJdbcTemplate.queryForObject(sql, RowMapper.PAO_IDENTIFIER);
            return paoIdentifier;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Parent Subbus was not found for cbc with ID: " + cbcPaoId);
        }
    }
    
    @Override
    public YukonPAObject getForId(int cbcId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PaObjectId,Category,PAOClass,PAOName,Type,Description,DisableFlag,PAOStatistics");
        sql.append("  FROM YukonPAObject");
        sql.append("WHERE PAObjectID").eq(cbcId);
        YukonPAObject capControlCBC = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return capControlCBC;
    }

    @Override
    public YukonPAObject getParentCBC(int cbcId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT YPO.PaObjectId,YPO.Category,YPO.PAOClass,YPO.PAOName,YPO.Type,YPO.Description ,YPO.DisableFlag,YPO.PAOStatistics");
        sql.append("FROM CAPBANK CB");
        sql.append("JOIN YukonPAObject YPO ON CB.DEVICEID = YPO.PAObjectID");
        sql.append("WHERE cb.CONTROLDEVICEID").eq(cbcId);

        try {
            YukonPAObject pao = yukonJdbcTemplate.queryForObject(sql, rowMapper);
            return pao;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Parent CBC was not found for cbc with ID: " + cbcId);
        }
    }

    private class YukonPAObjectRowMapper implements ParameterizedRowMapper<YukonPAObject> {

        @Override
        public YukonPAObject mapRow(ResultSet rs, int rowNum) throws SQLException {
            YukonPAObject capControlCBC = new YukonPAObject();
            capControlCBC.setPaObjectID(rs.getInt("PaObjectId"));
            capControlCBC.setCategory(rs.getString("Category"));
            capControlCBC.setPaoClass(rs.getString("PAOClass"));
            capControlCBC.setPaoName(rs.getString("PAOName"));
            capControlCBC.setPaoType(PaoType.getForDbString((rs.getString("Type"))));
            capControlCBC.setDescription(rs.getString("Description"));
            capControlCBC.setDisableFlag(rs.getString("DisableFlag").charAt(0));
            return capControlCBC;
        }
    }

}