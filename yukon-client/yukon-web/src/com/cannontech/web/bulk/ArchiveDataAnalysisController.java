package com.cannontech.web.bulk;

import java.beans.PropertyEditor;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateMidnight;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.callbackResult.ArchiveDataAnalysisCallbackResult;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.ArchiveDataAnalysisBackingBean;
import com.cannontech.common.bulk.service.ArchiveDataAnalysisService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.input.PeriodPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableSet;

@CheckRoleProperty(YukonRoleProperty.ARCHIVED_DATA_ANALYSIS)
@Controller
@RequestMapping("archiveDataAnalysis/home/*")
public class ArchiveDataAnalysisController {

    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private ArchiveDataAnalysisService archiveDataAnalysisService;
    @Autowired private ArchiveDataAnalysisDao archiveDataAnalysisDao;
    @Autowired private AttributeService attributeService;
    @Resource(name="recentResultsCache") private RecentResultsCache<ArchiveDataAnalysisCallbackResult> recentResultsCache;
    private Set<Period> intervalPeriods;

    @ModelAttribute("groupedAttributes")
    public Map<AttributeGroup, List<BuiltInAttribute>> getGroupedAttributes(YukonUserContext userContext) {
        Set<Attribute> advancedReadableAttributes = ImmutableSet.<Attribute>copyOf(attributeService.getAdvancedReadableAttributes());
        Map<AttributeGroup, List<BuiltInAttribute>> groupedAttributes = attributeService.
                getGroupedAttributeMapFromCollection(advancedReadableAttributes, userContext);
        return  groupedAttributes;
    }
    
    {
        intervalPeriods = new LinkedHashSet<Period>();
        intervalPeriods.add(Period.minutes(5));
        intervalPeriods.add(Period.minutes(10));
        intervalPeriods.add(Period.minutes(15));
        intervalPeriods.add(Period.minutes(30));
        intervalPeriods.add(Period.hours(1));
        intervalPeriods.add(Period.days(1));
    }
    
    @RequestMapping("setup")
    public String setup(ModelMap model, HttpServletRequest request, @ModelAttribute("backingBean") ArchiveDataAnalysisBackingBean backingBean) throws ServletException {
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        model.addAllAttributes(deviceCollection.getCollectionParameters());
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("intervalPeriods", intervalPeriods);
        
        Instant stopDateInitialValue = new DateMidnight().toInstant();
        Instant startDateInitialValue = stopDateInitialValue.minus(Duration.standardDays(1));
        
        model.addAttribute("startDateInitialValue", startDateInitialValue);
        model.addAttribute("stopDateInitialValue", stopDateInitialValue);
        return "archiveDataAnalysis/home.jsp";
    }
    
    @RequestMapping("analyze")
    public String analyze(ModelMap model, HttpServletRequest request, @ModelAttribute("backingBean") ArchiveDataAnalysisBackingBean backingBean) throws ServletException {
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        backingBean.setDeviceCollection(deviceCollection);
        
        int analysisId = archiveDataAnalysisService.createAnalysis(backingBean);
        String resultsId = archiveDataAnalysisService.startAnalysis(backingBean, analysisId);
        
        model.addAttribute("analysisId", analysisId);
        model.addAttribute("resultsId", resultsId);
        
        return "redirect:processing";
    }
    
    @RequestMapping("reanalyze")
    public String reanalyze(ModelMap model, HttpServletRequest request, int oldAnalysisId) {
        int newAnalysisId = archiveDataAnalysisService.createAnalysis(oldAnalysisId);
        String resultsId = archiveDataAnalysisService.startAnalysis(oldAnalysisId, newAnalysisId);
        
        model.addAttribute("analysisId", newAnalysisId);
        model.addAttribute("resultsId", resultsId);
        
        return "redirect:processing";
    }
    
    @RequestMapping("processing")
    public String processing(ModelMap model, HttpServletRequest request, int analysisId, String resultsId) throws ServletException {
        Analysis analysis = archiveDataAnalysisDao.getAnalysisById(analysisId);
        ArchiveDataAnalysisCallbackResult callbackResult = recentResultsCache.getResult(resultsId);
        DeviceCollection deviceCollection = callbackResult.getOriginalDeviceCollection();
        
        model.addAllAttributes(deviceCollection.getCollectionParameters());
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("analysis", analysis);
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
        
        PropertyEditor periodEditor = new PeriodPropertyEditor();
        binder.registerCustomEditor(Period.class, periodEditor);
    }
    
}
