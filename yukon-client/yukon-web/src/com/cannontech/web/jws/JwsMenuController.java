package com.cannontech.web.jws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class JwsMenuController extends AbstractController implements InitializingBean {
    List<JnlpController> jnlpList = new ArrayList<JnlpController>();

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("startpage");
        mav.addObject("jnlpList", jnlpList);
        return mav;
    }
    
    public void afterPropertiesSet() throws Exception {
        ApplicationContext context = getApplicationContext();
        Map beansOfType = context.getBeansOfType(JnlpController.class);
        Collection jnlpBeans = beansOfType.values();
        for (Object object : jnlpBeans) {
            JnlpController jnlpController = (JnlpController) object;
            jnlpList.add(jnlpController);
        }
    }


}
