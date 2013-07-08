package com.cannontech.common.device.commands.dao;

import java.util.List;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecUnsupported;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.pao.PaoIdentifier;

public interface CommandRequestExecutionResultDao {

	public void saveOrUpdate(CommandRequestExecutionResult commandRequestExecutionResult);
	
	public List<Integer> getResultsIdsByExecutionId(int commandRequestExecutionId);
	
	public int getCountByExecutionId(int commandRequestExecutionId);
	public int getSucessCountByExecutionId(int commandRequestExecutionId);
	public int getFailCountByExecutionId(int commandRequestExecutionId);
	public int getUnsupportedCountByExecutionId(int commandRequestExecutionId);
	
	public List<PaoIdentifier> getDeviceIdsByExecutionId(int commandRequestExecutionId);
	public List<PaoIdentifier> getSucessDeviceIdsByExecutionId(int commandRequestExecutionId);
    public List<PaoIdentifier> getFailDeviceIdsByExecutionId(int commandRequestExecutionId);
    public List<PaoIdentifier> getUnsupportedDeviceIdsByExecutionId(int commandRequestExecutionId);
	
	public List<CommandRequestExecutionResult> getResultsByExecutionId(int commandRequestExecutionId, CommandRequestExecutionResultsFilterType reportFilterType);
    public void saveUnsupported(CommandRequestExecUnsupported unsupportedCmd);
}
