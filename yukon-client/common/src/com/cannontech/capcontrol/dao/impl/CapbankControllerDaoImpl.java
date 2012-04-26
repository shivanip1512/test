package com.cannontech.capcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.DbChangeType;

public class CapbankControllerDaoImpl implements CapbankControllerDao {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private PaoDao paoDao;
    private AttributeService attributeService;
    private PaoCreationHelper paoCreationHelper;

    private static final ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;

    static {
        liteCapControlObjectRowMapper =
            CapbankControllerDaoImpl.createLiteCapControlObjectRowMapper();
    }

    public static final ParameterizedRowMapper<LiteCapControlObject> createLiteCapControlObjectRowMapper() {
        ParameterizedRowMapper<LiteCapControlObject> rowMapper =
            new ParameterizedRowMapper<LiteCapControlObject>() {
                public LiteCapControlObject mapRow(ResultSet rs, int rowNum) throws SQLException {

                    LiteCapControlObject lco = new LiteCapControlObject();
                    lco.setId(rs.getInt("PAObjectID"));
                    lco.setType(rs.getString("TYPE"));
                    lco.setDescription(rs.getString("Description"));
                    lco.setName(rs.getString("PAOName"));
                    // This is used for orphans. We will need to adjust the SQL if we intend to use
                    // this
                    // for anything other than orphaned cbcs.
                    lco.setParentId(0);
                    return lco;
                }
            };
        return rowMapper;
    }

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
            paoCreationHelper.processDbChange(controller, DbChangeType.UPDATE);
            paoCreationHelper.processDbChange(capBank, DbChangeType.UPDATE);
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
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count) {
        /* Get the unordered total count */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Category").eq(PaoCategory.DEVICE);
        sql.append("    AND PAOClass").eq(PaoClass.CAPCONTROL);
        sql.append("    AND type like 'CBC%'");
        sql.append("    AND PAObjectID not in (SELECT ControlDeviceID FROM CAPBANK)");

        int orphanCount = yukonJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());

        /* Get the paged subset of cc objects */
        sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, Description");
        sql.append("FROM YukonPAObject");
        sql.append("  WHERE Category").eq(PaoCategory.DEVICE);
        sql.append("    AND PAOClass").eq(PaoClass.CAPCONTROL);
        sql.append("    AND type like 'CBC%'");
        sql.append("    AND PAObjectID not in (SELECT ControlDeviceID FROM CAPBANK)");
        sql.append("ORDER BY PAOName");

        PagingResultSetExtractor<LiteCapControlObject> cbcOrphanExtractor =
            new PagingResultSetExtractor<LiteCapControlObject>(start,
                                                               count,
                                                               liteCapControlObjectRowMapper);
        
        yukonJdbcTemplate.getJdbcOperations().query(sql.getSql(),
                                                    sql.getArguments(),
                                                    cbcOrphanExtractor);

        List<LiteCapControlObject> unassignedCbcs = (List<LiteCapControlObject>) cbcOrphanExtractor.getResultList();

        SearchResult<LiteCapControlObject> searchResult = new SearchResult<LiteCapControlObject>();
        searchResult.setResultList(unassignedCbcs);
        searchResult.setBounds(start, count, orphanCount);

        return searchResult;
    }
    
    @Override
    public boolean isSerialNumberValid(String cbcName, int serialNumber) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT D.SerialNumber");
        sql.append("FROM YukonPaObject PAO JOIN DeviceCBC D ON PAO.PaObjectId = D.DeviceId");
        sql.append("WHERE PAO.PaoName").neq(cbcName);
        
        List<Integer> serialNumbers = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        
        return !serialNumbers.contains(serialNumber);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setPaoCreationHelper(PaoCreationHelper paoCreationHelper) {
        this.paoCreationHelper = paoCreationHelper;
    }
}