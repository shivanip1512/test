package com.cannontech.common.databaseMigration.service;

import java.util.List;

import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DatabaseMigrationImportExecutor<T> {
    public CommandResultHolder execute(List<T> commands, CommandRequestExecutionType type, LiteYukonUser user)
    throws CommandCompletionException;


    
}