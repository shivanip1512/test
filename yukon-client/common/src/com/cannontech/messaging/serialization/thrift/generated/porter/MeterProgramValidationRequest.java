/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated.porter;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.16.0)", date = "2022-05-31")
public class MeterProgramValidationRequest implements org.apache.thrift.TBase<MeterProgramValidationRequest, MeterProgramValidationRequest._Fields>, java.io.Serializable, Cloneable, Comparable<MeterProgramValidationRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MeterProgramValidationRequest");

  private static final org.apache.thrift.protocol.TField _METER_PROGRAM_GUID_FIELD_DESC = new org.apache.thrift.protocol.TField("_meterProgramGuid", org.apache.thrift.protocol.TType.STRING, (short)1);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new MeterProgramValidationRequestStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new MeterProgramValidationRequestTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable java.lang.String _meterProgramGuid; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _METER_PROGRAM_GUID((short)1, "_meterProgramGuid");

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
        case 1: // _METER_PROGRAM_GUID
          return _METER_PROGRAM_GUID;
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
    tmpMap.put(_Fields._METER_PROGRAM_GUID, new org.apache.thrift.meta_data.FieldMetaData("_meterProgramGuid", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MeterProgramValidationRequest.class, metaDataMap);
  }

  public MeterProgramValidationRequest() {
  }

  public MeterProgramValidationRequest(
    java.lang.String _meterProgramGuid)
  {
    this();
    this._meterProgramGuid = _meterProgramGuid;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MeterProgramValidationRequest(MeterProgramValidationRequest other) {
    if (other.isSet_meterProgramGuid()) {
      this._meterProgramGuid = other._meterProgramGuid;
    }
  }

  public MeterProgramValidationRequest deepCopy() {
    return new MeterProgramValidationRequest(this);
  }

  @Override
  public void clear() {
    this._meterProgramGuid = null;
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_meterProgramGuid() {
    return this._meterProgramGuid;
  }

  public void set_meterProgramGuid(@org.apache.thrift.annotation.Nullable java.lang.String _meterProgramGuid) {
    this._meterProgramGuid = _meterProgramGuid;
  }

  public void unset_meterProgramGuid() {
    this._meterProgramGuid = null;
  }

  /** Returns true if field _meterProgramGuid is set (has been assigned a value) and false otherwise */
  public boolean isSet_meterProgramGuid() {
    return this._meterProgramGuid != null;
  }

  public void set_meterProgramGuidIsSet(boolean value) {
    if (!value) {
      this._meterProgramGuid = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _METER_PROGRAM_GUID:
      if (value == null) {
        unset_meterProgramGuid();
      } else {
        set_meterProgramGuid((java.lang.String)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _METER_PROGRAM_GUID:
      return get_meterProgramGuid();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case _METER_PROGRAM_GUID:
      return isSet_meterProgramGuid();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof MeterProgramValidationRequest)
      return this.equals((MeterProgramValidationRequest)that);
    return false;
  }

  public boolean equals(MeterProgramValidationRequest that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present__meterProgramGuid = true && this.isSet_meterProgramGuid();
    boolean that_present__meterProgramGuid = true && that.isSet_meterProgramGuid();
    if (this_present__meterProgramGuid || that_present__meterProgramGuid) {
      if (!(this_present__meterProgramGuid && that_present__meterProgramGuid))
        return false;
      if (!this._meterProgramGuid.equals(that._meterProgramGuid))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSet_meterProgramGuid()) ? 131071 : 524287);
    if (isSet_meterProgramGuid())
      hashCode = hashCode * 8191 + _meterProgramGuid.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(MeterProgramValidationRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSet_meterProgramGuid(), other.isSet_meterProgramGuid());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_meterProgramGuid()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._meterProgramGuid, other._meterProgramGuid);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("MeterProgramValidationRequest(");
    boolean first = true;

    sb.append("_meterProgramGuid:");
    if (this._meterProgramGuid == null) {
      sb.append("null");
    } else {
      sb.append(this._meterProgramGuid);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_meterProgramGuid()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_meterProgramGuid' is unset! Struct:" + toString());
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

  private static class MeterProgramValidationRequestStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public MeterProgramValidationRequestStandardScheme getScheme() {
      return new MeterProgramValidationRequestStandardScheme();
    }
  }

  private static class MeterProgramValidationRequestStandardScheme extends org.apache.thrift.scheme.StandardScheme<MeterProgramValidationRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, MeterProgramValidationRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // _METER_PROGRAM_GUID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._meterProgramGuid = iprot.readString();
              struct.set_meterProgramGuidIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, MeterProgramValidationRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._meterProgramGuid != null) {
        oprot.writeFieldBegin(_METER_PROGRAM_GUID_FIELD_DESC);
        oprot.writeString(struct._meterProgramGuid);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MeterProgramValidationRequestTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public MeterProgramValidationRequestTupleScheme getScheme() {
      return new MeterProgramValidationRequestTupleScheme();
    }
  }

  private static class MeterProgramValidationRequestTupleScheme extends org.apache.thrift.scheme.TupleScheme<MeterProgramValidationRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, MeterProgramValidationRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeString(struct._meterProgramGuid);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, MeterProgramValidationRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._meterProgramGuid = iprot.readString();
      struct.set_meterProgramGuidIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

