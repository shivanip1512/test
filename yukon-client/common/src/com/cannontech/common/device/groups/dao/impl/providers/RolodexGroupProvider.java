package com.cannontech.common.device.groups.dao.impl.providers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class RolodexGroupProvider extends BinningDeviceGroupProviderBase<Character> {
    private PaoDao paoDao;

    @Override
    protected List<Character> getAllBins() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct SUBSTRING(ypo.PAOName, 1, 1)");
        sql.append("from YukonPaObject ypo");
        sql.append("order by SUBSTRING(ypo.PAOName, 1, 1)");
        List<Character> leadCharacters = getJdbcTemplate().query(sql.getSql(), new ParameterizedRowMapper<Character>() {
            public Character mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1).toUpperCase().charAt(0);
            }
        }, sql.getArguments() );
        
        return leadCharacters;
    }
    
    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(Character bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.paobjectid");
        sql.append("from Device d");
        sql.append("join YukonPaObject ypo on d.deviceid = ypo.paobjectid");
        sql.append("where upper(SUBSTRING(ypo.PAOName, 1, 1)) = ");
        sql.appendArgument(String.valueOf(bin)); // get JDBC error if this is passed in as a Character
        return sql;
    }
    
    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        return new SimpleSqlFragment("SELECT ypo.paobjectid FROM YukonPaObject ypo");
    }
    
    @Override
    protected Set<Character> getBinsForDevice(YukonDevice device) {
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(device.getDeviceId());
        Character firstLetter = liteYukonPAO.getPaoName().toUpperCase().charAt(0);
        return Collections.singleton(firstLetter);
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}
