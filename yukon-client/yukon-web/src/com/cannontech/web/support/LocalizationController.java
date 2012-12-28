package com.cannontech.web.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/localization/*")
public class LocalizationController {
    @Autowired private LocalizationService localizationService;

    @RequestMapping
    public String view(ModelMap model, HttpServletResponse response, @ModelAttribute("localizationBackingBean") LocalizationBackingBean localizationBackingBean, BindingResult bindingResult, FlashScope flashScope) throws IOException {
        if(localizationBackingBean.getTask() == null){
            localizationBackingBean.setTask("");
        }
        switch(localizationBackingBean.getTask()){
        
        case "search":
            try {
                List<String> searchResults = localizationService.search(localizationBackingBean);
                model.addAttribute("query", localizationBackingBean.getQuery());
                model.addAttribute("result", sanitizeHtml(searchResults));
            } catch (FileNotFoundException e) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.support.localization.errorOpen", ""));
            } catch (InvalidPropertiesFormatException e) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.support.localization.errorProcess", ""));
            }
            break;
            
        case "compare":
            try {
                List<LocalizationPair> comparedLocalizationPairs = localizationService.compare(localizationBackingBean);
                if(localizationBackingBean.getCompareAction().equals("download")){
                    respondWithXmlFile(response, localizationService.getXML(comparedLocalizationPairs));
                    return null;
                }
                comparedLocalizationPairs = LocalizationPair.sanitizeHtmlPairs(comparedLocalizationPairs);
                model.addAttribute("compareSets", true);
                model.addAttribute("compareResults", comparedLocalizationPairs);
            } catch (FileNotFoundException e) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.support.localization.errorOpen", ""));
            } catch (InvalidPropertiesFormatException e) {
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.support.localization.errorProcess", ""));
            }
            break;
            
        case "dump":
            respondWithXmlFile(response, localizationService.getDumpXml());
            return null;
            
        default:
        }
        localizationBackingBean.setDefaultsIfNotSet();
        
        model.addAttribute("localizationBackingBean", localizationBackingBean);
        model.addAttribute("installedThemes", localizationService.getThemeFilesList());
        
        return "localization/main.jsp";
    }
    
    private List<String> sanitizeHtml(List<String> unsanitizedStrings){
        List<String> sanitizedStrings = Lists.newArrayList();
        for(String unsanitizedString : unsanitizedStrings){
            sanitizedStrings.add(StringEscapeUtils.escapeHtml(unsanitizedString));
        }
        return sanitizedStrings;
    }
    
    private void respondWithXmlFile(HttpServletResponse response, InputStream xmlStream) throws IOException{
        response.setContentType("text/xml");
        response.setHeader("Content-Disposition", "attachment; filename=\"localization.xml\"");
        FileCopyUtils.copy(xmlStream, response.getOutputStream());
    }
}