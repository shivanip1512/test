/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;


@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2021-01-11")
public enum RfnMeterDisconnectConfirmationReplyType implements org.apache.thrift.TEnum {
  SUCCESS(0),
  FAILURE(1),
  FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD(2),
  FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT(3),
  FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT(4),
  FAILED_UNEXPECTED_STATUS(5),
  NOT_SUPPORTED(6),
  NETWORK_TIMEOUT(7),
  TIMEOUT(8);

  private final int value;

  private RfnMeterDisconnectConfirmationReplyType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  @org.apache.thrift.annotation.Nullable
  public static RfnMeterDisconnectConfirmationReplyType findByValue(int value) { 
    switch (value) {
      case 0:
        return SUCCESS;
      case 1:
        return FAILURE;
      case 2:
        return FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD;
      case 3:
        return FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT;
      case 4:
        return FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT;
      case 5:
        return FAILED_UNEXPECTED_STATUS;
      case 6:
        return NOT_SUPPORTED;
      case 7:
        return NETWORK_TIMEOUT;
      case 8:
        return TIMEOUT;
      default:
        return null;
    }
  }
}
