package com.cannontech.stars.util;

import java.util.*;
import com.cannontech.stars.xml.serialize.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ServletUtils {
	
	public static class ProgramHistory {
		private Date date = null;
		private String action = null;
		private String duration = null;
		private ArrayList programList = new ArrayList();

		public Date getDate() {
			return date;
		}

		public String getDuration() {
			return duration;
		}

		public String[] getPrograms() {
			String[] programs = new String[ programList.size() ];
			programList.toArray( programs );
			return programs;
		}

		public String getAction() {
			return action;
		}

	}
	
	public static final String TRANSIENT_ATT_LEADING = "$$";

    private static java.text.DecimalFormat decFormat = new java.text.DecimalFormat("0.#");
    
    private static GregorianCalendar veryEarlyDate = new GregorianCalendar(1970, Calendar.JANUARY, 2);

    public ServletUtils() {
    }

    public static String getDurationString(int sec) {
        String durationStr = null;

        if (sec >= 3600)
            durationStr = decFormat.format(1.0 * sec / 3600) + " Hours";
        else
            durationStr = String.valueOf(sec / 60) + " Minutes";

        return durationStr;
    }
    
    public static String getDurationString(Date startDate, Date stopDate) {
    	if (startDate == null || stopDate == null) return "";
    	
    	int duration = (int) ((stopDate.getTime() - startDate.getTime()) * 0.001 / (3600 * 24) + 0.5);
    	String durStr = String.valueOf(duration);
    	if (duration > 1)
    		durStr += " Days";
    	else
    		durStr += " Day";
    		
    	return durStr;
    }
    
    public static ProgramHistory[] createProgramHistory(StarsLMPrograms programs) {
    	TreeMap progHistMap = new TreeMap();
    	
    	for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
    		StarsLMProgram program = programs.getStarsLMProgram(i);
    		StarsLMProgramHistory starsProgHist = program.getStarsLMProgramHistory();
    		if (starsProgHist == null) continue;
    		
    		for (int j = 0; j < starsProgHist.getLMProgramEventCount(); j++) {
    			LMProgramEvent event = starsProgHist.getLMProgramEvent(j);
    			
	    		ProgramHistory progHist = (ProgramHistory) progHistMap.get( event.getEventDateTime() );
	    		if (progHist == null) {
	    			progHist = new ProgramHistory();
	    			progHist.date = event.getEventDateTime();
	    			progHist.action = event.getEventAction();
	    			progHistMap.put( event.getEventDateTime(), progHist );
	    		}
	    			
    			if (event.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_TEMPTERMINATION )) {
    				// Getting opt out duration by looking at the next program event,
    				// the next event must be 'FutureAction' or 'Completed'
    				if (++j >= starsProgHist.getLMProgramEventCount()) return null;
    				LMProgramEvent event2 = starsProgHist.getLMProgramEvent(j);
    				if (!event2.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_FUTUREACTIVATION )
    					&& !event2.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_COMPLETED ))
    					return null;
    				
    				if (progHist.duration == null)	
	    				progHist.duration = getDurationString( event.getEventDateTime(), event2.getEventDateTime() );
    			}
    			
	    		progHist.programList.add( program.getProgramName() );
    		}
    	}
    	
    	ProgramHistory[] progHists = new ProgramHistory[ progHistMap.size() ];
    	progHistMap.values().toArray( progHists );
    	return progHists;
    }
    
    public static String getDateFormat(Date date, java.text.SimpleDateFormat format) {
    	GregorianCalendar cal = new GregorianCalendar();
    	cal.setTime( date );
    	if (cal.before( veryEarlyDate ))	// Too early date means the date should be empty
    		return "";
    		
    	return format.format( date );
    }
}