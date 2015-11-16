package com.cannontech.web.capcontrol;

import java.util.Collections;
import java.util.Map;

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
    public @ResponseBody Map<String, Boolean> pageExpire(@RequestParam("paoIds[]") String[] paoIds) {

        for (String strPaoId : paoIds) {
            int paoId = Integer.parseInt(strPaoId);
            if (!webUpdatedDAO.containsKey(paoId)) {
                return Collections.singletonMap("expired", true);
            }
        }

        return Collections.singletonMap("expired", false);
    }

    @Autowired
    public void setCapControlCache(CapControlCache cache) {
        webUpdatedDAO = cache.getUpdatedObjects();
    }
}