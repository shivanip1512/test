package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.command.Command;
import com.cannontech.yukon.IDatabaseCache;

public final class CommandDaoImpl implements CommandDao {
    
    @Autowired private IDatabaseCache cache;
    @Autowired private PaoCommandAuthorizationService paoCommandAuthService;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired protected DBPersistentDao dbPersistentDao;
        
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
    public void updateCommand(LiteCommand command) {
        dbPersistentDao.performDBChange(LiteFactory.createDBPersistent(command), TransactionType.UPDATE);
    }
    
    @Override
    public void deleteDeviceTypeCommand(LiteDeviceTypeCommand typeCommand) {
        dbPersistentDao.performDBChange(LiteFactory.createDBPersistent(typeCommand), TransactionType.DELETE);
    }
    
    @Override
    public void deleteCommand(LiteCommand command) {
        dbPersistentDao.performDBChange(LiteFactory.createDBPersistent(command), TransactionType.DELETE);
    }
      
      
    @Override
    public int createCommand(String commandString, String label, String commandCategory) {
        Command command = new Command();
        command.setLabel(label);
        command.setCommand(commandString);
        command.setCategory(commandCategory);
 
        dbPersistentDao.performDBChange(command, TransactionType.INSERT);
        return command.getCommandID();
    }
    
    @Override
    public void createDeviceTypeCommand(int commandId, PaoType paoType, int displayOrder, boolean isVisible) {
                        
        com.cannontech.database.db.command.DeviceTypeCommand dbCommand = new  com.cannontech.database.db.command.DeviceTypeCommand();
        dbCommand.setDeviceCommandID(com.cannontech.database.db.command.DeviceTypeCommand.getNextID(CtiUtilities.getDatabaseAlias()));
        dbCommand.setCommandID(commandId);
        dbCommand.setDeviceType(paoType.getDbString());
        dbCommand.setDisplayOrder(displayOrder);
        dbCommand.setVisibleFlag(isVisible ? 'Y' : 'N');

        dbPersistentDao.performDBChange(dbCommand, TransactionType.INSERT);
    }
        
    @Override
    public void updateDeviceTypeCommand(LiteDeviceTypeCommand command) {
        dbPersistentDao.performDBChange(LiteFactory.createDBPersistent(command), TransactionType.UPDATE);
    }
    
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
    public List<LiteCommand> deleteUnusedCommands() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CommandId, Command, Label, Category");
        sql.append("FROM Command");
        sql.append("WHERE CommandId NOT IN (SELECT CommandId FROM DeviceTypeCommand)");
        
        List<LiteCommand> commands = jdbcTemplate.query(sql, COMMAND_MAPPER);
        commands.forEach(command -> deleteCommand(command));
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
 
    @Override    
    public String buildTOUScheduleCommand(int scheduleId) {
        
        String command = "putconfig tou ";
        List<LiteTOUSchedule> schedules = cache.getAllTOUSchedules();
        
        LiteTOUSchedule liteSchedule = null;
        for (LiteTOUSchedule schedule : schedules) {
            if (schedule.getScheduleID() == scheduleId) {
                liteSchedule = schedule;
            } 
        }
        
        if (liteSchedule != null) {
            
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT d.TOUDayID, d.TOUDayName, dm.TOUDayOffset, switchrate, switchoffset");
            sql.append("FROM TOUDay d JOIN TOUDayMapping dm ON dm.toudayid = d.toudayid");
            sql.append("JOIN toudayrateswitches trs ON trs.toudayid = dm.toudayid");
            sql.append("WHERE dm.touscheduleid").eq(liteSchedule.getScheduleID()); 
            sql.append("ORDER BY toudayoffset, switchoffset");

            int [] days = new int[] {-1, -1, -1, -1}; // At most 4 day mappings are allowed.
            int [] dayOffsets = new int[8];
            
            List<String> strings = jdbcTemplate.query(sql, new YukonRowMapper<String>() {
                boolean exists = false;
                int currentIndex = 0;
                int numDaysFound = 0;
                int currentDayOffset = -1;

                @Override
                public String mapRow(YukonResultSet rset) throws SQLException {
                    String scheduleStr = "";
                    int dayId = rset.getInt("TOUDayID");
                    int dayOffset = rset.getInt("TOUDayOffset");
                    String switchRate = rset.getString("SwitchRate");
                    int switchOffset = rset.getInt("SwitchOffset");
               
                    if (currentDayOffset != dayOffset) {
                        exists = false;
                        for (int i = 0; i < numDaysFound && i < days.length; i++) {
                            if (days[i] == dayId) {
                                currentIndex = i;
                                exists = true;
                                break;
                            }
                        }
                        
                        if (!exists) {
                            currentIndex = numDaysFound;
                            days[numDaysFound++] = dayId;
                            scheduleStr += " schedule " + (currentIndex+1);
                        }
                        dayOffsets[dayOffset-1] = currentIndex+1;
                    }
                    if (!exists) {
                        scheduleStr += " " + switchRate + "/" + convertSecondsToTimeString(switchOffset);
                    }
                    currentDayOffset = dayOffset;
                    return scheduleStr;
                }
            });    

            String scheduleStr = StringUtils.join(strings, "");
            for (int offset : dayOffsets) {
                command += offset;
            }
                
            command += " " + scheduleStr + " default " + liteSchedule.getDefaultRate();
        }
        return command;
    }
    
    private static String convertSecondsToTimeString(int seconds) {
        
        DecimalFormat format = new DecimalFormat("00");
        int hour = seconds / 3600;
        int temp = seconds % 3600;
        int min = temp / 60;
        
        return hour + ":" + format.format(min);
    }
}