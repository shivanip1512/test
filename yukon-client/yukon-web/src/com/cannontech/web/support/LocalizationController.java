package com.cannontech.web.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/localization/*")
public class LocalizationController {
    @Autowired private LocalizationService localizationService;

    /**
     * Only Controller-type method, since Localization is currently only 1 page.
     * Based on the backing bean's task, the page will
     * perform a search, a comparison, or a dump of default key-value pairs.
     * The results of these will be output to the page or downloaded,
     * depending on the selected action.
     */
    @RequestMapping("view")
    public String view(ModelMap model, HttpServletResponse response,
            @ModelAttribute("localizationBackingBean") LocalizationBackingBean localizationBackingBean,
            FlashScope flashScope) throws IOException {
        if(localizationBackingBean.getTask() == null){
            localizationBackingBean.setTask("");
        }
        switch(localizationBackingBean.getTaskEnum()){
        case SEARCH:
            search(model, localizationBackingBean, flashScope);
            break;
            
        case COMPARE:
            compare(model, localizationBackingBean, flashScope, response);
            if("DOWNLOAD".equals(localizationBackingBean.getCompareAction())){
                return null;
            }
    
            break;

        case DUMP:
            respondWithXmlFile(response, localizationService.getDumpXml());
            return null;
        case NOTHING:
            break;
        }

        model.addAttribute("localizationBackingBean", localizationBackingBean);
        model.addAttribute("installedThemes", localizationService.getThemeFilesList());

        return "localization/main.jsp";
    }
    
    /**
     * Searches the Localization specified in the backing bean for the query.
     * puts query and result in the model for later processing, and puts errors in the flash scope
     */
    private void search(ModelMap model, LocalizationBackingBean localizationBackingBean, FlashScope flashScope){
        try {
            List<String> searchResults = localizationService.search(localizationBackingBean);
            model.addAttribute("query", localizationBackingBean.getQuery());
            model.addAttribute("result", sanitizeHtml(searchResults));
        } catch (FileNotFoundException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.support.localization.errorOpen", ""));
        } catch (InvalidPropertiesFormatException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.support.localization.errorProcess", ""));
        }
    }
    
    /**
     * Compares 2 localizations specified in the backing bean.
     * sets up the response with a download if that was selected
     */
    private void compare(ModelMap model, LocalizationBackingBean localizationBackingBean, FlashScope flashScope, HttpServletResponse response) throws IOException{
        try {
            List<LocalizationPair> comparedLocalizationPairs = localizationService.compare(localizationBackingBean);
            if("DOWNLOAD".equals(localizationBackingBean.getCompareAction())){
                respondWithXmlFile(response, localizationService.getXML(comparedLocalizationPairs));
                return;
            }
            comparedLocalizationPairs = LocalizationPair.sanitizeHtmlPairs(comparedLocalizationPairs);
            model.addAttribute("compareSets", true);
            model.addAttribute("compareResults", comparedLocalizationPairs);
        } catch (FileNotFoundException e) {
            String problemFile = e.getMessage().toLowerCase();
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.support.localization.errorOpen", 
                                new YukonMessageSourceResolvable("yukon.web.modules.support.localization." + problemFile)));
        } catch (InvalidPropertiesFormatException e) {
            String problemFile = e.getMessage().toLowerCase();
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.support.localization.errorProcess",
                                new YukonMessageSourceResolvable("yukon.web.modules.support.localization." + problemFile)));
        }
    }

    /**
     * Applies StringEscapeUtils.escapeHtml to every member of a list
     */
    private List<String> sanitizeHtml(List<String> unsanitizedStrings){
        List<String> sanitizedStrings = Lists.newArrayList();
        for(String unsanitizedString : unsanitizedStrings){
            String sanitized = StringEscapeUtils.escapeHtml4(unsanitizedString);
            sanitized = sanitized.replaceAll("(\r\n|\n)", "<br />");
            sanitizedStrings.add(sanitized);
        }
        return sanitizedStrings;
    }

    /**
     * Sets up an xml file prepared as a stream for download in the response
     * @param response Http Response
     * @param xmlStream xml File as a stream
     * @throws IOException when unable to copy the stream to the response
     */
    private void respondWithXmlFile(HttpServletResponse response, InputStream xmlStream) throws IOException{
        response.setContentType("text/xml");
        response.setHeader("Content-Disposition", "attachment; filename=\"localization.xml\"");
        FileCopyUtils.copy(xmlStream, response.getOutputStream());
    }
}
