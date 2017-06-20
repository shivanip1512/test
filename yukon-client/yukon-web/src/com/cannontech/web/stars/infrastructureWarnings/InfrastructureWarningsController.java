package com.cannontech.web.stars.infrastructureWarnings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.infrastructure.dao.InfrastructureWarningsDao;
import com.cannontech.infrastructure.model.InfrastructureWarning;

@Controller
@RequestMapping("/infrastructureWarnings/*")
public class InfrastructureWarningsController {
    
    @Autowired private InfrastructureWarningsDao infrastructureWarningsDao;
    
    @RequestMapping("forceUpdate")
    public @ResponseBody Map<String, Object> forceUpdate() {
        Map<String, Object> json = new HashMap<>();
        //TODO: call service to collect data
        json.put("success", true);
        return json;
    }
    
    @RequestMapping("detail")
    public String detail(ModelMap model) {
        List<InfrastructureWarning> warnings = infrastructureWarningsDao.getWarnings();
        model.addAttribute("warnings", warnings);

        return "infrastructureWarnings/detail.jsp";
    }

}
