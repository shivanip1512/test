package com.cannontech.common.exception;

//this exception class is also recognized by the FriendlyExceptionResolver
public class PingMeterException extends RuntimeException {
  
  private static final String defaultMsg = "Unable to send ping request to meter";

  public PingMeterException() {
      // generic error msg
      super(defaultMsg);
  }
  
  public PingMeterException(Throwable cause) {
      super(defaultMsg, cause);
  }
}
