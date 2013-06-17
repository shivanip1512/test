package com.cannontech.web.capcontrol;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
@RequestMapping("/comments/*")
@Controller
public class CapControlCommentController {
    private static final String defaultCommentText = "(none)";
    private CapControlCommentDao commentDao;
    private RolePropertyDao rolePropertyDao;
    private FilterCacheFactory filterCacheFactory;
    
    @RequestMapping(method=RequestMethod.POST)
    public String add(int paoId, String comment, boolean submitNormal, boolean redirectToOneline, LiteYukonUser user, ModelMap model) throws Exception {
        rolePropertyDao.verifyProperty(YukonRoleProperty.ADD_COMMENTS, user);
        
        final CapControlComment capControlcomment = new CapControlComment();
        capControlcomment.setPaoId(paoId);
        capControlcomment.setUserId(user.getUserID());
        capControlcomment.setComment(StringUtils.isBlank(comment) ? defaultCommentText : comment);
        capControlcomment.setAltered(false);
        
        Date date = new Date();
        capControlcomment.setDate(date);
        capControlcomment.setAction(CommentAction.USER_COMMENT.toString());
        commentDao.add(capControlcomment);
        setupCommonAttributes(model, paoId, submitNormal, redirectToOneline);
        String redirectValue = getRedirectValue(redirectToOneline);
        return redirectValue;
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String update(int commentId, int paoId, String comment, boolean submitNormal, boolean redirectToOneline, LiteYukonUser user, ModelMap model) throws Exception {
        rolePropertyDao.verifyProperty(YukonRoleProperty.MODIFY_COMMENTS, user);
        
        CapControlComment capControlComment = commentDao.getById(commentId);
        capControlComment.setComment(comment);
        capControlComment.setAltered(true);
        commentDao.update(capControlComment);
        
        setupCommonAttributes(model, paoId, submitNormal, redirectToOneline);
        String redirectValue = getRedirectValue(redirectToOneline);
        return redirectValue;
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String remove(int commentId, int paoId, boolean submitNormal, boolean redirectToOneline, LiteYukonUser user, ModelMap model) throws Exception {
        rolePropertyDao.verifyProperty(YukonRoleProperty.MODIFY_COMMENTS, user);
        CapControlComment comment = new CapControlComment();
        comment.setId(commentId);
        commentDao.remove(comment);
        setupCommonAttributes(model, paoId, submitNormal, redirectToOneline);
        String redirectValue = getRedirectValue(redirectToOneline);
        return redirectValue;
    }
    
    @RequestMapping
    public String paoComments(HttpServletRequest request, HttpServletResponse response, int paoId, LiteYukonUser user, ModelMap model){
        setupPaoComments(paoId, user, model);
        model.addAttribute("submitNormal", false);
        model.addAttribute("redirectToOneline", false);
        return "comments/commentsPage.jsp";
    }
    
    @RequestMapping
    public String paoCommentsForOneline(HttpServletRequest request, HttpServletResponse response, int paoId, LiteYukonUser user, ModelMap model){
        setupPaoComments(paoId, user, model);
        model.addAttribute("submitNormal", true);
        model.addAttribute("redirectToOneline", true);
        return "comments/commentsPageOneline.jsp";
    }
    
    private void setupPaoComments(int paoId, LiteYukonUser user, ModelMap model) {
        CapControlCache filterCapControlCache = filterCacheFactory.createUserAccessFilteredCache(user);

        StreamableCapObject capObject = filterCapControlCache.getCapControlPAO(paoId);
        if (capObject == null) {
            throw new RuntimeException(user.getUsername() + " not authorized to view comments for paoId: " + paoId);
        }
        
        String name = capObject.getCcName();
        List<CapControlComment> comments = commentDao.getAllCommentsByPao(paoId);
        
        boolean modifyPermission = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.MODIFY_COMMENTS, user);
        boolean addPermission = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADD_COMMENTS, user);
        
        model.addAttribute("name", name);
        model.addAttribute("paoId", paoId);
        model.addAttribute("comments", comments);
        model.addAttribute("modifyPermission", modifyPermission);
        model.addAttribute("addPermission", addPermission);
    }

    private void setupCommonAttributes(ModelMap model, int paoId, boolean submitNormal, boolean redirectToOneline) {
        model.addAttribute("paoId", paoId);
        model.addAttribute("submitNormal", submitNormal);
        model.addAttribute("redirectToOneline", redirectToOneline);
    }
    
    private String getRedirectValue(boolean redirectToOneline) {
        if (redirectToOneline) {
            return "redirect:paoCommentsForOneline";
        }
        return "redirect:paoComments";
    }

    @Autowired
    public void setCommentDao(CapControlCommentDao commentDao) {
        this.commentDao = commentDao;
    }
    
    @Autowired
    public void setFilterCacheFactory(FilterCacheFactory filterCacheFactory) {
        this.filterCacheFactory = filterCacheFactory;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao){
        this.rolePropertyDao = rolePropertyDao;
    }
}
