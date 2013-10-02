package com.cannontech.web.capcontrol;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.WebUpdatedDAO;

@Controller
public class PageExpireController {
    private WebUpdatedDAO<Integer> webUpdatedDAO;

    @RequestMapping("/pageExpire")
    public @ResponseBody JSONObject pageExpire( @RequestParam("paoIds[]") String[] paoIds) {

        boolean expired = false;

        for (String strPaoId : paoIds) {
            int paoId = Integer.parseInt(strPaoId);
            expired = ! webUpdatedDAO.containsKey(paoId);
            if (expired) break;
        }
        JSONObject result = new JSONObject();
        result.put("expired", expired);
        return result;
    }

    @Autowired
    public void setCapControlCache(CapControlCache cache) {
        webUpdatedDAO = cache.getUpdatedObjMap();
    }
}