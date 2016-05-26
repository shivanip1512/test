package com.cannontech.web.dev;

import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.util.TextView;

@Controller
@RequestMapping("/pointInjection/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class PointDataInjectionController {
    
    private static final Logger log = YukonLogManager.getLogger(PointDataInjectionController.class);
    
    @Autowired private PointDao pointDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    
    private @Autowired DatePropertyEditorFactory datePropertyEditorFactory;
    private @Autowired StateGroupDao stateGroupDao;
    
    @RequestMapping("main")
    public void main() { }
    
    @RequestMapping("addRow")
    public void addRow(int pointId, boolean forceArchive, ModelMap model) {
        
        PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(pointId);
        model.addAttribute("paoPointIdentifier", paoPointIdentifier);
        
        PointValueQualityHolder pointValue = asyncDynamicDataSource.getPointValue(pointId);
        model.addAttribute("pointValue", pointValue);
        
        String pointName = pointDao.getPointName(pointId);
        model.addAttribute("pointName", pointName);
        
        model.addAttribute("pointId", pointId);
        boolean status = paoPointIdentifier.getPointIdentifier().getPointType().isStatus();
        
        model.addAttribute("status", status);
        if (status) {
            LitePoint litePoint = pointDao.getLitePoint(pointId);
            int stateGroupID = litePoint.getStateGroupID();
            List<LiteState> liteStates = stateGroupDao.getLiteStates(stateGroupID);
            
            model.addAttribute("states", liteStates);
            model.addAttribute("statePointValue", (int)pointValue.getValue());
        } else {
            model.addAttribute("decimalPointValue", String.format("%.4f", pointValue.getValue()));
        }
        model.addAttribute("qualities", PointQuality.values());
        model.addAttribute("forceArchive", forceArchive);
    }
    
    @RequestMapping("sendData")
    public View sendData(
            int pointId, 
            @RequestParam("date")LocalDate date, 
            @RequestParam("time")LocalTime time, 
            PointQuality quality, 
            double value, 
            String forceArchive,
            YukonUserContext userContext) {
        
        DateTime dateTime = date.toDateTime(time, userContext.getJodaTimeZone());
        
        PointData pointData = new PointData();
        pointData.setId(pointId);
        pointData.setTime(dateTime.toDate());
        pointData.setPointQuality(quality);
        pointData.setValue(value);
        pointData.setType(pointDao.getLitePoint(pointId).getPointType());
        
        if (forceArchive != null && forceArchive.equals("on")) {
        	pointData.setTagsPointMustArchive(true);
        }
        
        asyncDynamicDataSource.putValue(pointData);
        log.info("point data send from injector: " + pointData);
        
        return new TextView();
    }
    
    @InitBinder
    public void setupBinder(WebDataBinder webDataBinder, YukonUserContext userContext) {
        datePropertyEditorFactory.setupLocalDatePropertyEditor(webDataBinder, userContext, BlankMode.CURRENT);
        datePropertyEditorFactory.setupLocalTimePropertyEditor(webDataBinder, userContext, BlankMode.CURRENT);
    }
    
}
