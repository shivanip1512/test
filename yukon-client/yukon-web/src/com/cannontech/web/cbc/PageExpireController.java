package com.cannontech.web.cbc;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.WebUpdatedDAO;

public class PageExpireController implements Controller {
    private WebUpdatedDAO<Integer> webUpdatedDAO;
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        final String paoIds = ServletRequestUtils.getRequiredStringParameter(request, "paoIds");
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
        
        return null;
    }
    
    public void setCapControlCache(CapControlCache capControlCache) {
        webUpdatedDAO = capControlCache.getUpdatedObjMap();
    }
    
}
