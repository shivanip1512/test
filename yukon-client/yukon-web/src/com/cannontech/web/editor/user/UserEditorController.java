package com.cannontech.web.editor.user;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;

public class UserEditorController extends MultiActionController {

    private YukonUserDao yukonUserDao;

    public UserEditorController() {
        super();
    }

    public YukonUserDao getYukonUserDao() {
        return yukonUserDao;
    }

    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    public ModelAndView editUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("user/editUser.jsp");

        int userId = ServletRequestUtils.getRequiredIntParameter(request, "userId");
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        mav.addObject("user", user);

        return mav;
    }
    
}
