package com.cannontech.web.picker.databaseMigration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.databaseMigration.model.DatabaseMigrationContainer;
import com.cannontech.common.databaseMigration.model.ExportTypeEnum;
import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.multi.MultiPickerController;
import com.cannontech.web.util.JsonView;

public class DatabaseMigrationPickerController extends MultiPickerController {

    private DatabaseMigrationService databaseMigrationService;
    
    @Override
    public ModelAndView showAll(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView(new JsonView());
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        String exportTypeString = getConfigurationName(request);
        ExportTypeEnum exportType = ExportTypeEnum.valueOf(exportTypeString);
        int startIndex = getStartParameter(request);
        int count = getCountParameter(request);

        SearchResult<DatabaseMigrationContainer> searchResult = 
            databaseMigrationService.search(exportType, "", startIndex, count, userContext);
        processObjectHitList(mav, exportType, searchResult, userContext);
        mav.addObject("showAll", false);
        
        return mav;
    }
    
    @Override
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView(new JsonView());
        String queryString = ServletRequestUtils.getStringParameter(request, "ss", "");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        String exportTypeString = getConfigurationName(request);
        ExportTypeEnum exportType = ExportTypeEnum.valueOf(exportTypeString);
        
        int startIndex = getStartParameter(request);
        int count = getCountParameter(request);
        
        SearchResult<DatabaseMigrationContainer> searchResult = 
            databaseMigrationService.search(exportType, queryString, startIndex, count, userContext);
        processObjectHitList(mav, exportType, searchResult, userContext);
        mav.addObject("showAll", false);
        
        return mav;
    }
    
    private void processObjectHitList(ModelAndView mav, ExportTypeEnum exportType, 
                                        SearchResult<DatabaseMigrationContainer> hits, 
                                        YukonUserContext userContext) {
        
        mav.addObject("hitList", hits.getResultList());
        mav.addObject("hitCount", hits.getHitCount());
        mav.addObject("resultCount", hits.getResultCount());
        mav.addObject("startIndex", hits.getStartIndex());
        mav.addObject("endIndex", hits.getEndIndex());
        mav.addObject("previousIndex", hits.getPreviousStartIndex());
        mav.addObject("nextIndex", hits.getEndIndex());
    }
    
    private String getConfigurationName(HttpServletRequest request){
        try {
            return ServletRequestUtils.getRequiredStringParameter(request, "configurationName");
        } catch (ServletRequestBindingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Autowired
    public void setDatabaseMigrationService(DatabaseMigrationService databaseMigrationService) {
        this.databaseMigrationService = databaseMigrationService;
    }
    
}
