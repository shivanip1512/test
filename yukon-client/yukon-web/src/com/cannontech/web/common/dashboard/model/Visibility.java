package com.cannontech.web.common.dashboard.model;

/**
 * Describes which users can see a dashboard.
 */
public enum Visibility {
    SYSTEM, //Built-in, cannot be deleted
    PRIVATE, //Only visible to owner
    PUBLIC, //Visible to everyone
    ;
}
