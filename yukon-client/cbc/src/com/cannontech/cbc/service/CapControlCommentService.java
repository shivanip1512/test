package com.cannontech.cbc.service;

import java.util.List;

import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.web.CapControlType;

public interface CapControlCommentService {

    public String getReason(int paoId, CommentAction action, CapControlType type);
    
    public List<String> getComments(int paoId, int size);
    
}
