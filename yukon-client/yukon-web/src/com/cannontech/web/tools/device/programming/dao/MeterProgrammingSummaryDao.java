package com.cannontech.web.tools.device.programming.dao;

import java.util.List;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.device.programming.model.MeterProgramInfo;
import com.cannontech.web.tools.device.programming.model.MeterProgramStatistics;
import com.cannontech.web.tools.device.programming.model.MeterProgramWidgetDisplay;

public interface MeterProgrammingSummaryDao {

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

}
