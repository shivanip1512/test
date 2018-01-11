package com.cannontech.web.stars.rtu;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.common.rtu.service.RtuDnpService;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.TimeIntervals;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableMap;

@Controller
public class RtuController {
    
    @Autowired private RtuDnpService rtuDnpService;
    @Autowired private PointDao pointDao;
    @Autowired private AttributeService attributeService;
    @Autowired private IDatabaseCache cache;
    
    private static final Map<BuiltInAttribute,String> formatMappings = ImmutableMap.<BuiltInAttribute,String>builder()
            .put(BuiltInAttribute.FIRMWARE_VERSION, "{rawValue|firmwareVersion}")
            .put(BuiltInAttribute.IP_ADDRESS, "{rawValue|ipAddress}")
            .put(BuiltInAttribute.NEUTRAL_CURRENT_SENSOR, "{rawValue|neutralCurrent}")
            .put(BuiltInAttribute.SERIAL_NUMBER, "{rawValue|long}")
            .put(BuiltInAttribute.UDP_PORT, "{rawValue|long}")
            .put(BuiltInAttribute.LAST_CONTROL_REASON, "{rawValue|lastControlReason}")
            .put(BuiltInAttribute.IGNORED_CONTROL_REASON, "{rawValue|ignoredControlReason}")
            .build();
    
    @RequestMapping(value = "rtu/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id) {
        model.addAttribute("mode", PageEditMode.VIEW);
        model.addAttribute("timeIntervals", TimeIntervals.getCapControlIntervals());
        model.addAttribute("scanGroups", CapControlCBC.ScanGroup.values());
        RtuDnp rtu = rtuDnpService.getRtuDnp(id);
        model.addAttribute("rtu", rtu);
        getPointsForModel(id, model);
        return "/rtu/rtuDetail.jsp";
    }
    
    @RequestMapping(value = "rtu/child/{id}/points", method = RequestMethod.GET)
    public String getPoints(ModelMap model, @PathVariable int id) {
        getPointsForModel(id, model);
        return "../capcontrol/pointsTable.jsp";
    }
    
    private void getPointsForModel(int paoId, ModelMap model) {
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(paoId);
        //check for special formats
        for(List<PointInfo> pointList : points.values()){
            for(PointInfo point : pointList){
                LitePoint litePoint = pointDao.getLitePoint(point.getPointId());
                PointIdentifier pid = new PointIdentifier(point.getType(), litePoint.getPointOffset());
                PaoTypePointIdentifier pptId = PaoTypePointIdentifier.of(pao.getPaoType(), pid);
                //This set should contain 0 items if there is not a special format, or 1 if there is
                Set<BuiltInAttribute> attributes = attributeService.findAttributesForPoint(pptId, formatMappings.keySet());
                for (BuiltInAttribute attribute: attributes) {
                    if (formatMappings.get(attribute) != null) {
                        point.setFormat(formatMappings.get(attribute));
                    }
                }
            }
        }
        model.addAttribute("points", points);
    }
    
}