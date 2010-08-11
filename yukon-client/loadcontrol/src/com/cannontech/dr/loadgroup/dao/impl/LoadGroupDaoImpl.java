package com.cannontech.dr.loadgroup.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;

public class LoadGroupDaoImpl implements LoadGroupDao {
    private YukonJdbcTemplate yukonJdbcTemplate;

    private SqlStatementBuilder loadGroupSelect = new SqlStatementBuilder();{
        loadGroupSelect.append("SELECT PAObjectId, PAOName, Type ");
        loadGroupSelect.append("FROM YukonPAObject");
        loadGroupSelect.append("WHERE Category").eq(PaoCategory.DEVICE);
        loadGroupSelect.append(  "AND PAOClass").eq(PaoClass.GROUP);
    }
    
    private final static ParameterizedRowMapper<DisplayablePao> loadGroupRowMapper =
        new ParameterizedRowMapper<DisplayablePao>() {
        @Override
        public DisplayablePao mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            String paoTypeStr = rs.getString("type");
            int deviceTypeId = PAOGroups.getPAOType("DEVICE", paoTypeStr);
            PaoType paoType = PaoType.getForId(deviceTypeId);
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    paoType);
            DisplayablePao retVal = new DisplayablePaoBase(paoId,
                                                           rs.getString("paoName"));
            return retVal;
        }};

    @Override
    public List<DisplayablePao> getForProgram(int programId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT paobjectId, paoName, type FROM yukonPaobject");
        sql.append("WHERE paobjectId IN (");
        sql.append("SELECT lmGroupDeviceId FROM lmProgramDirectGroup");
        sql.append("WHERE deviceId").eq(programId).append(")");

        return yukonJdbcTemplate.query(sql, loadGroupRowMapper);
    }

    @Override
    public DisplayablePao getLoadGroup(int loadGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(loadGroupSelect);
        sql.append("AND PAObjectId").eq(loadGroupId);
        
        return yukonJdbcTemplate.queryForObject(sql, loadGroupRowMapper);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
