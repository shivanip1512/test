package com.cannontech.web.common.resources;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ResourceCacheController {

    @Autowired ResourceCache cache;

    @RequestMapping("/WebConfig/yukon/styles/layout.css")
    public void layout(HttpServletResponse resp) throws ExecutionException, IOException {
        
        String css = cache.getResource(CachedResource.LAYOUT_CSS);
        resp.setContentType("text/css");
        resp.setCharacterEncoding("UTF-8");
            
        resp.getWriter().write(css);
    }
    
    @RequestMapping("/WebConfig/yukon/styles/yukon.css")
    public void yukon(HttpServletResponse resp) throws ExecutionException, IOException {
        
        String css = cache.getResource(CachedResource.YUKON_CSS);
        resp.setContentType("text/css");
        resp.setCharacterEncoding("UTF-8");
        
        resp.getWriter().write(css);
    }
    
}