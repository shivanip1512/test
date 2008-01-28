package com.cannontech.web.cbc;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.cbc.web.CBCCommandExec;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CapControlConst;

public class CapControlCommandController extends MultiActionController {
    private static final String defaultReason = "(none)";
    private static final String emptyString = "";
    private CapControlCommentDao commentDao;
    private CapControlCache capControlCache;
	
	public void executeCommandCapControl(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//4-Tier Version of the command executor
		throw new UnsupportedOperationException("Function not implemented.");
	}
	
	public void executeManualStateChange(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    final LiteYukonUser user = ServletUtil.getYukonUser(request);
        final CBCCommandExec exec = new CBCCommandExec(capControlCache, user);
        
        final Integer paoId = ServletRequestUtils.getRequiredIntParameter(request, "paoId");
        final Integer rawStateId = ServletRequestUtils.getRequiredIntParameter(request, "rawStateId");
        final String controlType = ServletRequestUtils.getStringParameter(request, "controlType", emptyString);

        exec.commandExecuteMethod(controlType, CBCCommand.CMD_MANUAL_ENTRY, paoId, new float[]{rawStateId}, null);
	}
	
	public void executeCommandOneLine(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //Generic One line for sending commands that are not in the tag menu.
		final LiteYukonUser user = ServletUtil.getYukonUser(request);
        final CBCCommandExec exec = new CBCCommandExec(capControlCache, user);
        
        final Integer paoId = ServletRequestUtils.getRequiredIntParameter(request, "paoId");
        final String controlType = ServletRequestUtils.getStringParameter(request, "controlType", emptyString);
        final Integer cmdId = ServletRequestUtils.getRequiredIntParameter(request, "cmdId");
        final float[] optParams = ServletRequestUtils.getFloatParameters(request, "optParams");  
        
        exec.commandExecuteMethod(controlType, cmdId, paoId, optParams, null);
	}
	
	public void executeCommandOneLineTag(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        final CBCCommandExec exec = new CBCCommandExec(capControlCache, user);
        
        final Integer paoId = ServletRequestUtils.getRequiredIntParameter(request, "paoId");
        String controlType = ServletRequestUtils.getStringParameter(request, "controlType", emptyString);
        
        boolean disableChange = ServletRequestUtils.getBooleanParameter(request, "disableChange", false);
        boolean disableOVUVChange = ServletRequestUtils.getBooleanParameter(request, "disableOVUVChange", false);
        boolean operationalStateChange = ServletRequestUtils.getBooleanParameter(request, "operationalStateChange", false);
        
        boolean disableValue = ServletRequestUtils.getBooleanParameter(request, "disableValue", false);
        boolean disableOVUVValue = ServletRequestUtils.getBooleanParameter(request, "disableOVUVValue", false);
        String operationalStateValue = ServletRequestUtils.getStringParameter(request, "operationalStateValue", null);
        
        CTILogger.debug(request.getServletPath() +
                ", controlType = " + controlType +
                ", paoId = " + paoId +
                ", operationalStateValue = " + operationalStateValue +
                ", disableChange = " + disableChange + ", disableValue = " + disableValue + 
                ", disableOVUVChange = " + disableOVUVChange + ", disableOVUVValue = " + disableOVUVValue +
                ", operationalStateChange = " + operationalStateChange + ", operationalStateValue = " + operationalStateValue);
        
        if (disableChange) {
	        int cmdId = (disableValue) ? getDisableCommandId(controlType) : getEnableCommandId(controlType);
	        String reason = ServletRequestUtils.getStringParameter(request, "disableReason", defaultReason);
        	if( reason.equals("") || reason.equals(defaultReason)) reason = generateUpdateReason(cmdId, paoId, user.getUsername());

        	insertComment(paoId, user.getUserID(), reason, cmdId);
            exec.commandExecuteMethod(controlType, cmdId, paoId, null, operationalStateValue);
        }
        
        if(disableOVUVChange) {
	        int cmdId = (disableOVUVValue) ? getDisableOVUVCommandId(controlType) : getEnableOVUVCommandId(controlType);
	        String reason = ServletRequestUtils.getStringParameter(request, "disableOVUVReason", defaultReason);
        	if (reason.equals(emptyString) || reason.equals(defaultReason)) reason = generateUpdateReason(cmdId, paoId, user.getUsername());
	        
        	insertComment(paoId, user.getUserID(), reason, cmdId);
            exec.commandExecuteMethod(controlType, cmdId, paoId, null, operationalStateValue);
        }
        
        if (operationalStateChange) {
	        int cmdId = getOperationalStateCommandId();
        	String reason = ServletRequestUtils.getStringParameter(request, "operationalStateReason", defaultReason);
        	if (reason.equals(emptyString) || reason.equals(defaultReason)) reason = generateUpdateReason(cmdId, paoId, user.getUsername());

        	insertComment(paoId, user.getUserID(), reason, cmdId);
            exec.commandExecuteMethod(controlType, cmdId, paoId, null, operationalStateValue);
        }

	}
	
    private void insertComment(int paoId, int userId, String reason, int cmdId) {
        /* Add Comment */
        CapControlComment comment = new CapControlComment();
        comment.setPaoId(paoId);
        comment.setUserId(userId);
        comment.setComment(reason);
        comment.setAltered(false);
        
        Timestamp time = new Timestamp(System.currentTimeMillis());
        comment.setTime(time);
        
        String action = getAction(cmdId);
        comment.setAction(action);
        
        commentDao.add(comment);
    }
    
    private String generateUpdateReason(final int cmdId, final int paoId, final String userName) {
        //Check what the empty case will be
    	String updatedReason = defaultReason;

        if (cmdId == CBCCommand.BANK_ENABLE_OVUV || cmdId == CBCCommand.SEND_ALL_ENABLE_OVUV) {
            
            updatedReason = "PaoID " + paoId + " Enabled OVUV by " + userName;
            
        } else if (cmdId == CBCCommand.BANK_DISABLE_OVUV || cmdId == CBCCommand.SEND_ALL_DISABLE_OVUV) {
            
            updatedReason = "PaoID " + paoId + " Disabled OVUV by " + userName;

        } else if ( cmdId == CBCCommand.ENABLE_CAPBANK || 
        			cmdId == CBCCommand.ENABLE_AREA || 
        			cmdId == CBCCommand.ENABLE_FEEDER ||
        			cmdId == CBCCommand.ENABLE_SUBBUS) {

        	updatedReason = "PaoID " + paoId + " Enabled by " + userName;

        } else if ( cmdId == CBCCommand.DISABLE_CAPBANK || 
    			cmdId == CBCCommand.DISABLE_AREA || 
    			cmdId == CBCCommand.DISABLE_FEEDER ||
    			cmdId == CBCCommand.DISABLE_SUBBUS) {

        	updatedReason = "PaoID " + paoId + " Disabled by " + userName;

        } else if (cmdId == CBCCommand.OPERATIONAL_STATECHANGE) {

        	updatedReason = "PaoID " + paoId + " Operational State updated by " + userName;

        }
        
        return updatedReason;
    }
    
    private String getAction(int cmdId) {
        CommentAction action;
        
        if (cmdId == CBCCommand.BANK_ENABLE_OVUV || cmdId == CBCCommand.SEND_ALL_ENABLE_OVUV) {
            
        	action = CommentAction.ENABLED_OVUV;
            
        } else if (cmdId == CBCCommand.BANK_DISABLE_OVUV || cmdId == CBCCommand.SEND_ALL_DISABLE_OVUV) {
            
        	action = CommentAction.DISABLED_OVUV;

        } else if ( cmdId == CBCCommand.ENABLE_CAPBANK || 
        			cmdId == CBCCommand.ENABLE_AREA || 
        			cmdId == CBCCommand.ENABLE_FEEDER ||
        			cmdId == CBCCommand.ENABLE_SUBBUS) {

        	action = CommentAction.ENABLED;

        } else if ( cmdId == CBCCommand.DISABLE_CAPBANK || 
    			cmdId == CBCCommand.DISABLE_AREA || 
    			cmdId == CBCCommand.DISABLE_FEEDER ||
    			cmdId == CBCCommand.DISABLE_SUBBUS) {

        	action = CommentAction.DISABLED;

        } else if (cmdId == CBCCommand.OPERATIONAL_STATECHANGE) {

        	action = CommentAction.STANDALONE_REASON;

        } else {
        	throw new RuntimeException("Unsupported Action: " + cmdId);
        }

        String actionToString = action.toString();
        return actionToString;
    }
    
    private int getDisableCommandId(final String controlType) {
    	if( CapControlConst.CMD_TYPE_AREA.equals(controlType) ) {
    		return CBCCommand.DISABLE_AREA;
    	} else if ( CapControlConst.CMD_TYPE_SUBSTATION.equals(controlType)) {
    		return CBCCommand.DISABLE_AREA;//on purpose
    	} else if ( CapControlConst.CMD_TYPE_SUB.equals(controlType)) {
    		return CBCCommand.DISABLE_SUBBUS;
    	} else if ( CapControlConst.CMD_TYPE_FEEDER.equals(controlType)) {
    		return CBCCommand.DISABLE_FEEDER;
    	} else if ( CapControlConst.CMD_TYPE_CAPBANK.equals(controlType)) {
    		return CBCCommand.DISABLE_CAPBANK;
    	} else {
    		throw new UnsupportedOperationException(" No Disable Command Id for type: " + controlType );
    	}
    }
    private int getEnableCommandId( String type ) {
    	if( CapControlConst.CMD_TYPE_AREA.equals(type) ) {
    		return CBCCommand.ENABLE_AREA;
    	} else if ( CapControlConst.CMD_TYPE_SUBSTATION.equals(type)) {
    		return CBCCommand.ENABLE_AREA;//on purpose
    	} else if ( CapControlConst.CMD_TYPE_SUB.equals(type)) {
    		return CBCCommand.ENABLE_SUBBUS;
    	} else if ( CapControlConst.CMD_TYPE_FEEDER.equals(type)) {
    		return CBCCommand.ENABLE_FEEDER;
    	} else if ( CapControlConst.CMD_TYPE_CAPBANK.equals(type)) {
    		return CBCCommand.ENABLE_CAPBANK;
    	} else {
    		throw new UnsupportedOperationException(" No Enable Command Id for type: " + type );
    	}
    }
    private int getEnableOVUVCommandId( String type ) {
    	if( CapControlConst.CMD_TYPE_AREA.equals(type) ) {
    		return CBCCommand.SEND_ALL_ENABLE_OVUV;
    	} else if ( CapControlConst.CMD_TYPE_SUBSTATION.equals(type)) {
    		return CBCCommand.SEND_ALL_ENABLE_OVUV;//on purpose
    	} else if ( CapControlConst.CMD_TYPE_SUB.equals(type)) {
    		return CBCCommand.SEND_ALL_ENABLE_OVUV;
    	} else if ( CapControlConst.CMD_TYPE_FEEDER.equals(type)) {
    		return CBCCommand.SEND_ALL_ENABLE_OVUV;
    	} else if ( CapControlConst.CMD_TYPE_CAPBANK.equals(type)) {
    		return CBCCommand.BANK_ENABLE_OVUV;
    	} else {
    		throw new UnsupportedOperationException(" No Enable OVUV Command Id for type: " + type );
    	}
    }
    private int getDisableOVUVCommandId( String type ) {
    	if( CapControlConst.CMD_TYPE_AREA.equals(type) ) {
    		return CBCCommand.SEND_ALL_DISABLE_OVUV;
    	} else if ( CapControlConst.CMD_TYPE_SUBSTATION.equals(type)) {
    		return CBCCommand.SEND_ALL_DISABLE_OVUV;//on purpose
    	} else if ( CapControlConst.CMD_TYPE_SUB.equals(type)) {
    		return CBCCommand.SEND_ALL_DISABLE_OVUV;
    	} else if ( CapControlConst.CMD_TYPE_FEEDER.equals(type)) {
    		return CBCCommand.SEND_ALL_DISABLE_OVUV;
    	} else if ( CapControlConst.CMD_TYPE_CAPBANK.equals(type)) {
    		return CBCCommand.BANK_DISABLE_OVUV;
    	} else {
    		throw new UnsupportedOperationException(" No Disable OVUV Command Id for type: " + type );
    	}
    }
    
    private int getOperationalStateCommandId() {
    	return CBCCommand.OPERATIONAL_STATECHANGE;
    }
    
    public void setCommentDao(CapControlCommentDao commentDao) {
        this.commentDao = commentDao;
    }
    
    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }
    
}
