/*
 * Created on Nov 13, 2003
 */	
package com.cannontech.cttp;

import java.util.Iterator;

import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.cttp.data.CttpCmd;
import com.cannontech.cttp.db.CttpCmdGroup;
import com.cannontech.cttp.schema.cttp_CommandStatusGroupDetailType;
import com.cannontech.cttp.schema.cttp_CommandStatusRequestType;
import com.cannontech.cttp.schema.cttp_CommandStatusResponseType;
import com.cannontech.cttp.schema.cttp_CommandStatusType;
import com.cannontech.cttp.schema.cttp_CommandType;
import com.cannontech.cttp.schema.cttp_FailureType;
import com.cannontech.cttp.schema.cttp_OffsetCommandType;
import com.cannontech.cttp.schema.cttp_OperationType;
import com.cannontech.cttp.schema.cttp_OriginatorType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author aaron
 */
public class CommandStatusRequestHandler implements CttpMessageHandler {

	/* (non-Javadoc)
	 * @see com.cannontech.cttp.CttpMessageHandler#handleMessage(com.cannontech.cttp.CttpRequest)
	 */
	public CttpResponse handleMessage(CttpRequest req) throws Exception {
		cttp_OperationType cttpReq = req.getCttpOperation();
		cttp_CommandStatusRequestType cttpCmdStatusReq = cttpReq.getcttp_CommandStatusRequest();
		String trackingIDStr = cttpCmdStatusReq.getcommandTrackingCode().toString();
		int messageID = -1;
		if(trackingIDStr.length() >= Cttp.TRACKING_ID_PREFIX.length() + 1) {
			messageID = Integer.parseInt(trackingIDStr.substring(Cttp.TRACKING_ID_PREFIX.length()));
		}
		
		
		CttpCmd cmd = CttpCmdCache.getInstance().getCmd(messageID);
		

		cttp_OperationType cttpResp = new cttp_OperationType();
		cttp_CommandStatusResponseType cmdStatusResp = new cttp_CommandStatusResponseType();
		cttpResp.addcttp_CommandStatusResponse(cmdStatusResp);
				
		if(cmd == null || messageID == -1) {
			cttp_FailureType cttpFail = Cttp.makeFailure(Cttp.UNKNOWN_TRACKING_CODE, "Unknown tracking code");
			cmdStatusResp.addcttp_Failure(cttpFail);	
		}
		else {
		
		LiteYukonUser user = YukonSpringHook.getBean(YukonUserDao.class).getLiteYukonUser(cmd.getUserID().intValue());
		cttp_OriginatorType cttpOrig = new cttp_OriginatorType();
		cttpOrig.adduserName(user.getUsername());
		cttpOrig.adduserID(Integer.toString(user.getUserID()));
		cmdStatusResp.addcttp_Originator(cttpOrig);
		
		cttp_CommandType cttpCmd = new cttp_CommandType();
		if(cmd.getClearCmd().charValue() == 'Y') {
			cttpCmd.addcttp_ClearCommand("");
		}
		else {
			cttp_OffsetCommandType offsetCmd = new cttp_OffsetCommandType();
			offsetCmd.addoffsetDegreesF(cmd.getDegOffset().toString());
			offsetCmd.addoffsetDuration(cmd.getDuration().toString());
			offsetCmd.addoffsetOverrideable("yes");
			cttpCmd.addcttp_OffsetCommand(offsetCmd);
		}
		cmdStatusResp.addcttp_Command(cttpCmd);
		
		cttp_CommandStatusType cttpCmdStatus = new cttp_CommandStatusType();
		cttpCmdStatus.addsubmitCommandTime(Cttp.formatCTTPDate(cmd.getTimeSent()));
		cttpCmdStatus.addstatusUpdateTime(Cttp.formatCTTPDate(cmd.getLastUpdated()));
		cttpCmdStatus.addgroupsTotal(Integer.toString(cmd.getCttpCmdGroups().size()));
		cttpCmdStatus.addgroupsQueued("0");
		cttpCmdStatus.addgroupsSent(Integer.toString(cmd.getCttpCmdGroups().size()));
		cttpCmdStatus.addgroupsFailed("0");
		
		Iterator cmdGroupIter = cmd.getCttpCmdGroups().iterator();
		while(cmdGroupIter.hasNext()) {
			CttpCmdGroup cmdGrp = (CttpCmdGroup) cmdGroupIter.next();
			Integer groupID = cmdGrp.getLmGroupID();
			LiteYukonPAObject lmGroup = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(groupID.intValue());
			if(lmGroup != null) {
				cttp_CommandStatusGroupDetailType cmdStatusGroupDetail = new cttp_CommandStatusGroupDetailType();
				cmdStatusGroupDetail.addgroupID(Integer.toString(lmGroup.getLiteID()));
				//cmdStatusGroupDetail.addgroupID(lmGroup.getPaoName().replaceAll(" ", "").trim());
				cmdStatusGroupDetail.addgroupName(lmGroup.getPaoName());
				cmdStatusGroupDetail.addstatusUpdateTime(Cttp.formatCTTPDate(cmd.getLastUpdated()));
				cmdStatusGroupDetail.addgroupCommandStatus("SENT");
				cttpCmdStatus.addcttp_CommandStatusGroupDetail(cmdStatusGroupDetail);
			}			
		}
		
		cmdStatusResp.addcttp_CommandStatus(cttpCmdStatus);
		}
		
		return new CttpResponse(cttpResp);
	}

}
