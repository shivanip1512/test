package com.cannontech.web.picker;

public final class UltraLightNotificationGroup implements Comparable<UltraLightNotificationGroup> {
    private final int notificationGroupId;
    private final String name;

    public UltraLightNotificationGroup(int notificationGroupId, String name) {
        this.notificationGroupId = notificationGroupId;
        this.name = name;
    }

    public int getNotificationGroupId() {
        return notificationGroupId;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(UltraLightNotificationGroup other) {
        return name.compareTo(other.name);
    }
}
