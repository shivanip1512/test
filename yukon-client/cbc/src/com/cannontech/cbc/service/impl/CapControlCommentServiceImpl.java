package com.cannontech.cbc.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.cbc.service.CapControlCommentService;
import com.cannontech.cbc.web.CapControlType;

public class CapControlCommentServiceImpl implements CapControlCommentService {
    private static final String defaultReason = "";
    private CapControlCommentDao capControlCommentDao;

    @Override
    public String getReason(final int paoId, final CommentAction action, final CapControlType type) {
        CapControlComment comment = null;
        
        try {
            switch (action) {
                case DISABLED : {
                    comment = capControlCommentDao.getDisabledByPaoId(paoId);
                    break;
                }
                case DISABLED_OVUV : {
                    comment = capControlCommentDao.getDisabledOVUVByPaoId(paoId, type);
                    break;
                }
                case STANDALONE_REASON : {
                    comment = capControlCommentDao.getStandaloneReasonByPaoId(paoId);
                    break;
                }
            }
        } catch (DataRetrievalFailureException ignore) { }
        
        if (comment != null) {
            String reason = comment.getComment();
            return reason;
        }
        
        return defaultReason;
    }
    
    public List<String> getComments(final int paoId, final int size) {
        final List<String> list = new ArrayList<String>(size);
        final List<CapControlComment> commentList = capControlCommentDao.getAllCommentsByPao(paoId);
        
        for (int x = 0; x < size && x < commentList.size(); x++) {
            CapControlComment comment = commentList.get(x);
            String text = comment.getComment();
            list.add(text);
        }
        return list;
    }
    
    public void setCapControlCommentDao(CapControlCommentDao capControlCommentDao) {
        this.capControlCommentDao = capControlCommentDao;
    }
    
}
