/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2022-04-25")
public class EdgeDrUnicastRequest implements org.apache.thrift.TBase<EdgeDrUnicastRequest, EdgeDrUnicastRequest._Fields>, java.io.Serializable, Cloneable, Comparable<EdgeDrUnicastRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("EdgeDrUnicastRequest");

  private static final org.apache.thrift.protocol.TField MESSAGE_GUID_FIELD_DESC = new org.apache.thrift.protocol.TField("messageGuid", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField PAO_IDS_FIELD_DESC = new org.apache.thrift.protocol.TField("paoIds", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField PAYLOAD_FIELD_DESC = new org.apache.thrift.protocol.TField("payload", org.apache.thrift.protocol.TType.STRING, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new EdgeDrUnicastRequestStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new EdgeDrUnicastRequestTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable java.lang.String messageGuid; // required
  private @org.apache.thrift.annotation.Nullable java.util.List<java.lang.Integer> paoIds; // required
  private @org.apache.thrift.annotation.Nullable java.nio.ByteBuffer payload; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MESSAGE_GUID((short)1, "messageGuid"),
    PAO_IDS((short)2, "paoIds"),
    PAYLOAD((short)3, "payload");

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
        case 1: // MESSAGE_GUID
          return MESSAGE_GUID;
        case 2: // PAO_IDS
          return PAO_IDS;
        case 3: // PAYLOAD
          return PAYLOAD;
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
    tmpMap.put(_Fields.MESSAGE_GUID, new org.apache.thrift.meta_data.FieldMetaData("messageGuid", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PAO_IDS, new org.apache.thrift.meta_data.FieldMetaData("paoIds", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
    tmpMap.put(_Fields.PAYLOAD, new org.apache.thrift.meta_data.FieldMetaData("payload", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(EdgeDrUnicastRequest.class, metaDataMap);
  }

  public EdgeDrUnicastRequest() {
  }

  public EdgeDrUnicastRequest(
    java.lang.String messageGuid,
    java.util.List<java.lang.Integer> paoIds,
    java.nio.ByteBuffer payload)
  {
    this();
    this.messageGuid = messageGuid;
    this.paoIds = paoIds;
    this.payload = org.apache.thrift.TBaseHelper.copyBinary(payload);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public EdgeDrUnicastRequest(EdgeDrUnicastRequest other) {
    if (other.isSetMessageGuid()) {
      this.messageGuid = other.messageGuid;
    }
    if (other.isSetPaoIds()) {
      java.util.List<java.lang.Integer> __this__paoIds = new java.util.ArrayList<java.lang.Integer>(other.paoIds);
      this.paoIds = __this__paoIds;
    }
    if (other.isSetPayload()) {
      this.payload = org.apache.thrift.TBaseHelper.copyBinary(other.payload);
    }
  }

  public EdgeDrUnicastRequest deepCopy() {
    return new EdgeDrUnicastRequest(this);
  }

  @Override
  public void clear() {
    this.messageGuid = null;
    this.paoIds = null;
    this.payload = null;
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getMessageGuid() {
    return this.messageGuid;
  }

  public void setMessageGuid(@org.apache.thrift.annotation.Nullable java.lang.String messageGuid) {
    this.messageGuid = messageGuid;
  }

  public void unsetMessageGuid() {
    this.messageGuid = null;
  }

  /** Returns true if field messageGuid is set (has been assigned a value) and false otherwise */
  public boolean isSetMessageGuid() {
    return this.messageGuid != null;
  }

  public void setMessageGuidIsSet(boolean value) {
    if (!value) {
      this.messageGuid = null;
    }
  }

  public int getPaoIdsSize() {
    return (this.paoIds == null) ? 0 : this.paoIds.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<java.lang.Integer> getPaoIdsIterator() {
    return (this.paoIds == null) ? null : this.paoIds.iterator();
  }

  public void addToPaoIds(int elem) {
    if (this.paoIds == null) {
      this.paoIds = new java.util.ArrayList<java.lang.Integer>();
    }
    this.paoIds.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<java.lang.Integer> getPaoIds() {
    return this.paoIds;
  }

  public void setPaoIds(@org.apache.thrift.annotation.Nullable java.util.List<java.lang.Integer> paoIds) {
    this.paoIds = paoIds;
  }

  public void unsetPaoIds() {
    this.paoIds = null;
  }

  /** Returns true if field paoIds is set (has been assigned a value) and false otherwise */
  public boolean isSetPaoIds() {
    return this.paoIds != null;
  }

  public void setPaoIdsIsSet(boolean value) {
    if (!value) {
      this.paoIds = null;
    }
  }

  public byte[] getPayload() {
    setPayload(org.apache.thrift.TBaseHelper.rightSize(payload));
    return payload == null ? null : payload.array();
  }

  public java.nio.ByteBuffer bufferForPayload() {
    return org.apache.thrift.TBaseHelper.copyBinary(payload);
  }

  public void setPayload(byte[] payload) {
    this.payload = payload == null ? (java.nio.ByteBuffer)null   : java.nio.ByteBuffer.wrap(payload.clone());
  }

  public void setPayload(@org.apache.thrift.annotation.Nullable java.nio.ByteBuffer payload) {
    this.payload = org.apache.thrift.TBaseHelper.copyBinary(payload);
  }

  public void unsetPayload() {
    this.payload = null;
  }

  /** Returns true if field payload is set (has been assigned a value) and false otherwise */
  public boolean isSetPayload() {
    return this.payload != null;
  }

  public void setPayloadIsSet(boolean value) {
    if (!value) {
      this.payload = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case MESSAGE_GUID:
      if (value == null) {
        unsetMessageGuid();
      } else {
        setMessageGuid((java.lang.String)value);
      }
      break;

    case PAO_IDS:
      if (value == null) {
        unsetPaoIds();
      } else {
        setPaoIds((java.util.List<java.lang.Integer>)value);
      }
      break;

    case PAYLOAD:
      if (value == null) {
        unsetPayload();
      } else {
        if (value instanceof byte[]) {
          setPayload((byte[])value);
        } else {
          setPayload((java.nio.ByteBuffer)value);
        }
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case MESSAGE_GUID:
      return getMessageGuid();

    case PAO_IDS:
      return getPaoIds();

    case PAYLOAD:
      return getPayload();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case MESSAGE_GUID:
      return isSetMessageGuid();
    case PAO_IDS:
      return isSetPaoIds();
    case PAYLOAD:
      return isSetPayload();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof EdgeDrUnicastRequest)
      return this.equals((EdgeDrUnicastRequest)that);
    return false;
  }

  public boolean equals(EdgeDrUnicastRequest that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_messageGuid = true && this.isSetMessageGuid();
    boolean that_present_messageGuid = true && that.isSetMessageGuid();
    if (this_present_messageGuid || that_present_messageGuid) {
      if (!(this_present_messageGuid && that_present_messageGuid))
        return false;
      if (!this.messageGuid.equals(that.messageGuid))
        return false;
    }

    boolean this_present_paoIds = true && this.isSetPaoIds();
    boolean that_present_paoIds = true && that.isSetPaoIds();
    if (this_present_paoIds || that_present_paoIds) {
      if (!(this_present_paoIds && that_present_paoIds))
        return false;
      if (!this.paoIds.equals(that.paoIds))
        return false;
    }

    boolean this_present_payload = true && this.isSetPayload();
    boolean that_present_payload = true && that.isSetPayload();
    if (this_present_payload || that_present_payload) {
      if (!(this_present_payload && that_present_payload))
        return false;
      if (!this.payload.equals(that.payload))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetMessageGuid()) ? 131071 : 524287);
    if (isSetMessageGuid())
      hashCode = hashCode * 8191 + messageGuid.hashCode();

    hashCode = hashCode * 8191 + ((isSetPaoIds()) ? 131071 : 524287);
    if (isSetPaoIds())
      hashCode = hashCode * 8191 + paoIds.hashCode();

    hashCode = hashCode * 8191 + ((isSetPayload()) ? 131071 : 524287);
    if (isSetPayload())
      hashCode = hashCode * 8191 + payload.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(EdgeDrUnicastRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetMessageGuid()).compareTo(other.isSetMessageGuid());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessageGuid()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.messageGuid, other.messageGuid);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetPaoIds()).compareTo(other.isSetPaoIds());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPaoIds()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.paoIds, other.paoIds);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetPayload()).compareTo(other.isSetPayload());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPayload()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.payload, other.payload);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("EdgeDrUnicastRequest(");
    boolean first = true;

    sb.append("messageGuid:");
    if (this.messageGuid == null) {
      sb.append("null");
    } else {
      sb.append(this.messageGuid);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("paoIds:");
    if (this.paoIds == null) {
      sb.append("null");
    } else {
      sb.append(this.paoIds);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("payload:");
    if (this.payload == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.payload, sb);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetMessageGuid()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'messageGuid' is unset! Struct:" + toString());
    }

    if (!isSetPaoIds()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'paoIds' is unset! Struct:" + toString());
    }

    if (!isSetPayload()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'payload' is unset! Struct:" + toString());
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

  private static class EdgeDrUnicastRequestStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public EdgeDrUnicastRequestStandardScheme getScheme() {
      return new EdgeDrUnicastRequestStandardScheme();
    }
  }

  private static class EdgeDrUnicastRequestStandardScheme extends org.apache.thrift.scheme.StandardScheme<EdgeDrUnicastRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, EdgeDrUnicastRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // MESSAGE_GUID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.messageGuid = iprot.readString();
              struct.setMessageGuidIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // PAO_IDS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.paoIds = new java.util.ArrayList<java.lang.Integer>(_list0.size);
                int _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = iprot.readI32();
                  struct.paoIds.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.setPaoIdsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // PAYLOAD
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.payload = iprot.readBinary();
              struct.setPayloadIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, EdgeDrUnicastRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.messageGuid != null) {
        oprot.writeFieldBegin(MESSAGE_GUID_FIELD_DESC);
        oprot.writeString(struct.messageGuid);
        oprot.writeFieldEnd();
      }
      if (struct.paoIds != null) {
        oprot.writeFieldBegin(PAO_IDS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, struct.paoIds.size()));
          for (int _iter3 : struct.paoIds)
          {
            oprot.writeI32(_iter3);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.payload != null) {
        oprot.writeFieldBegin(PAYLOAD_FIELD_DESC);
        oprot.writeBinary(struct.payload);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class EdgeDrUnicastRequestTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public EdgeDrUnicastRequestTupleScheme getScheme() {
      return new EdgeDrUnicastRequestTupleScheme();
    }
  }

  private static class EdgeDrUnicastRequestTupleScheme extends org.apache.thrift.scheme.TupleScheme<EdgeDrUnicastRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, EdgeDrUnicastRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeString(struct.messageGuid);
      {
        oprot.writeI32(struct.paoIds.size());
        for (int _iter4 : struct.paoIds)
        {
          oprot.writeI32(_iter4);
        }
      }
      oprot.writeBinary(struct.payload);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, EdgeDrUnicastRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.messageGuid = iprot.readString();
      struct.setMessageGuidIsSet(true);
      {
        org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, iprot.readI32());
        struct.paoIds = new java.util.ArrayList<java.lang.Integer>(_list5.size);
        int _elem6;
        for (int _i7 = 0; _i7 < _list5.size; ++_i7)
        {
          _elem6 = iprot.readI32();
          struct.paoIds.add(_elem6);
        }
      }
      struct.setPaoIdsIsSet(true);
      struct.payload = iprot.readBinary();
      struct.setPayloadIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

