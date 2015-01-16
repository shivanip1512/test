package com.cannontech.web.capcontrol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.model.CapbankAdditional;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.web.capcontrol.models.BusDetails;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/addInfo/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class AdditionalInfoController {

    @Autowired private CapbankDao capbankDao;
    @Autowired private CapControlCache cache;
    @Autowired private CapControlWebUtilsService capControlWebUtilsService;
    @Autowired private PointDao pointDao;

    @RequestMapping("bank")
    public String bank(ModelMap model, int bankId) {
        CapbankAdditional capBankAdd = capbankDao.getCapbankAdditional(bankId);
        model.addAttribute("capBankAdd", capBankAdd);
        return "capBankAddInfoPopup.jsp";
    }

    @RequestMapping("bus")
    public String bus(ModelMap model, int busId) {
        SubBus bus = cache.getSubBus(busId);
        BusDetails details = busDetailsFor(bus);

        model.addAttribute("bus", details);
        return "busInfo.jsp";
    }

    private BusDetails busDetailsFor(SubBus bus) {
        BusDetails details = new BusDetails(bus);
        if(bus.getCurrentVarLoadPointID() != 0) {
            LitePoint point = pointDao.getLitePoint(bus.getCurrentVarLoadPointID());
            details.setVarPoint(point);
        }
        if(bus.getCurrentVoltLoadPointID() != 0) {
            LitePoint point = pointDao.getLitePoint(bus.getCurrentVoltLoadPointID());
            details.setVoltPoint(point);
        }
        if(bus.getCurrentWattLoadPointID() != 0) {
            LitePoint point = pointDao.getLitePoint(bus.getCurrentWattLoadPointID());
            details.setWattPoint(point);
        }
        return details;
    }
}