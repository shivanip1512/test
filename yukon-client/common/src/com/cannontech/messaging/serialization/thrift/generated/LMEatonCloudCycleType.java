/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;


@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2020-12-09")
public enum LMEatonCloudCycleType implements org.apache.thrift.TEnum {
  STANDARD(0),
  TRUE_CYCLE(1),
  SMART_CYCLE(2);

  private final int value;

  private LMEatonCloudCycleType(int value) {
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
  public static LMEatonCloudCycleType findByValue(int value) { 
    switch (value) {
      case 0:
        return STANDARD;
      case 1:
        return TRUE_CYCLE;
      case 2:
        return SMART_CYCLE;
      default:
        return null;
    }
  }
}
