/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.16.0)", date = "2022-05-31")
public class EdgeDrBroadcastResponse implements org.apache.thrift.TBase<EdgeDrBroadcastResponse, EdgeDrBroadcastResponse._Fields>, java.io.Serializable, Cloneable, Comparable<EdgeDrBroadcastResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("EdgeDrBroadcastResponse");

  private static final org.apache.thrift.protocol.TField MESSAGE_GUID_FIELD_DESC = new org.apache.thrift.protocol.TField("messageGuid", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField ERROR_FIELD_DESC = new org.apache.thrift.protocol.TField("error", org.apache.thrift.protocol.TType.STRUCT, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new EdgeDrBroadcastResponseStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new EdgeDrBroadcastResponseTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable java.lang.String messageGuid; // required
  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.EdgeDrError error; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MESSAGE_GUID((short)1, "messageGuid"),
    ERROR((short)2, "error");

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
        case 2: // ERROR
          return ERROR;
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
  private static final _Fields optionals[] = {_Fields.ERROR};
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.MESSAGE_GUID, new org.apache.thrift.meta_data.FieldMetaData("messageGuid", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ERROR, new org.apache.thrift.meta_data.FieldMetaData("error", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.EdgeDrError.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(EdgeDrBroadcastResponse.class, metaDataMap);
  }

  public EdgeDrBroadcastResponse() {
  }

  public EdgeDrBroadcastResponse(
    java.lang.String messageGuid)
  {
    this();
    this.messageGuid = messageGuid;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public EdgeDrBroadcastResponse(EdgeDrBroadcastResponse other) {
    if (other.isSetMessageGuid()) {
      this.messageGuid = other.messageGuid;
    }
    if (other.isSetError()) {
      this.error = new com.cannontech.messaging.serialization.thrift.generated.EdgeDrError(other.error);
    }
  }

  public EdgeDrBroadcastResponse deepCopy() {
    return new EdgeDrBroadcastResponse(this);
  }

  @Override
  public void clear() {
    this.messageGuid = null;
    this.error = null;
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

  @org.apache.thrift.annotation.Nullable
  public com.cannontech.messaging.serialization.thrift.generated.EdgeDrError getError() {
    return this.error;
  }

  public void setError(@org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.EdgeDrError error) {
    this.error = error;
  }

  public void unsetError() {
    this.error = null;
  }

  /** Returns true if field error is set (has been assigned a value) and false otherwise */
  public boolean isSetError() {
    return this.error != null;
  }

  public void setErrorIsSet(boolean value) {
    if (!value) {
      this.error = null;
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

    case ERROR:
      if (value == null) {
        unsetError();
      } else {
        setError((com.cannontech.messaging.serialization.thrift.generated.EdgeDrError)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case MESSAGE_GUID:
      return getMessageGuid();

    case ERROR:
      return getError();

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
    case ERROR:
      return isSetError();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof EdgeDrBroadcastResponse)
      return this.equals((EdgeDrBroadcastResponse)that);
    return false;
  }

  public boolean equals(EdgeDrBroadcastResponse that) {
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

    boolean this_present_error = true && this.isSetError();
    boolean that_present_error = true && that.isSetError();
    if (this_present_error || that_present_error) {
      if (!(this_present_error && that_present_error))
        return false;
      if (!this.error.equals(that.error))
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

    hashCode = hashCode * 8191 + ((isSetError()) ? 131071 : 524287);
    if (isSetError())
      hashCode = hashCode * 8191 + error.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(EdgeDrBroadcastResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetMessageGuid(), other.isSetMessageGuid());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessageGuid()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.messageGuid, other.messageGuid);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetError(), other.isSetError());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetError()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.error, other.error);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("EdgeDrBroadcastResponse(");
    boolean first = true;

    sb.append("messageGuid:");
    if (this.messageGuid == null) {
      sb.append("null");
    } else {
      sb.append(this.messageGuid);
    }
    first = false;
    if (isSetError()) {
      if (!first) sb.append(", ");
      sb.append("error:");
      if (this.error == null) {
        sb.append("null");
      } else {
        sb.append(this.error);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetMessageGuid()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'messageGuid' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (error != null) {
      error.validate();
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

  private static class EdgeDrBroadcastResponseStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public EdgeDrBroadcastResponseStandardScheme getScheme() {
      return new EdgeDrBroadcastResponseStandardScheme();
    }
  }

  private static class EdgeDrBroadcastResponseStandardScheme extends org.apache.thrift.scheme.StandardScheme<EdgeDrBroadcastResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, EdgeDrBroadcastResponse struct) throws org.apache.thrift.TException {
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
          case 2: // ERROR
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.error = new com.cannontech.messaging.serialization.thrift.generated.EdgeDrError();
              struct.error.read(iprot);
              struct.setErrorIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, EdgeDrBroadcastResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.messageGuid != null) {
        oprot.writeFieldBegin(MESSAGE_GUID_FIELD_DESC);
        oprot.writeString(struct.messageGuid);
        oprot.writeFieldEnd();
      }
      if (struct.error != null) {
        if (struct.isSetError()) {
          oprot.writeFieldBegin(ERROR_FIELD_DESC);
          struct.error.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class EdgeDrBroadcastResponseTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public EdgeDrBroadcastResponseTupleScheme getScheme() {
      return new EdgeDrBroadcastResponseTupleScheme();
    }
  }

  private static class EdgeDrBroadcastResponseTupleScheme extends org.apache.thrift.scheme.TupleScheme<EdgeDrBroadcastResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, EdgeDrBroadcastResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeString(struct.messageGuid);
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetError()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetError()) {
        struct.error.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, EdgeDrBroadcastResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.messageGuid = iprot.readString();
      struct.setMessageGuidIsSet(true);
      java.util.BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.error = new com.cannontech.messaging.serialization.thrift.generated.EdgeDrError();
        struct.error.read(iprot);
        struct.setErrorIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

