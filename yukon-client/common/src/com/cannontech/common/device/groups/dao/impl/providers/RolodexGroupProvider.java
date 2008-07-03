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

public class RolodexGroupProvider extends FullyBinningDeviceGroupProviderBase<Character> {
    private PaoDao paoDao;

    @Override
    protected List<Character> getAllBins() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct SUBSTRING(ypo.PAOName, 1, 1)");
        sql.append("from YukonPaObject ypo");
        sql.append("order by SUBSTRING(ypo.PAOName, 1, 1)");
        List<Character> leadCharacters = getJdbcTemplate().query(sql.toString(), new ParameterizedRowMapper<Character>() {
            public Character mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1).toUpperCase().charAt(0);
            }
        });
        
        return leadCharacters;
    }
    
    @Override
    protected String getChildSqlSelectForBin(Character bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.paobjectid");
        sql.append("from Device d");
        sql.append("join YukonPaObject ypo on d.deviceid = ypo.paobjectid");
        sql.append("where upper(SUBSTRING(ypo.PAOName, 1, 1)) = ");
        sql.appendQuotedString(bin);
        return sql.toString();
    }
    
    @Override
    protected Character getBinForDevice(YukonDevice device) {
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(device.getDeviceId());
        char firstLetter = liteYukonPAO.getPaoName().toUpperCase().charAt(0);
        return firstLetter;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}
