package com.cannontech.web.picker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.Direction;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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
        final String lcSS = ss.toLowerCase();
        Predicate<UltraLightNotificationGroup> searchFilterPredicate = new Predicate<UltraLightNotificationGroup>() {
            @Override
            public boolean apply(UltraLightNotificationGroup notificationGroup) {
                return notificationGroup.getName().toLowerCase().contains(lcSS);
            }
        };
        List<UltraLightNotificationGroup> notificationGroups =
            Lists.newArrayList(Iterables.filter(getAllNotificationGroups(), searchFilterPredicate));
        return SearchResults.indexBasedForWholeList(start, count, notificationGroups);
    }

    @Override
    public SearchResults<UltraLightNotificationGroup> search(Collection<Integer> initialIds, String extraArgs,
            YukonUserContext userContext) {
        final Set<Integer> initialIdsSet = ImmutableSet.copyOf(initialIds);
        Predicate<UltraLightNotificationGroup> idPredicate = new Predicate<UltraLightNotificationGroup>() {
            @Override
            public boolean apply(UltraLightNotificationGroup notificationGroup) {
                return initialIdsSet.contains(notificationGroup.getNotificationGroupId());
            }
        };
        List<UltraLightNotificationGroup> notificationGroups =
            Lists.newArrayList(Iterables.filter(getAllNotificationGroups(), idPredicate));
        return SearchResults.pageBasedForWholeList(1, Integer.MAX_VALUE, notificationGroups);
    }

    private List<UltraLightNotificationGroup> getAllNotificationGroups() {
        Set<LiteNotificationGroup> allNotificationGroups = notificationGroupDao.getAllNotificationGroups();
        List<UltraLightNotificationGroup> notificationGroups = new ArrayList<>(allNotificationGroups.size());
        
        for (LiteNotificationGroup notificationGroup : allNotificationGroups) {
            notificationGroups.add(new UltraLightNotificationGroup(notificationGroup.getLiteID(),
                notificationGroup.getNotificationGroupName()));
        }

        Collections.sort(notificationGroups);
        return notificationGroups;
    }
    
    @Override
    public SearchResults<UltraLightNotificationGroup> search(Collection<Integer> initialIds, String extraArgs, String sortBy,
            Direction direction, YukonUserContext userContext) {
       throw new UnsupportedOperationException();
    }  
    
    
    @Override
    public SearchResults<UltraLightNotificationGroup> search(String ss, int start, int count,
            String extraArgs, String sortBy, Direction direction, YukonUserContext userContext) {
       throw new UnsupportedOperationException();
    }
}
