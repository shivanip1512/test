package com.cannontech.loadcontrol;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.ILMGroup;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.loadcontrol.data.LMCurtailCustomer;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.datamodels.ControlAreaTableModel;
import com.cannontech.loadcontrol.datamodels.GroupTableModel;
import com.cannontech.loadcontrol.datamodels.ProgramTableModel;
import com.cannontech.loadcontrol.displays.ControlAreaActionListener;
import com.cannontech.loadcontrol.gui.MultiLineControlAreaRenderer;
import com.cannontech.loadcontrol.gui.manualentry.ResponseProg;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.messages.LMManualControlResponse;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.util.ServletUtil;


/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LCUtils
{
	public static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");
	public static final SimpleDateFormat TEMPORAL_FORMATTER = new SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");


	private static final GregorianCalendar currTime = new GregorianCalendar();
	private static final GregorianCalendar tempTime = new GregorianCalendar();


	public static final Color[] CELL_COLORS =
	{
		//Inactive
		Color.BLACK,
		//Active, Manual Active & Fully Active
		Color.GREEN,
		//Scheduled
		Color.YELLOW,

		//Disabled program
		Color.RED,
		
		//Scheduled
		Color.CYAN,
		//Notified
		Color.ORANGE
	};


	//available times a group can be shed for
	public static final String[] SHED_STRS = 
	{
		"5 minutes",
		"7 minutes",
		"10 minutes",
		"15 minutes",
		"20 minutes",
		"30 minutes",
		"45 minutes",
		"1 hour",
		"2 hours",
		"3 hours",
		"4 hours",
		"6 hours",
		"8 hours"
	};

	/**
	 * 
	 */
	private LCUtils()
	{
		super();
	}


	public static boolean isAreaDisplayed( String view, int areaState )
	{

		if( ControlAreaActionListener.SEL_ACTIVE_AREAS.equals(view) )
		{
			return
				areaState == LMControlArea.STATE_ACTIVE
				|| areaState == LMControlArea.STATE_FULLY_ACTIVE
				|| areaState == LMControlArea.STATE_MANUAL_ACTIVE;
		}
		else if( ControlAreaActionListener.SEL_INACTIVE_AREAS.equals(view) )
		{
			return areaState == LMControlArea.STATE_INACTIVE;
		}
		else
			return true;
	}

	public static synchronized String getFgColor( LMControlArea area ) 
	{
		Color retColor = Color.BLACK;
		
		if( area != null )
		{
			if( area.getDisableFlag().booleanValue() )
			{
				retColor = CELL_COLORS[3];
			}
			else if( area.getControlAreaState().intValue() == LMControlArea.STATE_INACTIVE )
			{
				retColor = CELL_COLORS[0];
			}
			else if( area.getControlAreaState().intValue() == LMControlArea.STATE_ACTIVE
						|| area.getControlAreaState().intValue() == LMControlArea.STATE_FULLY_ACTIVE
						|| area.getControlAreaState().intValue() == LMControlArea.STATE_MANUAL_ACTIVE )
			{
				retColor = CELL_COLORS[1];
			}
			else if( area.getControlAreaState().intValue() == LMControlArea.STATE_CNTRL_ATTEMPT )
						 //|| getRowAt(row).getControlAreaState().intValue() == LMControlArea.STATE_SCHEDULED )
			{
				retColor = CELL_COLORS[2];
			}
			
		}
	
		return "#" + ServletUtil.getHTMLColor(retColor);
	}


	public static synchronized String getFgColor( LMProgramBase prg ) 
	{
		Color retColor = Color.BLACK;
		
		if( prg != null )
		{
			if( prg.getDisableFlag().booleanValue() )
			{
				retColor = CELL_COLORS[3];
			}
			else if( prg.getProgramStatus().intValue() == LMProgramBase.STATUS_INACTIVE
			 			 || prg.getProgramStatus().intValue() == LMProgramBase.STATUS_NON_CNTRL )
			{
				retColor = CELL_COLORS[0];
			}
			else if( prg.getProgramStatus().intValue() == LMProgramBase.STATUS_ACTIVE
						|| prg.getProgramStatus().intValue() == LMProgramBase.STATUS_FULL_ACTIVE
						|| prg.getProgramStatus().intValue() == LMProgramBase.STATUS_MANUAL_ACTIVE
						|| prg.getProgramStatus().intValue() == LMProgramBase.STATUS_TIMED_ACTIVE )
			{
				retColor = CELL_COLORS[1];
			}
			else if( prg.getProgramStatus().intValue() == LMProgramBase.STATUS_NOTIFIED)
			{
				retColor = CELL_COLORS[4];
			}
			else if( prg.getProgramStatus().intValue() == LMProgramBase.STATUS_SCHEDULED
						 || prg.getProgramStatus().intValue() == LMProgramBase.STATUS_CNTRL_ATTEMPT )
			{
				retColor = CELL_COLORS[5];
			}
			else if( prg.getProgramStatus().intValue() == LMProgramBase.STATUS_STOPPING )
			{
				retColor = CELL_COLORS[2];
			}
		}
	
		return "#" + ServletUtil.getHTMLColor(retColor);
	}

	public static synchronized String getFgColor( ILMGroup grp ) 
	{
		Color retColor = Color.BLACK;

		if( grp != null )
		{
			String state = grp.getGroupControlStateString();
	
			if( grp.getDisableFlag().booleanValue() )
			{
				retColor = CELL_COLORS[3];	
			}
			else if( state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_INACTIVE])
						|| state.equalsIgnoreCase(LMCurtailCustomer.ACK_UNACKNOWLEDGED) )
			{
				retColor = CELL_COLORS[0];
			}
			else if( state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_ACTIVE])
						|| state.equalsIgnoreCase(LMCurtailCustomer.ACK_ACKNOWLEDGED) )
			{
				retColor = CELL_COLORS[1];
			}
			else if( state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_ACTIVE_PENDING])
						|| state.equalsIgnoreCase(LMCurtailCustomer.ACK_NOT_REQUIRED) )
			{
				retColor = CELL_COLORS[2];
			}
			else if( state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_INACTIVE_PENDING])
						|| state.equalsIgnoreCase(LMCurtailCustomer.ACK_VERBAL) )
			{
				retColor = CELL_COLORS[4];
			}
		}
	
		return "#" + ServletUtil.getHTMLColor(retColor);
	}

	public static String getTriggerText( final LMControlArea value, final int col )
	{
		StringBuffer topStrBuf = new StringBuffer();
		StringBuffer botStrBuf = new StringBuffer();

		if( value.getTriggerVector().size() > 0 )
		{			
			for( int i = 0; i < value.getTriggerVector().size(); i++ )
			{
				LMControlAreaTrigger trigger = 
						(LMControlAreaTrigger)value.getTriggerVector().get(i);
	
	
				if( trigger.getTriggerNumber().intValue() == 1 )
				{
					MultiLineControlAreaRenderer.setTriggerStrings(
							trigger, null, topStrBuf, col );
				}
				else if( trigger.getTriggerNumber().intValue() == 2 )
				{
					MultiLineControlAreaRenderer.setTriggerStrings(
							trigger, null, botStrBuf, col );
				}
				else
					com.cannontech.clientutils.CTILogger.info("**** ControlArea '" + value.getYukonName() +"' has more than 2 Triggers defined for it.");
	
			}
					
		}
		else
		{
			 topStrBuf = new StringBuffer( "(No Triggers Found)" );
		}
		
		return topStrBuf.toString() + "<BR>" + botStrBuf.toString();
	}
	
	public static synchronized Object getProgramValueAt(LMProgramBase prg, int col) 
	{
		switch( col )
		{
			case ProgramTableModel.PROGRAM_NAME:
				return prg.getYukonName() +
                        (prg.isRampingIn() ? " (RI)" : 
                            (prg.isRampingOut() ? " (RO)" : ""));

			case ProgramTableModel.CURRENT_STATUS:
				if( prg.getDisableFlag().booleanValue() )				
					return "DISABLED: " + LMProgramBase.getProgramStatusString( prg.getProgramStatus().intValue() );
				else
					return LMProgramBase.getProgramStatusString( prg.getProgramStatus().intValue() );
	
			case ProgramTableModel.START_TIME:
				if( prg.getDisableFlag().booleanValue() )
					return CtiUtilities.STRING_DASH_LINE;
				else
				{
					if( prg.getStartTime() == null
						 || prg.getStartTime().before(com.cannontech.common.util.CtiUtilities.get1990GregCalendar()) )
						return CtiUtilities.STRING_DASH_LINE;
					else
						return new ModifiedDate( prg.getStartTime().getTime().getTime(), ModifiedDate.FRMT_NOSECS );
				}

			case ProgramTableModel.CURRENT_GEAR:
			{
				if( prg instanceof IGearProgram ) 
				{
					return getCurrentGear( (IGearProgram)prg );
				}
				else
					return CtiUtilities.STRING_DASH_LINE;
			}
			
			case ProgramTableModel.STOP_TIME:
				if( prg.getDisableFlag().booleanValue() )
					return CtiUtilities.STRING_DASH_LINE;
				else
				{
					if( prg.getStopTime() == null
						|| prg.getStopTime().before(com.cannontech.common.util.CtiUtilities.get1990GregCalendar()) )
						return CtiUtilities.STRING_DASH_LINE;
					else
						return new ModifiedDate( prg.getStopTime().getTime().getTime(), ModifiedDate.FRMT_NOSECS );
				}
			
			case ProgramTableModel.PRIORITY:
				return 
					( prg.getStartPriority().intValue() <= 0
					? new Integer(1)
					: prg.getStartPriority() );

			case ProgramTableModel.REDUCTION:
				return prg.getReductionTotal();
				
			default:
				return null;
		}
	}

	/**
	 * getValueAt method comment.
	 */
	public static synchronized Object getGroupValueAt( ILMGroup grpVal, int col) 
	{
		switch( col )
		{
			case GroupTableModel.GROUP_NAME:
				return grpVal.getName() +
						(grpVal.isRampingIn() ? " (RI)" : 
						(grpVal.isRampingOut() ? " (RO)" : ""));

			case GroupTableModel.GROUP_STATE:
				if( grpVal.getDisableFlag().booleanValue() )
					return "DISABLED: " + grpVal.getGroupControlStateString();
				else
					return grpVal.getGroupControlStateString();

			case GroupTableModel.TIME:
			{
				if( grpVal.getGroupTime().getTime() > com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
					return new ModifiedDate( grpVal.getGroupTime().getTime(), ModifiedDate.FRMT_NOSECS );
				else
					return CtiUtilities.STRING_DASH_LINE;
			}
		
			case GroupTableModel.STATS:
				return grpVal.getStatistics();
			
			case GroupTableModel.REDUCTION:
				return grpVal.getReduction();

			default:
				return null;
		}

	}

	private static String getCurrentGear( IGearProgram dPrg )
	{
		LMProgramDirectGear gear = null;
		
		//get the current gear we are in
		for( int i = 0; i < dPrg.getDirectGearVector().size(); i++ )
		{			
			gear = (LMProgramDirectGear)dPrg.getDirectGearVector().get(i);
	
			if( dPrg.getCurrentGearNumber().intValue() == gear.getGearNumber().intValue() )
			{
				return gear.getGearName();
			}			
		}
	
		//should not get here
		com.cannontech.clientutils.CTILogger.info("*** Unable to find gear #: " + 
				gear.getGearNumber() + " was not found.");
	
		return "(Gear #" + gear.getGearNumber() + " not Found)";	
	}




	/**
	 * getValueAt method comment.
	 */
	public static synchronized Object getControlAreaValueAt(LMControlArea lmCntrArea, int col) 
	{
	
		switch( col )
		{
			case ControlAreaTableModel.AREA_NAME:
				return lmCntrArea.getYukonName();

			case ControlAreaTableModel.CURRENT_STATE:
			{
				if( lmCntrArea.getDisableFlag().booleanValue() )
					return "DISABLED: " + LMControlArea.getControlAreaStateString( lmCntrArea.getControlAreaState().intValue() );
				else
					return LMControlArea.getControlAreaStateString( lmCntrArea.getControlAreaState().intValue() );
			}

			case ControlAreaTableModel.VALUE_THRESHOLD:
			{
				//data is on the trigger object
				return lmCntrArea;
			}
					
			case ControlAreaTableModel.TIME_WINDOW:
			{
				return
					getTimeString(
							lmCntrArea,
							(lmCntrArea.getDefDailyStartTime() == null 
								? LMControlArea.INVAID_INT
								: lmCntrArea.getDefDailyStartTime().intValue()),
							(lmCntrArea.getCurrentDailyStartTime() == null
								? LMControlArea.INVAID_INT
								: lmCntrArea.getCurrentDailyStartTime().intValue()) ) +
					" - " +
					getTimeString(
							lmCntrArea,
							(lmCntrArea.getDefDailyStopTime() == null 
								? LMControlArea.INVAID_INT
								: lmCntrArea.getDefDailyStopTime().intValue()),
							(lmCntrArea.getCurrentDailyStopTime() == null
								? LMControlArea.INVAID_INT
								: lmCntrArea.getCurrentDailyStopTime().intValue()) ); 
					 
			}
			
			case ControlAreaTableModel.PEAK_PROJECTION:
			{
				//data is on the trigger object
				return lmCntrArea;
			}

			case ControlAreaTableModel.PRIORITY:
			{
				//data is on the trigger object
				return
					( lmCntrArea.getCurrentPriority().intValue() <= 0
					? new Integer(1)
					: lmCntrArea.getCurrentPriority() );
			}

			case ControlAreaTableModel.ATKU:
			{
				//data is on the trigger object
				return lmCntrArea;
			}
			
			
			default:
				return null;
		}
	}


	private static synchronized String getTimeString( LMControlArea row, int defSecs, int currSecs )
	{
		String retStr = null;
		
		if( row == null || row.getDisableFlag().booleanValue() )
			retStr = CtiUtilities.STRING_DASH_LINE;
		else
		{					
			if( defSecs <= LMControlArea.INVAID_INT && currSecs <= LMControlArea.INVAID_INT )
			{
				retStr = CtiUtilities.STRING_DASH_LINE;
			}
			else
			{
				//set our time to todays date
				currTime.setTime( new java.util.Date() );
										
				if( defSecs == currSecs || currSecs <= LMControlArea.INVAID_INT )
				{
					currTime.set( currTime.HOUR_OF_DAY, 0 ); 
					currTime.set( currTime.MINUTE, 0 );
					currTime.set( currTime.SECOND, defSecs );

					retStr = TIME_FORMATTER.format( currTime.getTime() );
				}
				else
				{
					tempTime.setTime( currTime.getTime() );
					tempTime.set( tempTime.HOUR_OF_DAY, 0 ); 
					tempTime.set( tempTime.MINUTE, 0 );
					tempTime.set( tempTime.SECOND, currSecs );
	
					retStr = TIME_FORMATTER.format( tempTime.getTime() );
				}
			}
		}
	
		return retStr;
	}
	
	public static synchronized String getProgAvailChgStr( LMProgramBase prog )
	{
		switch( prog.getProgramStatus().intValue() )
		{
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
			case LMProgramBase.STATUS_STOPPING: /*only used by the server*/
				return "Start...";

			default:
				throw new IllegalStateException(
					"Found an unexpected state for a LMProgram object, value = " + prog.getProgramStatus().intValue() );
		}

	}
	
	public static synchronized int decodeStartWindow( LMControlArea cntrlArea )
	{		
		if( cntrlArea == null )
			return LMControlArea.INVAID_INT;
		else
			return 
				(cntrlArea.getCurrentDailyStartTime() == null 
					? (cntrlArea.getDefDailyStartTime() == null
						? LMControlArea.INVAID_INT
						: cntrlArea.getDefDailyStartTime().intValue())
					: cntrlArea.getCurrentDailyStartTime().intValue() );
	}

	public static synchronized int decodeStopWindow( LMControlArea cntrlArea )
	{		
		if( cntrlArea == null )
			return LMControlArea.INVAID_INT;
		else
			return
				(cntrlArea.getCurrentDailyStopTime() == null 
					? (cntrlArea.getDefDailyStopTime() == null
						? LMControlArea.INVAID_INT
						: cntrlArea.getDefDailyStopTime().intValue())
					: cntrlArea.getCurrentDailyStopTime().intValue() );
	}

	/**
	 * A method to create a LMManualControlRequest with some set values. 
	 * Creation date: (5/14/2002 10:50:02 AM)
	 * @param
	 */
	public static synchronized LMManualControlRequest createProgMessage(
				boolean doItNow, boolean isStop,
				Date startTime, Date stopTime, LMProgramBase program,
				Integer gearNum ) 
	{
		LMManualControlRequest msg = null;
		
		//create the new message
		if( isStop )
		{
			if( doItNow )
				msg = program.createStartStopNowMsg(
							stopTime,
							(gearNum == null ? 0 : gearNum.intValue()), 
							null, false);
			else					
				msg = program.createScheduledStopMsg(
							startTime, 
							stopTime,
							(gearNum == null ? 0 : gearNum.intValue()), 
							null);
		}
		else
		{
			if( doItNow )
				msg = program.createStartStopNowMsg(
							stopTime,
							(gearNum == null ? 0 : gearNum.intValue()), 
							null, true);
			else
				msg = program.createScheduledStartMsg( 
							startTime, 
							stopTime,
							(gearNum == null ? 0 : gearNum.intValue()), 
							null, null );
		}
	
		
		//return the message created
		return msg;
	}

	/**
	 *
	 * Generates a scenario message based on the given params. If the given time is
	 * 1990 and we are to start/stop in the future, then we must change 
	 * the given start/stop time to the current time.
	 * @return
	 */
	public static synchronized LMManualControlRequest createScenarioMessage( 
			LMProgramBase program,
			boolean isStop,
			boolean isNow,
			int startDelay,
			int stopOffset,
			int gearNum,
			Date startTime,
			Date stopTime ) 
	{
		//we can not start/stop now if there is a delay for the program
		boolean doItNow = false;
		if( isStop )
		{
			doItNow = isNow && (stopOffset <= 0);
			if( !doItNow && stopTime.equals(CtiUtilities.get1990GregCalendar().getTime()) )
				stopTime = new Date();
		} 
		else
		{
			doItNow = isNow && (startDelay <= 0);
			if( !doItNow && startTime.equals(CtiUtilities.get1990GregCalendar().getTime()) )
				startTime = new Date();
		}

		GregorianCalendar startGC = new GregorianCalendar();
		GregorianCalendar stopGC = new GregorianCalendar();
		startGC.setTime( startTime );
		stopGC.setTime( stopTime );
		
		startGC.add( startGC.SECOND, startDelay );
		stopGC.add( stopGC.SECOND, stopOffset );
		
				
		return LCUtils.createProgMessage(
						doItNow,
						isStop,
						startGC.getTime(),
						stopGC.getTime(),
						program,
						(isStop ? null : new Integer(gearNum)) );
	}
	
	
	/**
	 * Executes a batch of requests and waits for their corresponding responses.
	 * Return false if an error occured, else true.
	 * If programResp is null, false is returned.
	 * 
	 * @param lmReqs
	 * @param programResp
	 * @return
	 */
	public static synchronized boolean executeSyncMessage( ResponseProg[] programResp )
	{
		boolean success = true;
		
		if( programResp == null )
			return false;

		try
		{ 
			ServerRequest[] srvrReq = new ServerRequest[ programResp.length ];

			ServerResponseMsg[] responseMsgs =
					new ServerResponseMsg[ srvrReq.length ];

			for( int i = 0; i < srvrReq.length; i++ )
			{
				srvrReq[i] = ServerRequest.makeServerRequest(
						LoadControlClientConnection.getInstance(),
						programResp[i].getLmRequest() );

				responseMsgs[i] = srvrReq[i].execute();
			}

			//fill in our responses
			for( int i = 0; i < responseMsgs.length; i++ )
			{
				// some type of error occured
				programResp[i].setStatus( responseMsgs[i].getStatus() );
                
                success &= (programResp[i].getStatus() == ServerResponseMsg.STATUS_OK);
 
                if( !success )
                {
    				LMManualControlResponse lmResp = (LMManualControlResponse) responseMsgs[i].getPayload();
    				if(lmResp != null)
    				{
    					//do something interesting.
    					List violationStrs = lmResp.getConstraintViolations();
    					for( int j = 0; j < violationStrs.size(); j++ )
    						programResp[i].addViolation( violationStrs.get(j).toString() );
    				}
                    else
                    {
                        //add the message from the response to out list of problems
                        programResp[i].addViolation( responseMsgs[i].getMessage() );
                    }
                }
                
			}

		}
        catch(Exception e)
        {
            CTILogger.error( "No response received from server", e );
            success = false;
        }

		return success;
	}


}