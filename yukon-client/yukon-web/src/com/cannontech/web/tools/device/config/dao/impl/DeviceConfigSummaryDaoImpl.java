package com.cannontech.web.tools.device.config.dao.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.impl.DeviceDaoImpl;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.web.amr.usageThresholdReport.dao.ThresholdReportDao.SortBy;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao;
import com.cannontech.web.tools.device.config.model.DeviceConfigActionHistory;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter;
import com.google.common.collect.ImmutableList;

public class DeviceConfigSummaryDaoImpl implements DeviceConfigSummaryDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigSummaryDaoImpl.class);
    private static List<DeviceRequestType> deviceConfigExecTypes = ImmutableList.of(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY,
        DeviceRequestType.GROUP_DEVICE_CONFIG_SEND, DeviceRequestType.GROUP_DEVICE_CONFIG_READ);
    
    @Override
    public SearchResults<DeviceConfigSummaryDetail> getSummary(DeviceConfigSummaryFilter filter,
            PagingParameters paging, SortBy sortBy, Direction direction) {
        log.debug("Getting summary for filter="+ filter);
        return new SearchResults<>();
    }

    @Override
    public DeviceConfigActionHistory getDeviceConfigActionHistory(int deviceId) {
        return new DeviceConfigActionHistory();
    }
    
    @Override
    public List<SimpleDevice> getDevicesToVerify(){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT paobjectId, Type");
        sql.append("FROM (");
        sql.append("    SELECT cre.CommandRequestExecId, DeviceId, CommandRequestExecType");
        sql.append("        FROM CommandRequestExec cre");
        sql.append("        JOIN CommandRequestExecResult crer on crer.CommandRequestExecId = cre.CommandRequestExecId");
        sql.append("        WHERE CommandRequestExecType").in_k(deviceConfigExecTypes);
        sql.append("        AND cre.CommandRequestExecId =");
        sql.append("        (");
        sql.append("            SELECT MAX(cre2.CommandRequestExecId)");
        sql.append("            FROM CommandRequestExec cre2");
        sql.append("            JOIN CommandRequestExecResult crer2");
        sql.append("                ON crer2.CommandRequestExecId = cre2.CommandRequestExecId");
        sql.append("                WHERE CommandRequestExecType").in_k(deviceConfigExecTypes);
        sql.append("                AND crer2.DeviceID = crer.DeviceId");
        sql.append("        )");
        sql.append(") AS t");
        sql.append("JOIN YukonPAObject ypo");
        sql.append("    ON t.DeviceId = ypo.PAObjectID");
        sql.append("WHERE t.CommandRequestExecType").neq_k(DeviceRequestType.GROUP_DEVICE_CONFIG_VERIFY);
        return jdbcTemplate.query(sql, DeviceDaoImpl.SIMPLE_DEVICE_MAPPER);
    }
}
