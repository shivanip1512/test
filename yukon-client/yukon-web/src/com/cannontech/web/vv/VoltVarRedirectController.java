package com.cannontech.web.vv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.util.CtiUtilities;

@Controller
public class VoltVarRedirectController {

    @Autowired private CapControlCache cache;
    @Autowired private CapbankDao capbankDao;

    @RequestMapping("area/{id}")
    public String area(ModelMap model, @PathVariable int id) {

        model.addAttribute("bc_areaId", id);

        return "redirect:/capcontrol/tier/substations";
    }

    @RequestMapping("substation/{id}")
    public String substation(ModelMap model, @PathVariable int id) {
        return editorOrSubstationDetail(model, id);
    }

    @RequestMapping("bus/{id}")
    public String bus(ModelMap model, @PathVariable int id) {
        return editorOrSubstationDetail(model, id);
    }

    @RequestMapping("feeder/{id}")
    public String feeder(ModelMap model, @PathVariable int id) {
        return editorOrSubstationDetail(model, id);
    }

    @RequestMapping("bank/{id}")
    public String bank(ModelMap model, @PathVariable int id) {
        return editorOrSubstationDetail(model, id);
    }

    @RequestMapping("cbc/{id}")
    public String cbc(ModelMap model, @PathVariable int id) {

        PaoIdentifier bank = capbankDao.findCapBankByCbc(id);

        if (bank == null) return editorPage(id);

        return bank(model, bank.getPaoId());
    }

    private String editorOrSubstationDetail(ModelMap model, int id) {

        int areaId = cache.getParentAreaId(id);

        if (areaId <= CtiUtilities.NONE_ZERO_ID) {
            return editorPage(id);
        }

        int substationId = cache.getParentSubStationId(id);

        model.addAttribute("substationId", substationId);
        return "redirect:/capcontrol/tier/feeders";
    }


    private String editorPage(int id) {
        return "redirect:/editor/cbcBase.jsf?type=2&itemid="+ id;
    }
}