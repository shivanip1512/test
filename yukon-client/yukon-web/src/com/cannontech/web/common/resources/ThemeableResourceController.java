package com.cannontech.web.common.resources;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/WebConfig/yukon/styles/")
public class ThemeableResourceController {

    @Autowired ThemeableResourceCache cache;

    @RequestMapping("layout.css")
    public void layout(HttpServletResponse resp) throws Exception {
        
        String css = cache.getResource(ThemeableResource.LAYOUT_CSS).getValue();
        resp.setContentType("text/css");
        resp.setCharacterEncoding("UTF-8");
            
        resp.getWriter().write(css);
    }
    
    @RequestMapping("yukon.css")
    public void yukon(HttpServletResponse resp) throws Exception {
        
        String css = cache.getResource(ThemeableResource.YUKON_CSS).getValue();
        resp.setContentType("text/css");
        resp.setCharacterEncoding("UTF-8");
        
        resp.getWriter().write(css);
    }
    
    @RequestMapping("yukon.default.css")
    public void yukonDefault(HttpServletResponse resp) throws Exception {
        
        String css = cache.getResource(ThemeableResource.YUKON_DEFAULT_CSS).getValue();
        resp.setContentType("text/css");
        resp.setCharacterEncoding("UTF-8");
        
        resp.getWriter().write(css);
    }
    
    @RequestMapping("buttons.css")
    public void buttons(HttpServletResponse resp) throws Exception {
        
        String css = cache.getResource(ThemeableResource.BUTTONS_CSS).getValue();
        resp.setContentType("text/css");
        resp.setCharacterEncoding("UTF-8");
        
        resp.getWriter().write(css);
    }
    
    @RequestMapping("buttons.default.css")
    public void buttonsDefault(HttpServletResponse resp) throws Exception {
        
        String css = cache.getResource(ThemeableResource.BUTTONS_DEFAULT_CSS).getValue();
        resp.setContentType("text/css");
        resp.setCharacterEncoding("UTF-8");
        
        resp.getWriter().write(css);
    }
    
    @RequestMapping("overrides.css")
    public void overrides(HttpServletResponse resp) throws Exception {
        
        String css = cache.getResource(ThemeableResource.OVERRIDES_CSS).getValue();
        resp.setContentType("text/css");
        resp.setCharacterEncoding("UTF-8");
        
        resp.getWriter().write(css);
    }
    
}