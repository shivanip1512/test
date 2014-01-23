package com.cannontech.common.survey.model;

import com.cannontech.common.i18n.DisplayableEnum;


/**
 * need to generate UI for creating question as well as UI for asking question
 * also need to generate db commands for:
 * creating a new question
 */
public enum QuestionType implements DisplayableEnum {
    DROP_DOWN,
    TEXT;

    private final static String keyPrefix =
        "yukon.web.modules.adminSetup.survey.questionType.";

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
