/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;


@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2021-01-11")
public enum RfnMeterDisconnectInitialReplyType implements org.apache.thrift.TEnum {
  OK(0),
  NO_NODE(1),
  NO_GATEWAY(2),
  FAILURE(3),
  TIMEOUT(4);

  private final int value;

  private RfnMeterDisconnectInitialReplyType(int value) {
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
  public static RfnMeterDisconnectInitialReplyType findByValue(int value) { 
    switch (value) {
      case 0:
        return OK;
      case 1:
        return NO_NODE;
      case 2:
        return NO_GATEWAY;
      case 3:
        return FAILURE;
      case 4:
        return TIMEOUT;
      default:
        return null;
    }
  }
}
