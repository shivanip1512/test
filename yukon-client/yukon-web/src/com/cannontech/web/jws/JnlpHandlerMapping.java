package com.cannontech.web.jws;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

public class JnlpHandlerMapping extends AbstractUrlHandlerMapping implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        ApplicationContext context = getApplicationContext();
        Map beansOfType = context.getBeansOfType(JnlpController.class);
        Collection jnlpBeans = beansOfType.values();
        for (Object object : jnlpBeans) {
            JnlpController jnlpController = (JnlpController) object;
            String path = "/" + jnlpController.getPath();
            registerHandler(path, jnlpController);
        }
    }

}
