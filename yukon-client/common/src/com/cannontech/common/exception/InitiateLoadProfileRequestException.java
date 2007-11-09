package com.cannontech.common.exception;

//this exception class is also recognized by the FriendlyExceptionResolver
public class InitiateLoadProfileRequestException extends RuntimeException {
  
  private static final String defaultMsg = "Unable to initiate Load Profile request";

  public InitiateLoadProfileRequestException() {
      // generic error msg
      super(defaultMsg);
  }
  
  public InitiateLoadProfileRequestException(Throwable cause) {
      super(defaultMsg, cause);
  }
}