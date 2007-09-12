package com.cannontech.web.stars;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.stars.util.ServletUtils;

public class StarsActionController implements Controller,BeanFactoryAware {
    private BeanFactory beanFactory;
    private String prefix;
    private String defaultBeanName;
    
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final boolean isMultiPart = ServletUtils.isMultiPartRequest(request);

        final String action = (isMultiPart) ?
                ServletUtils.getFormField(ServletUtils.getItemList(request), "action") : ServletRequestUtils.getStringParameter(request, "action");
        
        final String beanName = (action != null) ? prefix + action : prefix + defaultBeanName;

        Controller actionController = (Controller) beanFactory.getBean(beanName, Controller.class);
        actionController.handleRequest(request, response);
        return null;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public void setDefaultBeanName(String defaultBeanName) {
        this.defaultBeanName = defaultBeanName;
    }
    
}
