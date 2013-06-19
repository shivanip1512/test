package com.cannontech.web.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.core.users.dao.YukonUserPreferenceDao;
import com.cannontech.core.users.model.PreferenceGraphTimeDurationOption;
import com.cannontech.core.users.model.PreferenceGraphVisualTypeOption;
import com.cannontech.core.users.model.YukonUserPreference;
import com.cannontech.core.users.model.YukonUserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.user.service.YukonUserPreferenceService;

public class YukonUserPreferenceServiceImpl implements YukonUserPreferenceService {

    @Autowired private YukonUserPreferenceDao prefDao;

    @Override
    public YukonUserPreference savePreference(LiteYukonUser user, YukonUserPreferenceName preference, String newValue) {

        YukonUserPreference pref = prefDao.findPreference(user, preference);
        if(pref == null) {
            pref = new YukonUserPreference();
            pref.setName(preference);
            pref.setUserId(user.getUserID());
            pref.setValue(newValue);
            prefDao.create(pref);
        } else {
            pref.setValue(newValue);
            prefDao.update(pref);
        }
        return pref;
    }

    @Override
    public int deleteAllSavedPreferencesForUser(LiteYukonUser user) {
        int count = prefDao.deleteAllSavedPreferencesForUser(user);
        return count;
    }

    @Override
    public String getPreference(LiteYukonUser user, YukonUserPreferenceName category) {
        return prefDao.getValueOrDefault(user, category);
    }

    @Override
    public List<YukonUserPreference> findAllSavedPreferencesForUser(LiteYukonUser user) {
        return prefDao.findAllSavedPreferencesForUser(user);
    }

    @Override
    public PreferenceGraphVisualTypeOption updatePreferenceOrGetDefaultGraphType(GraphType requestedValueOrNull, 
                                                                                 LiteYukonUser user) {
        PreferenceGraphVisualTypeOption prefType = null;
        if (requestedValueOrNull == null) {
            String graphTypeString = this.getPreference(user, YukonUserPreferenceName.GRAPH_DISPLAY_VISUAL_TYPE);
            prefType = PreferenceGraphVisualTypeOption.valueOf(graphTypeString);
        } else {
            prefType = PreferenceGraphVisualTypeOption.fromGraphType(requestedValueOrNull);
            this.savePreference(user, YukonUserPreferenceName.GRAPH_DISPLAY_VISUAL_TYPE, prefType.toString());
        }
        return prefType;
    }

    @Override
    public PreferenceGraphTimeDurationOption updatePreferenceOrGetDefaultChartPeriod(ChartPeriod requestedValueOrNull, 
                                                                                     LiteYukonUser user) {
        PreferenceGraphTimeDurationOption prefType = null;
        if (requestedValueOrNull == null) {
            String chartPeriodString = this.getPreference(user, YukonUserPreferenceName.GRAPH_DISPLAY_TIME_DURATION);
            prefType = PreferenceGraphTimeDurationOption.valueOf(chartPeriodString);
        } else {
            prefType = PreferenceGraphTimeDurationOption.fromChartPeriod(requestedValueOrNull);
            this.savePreference(user, YukonUserPreferenceName.GRAPH_DISPLAY_TIME_DURATION, prefType.toString());
        }
        return prefType;
    }
}
