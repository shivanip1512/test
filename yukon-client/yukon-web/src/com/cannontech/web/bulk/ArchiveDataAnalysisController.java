package com.cannontech.web.bulk;

import java.beans.PropertyEditor;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateMidnight;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.callbackResult.ArchiveDataAnalysisCallbackResult;
import com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.model.ArchiveDataAnalysisBackingBean;
import com.cannontech.common.bulk.service.ArchiveDataAnalysisService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.model.DeviceCollectionFactory;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.EnumPropertyEditor;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("archiveDataAnalysis/home/*")
public class ArchiveDataAnalysisController {
    private Set<Attribute> attributes;
    private Set<Duration> intervalDurations;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private DeviceCollectionFactory deviceCollectionFactory;
    private ArchiveDataAnalysisService archiveDataAnalysisService;
    private RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache;
    
    {
        attributes = Sets.newLinkedHashSet();
        for (BuiltInAttribute attribute : BuiltInAttribute.values()) {
            attributes.add(attribute);
        }
        
        intervalDurations = new LinkedHashSet<Duration>();
        intervalDurations.add(Duration.standardMinutes(5));
        intervalDurations.add(Duration.standardMinutes(10));
        intervalDurations.add(Duration.standardMinutes(15));
        intervalDurations.add(Duration.standardMinutes(30));
        intervalDurations.add(Duration.standardHours(1));
    }
    
    @RequestMapping
    public String setup(ModelMap model, HttpServletRequest request, @ModelAttribute("backingBean") ArchiveDataAnalysisBackingBean backingBean) throws ServletException {
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        model.addAllAttributes(deviceCollection.getCollectionParameters());
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("attributes", attributes);
        model.addAttribute("intervalDurations", intervalDurations);
        
        Instant stopDateInitialValue = new DateMidnight().toInstant();
        Instant startDateInitialValue = stopDateInitialValue.minus(Duration.standardDays(1));
        
        model.addAttribute("startDateInitialValue", startDateInitialValue);
        model.addAttribute("stopDateInitialValue", stopDateInitialValue);
        
        return "archiveDataAnalysis/home.jsp";
    }
    
    @RequestMapping
    public String analyze(ModelMap model, HttpServletRequest request, @ModelAttribute("backingBean") ArchiveDataAnalysisBackingBean backingBean) throws ServletException {
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        backingBean.setDeviceCollection(deviceCollection);
        
        model.addAttribute("dateTimeRangeForDisplay", backingBean.getDateRange());
        
        int analysisId = archiveDataAnalysisService.createAnalysis(backingBean);
        String resultsId = archiveDataAnalysisService.startAnalysis(backingBean, analysisId);
        
        ArchiveDataAnalysisCallbackResult callbackResult = (ArchiveDataAnalysisCallbackResult) recentResultsCache.getResult(resultsId);
        
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("analysisId", analysisId);
        model.addAttribute("resultsId", resultsId);
        model.addAttribute("callbackResult", callbackResult);
        
        return "archiveDataAnalysis/analyze.jsp";
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        PropertyEditor fullDateTimeEditor = datePropertyEditorFactory.getPropertyEditor(DateFormatEnum.DATEHM, userContext);
        binder.registerCustomEditor(Date.class, fullDateTimeEditor);
        
        EnumPropertyEditor<BuiltInAttribute> attributeEditor = new EnumPropertyEditor<BuiltInAttribute>(BuiltInAttribute.class);
        binder.registerCustomEditor(BuiltInAttribute.class, attributeEditor);
    }
    
    @Resource(name="recentResultsCache")
    public void setRecentResultsCache(RecentResultsCache<BackgroundProcessResultHolder> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }
    
    @Autowired
    public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
    
    @Autowired
    public void setDeviceCollectionFactory(DeviceCollectionFactory deviceCollectionFactory) {
        this.deviceCollectionFactory = deviceCollectionFactory;
    }
    
    @Autowired
    public void setArchiveDataAnalysisService(ArchiveDataAnalysisService archiveDataAnalysisService) {
        this.archiveDataAnalysisService = archiveDataAnalysisService;
    }
}
