/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2018-06-27")
public class RfnExpressComBroadcastReply implements org.apache.thrift.TBase<RfnExpressComBroadcastReply, RfnExpressComBroadcastReply._Fields>, java.io.Serializable, Cloneable, Comparable<RfnExpressComBroadcastReply> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RfnExpressComBroadcastReply");

  private static final org.apache.thrift.protocol.TField STATUS_FIELD_DESC = new org.apache.thrift.protocol.TField("status", org.apache.thrift.protocol.TType.MAP, (short)1);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new RfnExpressComBroadcastReplyStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new RfnExpressComBroadcastReplyTupleSchemeFactory();

  private java.util.Map<java.lang.Long,RfnExpressComBroadcastReplyType> status; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    STATUS((short)1, "status");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // STATUS
          return STATUS;
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
    tmpMap.put(_Fields.STATUS, new org.apache.thrift.meta_data.FieldMetaData("status", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64), 
            new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, RfnExpressComBroadcastReplyType.class))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RfnExpressComBroadcastReply.class, metaDataMap);
  }

  public RfnExpressComBroadcastReply() {
  }

  public RfnExpressComBroadcastReply(
    java.util.Map<java.lang.Long,RfnExpressComBroadcastReplyType> status)
  {
    this();
    this.status = status;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RfnExpressComBroadcastReply(RfnExpressComBroadcastReply other) {
    if (other.isSetStatus()) {
      java.util.Map<java.lang.Long,RfnExpressComBroadcastReplyType> __this__status = new java.util.HashMap<java.lang.Long,RfnExpressComBroadcastReplyType>(other.status.size());
      for (java.util.Map.Entry<java.lang.Long, RfnExpressComBroadcastReplyType> other_element : other.status.entrySet()) {

        java.lang.Long other_element_key = other_element.getKey();
        RfnExpressComBroadcastReplyType other_element_value = other_element.getValue();

        java.lang.Long __this__status_copy_key = other_element_key;

        RfnExpressComBroadcastReplyType __this__status_copy_value = other_element_value;

        __this__status.put(__this__status_copy_key, __this__status_copy_value);
      }
      this.status = __this__status;
    }
  }

  public RfnExpressComBroadcastReply deepCopy() {
    return new RfnExpressComBroadcastReply(this);
  }

  @Override
  public void clear() {
    this.status = null;
  }

  public int getStatusSize() {
    return (this.status == null) ? 0 : this.status.size();
  }

  public void putToStatus(long key, RfnExpressComBroadcastReplyType val) {
    if (this.status == null) {
      this.status = new java.util.HashMap<java.lang.Long,RfnExpressComBroadcastReplyType>();
    }
    this.status.put(key, val);
  }

  public java.util.Map<java.lang.Long,RfnExpressComBroadcastReplyType> getStatus() {
    return this.status;
  }

  public void setStatus(java.util.Map<java.lang.Long,RfnExpressComBroadcastReplyType> status) {
    this.status = status;
  }

  public void unsetStatus() {
    this.status = null;
  }

  /** Returns true if field status is set (has been assigned a value) and false otherwise */
  public boolean isSetStatus() {
    return this.status != null;
  }

  public void setStatusIsSet(boolean value) {
    if (!value) {
      this.status = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case STATUS:
      if (value == null) {
        unsetStatus();
      } else {
        setStatus((java.util.Map<java.lang.Long,RfnExpressComBroadcastReplyType>)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case STATUS:
      return getStatus();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case STATUS:
      return isSetStatus();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof RfnExpressComBroadcastReply)
      return this.equals((RfnExpressComBroadcastReply)that);
    return false;
  }

  public boolean equals(RfnExpressComBroadcastReply that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_status = true && this.isSetStatus();
    boolean that_present_status = true && that.isSetStatus();
    if (this_present_status || that_present_status) {
      if (!(this_present_status && that_present_status))
        return false;
      if (!this.status.equals(that.status))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetStatus()) ? 131071 : 524287);
    if (isSetStatus())
      hashCode = hashCode * 8191 + status.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(RfnExpressComBroadcastReply other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetStatus()).compareTo(other.isSetStatus());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatus()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.status, other.status);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("RfnExpressComBroadcastReply(");
    boolean first = true;

    sb.append("status:");
    if (this.status == null) {
      sb.append("null");
    } else {
      sb.append(this.status);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetStatus()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'status' is unset! Struct:" + toString());
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

  private static class RfnExpressComBroadcastReplyStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RfnExpressComBroadcastReplyStandardScheme getScheme() {
      return new RfnExpressComBroadcastReplyStandardScheme();
    }
  }

  private static class RfnExpressComBroadcastReplyStandardScheme extends org.apache.thrift.scheme.StandardScheme<RfnExpressComBroadcastReply> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RfnExpressComBroadcastReply struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // STATUS
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map0 = iprot.readMapBegin();
                struct.status = new java.util.HashMap<java.lang.Long,RfnExpressComBroadcastReplyType>(2*_map0.size);
                long _key1;
                RfnExpressComBroadcastReplyType _val2;
                for (int _i3 = 0; _i3 < _map0.size; ++_i3)
                {
                  _key1 = iprot.readI64();
                  _val2 = com.cannontech.messaging.serialization.thrift.generated.RfnExpressComBroadcastReplyType.findByValue(iprot.readI32());
                  struct.status.put(_key1, _val2);
                }
                iprot.readMapEnd();
              }
              struct.setStatusIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, RfnExpressComBroadcastReply struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.status != null) {
        oprot.writeFieldBegin(STATUS_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I64, org.apache.thrift.protocol.TType.I32, struct.status.size()));
          for (java.util.Map.Entry<java.lang.Long, RfnExpressComBroadcastReplyType> _iter4 : struct.status.entrySet())
          {
            oprot.writeI64(_iter4.getKey());
            oprot.writeI32(_iter4.getValue().getValue());
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RfnExpressComBroadcastReplyTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RfnExpressComBroadcastReplyTupleScheme getScheme() {
      return new RfnExpressComBroadcastReplyTupleScheme();
    }
  }

  private static class RfnExpressComBroadcastReplyTupleScheme extends org.apache.thrift.scheme.TupleScheme<RfnExpressComBroadcastReply> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RfnExpressComBroadcastReply struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      {
        oprot.writeI32(struct.status.size());
        for (java.util.Map.Entry<java.lang.Long, RfnExpressComBroadcastReplyType> _iter5 : struct.status.entrySet())
        {
          oprot.writeI64(_iter5.getKey());
          oprot.writeI32(_iter5.getValue().getValue());
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RfnExpressComBroadcastReply struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      {
        org.apache.thrift.protocol.TMap _map6 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I64, org.apache.thrift.protocol.TType.I32, iprot.readI32());
        struct.status = new java.util.HashMap<java.lang.Long,RfnExpressComBroadcastReplyType>(2*_map6.size);
        long _key7;
        RfnExpressComBroadcastReplyType _val8;
        for (int _i9 = 0; _i9 < _map6.size; ++_i9)
        {
          _key7 = iprot.readI64();
          _val8 = com.cannontech.messaging.serialization.thrift.generated.RfnExpressComBroadcastReplyType.findByValue(iprot.readI32());
          struct.status.put(_key7, _val8);
        }
      }
      struct.setStatusIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

