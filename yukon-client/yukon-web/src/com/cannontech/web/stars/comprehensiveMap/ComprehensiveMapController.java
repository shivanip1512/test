package com.cannontech.web.stars.comprehensiveMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.mapping.model.NetworkMap;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.ColorCodeBy;
import com.cannontech.web.tools.mapping.model.NmNetworkException;
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.google.common.collect.Lists;


@RequestMapping("/comprehensiveMap/*")
@Controller
@CheckRoleProperty({YukonRoleProperty.INFRASTRUCTURE_ADMIN, 
                    YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE, 
                    YukonRoleProperty.INFRASTRUCTURE_DELETE, 
                    YukonRoleProperty.INFRASTRUCTURE_VIEW})
public class ComprehensiveMapController {
    
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private NmNetworkService nmNetworkService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private MeterDao meterDao;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private static final Logger log = YukonLogManager.getLogger(ComprehensiveMapController.class);
    
    @GetMapping("home")
    public String home(ModelMap model) {
        
        NetworkMapFilter filter = new NetworkMapFilter();
        model.addAttribute("filter", filter);                                                                
                                                                                                              
        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());                                                                                                
        Collections.sort(gateways);
        model.addAttribute("gateways", gateways);
        
        model.addAttribute("colorCodeByOptions", ColorCodeBy.values());
        
        return "comprehensiveMap/map.jsp";
    }
    
    @GetMapping("filter")
    public @ResponseBody Map<String, Object> filter(@ModelAttribute("filter") NetworkMapFilter filter, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        NetworkMap map = null;
        try {
            map = nmNetworkService.getNetworkMap(filter);
        } catch (NmNetworkException | NmCommunicationException e) {
            String errorMsg = accessor.getMessage("yukon.web.modules.operator.comprehensiveMap.nmError");
            log.error(errorMsg, e);
            json.put("errorMsg", errorMsg);
        }
        json.put("map", map);
        return json;
    }
    
    @GetMapping("search")
    public @ResponseBody Map<String, Object> searchForNode(String searchText) {        
        Map<String, Object> json = new HashMap<>();
        //List<Integer> foundPaoIds = new ArrayList<Integer>();
        Set<Integer> foundPaoIds = new HashSet<Integer>();
        //search for a Sensor Serial Number with the provided text
        Integer sensor = rfnDeviceDao.findDeviceBySensorSerialNumber(searchText);
        if (sensor != null) {
            foundPaoIds.add(sensor);
        }
        //search for a Meter Number with the provided text
        try {
            YukonMeter meter = meterDao.getForMeterNumber(searchText);
            if (meter != null) {
                foundPaoIds.add(meter.getDeviceId());
            }
        } catch (NotFoundException e) {
            //continue looking for matches
        }
        //search by partial device name
        List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(searchText, true);
        if (!paos.isEmpty()) {
            List<Integer> paoIds = paos.stream().map(pao -> pao.getPaoIdentifier().getPaoId()).collect(Collectors.toList());
            foundPaoIds.addAll(paoIds);
        }
        
        json.put("paoIds", foundPaoIds);
        return json;
    }
    
}