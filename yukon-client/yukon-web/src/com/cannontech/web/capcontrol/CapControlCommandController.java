package com.cannontech.web.capcontrol;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.cbc.web.CapControlCommandExecutor;
import com.cannontech.cbc.web.CapControlType;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.cbc.CapControlCommand;
import com.cannontech.yukon.cbc.CCSpecialArea;
import com.cannontech.yukon.cbc.SubStation;

public class CapControlCommandController extends MultiActionController {
    private static final String defaultReason = "(none)";
    private static final String emptyString = "";
    private CapControlCommentDao commentDao;
    private AuthDao authDao;
    private CapControlCache capControlCache;
	
    //4-Tier Version of the command executor
	public void executeCommandTier(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    final LiteYukonUser user = ServletUtil.getYukonUser(request);
	    final CapControlCommandExecutor executor = createCapControlCommandExec(user);
	    
	    final Integer paoId = ServletRequestUtils.getRequiredIntParameter(request, "paoId");
	    final Integer cmdId = ServletRequestUtils.getRequiredIntParameter(request, "cmdId");
	    final String controlTypeString = ServletRequestUtils.getRequiredStringParameter(request, "controlType");
	    final CapControlType controlType = CapControlType.valueOf(controlTypeString);
	    
	    final String reason = ServletRequestUtils.getStringParameter(request, "reason");
	    final float[] opt = ServletRequestUtils.getFloatParameters(request, "opt");
	    
	    if (controlType.equals(CapControlType.SPECIAL_AREA)) {
	        handleSpecialAreaExecute(request, response, executor, paoId, cmdId, opt, reason);
	        return;
	    }
	    
	    executor.execute(controlType, cmdId, paoId, opt, null);
	    String forceComment = authDao.getRolePropertyValue(user,CBCSettingsRole.FORCE_COMMENTS);
	    if (reason != null) {
	        insertComment(paoId, user.getUserID(), reason, cmdId);
	    }else if(forceComment.equalsIgnoreCase("true")) {
	        insertComment(paoId, user.getUserID(), CapControlCommand.getCommandString(cmdId), cmdId);
	    }
	    
	    String redirectURL = ServletRequestUtils.getStringParameter(request, "redirectURL");
	    if (redirectURL != null) {
	        String location = ServletUtil.createSafeUrl(request, redirectURL);
	        response.sendRedirect(location);
	        return;
	    }
	}
	
	private void handleSpecialAreaExecute(final HttpServletRequest request, final HttpServletResponse response , 
	        final CapControlCommandExecutor executor, final Integer paoId, final Integer cmdId,
	        final float[] opt, final String reason) {

	    LiteYukonUser user = ServletUtil.getYukonUser(request);
	    
	    if (cmdId == CapControlCommand.ENABLE_AREA) {
	        final List<String> subsInConflict = new ArrayList<String>();
	        // Check for other special areas with this special area's subIds that are enabled.
	        List<SubStation> subs = capControlCache.getSubstationsBySpecialArea(paoId);
	        for (final SubStation sub : subs) {
	            boolean alreadyUsed = sub.getSpecialAreaEnabled();
	            if (alreadyUsed) {
	                CCSpecialArea otherArea = capControlCache.getCBCSpecialArea(sub.getSpecialAreaId());
	                if (!otherArea.getCcDisableFlag()) {
	                    String areaName = otherArea.getCcName();
	                    String value = areaName + " : " + sub.getCcName();
	                    subsInConflict.add(value);
	                }
	            }
	        }

	        boolean success = subsInConflict.isEmpty();
	        if (success) {
	            executor.execute(CapControlType.SPECIAL_AREA, cmdId, paoId, opt, null);
	            String forceComment = authDao.getRolePropertyValue(user,CBCSettingsRole.FORCE_COMMENTS);
	            if (reason != null) {
	                insertComment(paoId, user.getUserID(), reason, cmdId);
	            }else if(forceComment.equalsIgnoreCase("true")) {
	                insertComment(paoId, user.getUserID(), CapControlCommand.getCommandString(cmdId), cmdId);
	            }
	        }

	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("success", success);
	        jsonObject.put("errMessage", "Error: Some substation/s in this area are already on another enabled special area:\n\n" 
	                       + org.apache.commons.lang.StringUtils.join(subsInConflict, ",\n") 
	                       + "\n\nRemove them from the other area or disable the other area first.");
	        String jsonStr = jsonObject.toString();
	        response.addHeader("X-JSON", jsonStr);
	    } else {
	        executor.execute(CapControlType.SPECIAL_AREA, cmdId, paoId, opt, null);
	        if (reason != null) insertComment(paoId, user.getUserID(), reason, cmdId);
	        
	        JSONObject jsonObject = new JSONObject();
	        Boolean success = true;
	        jsonObject.put("success", success);
	        String jsonStr = jsonObject.toString();
	        response.addHeader("X-JSON", jsonStr);
	    }
	}
	
	public void executeTempMoveBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    final CapControlCommandExecutor executor = createCapControlCommandExecutor(request);
	    final Integer paoId = ServletRequestUtils.getRequiredIntParameter(request, "paoId");
	    final Integer cmdId = ServletRequestUtils.getRequiredIntParameter(request, "cmdId");
	    executor.execute(CapControlType.CAPBANK, cmdId, paoId, new float[]{}, null);
	}
	
	public void executeManualStateChange(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final CapControlCommandExecutor exec = createCapControlCommandExecutor(request);
        
        final Integer paoId = ServletRequestUtils.getRequiredIntParameter(request, "paoId");
        final Integer rawStateId = ServletRequestUtils.getRequiredIntParameter(request, "rawStateId");
        final String controlTypeString = ServletRequestUtils.getStringParameter(request, "controlType", emptyString);
        CapControlType controlType = CapControlType.valueOf(controlTypeString);

        exec.execute(controlType, CapControlCommand.CMD_MANUAL_ENTRY, paoId, new float[]{rawStateId}, null);
	}
	
	public void executeCommandOneLine(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //Generic One line for sending commands that are not in the tag menu.
		final LiteYukonUser user = ServletUtil.getYukonUser(request);
        final CapControlCommandExecutor exec = new CapControlCommandExecutor(capControlCache, user);
        
        final Integer paoId = ServletRequestUtils.getRequiredIntParameter(request, "paoId");
        final String controlTypeString = ServletRequestUtils.getStringParameter(request, "controlType", emptyString);
        final Integer cmdId = ServletRequestUtils.getRequiredIntParameter(request, "cmdId");
        final float[] optParams = ServletRequestUtils.getFloatParameters(request, "optParams");  
        CapControlType controlType = CapControlType.valueOf(controlTypeString);
        
        exec.execute(controlType, cmdId, paoId, optParams, null);
	}
	
	public void executeCommandOneLineTag(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        final CapControlCommandExecutor exec = new CapControlCommandExecutor(capControlCache, user);
        
        final Integer paoId = ServletRequestUtils.getRequiredIntParameter(request, "paoId");
        String controlTypeString = ServletRequestUtils.getStringParameter(request, "controlType", emptyString);
        CapControlType controlType = CapControlType.valueOf(controlTypeString);
        
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

        	//Only add the comments on a Disable
        	if(disableValue) {
        		insertComment(paoId, user.getUserID(), reason, cmdId);
        	}
            exec.execute(controlType, cmdId, paoId, null, operationalStateValue);
        }
        
        if(disableOVUVChange) {
	        int cmdId = (disableOVUVValue) ? getDisableOVUVCommandId(controlType) : getEnableOVUVCommandId(controlType);
	        String reason = ServletRequestUtils.getStringParameter(request, "disableOVUVReason", defaultReason);
        	if (reason.equals(emptyString) || reason.equals(defaultReason)) reason = generateUpdateReason(cmdId, paoId, user.getUsername());
	        
        	if(disableOVUVValue) {
        		insertComment(paoId, user.getUserID(), reason, cmdId);
        	}
            exec.execute(controlType, cmdId, paoId, null, operationalStateValue);
        }
        
        if (operationalStateChange) {
	        int cmdId = getOperationalStateCommandId();
        	String reason = ServletRequestUtils.getStringParameter(request, "operationalStateReason", defaultReason);
        	if (reason.equals(emptyString) || reason.equals(defaultReason)) reason = generateUpdateReason(cmdId, paoId, user.getUsername());

        	insertComment(paoId, user.getUserID(), reason, cmdId);
            exec.execute(controlType, cmdId, paoId, null, operationalStateValue);
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

        if (cmdId == CapControlCommand.BANK_ENABLE_OVUV || cmdId == CapControlCommand.SEND_ALL_ENABLE_OVUV) {
            
            updatedReason = "Device with Id " + paoId + " Enabled OVUV by " + userName;
            
        } else if (cmdId == CapControlCommand.BANK_DISABLE_OVUV || cmdId == CapControlCommand.SEND_ALL_DISABLE_OVUV) {
            
            updatedReason = "Device with Id " + paoId + " Disabled OVUV by " + userName;

        } else if ( cmdId == CapControlCommand.ENABLE_CAPBANK || 
        			cmdId == CapControlCommand.ENABLE_AREA || 
        			cmdId == CapControlCommand.ENABLE_FEEDER ||
        			cmdId == CapControlCommand.ENABLE_SUBBUS) {

        	updatedReason = "Device with Id " + paoId + " Enabled by " + userName;

        } else if ( cmdId == CapControlCommand.DISABLE_CAPBANK || 
    			cmdId == CapControlCommand.DISABLE_AREA || 
    			cmdId == CapControlCommand.DISABLE_FEEDER ||
    			cmdId == CapControlCommand.DISABLE_SUBBUS) {

        	updatedReason = "Device with Id " + paoId + " Disabled by " + userName;

        } else if (cmdId == CapControlCommand.OPERATIONAL_STATECHANGE) {

        	updatedReason = "Device with Id " + paoId + " Operational State updated by " + userName;

        }
        
        return updatedReason;
    }
    
    private String getAction(int cmdId) {
        CommentAction action;
        
        if (cmdId == CapControlCommand.BANK_ENABLE_OVUV || cmdId == CapControlCommand.SEND_ALL_ENABLE_OVUV) {
            
        	action = CommentAction.ENABLED_OVUV;
            
        } else if (cmdId == CapControlCommand.BANK_DISABLE_OVUV || cmdId == CapControlCommand.SEND_ALL_DISABLE_OVUV) {
            
        	action = CommentAction.DISABLED_OVUV;

        } else if ( cmdId == CapControlCommand.ENABLE_CAPBANK || 
        			cmdId == CapControlCommand.ENABLE_AREA || 
        			cmdId == CapControlCommand.ENABLE_FEEDER ||
        			cmdId == CapControlCommand.ENABLE_SUBBUS) {

        	action = CommentAction.ENABLED;

        } else if ( cmdId == CapControlCommand.DISABLE_CAPBANK || 
    			cmdId == CapControlCommand.DISABLE_AREA || 
    			cmdId == CapControlCommand.DISABLE_FEEDER ||
    			cmdId == CapControlCommand.DISABLE_SUBBUS) {

        	action = CommentAction.DISABLED;

        } else if (cmdId == CapControlCommand.OPERATIONAL_STATECHANGE) {

        	action = CommentAction.STANDALONE_REASON;

        } else if (cmdId == CapControlCommand.SEND_ALL_OPEN || 
        	    cmdId == CapControlCommand.SEND_ALL_CLOSE || 
        	    cmdId == CapControlCommand.SEND_ALL_ENABLE_OVUV || 
        	    cmdId == CapControlCommand.SEND_ALL_DISABLE_OVUV || 
        	    cmdId == CapControlCommand.SEND_ALL_SCAN_2WAY || 
        	    cmdId == CapControlCommand.SEND_TIMESYNC || 
        	    cmdId == CapControlCommand.CONFIRM_SUBSTATION || 
        	    cmdId == CapControlCommand.CONFIRM_AREA || 
        	    cmdId == CapControlCommand.CONFIRM_SUB || 
        	    cmdId == CapControlCommand.RESET_OPCOUNT) { 
        	action = CommentAction.SEND_ALL_CONTROL; 
        } else if (cmdId == CapControlCommand.OPEN_CAPBANK || 
        	    cmdId == CapControlCommand.CLOSE_CAPBANK || 
        	    cmdId == CapControlCommand.CONFIRM_OPEN || 
        	    cmdId == CapControlCommand.CONFIRM_CLOSE || 
        	    cmdId == CapControlCommand.SCAN_2WAY_DEV || 
        	    cmdId == CapControlCommand.FLIP_7010_CAPBANK){ 
    	    action = CommentAction.CAPBANK_CONTROL; 
	    } else if (cmdId == CapControlCommand.CMD_ALL_BANKS || 
        	    cmdId == CapControlCommand.CMD_FQ_BANKS || 
        	    cmdId == CapControlCommand.CMD_FAILED_BANKS || 
        	    cmdId == CapControlCommand.CMD_QUESTIONABLE_BANKS || 
        	    cmdId == CapControlCommand.CMD_DISABLE_VERIFY || 
        	    cmdId == CapControlCommand.CMD_STANDALONE_VERIFY){ 
    	    action = CommentAction.VERIFY_CONTROL; 
	    } else {
        	throw new RuntimeException("Unsupported Action: " + cmdId);
        }

        String actionToString = action.toString();
        return actionToString;
    }
    
    private int getDisableCommandId(final CapControlType controlType) {
        switch (controlType) {
            case AREA :
            case SPECIAL_AREA : return CapControlCommand.DISABLE_AREA;
           
            case SUBSTATION : return CapControlCommand.DISABLE_AREA;//on purpose
           
            case SUBBUS : return CapControlCommand.DISABLE_SUBBUS;
            
            case FEEDER : return CapControlCommand.DISABLE_FEEDER;
            
            case CAPBANK : return CapControlCommand.DISABLE_CAPBANK;
            
            default: throw new UnsupportedOperationException("No Disable Command Id for type: " + controlType);
        }
    }
    
    private int getEnableCommandId(final CapControlType controlType) {
        switch (controlType) {
            case AREA :
            case SPECIAL_AREA : return CapControlCommand.ENABLE_AREA;
       
            case SUBSTATION : return CapControlCommand.ENABLE_AREA;//on purpose
       
            case SUBBUS : return CapControlCommand.ENABLE_SUBBUS;
        
            case FEEDER : return CapControlCommand.ENABLE_FEEDER;
        
            case CAPBANK : return CapControlCommand.ENABLE_CAPBANK;
        
            default: throw new UnsupportedOperationException("No Enable Command Id for type: " + controlType);
        }    
    }
    
    private int getEnableOVUVCommandId(final CapControlType controlType) {
        switch (controlType) {
            case AREA :
            case SPECIAL_AREA :
            case SUBSTATION :
            case SUBBUS :    
            case FEEDER : return CapControlCommand.SEND_ALL_ENABLE_OVUV;
    
            case CAPBANK : return CapControlCommand.BANK_ENABLE_OVUV;
    
            default: throw new UnsupportedOperationException("No Enable OVUV Command Id for type: " + controlType);
        }    
    }
    
    private int getDisableOVUVCommandId(final CapControlType controlType) {
        switch (controlType) {
            case AREA :
            case SPECIAL_AREA :
            case SUBSTATION :
            case SUBBUS :    
            case FEEDER : return CapControlCommand.SEND_ALL_DISABLE_OVUV;

            case CAPBANK : return CapControlCommand.BANK_DISABLE_OVUV;

            default: throw new UnsupportedOperationException("No Disable OVUV Command Id for type: " + controlType);
        }    
    }
    
    private CapControlCommandExecutor createCapControlCommandExecutor(final HttpServletRequest request) {
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        return createCapControlCommandExec(user);
    }
    
    private CapControlCommandExecutor createCapControlCommandExec(final LiteYukonUser user) {
        CapControlCommandExecutor exec = new CapControlCommandExecutor(capControlCache, user);
        return exec;
    }
    
    private int getOperationalStateCommandId() {
    	return CapControlCommand.OPERATIONAL_STATECHANGE;
    }
    
    public void setCommentDao(CapControlCommentDao commentDao) {
        this.commentDao = commentDao;
    }
    
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }
    
}
