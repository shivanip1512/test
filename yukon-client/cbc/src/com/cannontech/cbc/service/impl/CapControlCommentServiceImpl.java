package com.cannontech.cbc.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.cbc.service.CapControlCommentService;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.message.capcontrol.model.CommandType;

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
    
    /**
     * Returns a list of hmtl escaped comment strings.
     */
    public List<String> getComments(final int paoId, final int size) {
        final List<String> list = new ArrayList<String>(size);
        final List<CapControlComment> commentList = capControlCommentDao.getAllCommentsByPao(paoId);
        
        for (int x = 0; x < size && x < commentList.size(); x++) {
            CapControlComment comment = commentList.get(x);
            String text = comment.getComment();
            list.add(StringEscapeUtils.escapeHtml(text));
        }
        return list;
    }
    
    
    /**
     * Returns a list of hmtl escaped comment strings.
     */
    public List<String> getLastTenCommentsForActionAndType(final int paoId, final int cmdId) {
        final List<String> list = new ArrayList<String>(10);
        CommentAction action = CapControlComment.getActionForCommand(CommandType.getForId(cmdId));
        final List<String> commentList = capControlCommentDao.getLastTenCommentsByActionAndType(paoId, action);
        
        for (String comment : commentList) {
            list.add(StringEscapeUtils.escapeHtml(comment));
        }
        return list;
    }
    
    public void setCapControlCommentDao(CapControlCommentDao capControlCommentDao) {
        this.capControlCommentDao = capControlCommentDao;
    }
    
}
