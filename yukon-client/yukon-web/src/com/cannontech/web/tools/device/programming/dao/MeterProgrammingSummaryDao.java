package com.cannontech.web.tools.device.programming.dao;

import java.util.List;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.device.programming.model.MeterProgramInfo;
import com.cannontech.web.tools.device.programming.model.MeterProgramStatistics;
import com.cannontech.web.tools.device.programming.model.MeterProgramSummaryDetail;
import com.cannontech.web.tools.device.programming.model.MeterProgramWidgetDisplay;
import com.cannontech.web.tools.device.programming.model.MeterProgrammingSummaryFilter;

public interface MeterProgrammingSummaryDao {

	public enum SortBy implements DisplayableEnum {
        DEVICE_NAME("DeviceName"),
        METER_NUMBER("DeviceNumber"),
        DEVICE_TYPE("Type"),
        PROGRAM("ProgramName"),
        STATUS("Status"),
        LAST_UPDATE("LastUpdate");        
        private SortBy(String dbString) {
            this.dbString = dbString;
        }
        
        private final String dbString;

        public String getDbString() {
            return dbString;
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.amr.meterProgramming.summary." + name();
        }
    }
	
    /**
     * Returns meter program configuration to be displayed on a widget
     */
    MeterProgramWidgetDisplay getProgramConfigurationByDeviceId(int deviceId, YukonUserContext context);

    /**
     * Returns total and in progress counts for each program
     */
    List<MeterProgramStatistics> getProgramStatistics(YukonUserContext context);

    /**
     * Returns meter program information to display in the program picker
     */
    List<MeterProgramInfo> getMeterProgramInfos(YukonUserContext context);

    /**
     * Returns summary results
     */
    SearchResults<MeterProgramSummaryDetail> getSummary(MeterProgrammingSummaryFilter filter, PagingParameters paging, SortBy sortBy,
            Direction direction, YukonUserContext context);

}
