package com.cannontech.database.data.user;

public class RoleConflictInUserGroupException extends RuntimeException {
    public RoleConflictInUserGroupException(String message) {
        super(message);
    }
    
    public RoleConflictInUserGroupException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RoleConflictInUserGroupException(){}
    
}