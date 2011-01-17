package com.cannontech.amr.porterResponseMonitor.dao;

import java.util.List;

import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorErrorCode;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRule;
import com.cannontech.core.dao.NotFoundException;

public interface PorterResponseMonitorDao {

	public void save(PorterResponseMonitor monitor);

	public PorterResponseMonitor getMonitorById(Integer monitorId)
			throws NotFoundException;

	public List<PorterResponseMonitor> getAllMonitors();

	public List<PorterResponseMonitorRule> getRulesByMonitorId(int monitorId);

	public boolean deleteMonitor(int monitorId);

	public boolean deleteRule(int ruleId);

	public List<PorterResponseMonitorErrorCode> getErrorCodesByRuleId(int ruleId);
}
