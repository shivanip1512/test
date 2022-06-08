/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.16.0)", date = "2022-05-31")
public class RfnMeterReadDataReply implements org.apache.thrift.TBase<RfnMeterReadDataReply, RfnMeterReadDataReply._Fields>, java.io.Serializable, Cloneable, Comparable<RfnMeterReadDataReply> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RfnMeterReadDataReply");

  private static final org.apache.thrift.protocol.TField REPLY_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("replyType", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField DATA_FIELD_DESC = new org.apache.thrift.protocol.TField("data", org.apache.thrift.protocol.TType.STRUCT, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new RfnMeterReadDataReplyStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new RfnMeterReadDataReplyTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable RfnMeterReadingDataReplyType replyType; // required
  private @org.apache.thrift.annotation.Nullable RfnMeterReadingData data; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see RfnMeterReadingDataReplyType
     */
    REPLY_TYPE((short)1, "replyType"),
    DATA((short)2, "data");

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
        case 2: // DATA
          return DATA;
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
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, RfnMeterReadingDataReplyType.class)));
    tmpMap.put(_Fields.DATA, new org.apache.thrift.meta_data.FieldMetaData("data", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, RfnMeterReadingData.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RfnMeterReadDataReply.class, metaDataMap);
  }

  public RfnMeterReadDataReply() {
  }

  public RfnMeterReadDataReply(
    RfnMeterReadingDataReplyType replyType,
    RfnMeterReadingData data)
  {
    this();
    this.replyType = replyType;
    this.data = data;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RfnMeterReadDataReply(RfnMeterReadDataReply other) {
    if (other.isSetReplyType()) {
      this.replyType = other.replyType;
    }
    if (other.isSetData()) {
      this.data = new RfnMeterReadingData(other.data);
    }
  }

  public RfnMeterReadDataReply deepCopy() {
    return new RfnMeterReadDataReply(this);
  }

  @Override
  public void clear() {
    this.replyType = null;
    this.data = null;
  }

  /**
   * 
   * @see RfnMeterReadingDataReplyType
   */
  @org.apache.thrift.annotation.Nullable
  public RfnMeterReadingDataReplyType getReplyType() {
    return this.replyType;
  }

  /**
   * 
   * @see RfnMeterReadingDataReplyType
   */
  public void setReplyType(@org.apache.thrift.annotation.Nullable RfnMeterReadingDataReplyType replyType) {
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

  @org.apache.thrift.annotation.Nullable
  public RfnMeterReadingData getData() {
    return this.data;
  }

  public void setData(@org.apache.thrift.annotation.Nullable RfnMeterReadingData data) {
    this.data = data;
  }

  public void unsetData() {
    this.data = null;
  }

  /** Returns true if field data is set (has been assigned a value) and false otherwise */
  public boolean isSetData() {
    return this.data != null;
  }

  public void setDataIsSet(boolean value) {
    if (!value) {
      this.data = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case REPLY_TYPE:
      if (value == null) {
        unsetReplyType();
      } else {
        setReplyType((RfnMeterReadingDataReplyType)value);
      }
      break;

    case DATA:
      if (value == null) {
        unsetData();
      } else {
        setData((RfnMeterReadingData)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case REPLY_TYPE:
      return getReplyType();

    case DATA:
      return getData();

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
    case DATA:
      return isSetData();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof RfnMeterReadDataReply)
      return this.equals((RfnMeterReadDataReply)that);
    return false;
  }

  public boolean equals(RfnMeterReadDataReply that) {
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

    boolean this_present_data = true && this.isSetData();
    boolean that_present_data = true && that.isSetData();
    if (this_present_data || that_present_data) {
      if (!(this_present_data && that_present_data))
        return false;
      if (!this.data.equals(that.data))
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

    hashCode = hashCode * 8191 + ((isSetData()) ? 131071 : 524287);
    if (isSetData())
      hashCode = hashCode * 8191 + data.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(RfnMeterReadDataReply other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetReplyType(), other.isSetReplyType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetReplyType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.replyType, other.replyType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetData(), other.isSetData());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetData()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.data, other.data);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("RfnMeterReadDataReply(");
    boolean first = true;

    sb.append("replyType:");
    if (this.replyType == null) {
      sb.append("null");
    } else {
      sb.append(this.replyType);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("data:");
    if (this.data == null) {
      sb.append("null");
    } else {
      sb.append(this.data);
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

    if (!isSetData()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'data' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (data != null) {
      data.validate();
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class RfnMeterReadDataReplyStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RfnMeterReadDataReplyStandardScheme getScheme() {
      return new RfnMeterReadDataReplyStandardScheme();
    }
  }

  private static class RfnMeterReadDataReplyStandardScheme extends org.apache.thrift.scheme.StandardScheme<RfnMeterReadDataReply> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RfnMeterReadDataReply struct) throws org.apache.thrift.TException {
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
              struct.replyType = com.cannontech.messaging.serialization.thrift.generated.RfnMeterReadingDataReplyType.findByValue(iprot.readI32());
              struct.setReplyTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // DATA
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.data = new RfnMeterReadingData();
              struct.data.read(iprot);
              struct.setDataIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, RfnMeterReadDataReply struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.replyType != null) {
        oprot.writeFieldBegin(REPLY_TYPE_FIELD_DESC);
        oprot.writeI32(struct.replyType.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.data != null) {
        oprot.writeFieldBegin(DATA_FIELD_DESC);
        struct.data.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RfnMeterReadDataReplyTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RfnMeterReadDataReplyTupleScheme getScheme() {
      return new RfnMeterReadDataReplyTupleScheme();
    }
  }

  private static class RfnMeterReadDataReplyTupleScheme extends org.apache.thrift.scheme.TupleScheme<RfnMeterReadDataReply> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RfnMeterReadDataReply struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeI32(struct.replyType.getValue());
      struct.data.write(oprot);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RfnMeterReadDataReply struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.replyType = com.cannontech.messaging.serialization.thrift.generated.RfnMeterReadingDataReplyType.findByValue(iprot.readI32());
      struct.setReplyTypeIsSet(true);
      struct.data = new RfnMeterReadingData();
      struct.data.read(iprot);
      struct.setDataIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

