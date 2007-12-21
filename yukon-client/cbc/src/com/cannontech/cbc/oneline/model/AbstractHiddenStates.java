package com.cannontech.cbc.oneline.model;

import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.spring.YukonSpringHook;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxGraph;


public abstract class AbstractHiddenStates extends LxAbstractView implements HiddenStates {
    protected static final CapControlCommentDao commentDao = YukonSpringHook.getBean("capCommentDao", CapControlCommentDao.class);
    private static final int defaultTypeId = -1;
    private static final String defaultReason = "";
    
    protected String getReason(final int paoId, final CommentAction action) {
        return getReason(paoId, action, defaultTypeId);
    }

    protected String  getReason(final int paoId, final CommentAction action, final int type) {
        CapControlComment comment = null;
        
        try {
            switch (action) {
                case DISABLED : {
                    comment = commentDao.getDisabledByPaoId(paoId);
                    break;
                }
                case DISABLED_OVUV : {
                    if (type == defaultTypeId) break;
                    comment = commentDao.getDisabledOVUVByPaoId(paoId, type);
                    break;
                }
                case STANDALONE_REASON : {
                    comment = commentDao.getStandaloneReasonByPaoId(paoId);
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

    @Override
    public abstract LxGraph getGraph();
    
}
