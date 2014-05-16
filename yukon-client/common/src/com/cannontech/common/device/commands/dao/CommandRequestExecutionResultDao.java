package com.cannontech.common.device.commands.dao;

import java.util.List;

import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.model.CommandRequestUnsupported;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.pao.PaoIdentifier;

public interface CommandRequestExecutionResultDao {

	public void saveOrUpdate(CommandRequestExecutionResult commandRequestExecutionResult);
	
	public List<Integer> getResultsIdsByExecutionId(int commandRequestExecutionId);
	
	public int getCountByExecutionId(int commandRequestExecutionId);
	public int getSucessCountByExecutionId(int commandRequestExecutionId);
	public int getFailCountByExecutionId(int commandRequestExecutionId);
	
    /**
     * If type is null, returns the count for all unsupported types.
     */
    public int getUnsupportedCountByExecutionId(int commandRequestExecutionId, CommandRequestUnsupportedType type);
	
	public List<PaoIdentifier> getDeviceIdsByExecutionId(int commandRequestExecutionId);
	public List<PaoIdentifier> getSucessDeviceIdsByExecutionId(int commandRequestExecutionId);
    public List<PaoIdentifier> getFailDeviceIdsByExecutionId(int commandRequestExecutionId);
    public List<PaoIdentifier> getUnsupportedDeviceIdsByExecutionId(int commandRequestExecutionId,
                                                                    CommandRequestUnsupportedType type);
	
	public List<CommandRequestExecutionResult> getResultsByExecutionId(int commandRequestExecutionId, CommandRequestExecutionResultsFilterType reportFilterType);
    public void saveUnsupported(CommandRequestUnsupported unsupportedCmd);
    
    /**
     * Create command request for unsupported and not configured devices
     */
    public void create(Iterable<CommandRequestUnsupported> unsupported);
}
