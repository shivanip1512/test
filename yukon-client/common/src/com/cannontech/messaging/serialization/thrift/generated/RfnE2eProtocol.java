/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;


@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public enum RfnE2eProtocol implements org.apache.thrift.TEnum {
  APPLICATION(0),
  NETWORK(1),
  LINK(2);

  private final int value;

  private RfnE2eProtocol(int value) {
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
  public static RfnE2eProtocol findByValue(int value) { 
    switch (value) {
      case 0:
        return APPLICATION;
      case 1:
        return NETWORK;
      case 2:
        return LINK;
      default:
        return null;
    }
  }
}
