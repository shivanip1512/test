/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public class CCCapBankMove implements org.apache.thrift.TBase<CCCapBankMove, CCCapBankMove._Fields>, java.io.Serializable, Cloneable, Comparable<CCCapBankMove> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("CCCapBankMove");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _PERMANENT_FLAG_FIELD_DESC = new org.apache.thrift.protocol.TField("_permanentFlag", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField _OLD_FEEDER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_oldFeederId", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField _NEW_FEEDER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_newFeederId", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField _CAP_SWITCHING_ORDER_FIELD_DESC = new org.apache.thrift.protocol.TField("_capSwitchingOrder", org.apache.thrift.protocol.TType.DOUBLE, (short)5);
  private static final org.apache.thrift.protocol.TField _CLOSE_ORDER_FIELD_DESC = new org.apache.thrift.protocol.TField("_closeOrder", org.apache.thrift.protocol.TType.DOUBLE, (short)6);
  private static final org.apache.thrift.protocol.TField _TRIP_ORDER_FIELD_DESC = new org.apache.thrift.protocol.TField("_tripOrder", org.apache.thrift.protocol.TType.DOUBLE, (short)7);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new CCCapBankMoveStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new CCCapBankMoveTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.CCItemCommand _baseMessage; // required
  private int _permanentFlag; // required
  private int _oldFeederId; // required
  private int _newFeederId; // required
  private double _capSwitchingOrder; // required
  private double _closeOrder; // required
  private double _tripOrder; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _PERMANENT_FLAG((short)2, "_permanentFlag"),
    _OLD_FEEDER_ID((short)3, "_oldFeederId"),
    _NEW_FEEDER_ID((short)4, "_newFeederId"),
    _CAP_SWITCHING_ORDER((short)5, "_capSwitchingOrder"),
    _CLOSE_ORDER((short)6, "_closeOrder"),
    _TRIP_ORDER((short)7, "_tripOrder");

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
        case 2: // _PERMANENT_FLAG
          return _PERMANENT_FLAG;
        case 3: // _OLD_FEEDER_ID
          return _OLD_FEEDER_ID;
        case 4: // _NEW_FEEDER_ID
          return _NEW_FEEDER_ID;
        case 5: // _CAP_SWITCHING_ORDER
          return _CAP_SWITCHING_ORDER;
        case 6: // _CLOSE_ORDER
          return _CLOSE_ORDER;
        case 7: // _TRIP_ORDER
          return _TRIP_ORDER;
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
  private static final int ___PERMANENTFLAG_ISSET_ID = 0;
  private static final int ___OLDFEEDERID_ISSET_ID = 1;
  private static final int ___NEWFEEDERID_ISSET_ID = 2;
  private static final int ___CAPSWITCHINGORDER_ISSET_ID = 3;
  private static final int ___CLOSEORDER_ISSET_ID = 4;
  private static final int ___TRIPORDER_ISSET_ID = 5;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.CCItemCommand.class)));
    tmpMap.put(_Fields._PERMANENT_FLAG, new org.apache.thrift.meta_data.FieldMetaData("_permanentFlag", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._OLD_FEEDER_ID, new org.apache.thrift.meta_data.FieldMetaData("_oldFeederId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._NEW_FEEDER_ID, new org.apache.thrift.meta_data.FieldMetaData("_newFeederId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._CAP_SWITCHING_ORDER, new org.apache.thrift.meta_data.FieldMetaData("_capSwitchingOrder", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields._CLOSE_ORDER, new org.apache.thrift.meta_data.FieldMetaData("_closeOrder", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields._TRIP_ORDER, new org.apache.thrift.meta_data.FieldMetaData("_tripOrder", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(CCCapBankMove.class, metaDataMap);
  }

  public CCCapBankMove() {
  }

  public CCCapBankMove(
    com.cannontech.messaging.serialization.thrift.generated.CCItemCommand _baseMessage,
    int _permanentFlag,
    int _oldFeederId,
    int _newFeederId,
    double _capSwitchingOrder,
    double _closeOrder,
    double _tripOrder)
  {
    this();
    this._baseMessage = _baseMessage;
    this._permanentFlag = _permanentFlag;
    set_permanentFlagIsSet(true);
    this._oldFeederId = _oldFeederId;
    set_oldFeederIdIsSet(true);
    this._newFeederId = _newFeederId;
    set_newFeederIdIsSet(true);
    this._capSwitchingOrder = _capSwitchingOrder;
    set_capSwitchingOrderIsSet(true);
    this._closeOrder = _closeOrder;
    set_closeOrderIsSet(true);
    this._tripOrder = _tripOrder;
    set_tripOrderIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CCCapBankMove(CCCapBankMove other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.CCItemCommand(other._baseMessage);
    }
    this._permanentFlag = other._permanentFlag;
    this._oldFeederId = other._oldFeederId;
    this._newFeederId = other._newFeederId;
    this._capSwitchingOrder = other._capSwitchingOrder;
    this._closeOrder = other._closeOrder;
    this._tripOrder = other._tripOrder;
  }

  public CCCapBankMove deepCopy() {
    return new CCCapBankMove(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    set_permanentFlagIsSet(false);
    this._permanentFlag = 0;
    set_oldFeederIdIsSet(false);
    this._oldFeederId = 0;
    set_newFeederIdIsSet(false);
    this._newFeederId = 0;
    set_capSwitchingOrderIsSet(false);
    this._capSwitchingOrder = 0.0;
    set_closeOrderIsSet(false);
    this._closeOrder = 0.0;
    set_tripOrderIsSet(false);
    this._tripOrder = 0.0;
  }

  @org.apache.thrift.annotation.Nullable
  public com.cannontech.messaging.serialization.thrift.generated.CCItemCommand get_baseMessage() {
    return this._baseMessage;
  }

  public void set_baseMessage(@org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.CCItemCommand _baseMessage) {
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

  public int get_permanentFlag() {
    return this._permanentFlag;
  }

  public void set_permanentFlag(int _permanentFlag) {
    this._permanentFlag = _permanentFlag;
    set_permanentFlagIsSet(true);
  }

  public void unset_permanentFlag() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___PERMANENTFLAG_ISSET_ID);
  }

  /** Returns true if field _permanentFlag is set (has been assigned a value) and false otherwise */
  public boolean isSet_permanentFlag() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___PERMANENTFLAG_ISSET_ID);
  }

  public void set_permanentFlagIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___PERMANENTFLAG_ISSET_ID, value);
  }

  public int get_oldFeederId() {
    return this._oldFeederId;
  }

  public void set_oldFeederId(int _oldFeederId) {
    this._oldFeederId = _oldFeederId;
    set_oldFeederIdIsSet(true);
  }

  public void unset_oldFeederId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___OLDFEEDERID_ISSET_ID);
  }

  /** Returns true if field _oldFeederId is set (has been assigned a value) and false otherwise */
  public boolean isSet_oldFeederId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___OLDFEEDERID_ISSET_ID);
  }

  public void set_oldFeederIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___OLDFEEDERID_ISSET_ID, value);
  }

  public int get_newFeederId() {
    return this._newFeederId;
  }

  public void set_newFeederId(int _newFeederId) {
    this._newFeederId = _newFeederId;
    set_newFeederIdIsSet(true);
  }

  public void unset_newFeederId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___NEWFEEDERID_ISSET_ID);
  }

  /** Returns true if field _newFeederId is set (has been assigned a value) and false otherwise */
  public boolean isSet_newFeederId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___NEWFEEDERID_ISSET_ID);
  }

  public void set_newFeederIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___NEWFEEDERID_ISSET_ID, value);
  }

  public double get_capSwitchingOrder() {
    return this._capSwitchingOrder;
  }

  public void set_capSwitchingOrder(double _capSwitchingOrder) {
    this._capSwitchingOrder = _capSwitchingOrder;
    set_capSwitchingOrderIsSet(true);
  }

  public void unset_capSwitchingOrder() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___CAPSWITCHINGORDER_ISSET_ID);
  }

  /** Returns true if field _capSwitchingOrder is set (has been assigned a value) and false otherwise */
  public boolean isSet_capSwitchingOrder() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___CAPSWITCHINGORDER_ISSET_ID);
  }

  public void set_capSwitchingOrderIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___CAPSWITCHINGORDER_ISSET_ID, value);
  }

  public double get_closeOrder() {
    return this._closeOrder;
  }

  public void set_closeOrder(double _closeOrder) {
    this._closeOrder = _closeOrder;
    set_closeOrderIsSet(true);
  }

  public void unset_closeOrder() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___CLOSEORDER_ISSET_ID);
  }

  /** Returns true if field _closeOrder is set (has been assigned a value) and false otherwise */
  public boolean isSet_closeOrder() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___CLOSEORDER_ISSET_ID);
  }

  public void set_closeOrderIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___CLOSEORDER_ISSET_ID, value);
  }

  public double get_tripOrder() {
    return this._tripOrder;
  }

  public void set_tripOrder(double _tripOrder) {
    this._tripOrder = _tripOrder;
    set_tripOrderIsSet(true);
  }

  public void unset_tripOrder() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___TRIPORDER_ISSET_ID);
  }

  /** Returns true if field _tripOrder is set (has been assigned a value) and false otherwise */
  public boolean isSet_tripOrder() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___TRIPORDER_ISSET_ID);
  }

  public void set_tripOrderIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___TRIPORDER_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _BASE_MESSAGE:
      if (value == null) {
        unset_baseMessage();
      } else {
        set_baseMessage((com.cannontech.messaging.serialization.thrift.generated.CCItemCommand)value);
      }
      break;

    case _PERMANENT_FLAG:
      if (value == null) {
        unset_permanentFlag();
      } else {
        set_permanentFlag((java.lang.Integer)value);
      }
      break;

    case _OLD_FEEDER_ID:
      if (value == null) {
        unset_oldFeederId();
      } else {
        set_oldFeederId((java.lang.Integer)value);
      }
      break;

    case _NEW_FEEDER_ID:
      if (value == null) {
        unset_newFeederId();
      } else {
        set_newFeederId((java.lang.Integer)value);
      }
      break;

    case _CAP_SWITCHING_ORDER:
      if (value == null) {
        unset_capSwitchingOrder();
      } else {
        set_capSwitchingOrder((java.lang.Double)value);
      }
      break;

    case _CLOSE_ORDER:
      if (value == null) {
        unset_closeOrder();
      } else {
        set_closeOrder((java.lang.Double)value);
      }
      break;

    case _TRIP_ORDER:
      if (value == null) {
        unset_tripOrder();
      } else {
        set_tripOrder((java.lang.Double)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _PERMANENT_FLAG:
      return get_permanentFlag();

    case _OLD_FEEDER_ID:
      return get_oldFeederId();

    case _NEW_FEEDER_ID:
      return get_newFeederId();

    case _CAP_SWITCHING_ORDER:
      return get_capSwitchingOrder();

    case _CLOSE_ORDER:
      return get_closeOrder();

    case _TRIP_ORDER:
      return get_tripOrder();

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
    case _PERMANENT_FLAG:
      return isSet_permanentFlag();
    case _OLD_FEEDER_ID:
      return isSet_oldFeederId();
    case _NEW_FEEDER_ID:
      return isSet_newFeederId();
    case _CAP_SWITCHING_ORDER:
      return isSet_capSwitchingOrder();
    case _CLOSE_ORDER:
      return isSet_closeOrder();
    case _TRIP_ORDER:
      return isSet_tripOrder();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof CCCapBankMove)
      return this.equals((CCCapBankMove)that);
    return false;
  }

  public boolean equals(CCCapBankMove that) {
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

    boolean this_present__permanentFlag = true;
    boolean that_present__permanentFlag = true;
    if (this_present__permanentFlag || that_present__permanentFlag) {
      if (!(this_present__permanentFlag && that_present__permanentFlag))
        return false;
      if (this._permanentFlag != that._permanentFlag)
        return false;
    }

    boolean this_present__oldFeederId = true;
    boolean that_present__oldFeederId = true;
    if (this_present__oldFeederId || that_present__oldFeederId) {
      if (!(this_present__oldFeederId && that_present__oldFeederId))
        return false;
      if (this._oldFeederId != that._oldFeederId)
        return false;
    }

    boolean this_present__newFeederId = true;
    boolean that_present__newFeederId = true;
    if (this_present__newFeederId || that_present__newFeederId) {
      if (!(this_present__newFeederId && that_present__newFeederId))
        return false;
      if (this._newFeederId != that._newFeederId)
        return false;
    }

    boolean this_present__capSwitchingOrder = true;
    boolean that_present__capSwitchingOrder = true;
    if (this_present__capSwitchingOrder || that_present__capSwitchingOrder) {
      if (!(this_present__capSwitchingOrder && that_present__capSwitchingOrder))
        return false;
      if (this._capSwitchingOrder != that._capSwitchingOrder)
        return false;
    }

    boolean this_present__closeOrder = true;
    boolean that_present__closeOrder = true;
    if (this_present__closeOrder || that_present__closeOrder) {
      if (!(this_present__closeOrder && that_present__closeOrder))
        return false;
      if (this._closeOrder != that._closeOrder)
        return false;
    }

    boolean this_present__tripOrder = true;
    boolean that_present__tripOrder = true;
    if (this_present__tripOrder || that_present__tripOrder) {
      if (!(this_present__tripOrder && that_present__tripOrder))
        return false;
      if (this._tripOrder != that._tripOrder)
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

    hashCode = hashCode * 8191 + _permanentFlag;

    hashCode = hashCode * 8191 + _oldFeederId;

    hashCode = hashCode * 8191 + _newFeederId;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_capSwitchingOrder);

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_closeOrder);

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_tripOrder);

    return hashCode;
  }

  @Override
  public int compareTo(CCCapBankMove other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_permanentFlag()).compareTo(other.isSet_permanentFlag());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_permanentFlag()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._permanentFlag, other._permanentFlag);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_oldFeederId()).compareTo(other.isSet_oldFeederId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_oldFeederId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._oldFeederId, other._oldFeederId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_newFeederId()).compareTo(other.isSet_newFeederId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_newFeederId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._newFeederId, other._newFeederId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_capSwitchingOrder()).compareTo(other.isSet_capSwitchingOrder());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_capSwitchingOrder()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._capSwitchingOrder, other._capSwitchingOrder);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_closeOrder()).compareTo(other.isSet_closeOrder());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_closeOrder()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._closeOrder, other._closeOrder);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_tripOrder()).compareTo(other.isSet_tripOrder());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_tripOrder()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._tripOrder, other._tripOrder);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("CCCapBankMove(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_permanentFlag:");
    sb.append(this._permanentFlag);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_oldFeederId:");
    sb.append(this._oldFeederId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_newFeederId:");
    sb.append(this._newFeederId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_capSwitchingOrder:");
    sb.append(this._capSwitchingOrder);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_closeOrder:");
    sb.append(this._closeOrder);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_tripOrder:");
    sb.append(this._tripOrder);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_baseMessage()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_baseMessage' is unset! Struct:" + toString());
    }

    if (!isSet_permanentFlag()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_permanentFlag' is unset! Struct:" + toString());
    }

    if (!isSet_oldFeederId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_oldFeederId' is unset! Struct:" + toString());
    }

    if (!isSet_newFeederId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_newFeederId' is unset! Struct:" + toString());
    }

    if (!isSet_capSwitchingOrder()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_capSwitchingOrder' is unset! Struct:" + toString());
    }

    if (!isSet_closeOrder()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_closeOrder' is unset! Struct:" + toString());
    }

    if (!isSet_tripOrder()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_tripOrder' is unset! Struct:" + toString());
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

  private static class CCCapBankMoveStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CCCapBankMoveStandardScheme getScheme() {
      return new CCCapBankMoveStandardScheme();
    }
  }

  private static class CCCapBankMoveStandardScheme extends org.apache.thrift.scheme.StandardScheme<CCCapBankMove> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, CCCapBankMove struct) throws org.apache.thrift.TException {
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
              struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.CCItemCommand();
              struct._baseMessage.read(iprot);
              struct.set_baseMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _PERMANENT_FLAG
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._permanentFlag = iprot.readI32();
              struct.set_permanentFlagIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _OLD_FEEDER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._oldFeederId = iprot.readI32();
              struct.set_oldFeederIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _NEW_FEEDER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._newFeederId = iprot.readI32();
              struct.set_newFeederIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // _CAP_SWITCHING_ORDER
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct._capSwitchingOrder = iprot.readDouble();
              struct.set_capSwitchingOrderIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // _CLOSE_ORDER
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct._closeOrder = iprot.readDouble();
              struct.set_closeOrderIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // _TRIP_ORDER
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct._tripOrder = iprot.readDouble();
              struct.set_tripOrderIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, CCCapBankMove struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_PERMANENT_FLAG_FIELD_DESC);
      oprot.writeI32(struct._permanentFlag);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_OLD_FEEDER_ID_FIELD_DESC);
      oprot.writeI32(struct._oldFeederId);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_NEW_FEEDER_ID_FIELD_DESC);
      oprot.writeI32(struct._newFeederId);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_CAP_SWITCHING_ORDER_FIELD_DESC);
      oprot.writeDouble(struct._capSwitchingOrder);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_CLOSE_ORDER_FIELD_DESC);
      oprot.writeDouble(struct._closeOrder);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_TRIP_ORDER_FIELD_DESC);
      oprot.writeDouble(struct._tripOrder);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CCCapBankMoveTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CCCapBankMoveTupleScheme getScheme() {
      return new CCCapBankMoveTupleScheme();
    }
  }

  private static class CCCapBankMoveTupleScheme extends org.apache.thrift.scheme.TupleScheme<CCCapBankMove> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, CCCapBankMove struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeI32(struct._permanentFlag);
      oprot.writeI32(struct._oldFeederId);
      oprot.writeI32(struct._newFeederId);
      oprot.writeDouble(struct._capSwitchingOrder);
      oprot.writeDouble(struct._closeOrder);
      oprot.writeDouble(struct._tripOrder);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, CCCapBankMove struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.CCItemCommand();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._permanentFlag = iprot.readI32();
      struct.set_permanentFlagIsSet(true);
      struct._oldFeederId = iprot.readI32();
      struct.set_oldFeederIdIsSet(true);
      struct._newFeederId = iprot.readI32();
      struct.set_newFeederIdIsSet(true);
      struct._capSwitchingOrder = iprot.readDouble();
      struct.set_capSwitchingOrderIsSet(true);
      struct._closeOrder = iprot.readDouble();
      struct.set_closeOrderIsSet(true);
      struct._tripOrder = iprot.readDouble();
      struct.set_tripOrderIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

