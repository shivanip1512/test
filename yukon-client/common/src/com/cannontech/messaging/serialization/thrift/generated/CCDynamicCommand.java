/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public class CCDynamicCommand implements org.apache.thrift.TBase<CCDynamicCommand, CCDynamicCommand._Fields>, java.io.Serializable, Cloneable, Comparable<CCDynamicCommand> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("CCDynamicCommand");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _COMMAND_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("_commandType", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField _LONG_PARAMETERS_FIELD_DESC = new org.apache.thrift.protocol.TField("_longParameters", org.apache.thrift.protocol.TType.MAP, (short)3);
  private static final org.apache.thrift.protocol.TField _DOUBLE_PARAMETERS_FIELD_DESC = new org.apache.thrift.protocol.TField("_doubleParameters", org.apache.thrift.protocol.TType.MAP, (short)4);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new CCDynamicCommandStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new CCDynamicCommandTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.CCCommand _baseMessage; // required
  private int _commandType; // required
  private @org.apache.thrift.annotation.Nullable java.util.Map<java.lang.Integer,java.lang.Integer> _longParameters; // required
  private @org.apache.thrift.annotation.Nullable java.util.Map<java.lang.Integer,java.lang.Double> _doubleParameters; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _COMMAND_TYPE((short)2, "_commandType"),
    _LONG_PARAMETERS((short)3, "_longParameters"),
    _DOUBLE_PARAMETERS((short)4, "_doubleParameters");

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
        case 2: // _COMMAND_TYPE
          return _COMMAND_TYPE;
        case 3: // _LONG_PARAMETERS
          return _LONG_PARAMETERS;
        case 4: // _DOUBLE_PARAMETERS
          return _DOUBLE_PARAMETERS;
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
  private static final int ___COMMANDTYPE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.CCCommand.class)));
    tmpMap.put(_Fields._COMMAND_TYPE, new org.apache.thrift.meta_data.FieldMetaData("_commandType", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._LONG_PARAMETERS, new org.apache.thrift.meta_data.FieldMetaData("_longParameters", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32), 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
    tmpMap.put(_Fields._DOUBLE_PARAMETERS, new org.apache.thrift.meta_data.FieldMetaData("_doubleParameters", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32), 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(CCDynamicCommand.class, metaDataMap);
  }

  public CCDynamicCommand() {
  }

  public CCDynamicCommand(
    com.cannontech.messaging.serialization.thrift.generated.CCCommand _baseMessage,
    int _commandType,
    java.util.Map<java.lang.Integer,java.lang.Integer> _longParameters,
    java.util.Map<java.lang.Integer,java.lang.Double> _doubleParameters)
  {
    this();
    this._baseMessage = _baseMessage;
    this._commandType = _commandType;
    set_commandTypeIsSet(true);
    this._longParameters = _longParameters;
    this._doubleParameters = _doubleParameters;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CCDynamicCommand(CCDynamicCommand other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.CCCommand(other._baseMessage);
    }
    this._commandType = other._commandType;
    if (other.isSet_longParameters()) {
      java.util.Map<java.lang.Integer,java.lang.Integer> __this___longParameters = new java.util.HashMap<java.lang.Integer,java.lang.Integer>(other._longParameters);
      this._longParameters = __this___longParameters;
    }
    if (other.isSet_doubleParameters()) {
      java.util.Map<java.lang.Integer,java.lang.Double> __this___doubleParameters = new java.util.HashMap<java.lang.Integer,java.lang.Double>(other._doubleParameters);
      this._doubleParameters = __this___doubleParameters;
    }
  }

  public CCDynamicCommand deepCopy() {
    return new CCDynamicCommand(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    set_commandTypeIsSet(false);
    this._commandType = 0;
    this._longParameters = null;
    this._doubleParameters = null;
  }

  @org.apache.thrift.annotation.Nullable
  public com.cannontech.messaging.serialization.thrift.generated.CCCommand get_baseMessage() {
    return this._baseMessage;
  }

  public void set_baseMessage(@org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.CCCommand _baseMessage) {
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

  public int get_commandType() {
    return this._commandType;
  }

  public void set_commandType(int _commandType) {
    this._commandType = _commandType;
    set_commandTypeIsSet(true);
  }

  public void unset_commandType() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___COMMANDTYPE_ISSET_ID);
  }

  /** Returns true if field _commandType is set (has been assigned a value) and false otherwise */
  public boolean isSet_commandType() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___COMMANDTYPE_ISSET_ID);
  }

  public void set_commandTypeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___COMMANDTYPE_ISSET_ID, value);
  }

  public int get_longParametersSize() {
    return (this._longParameters == null) ? 0 : this._longParameters.size();
  }

  public void putTo_longParameters(int key, int val) {
    if (this._longParameters == null) {
      this._longParameters = new java.util.HashMap<java.lang.Integer,java.lang.Integer>();
    }
    this._longParameters.put(key, val);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Map<java.lang.Integer,java.lang.Integer> get_longParameters() {
    return this._longParameters;
  }

  public void set_longParameters(@org.apache.thrift.annotation.Nullable java.util.Map<java.lang.Integer,java.lang.Integer> _longParameters) {
    this._longParameters = _longParameters;
  }

  public void unset_longParameters() {
    this._longParameters = null;
  }

  /** Returns true if field _longParameters is set (has been assigned a value) and false otherwise */
  public boolean isSet_longParameters() {
    return this._longParameters != null;
  }

  public void set_longParametersIsSet(boolean value) {
    if (!value) {
      this._longParameters = null;
    }
  }

  public int get_doubleParametersSize() {
    return (this._doubleParameters == null) ? 0 : this._doubleParameters.size();
  }

  public void putTo_doubleParameters(int key, double val) {
    if (this._doubleParameters == null) {
      this._doubleParameters = new java.util.HashMap<java.lang.Integer,java.lang.Double>();
    }
    this._doubleParameters.put(key, val);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Map<java.lang.Integer,java.lang.Double> get_doubleParameters() {
    return this._doubleParameters;
  }

  public void set_doubleParameters(@org.apache.thrift.annotation.Nullable java.util.Map<java.lang.Integer,java.lang.Double> _doubleParameters) {
    this._doubleParameters = _doubleParameters;
  }

  public void unset_doubleParameters() {
    this._doubleParameters = null;
  }

  /** Returns true if field _doubleParameters is set (has been assigned a value) and false otherwise */
  public boolean isSet_doubleParameters() {
    return this._doubleParameters != null;
  }

  public void set_doubleParametersIsSet(boolean value) {
    if (!value) {
      this._doubleParameters = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _BASE_MESSAGE:
      if (value == null) {
        unset_baseMessage();
      } else {
        set_baseMessage((com.cannontech.messaging.serialization.thrift.generated.CCCommand)value);
      }
      break;

    case _COMMAND_TYPE:
      if (value == null) {
        unset_commandType();
      } else {
        set_commandType((java.lang.Integer)value);
      }
      break;

    case _LONG_PARAMETERS:
      if (value == null) {
        unset_longParameters();
      } else {
        set_longParameters((java.util.Map<java.lang.Integer,java.lang.Integer>)value);
      }
      break;

    case _DOUBLE_PARAMETERS:
      if (value == null) {
        unset_doubleParameters();
      } else {
        set_doubleParameters((java.util.Map<java.lang.Integer,java.lang.Double>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _COMMAND_TYPE:
      return get_commandType();

    case _LONG_PARAMETERS:
      return get_longParameters();

    case _DOUBLE_PARAMETERS:
      return get_doubleParameters();

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
    case _COMMAND_TYPE:
      return isSet_commandType();
    case _LONG_PARAMETERS:
      return isSet_longParameters();
    case _DOUBLE_PARAMETERS:
      return isSet_doubleParameters();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof CCDynamicCommand)
      return this.equals((CCDynamicCommand)that);
    return false;
  }

  public boolean equals(CCDynamicCommand that) {
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

    boolean this_present__commandType = true;
    boolean that_present__commandType = true;
    if (this_present__commandType || that_present__commandType) {
      if (!(this_present__commandType && that_present__commandType))
        return false;
      if (this._commandType != that._commandType)
        return false;
    }

    boolean this_present__longParameters = true && this.isSet_longParameters();
    boolean that_present__longParameters = true && that.isSet_longParameters();
    if (this_present__longParameters || that_present__longParameters) {
      if (!(this_present__longParameters && that_present__longParameters))
        return false;
      if (!this._longParameters.equals(that._longParameters))
        return false;
    }

    boolean this_present__doubleParameters = true && this.isSet_doubleParameters();
    boolean that_present__doubleParameters = true && that.isSet_doubleParameters();
    if (this_present__doubleParameters || that_present__doubleParameters) {
      if (!(this_present__doubleParameters && that_present__doubleParameters))
        return false;
      if (!this._doubleParameters.equals(that._doubleParameters))
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

    hashCode = hashCode * 8191 + _commandType;

    hashCode = hashCode * 8191 + ((isSet_longParameters()) ? 131071 : 524287);
    if (isSet_longParameters())
      hashCode = hashCode * 8191 + _longParameters.hashCode();

    hashCode = hashCode * 8191 + ((isSet_doubleParameters()) ? 131071 : 524287);
    if (isSet_doubleParameters())
      hashCode = hashCode * 8191 + _doubleParameters.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(CCDynamicCommand other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_commandType()).compareTo(other.isSet_commandType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_commandType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._commandType, other._commandType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_longParameters()).compareTo(other.isSet_longParameters());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_longParameters()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._longParameters, other._longParameters);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_doubleParameters()).compareTo(other.isSet_doubleParameters());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_doubleParameters()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._doubleParameters, other._doubleParameters);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("CCDynamicCommand(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_commandType:");
    sb.append(this._commandType);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_longParameters:");
    if (this._longParameters == null) {
      sb.append("null");
    } else {
      sb.append(this._longParameters);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_doubleParameters:");
    if (this._doubleParameters == null) {
      sb.append("null");
    } else {
      sb.append(this._doubleParameters);
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

    if (!isSet_commandType()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_commandType' is unset! Struct:" + toString());
    }

    if (!isSet_longParameters()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_longParameters' is unset! Struct:" + toString());
    }

    if (!isSet_doubleParameters()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_doubleParameters' is unset! Struct:" + toString());
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

  private static class CCDynamicCommandStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CCDynamicCommandStandardScheme getScheme() {
      return new CCDynamicCommandStandardScheme();
    }
  }

  private static class CCDynamicCommandStandardScheme extends org.apache.thrift.scheme.StandardScheme<CCDynamicCommand> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, CCDynamicCommand struct) throws org.apache.thrift.TException {
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
              struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.CCCommand();
              struct._baseMessage.read(iprot);
              struct.set_baseMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _COMMAND_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._commandType = iprot.readI32();
              struct.set_commandTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _LONG_PARAMETERS
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map0 = iprot.readMapBegin();
                struct._longParameters = new java.util.HashMap<java.lang.Integer,java.lang.Integer>(2*_map0.size);
                int _key1;
                int _val2;
                for (int _i3 = 0; _i3 < _map0.size; ++_i3)
                {
                  _key1 = iprot.readI32();
                  _val2 = iprot.readI32();
                  struct._longParameters.put(_key1, _val2);
                }
                iprot.readMapEnd();
              }
              struct.set_longParametersIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _DOUBLE_PARAMETERS
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map4 = iprot.readMapBegin();
                struct._doubleParameters = new java.util.HashMap<java.lang.Integer,java.lang.Double>(2*_map4.size);
                int _key5;
                double _val6;
                for (int _i7 = 0; _i7 < _map4.size; ++_i7)
                {
                  _key5 = iprot.readI32();
                  _val6 = iprot.readDouble();
                  struct._doubleParameters.put(_key5, _val6);
                }
                iprot.readMapEnd();
              }
              struct.set_doubleParametersIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, CCDynamicCommand struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_COMMAND_TYPE_FIELD_DESC);
      oprot.writeI32(struct._commandType);
      oprot.writeFieldEnd();
      if (struct._longParameters != null) {
        oprot.writeFieldBegin(_LONG_PARAMETERS_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.I32, struct._longParameters.size()));
          for (java.util.Map.Entry<java.lang.Integer, java.lang.Integer> _iter8 : struct._longParameters.entrySet())
          {
            oprot.writeI32(_iter8.getKey());
            oprot.writeI32(_iter8.getValue());
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct._doubleParameters != null) {
        oprot.writeFieldBegin(_DOUBLE_PARAMETERS_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.DOUBLE, struct._doubleParameters.size()));
          for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> _iter9 : struct._doubleParameters.entrySet())
          {
            oprot.writeI32(_iter9.getKey());
            oprot.writeDouble(_iter9.getValue());
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CCDynamicCommandTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CCDynamicCommandTupleScheme getScheme() {
      return new CCDynamicCommandTupleScheme();
    }
  }

  private static class CCDynamicCommandTupleScheme extends org.apache.thrift.scheme.TupleScheme<CCDynamicCommand> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, CCDynamicCommand struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeI32(struct._commandType);
      {
        oprot.writeI32(struct._longParameters.size());
        for (java.util.Map.Entry<java.lang.Integer, java.lang.Integer> _iter10 : struct._longParameters.entrySet())
        {
          oprot.writeI32(_iter10.getKey());
          oprot.writeI32(_iter10.getValue());
        }
      }
      {
        oprot.writeI32(struct._doubleParameters.size());
        for (java.util.Map.Entry<java.lang.Integer, java.lang.Double> _iter11 : struct._doubleParameters.entrySet())
        {
          oprot.writeI32(_iter11.getKey());
          oprot.writeDouble(_iter11.getValue());
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, CCDynamicCommand struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.CCCommand();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._commandType = iprot.readI32();
      struct.set_commandTypeIsSet(true);
      {
        org.apache.thrift.protocol.TMap _map12 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.I32, iprot.readI32());
        struct._longParameters = new java.util.HashMap<java.lang.Integer,java.lang.Integer>(2*_map12.size);
        int _key13;
        int _val14;
        for (int _i15 = 0; _i15 < _map12.size; ++_i15)
        {
          _key13 = iprot.readI32();
          _val14 = iprot.readI32();
          struct._longParameters.put(_key13, _val14);
        }
      }
      struct.set_longParametersIsSet(true);
      {
        org.apache.thrift.protocol.TMap _map16 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.DOUBLE, iprot.readI32());
        struct._doubleParameters = new java.util.HashMap<java.lang.Integer,java.lang.Double>(2*_map16.size);
        int _key17;
        double _val18;
        for (int _i19 = 0; _i19 < _map16.size; ++_i19)
        {
          _key17 = iprot.readI32();
          _val18 = iprot.readDouble();
          struct._doubleParameters.put(_key17, _val18);
        }
      }
      struct.set_doubleParametersIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

