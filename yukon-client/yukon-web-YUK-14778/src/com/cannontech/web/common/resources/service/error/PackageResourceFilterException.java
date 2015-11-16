package com.cannontech.web.common.resources.service.error;

public class PackageResourceFilterException extends Exception {
    private PackageResourceFilterServiceErrorState state;
    public PackageResourceFilterException(){
    }
    public PackageResourceFilterException(String message){
        super(message);
    }
    public PackageResourceFilterException(Throwable cause){
        super(cause);
    }
    public PackageResourceFilterException(String message, Throwable cause , PackageResourceFilterServiceErrorState state){
        super(message, cause);
        this.state = state;
     
    }
    public PackageResourceFilterServiceErrorState getState(){
        return this.state;
    }
}

