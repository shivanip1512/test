package com.cannontech.stars.web.action;

import java.util.*;
import javax.servlet.http.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class GetLMCtrlHistAction extends ActionBase {

    public GetLMCtrlHistAction() {
        super();
    }

    public StarsOperation build(HttpServletRequest req, HttpSession session) {
        StarsGetLMControlHistory getHist = new StarsGetLMControlHistory();

        int groupID = -1;
        if (req.getParameter("Group") != null)
            try {
                groupID = Integer.parseInt( req.getParameter("Group") );
            }
            catch (NumberFormatException e) {}
        getHist.setGroupID( groupID );
        getHist.setPeriod( StarsCtrlHistPeriod.valueOf(req.getParameter("Period")) );

        StarsOperation operation = new StarsOperation();
        operation.setStarsGetLMControlHistory( getHist );
        return operation;
    }

    public boolean parse(StarsOperation operation, HttpSession session) {
        StarsLMControlHistory ctrlHist = operation.getStarsLMControlHistory();
        if (ctrlHist == null) return false;

        session.setAttribute("RESPONSE_OPERATION", operation);
/*
        StarsCustomerAccountInformation accountInfo = (StarsCustomerAccountInformation)
                    session.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
        if (accountInfo == null) return false;

        StarsOperation reqOper = (StarsOperation) session.getAttribute("REQUEST_OPERATION");
        if (reqOper == null) return false;

        StarsLMPrograms programs = accountInfo.getStarsLMPrograms();
        int groupID = reqOper.getStarsGetLMControlHistory().getGroupID();

        for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
            StarsLMProgram program = programs.getStarsLMProgram(i);
            if (program.getGroupID() == groupID)
                program.setStarsLMControlHistory( ctrlHist );
        }*/

        return true;
    }

    public StarsOperation process(StarsOperation reqOper, HttpSession session) {
        StarsOperation respOper = new StarsOperation();

        StarsGetLMControlHistory getHist = reqOper.getStarsGetLMControlHistory();
        StarsLMControlHistory starsCtrlHist = new StarsLMControlHistory();

        com.cannontech.database.db.pao.LMControlHistory[] ctrlHist =
                    com.cannontech.database.data.starsprogram.LMControlHistory.getLMControlHistory(
                        new Integer(getHist.getGroupID()), getHist.getPeriod());
        com.cannontech.database.db.pao.LMControlHistory lastCtrlHist = null;

        for (int j = 0; j < ctrlHist.length; j++) {
            ControlHistory hist = new ControlHistory();
            hist.setControlType( ctrlHist[j].getControlType() );
            hist.setStartDateTime( ctrlHist[j].getStartDateTime() );
            hist.setControlDuration( ctrlHist[j].getControlDuration().intValue() );
            starsCtrlHist.addControlHistory( hist );

            if (lastCtrlHist == null || lastCtrlHist.getLmCtrlHistID().intValue() < ctrlHist[j].getLmCtrlHistID().intValue())
                lastCtrlHist = ctrlHist[j];
        }

        if (getHist.getGetSummary()) {
            ControlSummary summary = new ControlSummary();
            summary.setDailyTime(0);
            summary.setMonthlyTime(0);
            summary.setSeasonalTime(0);
            summary.setAnnualTime(0);

            if (lastCtrlHist != null) {
                summary.setDailyTime( lastCtrlHist.getCurrentDailyTime().intValue() );
                summary.setMonthlyTime( lastCtrlHist.getCurrentMonthlyTime().intValue() );
                summary.setSeasonalTime( lastCtrlHist.getCurrentSeasonalTime().intValue() );
                summary.setAnnualTime( lastCtrlHist.getCurrentAnnualTime().intValue() );
            }
            else {
                lastCtrlHist = com.cannontech.database.data.starsprogram.LMControlHistory.getLastLMControlHistory(
                        new Integer(getHist.getGroupID()) );

                if (lastCtrlHist != null) {
                    Calendar nowCal = Calendar.getInstance();
                    Calendar lastCal = Calendar.getInstance();
                    lastCal.setTime( lastCtrlHist.getStartDateTime() );

                    if (lastCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)) {
                        summary.setAnnualTime( lastCtrlHist.getCurrentAnnualTime().intValue() );
                        // Don't quite know how to deal with season yet, so just let it go with year now
                        summary.setSeasonalTime( lastCtrlHist.getCurrentSeasonalTime().intValue() );

                        if (lastCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH)) {
                            summary.setMonthlyTime( lastCtrlHist.getCurrentMonthlyTime().intValue() );

                            if (lastCal.get(Calendar.DAY_OF_MONTH) == nowCal.get(Calendar.DAY_OF_MONTH))
                                summary.setDailyTime( lastCtrlHist.getCurrentDailyTime().intValue() );
                        }
                    }
                }
            }

            starsCtrlHist.setControlSummary( summary );
        }

        respOper.setStarsLMControlHistory( starsCtrlHist );
        return respOper;
    }
}