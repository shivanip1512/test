package com.cannontech.common.device.commands.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.device.commands.dao.model.CommandRequestUnsupported;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;

public interface CommandRequestExecutionResultDao {

    public void saveOrUpdate(CommandRequestExecutionResult commandRequestExecutionResult);

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

    public List<CommandRequestExecutionResult> getResultsByExecutionId(int commandRequestExecutionId,
            CommandRequestExecutionResultsFilterType reportFilterType);

    public void saveUnsupported(CommandRequestUnsupported unsupportedCmd);

    public void saveUnsupported(Set<? extends YukonPao> devices, int commandRequestExecutionId,
                                CommandRequestUnsupportedType type);
    
    public void saveCommandRequestExecutionResult(CommandRequestExecution execution, int deviceId, int errorCode);
}
