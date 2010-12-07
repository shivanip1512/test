package com.cannontech.cbc.model;

import java.util.Date;

import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.cbc.CapControlCommand;

public class CapControlComment {
    private int id;
    private int paoId;
    private Integer userId;
    private Date date;
    private String comment;
    private boolean altered;
    private String action;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public Integer getUserId() {
        return userId;
    }
    
    public String getUserName(){
    	if (userId == null) {
    		return null;
    	}
    	
    	YukonUserDao yukonUserDao = YukonSpringHook.getBean("yukonUserDao", YukonUserDao.class);
		return yukonUserDao.getLiteYukonUser(userId).getUsername();
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isAltered() {
        return altered;
    }
    
    public void setAltered(boolean altered){
        this.altered = altered;
    }
    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    public static CommentAction getActionForCommandId(int cmdId) {
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
                cmdId == CapControlCommand.SEND_ALL_ENABLE_TEMPCONTROL || 
                cmdId == CapControlCommand.SEND_ALL_DISABLE_TEMPCONTROL ||
                cmdId == CapControlCommand.SEND_ALL_ENABLE_VARCONTROL || 
                cmdId == CapControlCommand.SEND_ALL_DISABLE_VARCONTROL ||
                cmdId == CapControlCommand.SEND_ALL_ENABLE_TIMECONTROL || 
                cmdId == CapControlCommand.SEND_ALL_DISABLE_TIMECONTROL ||                
                cmdId == CapControlCommand.SEND_ALL_SCAN_2WAY || 
                cmdId == CapControlCommand.SEND_TIMESYNC || 
                cmdId == CapControlCommand.CONFIRM_SUBSTATION || 
                cmdId == CapControlCommand.CONFIRM_AREA || 
                cmdId == CapControlCommand.CONFIRM_SUB ||
                cmdId == CapControlCommand.CONFIRM_FEEDER ||
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
                cmdId == CapControlCommand.CMD_EMERGENCY_DISABLE_VERIFY || 
                cmdId == CapControlCommand.CMD_STANDALONE_VERIFY){ 
            action = CommentAction.VERIFY_CONTROL; 
        } else if (cmdId == CapControlCommand.SYNC_CBC_CAPBANK_STATE ||
        		cmdId == CapControlCommand.SYNC_ALL_CAPBANK_STATES) {

            action = CommentAction.SYNC_CAPBANK_STATE;

        } else if (cmdId == CapControlCommand.LTC_REMOTE_DISABLE ||
                cmdId == CapControlCommand.LTC_REMOTE_ENABLE ||
                cmdId == CapControlCommand.LTC_SCAN_INTEGRITY ||
                cmdId == CapControlCommand.LTC_TAP_POSITION_LOWER ||
                cmdId == CapControlCommand.LTC_TAP_POSITION_RAISE) {

            action = CommentAction.LTC_CONTROL;

        }else {
            throw new RuntimeException("Unsupported Action: " + cmdId);
        }

        return action;
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + (altered ? 1231 : 1237);
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + id;
		result = prime * result + paoId;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CapControlComment other = (CapControlComment) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (altered != other.altered)
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id != other.id)
			return false;
		if (paoId != other.paoId)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
    
}
