/*
 * Created on Nov 13, 2003
 */
package com.cannontech.cttp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.cttp.data.CttpCmd;
import com.cannontech.cttp.schema.cttp_GroupStatusGroupDetailType;
import com.cannontech.cttp.schema.cttp_GroupStatusResponseType;
import com.cannontech.cttp.schema.cttp_GroupStatusType;
import com.cannontech.cttp.schema.cttp_OperationType;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOFactory;


/**
 * Return a group status response message.  Assumes they want the status of all groups for now.
 * TODO: handle requests for the status of specific groups
 * @author aaron
 */
public class GroupStatusRequestHandler implements CttpMessageHandler {

	/* (non-Javadoc)
	 * @see com.cannontech.cttp.CttpMessageHandler#handleMessage(com.cannontech.cttp.schema.cttp_OperationType)
	 */
	public CttpResponse handleMessage(CttpRequest req)
		throws Exception {
		
		//build response
		cttp_OperationType cttpResp = new cttp_OperationType();
			
					
		//find lm groups
		LiteYukonUser user = req.getUser();
		ArrayList lmGroups = Cttp.retrieveLMGroups(user);			

		cttp_GroupStatusResponseType groupStatusResp = new cttp_GroupStatusResponseType();
			cttp_GroupStatusType groupStatus = new cttp_GroupStatusType();
			
		int totalStats = 0;
		int groupsWithOffset = 0;
		int statsWithOffset = 0;
		
			Iterator lmGroupIter = lmGroups.iterator();
			while(lmGroupIter.hasNext()) {
				LiteYukonPAObject lmg = (LiteYukonPAObject) lmGroupIter.next();
				
				LMGroup lmGroupPAO= CttpCmdCache.getInstance().retrieveGroup(lmg.getLiteID());

				cttp_GroupStatusGroupDetailType groupDetail = new cttp_GroupStatusGroupDetailType();
				groupDetail.addgroupID(Integer.toString(lmg.getLiteID()));
				//groupDetail.addgroupID(lmg.getPaoName().replaceAll(" ", "").trim());
				groupDetail.addgroupName(lmg.getPaoName());
			
				int numStats = Cttp.getNumberOfStatsInGroup(lmGroupPAO);
				totalStats += numStats;
				
				groupDetail.addstatsInGroup(Integer.toString(numStats));
			
				String cmdInEffect = "NONE";				
						
				CttpCmd curCmd = CttpCmdCache.getInstance().getCurrentCmdForGroup(lmg.getLiteID());
				if(curCmd != null) {
					Date cmdExpires = new Date(curCmd.getTimeSent().getTime() + curCmd.getDuration().intValue()*60L*60L*1000L);
					groupDetail.addcommandInEffect("OFFSET");
					groupDetail.addcommandTrackingCode(Cttp.TRACKING_ID_PREFIX + curCmd.getTrackingID().toString());
					groupDetail.addcommandExpireTime(Cttp.formatCTTPDate(cmdExpires));
					groupsWithOffset++;
					statsWithOffset += Cttp.getNumberOfStatsInGroup(lmGroupPAO);
					
				}
				else {
					groupDetail.addcommandInEffect("NONE");
				}				
			
				groupStatus.addcttp_GroupStatusGroupDetail(groupDetail);
			}
			
		groupStatus.addgroupsTotal(Integer.toString(lmGroups.size()));		
		groupStatus.addstatsTotal(Integer.toString(totalStats));
		groupStatus.addgroupsWithOffset(Integer.toString(groupsWithOffset)); 
		groupStatus.addstatsWithOffset(Integer.toString(statsWithOffset)); 
		
		groupStatusResp.addcttp_GroupStatus(groupStatus);
		cttpResp.addcttp_GroupStatusResponse(groupStatusResp);
			
		return new CttpResponse(cttpResp);		
	}		
		
			
	

}
