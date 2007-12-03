package com.cannontech.web.jws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.util.ServletUtil;


public class JwsMenuController extends AbstractController implements InitializingBean {
    private List<JnlpController> jnlpList = new ArrayList<JnlpController>();
    private String view = "startpage";

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView(view);
        
        CheckUserPredicate predicate = new CheckUserPredicate(ServletUtil.getYukonUser(request));
        Iterator filteredList = IteratorUtils.filteredIterator(jnlpList.iterator(), predicate);
        
        String jreInstaller = CtiUtilities.getJREInstaller();
        mav.addObject("jreInstaller", jreInstaller);
        
        mav.addObject("jnlpList", filteredList);
        return mav;
    }
    
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {
        ApplicationContext context = getApplicationContext();
        Map beansOfType = context.getBeansOfType(JnlpController.class);
        Collection jnlpBeans = beansOfType.values();
        for (Object object : jnlpBeans) {
            JnlpController jnlpController = (JnlpController) object;
            jnlpList.add(jnlpController);
        }
    }
    
    private class CheckUserPredicate implements Predicate {
        private final LiteYukonUser user;
        public CheckUserPredicate(LiteYukonUser user) {
            this.user = user;
        }
        public boolean evaluate(Object o) {
            if (!(o instanceof JnlpController)) {
                return false;
            }
            JnlpController jnlpController = (JnlpController) o;
            UserChecker checker = jnlpController.getUserChecker();
            return checker.check(user);
        }
    }
}
