package com.cannontech.web.loadcontrol;

import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.LMFuncs;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.ILMData;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMScenarioWrapper;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.message.dispatch.message.Multi;

/**
 * @author rneuharth
 *
 * Automatically creates LC command messgaes
 */
public final class LMCmdMsgFactory
{
	//local references to any optional parameters 
	private static Integer duration = null, gearnum = null, 
		cyclepercent = null, periodcnt = null, starttime = null,
		stoptime = null;

	private static Date startdate = null, stopdate = null;
	private static Double dblarray1[] = null, dblarray2[] = null;


	/**
	 *
	 *	Returns WebCmdMsg objects that are populated with valid data. Use call
	 * when a load control cache is present.
	 *
	 *Possible cmd values:
	 * Area Commands:
	 *    a_start_progs, a_stop_progs, 
	 * 	a_trigger_chg, a_daily_chg, a_disable
	 * 
	 * Program Commands:
	 *		p_start_prog, p_disable
	 *
	 * Group Commands:
	 * 	g_shed, g_restore, g_truecyc,
	 * 	g_smartcyc, g_disable, g_confirm
	 *
	 * Posibble keys in optionalProps Hashtable:
	 * 	duration -> Integer
	 * 	gearnum -> Integer
	 * 	startdate -> Date
	 * 	stopdate -> Date
	 * 	cyclepercent -> Integer
	 * 	periodcnt -> Integer
	 * 	starttime -> Integer
	 * 	stoptime -> Integer
	 * 	dblarray1 -> Double[]
	 * 	dblarray2 -> Double[]
	 */
	public static synchronized WebCmdMsg createCmdMsg( 
			String cmdStr, Integer itemid,
			final Hashtable optionalProps,
			final LoadcontrolCache lcCache )
	{
		WebCmdMsg cmdMsg = new WebCmdMsg(cmdStr);
		ILMData lmData = null;
		
		if( cmdMsg.isControlAreaMsg() )
			return createCmdMsg( cmdMsg, lcCache.getControlArea(itemid), optionalProps );
		else if( cmdMsg.isProgramMsg() )
			return createCmdMsg( cmdMsg, lcCache.getProgram(itemid), optionalProps );
		else if( cmdMsg.isGroupMsg() )
			return createCmdMsg( cmdMsg, lcCache.getGroup(itemid), optionalProps );
		else if( cmdMsg.isScenarioMsg() )
		{
			//we need to get a reference to cache for this type of message
			if( optionalProps != null )
				optionalProps.put( "local_LCCache", lcCache );

			return createCmdMsg( cmdMsg, lcCache.getScenario(itemid), optionalProps );
		}
			

		//oops, we missed something
		throw new IllegalStateException("Unknown message type given for message creation");
	}

	/**
	 * This creation method is use when the we want to send a message for is
	 * known (does not require a look up).
	 * 
	 * @param cmdStr
	 * @param lmData
	 * @return
	 */
	public static synchronized WebCmdMsg createCmdMsg( 
			String cmdStr, ILMData lmData, final Hashtable optionalProps )
	{
		WebCmdMsg cmdMsg = new WebCmdMsg(cmdStr);

		return createCmdMsg( cmdMsg, lmData, optionalProps );
	}


	/**
	 * 
	 * If the passed in optionalProps value is null, the LC message will not be
	 * generated.
	 * 
	 * @param cmdMsg
	 * @param lmData
	 * @param optionalProps
	 * @return
	 */
	private static WebCmdMsg createCmdMsg( WebCmdMsg cmdMsg, ILMData lmData, Hashtable optionalProps )
	{
		if( optionalProps != null )
		{
			duration = (Integer)optionalProps.get("duration");
			gearnum = (Integer)optionalProps.get("gearnum");
			startdate = (Date)optionalProps.get("startdate");
			stopdate = (Date)optionalProps.get("stopdate");
			cyclepercent = (Integer)optionalProps.get("cyclepercent");
			periodcnt = (Integer)optionalProps.get("periodcnt");			
			starttime = (Integer)optionalProps.get("starttime");
			stoptime = (Integer)optionalProps.get("stoptime");
			dblarray1 = (Double[])optionalProps.get("dblarray1");
			dblarray2 = (Double[])optionalProps.get("dblarray2");

		}			

		//set the data we are operating with
		cmdMsg.setLMData( lmData );

		if( cmdMsg.isControlAreaMsg() ) //for all control area commands
		{
			handleArea( cmdMsg, optionalProps );
		}
		else if( cmdMsg.isProgramMsg() ) //for all program commands
		{
			handleProgram( cmdMsg, optionalProps );
		}
		else if( cmdMsg.isGroupMsg() ) //for all group commands
		{
			handleGroup( cmdMsg, optionalProps );
		}
		else if( cmdMsg.isScenarioMsg() ) //for all scenario commands
		{
			handleScenario( cmdMsg, optionalProps );
		}
		else
			throw new IllegalArgumentException("Unknown LM command msg, message : " + cmdMsg.getCmd() );


		return cmdMsg;
	}


	private static void handleArea( final WebCmdMsg cmdMsg, final Hashtable optionalProps )
	{
		LMControlArea lmCntrArea = (LMControlArea)cmdMsg.getLMData();
			
		if( lmCntrArea == null )
		{
			cmdMsg.setHTMLTextMsg(
				"<font class=boldMsg>Unable to find the Control Area specified (cmd = " +
				cmdMsg.getCmd() + ")</font><BR>" );

			return;
		}

		//always show the item selected
		cmdMsg.setHTMLTextMsg( 
				"Selected Item: <font class=boldMsg>" + lmCntrArea.getYukonName() + "</font>  (Control Area)<BR>");
			
		if( ILCCmds.AREA_DISABLE.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>" +
				(lmCntrArea.getDisableFlag().booleanValue() ? "ENABLE" : "DISABLE") + "</font> the selected CONTROL AREA?<BR>");


			//create the message we need to send to the LC server
			if( lmCntrArea.getDisableFlag().booleanValue() )
			{
				cmdMsg.setGenLCMsg( new LMCommand( 
							LMCommand.ENABLE_CONTROL_AREA,
							lmCntrArea.getYukonID().intValue(),
							0, 0.0 ) );
			}
			else
			{
				cmdMsg.setGenLCMsg( new LMCommand(
							LMCommand.DISABLE_CONTROL_AREA,
							lmCntrArea.getYukonID().intValue(),
							0, 0.0 ) );
			}

		}
		else if( ILCCmds.AREA_START_PROGS.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>START PROGRAMS" +
				"</font> in the selected CONTROL AREA?<BR>");

			if( optionalProps != null )
			{
				Multi multi = new Multi();
				List progIDs = Arrays.asList( dblarray1 );
			
				for( int i = 0; i < lmCntrArea.getLmProgramVector().size(); i++ )
				{
					LMProgramBase prg = 
							(LMProgramBase)lmCntrArea.getLmProgramVector().get(i);

					if( !progIDs.contains( new Double(prg.getYukonID().doubleValue()) ) )
						continue;					
				
				
					if( startdate.equals(CtiUtilities.get1990GregCalendar().getTime()) )
						multi.getVector().add( prg.createStartStopNowMsg(
									stopdate,
									dblarray2[i].intValue(), 
									null, true) );
					else
						multi.getVector().add( prg.createScheduledStartMsg(
									startdate,
									stopdate,
									dblarray2[i].intValue(),
									null, null) );					
				}

				if( multi.getVector().size() > 0 )
					cmdMsg.setGenLCMsg( multi );
			}

		}
		else if( ILCCmds.AREA_STOP_PROGS.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>STOP PROGRAMS" +
				"</font> in the selected CONTROL AREA?<BR>");

			if( optionalProps != null )
			{
				Multi multi = new Multi();
				List progIDs = Arrays.asList( dblarray1 );
				
				for( int i = 0; i < lmCntrArea.getLmProgramVector().size(); i++ )
				{
					LMProgramBase prg = 
							(LMProgramBase)lmCntrArea.getLmProgramVector().get(i);

					if( !progIDs.contains( new Double(prg.getYukonID().doubleValue()) ) )
						continue;					
					

					//does the stop now
					if( stopdate.equals(CtiUtilities.get1990GregCalendar().getTime()) )
						multi.getVector().add( 
							prg.createStartStopNowMsg(
									stopdate,
									1, null, false) );
					else					
						multi.getVector().add(
							prg.createScheduledStopMsg(
									startdate, 
									stopdate,
									1, null) );
				}

				if( multi.getVector().size() > 0 )
					cmdMsg.setGenLCMsg( multi );
			}

		}
		else if( ILCCmds.AREA_TRIG_CHG.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to change the <font class=boldMsg>TRIGGER" +
				"</font> values for the selected CONTROL AREA?<BR>");

			if( optionalProps != null )
			{
				Multi multi = new Multi();
				for( int i = 0; i < lmCntrArea.getTriggerVector().size(); i++ )
				{
					LMControlAreaTrigger oldTrig = 
							(LMControlAreaTrigger)lmCntrArea.getTriggerVector().get(i);
					
					//create a new restore offset command message
					LMCommand offsetCmd = new LMCommand(
							LMCommand.CHANGE_RESTORE_OFFSET,
							lmCntrArea.getYukonID().intValue(),
							i+1,  //the trigger number
							dblarray2[i].doubleValue() );
		
					//only add changes when the value is different
					if( offsetCmd.getValue() != oldTrig.getMinRestoreOffset().doubleValue() )
						multi.getVector().add( offsetCmd );
				
					//create a new threshold command message
					LMCommand threshCmd = new LMCommand(
							LMCommand.CHANGE_THRESHOLD,
							lmCntrArea.getYukonID().intValue(),
							i+1,  //the trigger number
							dblarray1[i].doubleValue() );
			
					//only add changes when the value is different
					if( threshCmd.getValue() != oldTrig.getThreshold().doubleValue() )
						multi.getVector().add( threshCmd );
				}
				
				
				if( multi.getVector().size() > 0 )
					cmdMsg.setGenLCMsg( multi );
			}

		}
		else if( ILCCmds.AREA_DAILY_CHG.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to change the <font class=boldMsg>TIME WINDOW" +
				"</font> for the selected CONTROL AREA?<BR>");

			if( optionalProps != null )
			{
				Multi multi = new Multi();
				
				//send a message to the server telling it to change the START time
				if( starttime.intValue() != LMControlArea.INVAID_INT )
					multi.getVector().add(
							new LMCommand( LMCommand.CHANGE_CURRENT_START_TIME,
											lmCntrArea.getYukonID().intValue(),
											0, 
											(double)starttime.intValue()) );

				//send a message to the server telling it to change the STOP time
				if( stoptime.intValue() != LMControlArea.INVAID_INT )
					multi.getVector().add(
								new LMCommand( LMCommand.CHANGE_CURRENT_STOP_TIME,
											lmCntrArea.getYukonID().intValue(),
											0,
											(double)stoptime.intValue()) );
				
				if( multi.getVector().size() > 0 )
					cmdMsg.setGenLCMsg( multi );
			}
			

		}

	}


	private static void handleProgram( final WebCmdMsg cmdMsg, final Hashtable optionalProps )
	{
		LMProgramBase prg = (LMProgramBase)cmdMsg.getLMData();

		if( prg == null )
		{
			cmdMsg.setHTMLTextMsg(
				"<font class=boldMsg>Unable to find the Program specified (cmd = " +
				cmdMsg.getCmd() + ")</font><BR>" );

			return;
		}


		//always show the item selected
		cmdMsg.setHTMLTextMsg(
			"Selected Item: <font class=boldMsg>" + prg.getYukonName() + "</font>  (Program)<BR>");


		if( ILCCmds.PROG_DISABLE.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>" +
				(prg.getDisableFlag().booleanValue() ? "ENABLE" : "DISABLE") + "</font> the selected PROGRAM?<BR>");


			if( prg.getDisableFlag().booleanValue() )
			{
				cmdMsg.setGenLCMsg(
						new LMCommand(
							LMCommand.ENABLE_PROGRAM,
							prg.getYukonID().intValue(),
							0, 0.0) );
			}
			else
			{
				cmdMsg.setGenLCMsg(
						new LMCommand(
							LMCommand.DISABLE_PROGRAM,
							prg.getYukonID().intValue(),
							0, 0.0) );
			}

		}
		else if( ILCCmds.PROG_START.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>START" +
				"</font> the selected PROGRAM?<BR>");
				
			if( optionalProps != null )
			{
				//does the start now
				if( startdate.equals(CtiUtilities.get1990GregCalendar().getTime()) )
					cmdMsg.setGenLCMsg( prg.createStartStopNowMsg(
								stopdate,
								gearnum.intValue(), 
								null, true) );
				else
					cmdMsg.setGenLCMsg( prg.createScheduledStartMsg(
								startdate,
								stopdate,
								gearnum.intValue(),
								null, null) );
			}
		}
		else if( ILCCmds.PROG_STOP.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>STOP" +
				"</font> the selected PROGRAM?<BR>");

			if( optionalProps != null )
			{
				//does the stop now
				if( stopdate.equals(CtiUtilities.get1990GregCalendar().getTime()) )
					cmdMsg.setGenLCMsg( 
						prg.createStartStopNowMsg(
								stopdate,
								1, null, false) );
				else					
					cmdMsg.setGenLCMsg(
						prg.createScheduledStopMsg(
								startdate, 
								stopdate,
								1, null) );
			}

		}

	}

	private static void handleGroup( final WebCmdMsg cmdMsg, final Hashtable optionalProps )
	{
		LMGroupBase grp = (LMGroupBase)cmdMsg.getLMData();

		if( grp == null )
		{
			cmdMsg.setHTMLTextMsg(
				"<font class=boldMsg>Unable to find the Group specified (cmd = " +
				cmdMsg.getCmd() + ")</font><BR>" );

			return;
		}


		//always show the item selected
		cmdMsg.setHTMLTextMsg(
			"Selected Item: <font class=boldMsg>" + grp.getYukonName() + "</font>  (Group)<BR>");



		if( ILCCmds.GRP_DISABLE.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>" +
				(grp.getDisableFlag().booleanValue() ? "ENABLE" : "DISABLE") + "</font> the selected GROUP?<BR>");


			if( grp.getDisableFlag().booleanValue() )
			{
				cmdMsg.setGenLCMsg(
						new LMCommand(
							LMCommand.ENABLE_GROUP,
							grp.getYukonID().intValue(),
							0, 0.0) );
			}
			else
			{
				cmdMsg.setGenLCMsg(
						new LMCommand(
							LMCommand.DISABLE_GROUP,
							grp.getYukonID().intValue(),
							0, 0.0) );
			}

		}
		else if( ILCCmds.GRP_SHED.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>SHED" +
				"</font> the selected GROUP?<BR>");
				
			if( optionalProps != null )
			{
				cmdMsg.setGenLCMsg(
					new LMCommand( LMCommand.SHED_GROUP,
								grp.getYukonID().intValue(),
								duration.intValue(), //shed time in seconds
								0.0,
								0,
								0) ); //this auxid will be used for the alt routeID soon
			}

		}
		else if( ILCCmds.GRP_RESTORE.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>RESTORE" +
				"</font> the selected GROUP?<BR>");

			if( optionalProps != null )
			{
				cmdMsg.setGenLCMsg(
					new LMCommand( LMCommand.RESTORE_GROUP,
								grp.getYukonID().intValue(),
								0,
								0.0) );
			}
		}
		else if( ILCCmds.GRP_TRUE_CY.equals(cmdMsg.getCmd())
					 || ILCCmds.GRP_SMRT_CY.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>" +
				(ILCCmds.GRP_TRUE_CY.equals(cmdMsg.getCmd()) ? "TRUE CYCLE" : "SMART CYCLE") +
				"</font> the selected GROUP?<BR>");
				
			if( optionalProps != null )
			{
				LMCommand cmd = new LMCommand();
				cmd.setCommand(
					(ILCCmds.GRP_TRUE_CY.equals(cmdMsg.getCmd()) 
						? LMCommand.TRUE_CYCLE_GROUP 
						: LMCommand.SMART_CYCLE_GROUP) );

				cmd.setYukonID( grp.getYukonID().intValue() );
					
				//the alt route may or may not be available
//				LiteYukonPAObject liteRoute = null;
//				if( getJComboBoxAltRoute().isVisible() )	
//					liteRoute = (LiteYukonPAObject)getJComboBoxAltRoute().getSelectedItem();
//				cmd.setAuxid( (liteRoute == null ? 0 : liteRoute.getYukonID()) );//this auxid will be used for the alt routeID soon

				cmd.setNumber( cyclepercent.intValue() );  //cycle percent
				cmd.setValue( duration.intValue() ); //period length in seconds
				cmd.setCount( periodcnt.intValue() );  //number of cycle periods
					
				cmdMsg.setGenLCMsg( cmd );
			}

		}
		else if( ILCCmds.GRP_CONFIRM.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>CONFIRM" +
				"</font> the selected GROUP?<BR>");

			if( optionalProps != null )
			{
					
				cmdMsg.setGenLCMsg(
					new LMCommand( LMCommand.CONFIRM_GROUP,
								grp.getYukonID().intValue(),
								0, 
								0.0,
								0,
								0) ); //this auxid will be used for the alt routeID soon		

			}
		}

	}



	private static void handleScenario( final WebCmdMsg cmdMsg, final Hashtable optionalProps )
	{
		LMScenarioWrapper lmScenario = (LMScenarioWrapper)cmdMsg.getLMData();

		if( lmScenario == null )
		{
			cmdMsg.setHTMLTextMsg(
				"<font class=boldMsg>Unable to find the Control Scenario specified (cmd = " +
				cmdMsg.getCmd() + ")</font><BR>" );

			return;
		}

		//always show the item selected
		cmdMsg.setHTMLTextMsg( 
				"Selected Item: <font class=boldMsg>" + lmScenario.getYukonName() + "</font>  (Control Scenario)<BR>");
			
		if( ILCCmds.SC_START.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>START PROGRAMS" +
				"</font> in this Control Scenario?<BR>");
			

			if( optionalProps != null )
			{
				Multi multi = new Multi();
				List progIDs = Arrays.asList( dblarray1 );
				LoadcontrolCache tempCache =
					(LoadcontrolCache)optionalProps.get("local_LCCache");


				LiteLMProgScenario[] programs = 
						LMFuncs.getLMScenarioProgs( lmScenario.getYukonID().intValue() );

				for( int i = 0; i < programs.length; i++ )
				{
					LiteLMProgScenario scenProg = programs[i];

					if( !progIDs.contains( new Double(scenProg.getProgramID()) ) )
						continue;
						
					boolean 
						doItNow = startdate.equals(CtiUtilities.get1990GregCalendar().getTime());

					multi.getVector().add(
						LCUtils.createScenarioMessage(
							tempCache.getProgram( new Integer(scenProg.getProgramID()) ),
							false,
							doItNow,
							scenProg.getStartOffset(),
							scenProg.getStopOffset(),
							dblarray2[i].intValue(),
							startdate, 
							stopdate) );
				}

				if( multi.getVector().size() > 0 )
					cmdMsg.setGenLCMsg( multi );
			}

		}
		else if( ILCCmds.SC_STOP.equals(cmdMsg.getCmd()) )
		{
			cmdMsg.setHTMLTextMsg( cmdMsg.getHTMLTextMsg() +
				"Are you sure you want to <font class=boldMsg>STOP PROGRAMS" +
				"</font> in this Control Scenario?<BR>");

			if( optionalProps != null )
			{
				Multi multi = new Multi();
				List progIDs = Arrays.asList( dblarray1 );
				LoadcontrolCache tempCache =
					(LoadcontrolCache)optionalProps.get("local_LCCache");


				LiteLMProgScenario[] programs = 
						LMFuncs.getLMScenarioProgs( lmScenario.getYukonID().intValue() );

				for( int i = 0; i < programs.length; i++ )
				{
					LiteLMProgScenario scenProg = programs[i];

					if( !progIDs.contains( new Double(scenProg.getProgramID()) ) )
						continue;
						
					boolean 
						doItNow = stopdate.equals(CtiUtilities.get1990GregCalendar().getTime());

					multi.getVector().add(
						LCUtils.createScenarioMessage(
							tempCache.getProgram( new Integer(scenProg.getProgramID()) ),
							true,
							doItNow,
							scenProg.getStartOffset(),
							scenProg.getStopOffset(),
							0,
							startdate, 
							stopdate) );
				}

				if( multi.getVector().size() > 0 )
					cmdMsg.setGenLCMsg( multi );
			}

		}

	}

}
