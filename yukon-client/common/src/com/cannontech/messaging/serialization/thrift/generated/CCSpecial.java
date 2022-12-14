/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public class CCSpecial implements org.apache.thrift.TBase<CCSpecial, CCSpecial._Fields>, java.io.Serializable, Cloneable, Comparable<CCSpecial> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("CCSpecial");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _SUBSTATION_IDS_FIELD_DESC = new org.apache.thrift.protocol.TField("_substationIds", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField _OV_UV_DISABLED_FLAG_FIELD_DESC = new org.apache.thrift.protocol.TField("_ovUvDisabledFlag", org.apache.thrift.protocol.TType.BOOL, (short)3);
  private static final org.apache.thrift.protocol.TField _PF_DISPLAY_VALUE_FIELD_DESC = new org.apache.thrift.protocol.TField("_pfDisplayValue", org.apache.thrift.protocol.TType.DOUBLE, (short)4);
  private static final org.apache.thrift.protocol.TField _EST_PF_DISPLAY_VALUE_FIELD_DESC = new org.apache.thrift.protocol.TField("_estPfDisplayValue", org.apache.thrift.protocol.TType.DOUBLE, (short)5);
  private static final org.apache.thrift.protocol.TField _VOLT_REDUCTION_CONTROL_VALUE_FIELD_DESC = new org.apache.thrift.protocol.TField("_voltReductionControlValue", org.apache.thrift.protocol.TType.BOOL, (short)6);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new CCSpecialStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new CCSpecialTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.CCPao _baseMessage; // required
  private @org.apache.thrift.annotation.Nullable java.util.List<java.lang.Integer> _substationIds; // required
  private boolean _ovUvDisabledFlag; // required
  private double _pfDisplayValue; // required
  private double _estPfDisplayValue; // required
  private boolean _voltReductionControlValue; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _SUBSTATION_IDS((short)2, "_substationIds"),
    _OV_UV_DISABLED_FLAG((short)3, "_ovUvDisabledFlag"),
    _PF_DISPLAY_VALUE((short)4, "_pfDisplayValue"),
    _EST_PF_DISPLAY_VALUE((short)5, "_estPfDisplayValue"),
    _VOLT_REDUCTION_CONTROL_VALUE((short)6, "_voltReductionControlValue");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // _BASE_MESSAGE
          return _BASE_MESSAGE;
        case 2: // _SUBSTATION_IDS
          return _SUBSTATION_IDS;
        case 3: // _OV_UV_DISABLED_FLAG
          return _OV_UV_DISABLED_FLAG;
        case 4: // _PF_DISPLAY_VALUE
          return _PF_DISPLAY_VALUE;
        case 5: // _EST_PF_DISPLAY_VALUE
          return _EST_PF_DISPLAY_VALUE;
        case 6: // _VOLT_REDUCTION_CONTROL_VALUE
          return _VOLT_REDUCTION_CONTROL_VALUE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int ___OVUVDISABLEDFLAG_ISSET_ID = 0;
  private static final int ___PFDISPLAYVALUE_ISSET_ID = 1;
  private static final int ___ESTPFDISPLAYVALUE_ISSET_ID = 2;
  private static final int ___VOLTREDUCTIONCONTROLVALUE_ISSET_ID = 3;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.CCPao.class)));
    tmpMap.put(_Fields._SUBSTATION_IDS, new org.apache.thrift.meta_data.FieldMetaData("_substationIds", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
    tmpMap.put(_Fields._OV_UV_DISABLED_FLAG, new org.apache.thrift.meta_data.FieldMetaData("_ovUvDisabledFlag", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields._PF_DISPLAY_VALUE, new org.apache.thrift.meta_data.FieldMetaData("_pfDisplayValue", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields._EST_PF_DISPLAY_VALUE, new org.apache.thrift.meta_data.FieldMetaData("_estPfDisplayValue", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields._VOLT_REDUCTION_CONTROL_VALUE, new org.apache.thrift.meta_data.FieldMetaData("_voltReductionControlValue", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(CCSpecial.class, metaDataMap);
  }

  public CCSpecial() {
  }

  public CCSpecial(
    com.cannontech.messaging.serialization.thrift.generated.CCPao _baseMessage,
    java.util.List<java.lang.Integer> _substationIds,
    boolean _ovUvDisabledFlag,
    double _pfDisplayValue,
    double _estPfDisplayValue,
    boolean _voltReductionControlValue)
  {
    this();
    this._baseMessage = _baseMessage;
    this._substationIds = _substationIds;
    this._ovUvDisabledFlag = _ovUvDisabledFlag;
    set_ovUvDisabledFlagIsSet(true);
    this._pfDisplayValue = _pfDisplayValue;
    set_pfDisplayValueIsSet(true);
    this._estPfDisplayValue = _estPfDisplayValue;
    set_estPfDisplayValueIsSet(true);
    this._voltReductionControlValue = _voltReductionControlValue;
    set_voltReductionControlValueIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CCSpecial(CCSpecial other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.CCPao(other._baseMessage);
    }
    if (other.isSet_substationIds()) {
      java.util.List<java.lang.Integer> __this___substationIds = new java.util.ArrayList<java.lang.Integer>(other._substationIds);
      this._substationIds = __this___substationIds;
    }
    this._ovUvDisabledFlag = other._ovUvDisabledFlag;
    this._pfDisplayValue = other._pfDisplayValue;
    this._estPfDisplayValue = other._estPfDisplayValue;
    this._voltReductionControlValue = other._voltReductionControlValue;
  }

  public CCSpecial deepCopy() {
    return new CCSpecial(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    this._substationIds = null;
    set_ovUvDisabledFlagIsSet(false);
    this._ovUvDisabledFlag = false;
    set_pfDisplayValueIsSet(false);
    this._pfDisplayValue = 0.0;
    set_estPfDisplayValueIsSet(false);
    this._estPfDisplayValue = 0.0;
    set_voltReductionControlValueIsSet(false);
    this._voltReductionControlValue = false;
  }

  @org.apache.thrift.annotation.Nullable
  public com.cannontech.messaging.serialization.thrift.generated.CCPao get_baseMessage() {
    return this._baseMessage;
  }

  public void set_baseMessage(@org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.CCPao _baseMessage) {
    this._baseMessage = _baseMessage;
  }

  public void unset_baseMessage() {
    this._baseMessage = null;
  }

  /** Returns true if field _baseMessage is set (has been assigned a value) and false otherwise */
  public boolean isSet_baseMessage() {
    return this._baseMessage != null;
  }

  public void set_baseMessageIsSet(boolean value) {
    if (!value) {
      this._baseMessage = null;
    }
  }

  public int get_substationIdsSize() {
    return (this._substationIds == null) ? 0 : this._substationIds.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<java.lang.Integer> get_substationIdsIterator() {
    return (this._substationIds == null) ? null : this._substationIds.iterator();
  }

  public void addTo_substationIds(int elem) {
    if (this._substationIds == null) {
      this._substationIds = new java.util.ArrayList<java.lang.Integer>();
    }
    this._substationIds.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<java.lang.Integer> get_substationIds() {
    return this._substationIds;
  }

  public void set_substationIds(@org.apache.thrift.annotation.Nullable java.util.List<java.lang.Integer> _substationIds) {
    this._substationIds = _substationIds;
  }

  public void unset_substationIds() {
    this._substationIds = null;
  }

  /** Returns true if field _substationIds is set (has been assigned a value) and false otherwise */
  public boolean isSet_substationIds() {
    return this._substationIds != null;
  }

  public void set_substationIdsIsSet(boolean value) {
    if (!value) {
      this._substationIds = null;
    }
  }

  public boolean is_ovUvDisabledFlag() {
    return this._ovUvDisabledFlag;
  }

  public void set_ovUvDisabledFlag(boolean _ovUvDisabledFlag) {
    this._ovUvDisabledFlag = _ovUvDisabledFlag;
    set_ovUvDisabledFlagIsSet(true);
  }

  public void unset_ovUvDisabledFlag() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___OVUVDISABLEDFLAG_ISSET_ID);
  }

  /** Returns true if field _ovUvDisabledFlag is set (has been assigned a value) and false otherwise */
  public boolean isSet_ovUvDisabledFlag() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___OVUVDISABLEDFLAG_ISSET_ID);
  }

  public void set_ovUvDisabledFlagIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___OVUVDISABLEDFLAG_ISSET_ID, value);
  }

  public double get_pfDisplayValue() {
    return this._pfDisplayValue;
  }

  public void set_pfDisplayValue(double _pfDisplayValue) {
    this._pfDisplayValue = _pfDisplayValue;
    set_pfDisplayValueIsSet(true);
  }

  public void unset_pfDisplayValue() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___PFDISPLAYVALUE_ISSET_ID);
  }

  /** Returns true if field _pfDisplayValue is set (has been assigned a value) and false otherwise */
  public boolean isSet_pfDisplayValue() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___PFDISPLAYVALUE_ISSET_ID);
  }

  public void set_pfDisplayValueIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___PFDISPLAYVALUE_ISSET_ID, value);
  }

  public double get_estPfDisplayValue() {
    return this._estPfDisplayValue;
  }

  public void set_estPfDisplayValue(double _estPfDisplayValue) {
    this._estPfDisplayValue = _estPfDisplayValue;
    set_estPfDisplayValueIsSet(true);
  }

  public void unset_estPfDisplayValue() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___ESTPFDISPLAYVALUE_ISSET_ID);
  }

  /** Returns true if field _estPfDisplayValue is set (has been assigned a value) and false otherwise */
  public boolean isSet_estPfDisplayValue() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___ESTPFDISPLAYVALUE_ISSET_ID);
  }

  public void set_estPfDisplayValueIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___ESTPFDISPLAYVALUE_ISSET_ID, value);
  }

  public boolean is_voltReductionControlValue() {
    return this._voltReductionControlValue;
  }

  public void set_voltReductionControlValue(boolean _voltReductionControlValue) {
    this._voltReductionControlValue = _voltReductionControlValue;
    set_voltReductionControlValueIsSet(true);
  }

  public void unset_voltReductionControlValue() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___VOLTREDUCTIONCONTROLVALUE_ISSET_ID);
  }

  /** Returns true if field _voltReductionControlValue is set (has been assigned a value) and false otherwise */
  public boolean isSet_voltReductionControlValue() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___VOLTREDUCTIONCONTROLVALUE_ISSET_ID);
  }

  public void set_voltReductionControlValueIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___VOLTREDUCTIONCONTROLVALUE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _BASE_MESSAGE:
      if (value == null) {
        unset_baseMessage();
      } else {
        set_baseMessage((com.cannontech.messaging.serialization.thrift.generated.CCPao)value);
      }
      break;

    case _SUBSTATION_IDS:
      if (value == null) {
        unset_substationIds();
      } else {
        set_substationIds((java.util.List<java.lang.Integer>)value);
      }
      break;

    case _OV_UV_DISABLED_FLAG:
      if (value == null) {
        unset_ovUvDisabledFlag();
      } else {
        set_ovUvDisabledFlag((java.lang.Boolean)value);
      }
      break;

    case _PF_DISPLAY_VALUE:
      if (value == null) {
        unset_pfDisplayValue();
      } else {
        set_pfDisplayValue((java.lang.Double)value);
      }
      break;

    case _EST_PF_DISPLAY_VALUE:
      if (value == null) {
        unset_estPfDisplayValue();
      } else {
        set_estPfDisplayValue((java.lang.Double)value);
      }
      break;

    case _VOLT_REDUCTION_CONTROL_VALUE:
      if (value == null) {
        unset_voltReductionControlValue();
      } else {
        set_voltReductionControlValue((java.lang.Boolean)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _SUBSTATION_IDS:
      return get_substationIds();

    case _OV_UV_DISABLED_FLAG:
      return is_ovUvDisabledFlag();

    case _PF_DISPLAY_VALUE:
      return get_pfDisplayValue();

    case _EST_PF_DISPLAY_VALUE:
      return get_estPfDisplayValue();

    case _VOLT_REDUCTION_CONTROL_VALUE:
      return is_voltReductionControlValue();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case _BASE_MESSAGE:
      return isSet_baseMessage();
    case _SUBSTATION_IDS:
      return isSet_substationIds();
    case _OV_UV_DISABLED_FLAG:
      return isSet_ovUvDisabledFlag();
    case _PF_DISPLAY_VALUE:
      return isSet_pfDisplayValue();
    case _EST_PF_DISPLAY_VALUE:
      return isSet_estPfDisplayValue();
    case _VOLT_REDUCTION_CONTROL_VALUE:
      return isSet_voltReductionControlValue();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof CCSpecial)
      return this.equals((CCSpecial)that);
    return false;
  }

  public boolean equals(CCSpecial that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present__baseMessage = true && this.isSet_baseMessage();
    boolean that_present__baseMessage = true && that.isSet_baseMessage();
    if (this_present__baseMessage || that_present__baseMessage) {
      if (!(this_present__baseMessage && that_present__baseMessage))
        return false;
      if (!this._baseMessage.equals(that._baseMessage))
        return false;
    }

    boolean this_present__substationIds = true && this.isSet_substationIds();
    boolean that_present__substationIds = true && that.isSet_substationIds();
    if (this_present__substationIds || that_present__substationIds) {
      if (!(this_present__substationIds && that_present__substationIds))
        return false;
      if (!this._substationIds.equals(that._substationIds))
        return false;
    }

    boolean this_present__ovUvDisabledFlag = true;
    boolean that_present__ovUvDisabledFlag = true;
    if (this_present__ovUvDisabledFlag || that_present__ovUvDisabledFlag) {
      if (!(this_present__ovUvDisabledFlag && that_present__ovUvDisabledFlag))
        return false;
      if (this._ovUvDisabledFlag != that._ovUvDisabledFlag)
        return false;
    }

    boolean this_present__pfDisplayValue = true;
    boolean that_present__pfDisplayValue = true;
    if (this_present__pfDisplayValue || that_present__pfDisplayValue) {
      if (!(this_present__pfDisplayValue && that_present__pfDisplayValue))
        return false;
      if (this._pfDisplayValue != that._pfDisplayValue)
        return false;
    }

    boolean this_present__estPfDisplayValue = true;
    boolean that_present__estPfDisplayValue = true;
    if (this_present__estPfDisplayValue || that_present__estPfDisplayValue) {
      if (!(this_present__estPfDisplayValue && that_present__estPfDisplayValue))
        return false;
      if (this._estPfDisplayValue != that._estPfDisplayValue)
        return false;
    }

    boolean this_present__voltReductionControlValue = true;
    boolean that_present__voltReductionControlValue = true;
    if (this_present__voltReductionControlValue || that_present__voltReductionControlValue) {
      if (!(this_present__voltReductionControlValue && that_present__voltReductionControlValue))
        return false;
      if (this._voltReductionControlValue != that._voltReductionControlValue)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSet_baseMessage()) ? 131071 : 524287);
    if (isSet_baseMessage())
      hashCode = hashCode * 8191 + _baseMessage.hashCode();

    hashCode = hashCode * 8191 + ((isSet_substationIds()) ? 131071 : 524287);
    if (isSet_substationIds())
      hashCode = hashCode * 8191 + _substationIds.hashCode();

    hashCode = hashCode * 8191 + ((_ovUvDisabledFlag) ? 131071 : 524287);

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_pfDisplayValue);

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_estPfDisplayValue);

    hashCode = hashCode * 8191 + ((_voltReductionControlValue) ? 131071 : 524287);

    return hashCode;
  }

  @Override
  public int compareTo(CCSpecial other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSet_baseMessage()).compareTo(other.isSet_baseMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_baseMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._baseMessage, other._baseMessage);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_substationIds()).compareTo(other.isSet_substationIds());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_substationIds()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._substationIds, other._substationIds);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_ovUvDisabledFlag()).compareTo(other.isSet_ovUvDisabledFlag());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_ovUvDisabledFlag()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._ovUvDisabledFlag, other._ovUvDisabledFlag);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_pfDisplayValue()).compareTo(other.isSet_pfDisplayValue());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_pfDisplayValue()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._pfDisplayValue, other._pfDisplayValue);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_estPfDisplayValue()).compareTo(other.isSet_estPfDisplayValue());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_estPfDisplayValue()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._estPfDisplayValue, other._estPfDisplayValue);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_voltReductionControlValue()).compareTo(other.isSet_voltReductionControlValue());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_voltReductionControlValue()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._voltReductionControlValue, other._voltReductionControlValue);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("CCSpecial(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_substationIds:");
    if (this._substationIds == null) {
      sb.append("null");
    } else {
      sb.append(this._substationIds);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_ovUvDisabledFlag:");
    sb.append(this._ovUvDisabledFlag);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_pfDisplayValue:");
    sb.append(this._pfDisplayValue);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_estPfDisplayValue:");
    sb.append(this._estPfDisplayValue);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_voltReductionControlValue:");
    sb.append(this._voltReductionControlValue);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_baseMessage()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_baseMessage' is unset! Struct:" + toString());
    }

    if (!isSet_substationIds()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_substationIds' is unset! Struct:" + toString());
    }

    if (!isSet_ovUvDisabledFlag()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_ovUvDisabledFlag' is unset! Struct:" + toString());
    }

    if (!isSet_pfDisplayValue()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_pfDisplayValue' is unset! Struct:" + toString());
    }

    if (!isSet_estPfDisplayValue()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_estPfDisplayValue' is unset! Struct:" + toString());
    }

    if (!isSet_voltReductionControlValue()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_voltReductionControlValue' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (_baseMessage != null) {
      _baseMessage.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class CCSpecialStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CCSpecialStandardScheme getScheme() {
      return new CCSpecialStandardScheme();
    }
  }

  private static class CCSpecialStandardScheme extends org.apache.thrift.scheme.StandardScheme<CCSpecial> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, CCSpecial struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // _BASE_MESSAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.CCPao();
              struct._baseMessage.read(iprot);
              struct.set_baseMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _SUBSTATION_IDS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct._substationIds = new java.util.ArrayList<java.lang.Integer>(_list0.size);
                int _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = iprot.readI32();
                  struct._substationIds.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.set_substationIdsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _OV_UV_DISABLED_FLAG
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct._ovUvDisabledFlag = iprot.readBool();
              struct.set_ovUvDisabledFlagIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _PF_DISPLAY_VALUE
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct._pfDisplayValue = iprot.readDouble();
              struct.set_pfDisplayValueIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // _EST_PF_DISPLAY_VALUE
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct._estPfDisplayValue = iprot.readDouble();
              struct.set_estPfDisplayValueIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // _VOLT_REDUCTION_CONTROL_VALUE
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct._voltReductionControlValue = iprot.readBool();
              struct.set_voltReductionControlValueIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, CCSpecial struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct._substationIds != null) {
        oprot.writeFieldBegin(_SUBSTATION_IDS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, struct._substationIds.size()));
          for (int _iter3 : struct._substationIds)
          {
            oprot.writeI32(_iter3);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_OV_UV_DISABLED_FLAG_FIELD_DESC);
      oprot.writeBool(struct._ovUvDisabledFlag);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_PF_DISPLAY_VALUE_FIELD_DESC);
      oprot.writeDouble(struct._pfDisplayValue);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_EST_PF_DISPLAY_VALUE_FIELD_DESC);
      oprot.writeDouble(struct._estPfDisplayValue);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_VOLT_REDUCTION_CONTROL_VALUE_FIELD_DESC);
      oprot.writeBool(struct._voltReductionControlValue);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CCSpecialTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CCSpecialTupleScheme getScheme() {
      return new CCSpecialTupleScheme();
    }
  }

  private static class CCSpecialTupleScheme extends org.apache.thrift.scheme.TupleScheme<CCSpecial> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, CCSpecial struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      {
        oprot.writeI32(struct._substationIds.size());
        for (int _iter4 : struct._substationIds)
        {
          oprot.writeI32(_iter4);
        }
      }
      oprot.writeBool(struct._ovUvDisabledFlag);
      oprot.writeDouble(struct._pfDisplayValue);
      oprot.writeDouble(struct._estPfDisplayValue);
      oprot.writeBool(struct._voltReductionControlValue);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, CCSpecial struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.CCPao();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      {
        org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, iprot.readI32());
        struct._substationIds = new java.util.ArrayList<java.lang.Integer>(_list5.size);
        int _elem6;
        for (int _i7 = 0; _i7 < _list5.size; ++_i7)
        {
          _elem6 = iprot.readI32();
          struct._substationIds.add(_elem6);
        }
      }
      struct.set_substationIdsIsSet(true);
      struct._ovUvDisabledFlag = iprot.readBool();
      struct.set_ovUvDisabledFlagIsSet(true);
      struct._pfDisplayValue = iprot.readDouble();
      struct.set_pfDisplayValueIsSet(true);
      struct._estPfDisplayValue = iprot.readDouble();
      struct.set_estPfDisplayValueIsSet(true);
      struct._voltReductionControlValue = iprot.readBool();
      struct.set_voltReductionControlValueIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

