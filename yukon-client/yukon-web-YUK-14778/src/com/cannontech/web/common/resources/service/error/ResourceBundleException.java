package com.cannontech.web.common.resources.service.error;

public class ResourceBundleException extends Exception{
    
    private ResourceBundleErrorState state;
    private PackageResourceFilterServiceErrorState filter_state;
    
    public ResourceBundleException(){
    }
    
    public ResourceBundleException(String message){
        super(message);  
    }
    
    public ResourceBundleException(String message, ResourceBundleErrorState state){
       super(message);
       this.state = state;
    }
    
    public ResourceBundleException(String message, Throwable cause, ResourceBundleErrorState state, PackageResourceFilterServiceErrorState filter_state){
        super(message, cause);
        this.state = state;
        this.filter_state = filter_state;
     }
    public ResourceBundleException(Throwable cause)
    {
        super(cause);
    }
    
    public ResourceBundleException(String message, Throwable cause, ResourceBundleErrorState state)
    {
        super(message, cause);
        this.state = state;
    }
    
    public ResourceBundleErrorState getState(){
        return this.state;
    }
    
    public PackageResourceFilterServiceErrorState getFilterState()
    {
        return this.filter_state;
    }
}

