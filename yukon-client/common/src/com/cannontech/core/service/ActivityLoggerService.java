package com.cannontech.core.service;

public interface ActivityLoggerService {
    void logEvent(int userID, String action, String description);
}