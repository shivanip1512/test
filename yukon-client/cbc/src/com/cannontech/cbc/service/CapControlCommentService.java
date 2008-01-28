package com.cannontech.cbc.service;

import java.util.List;

import com.cannontech.cbc.dao.CommentAction;

public interface CapControlCommentService {

    public String getReason(int paoId, CommentAction action, int type);
    
    public List<String> getComments(int paoId, int size);
    
}
