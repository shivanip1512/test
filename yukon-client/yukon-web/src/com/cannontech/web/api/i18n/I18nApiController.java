package com.cannontech.web.api.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;


@RestController
@RequestMapping("/i18n")
public class I18nApiController {
    
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;

    @GetMapping("/keys")
    public ResponseEntity<Object> getKeys(@RequestBody I18nKeyValue[] i18nKeysArgs, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        for (I18nKeyValue i18nKeyValue : i18nKeysArgs) {
            String i18nValue = "";
            if (i18nKeyValue.getArgs() != null) {
                i18nValue = accessor.getMessage(i18nKeyValue.getNameKey(), Arrays.asList(i18nKeyValue.getArgs()));
            } else {
                i18nValue = accessor.getMessage(i18nKeyValue.getNameKey());
            }
            i18nKeyValue.setI18nValue(i18nValue);
        }
        return new ResponseEntity<>(i18nKeysArgs, HttpStatus.OK);
    }
    
    @GetMapping("/key")
    public ResponseEntity<Object> getSingleKey(@RequestParam(name = "key") String key, @RequestParam(name = "args", required=false) String[] args, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String i18nValue = "";
        if (args != null) {
            i18nValue = accessor.getMessage(key, Arrays.asList(args));
        } else {
            i18nValue = accessor.getMessage(key);
        }
        return new ResponseEntity<>(i18nValue, HttpStatus.OK);
    }

}
