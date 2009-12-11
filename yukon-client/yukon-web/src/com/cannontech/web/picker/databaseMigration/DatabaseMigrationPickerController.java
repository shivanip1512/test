package com.cannontech.web.picker.databaseMigration;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.multi.MultiPickerController;
import com.cannontech.web.util.JsonView;
import com.google.common.collect.Lists;

public class DatabaseMigrationPickerController extends MultiPickerController {

    private DatabaseMigrationService databaseMigrationService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private TemplateProcessorFactory templateProcessorFactory;
    
    public DatabaseMigrationPickerController() {
        super();
    }
    
    public ModelAndView showAll(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView(new JsonView());
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String configurationName = getConfigurationName(request);
        int start = getStartParameter(request);
        int count = getCountParameter(request);

        SearchResult<DatabaseMigrationPicker> hits = getHits(configurationName, start, count);
        
        processObjectHitList(mav, configurationName, hits, userContext);
        mav.addObject("showAll", true);
        
        return mav;
    }
    
    private SearchResult<DatabaseMigrationPicker> getHits(String configurationName, int start, int count) {
        
        List<DatabaseMigrationPicker> allHits = getAllObjects(configurationName);
        int endIndex = (start + count) > allHits.size() ? allHits.size() : (start+count);
        
        List<DatabaseMigrationPicker> subList = allHits.subList(start, endIndex);
        
        SearchResult<DatabaseMigrationPicker> hits;
        hits = new SearchResult<DatabaseMigrationPicker>();
        hits.setStartIndex(start);
        hits.setEndIndex(endIndex);
        hits.setHitCount(allHits.size());
        hits.setResultList(subList);
        
        return hits;
    }
    
    private List<DatabaseMigrationPicker> getAllObjects(String configurationName) {
        
        List<Map<String, Object>> configurationItems = databaseMigrationService.getConfigurationItems(configurationName);
        List<DatabaseMigrationPicker> resultsList = Lists.newArrayList();
        
        for (Map<String, Object> map : configurationItems) {
            Object[] keySet = map.keySet().toArray();
            Object key = keySet[0];
            Integer databaseMigrationId = new Integer(map.get(key).toString());
            map.remove(key);
            resultsList.add(new DatabaseMigrationPicker(databaseMigrationId, map));
        }

        return resultsList;
    }
    
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ModelAndView mav = new ModelAndView(new JsonView());
        String queryString = ServletRequestUtils.getStringParameter(request, "ss", "");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String configurationName = getConfigurationName(request);
        int start = getStartParameter(request);
        int count = getCountParameter(request);
        
        boolean blank = StringUtils.isBlank(queryString);
        SearchResult<DatabaseMigrationPicker> hits;
        if (blank) {
            hits = getHits(configurationName, start, count);
        } else {
            hits = getHits(configurationName, start, count);
        }
        processObjectHitList(mav, configurationName, hits, userContext);
        mav.addObject("showAll", false);
        
        return mav;
    }
    
    private List<DatabaseMigrationContainer> convertThing(String configurationName, List<DatabaseMigrationPicker> input, YukonUserContext userContext) {

        FormattingTemplateProcessor formattingTemplateProcessor = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
        String keyName = com.cannontech.common.util.StringUtils.toCamelCase(configurationName);
        List<DatabaseMigrationContainer> result = Lists.newArrayList();
        
        for (DatabaseMigrationPicker databaseMigrationPicker : input) {
            
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String message = messageSourceAccessor.getMessage("yukon.web.modules.support.databaseMigrationTemplate."+keyName);
            
            String databaseMigrationDisplay = 
            formattingTemplateProcessor.process(message, 
                                                databaseMigrationPicker.getIdentifierColumnValueMap());
            
            DatabaseMigrationContainer databaseMigrationContainer =
                new DatabaseMigrationContainer(databaseMigrationPicker.getDatabaseMigrationId(),
                                               databaseMigrationDisplay);

            result.add(databaseMigrationContainer);
        }
        
        return result;
    }
    
    protected void processObjectHitList(ModelAndView mav, String configurationName, SearchResult<DatabaseMigrationPicker> hits, YukonUserContext userContext) {
        
        List<DatabaseMigrationPicker> hitList = hits.getResultList();
        mav.addObject("hitList", convertThing(configurationName, hitList, userContext));
        mav.addObject("hitCount", hits.getHitCount());
        mav.addObject("resultCount", hits.getResultCount());
        mav.addObject("startIndex", hits.getStartIndex());
        mav.addObject("endIndex", hits.getEndIndex());
        mav.addObject("previousIndex", hits.getPreviousStartIndex());
        mav.addObject("nextIndex", hits.getEndIndex());
    }
    
    protected String getConfigurationName(HttpServletRequest request){
        try {
            return ServletRequestUtils.getRequiredStringParameter(request, "configurationName");
        } catch (ServletRequestBindingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    @Autowired
    public void setDatabaseMigrationService(DatabaseMigrationService databaseMigrationService) {
        this.databaseMigrationService = databaseMigrationService;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setTemplateProcessorFactory(TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }
}
