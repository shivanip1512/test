package com.cannontech.web.admin.substations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.model.Substation;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.multispeak.MspHandler;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP)
@RequestMapping("/substations/routemapping/multispeak/*")
public class MspSubstationsController {

    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakDao multispeakDao;
    @Autowired MspHandler mspHandler;
    @Autowired private SubstationDao substationDao;

    @RequestMapping("choose")
    public ModelAndView choose(HttpServletRequest request, HttpServletResponse response)  {
        
        ModelAndView mav = new ModelAndView("substations/mspSubstations.jsp");
        
        // currentSubstationNames
        List<Substation> currentSubstations = substationDao.getAll();
        Set<String> currentSubstationNames = new HashSet<String>();
        for (Substation currentSubstation : currentSubstations) {
            currentSubstationNames.add(currentSubstation.getName());
        }
        
        // mspSubstationNames
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
        List<String> mspSubstationNames = mspHandler.getMspSubstationName(mspVendor);
        
        // make MspSubstation list. Don't show those that already exists (case insensitive)
        List<MspSubstation> mspSubstations = new ArrayList<MspSubstation>();
        for (String mspSubstationName : mspSubstationNames) {
            
            boolean show = true;
            for (String currentSubstationName : currentSubstationNames) {
                if (mspSubstationName.equalsIgnoreCase(currentSubstationName)) {
                    show = false;
                    break;
                }
            }
            
            MspSubstation mspSubstation = new MspSubstation(mspSubstationName, show);
            if (!(PaoUtils.isValidPaoName(mspSubstationName))) {
                mspHandler.invalidSubstationName(mspVendor, mspSubstationName);
                continue;
            }
            mspSubstations.add(mspSubstation);
        }
        
        Collections.sort(mspSubstations);
        mav.addObject("mspSubstations", mspSubstations);
        return mav;
    }
    
    @RequestMapping("add")
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
        
        ModelAndView mav = new ModelAndView("redirect:/admin/substations/routeMapping/view");

        Map<String, String> substatioNamesMap = ServletUtil.getStringParameters(request, "substationName_");

        for (String mspSubstationName : substatioNamesMap.values()) {

            Substation substation = new Substation();
            substation.setName(mspSubstationName);

            substationDao.add(substation);
        }

        return mav;
    }
    
    public class MspSubstation implements Comparable<MspSubstation> {
        
        private String name;
        private boolean show;
        
        public MspSubstation(String name, boolean show) {
            this.name = name;
            this.show = show;
        }
        
        public String getName() {
            return name;
        }
        public boolean isShow() {
            return show;
        }
        
        @Override
        public int compareTo(MspSubstation o) {
            return this.name.compareTo(o.getName());
        }
    }
}
