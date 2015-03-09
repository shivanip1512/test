package com.cannontech.stars.dr.hardware.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteLMConfiguration;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatFanState;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedulePeriod;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

/**
 * Helper class to build up ExpressCom command strings for LM hardware devices.
 */
public class PorterExpressComCommandBuilder {
    
    private static final Logger log = YukonLogManager.getLogger(PorterExpressComCommandBuilder.class);
    
    public static final int SA205_UNUSED_ADDR = 3909;
    
    private static final String SA205_FILE = "sa_205.txt";
    private static final String SA305_FILE = "sa_305.txt";
    private static final String EXPRESSCOM_FILE = "expresscom.txt";
    private static final String VERSACOM_FILE = "versacom.txt";
    private static final String PROBLEM_FILE = "problem.txt";
    
    @Autowired private StarsCustAccountInformationDao caiDao;
    @Autowired private LMHardwareConfigurationDao configDao;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonListDao yukonListDao;
    
    /**
     * Builds up a list of commands to enable the device (in-service commands)
     * @throws WebClientException
     */
    public List<String> getEnableCommands(LiteLmHardwareBase liteHw, boolean willAlsoConfig) {
        List<String> commands = Lists.newArrayList();
        String sn = liteHw.getManufacturerSerialNumber();

        HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(liteHw.getLmHardwareTypeID()).getYukonDefID());
        HardwareConfigType configType = type.getHardwareConfigType();
        
        if (configType == HardwareConfigType.VERSACOM) {
            commands.add("putconfig vcom service in serial " + sn);
        } else if (configType == HardwareConfigType.EXPRESSCOM) {
            commands.add("putconfig xcom service in serial " + sn);
        } else if (configType == HardwareConfigType.SA205) {
            // To enable a SA205 switch, just reconfig it using the saved configuration.
            if (!willAlsoConfig) {
                commands.add(getConfigCommands(liteHw, true, null).get(0));
            }
        } else if (configType == HardwareConfigType.SA305) {
            // To enable a SA305 switch, we need to tell the switch to use map 1 instead of map 0
            // and then reset values in relay map 1 to their defaults just to be neat and tidy.
            // Tell the switch to use relay map 0 instead of relay map1.
            int utility = liteHw.getLMConfiguration().getSA305().getUtility();
            commands.add("putconfig sa305 serial " + sn + " utility " + utility + " use relay map 0");

            // puts relay 1 back to its default values
            commands.add("putconfig sa305 serial " + sn + " utility " + utility + " lorm1=40");
            commands.add("putconfig sa305 serial " + sn + " utility " + utility + " horm1=65");
        } else {
            throw new BadConfigurationException("No In-Service commands for device with config type: " + configType.name());
        }

        return commands;
    }
    
    /**
     * Builds up a list of commands to enable the device (in-service commands)
     */
    public List<String> getDisableCommands(LiteLmHardwareBase lmhb) {
        
        List<String> commands = Lists.newArrayList();
        HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(lmhb.getLmHardwareTypeID()).getYukonDefID());
        HardwareConfigType configType = type.getHardwareConfigType();
        String sn = lmhb.getManufacturerSerialNumber();
        
        if (configType == HardwareConfigType.VERSACOM) {
            commands.add("putconfig vcom service out serial " + sn);
        } else if (configType == HardwareConfigType.EXPRESSCOM) {
            commands.add("putconfig xcom service out serial " + sn);
        } else if (configType == HardwareConfigType.SA205) {
            // To disable a SA205 switch, reconfig all slots to the unused address.
            commands.add("putconfig sa205 serial " + sn + " assign" 
            + " 1=" + SA205_UNUSED_ADDR 
            + ",2=" + SA205_UNUSED_ADDR 
            + ",3=" + SA205_UNUSED_ADDR 
            + ",4=" + SA205_UNUSED_ADDR 
            + ",5=" + SA205_UNUSED_ADDR 
            + ",6=" + SA205_UNUSED_ADDR);
        } else if (configType == HardwareConfigType.SA305) {
            // To disable a SA305 switch, we need to zero out relay map 1 and tell the switch to use map 1 instead of map 0.
            // Sets map 1 to zero values.
            int utility = lmhb.getLMConfiguration().getSA305().getUtility();
            commands.add("putconfig sa305 serial " + sn + " utility " + utility + " lorm1=0");
            commands.add("putconfig sa305 serial " + sn + " utility " + utility + " horm1=0");
            commands.add("putconfig sa305 serial " + sn + " utility " + utility + " use relay map 1");
        }
        return commands;
    }
    
    public List<String> getOptOutCommands(LiteLmHardwareBase inventory, 
                                          Duration duration, 
                                          boolean useHardwareAddressing,
                                          boolean restoreFirst) {
        String serialNumber = inventory.getManufacturerSerialNumber();
        
        String durationInHours = String.valueOf(duration.getStandardHours());

        if (StringUtils.isBlank(serialNumber)) {
            throw new IllegalArgumentException("Cannot send opt out command. " +
                    "The serial # of inventory with id: " + 
                    inventory.getInventoryID() + " is empty." );
        }
        
        // Build the command
        
        StringBuffer cmd = new StringBuffer();
        cmd.append("putconfig serial ");
        cmd.append(inventory.getManufacturerSerialNumber());
        
        HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(inventory.getLmHardwareTypeID()).getYukonDefID());
        HardwareConfigType configType = type.getHardwareConfigType();
        if (configType == HardwareConfigType.VERSACOM) {
            // Versacom
            cmd.append(" vcom service out temp offhours ");
            cmd.append(durationInHours);
        } else if (configType == HardwareConfigType.EXPRESSCOM) {
            // Expresscom
            cmd.append(" xcom service out temp offhours ");
            cmd.append(durationInHours);
            
            //if true, the opt out also includes a restore command so the switch gets both at once
            if (restoreFirst) {
                cmd.append(" control restore load 0");
            }
        } else if (configType == HardwareConfigType.SA205) {
            //SA205
            cmd.append(" sa205 service out temp offhours ");
            cmd.append(durationInHours);
        } else if (configType == HardwareConfigType.SA305) {
            //SA305
            
            if (!useHardwareAddressing) {
                throw new IllegalStateException("The utility ID of the SA305 switch is unknown");
            }
            
            int utilityId = inventory.getLMConfiguration().getSA305().getUtility();
            cmd.append(" sa305 utility ");
            cmd.append(utilityId);
            cmd.append(" override ");
            cmd.append(durationInHours);
        }
        
        return Lists.newArrayList(cmd.toString());
    }
    
    public List<String> getCancelOptOutCommands(LiteLmHardwareBase inventory, boolean useHardwareAddressing) {
        String serialNumber = inventory.getManufacturerSerialNumber();

        if (StringUtils.isBlank(serialNumber)) {
            throw new IllegalArgumentException("Cannot send cancel opt out command. " +
                    "The serial # of inventory with id: " + 
                    inventory.getInventoryID() + " is empty." );
        }
        
        StringBuffer cmd = new StringBuffer();
        cmd.append("putconfig serial ");
        cmd.append(inventory.getManufacturerSerialNumber());
        
        HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(inventory.getLmHardwareTypeID()).getYukonDefID());
        HardwareConfigType configType = type.getHardwareConfigType();
        if (configType == HardwareConfigType.VERSACOM) {
            // Versacom
            cmd.append(" vcom service in temp");
        } else if (configType == HardwareConfigType.EXPRESSCOM) {
            // Expresscom
            cmd.append(" xcom service in temp");
        } else if (configType == HardwareConfigType.SA205) {
            // SA205
            cmd.append(" sa205 service out temp offhours 0");
        } else if (configType == HardwareConfigType.SA305) {
            // SA305
            
            if (!useHardwareAddressing) {
                throw new IllegalStateException("The utility ID of the SA305 switch is unknown");
            }
            
            int utilityId = inventory.getLMConfiguration().getSA305().getUtility();
            cmd.append(" sa305 utility ");
            cmd.append(utilityId);
            cmd.append(" override 0");
            
        }
        
        return Lists.newArrayList(cmd.toString());
    }
    
    /**
     *  Builds up the commands from the information stored in the LMConfiguration
     */
    public List<String> getConfigCommands(LiteLmHardwareBase liteHw, boolean useHardwareAddressing, Integer groupId) throws BadConfigurationException {
        
        List<String> commands = Lists.newArrayList();
        String[] coldLoads = new String[4];
        String[] tamperDetects = new String[2];
        String sn = liteHw.getManufacturerSerialNumber();
        LiteLMConfiguration config = liteHw.getLMConfiguration();
    
        if (useHardwareAddressing) {
            if (config == null) {
                throw new BadConfigurationException("There is no configuration saved for serial #" + sn + ".");
            }
    
            String freezer = config.getColdLoadPickup();
            if (freezer.compareTo(CtiUtilities.STRING_NONE) != 0 && freezer.length() > 0) {
                coldLoads = StarsUtils.splitString(freezer, ",");
            }
    
            String tamperDetect = config.getTamperDetect();
            if (tamperDetect.compareTo(CtiUtilities.STRING_NONE) != 0 && tamperDetect.length() > 0) {
                tamperDetects = StarsUtils.splitString(tamperDetect, ",");
            }
    
            if (config.getExpressCom() != null) {
                String program = null;
                String splinter = null;
                String load = null;
                String[] programs = config.getExpressCom().getProgram().split(",");
                String[] splinters = config.getExpressCom().getSplinter().split(",");
    
                for (int loadNo = 1; loadNo <= 8; loadNo++) {
                    int prog = 0;
                    if (programs.length >= loadNo && programs[loadNo - 1].length() > 0) {
                        prog = Integer.parseInt(programs[loadNo - 1]);
                    }
                    int splt = 0;
                    if (splinters.length >= loadNo && splinters[loadNo - 1].length() > 0) {
                        splt = Integer.parseInt(splinters[loadNo - 1]);
                    }
    
                    if (prog > 0 || splt > 0) {
                        if (program == null) {
                            program = String.valueOf(prog);
                        } else {
                            program += "," + String.valueOf(prog);
                        }
                        if (splinter == null) {
                            splinter = String.valueOf(splt);
                        } else {
                            splinter += "," + String.valueOf(splt);
                        }
                        if (load == null) {
                            load = String.valueOf(loadNo);
                        } else {
                            load += "," + String.valueOf(loadNo);
                        }
                    }
                }
    
                String command = "putconfig xcom serial " + sn
                + " assign"
                + " S "+ config.getExpressCom().getServiceProvider()
                + " G "+ config.getExpressCom().getGEO()
                + " B " + config.getExpressCom().getSubstation()
                + " F " + config.getExpressCom().getFeeder()
                + " Z " + config.getExpressCom().getZip()
                + " U " + config.getExpressCom().getUserAddress();
                if (load != null) {
                    command += " P " + program + " R " + splinter + " Load " + load;
                }
                commands.add(command);
    
                // Cold load pickup needs to be in a separate command for each individual value.
                for (int j = 0; j < coldLoads.length; j++) {
                    /*
                     * putconfig xcom coldload rx=Z....
                     * r=Z sets all cold load times out to Z
                     * r1=Z sets relay 1 to Z.
                     * r1 to r15 are valid for the 15 possible relays, and
                     * multiple may be in the same message. r= and rx= may not
                     * be used at the same time.
                     * 
                     * putconfig xcom coldload r1=3 r2=33 r4=333. 
                     * This sets relay 1, 2, and 4.
                     * 
                     * Z may be in minutes, hours or seconds. so the following
                     * four commands are identical. Minutes is the default.
                     * r1=1h, r1=60m, r1=3600s, r1=60
                     * 
                     * Expresscom can send at most:
                     * 32767 seconds or 546 minutes or 9 hours.
                     */
                    if (coldLoads[j].length() > 0) {
                        commands.add("putconfig xcom serial " + sn + " coldload r" + (j + 1) + "=" + coldLoads[j]);
                    }
                }
            } else if (config.getVersaCom() != null) {
                String command = "putconfig vcom serial " + sn 
                             + " assign" + " U " + config.getVersaCom().getUtilityID()
                             + " S " + config.getVersaCom().getSection()
                             + " C 0x" + Integer.toHexString(config.getVersaCom().getClassAddress())
                             + " D 0x" + Integer.toHexString(config.getVersaCom().getDivisionAddress());
                commands.add(command);
            } else if (config.getSA205() != null) {
                String command = "putconfig sa205 serial " + sn 
                             + " assign" + " 1=" + config.getSA205().getSlot1()
                             + ",2=" + config.getSA205().getSlot2()
                             + ",3=" + config.getSA205().getSlot3()
                             + ",4=" + config.getSA205().getSlot4()
                             + ",5=" + config.getSA205().getSlot5()
                             + ",6=" + config.getSA205().getSlot6();
                commands.add(command);
    
                // Cold load pickup needs to be in a separate command for each individual value.
                for (int j = 0; j < coldLoads.length; j++) {
                    if (coldLoads[j].length() > 0) {
                        commands.add("putconfig sa205 serial " + sn + " coldload f" + (j + 1) + "=" + coldLoads[j]);
                    }
                }
    
                // Tamper detect also needs to be in a separate command for each value.
                for (int j = 0; j < tamperDetects.length; j++) {
                    if (tamperDetects[j].length() > 0) {
                        commands.add("putconfig sa205 serial " + sn + " tamper f" + (j + 1) + "=" + tamperDetects[j]);
                    }
                }
            }
    
            else if (config.getSA305() != null) {
                int utility = config.getSA305().getUtility();
                String cmd = "putconfig sa305 serial " + sn + " utility " + utility
                             + " assign" + " g=" + config.getSA305().getGroup()
                             + " d=" + config.getSA305().getDivision()
                             + " s=" + config.getSA305().getSubstation()
                             + " f=" + config.getSA305().getRateFamily()
                             + " m=" + config.getSA305().getRateMember();
                commands.add(cmd);
    
                // Cold load pickup needs to be in a separate command for each individual value.
                for (int j = 0; j < coldLoads.length; j++) {
                    if (coldLoads[j].length() > 0) {
                        String command = "putconfig sa305 serial " + sn + " utility " + utility + " coldload f" + (j + 1) + "=" + coldLoads[j];
                        commands.add(command);
                    }
                }
    
                // Tamper detect also needs to be in a separate command for each value.
                for (int j = 0; j < tamperDetects.length; j++) {
                    if (tamperDetects[j].length() > 0) {
                        String command = "putconfig sa305 serial " + sn + " utility " + utility + " tamper f" + (j + 1) + "=" + tamperDetects[j];
                        commands.add(command);
                    }
                }
            } else {
                throw new BadConfigurationException("Unsupported configuration type for serial #" + sn + ".");
            }
        } else if (groupId != null) {
            try {
                String groupName = paoDao.getYukonPAOName(groupId.intValue());
                String command = "putconfig serial " + sn + " template '" + groupName + "'";
                commands.add(command);
            } catch (NotFoundException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            if (liteHw.getAccountID() > 0) {
                
                LiteAccountInfo liteAcctInfo = caiDao.getByAccountId(liteHw.getAccountID());

                for (LiteStarsAppliance liteApp : liteAcctInfo.getAppliances()) {
                    
                    int appInventoryId = liteApp.getInventoryID();
                    int inventoryId = liteHw.getInventoryID();
                    int addressingGroupId = liteApp.getAddressingGroupID();
                    
                    if (appInventoryId == inventoryId && addressingGroupId > 0) {
                        try {
                            String groupName = paoDao.getYukonPAOName(addressingGroupId);
                            String command = "putconfig serial " + sn + " template '" + groupName + "'";
                            commands.add(command);
                        } catch (NotFoundException e) {
                            log.error(e.getMessage(), e);
                        }
                    } else if (appInventoryId == inventoryId && addressingGroupId == 0) {
                        throw new BadConfigurationException("Addressing Group is not assigned.  If no groups are available in the Assigned Group column, please verify that your programs are valid Yukon LM Programs with assigned load groups.");
                    }
                }
            }
        }
    
        return commands;
    }
    
    /**
     * This method returns the ExpressCom command that performs a system-wide
     *  broadcast cancel all opt out event.  All devices on a given customer's network 
     *  should share a common SPID, the highest addressing level, so they can all be
     *  cancelled by sending a single command targeting only the SPID address level.  
     *  
     * @param spid The SPID address to build the broadcast command with.
     */
    public String getBroadcastCancelAllOptOuts(Integer spid) {
        return "putconfig xcom service in temp spid " + spid;
    }
    
    /**
     * Helper method to build a command string for updating schedule (creates a
     * command for only the mode and time of week specified)
     * @param stat - Thermostat to update
     * @param ats - Full thermostat schedule
     * @param timeOfWeek - Time of week for schedule to update
     * @return Command string in format:
     *         <p>
     *         putconfig xcom schedule weekday 01:30, ff, 72, 08:00 ff, 72,
     *         15:00, ff, 72, 20:00, ff, 72 serial 1234567
     *         </p>
     */
    public String buildScheduleCommand(Thermostat stat, 
                                       AccountThermostatSchedule ats, 
                                       TimeOfWeek timeOfWeek,
                                       ThermostatScheduleMode mode) {

        
        ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> entriesByTimeOfWeekMap = ats.getEntriesByTimeOfWeekMultimap();
        List<AccountThermostatScheduleEntry> entries = entriesByTimeOfWeekMap.get(timeOfWeek);

        StringBuilder command = new StringBuilder();
        command.append("putconfig xcom schedule ");

        String timeOfWeekCommand = timeOfWeek.getCommandString();
        if (mode == ThermostatScheduleMode.ALL) {
            command.append("all ");
        } else {
            command.append(timeOfWeekCommand + " ");
        }
        
        int count = 1;
        for(ThermostatSchedulePeriod period : ats.getThermostatType().getPeriodStyle().getAllPeriods()){
            
            AccountThermostatScheduleEntry entry = entries.get(period.getEntryIndex());
            LocalTime startTime = entry.getStartTimeLocalTime();

            Temperature coolTemp = entry.getCoolTemp();
            Temperature heatTemp = entry.getHeatTemp();

            if (period.isPsuedo()) {
                // sending two time/temp values
                command.append("HH:MM,");
                command.append("ff,");
                command.append("ff");

            } else {
                
                String startTimeString = startTime.toString("HH:mm");
                command.append(startTimeString + ",");
                command.append(heatTemp.toFahrenheit().toIntValue() + ",");
                command.append(coolTemp.toFahrenheit().toIntValue());
            }
            
            // No trailing comma on the last season entry cool temp
            if (count++ != entries.size()) {
                command.append(", ");
            }
        }
        
        command.append(" serial " + stat.getSerialNumber());

        return command.toString();
    }
    
    /**
     * Helper method to build a command string for the manual event
     * @param thermostat - Thermostat to send command to
     * @param event - Event for the command
     * @return The command string
     */
    public String buildManualCommand(Thermostat thermostat, ThermostatManualEvent event) {

        StringBuilder command = new StringBuilder();
        command.append("putconfig xcom setstate ");

        if (event.isRunProgram()) {
            // Run scheduled program
            command.append(" run");
        } else {
            ThermostatMode mode = event.getMode();
            if (mode != null) {
                command.append(" system ").append(mode.getCommandString());
            }
            
            // Set manual values
            Integer coolTemperatureInF = null;
            if (event.getPreviousCoolTemperature() != null) 
                coolTemperatureInF = event.getPreviousCoolTemperature().toFahrenheit().toIntValue();
            
            Integer heatTemperatureInF = null;
            if (event.getPreviousHeatTemperature() != null) 
                heatTemperatureInF = event.getPreviousHeatTemperature().toFahrenheit().toIntValue();
            
            // The command was sent from an autoModeEnabled page.  Send both temperatures.
            if(thermostat.getType().isAutoModeEnableable() && event.isAutoModeEnabledCommand()) {
                command.append(" heattemp ").append(heatTemperatureInF);
                command.append(" cooltemp ").append(coolTemperatureInF);

            } else if (thermostat.getType().isUtilityProType() && mode == ThermostatMode.HEAT) {
                command.append(" heattemp ").append(heatTemperatureInF);
            
            } else if (thermostat.getType().isUtilityProType() && mode == ThermostatMode.COOL) {
                command.append(" cooltemp ").append(coolTemperatureInF);

            } else {
                int temperatureInF = (mode == ThermostatMode.HEAT) ? heatTemperatureInF : coolTemperatureInF;
                command.append(" temp ").append(temperatureInF);
            }
            
            ThermostatFanState fanState = event.getFanState();
            if (fanState != null) {
                command.append(" fan ").append(fanState.getCommandString());
            }

            if (event.isHoldTemperature()) {
                command.append(" hold");
            }
        }

        String serialNumber = thermostat.getSerialNumber();
        command.append(" serial ");
        command.append(serialNumber);

        return command.toString();
    }
    
    /**
     * These changes originated with the PMSI Replacement Project at Xcel
     * Some assumptions are made here since we are writing out MACS format configs.
     * -Don't need RouteID
     * -Don't need to worry about hardware addressing (although MACS format does support it)
     * -Also don't need to use the getConfigCommands() method like sendConfigCommand() does.
     * Parameter options corresponds to the infoString field of the switch command queue.
     * It takes the format of "GroupID:XX;RouteID:XX"
     */
    public  void fileWriteConfigCommand(YukonEnergyCompany yec, LiteLmHardwareBase liteHw, boolean forceInService, String options) {
        
        String sn = liteHw.getManufacturerSerialNumber();
        Integer optGroupId = null;
        
        if (options != null) {
            String[] fields = options.split(";");
            for (String field : fields) {
                try {
                    if (field.startsWith("GroupID:")) {
                        optGroupId = Integer.valueOf(field.substring("GroupID:".length()));
                    }
                } catch (NumberFormatException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    
        String loadGroupName = null;
        if (optGroupId != null) {
            try {
                loadGroupName = paoDao.getYukonPAOName(optGroupId);
            } catch (NotFoundException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            optGroupId = configDao.getForInventoryId(liteHw.getInventoryID()).get(0).getAddressingGroupId();
            if (optGroupId > 0) {
                loadGroupName = paoDao.getYukonPAOName(optGroupId);
            }
        }
    
        final String fs = System.getProperty("file.separator");
        File ecDir = new File(StarsUtils.getFileWriteSwitchConfigDir() + fs + yec.getName());
        if (!ecDir.exists()) {
            ecDir.mkdirs();
        }
    
        File file;
        String command = null;
    
        HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(liteHw.getLmHardwareTypeID()).getYukonDefID());
        HardwareConfigType configType = type.getHardwareConfigType();
        if (configType == HardwareConfigType.VERSACOM) {
            file = new File(ecDir, VERSACOM_FILE);
            command = "1," + sn + "," + loadGroupName;
        } else if (configType == HardwareConfigType.EXPRESSCOM) {
            file = new File(ecDir, EXPRESSCOM_FILE);
            command = "1," + sn + "," + loadGroupName;
        } else if (configType == HardwareConfigType.SA205) {
            file = new File(ecDir, SA205_FILE);
            command = sn + ", Config";
        } else if (configType == HardwareConfigType.SA305) {
            file = new File(ecDir, SA305_FILE);
            command = sn + ", Config";
        } else {
            file = new File(ecDir, PROBLEM_FILE);
            command = sn;
        }
    
        if (loadGroupName == null) {
            file = new File(ecDir, PROBLEM_FILE);
            command = sn + ": Unable to find a load group in Yukon with the specified groupID of " + optGroupId;
        }
    
        fileWriteReceiverConfigLine(file, command);
    }
    
    public void fileWriteDisableCommand(YukonEnergyCompany yec, LiteLmHardwareBase liteHw) {
        
        String sn = liteHw.getManufacturerSerialNumber();
        
        final String fs = System.getProperty("file.separator");
        File ecDir = new File(StarsUtils.getFileWriteSwitchConfigDir() + fs + yec.getName());
        if (!ecDir.exists()) {
            ecDir.mkdirs();
        }
    
        File file;
        String command = null;
    
        HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(liteHw.getLmHardwareTypeID()).getYukonDefID());
        HardwareConfigType configType = type.getHardwareConfigType();
        
        if (configType == HardwareConfigType.VERSACOM) {
            file = new File(ecDir, VERSACOM_FILE);
            command = "2," + sn + ",OUT";
        } else if (configType == HardwareConfigType.EXPRESSCOM) {
            file = new File(ecDir, EXPRESSCOM_FILE);
            command = "2," + sn + ",OUT";
        } else if (configType == HardwareConfigType.SA205) {
            file = new File(ecDir, SA205_FILE);
            command = sn + ", Deactivate";
        } else if (configType == HardwareConfigType.SA305) {
            file = new File(ecDir, SA305_FILE);
            command = sn + ", Deactivate";
        } else {
            file = new File(ecDir, PROBLEM_FILE);
            command = sn;
        }
        
        fileWriteReceiverConfigLine(file, command);
    }
    
    public void fileWriteEnableCommand(YukonEnergyCompany yec, LiteLmHardwareBase liteHw) {
        
        String sn = liteHw.getManufacturerSerialNumber();
    
        final String fs = System.getProperty("file.separator");
        File ecDir = new File(StarsUtils.getFileWriteSwitchConfigDir() + fs + yec.getName());
        if (!ecDir.exists()) {
            ecDir.mkdirs();
        }
        
        File file;
        String command = null;
    
        HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(liteHw.getLmHardwareTypeID()).getYukonDefID());
        HardwareConfigType configType = type.getHardwareConfigType();
        
        if (configType == HardwareConfigType.VERSACOM) {
            file = new File(ecDir, VERSACOM_FILE);
            command = "2," + sn + ",IN";
        } else if (configType == HardwareConfigType.EXPRESSCOM) {
            file = new File(ecDir, EXPRESSCOM_FILE);
            command = "2," + sn + ",IN";
        } else if (configType == HardwareConfigType.SA205) {
            file = new File(ecDir, SA205_FILE);
            command = sn + ", Activate";
        } else if (configType == HardwareConfigType.SA305) {
            file = new File(ecDir, SA305_FILE);
            command = sn + ", Activate";
        } else {
            file = new File(ecDir, PROBLEM_FILE);
            command = sn;
        }
    
        fileWriteReceiverConfigLine(file, command);
    }
    
    private static void fileWriteReceiverConfigLine(File commFile, String cmd) {
        try {
            if (!commFile.exists()) {
                commFile.createNewFile();
            }
        } catch (IOException e) {
            CTILogger.error("Failed to create the switch command file...");
            CTILogger.error(e.getMessage(), e);
            return;
        }
    
        PrintWriter fw = null;
        try {
            fw = new PrintWriter(new FileWriter(commFile, true));
            fw.println(cmd);
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
    }
    
}