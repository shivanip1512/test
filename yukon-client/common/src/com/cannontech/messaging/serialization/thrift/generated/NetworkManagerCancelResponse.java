/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-09-24")
public class NetworkManagerCancelResponse implements org.apache.thrift.TBase<NetworkManagerCancelResponse, NetworkManagerCancelResponse._Fields>, java.io.Serializable, Cloneable, Comparable<NetworkManagerCancelResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("NetworkManagerCancelResponse");

  private static final org.apache.thrift.protocol.TField CLIENT_GUID_FIELD_DESC = new org.apache.thrift.protocol.TField("clientGuid", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField SESSION_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("sessionId", org.apache.thrift.protocol.TType.I64, (short)2);
  private static final org.apache.thrift.protocol.TField MESSAGE_IDS_FIELD_DESC = new org.apache.thrift.protocol.TField("messageIds", org.apache.thrift.protocol.TType.MAP, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new NetworkManagerCancelResponseStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new NetworkManagerCancelResponseTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable java.lang.String clientGuid; // required
  private long sessionId; // required
  private @org.apache.thrift.annotation.Nullable java.util.Map<java.lang.Long,NetworkManagerMessageCancelStatus> messageIds; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    CLIENT_GUID((short)1, "clientGuid"),
    SESSION_ID((short)2, "sessionId"),
    MESSAGE_IDS((short)3, "messageIds");

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
        case 3: // MESSAGE_IDS
          return MESSAGE_IDS;
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
    tmpMap.put(_Fields.MESSAGE_IDS, new org.apache.thrift.meta_data.FieldMetaData("messageIds", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.MAP        , "MessageStatusPerId")));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(NetworkManagerCancelResponse.class, metaDataMap);
  }

  public NetworkManagerCancelResponse() {
  }

  public NetworkManagerCancelResponse(
    java.lang.String clientGuid,
    long sessionId,
    java.util.Map<java.lang.Long,NetworkManagerMessageCancelStatus> messageIds)
  {
    this();
    this.clientGuid = clientGuid;
    this.sessionId = sessionId;
    setSessionIdIsSet(true);
    this.messageIds = messageIds;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public NetworkManagerCancelResponse(NetworkManagerCancelResponse other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetClientGuid()) {
      this.clientGuid = other.clientGuid;
    }
    this.sessionId = other.sessionId;
    if (other.isSetMessageIds()) {
      java.util.Map<java.lang.Long,NetworkManagerMessageCancelStatus> __this__messageIds = new java.util.HashMap<java.lang.Long,NetworkManagerMessageCancelStatus>(other.messageIds.size());
      for (java.util.Map.Entry<java.lang.Long, NetworkManagerMessageCancelStatus> other_element : other.messageIds.entrySet()) {

        java.lang.Long other_element_key = other_element.getKey();
        NetworkManagerMessageCancelStatus other_element_value = other_element.getValue();

        java.lang.Long __this__messageIds_copy_key = other_element_key;

        NetworkManagerMessageCancelStatus __this__messageIds_copy_value = other_element_value;

        __this__messageIds.put(__this__messageIds_copy_key, __this__messageIds_copy_value);
      }
      this.messageIds = __this__messageIds;
    }
  }

  public NetworkManagerCancelResponse deepCopy() {
    return new NetworkManagerCancelResponse(this);
  }

  @Override
  public void clear() {
    this.clientGuid = null;
    setSessionIdIsSet(false);
    this.sessionId = 0;
    this.messageIds = null;
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

  public int getMessageIdsSize() {
    return (this.messageIds == null) ? 0 : this.messageIds.size();
  }

  public void putToMessageIds(long key, NetworkManagerMessageCancelStatus val) {
    if (this.messageIds == null) {
      this.messageIds = new java.util.HashMap<java.lang.Long,NetworkManagerMessageCancelStatus>();
    }
    this.messageIds.put(key, val);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Map<java.lang.Long,NetworkManagerMessageCancelStatus> getMessageIds() {
    return this.messageIds;
  }

  public void setMessageIds(@org.apache.thrift.annotation.Nullable java.util.Map<java.lang.Long,NetworkManagerMessageCancelStatus> messageIds) {
    this.messageIds = messageIds;
  }

  public void unsetMessageIds() {
    this.messageIds = null;
  }

  /** Returns true if field messageIds is set (has been assigned a value) and false otherwise */
  public boolean isSetMessageIds() {
    return this.messageIds != null;
  }

  public void setMessageIdsIsSet(boolean value) {
    if (!value) {
      this.messageIds = null;
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

    case MESSAGE_IDS:
      if (value == null) {
        unsetMessageIds();
      } else {
        setMessageIds((java.util.Map<java.lang.Long,NetworkManagerMessageCancelStatus>)value);
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

    case MESSAGE_IDS:
      return getMessageIds();

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
    case MESSAGE_IDS:
      return isSetMessageIds();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof NetworkManagerCancelResponse)
      return this.equals((NetworkManagerCancelResponse)that);
    return false;
  }

  public boolean equals(NetworkManagerCancelResponse that) {
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

    boolean this_present_messageIds = true && this.isSetMessageIds();
    boolean that_present_messageIds = true && that.isSetMessageIds();
    if (this_present_messageIds || that_present_messageIds) {
      if (!(this_present_messageIds && that_present_messageIds))
        return false;
      if (!this.messageIds.equals(that.messageIds))
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

    hashCode = hashCode * 8191 + ((isSetMessageIds()) ? 131071 : 524287);
    if (isSetMessageIds())
      hashCode = hashCode * 8191 + messageIds.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(NetworkManagerCancelResponse other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSetMessageIds()).compareTo(other.isSetMessageIds());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessageIds()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.messageIds, other.messageIds);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("NetworkManagerCancelResponse(");
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
    sb.append("messageIds:");
    if (this.messageIds == null) {
      sb.append("null");
    } else {
      sb.append(this.messageIds);
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

    if (!isSetMessageIds()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'messageIds' is unset! Struct:" + toString());
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

  private static class NetworkManagerCancelResponseStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public NetworkManagerCancelResponseStandardScheme getScheme() {
      return new NetworkManagerCancelResponseStandardScheme();
    }
  }

  private static class NetworkManagerCancelResponseStandardScheme extends org.apache.thrift.scheme.StandardScheme<NetworkManagerCancelResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, NetworkManagerCancelResponse struct) throws org.apache.thrift.TException {
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
          case 3: // MESSAGE_IDS
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map8 = iprot.readMapBegin();
                struct.messageIds = new java.util.HashMap<java.lang.Long,NetworkManagerMessageCancelStatus>(2*_map8.size);
                long _key9;
                @org.apache.thrift.annotation.Nullable NetworkManagerMessageCancelStatus _val10;
                for (int _i11 = 0; _i11 < _map8.size; ++_i11)
                {
                  _key9 = iprot.readI64();
                  _val10 = com.cannontech.messaging.serialization.thrift.generated.NetworkManagerMessageCancelStatus.findByValue(iprot.readI32());
                  struct.messageIds.put(_key9, _val10);
                }
                iprot.readMapEnd();
              }
              struct.setMessageIdsIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, NetworkManagerCancelResponse struct) throws org.apache.thrift.TException {
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
      if (struct.messageIds != null) {
        oprot.writeFieldBegin(MESSAGE_IDS_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I64, org.apache.thrift.protocol.TType.I32, struct.messageIds.size()));
          for (java.util.Map.Entry<java.lang.Long, NetworkManagerMessageCancelStatus> _iter12 : struct.messageIds.entrySet())
          {
            oprot.writeI64(_iter12.getKey());
            oprot.writeI32(_iter12.getValue().getValue());
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class NetworkManagerCancelResponseTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public NetworkManagerCancelResponseTupleScheme getScheme() {
      return new NetworkManagerCancelResponseTupleScheme();
    }
  }

  private static class NetworkManagerCancelResponseTupleScheme extends org.apache.thrift.scheme.TupleScheme<NetworkManagerCancelResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, NetworkManagerCancelResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeString(struct.clientGuid);
      oprot.writeI64(struct.sessionId);
      {
        oprot.writeI32(struct.messageIds.size());
        for (java.util.Map.Entry<java.lang.Long, NetworkManagerMessageCancelStatus> _iter13 : struct.messageIds.entrySet())
        {
          oprot.writeI64(_iter13.getKey());
          oprot.writeI32(_iter13.getValue().getValue());
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, NetworkManagerCancelResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.clientGuid = iprot.readString();
      struct.setClientGuidIsSet(true);
      struct.sessionId = iprot.readI64();
      struct.setSessionIdIsSet(true);
      {
        org.apache.thrift.protocol.TMap _map14 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I64, org.apache.thrift.protocol.TType.I32, iprot.readI32());
        struct.messageIds = new java.util.HashMap<java.lang.Long,NetworkManagerMessageCancelStatus>(2*_map14.size);
        long _key15;
        @org.apache.thrift.annotation.Nullable NetworkManagerMessageCancelStatus _val16;
        for (int _i17 = 0; _i17 < _map14.size; ++_i17)
        {
          _key15 = iprot.readI64();
          _val16 = com.cannontech.messaging.serialization.thrift.generated.NetworkManagerMessageCancelStatus.findByValue(iprot.readI32());
          struct.messageIds.put(_key15, _val16);
        }
      }
      struct.setMessageIdsIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

