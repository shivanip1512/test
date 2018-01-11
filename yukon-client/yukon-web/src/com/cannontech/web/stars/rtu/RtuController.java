package com.cannontech.web.stars.rtu;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.web.PageEditMode;

@Controller
public class RtuController {
    
    @RequestMapping(value = "rtu/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id) {
        model.addAttribute("mode", PageEditMode.VIEW);
        RtuDnp rtu = createMockedRtuDnp(id);
        model.addAttribute("rtu", rtu);
        return "/rtu/rtuDetail.jsp";
    }
    
    private RtuDnp createMockedRtuDnp(int id) {
        RtuDnp rtu = new RtuDnp();
        rtu.setId(id);
        rtu.setName("RTU-1122");
        rtu.setPaoType(PaoType.RTU_DNP);
        return rtu;
    }

}