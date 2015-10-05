package com.cannontech.web.common.resources.service.impl;


import java.util.Scanner;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.admin.theme.dao.ThemeDao;
import com.cannontech.web.admin.theme.model.Theme;
import com.cannontech.web.admin.theme.model.ThemePropertyType;
import com.cannontech.web.common.resources.data.ResourceBundle;
import com.cannontech.web.common.resources.service.PackageFilterService;
import com.cannontech.web.common.resources.service.data.ResourceBundleFilterServiceType;
import com.cannontech.web.common.resources.service.error.*;
import com.cannontech.web.input.type.ImageType;
import com.cannontech.web.input.type.PixelType;

/** LessToCSSFilterServiceImpl
*
* This filter provides conversion for less files into css as provided by
* WRO4j library. @see http://wro4j.readthedocs.org/en/latest/
* @see http://lesscss.org/ 
* 
* TODO create a validation for the CSS validation. 
* @version 1
*/
public class LessToCSSFilterServiceImpl implements PackageFilterService {
    @Autowired private ThemeDao themeDao;
    private static final Logger log = YukonLogManager.getLogger(LessToCSSFilterServiceImpl.class);
    private ResourceBundleFilterServiceType filterType = ResourceBundleFilterServiceType.LESS_TO_CSS;
    
    @Override
    public void validateResourceBundle(ResourceBundle bundle) throws PackageResourceFilterException {
        //TODO:create validation for CSS validation
    }

    @Override
    public void processResourceBundle(ResourceBundle bundle) throws PackageResourceFilterException {
        log.debug("processFilterList -> " + bundle.getName());
        Instant start = Instant.now();
        String less = bundle.getResourceResult();
        try {
            if (!bundle.isDefaultLessPackage()){
                less = applyThemeProperties(bundle);
            }
            LessEngine engine = new LessEngine();
            bundle.setResourceResult(engine.compile(less));
            log.debug("processFilterList operation for : " +  bundle.getName() + "duration of : " + 
            new Duration(start, Instant.now()).getMillis() + "ms");
        } catch (LessException e) {
            String message = "caught exception in processResourceBundle: Less engine failed to compile";
            log.warn(message, e);
            
            throw new PackageResourceFilterException(message, e, PackageResourceFilterServiceErrorState.LESS_CONVERSION_FAIL);
        }
    }

    @Override
    public ResourceBundleFilterServiceType getFilterType() {
        return this.filterType;
    }

    private String applyThemeProperties(ResourceBundle bundle)  {
        log.debug("applyThemeProperties -> " + bundle.getName());
        Instant start = Instant.now();
        Theme theme = themeDao.getCurrentTheme();
        StringBuilder css = new StringBuilder();
        Scanner scanner = new Scanner(bundle.getResourceResult());
        String regexPrefix = "(?<=@";
        String regexSuffix = ":\\s{0,5}+)[^;]+?(?=;)";
        while(scanner.hasNextLine()){
           String line = scanner.nextLine();
           for (ThemePropertyType type : theme.getProperties().keySet()) {
               log.debug("regex composite token:" + type.getVarName());
               String regex = regexPrefix + type.getVarName() + regexSuffix;
               String replacement = (String)theme.getProperties().get(type);
               
               if (type.getInputType() instanceof ImageType) {
                   replacement = "'../resources/images/" +  replacement + "'";
               } else if (type.getInputType() instanceof PixelType) {
                   replacement = replacement + "px";
               }
               
               line = line.replaceAll(regex, replacement);
           }
           css.append(line).append("\n");   
        }
        scanner.close();
        log.debug("applyThemeProperties operation for : " +  bundle.getName() + "duration of : " + 
                new Duration(start, Instant.now()).getMillis() + "ms");
        return css.toString();
    }
}

