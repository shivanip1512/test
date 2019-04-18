package com.cannontech.web.capcontrol.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.cannontech.capcontrol.ScheduleCommand;
import com.cannontech.capcontrol.dao.DmvTestDao;
import com.cannontech.cbc.commands.CapControlCommandExecutor;
import com.cannontech.cbc.commands.CommandHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterDao;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.core.schedule.dao.PaoScheduleDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.pao.PaoScheduleAssignment;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.model.VerifyBanks;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.capcontrol.filter.ScheduleAssignmentCommandFilter;
import com.cannontech.web.capcontrol.filter.ScheduleAssignmentFilter;
import com.cannontech.web.capcontrol.filter.ScheduleAssignmentRowMapper;
import com.cannontech.web.capcontrol.service.PaoScheduleService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap;

@Service
public class PaoScheduleServiceImpl implements PaoScheduleService {
    
    @Autowired private CapControlCommandExecutor executor;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private FilterDao filterDao;
    @Autowired private PaoScheduleDao paoScheduleDao;
    @Autowired private DmvTestDao dmvTestDao;
    @Autowired private PaoScheduleServiceHelper paoScheduleServiceHelper;
    
    private static final String NO_FILTER = "All";

    private Map<String, Number> supportedDurations = ImmutableMap.of(
        "min", Duration.standardMinutes(1).getStandardSeconds(),
        "hr",  Duration.standardHours(1).getStandardSeconds(),
        "day", Duration.standardDays(1).getStandardSeconds(),
        "wk",  Duration.standardDays(7).getStandardSeconds());
    
    private static final Logger log = YukonLogManager.getLogger(PaoScheduleServiceImpl.class);

    
    @Override
    public Map<String, Collection<String>> getDeviceToCommandMapForSchedule(int id) {
        List<PaoScheduleAssignment> assignments = paoScheduleDao.getScheduleAssignmentByScheduleId(id);

        assignments = paoScheduleServiceHelper.getAssignmentsByDMVFilter(assignments);
        Multimap<String,String> deviceToCommands = HashMultimap.create();
        
        for (PaoScheduleAssignment assignment : assignments) {
            deviceToCommands.put(assignment.getDeviceName(), assignment.getCommandName());
        }
        
        return deviceToCommands.asMap();
    }
    
    @Override
    public List<PaoScheduleAssignment> getAssignmentsByFilter(String command, String schedule) {
        
        //Create filters
        List<UiFilter<PaoScheduleAssignment>> filters = new ArrayList<UiFilter<PaoScheduleAssignment>>();
        
        if (StringUtils.isNotEmpty(command) && !command.equals(NO_FILTER)) {
            filters.add(new ScheduleAssignmentCommandFilter(ScheduleCommand.getScheduleCommand(command)));
        }
        if (StringUtils.isNotEmpty(schedule) && !schedule.equals(NO_FILTER)) {
            filters.add(new ScheduleAssignmentFilter(schedule));
        }
        UiFilter<PaoScheduleAssignment> filter = UiFilterList.wrap(filters);
        
        Comparator<PaoScheduleAssignment> sorter = new Comparator<PaoScheduleAssignment>() {
            @Override
            public int compare(PaoScheduleAssignment assignment1, PaoScheduleAssignment assignment2) {
                return assignment1.compareTo(assignment2);
            }
        };
        
        ScheduleAssignmentRowMapper rowMapper = new ScheduleAssignmentRowMapper();
        
        //Filter, sort and get search results
        List<PaoScheduleAssignment> assignments = filterDao.filter(filter, sorter, rowMapper);
        assignments = paoScheduleServiceHelper.getAssignmentsByDMVFilter(assignments);
        return assignments;
    }
    
    @Override
    public void delete(int id) {
        
        List<PaoScheduleAssignment> assignments = paoScheduleDao.getScheduleAssignmentByScheduleId(id);
        paoScheduleDao.delete(id);
        
        for (PaoScheduleAssignment assignment : assignments) {
            
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(assignment.getPaoId());
            
            DBChangeMsg dbChange = new DBChangeMsg(assignment.getPaoId(),
                DBChangeMsg.CHANGE_PAO_DB,
                pao.getPaoType().getPaoCategory().getDbString(),
                pao.getPaoType().getDbString(),
                DbChangeType.UPDATE);
            
            dbChangeManager.processDbChange(dbChange);
        }
        
    }
    
    
    @Override
    public AssignmentStatus assignCommand(int scheduleId, ScheduleCommand cmd, List<Integer> paoIds, String cmdInput, Integer dmvTestId) {
        
        if (cmd == null) {
            return AssignmentStatus.INVALID;
        }
        
        if (cmd != ScheduleCommand.VerifyNotOperatedIn) {
            cmdInput = cmd.getCommandName();
        } 
        else if (StringUtils.isEmpty(cmdInput)
                || !cmdInput.toLowerCase().startsWith(ScheduleCommand.VerifyNotOperatedIn.getCommandName().toLowerCase().substring(0, 41))) {
            
            return AssignmentStatus.INVALID;
        }
        
        if (paoIds.size() == 0) {
            return AssignmentStatus.NO_DEVICES;
        } else {
            List<PaoScheduleAssignment> assignments = new ArrayList<PaoScheduleAssignment>();

            if (cmd == ScheduleCommand.DmvTest && dmvTestId == null) {
                return AssignmentStatus.NO_DMVTEST;
            }

            for (Integer paoId : paoIds) {
                PaoScheduleAssignment newAssignment = new PaoScheduleAssignment();

                if (cmd == ScheduleCommand.DmvTest) {
                    String dmvTestCommand= dmvTestDao.getDmvTestById(dmvTestId).getName();
                    newAssignment.setCommandName(cmd.getCommandName() + ": " + dmvTestCommand);
                } else {
                    newAssignment.setCommandName(cmdInput);
                }
                
                newAssignment.setPaoId(paoId);
                newAssignment.setScheduleId(scheduleId);
                newAssignment.setDisableOvUv("N");
                
                assignments.add(newAssignment);
            }
            
            try {
                paoScheduleDao.assignCommand(assignments);
                
                //Send DB Change
                //These can only be assigned to sub bus objects right now, if that changes, this needs to change with it.
                for (int paoId : paoIds) {
                    
                    LiteYukonPAObject pao = dbCache.getAllPaosMap().get(paoId);
                    
                    DBChangeMsg dbChange = new DBChangeMsg(paoId,
                                                           DBChangeMsg.CHANGE_PAO_DB,
                                                           pao.getPaoType().getPaoCategory().getDbString(),
                                                           pao.getPaoType().getDbString(),
                                                           DbChangeType.UPDATE);
                    dbChangeManager.processDbChange(dbChange);
                }
                return AssignmentStatus.SUCCESS;
                
            } catch (DataIntegrityViolationException e) {
                return AssignmentStatus.DUPLICATE;
            }
        }
    }
    
    
    @Override
    public boolean unassignCommand(int eventId) {
        
        PaoScheduleAssignment assignment = paoScheduleDao.getScheduleAssignmentByEventId(eventId);
        
        boolean success = paoScheduleDao.unassignCommandByEventId(eventId);
        
        if (success) {
            
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(assignment.getPaoId());
            
            //Send DB Change for affected bus.
            DBChangeMsg dbChange = new DBChangeMsg(pao.getLiteID(),
                                                   DBChangeMsg.CHANGE_PAO_DB,
                                                   pao.getPaoType().getPaoCategory().getDbString(),
                                                   pao.getPaoType().getDbString(),
                                                   DbChangeType.UPDATE);
            dbChangeManager.processDbChange(dbChange);
        }
        
        return success;
        
    }

    
    @Override
    public boolean sendStartCommand(PaoScheduleAssignment assignment, LiteYukonUser user) {
        boolean isCommandValid = true;
        ScheduleCommand schedCommand = null;
        
        try{
            schedCommand = ScheduleCommand.getScheduleCommand(assignment.getCommandName());
        } catch(IllegalArgumentException e) {
            isCommandValid = false;
            log.error("Run schedule assignment command failed.  Invalid command: " + assignment.getCommandName());
        }
        
        if (isCommandValid) {
            
            CapControlCommand command;
            if (schedCommand == ScheduleCommand.VerifyNotOperatedIn) {
                //VerifyNotOperatedIn command is special.  It has a time value associated
                //with it that must be parsed from the command string.
                long secondsNotOperatedIn = ScheduleCommand.DEFAULT_INACTIVITY_TIME;
                try {
                    secondsNotOperatedIn = parseSecondsNotOperatedIn(assignment);
                    command = CommandHelper.buildVerifyInactiveBanks(user, CommandType.VERIFY_INACTIVE_BANKS, 
                                                                     assignment.getPaoId(), false, secondsNotOperatedIn);
                } catch (Exception e) {
                    log.error("There was a problem parsing the time period for a 'Verify Capbanks Not Operated in' command", e);
                   return false;
                }

            } else if (ScheduleCommand.getVerifyCommandsList().contains(schedCommand)) {
                command = CommandHelper.buildVerifyBanks(user, CommandType.getForId(schedCommand.getCapControlCommand()), 
                        assignment.getPaoId(), false);
            } else {
                command = CommandHelper.buildItemCommand(schedCommand.getCapControlCommand(), assignment.getPaoId(), 
                        user);
            }
            try {
                executor.execute(command);
            } catch (CommandExecutionException e) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean sendStop(int deviceId, LiteYukonUser user) {
        
        try {
            executor.execute(CommandHelper.buildStopVerifyBanks(user, CommandType.STOP_VERIFICATION, deviceId));
            return true;
        } catch (CommandExecutionException e) {
            log.warn("caught exception in stopSchedule", e);
            return false;
        }
        
    }

    @Override
    public int sendStopCommands(String filterByCommand, String filterBySchedule, LiteYukonUser user) {

        List<PaoScheduleAssignment> assignments = getAssignmentsByFilter(filterByCommand, filterBySchedule);
        
        int commandsSentCount = 0;
        
        for (PaoScheduleAssignment assignment : assignments) {
            
            boolean stopApplicable = true;
            int deviceId = assignment.getPaoId();
            ScheduleCommand schedCommand = null;
            
            try {
                schedCommand = ScheduleCommand.getScheduleCommand(assignment.getCommandName());
                if (schedCommand == ScheduleCommand.ConfirmSub || schedCommand == ScheduleCommand.SendTimeSyncs) {
                    //stop is not applicable to schedules with these commands
                    stopApplicable = false;
                    log.info("Schedule assignment stop ignored.  Command: " + assignment.getCommandName() + 
                            " is not a verification command.");
                }
            } catch(IllegalArgumentException e) {
                //invalid ScheduleCommand
                stopApplicable = false;
                log.error("Stop schedule assignment command failed.  Invalid command: " + assignment.getCommandName());
            }
            
            //send stop command
            if (stopApplicable) {
                VerifyBanks command = CommandHelper.buildStopVerifyBanks(user, CommandType.STOP_VERIFICATION, deviceId);
                try {
                    executor.execute(command);
                    commandsSentCount++;
                } catch (CommandExecutionException e) {
                    log.error("Stop schedule assignment command failed: " + assignment.getCommandName(), e);
                }
            }    
        }
        
        return commandsSentCount;
    }
    
    private long parseSecondsNotOperatedIn(PaoScheduleAssignment assignment) {
        String timeString = assignment.getCommandName().replaceAll(ScheduleCommand.VerifyNotOperatedIn.getCommandName().substring(0, 41), "").trim();
    
        //parse min/hr/day/wk value from command string
        String[] tokens = timeString.replaceAll(" +", " ").split("\\s");

        long seconds = 0;
        for (int i = 0; i < tokens.length; i+=2) {
            seconds += supportedDurations.get(tokens[i+1]).intValue() * Integer.parseInt(tokens[i]);
        }
        
        return seconds;
    }
}