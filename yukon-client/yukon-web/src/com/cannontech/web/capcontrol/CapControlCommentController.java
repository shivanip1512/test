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
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.cbc.StreamableCapObject;

@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
@RequestMapping("/comments/*")
@Controller
public class CapControlCommentController {
    private static final String defaultCommentText = "(none)";
    private CapControlCommentDao commentDao;
    private RolePropertyDao rolePropertyDao;
    private FilterCacheFactory filterCacheFactory;
    
    @RequestMapping(method=RequestMethod.POST)
    public String add(int paoId, String comment, LiteYukonUser user, ModelMap model) throws Exception {
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
        
        model.addAttribute("paoId", paoId);
        return "redirect:commentList";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String update(int commentId, int paoId, String comment, LiteYukonUser user, ModelMap model) throws Exception {
        rolePropertyDao.verifyProperty(YukonRoleProperty.MODIFY_COMMENTS, user);
        
        CapControlComment capControlComment = commentDao.getById(commentId);
        capControlComment.setComment(comment);
        capControlComment.setAltered(true);
        commentDao.update(capControlComment);
        
        model.addAttribute("paoId", paoId);
        return "redirect:commentList";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String remove(int commentId, int paoId, LiteYukonUser user, ModelMap model) throws Exception {
        rolePropertyDao.verifyProperty(YukonRoleProperty.MODIFY_COMMENTS, user);
        
        CapControlComment comment = new CapControlComment();
        comment.setId(commentId);
        commentDao.remove(comment);
        
        model.addAttribute("paoId", paoId);
        return "redirect:commentList";
    }
    
    @RequestMapping
    public String paoComments(HttpServletRequest request, HttpServletResponse response, int paoId, LiteYukonUser user, ModelMap model){
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
        return "comments/commentsPage.jsp";
    }
    
    @RequestMapping
    public String commentList(int paoId, LiteYukonUser user, ModelMap model) {
        List<CapControlComment> comments = commentDao.getAllCommentsByPao(paoId);
        boolean modifyPermission = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.MODIFY_COMMENTS, user);
        boolean addPermission = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADD_COMMENTS, user);
        model.addAttribute("comments", comments);
        model.addAttribute("modifyPermission", modifyPermission);
        model.addAttribute("addPermission", addPermission);
        return "comments/commentList.jsp";
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
