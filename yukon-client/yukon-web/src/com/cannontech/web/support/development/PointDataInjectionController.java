package com.cannontech.web.support.development;

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
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckDevelopmentMode;
import com.cannontech.web.util.TextView;

@Controller
@RequestMapping("/development/pointInjection/*")
@CheckDevelopmentMode
public class PointDataInjectionController {
    private static final Logger log = YukonLogManager.getLogger(PointDataInjectionController.class);
    
    private PointDao pointDao;
    private DynamicDataSource dynamicDataSource;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private StateDao stateDao;
    
    @RequestMapping("main")
    public void main() {
    }
    
    @RequestMapping("addRow")
    public void addRow(int pointId, String forceArchive, ModelMap model) {
        PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(pointId);
        model.addAttribute("paoPointIdentifier", paoPointIdentifier);
        PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
        model.addAttribute("pointValue", pointValue);
        String pointName = pointDao.getPointName(pointId);
        model.addAttribute("pointName", pointName);
        model.addAttribute("pointId", pointId);
        boolean status = paoPointIdentifier.getPointIdentifier().getPointType().isStatus();
        model.addAttribute("status", status);
        if (status) {
            LitePoint litePoint = pointDao.getLitePoint(pointId);
            int stateGroupID = litePoint.getStateGroupID();
            LiteState[] liteStates = stateDao.getLiteStates(stateGroupID);
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
        
        if (forceArchive != null && forceArchive.equals("on")) {
        	pointData.setTagsPointMustArchive(true);
        }
        
        dynamicDataSource.putValue(pointData);
        log.info("point data send from injector: " + pointData);
        return new TextView();
    }
    
    @InitBinder
    public void setupBinder(WebDataBinder webDataBinder, YukonUserContext userContext) {
        datePropertyEditorFactory.setupLocalDatePropertyEditor(webDataBinder, userContext, BlankMode.CURRENT);
        datePropertyEditorFactory.setupLocalTimePropertyEditor(webDataBinder, userContext, BlankMode.CURRENT);
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
    
    @Autowired
    public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
    
    @Autowired
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
}
