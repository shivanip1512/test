/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public class RfnDataStreamingUpdate implements org.apache.thrift.TBase<RfnDataStreamingUpdate, RfnDataStreamingUpdate._Fields>, java.io.Serializable, Cloneable, Comparable<RfnDataStreamingUpdate> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RfnDataStreamingUpdate");

  private static final org.apache.thrift.protocol.TField PAO_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("paoId", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField JSON_FIELD_DESC = new org.apache.thrift.protocol.TField("json", org.apache.thrift.protocol.TType.STRING, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new RfnDataStreamingUpdateStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new RfnDataStreamingUpdateTupleSchemeFactory();

  private int paoId; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String json; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    PAO_ID((short)1, "paoId"),
    JSON((short)2, "json");

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
        case 1: // PAO_ID
          return PAO_ID;
        case 2: // JSON
          return JSON;
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
  private static final int __PAOID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.PAO_ID, new org.apache.thrift.meta_data.FieldMetaData("paoId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.JSON, new org.apache.thrift.meta_data.FieldMetaData("json", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RfnDataStreamingUpdate.class, metaDataMap);
  }

  public RfnDataStreamingUpdate() {
  }

  public RfnDataStreamingUpdate(
    int paoId,
    java.lang.String json)
  {
    this();
    this.paoId = paoId;
    setPaoIdIsSet(true);
    this.json = json;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RfnDataStreamingUpdate(RfnDataStreamingUpdate other) {
    __isset_bitfield = other.__isset_bitfield;
    this.paoId = other.paoId;
    if (other.isSetJson()) {
      this.json = other.json;
    }
  }

  public RfnDataStreamingUpdate deepCopy() {
    return new RfnDataStreamingUpdate(this);
  }

  @Override
  public void clear() {
    setPaoIdIsSet(false);
    this.paoId = 0;
    this.json = null;
  }

  public int getPaoId() {
    return this.paoId;
  }

  public void setPaoId(int paoId) {
    this.paoId = paoId;
    setPaoIdIsSet(true);
  }

  public void unsetPaoId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __PAOID_ISSET_ID);
  }

  /** Returns true if field paoId is set (has been assigned a value) and false otherwise */
  public boolean isSetPaoId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __PAOID_ISSET_ID);
  }

  public void setPaoIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __PAOID_ISSET_ID, value);
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getJson() {
    return this.json;
  }

  public void setJson(@org.apache.thrift.annotation.Nullable java.lang.String json) {
    this.json = json;
  }

  public void unsetJson() {
    this.json = null;
  }

  /** Returns true if field json is set (has been assigned a value) and false otherwise */
  public boolean isSetJson() {
    return this.json != null;
  }

  public void setJsonIsSet(boolean value) {
    if (!value) {
      this.json = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case PAO_ID:
      if (value == null) {
        unsetPaoId();
      } else {
        setPaoId((java.lang.Integer)value);
      }
      break;

    case JSON:
      if (value == null) {
        unsetJson();
      } else {
        setJson((java.lang.String)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case PAO_ID:
      return getPaoId();

    case JSON:
      return getJson();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case PAO_ID:
      return isSetPaoId();
    case JSON:
      return isSetJson();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof RfnDataStreamingUpdate)
      return this.equals((RfnDataStreamingUpdate)that);
    return false;
  }

  public boolean equals(RfnDataStreamingUpdate that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_paoId = true;
    boolean that_present_paoId = true;
    if (this_present_paoId || that_present_paoId) {
      if (!(this_present_paoId && that_present_paoId))
        return false;
      if (this.paoId != that.paoId)
        return false;
    }

    boolean this_present_json = true && this.isSetJson();
    boolean that_present_json = true && that.isSetJson();
    if (this_present_json || that_present_json) {
      if (!(this_present_json && that_present_json))
        return false;
      if (!this.json.equals(that.json))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + paoId;

    hashCode = hashCode * 8191 + ((isSetJson()) ? 131071 : 524287);
    if (isSetJson())
      hashCode = hashCode * 8191 + json.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(RfnDataStreamingUpdate other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetPaoId()).compareTo(other.isSetPaoId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPaoId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.paoId, other.paoId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetJson()).compareTo(other.isSetJson());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetJson()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.json, other.json);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("RfnDataStreamingUpdate(");
    boolean first = true;

    sb.append("paoId:");
    sb.append(this.paoId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("json:");
    if (this.json == null) {
      sb.append("null");
    } else {
      sb.append(this.json);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetPaoId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'paoId' is unset! Struct:" + toString());
    }

    if (!isSetJson()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'json' is unset! Struct:" + toString());
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class RfnDataStreamingUpdateStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RfnDataStreamingUpdateStandardScheme getScheme() {
      return new RfnDataStreamingUpdateStandardScheme();
    }
  }

  private static class RfnDataStreamingUpdateStandardScheme extends org.apache.thrift.scheme.StandardScheme<RfnDataStreamingUpdate> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RfnDataStreamingUpdate struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // PAO_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.paoId = iprot.readI32();
              struct.setPaoIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // JSON
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.json = iprot.readString();
              struct.setJsonIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, RfnDataStreamingUpdate struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(PAO_ID_FIELD_DESC);
      oprot.writeI32(struct.paoId);
      oprot.writeFieldEnd();
      if (struct.json != null) {
        oprot.writeFieldBegin(JSON_FIELD_DESC);
        oprot.writeString(struct.json);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RfnDataStreamingUpdateTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RfnDataStreamingUpdateTupleScheme getScheme() {
      return new RfnDataStreamingUpdateTupleScheme();
    }
  }

  private static class RfnDataStreamingUpdateTupleScheme extends org.apache.thrift.scheme.TupleScheme<RfnDataStreamingUpdate> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RfnDataStreamingUpdate struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeI32(struct.paoId);
      oprot.writeString(struct.json);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RfnDataStreamingUpdate struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.paoId = iprot.readI32();
      struct.setPaoIdIsSet(true);
      struct.json = iprot.readString();
      struct.setJsonIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

