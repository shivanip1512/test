package com.cannontech.web.widget.support;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * Finds all of the defined widgets and sets up the appropriate mappings.
 * 
 * This will look through the entire application context hierarchy for beans that
 * have the WidgetDefinitionBean interface. When found, each one is wrapped in
 * a MultiActionController which is then mapped to the path "/&lt;shortName&gt;/*".
 */
public class WidgetHandlerMapping extends AbstractUrlHandlerMapping {
    
    @PostConstruct
    public void init() throws Exception {
        ApplicationContext context = getApplicationContext();
        Map<String, WidgetDefinitionBean> beansOfType = 
            BeanFactoryUtils.beansOfTypeIncludingAncestors(context, WidgetDefinitionBean.class);
        for (WidgetDefinitionBean bean : beansOfType.values()) {
            String path = "/" + bean.getShortName() + "/*";
            Object actionTarget = bean.getActionTarget();
            if(actionTarget instanceof WidgetController) {
                MultiActionController controller = new WidgetMultiActionController(actionTarget);
                registerHandler(path, controller);
            } else {
                registerHandler(path, actionTarget);
            }
        }
    }

}