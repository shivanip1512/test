/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum RfnE2eDataReplyType implements org.apache.thrift.TEnum {
  OK(0),
  DESTINATION_DEVICE_ADDRESS_UNKNOWN(1),
  DESTINATION_NETWORK_UNAVAILABLE(2),
  PMTU_LENGTH_EXCEEDED(3),
  E2E_PROTOCOL_TYPE_NOT_SUPPORTED(4),
  NETWORK_SERVER_IDENTIFIER_INVALID(5),
  APPLICATION_SERVICE_IDENTIFIER_INVALID(6),
  NETWORK_LOAD_CONTROL(7),
  NETWORK_SERVICE_FAILURE(8);

  private final int value;

  private RfnE2eDataReplyType(int value) {
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
  public static RfnE2eDataReplyType findByValue(int value) { 
    switch (value) {
      case 0:
        return OK;
      case 1:
        return DESTINATION_DEVICE_ADDRESS_UNKNOWN;
      case 2:
        return DESTINATION_NETWORK_UNAVAILABLE;
      case 3:
        return PMTU_LENGTH_EXCEEDED;
      case 4:
        return E2E_PROTOCOL_TYPE_NOT_SUPPORTED;
      case 5:
        return NETWORK_SERVER_IDENTIFIER_INVALID;
      case 6:
        return APPLICATION_SERVICE_IDENTIFIER_INVALID;
      case 7:
        return NETWORK_LOAD_CONTROL;
      case 8:
        return NETWORK_SERVICE_FAILURE;
      default:
        return null;
    }
  }
}
