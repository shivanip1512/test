/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2021-01-11")
public class RfnMeterDisconnectConfirmationReply implements org.apache.thrift.TBase<RfnMeterDisconnectConfirmationReply, RfnMeterDisconnectConfirmationReply._Fields>, java.io.Serializable, Cloneable, Comparable<RfnMeterDisconnectConfirmationReply> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RfnMeterDisconnectConfirmationReply");

  private static final org.apache.thrift.protocol.TField REPLY_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("replyType", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField STATE_FIELD_DESC = new org.apache.thrift.protocol.TField("state", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new RfnMeterDisconnectConfirmationReplyStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new RfnMeterDisconnectConfirmationReplyTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable RfnMeterDisconnectConfirmationReplyType replyType; // required
  private @org.apache.thrift.annotation.Nullable RfnMeterDisconnectState state; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see RfnMeterDisconnectConfirmationReplyType
     */
    REPLY_TYPE((short)1, "replyType"),
    /**
     * 
     * @see RfnMeterDisconnectState
     */
    STATE((short)2, "state");

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
        case 1: // REPLY_TYPE
          return REPLY_TYPE;
        case 2: // STATE
          return STATE;
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
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.REPLY_TYPE, new org.apache.thrift.meta_data.FieldMetaData("replyType", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, RfnMeterDisconnectConfirmationReplyType.class)));
    tmpMap.put(_Fields.STATE, new org.apache.thrift.meta_data.FieldMetaData("state", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, RfnMeterDisconnectState.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RfnMeterDisconnectConfirmationReply.class, metaDataMap);
  }

  public RfnMeterDisconnectConfirmationReply() {
  }

  public RfnMeterDisconnectConfirmationReply(
    RfnMeterDisconnectConfirmationReplyType replyType,
    RfnMeterDisconnectState state)
  {
    this();
    this.replyType = replyType;
    this.state = state;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RfnMeterDisconnectConfirmationReply(RfnMeterDisconnectConfirmationReply other) {
    if (other.isSetReplyType()) {
      this.replyType = other.replyType;
    }
    if (other.isSetState()) {
      this.state = other.state;
    }
  }

  public RfnMeterDisconnectConfirmationReply deepCopy() {
    return new RfnMeterDisconnectConfirmationReply(this);
  }

  @Override
  public void clear() {
    this.replyType = null;
    this.state = null;
  }

  /**
   * 
   * @see RfnMeterDisconnectConfirmationReplyType
   */
  @org.apache.thrift.annotation.Nullable
  public RfnMeterDisconnectConfirmationReplyType getReplyType() {
    return this.replyType;
  }

  /**
   * 
   * @see RfnMeterDisconnectConfirmationReplyType
   */
  public void setReplyType(@org.apache.thrift.annotation.Nullable RfnMeterDisconnectConfirmationReplyType replyType) {
    this.replyType = replyType;
  }

  public void unsetReplyType() {
    this.replyType = null;
  }

  /** Returns true if field replyType is set (has been assigned a value) and false otherwise */
  public boolean isSetReplyType() {
    return this.replyType != null;
  }

  public void setReplyTypeIsSet(boolean value) {
    if (!value) {
      this.replyType = null;
    }
  }

  /**
   * 
   * @see RfnMeterDisconnectState
   */
  @org.apache.thrift.annotation.Nullable
  public RfnMeterDisconnectState getState() {
    return this.state;
  }

  /**
   * 
   * @see RfnMeterDisconnectState
   */
  public void setState(@org.apache.thrift.annotation.Nullable RfnMeterDisconnectState state) {
    this.state = state;
  }

  public void unsetState() {
    this.state = null;
  }

  /** Returns true if field state is set (has been assigned a value) and false otherwise */
  public boolean isSetState() {
    return this.state != null;
  }

  public void setStateIsSet(boolean value) {
    if (!value) {
      this.state = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case REPLY_TYPE:
      if (value == null) {
        unsetReplyType();
      } else {
        setReplyType((RfnMeterDisconnectConfirmationReplyType)value);
      }
      break;

    case STATE:
      if (value == null) {
        unsetState();
      } else {
        setState((RfnMeterDisconnectState)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case REPLY_TYPE:
      return getReplyType();

    case STATE:
      return getState();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case REPLY_TYPE:
      return isSetReplyType();
    case STATE:
      return isSetState();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof RfnMeterDisconnectConfirmationReply)
      return this.equals((RfnMeterDisconnectConfirmationReply)that);
    return false;
  }

  public boolean equals(RfnMeterDisconnectConfirmationReply that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_replyType = true && this.isSetReplyType();
    boolean that_present_replyType = true && that.isSetReplyType();
    if (this_present_replyType || that_present_replyType) {
      if (!(this_present_replyType && that_present_replyType))
        return false;
      if (!this.replyType.equals(that.replyType))
        return false;
    }

    boolean this_present_state = true && this.isSetState();
    boolean that_present_state = true && that.isSetState();
    if (this_present_state || that_present_state) {
      if (!(this_present_state && that_present_state))
        return false;
      if (!this.state.equals(that.state))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetReplyType()) ? 131071 : 524287);
    if (isSetReplyType())
      hashCode = hashCode * 8191 + replyType.getValue();

    hashCode = hashCode * 8191 + ((isSetState()) ? 131071 : 524287);
    if (isSetState())
      hashCode = hashCode * 8191 + state.getValue();

    return hashCode;
  }

  @Override
  public int compareTo(RfnMeterDisconnectConfirmationReply other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetReplyType()).compareTo(other.isSetReplyType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetReplyType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.replyType, other.replyType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetState()).compareTo(other.isSetState());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetState()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.state, other.state);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("RfnMeterDisconnectConfirmationReply(");
    boolean first = true;

    sb.append("replyType:");
    if (this.replyType == null) {
      sb.append("null");
    } else {
      sb.append(this.replyType);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("state:");
    if (this.state == null) {
      sb.append("null");
    } else {
      sb.append(this.state);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetReplyType()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'replyType' is unset! Struct:" + toString());
    }

    if (!isSetState()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'state' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class RfnMeterDisconnectConfirmationReplyStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RfnMeterDisconnectConfirmationReplyStandardScheme getScheme() {
      return new RfnMeterDisconnectConfirmationReplyStandardScheme();
    }
  }

  private static class RfnMeterDisconnectConfirmationReplyStandardScheme extends org.apache.thrift.scheme.StandardScheme<RfnMeterDisconnectConfirmationReply> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RfnMeterDisconnectConfirmationReply struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // REPLY_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.replyType = com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectConfirmationReplyType.findByValue(iprot.readI32());
              struct.setReplyTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // STATE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.state = com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectState.findByValue(iprot.readI32());
              struct.setStateIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, RfnMeterDisconnectConfirmationReply struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.replyType != null) {
        oprot.writeFieldBegin(REPLY_TYPE_FIELD_DESC);
        oprot.writeI32(struct.replyType.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.state != null) {
        oprot.writeFieldBegin(STATE_FIELD_DESC);
        oprot.writeI32(struct.state.getValue());
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RfnMeterDisconnectConfirmationReplyTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RfnMeterDisconnectConfirmationReplyTupleScheme getScheme() {
      return new RfnMeterDisconnectConfirmationReplyTupleScheme();
    }
  }

  private static class RfnMeterDisconnectConfirmationReplyTupleScheme extends org.apache.thrift.scheme.TupleScheme<RfnMeterDisconnectConfirmationReply> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RfnMeterDisconnectConfirmationReply struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeI32(struct.replyType.getValue());
      oprot.writeI32(struct.state.getValue());
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RfnMeterDisconnectConfirmationReply struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.replyType = com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectConfirmationReplyType.findByValue(iprot.readI32());
      struct.setReplyTypeIsSet(true);
      struct.state = com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectState.findByValue(iprot.readI32());
      struct.setStateIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

