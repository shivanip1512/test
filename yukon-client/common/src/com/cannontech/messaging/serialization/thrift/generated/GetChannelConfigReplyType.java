/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;


@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public enum GetChannelConfigReplyType implements org.apache.thrift.TEnum {
  SUCCESS(0),
  INVALID_DEVICE(1),
  NO_NODE(2),
  FAILURE(3);

  private final int value;

  private GetChannelConfigReplyType(int value) {
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
  public static GetChannelConfigReplyType findByValue(int value) { 
    switch (value) {
      case 0:
        return SUCCESS;
      case 1:
        return INVALID_DEVICE;
      case 2:
        return NO_NODE;
      case 3:
        return FAILURE;
      default:
        return null;
    }
  }
}
