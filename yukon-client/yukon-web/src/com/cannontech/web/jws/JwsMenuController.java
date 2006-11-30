package com.cannontech.web.jws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.OptionPropertyChecker;


public class JwsMenuController extends AbstractController implements InitializingBean {
    List<JnlpController> jnlpList = new ArrayList<JnlpController>();
    Map<JnlpController, OptionPropertyChecker> checkerMap = new HashMap<JnlpController, OptionPropertyChecker>();

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("startpage");
        
        CheckUserPredicate predicate = new CheckUserPredicate(ServletUtil.getYukonUser(request.getSession()));
        Iterator filteredList = IteratorUtils.filteredIterator(jnlpList.iterator(), predicate);
        
        mav.addObject("jnlpList", filteredList);
        return mav;
    }
    
    public void afterPropertiesSet() throws Exception {
        ApplicationContext context = getApplicationContext();
        Map beansOfType = context.getBeansOfType(JnlpController.class);
        Collection jnlpBeans = beansOfType.values();
        for (Object object : jnlpBeans) {
            JnlpController jnlpController = (JnlpController) object;
            OptionPropertyChecker checker = OptionPropertyChecker.createRoleChecker(jnlpController.getRequiredRole());
            checkerMap.put(jnlpController, checker);
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
            OptionPropertyChecker checker = checkerMap.get(o);
            return checker.check(user);
        }
    }
}
