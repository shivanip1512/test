package com.cannontech.dr.loadgroup.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;
import com.cannontech.dr.loadgroup.model.LoadGroupDisplayField;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.message.util.Message;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Ordering;

public class LoadGroupServiceImpl implements LoadGroupService {
    private LoadGroupDao loadGroupDao = null;
    private LoadControlClientConnection loadControlClientConnection = null;
    private FilterService filterService;

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
    public SearchResult<DisplayablePao> filterGroups(
            YukonUserContext userContext, UiFilter<DisplayablePao> filter,
            Comparator<DisplayablePao> sorter, int startIndex, int count) {

        Comparator<DisplayablePao> defaultSorter = LoadGroupDisplayField.NAME.getSorter(this, userContext, false);
        if (sorter == null) {
            sorter = defaultSorter;
        } else {
            sorter = Ordering.from(sorter).compound(defaultSorter);
        }
        SearchResult<DisplayablePao> searchResult =
            filterService.filter(filter, sorter, startIndex, count, rowMapper);
        return searchResult;
    }    

    @Override
    public void sendShed(int loadGroupId, int durationInSeconds) {
        // TODO:  Log action
        Message msg = new LMCommand(LMCommand.SHED_GROUP, loadGroupId,
                                    durationInSeconds, 0.0);
        loadControlClientConnection.write(msg);
    }

    @Override
    public void sendRestore(int loadGroupId) {
        // TODO:  Log action
        Message msg = new LMCommand(LMCommand.RESTORE_GROUP, loadGroupId,
                                    0, 0.0);
        loadControlClientConnection.write(msg);
    }

    @Override
    public void setEnabled(int loadGroupId, boolean isEnabled) {
        // TODO:  Log action
        int loadControlCommand = isEnabled ? LMCommand.ENABLE_GROUP
                : LMCommand.DISABLE_GROUP;
        Message msg = new LMCommand(loadControlCommand, loadGroupId, 0, 0.0);
        loadControlClientConnection.write(msg);
    }    

    private static RowMapperWithBaseQuery<DisplayablePao> rowMapper =
        new AbstractRowMapperWithBaseQuery<DisplayablePao>() {

            @Override
            public SqlFragmentSource getBaseQuery() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("SELECT paObjectId, paoName, type FROM yukonPAObject"); 
                retVal.append("WHERE category = 'DEVICE' AND paoClass = 'GROUP'");
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
                DisplayablePao retVal = new DisplayablePaoBase(paoId,
                                                               rs.getString("paoName"));
                return retVal;
            }
        };

    @Autowired
    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
    }

    @Autowired
    public void setLoadControlClientConnection(
            LoadControlClientConnection loadControlClientConnection) {
        this.loadControlClientConnection = loadControlClientConnection;
    }
    
    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
}
