package com.cannontech.common.device.groups.dao.impl.providers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.Device;

public class DeviceTypeGroupProvider extends CompleteBinningDeviceGroupProviderBase<String> {
    private PaoDao paoDao;
    
    @Override
    protected List<String> getAllBins() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT ypo.type");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE ypo.paobjectid").neq(Device.SYSTEM_DEVICE_ID);
        sql.append("ORDER BY ypo.type");

        ParameterizedRowMapper<String> mapper = new ParameterizedRowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("type");
            }
        };
        List<String> resultList = getJdbcTemplate().query(sql.getSql(), mapper, sql.getArguments());
        return resultList;
    }
    
    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(String bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE ypo.type = ");
        sql.appendArgument(bin);
        return sql;
    }
    
    @Override
    protected Set<String> getBinsForDevice(YukonDevice device) {
        LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getPaoIdentifier().getPaoId());
        String type = devicePao.getPaoType().getDbString();
        return Collections.singleton(type);
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}
