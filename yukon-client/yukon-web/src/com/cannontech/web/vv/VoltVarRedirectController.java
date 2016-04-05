package com.cannontech.web.vv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.mbean.ServerDatabaseCache;

@Controller
public class VoltVarRedirectController {

    @Autowired private ServerDatabaseCache dbCache;
    @Autowired private CapControlCache cache;

    @RequestMapping("area/{id}")
    public String area(@PathVariable int id) {
        return "redirect:/capcontrol/areas/" + id;
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
        return editorOrSubstationDetail(model, id);
    }

    private String editorOrSubstationDetail(ModelMap model, int id) {

        int areaId = cache.getParentAreaId(id);
        
        if (areaId <= CtiUtilities.NONE_ZERO_ID) {
            return editorPage(id);
        } else {
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(areaId);
            if (pao.getPaoType() == PaoType.CAP_CONTROL_SPECIAL_AREA) {
                // When a substation is assigned to a special area but 
                // not a normal area...the CapControlCache#getParentAreaId
                // returns the special area id.
                return editorPage(id);
            }
        }
        
        int substationId = cache.getParentSubStationId(id);
        
        return "redirect:/capcontrol/substations/" + substationId;
    }


    private String editorPage(int id) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        if (pao.getPaoType().isCbc()) {
            return "redirect:/capcontrol/cbc/" + id;
        } else if (pao.getPaoType() == PaoType.CAP_CONTROL_FEEDER){
            return "redirect:/capcontrol/feeders/" + id;
        } else if (pao.getPaoType() == PaoType.CAP_CONTROL_SUBBUS){
            return "redirect:/capcontrol/buses/" + id;
        } else if (pao.getPaoType() == PaoType.CAP_CONTROL_AREA || pao.getPaoType() == PaoType.CAP_CONTROL_SPECIAL_AREA){
            return "redirect:/capcontrol/areas/" + id;
        } else if (pao.getPaoType() == PaoType.CAP_CONTROL_SUBSTATION){
            return "redirect:/capcontrol/substations/" + id;
        } else if (pao.getPaoType() == PaoType.CAPBANK){
            return "redirect:/capcontrol/capbanks/" + id;
        }

        return "redirect:/capcontrol/tier/areas";
    }
}