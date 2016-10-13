package com.cannontech.web.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.common.collect.Multimap;

@Controller
@CheckCparm(MasterConfigBoolean.RF_DATA_STREAMING_ENABLED)
public class DataStreamingSupportController {

    private final static String baseKey = "yukon.web.modules.support.";
    @Autowired
    private DataStreamingAttributeHelper dsHelper;

    @RequestMapping("/dataStreamingSupport")
    public String view(HttpServletRequest request, ModelMap model, YukonUserContext context) {

        Multimap<PaoType, BuiltInAttribute> devicesMap = dsHelper.getAllTypesAndAttributes();
        ArrayList<PaoType> deviceList = new ArrayList<PaoType>();
        deviceList.addAll(dsHelper.getAllSupportedPaoTypes());
        Comparator<PaoType> paoComparator = new Comparator<PaoType>() {
            @Override
            public int compare(PaoType pt1, PaoType pt2){
                return pt1.getDbString().compareTo(pt2.getDbString());
            }
        };
        Collections.sort(deviceList, paoComparator);
        ArrayList<BuiltInAttribute> biaList = new ArrayList<BuiltInAttribute>();
        biaList.addAll(dsHelper.getAllSupportedAttributes());
        Comparator<BuiltInAttribute> biaComparator = new Comparator<BuiltInAttribute>() {
            @Override
            public int compare(BuiltInAttribute bia1, BuiltInAttribute bia2) {
                return bia1.getDescription().compareTo(bia2.getDescription());
            }
        };
        Collections.sort(biaList, biaComparator);
        model.addAttribute("devices", deviceList);
        model.addAttribute("attributes", biaList);
        model.addAttribute("dataStreamingDevices", devicesMap.asMap());
        return "dataStreaming/dataStreamingSupport.jsp";
    }
}
