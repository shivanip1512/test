/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;


@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.16.0)", date = "2022-05-31")
public enum RfnExpressComBroadcastReplyType implements org.apache.thrift.TEnum {
  SUCCESS(0),
  FAILURE(1),
  NETWORK_TIMEOUT(2),
  TIMEOUT(3);

  private final int value;

  private RfnExpressComBroadcastReplyType(int value) {
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
  public static RfnExpressComBroadcastReplyType findByValue(int value) { 
    switch (value) {
      case 0:
        return SUCCESS;
      case 1:
        return FAILURE;
      case 2:
        return NETWORK_TIMEOUT;
      case 3:
        return TIMEOUT;
      default:
        return null;
    }
  }
}
