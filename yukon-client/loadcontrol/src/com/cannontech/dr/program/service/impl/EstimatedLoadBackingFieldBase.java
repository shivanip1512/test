package com.cannontech.dr.program.service.impl;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;

public abstract class EstimatedLoadBackingFieldBase implements EstimatedLoadBackingField {

    protected final static MessageSourceResolvable blankFieldResolvable = 
            new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");

    protected final static MessageSourceResolvable calculatingFieldResolvable = 
            new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");
}
