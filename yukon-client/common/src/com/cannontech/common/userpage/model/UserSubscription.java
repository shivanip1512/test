package com.cannontech.common.userpage.model;


public final class UserSubscription {

    private final Integer id;
    private final Integer userId;
    private final SubscriptionType type;
    private final Integer refId;

    public enum SubscriptionType{
        DEVICE_DATA_MONITOR,
        OUTAGE_MONITOR,
        TAMPER_FLAG_MONITOR,
        STATUS_POINT_MONITOR,
        PORTER_RESPONSE_MONITOR,
        VALIDATION_MONITOR,
    }

    public UserSubscription(Integer userId, SubscriptionType type, Integer refId, Integer id) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.refId = refId;
    }

    public UserSubscription updateId(Integer id) {
        if (this.id == id) {
            return this;
        } else {
            return new UserSubscription(this.userId, this.type, this.refId, id);
        }
    }

    public final Integer getId() {
        return id;
    }
    public final Integer getUserId() {
        return userId;
    }
    public final SubscriptionType getType() {
        return type;
    }
    public final Integer getRefId() {
        return refId;
    }
}