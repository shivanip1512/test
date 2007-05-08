package com.cannontech.web.widget.support;

import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Finds all of the defined widgets and sets up the appropriate mappings.
 * 
 * This will look through the entire application context hierarchy for beans that
 * have the WidgetDefinitionBean interface. When found, each one is wrapped in
 * a MultiActionController which is then mapped to the path "/<bean name>/*".
 */
public class WidgetHandlerMapping extends AbstractUrlHandlerMapping implements InitializingBean {
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {
        ApplicationContext context = getApplicationContext();
        Map<String, WidgetDefinitionBean> beansOfType = 
            BeanFactoryUtils.beansOfTypeIncludingAncestors(context, WidgetDefinitionBean.class);
        for (Map.Entry<String, WidgetDefinitionBean> entry : beansOfType.entrySet()) {
            String path = "/" + entry.getKey() + "/*";
            MultiActionController controller = new MultiActionController(entry.getValue());
            registerHandler(path, controller);
        }
    }

}
