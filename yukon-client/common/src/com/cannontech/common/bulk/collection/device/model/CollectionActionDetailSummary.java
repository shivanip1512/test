package com.cannontech.common.bulk.collection.device.model;

/**
 * This enum defines SUCCESS, FAILURE, NOT_ATTEMPTED for the bucket.
 * For example SUCCESS, CONNNECTED, CONFIRMED are considered to be success.
 * Example of use is display of success counts for alert for disconnect collection action: ARM+CONNECT=SUCCESS
 */
public enum CollectionActionDetailSummary {
    SUCCESS,
    FAILURE,
    NOT_ATTEMPTED;
}
