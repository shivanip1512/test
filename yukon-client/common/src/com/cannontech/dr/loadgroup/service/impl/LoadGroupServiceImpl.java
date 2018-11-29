package com.cannontech.dr.loadgroup.service.impl;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterDao;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.dr.loadgroup.filter.MacroLoadGroupForLoadGroupFilter;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.message.util.Message;
import com.cannontech.user.YukonUserContext;

public class LoadGroupServiceImpl implements LoadGroupService {
    @Autowired private LoadGroupDao loadGroupDao = null;
    @Autowired private LoadControlClientConnection loadControlClientConnection = null;
    @Autowired private FilterDao filterDao;
    @Autowired private DemandResponseEventLogService demandResponseEventLogService;

    @Override
    public LMDirectGroupBase getGroupForPao(YukonPao from) {
        LMDirectGroupBase group = null;
        DatedObject<LMGroupBase> datedGroup =
            loadControlClientConnection.getDatedGroup(from.getPaoIdentifier().getPaoId());
        if (datedGroup != null && datedGroup.getObject() instanceof LMDirectGroupBase) {
            group = (LMDirectGroupBase) datedGroup.getObject();
        }
        return group;
    }

    @Override
    public DatedObject<? extends LMGroupBase> findDatedGroup(int loadGroupId) {
        return loadControlClientConnection.getDatedGroup(loadGroupId);
    }

    @Override
    public List<DisplayablePao> findLoadGroupsForMacroLoadGroup(
            int loadGroupId, YukonUserContext userContext) {
        UiFilter<DisplayablePao> filter = new MacroLoadGroupForLoadGroupFilter(loadGroupId);

        SearchResults<DisplayablePao> searchResult =
            filterGroups(filter, null, 0, Integer.MAX_VALUE, userContext);

        return searchResult.getResultList();
    }

    @Override
    public DisplayablePao getLoadGroup(int loadGroupId) {
        return loadGroupDao.getById(loadGroupId);
    }

    @Override
    public SearchResults<DisplayablePao> filterGroups(
            UiFilter<DisplayablePao> filter, Comparator<DisplayablePao> sorter,
            int startIndex, int count, YukonUserContext userContext) {

        SearchResults<DisplayablePao> searchResult =
            filterDao.filter(filter, sorter, startIndex, count, rowMapper);
        return searchResult;
    }

    @Override
    public void sendShed(int loadGroupId, int durationInSeconds) {

        DisplayablePao loadGroup = this.getLoadGroup(loadGroupId);
        if (loadGroup.getPaoIdentifier().getPaoType() == PaoType.LM_GROUP_NEST) {
            throw new IllegalArgumentException("Nest load groups cannot be directly shed.");
        }
        Message msg = new LMCommand(LMCommand.SHED_GROUP, loadGroupId,
                                    durationInSeconds, 0.0);
        loadControlClientConnection.write(msg);

        demandResponseEventLogService.loadGroupShed(loadGroup.getName(),
                                                    durationInSeconds);
    }

    @Override
    public void sendRestore(int loadGroupId) {
        DisplayablePao loadGroup = this.getLoadGroup(loadGroupId);
        if (loadGroup.getPaoIdentifier().getPaoType() == PaoType.LM_GROUP_NEST) {
            throw new IllegalArgumentException("Nest load groups cannot be directly restored.");
        }
        Message msg = new LMCommand(LMCommand.RESTORE_GROUP, loadGroupId,
                                    0, 0.0);
        loadControlClientConnection.write(msg);
        
        demandResponseEventLogService.loadGroupRestore(loadGroup.getName());
    }

    @Override
    public void setEnabled(int loadGroupId, boolean isEnabled) {
        DisplayablePao loadGroup = this.getLoadGroup(loadGroupId);
        if (loadGroup.getPaoIdentifier().getPaoType() == PaoType.LM_GROUP_NEST) {
            throw new IllegalArgumentException("Nest load groups cannot be directly restored.");
        }
        int loadControlCommand = isEnabled ? LMCommand.ENABLE_GROUP
                : LMCommand.DISABLE_GROUP;
        Message msg = new LMCommand(loadControlCommand, loadGroupId, 0, 0.0);
        loadControlClientConnection.write(msg);

        String name = loadGroup.getName();
        if(isEnabled) {
            demandResponseEventLogService.loadGroupEnabled(name);
        } else {
            demandResponseEventLogService.loadGroupDisabled(name);
        }
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
            public DisplayablePao mapRow(YukonResultSet rs)
                    throws SQLException {
                String paoTypeStr = rs.getString("type");
                int deviceTypeId = PaoType.getPaoTypeId(paoTypeStr);
                PaoType paoType = PaoType.getForId(deviceTypeId);
                PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                        paoType);
                DisplayablePao retVal = new DisplayablePaoBase(paoId,
                                                               rs.getString("paoName"));
                return retVal;
            }
        };
}
