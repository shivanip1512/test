package com.cannontech.web.picker.v2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ImmutableList;

public class NotificationGroupPicker extends BasePicker<UltraLightNotificationGroup> {
    @Autowired private NotificationGroupDao notificationGroupDao;

    private final static List<OutputColumn> outputColumns =
            ImmutableList.of(new OutputColumn("name", "yukon.web.picker.notificationGroup.name"));

    @Override
    public String getIdFieldName() {
        return "notificationGroupId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Override
    public SearchResults<UltraLightNotificationGroup> search(String ss, int start, int count, String extraArgs,
            YukonUserContext userContext) {
        return SearchResults.indexBasedForWholeList(start, count, getAllNotificationGroups());
    }

    @Override
    public SearchResults<UltraLightNotificationGroup> search(Iterable<Integer> initialIds, String extraArgs,
            YukonUserContext userContext) {
        return SearchResults.pageBasedForWholeList(1, Integer.MAX_VALUE, getAllNotificationGroups());
    }

    private List<UltraLightNotificationGroup> getAllNotificationGroups() {
        List<UltraLightNotificationGroup> notificationGroups = new ArrayList<>();
        for (LiteNotificationGroup notificationGroup : notificationGroupDao.getAllNotificationGroups()) {
            notificationGroups.add(new UltraLightNotificationGroup(notificationGroup.getLiteID(),
                notificationGroup.getNotificationGroupName()));
        }

        Collections.sort(notificationGroups);
        return notificationGroups;
    }
}
