/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public class NetworkManagerCancelRequest implements org.apache.thrift.TBase<NetworkManagerCancelRequest, NetworkManagerCancelRequest._Fields>, java.io.Serializable, Cloneable, Comparable<NetworkManagerCancelRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("NetworkManagerCancelRequest");

  private static final org.apache.thrift.protocol.TField CLIENT_GUID_FIELD_DESC = new org.apache.thrift.protocol.TField("clientGuid", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField SESSION_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("sessionId", org.apache.thrift.protocol.TType.I64, (short)2);
  private static final org.apache.thrift.protocol.TField TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("type", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField IDS_FIELD_DESC = new org.apache.thrift.protocol.TField("ids", org.apache.thrift.protocol.TType.SET, (short)4);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new NetworkManagerCancelRequestStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new NetworkManagerCancelRequestTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable java.lang.String clientGuid; // required
  private long sessionId; // required
  private @org.apache.thrift.annotation.Nullable NetworkManagerCancelType type; // required
  private @org.apache.thrift.annotation.Nullable java.util.Set<java.lang.Long> ids; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    CLIENT_GUID((short)1, "clientGuid"),
    SESSION_ID((short)2, "sessionId"),
    /**
     * 
     * @see NetworkManagerCancelType
     */
    TYPE((short)3, "type"),
    IDS((short)4, "ids");

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
        case 1: // CLIENT_GUID
          return CLIENT_GUID;
        case 2: // SESSION_ID
          return SESSION_ID;
        case 3: // TYPE
          return TYPE;
        case 4: // IDS
          return IDS;
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
  private static final int __SESSIONID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.CLIENT_GUID, new org.apache.thrift.meta_data.FieldMetaData("clientGuid", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.SESSION_ID, new org.apache.thrift.meta_data.FieldMetaData("sessionId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.TYPE, new org.apache.thrift.meta_data.FieldMetaData("type", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, NetworkManagerCancelType.class)));
    tmpMap.put(_Fields.IDS, new org.apache.thrift.meta_data.FieldMetaData("ids", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(NetworkManagerCancelRequest.class, metaDataMap);
  }

  public NetworkManagerCancelRequest() {
  }

  public NetworkManagerCancelRequest(
    java.lang.String clientGuid,
    long sessionId,
    NetworkManagerCancelType type,
    java.util.Set<java.lang.Long> ids)
  {
    this();
    this.clientGuid = clientGuid;
    this.sessionId = sessionId;
    setSessionIdIsSet(true);
    this.type = type;
    this.ids = ids;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public NetworkManagerCancelRequest(NetworkManagerCancelRequest other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetClientGuid()) {
      this.clientGuid = other.clientGuid;
    }
    this.sessionId = other.sessionId;
    if (other.isSetType()) {
      this.type = other.type;
    }
    if (other.isSetIds()) {
      java.util.Set<java.lang.Long> __this__ids = new java.util.HashSet<java.lang.Long>(other.ids);
      this.ids = __this__ids;
    }
  }

  public NetworkManagerCancelRequest deepCopy() {
    return new NetworkManagerCancelRequest(this);
  }

  @Override
  public void clear() {
    this.clientGuid = null;
    setSessionIdIsSet(false);
    this.sessionId = 0;
    this.type = null;
    this.ids = null;
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getClientGuid() {
    return this.clientGuid;
  }

  public void setClientGuid(@org.apache.thrift.annotation.Nullable java.lang.String clientGuid) {
    this.clientGuid = clientGuid;
  }

  public void unsetClientGuid() {
    this.clientGuid = null;
  }

  /** Returns true if field clientGuid is set (has been assigned a value) and false otherwise */
  public boolean isSetClientGuid() {
    return this.clientGuid != null;
  }

  public void setClientGuidIsSet(boolean value) {
    if (!value) {
      this.clientGuid = null;
    }
  }

  public long getSessionId() {
    return this.sessionId;
  }

  public void setSessionId(long sessionId) {
    this.sessionId = sessionId;
    setSessionIdIsSet(true);
  }

  public void unsetSessionId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __SESSIONID_ISSET_ID);
  }

  /** Returns true if field sessionId is set (has been assigned a value) and false otherwise */
  public boolean isSetSessionId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __SESSIONID_ISSET_ID);
  }

  public void setSessionIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __SESSIONID_ISSET_ID, value);
  }

  /**
   * 
   * @see NetworkManagerCancelType
   */
  @org.apache.thrift.annotation.Nullable
  public NetworkManagerCancelType getType() {
    return this.type;
  }

  /**
   * 
   * @see NetworkManagerCancelType
   */
  public void setType(@org.apache.thrift.annotation.Nullable NetworkManagerCancelType type) {
    this.type = type;
  }

  public void unsetType() {
    this.type = null;
  }

  /** Returns true if field type is set (has been assigned a value) and false otherwise */
  public boolean isSetType() {
    return this.type != null;
  }

  public void setTypeIsSet(boolean value) {
    if (!value) {
      this.type = null;
    }
  }

  public int getIdsSize() {
    return (this.ids == null) ? 0 : this.ids.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<java.lang.Long> getIdsIterator() {
    return (this.ids == null) ? null : this.ids.iterator();
  }

  public void addToIds(long elem) {
    if (this.ids == null) {
      this.ids = new java.util.HashSet<java.lang.Long>();
    }
    this.ids.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Set<java.lang.Long> getIds() {
    return this.ids;
  }

  public void setIds(@org.apache.thrift.annotation.Nullable java.util.Set<java.lang.Long> ids) {
    this.ids = ids;
  }

  public void unsetIds() {
    this.ids = null;
  }

  /** Returns true if field ids is set (has been assigned a value) and false otherwise */
  public boolean isSetIds() {
    return this.ids != null;
  }

  public void setIdsIsSet(boolean value) {
    if (!value) {
      this.ids = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case CLIENT_GUID:
      if (value == null) {
        unsetClientGuid();
      } else {
        setClientGuid((java.lang.String)value);
      }
      break;

    case SESSION_ID:
      if (value == null) {
        unsetSessionId();
      } else {
        setSessionId((java.lang.Long)value);
      }
      break;

    case TYPE:
      if (value == null) {
        unsetType();
      } else {
        setType((NetworkManagerCancelType)value);
      }
      break;

    case IDS:
      if (value == null) {
        unsetIds();
      } else {
        setIds((java.util.Set<java.lang.Long>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case CLIENT_GUID:
      return getClientGuid();

    case SESSION_ID:
      return getSessionId();

    case TYPE:
      return getType();

    case IDS:
      return getIds();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case CLIENT_GUID:
      return isSetClientGuid();
    case SESSION_ID:
      return isSetSessionId();
    case TYPE:
      return isSetType();
    case IDS:
      return isSetIds();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof NetworkManagerCancelRequest)
      return this.equals((NetworkManagerCancelRequest)that);
    return false;
  }

  public boolean equals(NetworkManagerCancelRequest that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_clientGuid = true && this.isSetClientGuid();
    boolean that_present_clientGuid = true && that.isSetClientGuid();
    if (this_present_clientGuid || that_present_clientGuid) {
      if (!(this_present_clientGuid && that_present_clientGuid))
        return false;
      if (!this.clientGuid.equals(that.clientGuid))
        return false;
    }

    boolean this_present_sessionId = true;
    boolean that_present_sessionId = true;
    if (this_present_sessionId || that_present_sessionId) {
      if (!(this_present_sessionId && that_present_sessionId))
        return false;
      if (this.sessionId != that.sessionId)
        return false;
    }

    boolean this_present_type = true && this.isSetType();
    boolean that_present_type = true && that.isSetType();
    if (this_present_type || that_present_type) {
      if (!(this_present_type && that_present_type))
        return false;
      if (!this.type.equals(that.type))
        return false;
    }

    boolean this_present_ids = true && this.isSetIds();
    boolean that_present_ids = true && that.isSetIds();
    if (this_present_ids || that_present_ids) {
      if (!(this_present_ids && that_present_ids))
        return false;
      if (!this.ids.equals(that.ids))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetClientGuid()) ? 131071 : 524287);
    if (isSetClientGuid())
      hashCode = hashCode * 8191 + clientGuid.hashCode();

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(sessionId);

    hashCode = hashCode * 8191 + ((isSetType()) ? 131071 : 524287);
    if (isSetType())
      hashCode = hashCode * 8191 + type.getValue();

    hashCode = hashCode * 8191 + ((isSetIds()) ? 131071 : 524287);
    if (isSetIds())
      hashCode = hashCode * 8191 + ids.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(NetworkManagerCancelRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetClientGuid()).compareTo(other.isSetClientGuid());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetClientGuid()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.clientGuid, other.clientGuid);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetSessionId()).compareTo(other.isSetSessionId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSessionId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.sessionId, other.sessionId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetType()).compareTo(other.isSetType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.type, other.type);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetIds()).compareTo(other.isSetIds());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIds()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.ids, other.ids);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("NetworkManagerCancelRequest(");
    boolean first = true;

    sb.append("clientGuid:");
    if (this.clientGuid == null) {
      sb.append("null");
    } else {
      sb.append(this.clientGuid);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("sessionId:");
    sb.append(this.sessionId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("type:");
    if (this.type == null) {
      sb.append("null");
    } else {
      sb.append(this.type);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("ids:");
    if (this.ids == null) {
      sb.append("null");
    } else {
      sb.append(this.ids);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetClientGuid()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'clientGuid' is unset! Struct:" + toString());
    }

    if (!isSetSessionId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'sessionId' is unset! Struct:" + toString());
    }

    if (!isSetType()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'type' is unset! Struct:" + toString());
    }

    if (!isSetIds()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'ids' is unset! Struct:" + toString());
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

  private static class NetworkManagerCancelRequestStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public NetworkManagerCancelRequestStandardScheme getScheme() {
      return new NetworkManagerCancelRequestStandardScheme();
    }
  }

  private static class NetworkManagerCancelRequestStandardScheme extends org.apache.thrift.scheme.StandardScheme<NetworkManagerCancelRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, NetworkManagerCancelRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // CLIENT_GUID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.clientGuid = iprot.readString();
              struct.setClientGuidIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // SESSION_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.sessionId = iprot.readI64();
              struct.setSessionIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.type = com.cannontech.messaging.serialization.thrift.generated.NetworkManagerCancelType.findByValue(iprot.readI32());
              struct.setTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // IDS
            if (schemeField.type == org.apache.thrift.protocol.TType.SET) {
              {
                org.apache.thrift.protocol.TSet _set0 = iprot.readSetBegin();
                struct.ids = new java.util.HashSet<java.lang.Long>(2*_set0.size);
                long _elem1;
                for (int _i2 = 0; _i2 < _set0.size; ++_i2)
                {
                  _elem1 = iprot.readI64();
                  struct.ids.add(_elem1);
                }
                iprot.readSetEnd();
              }
              struct.setIdsIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, NetworkManagerCancelRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.clientGuid != null) {
        oprot.writeFieldBegin(CLIENT_GUID_FIELD_DESC);
        oprot.writeString(struct.clientGuid);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(SESSION_ID_FIELD_DESC);
      oprot.writeI64(struct.sessionId);
      oprot.writeFieldEnd();
      if (struct.type != null) {
        oprot.writeFieldBegin(TYPE_FIELD_DESC);
        oprot.writeI32(struct.type.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.ids != null) {
        oprot.writeFieldBegin(IDS_FIELD_DESC);
        {
          oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.I64, struct.ids.size()));
          for (long _iter3 : struct.ids)
          {
            oprot.writeI64(_iter3);
          }
          oprot.writeSetEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class NetworkManagerCancelRequestTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public NetworkManagerCancelRequestTupleScheme getScheme() {
      return new NetworkManagerCancelRequestTupleScheme();
    }
  }

  private static class NetworkManagerCancelRequestTupleScheme extends org.apache.thrift.scheme.TupleScheme<NetworkManagerCancelRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, NetworkManagerCancelRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeString(struct.clientGuid);
      oprot.writeI64(struct.sessionId);
      oprot.writeI32(struct.type.getValue());
      {
        oprot.writeI32(struct.ids.size());
        for (long _iter4 : struct.ids)
        {
          oprot.writeI64(_iter4);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, NetworkManagerCancelRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.clientGuid = iprot.readString();
      struct.setClientGuidIsSet(true);
      struct.sessionId = iprot.readI64();
      struct.setSessionIdIsSet(true);
      struct.type = com.cannontech.messaging.serialization.thrift.generated.NetworkManagerCancelType.findByValue(iprot.readI32());
      struct.setTypeIsSet(true);
      {
        org.apache.thrift.protocol.TSet _set5 = new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.I64, iprot.readI32());
        struct.ids = new java.util.HashSet<java.lang.Long>(2*_set5.size);
        long _elem6;
        for (int _i7 = 0; _i7 < _set5.size; ++_i7)
        {
          _elem6 = iprot.readI64();
          struct.ids.add(_elem6);
        }
      }
      struct.setIdsIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

