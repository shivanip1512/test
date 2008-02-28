package com.cannontech.web.capcontrol;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.util.ServletUtil;

public class CapControlCommentController extends MultiActionController {
    private static final String defaultCommentText = "(none)";
    private CapControlCommentDao commentDao;
    private AuthDao authDao;
    
    public void add(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer paoId = ServletRequestUtils.getRequiredIntParameter(request, "paoId");
        final String commentText = ServletRequestUtils.getStringParameter(request,  "comment", defaultCommentText);
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        boolean isAuthorized = authDao.checkRoleProperty(user, CBCSettingsRole.ADD_COMMENTS);
        if (!isAuthorized) {
            throw new RuntimeException(user.getUsername() + " not authorized to add comments");
        }
        
        final CapControlComment comment = new CapControlComment();
        comment.setPaoId(paoId);
        comment.setUserId(user.getUserID());
        comment.setComment(commentText);
        comment.setAltered(false);
        
        Timestamp time = new Timestamp(System.currentTimeMillis());
        comment.setTime(time);
        comment.setAction(CommentAction.USER_COMMENT.toString());
        commentDao.add(comment);
    }
    
    public void update(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer commentId = ServletRequestUtils.getRequiredIntParameter(request, "commentId");
        final String commentText = ServletRequestUtils.getRequiredStringParameter(request, "comment");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        boolean isAuthorized = authDao.checkRoleProperty(user, CBCSettingsRole.MODIFY_COMMENTS);
        if (!isAuthorized) {
            throw new RuntimeException(user.getUsername() + " not authorized to update comments");
        }
        
        CapControlComment comment = commentDao.getById(commentId);
        comment.setComment(commentText);
        comment.setAltered(true);
        commentDao.update(comment);
    }
    
    public void remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer commentId = ServletRequestUtils.getRequiredIntParameter(request, "commentId");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        boolean isAuthorized = authDao.checkRoleProperty(user, CBCSettingsRole.MODIFY_COMMENTS);
        if (!isAuthorized) {
            throw new RuntimeException(user.getUsername() + " not authorized to remove comments");
        }
        
        CapControlComment comment = new CapControlComment();
        comment.setId(commentId);
        commentDao.remove(comment);
    }
    
    public void setCommentDao(CapControlCommentDao commentDao) {
        this.commentDao = commentDao;
    }
    
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

}
