package com.cannontech.web.jws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.JAVA_WEB_START_LAUNCHER_ENABLED)
public class JwsMenuController extends AbstractController {
    private List<JnlpController> jnlpList = new ArrayList<JnlpController>();
    private String view = "startpage";

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView(view);
        List<JnlpController> filteredList = new ArrayList<JnlpController>();
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        for(JnlpController controller: jnlpList) {
            UserChecker checker = controller.getUserChecker();
            if(checker.check(user)) {
                filteredList.add(controller);
            }
        }
        
        String jreInstaller = CtiUtilities.getJREInstaller();
        mav.addObject("jreInstaller", jreInstaller);
        mav.addObject("jnlpListSize", filteredList.size());
        mav.addObject("jnlpList", filteredList);
        return mav;
    }
    
    @PostConstruct
    public void init() throws Exception {
        ApplicationContext context = getApplicationContext();
        Map beansOfType = context.getBeansOfType(JnlpController.class);
        Collection jnlpBeans = beansOfType.values();
        for (Object object : jnlpBeans) {
            JnlpController jnlpController = (JnlpController) object;
            jnlpList.add(jnlpController);
        }
    }
}
