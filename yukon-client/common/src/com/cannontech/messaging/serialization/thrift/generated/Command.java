/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public class Command implements org.apache.thrift.TBase<Command, Command._Fields>, java.io.Serializable, Cloneable, Comparable<Command> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Command");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _OPERATION_FIELD_DESC = new org.apache.thrift.protocol.TField("_operation", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField _OP_STRING_FIELD_DESC = new org.apache.thrift.protocol.TField("_opString", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField _OP_ARG_COUNT_FIELD_DESC = new org.apache.thrift.protocol.TField("_opArgCount", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField _OP_ARG_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("_opArgList", org.apache.thrift.protocol.TType.LIST, (short)5);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new CommandStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new CommandTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private int _operation; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _opString; // required
  private int _opArgCount; // required
  private @org.apache.thrift.annotation.Nullable java.util.List<java.lang.Integer> _opArgList; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _OPERATION((short)2, "_operation"),
    _OP_STRING((short)3, "_opString"),
    _OP_ARG_COUNT((short)4, "_opArgCount"),
    _OP_ARG_LIST((short)5, "_opArgList");

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
        case 2: // _OPERATION
          return _OPERATION;
        case 3: // _OP_STRING
          return _OP_STRING;
        case 4: // _OP_ARG_COUNT
          return _OP_ARG_COUNT;
        case 5: // _OP_ARG_LIST
          return _OP_ARG_LIST;
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
  private static final int ___OPERATION_ISSET_ID = 0;
  private static final int ___OPARGCOUNT_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._OPERATION, new org.apache.thrift.meta_data.FieldMetaData("_operation", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._OP_STRING, new org.apache.thrift.meta_data.FieldMetaData("_opString", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._OP_ARG_COUNT, new org.apache.thrift.meta_data.FieldMetaData("_opArgCount", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._OP_ARG_LIST, new org.apache.thrift.meta_data.FieldMetaData("_opArgList", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Command.class, metaDataMap);
  }

  public Command() {
  }

  public Command(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    int _operation,
    java.lang.String _opString,
    int _opArgCount,
    java.util.List<java.lang.Integer> _opArgList)
  {
    this();
    this._baseMessage = _baseMessage;
    this._operation = _operation;
    set_operationIsSet(true);
    this._opString = _opString;
    this._opArgCount = _opArgCount;
    set_opArgCountIsSet(true);
    this._opArgList = _opArgList;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Command(Command other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    this._operation = other._operation;
    if (other.isSet_opString()) {
      this._opString = other._opString;
    }
    this._opArgCount = other._opArgCount;
    if (other.isSet_opArgList()) {
      java.util.List<java.lang.Integer> __this___opArgList = new java.util.ArrayList<java.lang.Integer>(other._opArgList);
      this._opArgList = __this___opArgList;
    }
  }

  public Command deepCopy() {
    return new Command(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    set_operationIsSet(false);
    this._operation = 0;
    this._opString = null;
    set_opArgCountIsSet(false);
    this._opArgCount = 0;
    this._opArgList = null;
  }

  @org.apache.thrift.annotation.Nullable
  public com.cannontech.messaging.serialization.thrift.generated.Message get_baseMessage() {
    return this._baseMessage;
  }

  public void set_baseMessage(@org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage) {
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

  public int get_operation() {
    return this._operation;
  }

  public void set_operation(int _operation) {
    this._operation = _operation;
    set_operationIsSet(true);
  }

  public void unset_operation() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___OPERATION_ISSET_ID);
  }

  /** Returns true if field _operation is set (has been assigned a value) and false otherwise */
  public boolean isSet_operation() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___OPERATION_ISSET_ID);
  }

  public void set_operationIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___OPERATION_ISSET_ID, value);
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_opString() {
    return this._opString;
  }

  public void set_opString(@org.apache.thrift.annotation.Nullable java.lang.String _opString) {
    this._opString = _opString;
  }

  public void unset_opString() {
    this._opString = null;
  }

  /** Returns true if field _opString is set (has been assigned a value) and false otherwise */
  public boolean isSet_opString() {
    return this._opString != null;
  }

  public void set_opStringIsSet(boolean value) {
    if (!value) {
      this._opString = null;
    }
  }

  public int get_opArgCount() {
    return this._opArgCount;
  }

  public void set_opArgCount(int _opArgCount) {
    this._opArgCount = _opArgCount;
    set_opArgCountIsSet(true);
  }

  public void unset_opArgCount() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___OPARGCOUNT_ISSET_ID);
  }

  /** Returns true if field _opArgCount is set (has been assigned a value) and false otherwise */
  public boolean isSet_opArgCount() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___OPARGCOUNT_ISSET_ID);
  }

  public void set_opArgCountIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___OPARGCOUNT_ISSET_ID, value);
  }

  public int get_opArgListSize() {
    return (this._opArgList == null) ? 0 : this._opArgList.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<java.lang.Integer> get_opArgListIterator() {
    return (this._opArgList == null) ? null : this._opArgList.iterator();
  }

  public void addTo_opArgList(int elem) {
    if (this._opArgList == null) {
      this._opArgList = new java.util.ArrayList<java.lang.Integer>();
    }
    this._opArgList.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<java.lang.Integer> get_opArgList() {
    return this._opArgList;
  }

  public void set_opArgList(@org.apache.thrift.annotation.Nullable java.util.List<java.lang.Integer> _opArgList) {
    this._opArgList = _opArgList;
  }

  public void unset_opArgList() {
    this._opArgList = null;
  }

  /** Returns true if field _opArgList is set (has been assigned a value) and false otherwise */
  public boolean isSet_opArgList() {
    return this._opArgList != null;
  }

  public void set_opArgListIsSet(boolean value) {
    if (!value) {
      this._opArgList = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _BASE_MESSAGE:
      if (value == null) {
        unset_baseMessage();
      } else {
        set_baseMessage((com.cannontech.messaging.serialization.thrift.generated.Message)value);
      }
      break;

    case _OPERATION:
      if (value == null) {
        unset_operation();
      } else {
        set_operation((java.lang.Integer)value);
      }
      break;

    case _OP_STRING:
      if (value == null) {
        unset_opString();
      } else {
        set_opString((java.lang.String)value);
      }
      break;

    case _OP_ARG_COUNT:
      if (value == null) {
        unset_opArgCount();
      } else {
        set_opArgCount((java.lang.Integer)value);
      }
      break;

    case _OP_ARG_LIST:
      if (value == null) {
        unset_opArgList();
      } else {
        set_opArgList((java.util.List<java.lang.Integer>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _OPERATION:
      return get_operation();

    case _OP_STRING:
      return get_opString();

    case _OP_ARG_COUNT:
      return get_opArgCount();

    case _OP_ARG_LIST:
      return get_opArgList();

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
    case _OPERATION:
      return isSet_operation();
    case _OP_STRING:
      return isSet_opString();
    case _OP_ARG_COUNT:
      return isSet_opArgCount();
    case _OP_ARG_LIST:
      return isSet_opArgList();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof Command)
      return this.equals((Command)that);
    return false;
  }

  public boolean equals(Command that) {
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

    boolean this_present__operation = true;
    boolean that_present__operation = true;
    if (this_present__operation || that_present__operation) {
      if (!(this_present__operation && that_present__operation))
        return false;
      if (this._operation != that._operation)
        return false;
    }

    boolean this_present__opString = true && this.isSet_opString();
    boolean that_present__opString = true && that.isSet_opString();
    if (this_present__opString || that_present__opString) {
      if (!(this_present__opString && that_present__opString))
        return false;
      if (!this._opString.equals(that._opString))
        return false;
    }

    boolean this_present__opArgCount = true;
    boolean that_present__opArgCount = true;
    if (this_present__opArgCount || that_present__opArgCount) {
      if (!(this_present__opArgCount && that_present__opArgCount))
        return false;
      if (this._opArgCount != that._opArgCount)
        return false;
    }

    boolean this_present__opArgList = true && this.isSet_opArgList();
    boolean that_present__opArgList = true && that.isSet_opArgList();
    if (this_present__opArgList || that_present__opArgList) {
      if (!(this_present__opArgList && that_present__opArgList))
        return false;
      if (!this._opArgList.equals(that._opArgList))
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

    hashCode = hashCode * 8191 + _operation;

    hashCode = hashCode * 8191 + ((isSet_opString()) ? 131071 : 524287);
    if (isSet_opString())
      hashCode = hashCode * 8191 + _opString.hashCode();

    hashCode = hashCode * 8191 + _opArgCount;

    hashCode = hashCode * 8191 + ((isSet_opArgList()) ? 131071 : 524287);
    if (isSet_opArgList())
      hashCode = hashCode * 8191 + _opArgList.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(Command other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_operation()).compareTo(other.isSet_operation());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_operation()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._operation, other._operation);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_opString()).compareTo(other.isSet_opString());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_opString()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._opString, other._opString);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_opArgCount()).compareTo(other.isSet_opArgCount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_opArgCount()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._opArgCount, other._opArgCount);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_opArgList()).compareTo(other.isSet_opArgList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_opArgList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._opArgList, other._opArgList);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("Command(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_operation:");
    sb.append(this._operation);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_opString:");
    if (this._opString == null) {
      sb.append("null");
    } else {
      sb.append(this._opString);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_opArgCount:");
    sb.append(this._opArgCount);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_opArgList:");
    if (this._opArgList == null) {
      sb.append("null");
    } else {
      sb.append(this._opArgList);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_baseMessage()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_baseMessage' is unset! Struct:" + toString());
    }

    if (!isSet_operation()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_operation' is unset! Struct:" + toString());
    }

    if (!isSet_opString()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_opString' is unset! Struct:" + toString());
    }

    if (!isSet_opArgCount()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_opArgCount' is unset! Struct:" + toString());
    }

    if (!isSet_opArgList()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_opArgList' is unset! Struct:" + toString());
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

  private static class CommandStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CommandStandardScheme getScheme() {
      return new CommandStandardScheme();
    }
  }

  private static class CommandStandardScheme extends org.apache.thrift.scheme.StandardScheme<Command> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Command struct) throws org.apache.thrift.TException {
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
              struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
              struct._baseMessage.read(iprot);
              struct.set_baseMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _OPERATION
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._operation = iprot.readI32();
              struct.set_operationIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _OP_STRING
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._opString = iprot.readString();
              struct.set_opStringIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _OP_ARG_COUNT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._opArgCount = iprot.readI32();
              struct.set_opArgCountIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // _OP_ARG_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct._opArgList = new java.util.ArrayList<java.lang.Integer>(_list0.size);
                int _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = iprot.readI32();
                  struct._opArgList.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.set_opArgListIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, Command struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_OPERATION_FIELD_DESC);
      oprot.writeI32(struct._operation);
      oprot.writeFieldEnd();
      if (struct._opString != null) {
        oprot.writeFieldBegin(_OP_STRING_FIELD_DESC);
        oprot.writeString(struct._opString);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_OP_ARG_COUNT_FIELD_DESC);
      oprot.writeI32(struct._opArgCount);
      oprot.writeFieldEnd();
      if (struct._opArgList != null) {
        oprot.writeFieldBegin(_OP_ARG_LIST_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, struct._opArgList.size()));
          for (int _iter3 : struct._opArgList)
          {
            oprot.writeI32(_iter3);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CommandTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CommandTupleScheme getScheme() {
      return new CommandTupleScheme();
    }
  }

  private static class CommandTupleScheme extends org.apache.thrift.scheme.TupleScheme<Command> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Command struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeI32(struct._operation);
      oprot.writeString(struct._opString);
      oprot.writeI32(struct._opArgCount);
      {
        oprot.writeI32(struct._opArgList.size());
        for (int _iter4 : struct._opArgList)
        {
          oprot.writeI32(_iter4);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Command struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._operation = iprot.readI32();
      struct.set_operationIsSet(true);
      struct._opString = iprot.readString();
      struct.set_opStringIsSet(true);
      struct._opArgCount = iprot.readI32();
      struct.set_opArgCountIsSet(true);
      {
        org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, iprot.readI32());
        struct._opArgList = new java.util.ArrayList<java.lang.Integer>(_list5.size);
        int _elem6;
        for (int _i7 = 0; _i7 < _list5.size; ++_i7)
        {
          _elem6 = iprot.readI32();
          struct._opArgList.add(_elem6);
        }
      }
      struct.set_opArgListIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

