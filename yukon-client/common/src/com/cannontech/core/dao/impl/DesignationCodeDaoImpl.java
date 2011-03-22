package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.model.DesignationCodeDto;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DesignationCodeDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

public class DesignationCodeDaoImpl implements DesignationCodeDao, InitializingBean {
    
    private NextValueHelper nextValueHelper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<DesignationCodeDto> designationCodeTemplate;
    private DBPersistentDao dbPersistantDao = null;

    // DesignationCodeDto -> to -> sql
    private FieldMapper<DesignationCodeDto> designationCodeDtoFieldMapper = new FieldMapper<DesignationCodeDto>() {

        @Override
        public void extractValues(MapSqlParameterSource p, DesignationCodeDto designationCodeDto) {
            p.addValue("DesignationCodeID", designationCodeDto.getId());
            p.addValue("DesignationCodeValue", designationCodeDto.getValue());        
            p.addValue("ServiceCompanyID", designationCodeDto.getServiceCompanyId());
        }

        @Override
        public Number getPrimaryKey(DesignationCodeDto object) {
            return object.getId();
        }

        @Override
        public void setPrimaryKey(DesignationCodeDto object, int value) {
            object.setId(value);
        }
    };
    
    // Row Mappers  sql -> to -> DesignationCodeDto
    private static class DesignationCodeDtoRowMapper implements YukonRowMapper<DesignationCodeDto> {

        @Override
        public DesignationCodeDto mapRow(YukonResultSet rs) throws SQLException {
            DesignationCodeDto designationCodeDto = new DesignationCodeDto();

            designationCodeDto.setId(rs.getInt("DesignationCodeID"));
            designationCodeDto.setValue(rs.getString("DesignationCodeValue"));
            designationCodeDto.setServiceCompanyId(rs.getInt("ServiceCompanyID"));
            
            return designationCodeDto;
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        designationCodeTemplate = new SimpleTableAccessTemplate<DesignationCodeDto>(yukonJdbcTemplate, nextValueHelper);
        designationCodeTemplate.setTableName("ServiceCompanyDesignationCode");
        designationCodeTemplate.setPrimaryKeyField("DesignationCodeID");
        designationCodeTemplate.setFieldMapper(designationCodeDtoFieldMapper);
        designationCodeTemplate.setPrimaryKeyValidOver(0);
    }

    //CRUD
    @Override
    public DesignationCodeDto getServiceCompanyDesignationCode(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM ServiceCompanyDesignationCode");
        sql.append("WHERE DesignationCodeID").eq(id);
        
        return yukonJdbcTemplate.queryForObject(sql, new DesignationCodeDtoRowMapper());
    }

    @Override
    public List<DesignationCodeDto> getDesignationCodesByServiceCompanyId(int serviceCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM ServiceCompanyDesignationCode");
        sql.append("WHERE ServiceCompanyID").eq(serviceCompanyId);
        
        return yukonJdbcTemplate.query(sql, new DesignationCodeDtoRowMapper());
    }

    @Override
    public void add(DesignationCodeDto designationCode) {
        designationCodeTemplate.save(designationCode);
        sendDesignationCodeChangeMessage(designationCode.getId(), DbChangeType.ADD);
    }

    @Override
    public void bulkAdd(List<DesignationCodeDto> designationCodes) {
        for(DesignationCodeDto designationCode : designationCodes) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("INSERT INTO ServiceCompanyDesignationCode");
            sql.values(nextValueHelper.getNextValue("ServiceCompanyDesignationCode"),
                       designationCode.getValue(),
                       designationCode.getServiceCompanyId());
            yukonJdbcTemplate.update(sql);
            sendDesignationCodeChangeMessage(designationCode.getId(), DbChangeType.ADD);
        }
    }

    @Override
    public void update(DesignationCodeDto designationCode) {
        designationCodeTemplate.save(designationCode);
        sendDesignationCodeChangeMessage(designationCode.getId(), DbChangeType.UPDATE);
    }

    @Override
    public void delete(DesignationCodeDto designationCode) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ServiceCompanyDesignationCode");
        sql.append("WHERE DesignationCodeID").eq(designationCode.getId());
        sendDesignationCodeChangeMessage(designationCode.getId(), DbChangeType.DELETE);
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void delete(int id) {
        delete(getServiceCompanyDesignationCode(id));
    }

    @Override
    public void bulkDelete(List<DesignationCodeDto> designationCodes) {
        for(DesignationCodeDto designationCode : designationCodes) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM ServiceCompanyDesignationCode");
            sql.append("WHERE DesignationCodeID").eq(designationCode.getId());
            yukonJdbcTemplate.update(sql);
            sendDesignationCodeChangeMessage(designationCode.getId(), DbChangeType.DELETE);
        }
    }
    
    private void sendDesignationCodeChangeMessage(Integer designationCodeId, DbChangeType dbChangeType) {
        dbPersistantDao.processDatabaseChange(dbChangeType, DbChangeCategory.SERVICE_COMPANY_DESIGNATION_CODE, designationCodeId);
    }
    
 // DI
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    @Autowired
    public void setDbPersistantDao(DBPersistentDao dbPersistantDao) {
        this.dbPersistantDao = dbPersistantDao;
    }
}
