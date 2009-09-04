package com.cannontech.dr.loadgroup.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupBase;

public class LoadGroupServiceImpl implements LoadGroupService {
    private LoadGroupDao loadGroupDao = null;
    private LoadControlClientConnection loadControlClientConnection = null;

    @Override
    public ObjectMapper<DisplayablePao, LMDirectGroupBase> getMapper() {
        return this;
    }

    @Override
    public LMDirectGroupBase map(DisplayablePao from) throws ObjectMappingException {
        LMDirectGroupBase group = null;
        DatedObject<LMGroupBase> datedGroup = loadControlClientConnection.getDatedGroup(from.getPaoIdentifier()
                                                                                            .getPaoId());
        if (datedGroup != null && datedGroup.getObject() instanceof LMDirectGroupBase) {
            group = (LMDirectGroupBase) datedGroup.getObject();
        }
        return group;
    }

    @Override
    public DatedObject<LMGroupBase> findDatedGroup(int loadGroupId) {
        return loadControlClientConnection.getDatedGroup(loadGroupId);
    }

    @Override
    public DisplayablePao getLoadGroup(int loadGroupId) {
        return loadGroupDao.getLoadGroup(loadGroupId);
    }
    
    @Override
    public RowMapperWithBaseQuery<DisplayablePao> getRowMapper() {
        return new AbstractRowMapperWithBaseQuery<DisplayablePao>() {

            @Override
            public SqlFragmentSource getBaseQuery() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("SELECT paObjectId, paoName, type FROM yukonPAObject " 
                              + "WHERE category = 'DEVICE' AND paoClass = 'GROUP'");
                return retVal;
            }

            @Override
            public DisplayablePao mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                String paoTypeStr = rs.getString("type");
                int deviceTypeId = PAOGroups.getPAOType("DEVICE", paoTypeStr);
                PaoType paoType = PaoType.getForId(deviceTypeId);
                PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                        paoType);
                DisplayablePao retVal = new DisplayableDevice(paoId,
                                                              rs.getString("paoName"));
                return retVal;
            }
        };
    }

    @Autowired
    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
    }

    @Autowired
    public void setLoadControlClientConnection(
            LoadControlClientConnection loadControlClientConnection) {
        this.loadControlClientConnection = loadControlClientConnection;
    }
}
