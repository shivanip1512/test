package com.cannontech.cbc.model;

import java.util.Date;

import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.spring.YukonSpringHook;

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
    
    public static CommentAction getActionForCommand(CommandType type) {
        CommentAction action = CommentAction.getForCommand(type);
        if (action == null) {
            throw new RuntimeException("Unsupported Action: " + type);
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
