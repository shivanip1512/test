package com.cannontech.loadcontrol;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.dr.program.service.ConstraintContainer;
import com.cannontech.loadcontrol.data.ILMGroup;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMCurtailCustomer;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.gui.manualentry.ResponseProg;
import com.cannontech.loadcontrol.messages.ConstraintViolation;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.messages.LMManualControlResponse;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.message.util.ServerRequestImpl;
import com.cannontech.util.ServletUtil;
import com.google.common.collect.Lists;

/**
 * @author rneuharth
 *
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LCUtils {
    public static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");
    public static final SimpleDateFormat TEMPORAL_FORMATTER = new SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");

    public static final Color[] CELL_COLORS = {
        // Inactive
        Color.BLACK,
        // Active, Manual Active & Fully Active
        Color.GREEN,
        // Scheduled
        Color.YELLOW,

        // Disabled program
        Color.RED,

        // Scheduled
        Color.CYAN,
        // Notified
        Color.ORANGE };

    // available times a group can be shed for
    public static final String[] SHED_STRS = { "5 minutes", "7 minutes", "10 minutes", "15 minutes", "20 minutes",
        "30 minutes", "45 minutes", "1 hour", "2 hours", "3 hours", "4 hours", "6 hours", "8 hours" };

    /**
	 * 
	 */
    private LCUtils() {
        super();
    }

    public static synchronized String getFgColor(LMControlArea area) {
        Color retColor = Color.BLACK;

        if (area != null) {
            if (area.getDisableFlag().booleanValue()) {
                retColor = CELL_COLORS[3];
            } else if (area.getControlAreaState().intValue() == LMControlArea.STATE_INACTIVE) {
                retColor = CELL_COLORS[0];
            } else if (area.getControlAreaState().intValue() == LMControlArea.STATE_PARTIALLY_ACTIVE
                || area.getControlAreaState().intValue() == LMControlArea.STATE_FULLY_ACTIVE
                || area.getControlAreaState().intValue() == LMControlArea.STATE_MANUAL_ACTIVE) {
                retColor = CELL_COLORS[1];
            } else if (area.getControlAreaState().intValue() == LMControlArea.STATE_CNTRL_ATTEMPT
                || area.getControlAreaState().intValue() == LMControlArea.STATE_FULLY_SCHEDULED
                || area.getControlAreaState().intValue() == LMControlArea.STATE_PARTIALLY_SCHEDULED) {
                retColor = CELL_COLORS[2];
            }

        }

        return "#" + ServletUtil.getHTMLColor(retColor);
    }

    public static synchronized String getFgColor(LMProgramBase prg) {
        Color retColor = Color.BLACK;

        if (prg != null) {
            if (prg.getDisableFlag().booleanValue()) {
                retColor = CELL_COLORS[3];
            } else if (prg.getProgramStatus().intValue() == LMProgramBase.STATUS_INACTIVE
                || prg.getProgramStatus().intValue() == LMProgramBase.STATUS_NON_CNTRL) {
                retColor = CELL_COLORS[0];
            } else if (prg.getProgramStatus().intValue() == LMProgramBase.STATUS_ACTIVE
                || prg.getProgramStatus().intValue() == LMProgramBase.STATUS_FULL_ACTIVE
                || prg.getProgramStatus().intValue() == LMProgramBase.STATUS_MANUAL_ACTIVE
                || prg.getProgramStatus().intValue() == LMProgramBase.STATUS_TIMED_ACTIVE) {
                retColor = CELL_COLORS[1];
            } else if (prg.getProgramStatus().intValue() == LMProgramBase.STATUS_NOTIFIED) {
                retColor = CELL_COLORS[4];
            } else if (prg.getProgramStatus().intValue() == LMProgramBase.STATUS_SCHEDULED
                || prg.getProgramStatus().intValue() == LMProgramBase.STATUS_CNTRL_ATTEMPT) {
                retColor = CELL_COLORS[5];
            } else if (prg.getProgramStatus().intValue() == LMProgramBase.STATUS_STOPPING) {
                retColor = CELL_COLORS[2];
            }
        }

        return "#" + ServletUtil.getHTMLColor(retColor);
    }

    public static synchronized String getFgColor(ILMGroup grp) {
        Color retColor = Color.BLACK;

        if (grp != null) {
            String state = grp.getGroupControlStateString();

            if (grp.getDisableFlag().booleanValue()) {
                retColor = CELL_COLORS[3];
            } else if (state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_INACTIVE])
                || state.equalsIgnoreCase(LMCurtailCustomer.ACK_UNACKNOWLEDGED)) {
                retColor = CELL_COLORS[0];
            } else if (state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_ACTIVE])
                || state.equalsIgnoreCase(LMCurtailCustomer.ACK_ACKNOWLEDGED)) {
                retColor = CELL_COLORS[1];
            } else if (state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_ACTIVE_PENDING])
                || state.equalsIgnoreCase(LMCurtailCustomer.ACK_NOT_REQUIRED)) {
                retColor = CELL_COLORS[2];
            } else if (state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_INACTIVE_PENDING])
                || state.equalsIgnoreCase(LMCurtailCustomer.ACK_VERBAL)) {
                retColor = CELL_COLORS[4];
            }
        }

        return "#" + ServletUtil.getHTMLColor(retColor);
    }

    public static synchronized String getProgAvailChgStr(LMProgramBase prog) {
        switch (prog.getProgramStatus().intValue()) {
        case LMProgramBase.STATUS_ACTIVE:
        case LMProgramBase.STATUS_MANUAL_ACTIVE:
        case LMProgramBase.STATUS_FULL_ACTIVE:
        case LMProgramBase.STATUS_NOTIFIED:
        case LMProgramBase.STATUS_SCHEDULED:
        case LMProgramBase.STATUS_CNTRL_ATTEMPT:
        case LMProgramBase.STATUS_TIMED_ACTIVE:
            return "Stop...";

        case LMProgramBase.STATUS_INACTIVE:
        case LMProgramBase.STATUS_NON_CNTRL:
        case LMProgramBase.STATUS_STOPPING: /* only used by the server */
            return "Start...";

        default:
            throw new IllegalStateException("Found an unexpected state for a LMProgram object, value = "
                + prog.getProgramStatus().intValue());
        }

    }

    public static synchronized int decodeStartWindow(LMControlArea cntrlArea) {
        if (cntrlArea == null) {
            return LMControlArea.INVALID_INT;
        } else {
            return (cntrlArea.getCurrentDailyStartTime() == null ? (cntrlArea.getDefDailyStartTime() == null
                ? LMControlArea.INVALID_INT : cntrlArea.getDefDailyStartTime().intValue())
                : cntrlArea.getCurrentDailyStartTime().intValue());
        }
    }

    public static synchronized int decodeStopWindow(LMControlArea cntrlArea) {
        if (cntrlArea == null) {
            return LMControlArea.INVALID_INT;
        } else {
            return (cntrlArea.getCurrentDailyStopTime() == null ? (cntrlArea.getDefDailyStopTime() == null
                ? LMControlArea.INVALID_INT : cntrlArea.getDefDailyStopTime().intValue())
                : cntrlArea.getCurrentDailyStopTime().intValue());
        }
    }

    /**
     * A method to create a LMManualControlRequest with some set values.
     * Creation date: (5/14/2002 10:50:02 AM)
     * 
     * @param
     */
    public static synchronized LMManualControlRequest createProgMessage(boolean doItNow, boolean isStop,
            Date startTime, Date stopTime, LMProgramBase program, Integer gearNum, int constraintFlag, ProgramOriginSource programOriginSource) {
        LMManualControlRequest msg = null;

        // create the new message
        if (isStop) {
            if (doItNow) {
                msg =
                    program.createStartStopNowMsg(stopTime, (gearNum == null ? 0 : gearNum.intValue()), null, false,
                        constraintFlag, programOriginSource);
            } else {
                msg =
                    program.createScheduledStopMsg(startTime, stopTime, (gearNum == null ? 0 : gearNum.intValue()),
                        null, programOriginSource);
            }
        } else {
            msg = createStartMessage(doItNow, startTime, stopTime, program, gearNum, constraintFlag, null, programOriginSource);
        }
        // return the message created
        return msg;
    }

    public static LMManualControlRequest createStartMessage(boolean doItNow, Date startTime, Date stopTime,
            LMProgramBase program, Integer gearNum, int constraintFlag, String addtionalInfo, ProgramOriginSource programOriginSource) {
        LMManualControlRequest msg;
        if (doItNow) {
            msg =
                program.createStartStopNowMsg(stopTime, (gearNum == null ? 0 : gearNum.intValue()), addtionalInfo,
                    true, constraintFlag, programOriginSource);
        } else {
            msg =
                program.createScheduledStartMsg(startTime, stopTime, (gearNum == null ? 0 : gearNum.intValue()), null,
                    addtionalInfo, constraintFlag, programOriginSource);
        }

        return msg;
    }

    /**
     *
     * Generates a scenario message based on the given params. If the given time is
     * 1990 and we are to start/stop in the future, then we must change
     * the given start/stop time to the current time.
     * 
     * @return
     */
    public static synchronized LMManualControlRequest createScenarioMessage(LMProgramBase program, boolean isStop,
            boolean isNow, int startDelay, int stopOffset, int gearNum, Date startTime, Date stopTime,
            int constraintFlag) {
        // we can not start/stop now if there is a delay for the program
        boolean doItNow = false;
        if (isStop) {
            doItNow = isNow && (stopOffset <= 0);
            if (!doItNow && stopTime.equals(CtiUtilities.get1990GregCalendar().getTime())) {
                stopTime = new Date();
            }
        } else {
            doItNow = isNow && (startDelay <= 0);
            if (!doItNow && startTime.equals(CtiUtilities.get1990GregCalendar().getTime())) {
                startTime = new Date();
            }
        }

        GregorianCalendar startGC = new GregorianCalendar();
        GregorianCalendar stopGC = new GregorianCalendar();
        startGC.setTime(startTime);
        stopGC.setTime(stopTime);

        startGC.add(GregorianCalendar.SECOND, startDelay);
        stopGC.add(GregorianCalendar.SECOND, stopOffset);

        LMManualControlRequest msg =
            LCUtils.createProgMessage(doItNow, isStop, startGC.getTime(), stopGC.getTime(), program, (isStop ? null
                : new Integer(gearNum)), constraintFlag, ProgramOriginSource.MANUAL);

        return msg;
    }

    public static List<ConstraintContainer> convertViolationsToContainers(List<ConstraintViolation> violations) {
        List<ConstraintContainer> containerList = Lists.newArrayList();
        for (ConstraintViolation violation : violations) {
            ConstraintContainer constraintContainer = new ConstraintContainer(violation);
            containerList.add(constraintContainer);
        }
        return containerList;
    }

    /**
     * Executes a batch of requests and waits for their corresponding responses.
     * Return false if an error occurred, else true.
     * If programResp is null, false is returned.
     * 
     * @param lmReqs
     * @param programResp
     * @return
     */
    public static synchronized boolean executeSyncMessage(ResponseProg[] programResp) {
        boolean success = true;

        if (programResp == null) {
            return false;
        }

        try {
            ServerRequest serverRequest = new ServerRequestImpl();
            ServerResponseMsg[] responseMsgs = new ServerResponseMsg[programResp.length];

            for (int i = 0; i < responseMsgs.length; i++) {
                LMManualControlRequest lmRequest = programResp[i].getLmRequest();
                CTILogger.info("cmd-" + lmRequest.getCommand() + "," + "program- " + lmRequest.getYukonID() + ","
                    + lmRequest.getAddditionalInfo());
                responseMsgs[i] = serverRequest.makeServerRequest(LoadControlClientConnection.getInstance(), lmRequest);
            }

            // fill in our responses
            for (int i = 0; i < responseMsgs.length; i++) {
                // some type of error occurred
                programResp[i].setStatus(responseMsgs[i].getStatus());

                LMManualControlResponse lmResp = (LMManualControlResponse) responseMsgs[i].getPayload();
                programResp[i].setViolations(convertViolationsToContainers(lmResp.getConstraintViolations()));

                success &= programResp[i].getViolations().isEmpty();
            }

        } catch (Exception e) {
            CTILogger.error("No response received from server", e);
            success = false;
        }

        return success;
    }
}
