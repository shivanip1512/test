/*
 * Created on Nov 13, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.cttp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.cache.PILCommandCache;
import com.cannontech.common.pao.PaoType;
import com.cannontech.cttp.schema.cttp_CommandResponseType;
import com.cannontech.cttp.schema.cttp_CommandType;
import com.cannontech.cttp.schema.cttp_FailureType;
import com.cannontech.cttp.schema.cttp_OffsetCommandType;
import com.cannontech.cttp.schema.cttp_OperationType;
import com.cannontech.cttp.schema.cttp_SubmitCommandRequestType;
import com.cannontech.cttp.schema.cttp_SubmitCommandResponseType;
import com.cannontech.cttp.schema.cttp_TargetGroupType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.porter.message.Request;

/**
 * @author aaron
 */
public class SubmitCommandRequestHandler implements CttpMessageHandler {

	/* (non-Javadoc)
	 * @see com.cannontech.cttp.CttpMessageHandler#handleMessage(com.cannontech.cttp.CttpRequest)
	 */
	public CttpResponse handleMessage(CttpRequest req) throws Exception {
		
		cttp_OperationType cttpOp = req.getCttpOperation();
		cttp_SubmitCommandRequestType submitCmd = cttpOp.getcttp_SubmitCommandRequest();
	
		//Response objects
		cttp_OperationType cttpResp = new cttp_OperationType();
		cttp_SubmitCommandResponseType submitCmdResp = new cttp_SubmitCommandResponseType();
		cttpResp.addcttp_SubmitCommandResponse(submitCmdResp);
			
		ArrayList groupList = new ArrayList();
		ArrayList allGroups = Cttp.retrieveLMGroups(req.getUser());
		
		//They either sent a command to all groups or to specific groups
		//Build up a list of which ones this command should go to
		if(submitCmd.hascttp_AllGroups()) {
			Iterator gIter = allGroups.iterator();
			while(gIter.hasNext()) {
				LiteYukonPAObject group = (LiteYukonPAObject) gIter.next();
				groupList.add(group);
			}
		}
		else {
			//SCE doesn't respect the group id's we provide them, they
			//send precanned ids, zone1,zone2,zone3 at this time.
			//First expect them to do the right thing in case they start doing this
			//If what they send doesn't appear to be the id we sent them, then
			//see if we can match a group by removing the space of a group name
			//yukon group name = zone 1 would then match cttp group id = zone1
			  
			for(int i = 0; i < submitCmd.getcttp_TargetGroupCount(); i++) {
				cttp_TargetGroupType targetGroup = submitCmd.getcttp_TargetGroupAt(i);
				int groupID;
				try {			
					groupID = Integer.parseInt(targetGroup.getgroupID().toString());

					Iterator gIter = allGroups.iterator();
					while(gIter.hasNext()) {
						LiteYukonPAObject group = (LiteYukonPAObject) gIter.next();
						if(groupID == group.getLiteID()) {
							groupList.add(group);
						}
					}
						
				} catch(Exception e) {
					//doh, fall back as described above
					String cttpGroupID = targetGroup.getgroupID().toString();

					Iterator gIter = allGroups.iterator();
					while(gIter.hasNext()) {
						LiteYukonPAObject group = (LiteYukonPAObject) gIter.next();
						String hackedName = group.getPaoName().replaceAll(" ", "").trim();
						if(cttpGroupID.equalsIgnoreCase(hackedName)) {
							CTILogger.info("CTTP GROUP ID " + cttpGroupID + " MATCHES " + group.getPaoName());
							groupList.add(group);
						}
					}
				}
			}
		}
		
		if(groupList.size() == 0) {
			CTILogger.warn("No (valid) group specified in submit command request message");
			cttp_FailureType cttpFailure = Cttp.makeFailure(Cttp.UNKNOWN_GROUPID, "Unknown Group ID");
			submitCmdResp.addcttp_Failure(cttpFailure);
			return new CttpResponse(cttpResp);
		} 
		
		cttp_CommandType cttpCmd = submitCmd.getcttp_Command();
		ArrayList groupIDList = new ArrayList();
		int trackingID= CttpCmdCache.getInstance().getNextTrackingID();
		
		if(cttpCmd.hascttp_OffsetCommand()) {
			//Create and send an offset command
			cttp_OffsetCommandType cmd = cttpCmd.getcttp_OffsetCommand();
			int deg = Integer.parseInt(cmd.getoffsetDegreesF().toString());
			int duration = Integer.parseInt(cmd.getoffsetDuration().toString());
			String overridable = cmd.getoffsetOverrideable().toString(); 
			
			Iterator gIter = groupList.iterator();
			while(gIter.hasNext()) {
				LiteYukonPAObject group = (LiteYukonPAObject) gIter.next();
				
				groupIDList.add(new Integer(group.getLiteID()));
				
				//find all groups (explode macro groups) and send an offset command
				if(group.getPaoType() == PaoType.MACRO_GROUP) {
					List cGrps = Cttp.retrieveLMGroupChildren(group.getLiteID());
					Iterator cgIter = cGrps.iterator();
					while(cgIter.hasNext()) {
						LiteYukonPAObject cG = (LiteYukonPAObject) cgIter.next();
						sendCommand(makeOffsetCommand(cG, deg, duration));
					}
				} 
				else {
					sendCommand(makeOffsetCommand(group, deg, duration));
				}
			} 
			
			CttpCmdCache.getInstance().addOffsetCmd(trackingID, req.getUser().getUserID(), deg, duration, groupIDList);
		}
		else 
		if(cttpCmd.hascttp_ClearCommand()) {
			//Create and send a clear command
			Iterator gIter = groupList.iterator();
			while(gIter.hasNext()) {
				LiteYukonPAObject group = (LiteYukonPAObject) gIter.next();
				groupIDList.add(new Integer(group.getLiteID()));

				//find all groups (explode macro groups) and send an offset command
				if(group.getPaoType() == PaoType.MACRO_GROUP) {
					List cGrps = Cttp.retrieveLMGroupChildren(group.getLiteID());
					Iterator cgIter = cGrps.iterator();
					while(cgIter.hasNext()) {
						LiteYukonPAObject cG = (LiteYukonPAObject) cgIter.next();
						sendCommand(makeClearCommand(cG));
					}
				} 
				else {
					sendCommand(makeClearCommand(group));
				}				
			}
			
			CttpCmdCache.getInstance().addClearCmd(trackingID, req.getUser().getUserID(), groupIDList);			
		}

		cttp_CommandResponseType cmdResp = new cttp_CommandResponseType();
		cmdResp.addtimestamp(Cttp.formatCTTPDate(new Date()));
		cmdResp.addcommandTrackingCode(Cttp.TRACKING_ID_PREFIX+ Integer.toString(trackingID));
		submitCmdResp.addcttp_CommandResponse(cmdResp);		
		return new CttpResponse(cttpResp);
	}
	
	private void sendCommand(String cmd) {
		Request pilReq = new Request();
		pilReq.setDeviceID(0);
		pilReq.setCommandString(cmd);			
		int pilMsgID = PILCommandCache.getInstance().write(pilReq);
		CTILogger.info("CTTP command going to PIL: " + cmd + " msgid: " + pilMsgID);
	}	
	
	/**
	 * Build up a offset command string
	 * @param lmgroup
	 * @return
	 */
/*	private String[] makeOffsetCommand(LiteYukonPAObject lmgroup, int offset, int duration) {
		LMGroup lmg = CttpCmdCache.getInstance().retrieveGroup(lmgroup.getLiteID());
		
		
		if(lmg instanceof MacroGroup) {
			LMGroup[] cGrp = Cttp.getMacroGroupChildren((MacroGroup) lmg);
			String[] retCmds = new String[cGrp.length];
			for(int i = 0; i < cGrp.length; i++) {
				retCmds[i] = _makeOffsetCommand(cGrp[i].getPAObjectID().intValue(), 
			}
		}
		else {
			return new String[] { _makeOffsetCommand(lmgroup, offset, duration) };
		}
	}
*/	
	/**
	 * Assume the lite passed in cannot be a macro!!!
	 * @param lmGroup
	 * @param offset
	 * @param duration
	 * @return
	 */
	private String makeOffsetCommand(LiteYukonPAObject liteGroup, int offset, int duration) {
		if(liteGroup.getPaoName().indexOf("LCR") != -1 || liteGroup.getPaoName().indexOf("lcr") != -1) {
			return "control cycle " + calcCyclePercent(offset) + " period 30 count " + (duration*2) + " select id " + liteGroup.getLiteID();			
		}
		else
		if(liteGroup.getPaoName().indexOf("EPRO") != -1 || liteGroup.getPaoName().indexOf("epro") != -1) {
			return "control cycle " + calcCyclePercent(offset) + " period 30 count " + (duration*2) + " select id " + liteGroup.getLiteID();						
		}
		else {
			return "control xcom delta setpoint td 1 dsd " + offset + " te " + (duration*60) + " select id " + liteGroup.getLiteID();		
		}
	}
	
	/**
	 * build up a clear command string
	 * @author aaron	
	 */
	private String makeClearCommand(LiteYukonPAObject liteGroup) {
		if(liteGroup.getPaoName().indexOf("LCR") != -1 || liteGroup.getPaoName().indexOf("lcr") != -1) {
			// cycle terminate is the right thing to do , but for demos a 1s shed is more satisfying
			//return "control cycle terminate select id " + liteGroup.getLiteID();
			return "control shed 1s select id " + liteGroup.getLiteID();
		}
		else
		if(liteGroup.getPaoName().indexOf("EPRO") != -1 || liteGroup.getPaoName().indexOf("epro") != -1) {
			//return "control cycle terminate select id " + liteGroup.getLiteID();
			return "control shed 1s select id " + liteGroup.getLiteID();
		}
		else {
			return "control xcom delta setpoint td 0 dsd 0 te 0 select id " + liteGroup.getLiteID();
			//return "putconfig xcom setstate run select id " + liteGroup.getLiteID();						
		}
	}
	
	private String calcCyclePercent(int offset) {
		//scale linearly 30% - 60%, assuming 1-15 degrees, for now
		int pcnt = 30 + (int) ((double)(offset-1) * (1.0/15.0) * 30.0);
		return Integer.toString(pcnt) + "%";
	}

}
