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

    void saveOrUpdate(CommandRequestExecutionResult commandRequestExecutionResult);

    int getCountByExecutionId(int commandRequestExecutionId);

    int getSucessCountByExecutionId(int commandRequestExecutionId);

    int getFailCountByExecutionId(int commandRequestExecutionId);

    /**
     * If type is null, returns the count for all unsupported types.
     */
    int getUnsupportedCountByExecutionId(int commandRequestExecutionId, CommandRequestUnsupportedType type);

    List<PaoIdentifier> getDeviceIdsByExecutionId(int commandRequestExecutionId);

    List<PaoIdentifier> getSucessDeviceIdsByExecutionId(int commandRequestExecutionId);

    List<PaoIdentifier> getFailDeviceIdsByExecutionId(int commandRequestExecutionId);

    List<PaoIdentifier> getUnsupportedDeviceIdsByExecutionId(int commandRequestExecutionId,
            CommandRequestUnsupportedType type);

    List<CommandRequestExecutionResult> getResultsByExecutionId(int commandRequestExecutionId,
            CommandRequestExecutionResultsFilterType reportFilterType);

    void saveUnsupported(CommandRequestUnsupported unsupportedCmd);

    void saveUnsupported(Set<? extends YukonPao> devices, int commandRequestExecutionId,
                                CommandRequestUnsupportedType type);
    
    void saveCommandRequestExecutionResult(CommandRequestExecution execution, int deviceId, int errorCode);

    void saveCommandRequestExecutionResult(int executionId, int deviceId, int errorCode, String command);

    void saveExecutionRequest(int executionId, Set<Integer> deviceIds);

    List<PaoIdentifier> getRequestedDeviceIds(int creId);

    List<PaoIdentifier> getUnsupportedDeviceIdsByExecutionId(int commandRequestExecutionId,
            List<CommandRequestUnsupportedType> types);
}
