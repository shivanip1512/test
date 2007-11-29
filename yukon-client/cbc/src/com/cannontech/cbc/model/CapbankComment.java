package com.cannontech.cbc.model;

import java.util.Date;

public class CapbankComment {
    
    int id;
    int paoId;
    int userId;
    Date time;
    String comment;
    boolean altered;
    
    public CapbankComment()
    {}

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + id;
        result = prime * result + paoId;
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + userId;
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
        final CapbankComment other = (CapbankComment) obj;
        if (comment == null) {
            if (other.comment != null)
                return false;
        } else if (!comment.equals(other.comment))
            return false;
        if (id != other.id)
            return false;
        if (paoId != other.paoId)
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        if (userId != other.userId)
            return false;
        return true;
    }

    public boolean isAltered() {
        return altered;
    }

    public void setAltered(boolean altered){
        this.altered = altered;
    }
    
    
}
