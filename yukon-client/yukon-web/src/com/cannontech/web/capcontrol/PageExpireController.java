package com.cannontech.web.capcontrol;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.WebUpdatedDAO;

@Controller
public class PageExpireController {
    private WebUpdatedDAO<Integer> webUpdatedDAO;
    
    @RequestMapping("/pageExpire")
    public void pageExpire(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam("paoIds[]") String[] paoIds) throws Exception {
        response.setContentType("text/plain");
        final JSONArray array = new JSONArray(paoIds);
        
        boolean expired = false;
        
        for (int x = 0; x < array.length(); x++) {
            int paoId = array.getInt(x);
            expired = !webUpdatedDAO.containsKey(paoId);
            if (expired) break;
        }
        
        Writer writer = null;
        try {
            writer = response.getWriter();
            writer.write(Boolean.toString(expired));
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
    
    @Autowired
    public void setCapControlCache(CapControlCache cache) {
        webUpdatedDAO = cache.getUpdatedObjMap();
    }
    
}