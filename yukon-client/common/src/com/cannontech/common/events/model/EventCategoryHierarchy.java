package com.cannontech.common.events.model;

import java.util.List;

import com.google.common.collect.Lists;

public class EventCategoryHierarchy implements Cloneable {
    private EventCategory eventCategory = null;
    private List<String> eventLogTypes = Lists.newArrayList();
    private List<EventCategoryHierarchy> childEventCategoryHierarchyList = Lists.newArrayList();

    public EventCategory getEventCategory() {
        return eventCategory;
    }
    public void setEventCategory(EventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }

    public List<String> getEventLogTypes() {
        return eventLogTypes;
    }
    public void setEventLogTypes(List<String> eventLogTypes) {
        this.eventLogTypes = eventLogTypes;
    }

    public void addChildEventCategoryHierarchyList(EventCategoryHierarchy childEventCategoryHierarchy) {
        this.childEventCategoryHierarchyList.add(childEventCategoryHierarchy);
    }
    public List<EventCategoryHierarchy> getChildEventCategoryHierarchyList() {
        return childEventCategoryHierarchyList;
    }
    public void setChildEventCategoryHierarchyList(List<EventCategoryHierarchy> childEventCategoryHierarchyList) {
        this.childEventCategoryHierarchyList = childEventCategoryHierarchyList;
    }
    public boolean isChildEventCategoryHierarchyList() {
        return childEventCategoryHierarchyList.size() > 0;
    }

}