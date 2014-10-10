package com.cannontech.common.weather;


public class NoaaWeatherDataServiceException extends Exception {
    public NoaaWeatherDataServiceException(String message) {
        super(message);
    }
    public NoaaWeatherDataServiceException(Exception e) {
        super(e);
    }
    public NoaaWeatherDataServiceException(String string, Throwable e) {
        super(string,e);
    }
}
