package com.cannontech.common.exception;

//this exception class is also recognized by the FriendlyExceptionResolver
public class PeakSummaryReportRequestException extends RuntimeException {
  
  private static final String defaultMsg = "Unable to initiate Peak Summary Report request";

  public PeakSummaryReportRequestException() {
      // generic error msg
      super(defaultMsg);
  }
  
  public PeakSummaryReportRequestException(Throwable cause) {
      super(defaultMsg, cause);
  }
}