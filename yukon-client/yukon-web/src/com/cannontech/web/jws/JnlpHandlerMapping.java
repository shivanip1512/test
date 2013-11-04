package com.cannontech.web.jws;

import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

public class JnlpHandlerMapping extends AbstractUrlHandlerMapping {
    @PostConstruct
    public void init() throws Exception {
        ApplicationContext context = getApplicationContext();
        Map<String, JnlpControllerBase> beansOfType = context.getBeansOfType(JnlpControllerBase.class);
        Collection<JnlpControllerBase> jnlpBeans = beansOfType.values();
        for (JnlpControllerBase jnlpController : jnlpBeans) {
            String path = "/" + jnlpController.getPath();
            registerHandler(path, jnlpController);
        }
    }
}
