package com.cannontech.web.tools.trends.validator;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

@Service
public class TrendEditorValidator extends SimpleValidator<TrendModel> {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private GraphDao graphDao;

    private static final String baseKey = "yukon.web.modules.tools.trend";

    public TrendEditorValidator() {
        super(TrendModel.class);
    }

    @Override
    protected void doValidation(TrendModel trendModel, Errors errors) {

        // TODO: check if this is correct??
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
        String nameI18nText = accessor.getMessage("yukon.common.name");

        // 1. Mandatory field.
        String trendName = StringUtils.trim(trendModel.getName());
        if (StringUtils.isBlank(trendName)) {
            errors.rejectValue("name", "yukon.web.error.fieldrequired", new Object[] { nameI18nText }, "Name is required.");
        }

        // 2. Should not contain illegal chars
        // TODO: We could also use PaoUtils.isValidPaoName() here.. That could be misleading since trend is not a PAO. Confirm
        // this.
        if (!errors.hasErrors() && !StringUtils.containsNone(trendName, PaoUtils.ILLEGAL_NAME_CHARS)) {
            errors.rejectValue("name", baseKey + ".trend.field.error.containsIllegalChars",
                    new Object[] { nameI18nText, String.valueOf(PaoUtils.ILLEGAL_NAME_CHARS) },
                    "Name cannot include any of the following characters: " + String.valueOf(PaoUtils.ILLEGAL_NAME_CHARS));
        }

        // 3. Max length is 60 chars.
        if (!errors.hasErrors() && StringUtils.length(trendName) > 60) {
            errors.rejectValue("name", baseKey + ".field.error.maxLengthExceeded",
                    new Object[] { nameI18nText, 60 }, "Name cannot exceed 60 characters.");
        }

        // 4. Name should be unique.
        if (!errors.hasErrors()) {
            List<LiteGraphDefinition> graphDefs = graphDao.getGraphDefinitions();
            for (LiteGraphDefinition liteGraphDefinition : graphDefs) {
                if (StringUtils.equals(trendName, liteGraphDefinition.getName())) {
                    if (trendModel.getTrendId() == null || liteGraphDefinition.getLiteID() != trendModel.getTrendId()) {
                        errors.rejectValue("name", baseKey + ".name.error.notUnique");
                    }
                    break;
                }
            }
        }
    }
}