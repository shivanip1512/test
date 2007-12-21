package com.cannontech.web.cbc;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.capcontrol.CapBankOperationalState;
import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public class OnelineCommentController implements Controller {
    private static final String emptyReason = "EMPTY";
    private static final String defaultReason = "(none)";
    private CapControlCommentDao commentDao;
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        final String userName = user.getUsername();
        
        final Integer paoId = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final String tagDesc = ServletRequestUtils.getRequiredStringParameter(request, "tagDesc");
        String reason = ServletRequestUtils.getStringParameter(request,  "reason", defaultReason);
        reason = updateReason(reason, tagDesc, paoId, userName);
        
        CapControlComment comment = new CapControlComment();
        comment.setPaoId(paoId);
        comment.setUserId(user.getUserID());
        comment.setComment(reason);
        comment.setAltered(false);
        
        Timestamp time = new Timestamp(System.currentTimeMillis());
        comment.setTime(time);
        
        String action = getAction(tagDesc);
        comment.setAction(action);
        
        commentDao.add(comment);
        return null;
    }
    
    private String updateReason(final String reason, final String tagDesc, final int paoId, final String userName) {
        String updatedReason = (reason.equals(emptyReason)) ? defaultReason : reason;

        if (tagDesc.endsWith("OVUVEnabled")) {
            
            updatedReason = "PaoID " + paoId + " Enabled OVUV from Oneline by " + userName;
            
        } else if (tagDesc.endsWith("Enabled")) {
            
            updatedReason = "PaoID " + paoId + " Enabled from Oneline by " + userName;
            
        } else if (tagDesc.equalsIgnoreCase("Switched") || tagDesc.equalsIgnoreCase("Fixed")) {
            
            if (reason.equals(defaultReason)) {
                updatedReason = "PaoID " + paoId + " Operational State updated by " + userName;
            }
            
        }
        
        return updatedReason;
    }
    
    private String getAction(String tagDesc) {
        CommentAction action;
        
        if (tagDesc.endsWith("OVUVEnabled")) {
            action = CommentAction.ENABLED_OVUV;
        } else if (tagDesc.endsWith("Enabled")) {
            action = CommentAction.ENABLED;
        } else if (tagDesc.endsWith("OVUVDisabled")) {
            action = CommentAction.DISABLED_OVUV;
        } else if (tagDesc.endsWith("Disabled")) {
            action = CommentAction.DISABLED;
        } else if (tagDesc.equalsIgnoreCase(CapBankOperationalState.Switched.name())) {
            action = CommentAction.STANDALONE_REASON;
        } else if (tagDesc.equalsIgnoreCase(CapBankOperationalState.Fixed.name())) {
            action = CommentAction.STANDALONE_REASON;    
        } else if (tagDesc.equalsIgnoreCase(CapBankOperationalState.StandAlone.name())) {
            action = CommentAction.STANDALONE_REASON;
        } else {
            throw new RuntimeException("Unsupported Action!");
        }
        
        String actionToString = action.toString();
        return actionToString;
    }
    
    public void setCommentDao(CapControlCommentDao commentDao) {
        this.commentDao = commentDao;
    }

}
