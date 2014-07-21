package com.cannontech.web.widget.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class WidgetTestController extends MultiActionController implements ApplicationContextAware {
    @SuppressWarnings("unchecked")
    public ModelAndView menu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("test/menu.jsp");
        ApplicationContext context = getApplicationContext();
        Map<String, WidgetDefinitionBean> beansOfType = 
            BeanFactoryUtils.beansOfTypeIncludingAncestors(context, WidgetDefinitionBean.class);
        mav.addObject("widgets", beansOfType.values());
        return mav;
    }
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("test/create.jsp");
        return mav;
    }
}
