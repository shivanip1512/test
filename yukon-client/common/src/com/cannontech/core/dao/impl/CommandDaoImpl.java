package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.IDatabaseCache;

public final class CommandDaoImpl implements CommandDao {
    
    @Autowired private IDatabaseCache cache;
    @Autowired private PaoCommandAuthorizationService paoCommandAuthService;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    public static final YukonRowMapper<LiteDeviceTypeCommand> DEVICE_TYPE_COMMAND_MAPPER = new YukonRowMapper<LiteDeviceTypeCommand>() {
        @Override
        public LiteDeviceTypeCommand mapRow(YukonResultSet rs) throws SQLException {
            int deviceCommandId = rs.getInt("DeviceCommandId");
            int commandId = rs.getInt("CommandId");
            String deviceType = rs.getString("deviceType");
            int order = rs.getInt("DisplayOrder");
            String visible = rs.getString("VisibleFlag");
            LiteDeviceTypeCommand command = new LiteDeviceTypeCommand(deviceCommandId, 
                    commandId, 
                    deviceType, 
                    order, 
                    visible.charAt(0));
            return command;
        }
    };
    
    public static final YukonRowMapper<LiteCommand> COMMAND_MAPPER = new YukonRowMapper<LiteCommand>() {
        @Override
        public LiteCommand mapRow(YukonResultSet rs) throws SQLException {
            int commandId = rs.getInt("CommandId");
            String commandText = rs.getString("Command");
            String label = rs.getString("Label");
            String category = rs.getString("Category");
            LiteCommand command = new LiteCommand(commandId, commandText, label, category);
            return command;
        }
    };
    
    @Override
    public List<LiteDeviceTypeCommand> getAllDevTypeCommands(String type) {
        
        List<LiteDeviceTypeCommand> commands = new ArrayList<>();
        synchronized (cache) {
            
            for (LiteDeviceTypeCommand command: cache.getAllDeviceTypeCommands()) {
                if (type.equalsIgnoreCase(command.getDeviceType())) commands.add(command); 
            }
        }
        
        Collections.sort(commands, LiteComparators.liteDeviceTypeCommandComparator);
        
        return commands;
    }
    
    @Override
    public List<LiteDeviceTypeCommand> getAllDevTypeCommands(int commandId) {
        
        List<LiteDeviceTypeCommand> commands = new ArrayList<>();
        
        synchronized (cache) {
            for (LiteDeviceTypeCommand command : cache.getAllDeviceTypeCommands()) {
                if (command.getCommandId() == commandId) commands.add(command);
            }
        }
        
        return commands;
    }
    
    @Override
    public List<LiteCommand> getAllCommandsByCategory(String category) {
        
        List<LiteCommand> commands = new ArrayList<>();
        
        synchronized (cache) {
            
            for (LiteCommand command : cache.getAllCommands().values()) {
                if (category.equalsIgnoreCase(command.getCategory())) commands.add(command);
            }
        }
        Collections.sort(commands, LiteComparators.liteCommandComparator);
        
        return commands;
    }

    @Override
    public List<LiteCommand> filterCommandsForUser(Iterable<LiteCommand> commands, LiteYukonUser user) {
        
        List<LiteCommand> authorized = new ArrayList<LiteCommand>();
        for (LiteCommand command : commands) {
            if (paoCommandAuthService.isAuthorized(user, command.getCommand())) {
                authorized.add(command);
            }
        }
        
        return authorized;
    }
    
    @Override
    public LiteCommand getCommand(int commandId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select CommandId, Command, Label, Category");
        sql.append("from Command");
        sql.append("where CommandId").eq(commandId);
        
        LiteCommand command = jdbcTemplate.queryForObject(sql, COMMAND_MAPPER);
        
        return command;
    }
    
    @Override
    public List<LiteCommand> getAllCommands() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select CommandId, Command, Label, Category");
        sql.append("from Command");
        
        List<LiteCommand> commands = jdbcTemplate.query(sql, COMMAND_MAPPER);
        
        return commands;
    }
    
    @Override
    public List<LiteDeviceTypeCommand> getAllDeviceTypeCommands() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select DeviceCommandId, CommandId, DeviceType, DisplayOrder, VisibleFlag");
        sql.append("from DeviceTypeCommand");
        
        List<LiteDeviceTypeCommand> commands = jdbcTemplate.query(sql, new YukonRowMapper<LiteDeviceTypeCommand>() {
            @Override
            public LiteDeviceTypeCommand mapRow(YukonResultSet rs) throws SQLException {
                int deviceCommandId = rs.getInt("DeviceCommandId");
                int commandId = rs.getInt("CommandId");
                String deviceType = rs.getString("deviceType");
                int order = rs.getInt("DisplayOrder");
                String visible = rs.getString("VisibleFlag");
                LiteDeviceTypeCommand command = new LiteDeviceTypeCommand(deviceCommandId, 
                        commandId, 
                        deviceType, 
                        order, 
                        visible.charAt(0));
                return command;
            }
        });
        
        return commands;
    }
    
    @Override
    public LiteDeviceTypeCommand getDeviceTypeCommand(int deviceCommandId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select DeviceCommandId, CommandId, DeviceType, DisplayOrder, VisibleFlag");
        sql.append("from DeviceTypeCommand");
        sql.append("where DeviceCommandId").eq(deviceCommandId);
        
        LiteDeviceTypeCommand command = jdbcTemplate.queryForObject(sql, DEVICE_TYPE_COMMAND_MAPPER);
        
        return command;
    }
    
}