/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-09-24")
public class Trace implements org.apache.thrift.TBase<Trace, Trace._Fields>, java.io.Serializable, Cloneable, Comparable<Trace> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Trace");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _END_FIELD_DESC = new org.apache.thrift.protocol.TField("_end", org.apache.thrift.protocol.TType.BOOL, (short)2);
  private static final org.apache.thrift.protocol.TField _ATTRIBUTES_FIELD_DESC = new org.apache.thrift.protocol.TField("_attributes", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField _TRACE_FIELD_DESC = new org.apache.thrift.protocol.TField("_trace", org.apache.thrift.protocol.TType.STRING, (short)4);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new TraceStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new TraceTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private boolean _end; // required
  private int _attributes; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _trace; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _END((short)2, "_end"),
    _ATTRIBUTES((short)3, "_attributes"),
    _TRACE((short)4, "_trace");

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
        case 2: // _END
          return _END;
        case 3: // _ATTRIBUTES
          return _ATTRIBUTES;
        case 4: // _TRACE
          return _TRACE;
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
  private static final int ___END_ISSET_ID = 0;
  private static final int ___ATTRIBUTES_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._END, new org.apache.thrift.meta_data.FieldMetaData("_end", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields._ATTRIBUTES, new org.apache.thrift.meta_data.FieldMetaData("_attributes", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._TRACE, new org.apache.thrift.meta_data.FieldMetaData("_trace", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Trace.class, metaDataMap);
  }

  public Trace() {
  }

  public Trace(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    boolean _end,
    int _attributes,
    java.lang.String _trace)
  {
    this();
    this._baseMessage = _baseMessage;
    this._end = _end;
    set_endIsSet(true);
    this._attributes = _attributes;
    set_attributesIsSet(true);
    this._trace = _trace;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Trace(Trace other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    this._end = other._end;
    this._attributes = other._attributes;
    if (other.isSet_trace()) {
      this._trace = other._trace;
    }
  }

  public Trace deepCopy() {
    return new Trace(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    set_endIsSet(false);
    this._end = false;
    set_attributesIsSet(false);
    this._attributes = 0;
    this._trace = null;
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

  public boolean is_end() {
    return this._end;
  }

  public void set_end(boolean _end) {
    this._end = _end;
    set_endIsSet(true);
  }

  public void unset_end() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___END_ISSET_ID);
  }

  /** Returns true if field _end is set (has been assigned a value) and false otherwise */
  public boolean isSet_end() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___END_ISSET_ID);
  }

  public void set_endIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___END_ISSET_ID, value);
  }

  public int get_attributes() {
    return this._attributes;
  }

  public void set_attributes(int _attributes) {
    this._attributes = _attributes;
    set_attributesIsSet(true);
  }

  public void unset_attributes() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___ATTRIBUTES_ISSET_ID);
  }

  /** Returns true if field _attributes is set (has been assigned a value) and false otherwise */
  public boolean isSet_attributes() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___ATTRIBUTES_ISSET_ID);
  }

  public void set_attributesIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___ATTRIBUTES_ISSET_ID, value);
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_trace() {
    return this._trace;
  }

  public void set_trace(@org.apache.thrift.annotation.Nullable java.lang.String _trace) {
    this._trace = _trace;
  }

  public void unset_trace() {
    this._trace = null;
  }

  /** Returns true if field _trace is set (has been assigned a value) and false otherwise */
  public boolean isSet_trace() {
    return this._trace != null;
  }

  public void set_traceIsSet(boolean value) {
    if (!value) {
      this._trace = null;
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

    case _END:
      if (value == null) {
        unset_end();
      } else {
        set_end((java.lang.Boolean)value);
      }
      break;

    case _ATTRIBUTES:
      if (value == null) {
        unset_attributes();
      } else {
        set_attributes((java.lang.Integer)value);
      }
      break;

    case _TRACE:
      if (value == null) {
        unset_trace();
      } else {
        set_trace((java.lang.String)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _END:
      return is_end();

    case _ATTRIBUTES:
      return get_attributes();

    case _TRACE:
      return get_trace();

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
    case _END:
      return isSet_end();
    case _ATTRIBUTES:
      return isSet_attributes();
    case _TRACE:
      return isSet_trace();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof Trace)
      return this.equals((Trace)that);
    return false;
  }

  public boolean equals(Trace that) {
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

    boolean this_present__end = true;
    boolean that_present__end = true;
    if (this_present__end || that_present__end) {
      if (!(this_present__end && that_present__end))
        return false;
      if (this._end != that._end)
        return false;
    }

    boolean this_present__attributes = true;
    boolean that_present__attributes = true;
    if (this_present__attributes || that_present__attributes) {
      if (!(this_present__attributes && that_present__attributes))
        return false;
      if (this._attributes != that._attributes)
        return false;
    }

    boolean this_present__trace = true && this.isSet_trace();
    boolean that_present__trace = true && that.isSet_trace();
    if (this_present__trace || that_present__trace) {
      if (!(this_present__trace && that_present__trace))
        return false;
      if (!this._trace.equals(that._trace))
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

    hashCode = hashCode * 8191 + ((_end) ? 131071 : 524287);

    hashCode = hashCode * 8191 + _attributes;

    hashCode = hashCode * 8191 + ((isSet_trace()) ? 131071 : 524287);
    if (isSet_trace())
      hashCode = hashCode * 8191 + _trace.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(Trace other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_end()).compareTo(other.isSet_end());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_end()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._end, other._end);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_attributes()).compareTo(other.isSet_attributes());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_attributes()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._attributes, other._attributes);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_trace()).compareTo(other.isSet_trace());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_trace()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._trace, other._trace);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("Trace(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_end:");
    sb.append(this._end);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_attributes:");
    sb.append(this._attributes);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_trace:");
    if (this._trace == null) {
      sb.append("null");
    } else {
      sb.append(this._trace);
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

    if (!isSet_end()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_end' is unset! Struct:" + toString());
    }

    if (!isSet_attributes()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_attributes' is unset! Struct:" + toString());
    }

    if (!isSet_trace()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_trace' is unset! Struct:" + toString());
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

  private static class TraceStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TraceStandardScheme getScheme() {
      return new TraceStandardScheme();
    }
  }

  private static class TraceStandardScheme extends org.apache.thrift.scheme.StandardScheme<Trace> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Trace struct) throws org.apache.thrift.TException {
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
          case 2: // _END
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct._end = iprot.readBool();
              struct.set_endIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _ATTRIBUTES
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._attributes = iprot.readI32();
              struct.set_attributesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _TRACE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._trace = iprot.readString();
              struct.set_traceIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, Trace struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_END_FIELD_DESC);
      oprot.writeBool(struct._end);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_ATTRIBUTES_FIELD_DESC);
      oprot.writeI32(struct._attributes);
      oprot.writeFieldEnd();
      if (struct._trace != null) {
        oprot.writeFieldBegin(_TRACE_FIELD_DESC);
        oprot.writeString(struct._trace);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TraceTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TraceTupleScheme getScheme() {
      return new TraceTupleScheme();
    }
  }

  private static class TraceTupleScheme extends org.apache.thrift.scheme.TupleScheme<Trace> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Trace struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeBool(struct._end);
      oprot.writeI32(struct._attributes);
      oprot.writeString(struct._trace);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Trace struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._end = iprot.readBool();
      struct.set_endIsSet(true);
      struct._attributes = iprot.readI32();
      struct.set_attributesIsSet(true);
      struct._trace = iprot.readString();
      struct.set_traceIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

