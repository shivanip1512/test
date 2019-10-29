/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated.meterProgramming;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-10-28")
public class MeterProgramStatusArchiveRequest implements org.apache.thrift.TBase<MeterProgramStatusArchiveRequest, MeterProgramStatusArchiveRequest._Fields>, java.io.Serializable, Cloneable, Comparable<MeterProgramStatusArchiveRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MeterProgramStatusArchiveRequest");

  private static final org.apache.thrift.protocol.TField RFN_IDENTIFIER_FIELD_DESC = new org.apache.thrift.protocol.TField("rfnIdentifier", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField CONFIGURATION_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("configurationId", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField STATUS_FIELD_DESC = new org.apache.thrift.protocol.TField("status", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField ERROR_FIELD_DESC = new org.apache.thrift.protocol.TField("error", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField TIME_STAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("timeStamp", org.apache.thrift.protocol.TType.I64, (short)5);
  private static final org.apache.thrift.protocol.TField SOURCE_FIELD_DESC = new org.apache.thrift.protocol.TField("source", org.apache.thrift.protocol.TType.I32, (short)6);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new MeterProgramStatusArchiveRequestStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new MeterProgramStatusArchiveRequestTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier rfnIdentifier; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String configurationId; // optional
  private @org.apache.thrift.annotation.Nullable ProgrammingStatus status; // required
  private int error; // required
  private long timeStamp; // required
  private @org.apache.thrift.annotation.Nullable Source source; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    RFN_IDENTIFIER((short)1, "rfnIdentifier"),
    CONFIGURATION_ID((short)2, "configurationId"),
    /**
     * 
     * @see ProgrammingStatus
     */
    STATUS((short)3, "status"),
    ERROR((short)4, "error"),
    TIME_STAMP((short)5, "timeStamp"),
    /**
     * 
     * @see Source
     */
    SOURCE((short)6, "source");

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
        case 1: // RFN_IDENTIFIER
          return RFN_IDENTIFIER;
        case 2: // CONFIGURATION_ID
          return CONFIGURATION_ID;
        case 3: // STATUS
          return STATUS;
        case 4: // ERROR
          return ERROR;
        case 5: // TIME_STAMP
          return TIME_STAMP;
        case 6: // SOURCE
          return SOURCE;
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
  private static final int __ERROR_ISSET_ID = 0;
  private static final int __TIMESTAMP_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.CONFIGURATION_ID};
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.RFN_IDENTIFIER, new org.apache.thrift.meta_data.FieldMetaData("rfnIdentifier", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier.class)));
    tmpMap.put(_Fields.CONFIGURATION_ID, new org.apache.thrift.meta_data.FieldMetaData("configurationId", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.STATUS, new org.apache.thrift.meta_data.FieldMetaData("status", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, ProgrammingStatus.class)));
    tmpMap.put(_Fields.ERROR, new org.apache.thrift.meta_data.FieldMetaData("error", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.TIME_STAMP, new org.apache.thrift.meta_data.FieldMetaData("timeStamp", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.SOURCE, new org.apache.thrift.meta_data.FieldMetaData("source", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, Source.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MeterProgramStatusArchiveRequest.class, metaDataMap);
  }

  public MeterProgramStatusArchiveRequest() {
  }

  public MeterProgramStatusArchiveRequest(
    com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier rfnIdentifier,
    ProgrammingStatus status,
    int error,
    long timeStamp,
    Source source)
  {
    this();
    this.rfnIdentifier = rfnIdentifier;
    this.status = status;
    this.error = error;
    setErrorIsSet(true);
    this.timeStamp = timeStamp;
    setTimeStampIsSet(true);
    this.source = source;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MeterProgramStatusArchiveRequest(MeterProgramStatusArchiveRequest other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetRfnIdentifier()) {
      this.rfnIdentifier = new com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier(other.rfnIdentifier);
    }
    if (other.isSetConfigurationId()) {
      this.configurationId = other.configurationId;
    }
    if (other.isSetStatus()) {
      this.status = other.status;
    }
    this.error = other.error;
    this.timeStamp = other.timeStamp;
    if (other.isSetSource()) {
      this.source = other.source;
    }
  }

  public MeterProgramStatusArchiveRequest deepCopy() {
    return new MeterProgramStatusArchiveRequest(this);
  }

  @Override
  public void clear() {
    this.rfnIdentifier = null;
    this.configurationId = null;
    this.status = null;
    setErrorIsSet(false);
    this.error = 0;
    setTimeStampIsSet(false);
    this.timeStamp = 0;
    this.source = null;
  }

  @org.apache.thrift.annotation.Nullable
  public com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier getRfnIdentifier() {
    return this.rfnIdentifier;
  }

  public void setRfnIdentifier(@org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier rfnIdentifier) {
    this.rfnIdentifier = rfnIdentifier;
  }

  public void unsetRfnIdentifier() {
    this.rfnIdentifier = null;
  }

  /** Returns true if field rfnIdentifier is set (has been assigned a value) and false otherwise */
  public boolean isSetRfnIdentifier() {
    return this.rfnIdentifier != null;
  }

  public void setRfnIdentifierIsSet(boolean value) {
    if (!value) {
      this.rfnIdentifier = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getConfigurationId() {
    return this.configurationId;
  }

  public void setConfigurationId(@org.apache.thrift.annotation.Nullable java.lang.String configurationId) {
    this.configurationId = configurationId;
  }

  public void unsetConfigurationId() {
    this.configurationId = null;
  }

  /** Returns true if field configurationId is set (has been assigned a value) and false otherwise */
  public boolean isSetConfigurationId() {
    return this.configurationId != null;
  }

  public void setConfigurationIdIsSet(boolean value) {
    if (!value) {
      this.configurationId = null;
    }
  }

  /**
   * 
   * @see ProgrammingStatus
   */
  @org.apache.thrift.annotation.Nullable
  public ProgrammingStatus getStatus() {
    return this.status;
  }

  /**
   * 
   * @see ProgrammingStatus
   */
  public void setStatus(@org.apache.thrift.annotation.Nullable ProgrammingStatus status) {
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

  public int getError() {
    return this.error;
  }

  public void setError(int error) {
    this.error = error;
    setErrorIsSet(true);
  }

  public void unsetError() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __ERROR_ISSET_ID);
  }

  /** Returns true if field error is set (has been assigned a value) and false otherwise */
  public boolean isSetError() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __ERROR_ISSET_ID);
  }

  public void setErrorIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __ERROR_ISSET_ID, value);
  }

  public long getTimeStamp() {
    return this.timeStamp;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
    setTimeStampIsSet(true);
  }

  public void unsetTimeStamp() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __TIMESTAMP_ISSET_ID);
  }

  /** Returns true if field timeStamp is set (has been assigned a value) and false otherwise */
  public boolean isSetTimeStamp() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __TIMESTAMP_ISSET_ID);
  }

  public void setTimeStampIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __TIMESTAMP_ISSET_ID, value);
  }

  /**
   * 
   * @see Source
   */
  @org.apache.thrift.annotation.Nullable
  public Source getSource() {
    return this.source;
  }

  /**
   * 
   * @see Source
   */
  public void setSource(@org.apache.thrift.annotation.Nullable Source source) {
    this.source = source;
  }

  public void unsetSource() {
    this.source = null;
  }

  /** Returns true if field source is set (has been assigned a value) and false otherwise */
  public boolean isSetSource() {
    return this.source != null;
  }

  public void setSourceIsSet(boolean value) {
    if (!value) {
      this.source = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case RFN_IDENTIFIER:
      if (value == null) {
        unsetRfnIdentifier();
      } else {
        setRfnIdentifier((com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier)value);
      }
      break;

    case CONFIGURATION_ID:
      if (value == null) {
        unsetConfigurationId();
      } else {
        setConfigurationId((java.lang.String)value);
      }
      break;

    case STATUS:
      if (value == null) {
        unsetStatus();
      } else {
        setStatus((ProgrammingStatus)value);
      }
      break;

    case ERROR:
      if (value == null) {
        unsetError();
      } else {
        setError((java.lang.Integer)value);
      }
      break;

    case TIME_STAMP:
      if (value == null) {
        unsetTimeStamp();
      } else {
        setTimeStamp((java.lang.Long)value);
      }
      break;

    case SOURCE:
      if (value == null) {
        unsetSource();
      } else {
        setSource((Source)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case RFN_IDENTIFIER:
      return getRfnIdentifier();

    case CONFIGURATION_ID:
      return getConfigurationId();

    case STATUS:
      return getStatus();

    case ERROR:
      return getError();

    case TIME_STAMP:
      return getTimeStamp();

    case SOURCE:
      return getSource();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case RFN_IDENTIFIER:
      return isSetRfnIdentifier();
    case CONFIGURATION_ID:
      return isSetConfigurationId();
    case STATUS:
      return isSetStatus();
    case ERROR:
      return isSetError();
    case TIME_STAMP:
      return isSetTimeStamp();
    case SOURCE:
      return isSetSource();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof MeterProgramStatusArchiveRequest)
      return this.equals((MeterProgramStatusArchiveRequest)that);
    return false;
  }

  public boolean equals(MeterProgramStatusArchiveRequest that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_rfnIdentifier = true && this.isSetRfnIdentifier();
    boolean that_present_rfnIdentifier = true && that.isSetRfnIdentifier();
    if (this_present_rfnIdentifier || that_present_rfnIdentifier) {
      if (!(this_present_rfnIdentifier && that_present_rfnIdentifier))
        return false;
      if (!this.rfnIdentifier.equals(that.rfnIdentifier))
        return false;
    }

    boolean this_present_configurationId = true && this.isSetConfigurationId();
    boolean that_present_configurationId = true && that.isSetConfigurationId();
    if (this_present_configurationId || that_present_configurationId) {
      if (!(this_present_configurationId && that_present_configurationId))
        return false;
      if (!this.configurationId.equals(that.configurationId))
        return false;
    }

    boolean this_present_status = true && this.isSetStatus();
    boolean that_present_status = true && that.isSetStatus();
    if (this_present_status || that_present_status) {
      if (!(this_present_status && that_present_status))
        return false;
      if (!this.status.equals(that.status))
        return false;
    }

    boolean this_present_error = true;
    boolean that_present_error = true;
    if (this_present_error || that_present_error) {
      if (!(this_present_error && that_present_error))
        return false;
      if (this.error != that.error)
        return false;
    }

    boolean this_present_timeStamp = true;
    boolean that_present_timeStamp = true;
    if (this_present_timeStamp || that_present_timeStamp) {
      if (!(this_present_timeStamp && that_present_timeStamp))
        return false;
      if (this.timeStamp != that.timeStamp)
        return false;
    }

    boolean this_present_source = true && this.isSetSource();
    boolean that_present_source = true && that.isSetSource();
    if (this_present_source || that_present_source) {
      if (!(this_present_source && that_present_source))
        return false;
      if (!this.source.equals(that.source))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetRfnIdentifier()) ? 131071 : 524287);
    if (isSetRfnIdentifier())
      hashCode = hashCode * 8191 + rfnIdentifier.hashCode();

    hashCode = hashCode * 8191 + ((isSetConfigurationId()) ? 131071 : 524287);
    if (isSetConfigurationId())
      hashCode = hashCode * 8191 + configurationId.hashCode();

    hashCode = hashCode * 8191 + ((isSetStatus()) ? 131071 : 524287);
    if (isSetStatus())
      hashCode = hashCode * 8191 + status.getValue();

    hashCode = hashCode * 8191 + error;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(timeStamp);

    hashCode = hashCode * 8191 + ((isSetSource()) ? 131071 : 524287);
    if (isSetSource())
      hashCode = hashCode * 8191 + source.getValue();

    return hashCode;
  }

  @Override
  public int compareTo(MeterProgramStatusArchiveRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetRfnIdentifier()).compareTo(other.isSetRfnIdentifier());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRfnIdentifier()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rfnIdentifier, other.rfnIdentifier);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetConfigurationId()).compareTo(other.isSetConfigurationId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetConfigurationId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.configurationId, other.configurationId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
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
    lastComparison = java.lang.Boolean.valueOf(isSetError()).compareTo(other.isSetError());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetError()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.error, other.error);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetTimeStamp()).compareTo(other.isSetTimeStamp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTimeStamp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.timeStamp, other.timeStamp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetSource()).compareTo(other.isSetSource());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSource()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.source, other.source);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("MeterProgramStatusArchiveRequest(");
    boolean first = true;

    sb.append("rfnIdentifier:");
    if (this.rfnIdentifier == null) {
      sb.append("null");
    } else {
      sb.append(this.rfnIdentifier);
    }
    first = false;
    if (isSetConfigurationId()) {
      if (!first) sb.append(", ");
      sb.append("configurationId:");
      if (this.configurationId == null) {
        sb.append("null");
      } else {
        sb.append(this.configurationId);
      }
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("status:");
    if (this.status == null) {
      sb.append("null");
    } else {
      sb.append(this.status);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("error:");
    sb.append(this.error);
    first = false;
    if (!first) sb.append(", ");
    sb.append("timeStamp:");
    sb.append(this.timeStamp);
    first = false;
    if (!first) sb.append(", ");
    sb.append("source:");
    if (this.source == null) {
      sb.append("null");
    } else {
      sb.append(this.source);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetRfnIdentifier()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'rfnIdentifier' is unset! Struct:" + toString());
    }

    if (!isSetStatus()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'status' is unset! Struct:" + toString());
    }

    if (!isSetError()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'error' is unset! Struct:" + toString());
    }

    if (!isSetTimeStamp()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'timeStamp' is unset! Struct:" + toString());
    }

    if (!isSetSource()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'source' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (rfnIdentifier != null) {
      rfnIdentifier.validate();
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

  private static class MeterProgramStatusArchiveRequestStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public MeterProgramStatusArchiveRequestStandardScheme getScheme() {
      return new MeterProgramStatusArchiveRequestStandardScheme();
    }
  }

  private static class MeterProgramStatusArchiveRequestStandardScheme extends org.apache.thrift.scheme.StandardScheme<MeterProgramStatusArchiveRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, MeterProgramStatusArchiveRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // RFN_IDENTIFIER
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.rfnIdentifier = new com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier();
              struct.rfnIdentifier.read(iprot);
              struct.setRfnIdentifierIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // CONFIGURATION_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.configurationId = iprot.readString();
              struct.setConfigurationIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // STATUS
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.status = com.cannontech.messaging.serialization.thrift.generated.meterProgramming.ProgrammingStatus.findByValue(iprot.readI32());
              struct.setStatusIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // ERROR
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.error = iprot.readI32();
              struct.setErrorIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // TIME_STAMP
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.timeStamp = iprot.readI64();
              struct.setTimeStampIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // SOURCE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.source = com.cannontech.messaging.serialization.thrift.generated.meterProgramming.Source.findByValue(iprot.readI32());
              struct.setSourceIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, MeterProgramStatusArchiveRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.rfnIdentifier != null) {
        oprot.writeFieldBegin(RFN_IDENTIFIER_FIELD_DESC);
        struct.rfnIdentifier.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.configurationId != null) {
        if (struct.isSetConfigurationId()) {
          oprot.writeFieldBegin(CONFIGURATION_ID_FIELD_DESC);
          oprot.writeString(struct.configurationId);
          oprot.writeFieldEnd();
        }
      }
      if (struct.status != null) {
        oprot.writeFieldBegin(STATUS_FIELD_DESC);
        oprot.writeI32(struct.status.getValue());
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(ERROR_FIELD_DESC);
      oprot.writeI32(struct.error);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(TIME_STAMP_FIELD_DESC);
      oprot.writeI64(struct.timeStamp);
      oprot.writeFieldEnd();
      if (struct.source != null) {
        oprot.writeFieldBegin(SOURCE_FIELD_DESC);
        oprot.writeI32(struct.source.getValue());
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MeterProgramStatusArchiveRequestTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public MeterProgramStatusArchiveRequestTupleScheme getScheme() {
      return new MeterProgramStatusArchiveRequestTupleScheme();
    }
  }

  private static class MeterProgramStatusArchiveRequestTupleScheme extends org.apache.thrift.scheme.TupleScheme<MeterProgramStatusArchiveRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, MeterProgramStatusArchiveRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.rfnIdentifier.write(oprot);
      oprot.writeI32(struct.status.getValue());
      oprot.writeI32(struct.error);
      oprot.writeI64(struct.timeStamp);
      oprot.writeI32(struct.source.getValue());
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetConfigurationId()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetConfigurationId()) {
        oprot.writeString(struct.configurationId);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, MeterProgramStatusArchiveRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.rfnIdentifier = new com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier();
      struct.rfnIdentifier.read(iprot);
      struct.setRfnIdentifierIsSet(true);
      struct.status = com.cannontech.messaging.serialization.thrift.generated.meterProgramming.ProgrammingStatus.findByValue(iprot.readI32());
      struct.setStatusIsSet(true);
      struct.error = iprot.readI32();
      struct.setErrorIsSet(true);
      struct.timeStamp = iprot.readI64();
      struct.setTimeStampIsSet(true);
      struct.source = com.cannontech.messaging.serialization.thrift.generated.meterProgramming.Source.findByValue(iprot.readI32());
      struct.setSourceIsSet(true);
      java.util.BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.configurationId = iprot.readString();
        struct.setConfigurationIdIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

