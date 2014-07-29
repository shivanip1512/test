package com.cannontech.loadcontrol;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.ILMGroup;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.datamodels.ControlAreaTableModel;
import com.cannontech.loadcontrol.datamodels.GroupTableModel;
import com.cannontech.loadcontrol.datamodels.ProgramTableModel;
import com.cannontech.loadcontrol.gui.MultiLineControlAreaRenderer;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;

public class TdcLoadcontrolUtils {

    /**
     * getValueAt method comment.
     */
    public static synchronized Object getGroupValueAt(ILMGroup grpVal, int col, YukonUserContext userContext) {
        switch (col) {
        case GroupTableModel.GROUP_NAME:
            return grpVal.getName() + (grpVal.isRampingIn() ? " (RI)" : (grpVal.isRampingOut() ? " (RO)" : ""));

        case GroupTableModel.GROUP_STATE:
            if (grpVal.getDisableFlag().booleanValue()) {
                return "DISABLED: " + grpVal.getGroupControlStateString();
            } else {
                return grpVal.getGroupControlStateString();
            }

        case GroupTableModel.TIME: {
            if (grpVal.getGroupTime().getTime() > com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime()) {
                DateFormattingService dateFormattingService =
                    (DateFormattingService) YukonSpringHook.getBean("dateFormattingService");

                String result = dateFormattingService.format(grpVal.getGroupTime(), DateFormatEnum.DATEHM, userContext);
                return result;
            } else {
                return CtiUtilities.STRING_DASH_LINE;
            }
        }

        case GroupTableModel.STATS:
            return grpVal.getStatistics();

        case GroupTableModel.REDUCTION:
            Double reductionTotal = grpVal.getReduction();
            return roundToOneDecPt(reductionTotal);

        default:
            return null;
        }
    }

    private static String roundToOneDecPt(Double reductionTotal) {
        DecimalFormat formater = new DecimalFormat("#.#");
        return formater.format(reductionTotal.doubleValue());
    }

    public static String getTriggerText(final LMControlArea value, final int col) {
        StringBuffer topStrBuf = new StringBuffer();
        StringBuffer botStrBuf = new StringBuffer();

        if (value.getTriggerVector().size() > 0) {
            for (int i = 0; i < value.getTriggerVector().size(); i++) {
                LMControlAreaTrigger trigger = value.getTriggerVector().get(i);

                if (trigger.getTriggerNumber().intValue() == 1) {
                    MultiLineControlAreaRenderer.setTriggerStrings(trigger, null, topStrBuf, col);
                } else if (trigger.getTriggerNumber().intValue() == 2) {
                    MultiLineControlAreaRenderer.setTriggerStrings(trigger, null, botStrBuf, col);
                } else {
                    com.cannontech.clientutils.CTILogger.info("**** ControlArea '" + value.getYukonName()
                        + "' has more than 2 Triggers defined for it.");
                }

            }

        } else {
            topStrBuf = new StringBuffer("(No Triggers Found)");
        }

        return topStrBuf.toString() + "<BR>" + botStrBuf.toString();
    }

    public static synchronized Object getProgramValueAt(LMProgramBase program, int col, YukonUserContext userContext) {
        DateFormattingService dateFormattingService =
            (DateFormattingService) YukonSpringHook.getBean("dateFormattingService");
        switch (col) {
        case ProgramTableModel.PROGRAM_NAME:
            return program.getYukonName() + (program.isRampingIn() ? " (RI)" : (program.isRampingOut() ? " (RO)" : ""));

        case ProgramTableModel.CURRENT_STATUS:
            if (program.getDisableFlag().booleanValue()) {
                return "DISABLED: " + LMProgramBase.getProgramStatusString(program.getProgramStatus().intValue());
            } else {
                return LMProgramBase.getProgramStatusString(program.getProgramStatus().intValue());
            }

        case ProgramTableModel.START_TIME:
            if (program.getDisableFlag().booleanValue()) {
                return CtiUtilities.STRING_DASH_LINE;
            } else {
                if (program.getStartTime() == null || program.getStartTime().before(CtiUtilities.get1990GregCalendar())) {
                    return CtiUtilities.STRING_DASH_LINE;
                } else {
                    String result =
                        dateFormattingService.format(program.getStartTime().getTime(), DateFormatEnum.DATEHM,
                            userContext);
                    return result;
                }
            }

        case ProgramTableModel.CURRENT_GEAR: {
            if (program instanceof IGearProgram) {
                return getCurrentGear((IGearProgram) program);
            } else {
                return CtiUtilities.STRING_DASH_LINE;
            }
        }

        case ProgramTableModel.STOP_TIME:
            if (program.getDisableFlag().booleanValue()) {
                return CtiUtilities.STRING_DASH_LINE;
            } else {
                // return dashes if stop time is null, <1990 or >= 2035
                if (program.getStopTime() == null || program.getStopTime().before(CtiUtilities.get1990GregCalendar())
                    || program.getStopTime().compareTo(CtiUtilities.get2035GregCalendar()) >= 0) {
                    return CtiUtilities.STRING_DASH_LINE;
                } else {
                    String result =
                        dateFormattingService.format(program.getStopTime().getTime(), DateFormatEnum.DATEHM,
                            userContext);
                    return result;
                }
            }

        case ProgramTableModel.PRIORITY:
            return (program.getStartPriority().intValue() <= 0 ? new Integer(1) : program.getStartPriority());

        case ProgramTableModel.REDUCTION:
            Double reductionTotal = program.getReductionTotal();
            return roundToOneDecPt(reductionTotal);

        default:
            return null;
        }
    }

    /**
     * getValueAt method comment.
     * 
     * @param userContext
     * @param currentUser TODO
     */
    public static synchronized Object getControlAreaValueAt(LMControlArea lmCntrArea, int col,
            YukonUserContext userContext) {

        switch (col) {
        case ControlAreaTableModel.AREA_NAME:
            return lmCntrArea.getYukonName();

        case ControlAreaTableModel.CURRENT_STATE: {
            if (lmCntrArea.getDisableFlag().booleanValue()) {
                return "DISABLED: "
                    + LMControlArea.getControlAreaStateString(lmCntrArea.getControlAreaState().intValue());
            } else {
                return LMControlArea.getControlAreaStateString(lmCntrArea.getControlAreaState().intValue());
            }
        }

        case ControlAreaTableModel.VALUE_THRESHOLD: {
            // data is on the trigger object
            return lmCntrArea;
        }

        case ControlAreaTableModel.TIME_WINDOW: {
            String timeString = null;
            DateFormattingService dateFormattingService =
                (DateFormattingService) YukonSpringHook.getBean("dateFormattingService");
            SystemDateFormattingService systemDateFormattingService =
                (SystemDateFormattingService) YukonSpringHook.getBean("systemDateFormattingService");
            Calendar startDate = systemDateFormattingService.getSystemCalendar();
            Calendar stopDate = systemDateFormattingService.getSystemCalendar();
            String winStart = CtiUtilities.STRING_DASH_LINE;
            String winStop = CtiUtilities.STRING_DASH_LINE;

            int winStartInt = lmCntrArea.getCurrentDailyStartTime();
            int winStopInt = lmCntrArea.getCurrentDailyStopTime();

            if (winStartInt < 0) {
                if (lmCntrArea.getDefDailyStartTime() > -1) {
                    winStartInt = lmCntrArea.getDefDailyStartTime();
                }
            }

            if (winStopInt < 0) {
                if (lmCntrArea.getDefDailyStopTime() > -1) {
                    winStopInt = lmCntrArea.getDefDailyStopTime();
                }
            }

            if (winStartInt > -1) {
                startDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
                startDate.set(GregorianCalendar.MINUTE, 0);
                startDate.set(GregorianCalendar.SECOND, winStartInt);

                winStart = dateFormattingService.format(startDate.getTime(), DateFormatEnum.TIME_TZ, userContext);
            }

            if (winStopInt > -1) {
                stopDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
                stopDate.set(GregorianCalendar.MINUTE, 0);
                stopDate.set(GregorianCalendar.SECOND, winStopInt);

                winStop = dateFormattingService.format(stopDate.getTime(), DateFormatEnum.TIME_TZ, userContext);
            }

            timeString = winStart + " - " + winStop;

            return timeString;

        }

        case ControlAreaTableModel.START_TIME: {
            SystemDateFormattingService systemDateFormattingService =
                (SystemDateFormattingService) YukonSpringHook.getBean("systemDateFormattingService");
            Calendar startDate = systemDateFormattingService.getSystemCalendar();
            String winStart = CtiUtilities.STRING_DASH_LINE;

            int winStartInt = lmCntrArea.getCurrentDailyStartTime();

            if (winStartInt < 0) {
                if (lmCntrArea.getDefDailyStartTime() > -1) {
                    winStartInt = lmCntrArea.getDefDailyStartTime();
                }
            }

            if (winStartInt > -1) {
                startDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
                startDate.set(GregorianCalendar.MINUTE, 0);
                startDate.set(GregorianCalendar.SECOND, winStartInt);

                DateFormattingService dateFormattingService =
                    (DateFormattingService) YukonSpringHook.getBean("dateFormattingService");
                winStart = dateFormattingService.format(startDate.getTime(), DateFormatEnum.TIME, userContext);
            }
            return winStart;
        }

        case ControlAreaTableModel.STOP_TIME: {
            SystemDateFormattingService systemDateFormattingService =
                (SystemDateFormattingService) YukonSpringHook.getBean("systemDateFormattingService");
            Calendar stopDate = systemDateFormattingService.getSystemCalendar();
            String winStop = CtiUtilities.STRING_DASH_LINE;

            int winStopInt = lmCntrArea.getCurrentDailyStopTime();

            if (winStopInt < 0) {
                if (lmCntrArea.getDefDailyStopTime() > -1) {
                    winStopInt = lmCntrArea.getDefDailyStopTime();
                }
            }

            if (winStopInt > -1) {
                stopDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
                stopDate.set(GregorianCalendar.MINUTE, 0);
                stopDate.set(GregorianCalendar.SECOND, winStopInt);

                DateFormattingService dateFormattingService =
                    (DateFormattingService) YukonSpringHook.getBean("dateFormattingService");
                winStop = dateFormattingService.format(stopDate.getTime(), DateFormatEnum.TIME, userContext);
            }
            return winStop;
        }

        case ControlAreaTableModel.PEAK_PROJECTION: {
            // data is on the trigger object
            return lmCntrArea;
        }

        case ControlAreaTableModel.PRIORITY: {
            // data is on the trigger object
            return (lmCntrArea.getCurrentPriority().intValue() <= 0 ? new Integer(1) : lmCntrArea.getCurrentPriority());
        }

        case ControlAreaTableModel.ATKU: {
            // data is on the trigger object
            return lmCntrArea;
        }

        default:
            return null;
        }
    }

    private static String getCurrentGear(IGearProgram dPrg) {
        LMProgramDirectGear gear = null;

        // get the current gear we are in
        for (int i = 0; i < dPrg.getDirectGearVector().size(); i++) {
            gear = dPrg.getDirectGearVector().get(i);

            if (dPrg.getCurrentGearNumber().intValue() == gear.getGearNumber().intValue()) {
                return gear.getGearName();
            }
        }

        // should not get here
        com.cannontech.clientutils.CTILogger.info("*** Unable to find gear #: " + gear.getGearNumber()
            + " was not found.");

        return "(Gear #" + gear.getGearNumber() + " not Found)";
    }
}
