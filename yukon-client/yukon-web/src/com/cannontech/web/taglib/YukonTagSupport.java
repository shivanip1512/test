package com.cannontech.web.taglib;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.AnnotationBeanWiringInfoResolver;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.wiring.BeanWiringInfo;
import org.springframework.beans.factory.wiring.BeanWiringInfoResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class YukonTagSupport extends SimpleTagSupport {
    private Logger logger = YukonLogManager.getLogger(this.getClass());
    protected ApplicationContext getApplicationContext() {
        PageContext pageContext = (PageContext) getJspContext();
        return WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
    }
    
    protected LiteYukonUser getYukonUser() {
        PageContext pageContext = (PageContext) getJspContext();
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(pageContext.getRequest());
        return yukonUser;
    }
    
    protected YukonUserContext getUserContext() {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(getJspContext());
        return userContext;
    }
    
    @Override
    public void setJspContext(JspContext pc) {
        super.setJspContext(pc);
        PageContext pageContext = (PageContext) getJspContext();
        ServletContext servletContext = pageContext.getServletContext();
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        
        BeanWiringInfoResolver beanWiringInfoResolver = new AnnotationBeanWiringInfoResolver();
        
        
        BeanWiringInfo bwi = beanWiringInfoResolver.resolveWiringInfo(this);
        if (bwi == null) {
            // Skip the bean if no wiring info given.
            return;
        }

        if (beanFactory == null) {
            if(logger.isEnabledFor(Level.WARN)) {
                logger.warn("BeanFactory has not been set on [" + getClass().getName() + "]: " +
                    "Make sure this configurer runs in a Spring container. " +
                    "For example, add it to a Spring application context as an XML bean definition.");
            }
            return;
        }

        if (bwi.indicatesAutowiring()) {
            // Perform autowiring.
            beanFactory.autowireBeanProperties(this, bwi.getAutowireMode(), bwi.getDependencyCheck());
        }
        else {
            // Perform explicit wiring.
            beanFactory.applyBeanPropertyValues(this, bwi.getBeanName());
        }
    }

}
