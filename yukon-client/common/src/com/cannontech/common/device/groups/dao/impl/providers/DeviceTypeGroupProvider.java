package com.cannontech.common.device.groups.dao.impl.providers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class DeviceTypeGroupProvider extends BinningDeviceGroupProviderBase<String> {
    private PaoDao paoDao;
    
    @Override
    protected List<String> getAllBins() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT ypo.type");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("JOIN devicemetergroup dmg ON (dmg.deviceid = ypo.paobjectid)");
        sql.append("ORDER BY ypo.type");

        ParameterizedRowMapper<String> mapper = new ParameterizedRowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("type");
            }
        };
        List<String> resultList = getJdbcTemplate().query(sql.toString(), mapper);
        return resultList;
    }
    
    @Override
    protected String getChildSqlSelectForBin(String bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM DeviceMeterGroup d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE ypo.type = ");
        sql.appendQuotedString(bin);
        return sql.toString();
    }
    
    @Override
    protected String getAllBinnedDeviceSqlSelect() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM DeviceMeterGroup d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        return sql.toString();
    }

    @Override
    protected String getBinForDevice(YukonDevice device) {
        LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getDeviceId());
        String type = getPaoGroupsWrapper().getPAOTypeString(devicePao.getType());
        return type;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}
