package com.cannontech.web.api.trend;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class ResetPeakValidatorHelper {
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private MessageSourceAccessor accessor;

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    /**
     * Return true if Reset Peak is applicable for the trend.
     */
    @SuppressWarnings("unchecked")
    public boolean checkIfResetPeakApplicable(Integer id) {
        LiteGraphDefinition liteTrend = dbCache.getAllGraphDefinitions()
                                               .stream()
                                               .filter(group -> group.getLiteID() == id)
                                               .findFirst()
                                               .orElseThrow(() -> new NotFoundException("Trend Id not found"));

        GraphDefinition trend = (GraphDefinition) LiteFactory.createDBPersistent(liteTrend);
        GraphDefinition graphDefinition = (GraphDefinition) dbPersistentDao.retrieveDBPersistent(trend);

        return graphDefinition.getGraphDataSeries()
                              .stream()
                              .filter(series -> GDSTypesFuncs.isPeakType(((GraphDataSeries) series).getType().intValue()))
                              .findAny()
                              .isPresent();
    }

    /**
     * Validate resetPeak StartDate.
     */
    public void validateStartDate(DateTime startDate, Errors errors) {
        String dateI18nText = accessor.getMessage("yukon.common.date");
        if (!errors.hasFieldErrors("startDate")) {
            YukonValidationUtils.checkIsBlank(errors, "startDate", Objects.toString(startDate, null), dateI18nText, false);
            if (!errors.hasFieldErrors("startDate") && startDate.isAfterNow()) {
                errors.rejectValue("startDate", "yukon.web.error.date.inThePast");
            }
        }
    }
    
    public void validateIfResetPeakIsApplicable(Integer trendId, Errors errors) {
        if (!checkIfResetPeakApplicable(Integer.valueOf(trendId))) {
            errors.reject("yukon.web.error.resetPeak.notApplicable");
        }
    }
}
