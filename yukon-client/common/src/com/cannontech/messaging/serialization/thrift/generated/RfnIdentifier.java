/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.16.0)", date = "2022-05-31")
public class RfnIdentifier implements org.apache.thrift.TBase<RfnIdentifier, RfnIdentifier._Fields>, java.io.Serializable, Cloneable, Comparable<RfnIdentifier> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RfnIdentifier");

  private static final org.apache.thrift.protocol.TField SENSOR_MANUFACTURER_FIELD_DESC = new org.apache.thrift.protocol.TField("sensorManufacturer", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField SENSOR_MODEL_FIELD_DESC = new org.apache.thrift.protocol.TField("sensorModel", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField SENSOR_SERIAL_NUMBER_FIELD_DESC = new org.apache.thrift.protocol.TField("sensorSerialNumber", org.apache.thrift.protocol.TType.STRING, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new RfnIdentifierStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new RfnIdentifierTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable java.lang.String sensorManufacturer; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String sensorModel; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String sensorSerialNumber; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    SENSOR_MANUFACTURER((short)1, "sensorManufacturer"),
    SENSOR_MODEL((short)2, "sensorModel"),
    SENSOR_SERIAL_NUMBER((short)3, "sensorSerialNumber");

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
        case 1: // SENSOR_MANUFACTURER
          return SENSOR_MANUFACTURER;
        case 2: // SENSOR_MODEL
          return SENSOR_MODEL;
        case 3: // SENSOR_SERIAL_NUMBER
          return SENSOR_SERIAL_NUMBER;
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
    tmpMap.put(_Fields.SENSOR_MANUFACTURER, new org.apache.thrift.meta_data.FieldMetaData("sensorManufacturer", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.SENSOR_MODEL, new org.apache.thrift.meta_data.FieldMetaData("sensorModel", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.SENSOR_SERIAL_NUMBER, new org.apache.thrift.meta_data.FieldMetaData("sensorSerialNumber", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RfnIdentifier.class, metaDataMap);
  }

  public RfnIdentifier() {
  }

  public RfnIdentifier(
    java.lang.String sensorManufacturer,
    java.lang.String sensorModel,
    java.lang.String sensorSerialNumber)
  {
    this();
    this.sensorManufacturer = sensorManufacturer;
    this.sensorModel = sensorModel;
    this.sensorSerialNumber = sensorSerialNumber;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RfnIdentifier(RfnIdentifier other) {
    if (other.isSetSensorManufacturer()) {
      this.sensorManufacturer = other.sensorManufacturer;
    }
    if (other.isSetSensorModel()) {
      this.sensorModel = other.sensorModel;
    }
    if (other.isSetSensorSerialNumber()) {
      this.sensorSerialNumber = other.sensorSerialNumber;
    }
  }

  public RfnIdentifier deepCopy() {
    return new RfnIdentifier(this);
  }

  @Override
  public void clear() {
    this.sensorManufacturer = null;
    this.sensorModel = null;
    this.sensorSerialNumber = null;
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getSensorManufacturer() {
    return this.sensorManufacturer;
  }

  public void setSensorManufacturer(@org.apache.thrift.annotation.Nullable java.lang.String sensorManufacturer) {
    this.sensorManufacturer = sensorManufacturer;
  }

  public void unsetSensorManufacturer() {
    this.sensorManufacturer = null;
  }

  /** Returns true if field sensorManufacturer is set (has been assigned a value) and false otherwise */
  public boolean isSetSensorManufacturer() {
    return this.sensorManufacturer != null;
  }

  public void setSensorManufacturerIsSet(boolean value) {
    if (!value) {
      this.sensorManufacturer = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getSensorModel() {
    return this.sensorModel;
  }

  public void setSensorModel(@org.apache.thrift.annotation.Nullable java.lang.String sensorModel) {
    this.sensorModel = sensorModel;
  }

  public void unsetSensorModel() {
    this.sensorModel = null;
  }

  /** Returns true if field sensorModel is set (has been assigned a value) and false otherwise */
  public boolean isSetSensorModel() {
    return this.sensorModel != null;
  }

  public void setSensorModelIsSet(boolean value) {
    if (!value) {
      this.sensorModel = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getSensorSerialNumber() {
    return this.sensorSerialNumber;
  }

  public void setSensorSerialNumber(@org.apache.thrift.annotation.Nullable java.lang.String sensorSerialNumber) {
    this.sensorSerialNumber = sensorSerialNumber;
  }

  public void unsetSensorSerialNumber() {
    this.sensorSerialNumber = null;
  }

  /** Returns true if field sensorSerialNumber is set (has been assigned a value) and false otherwise */
  public boolean isSetSensorSerialNumber() {
    return this.sensorSerialNumber != null;
  }

  public void setSensorSerialNumberIsSet(boolean value) {
    if (!value) {
      this.sensorSerialNumber = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case SENSOR_MANUFACTURER:
      if (value == null) {
        unsetSensorManufacturer();
      } else {
        setSensorManufacturer((java.lang.String)value);
      }
      break;

    case SENSOR_MODEL:
      if (value == null) {
        unsetSensorModel();
      } else {
        setSensorModel((java.lang.String)value);
      }
      break;

    case SENSOR_SERIAL_NUMBER:
      if (value == null) {
        unsetSensorSerialNumber();
      } else {
        setSensorSerialNumber((java.lang.String)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case SENSOR_MANUFACTURER:
      return getSensorManufacturer();

    case SENSOR_MODEL:
      return getSensorModel();

    case SENSOR_SERIAL_NUMBER:
      return getSensorSerialNumber();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case SENSOR_MANUFACTURER:
      return isSetSensorManufacturer();
    case SENSOR_MODEL:
      return isSetSensorModel();
    case SENSOR_SERIAL_NUMBER:
      return isSetSensorSerialNumber();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof RfnIdentifier)
      return this.equals((RfnIdentifier)that);
    return false;
  }

  public boolean equals(RfnIdentifier that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_sensorManufacturer = true && this.isSetSensorManufacturer();
    boolean that_present_sensorManufacturer = true && that.isSetSensorManufacturer();
    if (this_present_sensorManufacturer || that_present_sensorManufacturer) {
      if (!(this_present_sensorManufacturer && that_present_sensorManufacturer))
        return false;
      if (!this.sensorManufacturer.equals(that.sensorManufacturer))
        return false;
    }

    boolean this_present_sensorModel = true && this.isSetSensorModel();
    boolean that_present_sensorModel = true && that.isSetSensorModel();
    if (this_present_sensorModel || that_present_sensorModel) {
      if (!(this_present_sensorModel && that_present_sensorModel))
        return false;
      if (!this.sensorModel.equals(that.sensorModel))
        return false;
    }

    boolean this_present_sensorSerialNumber = true && this.isSetSensorSerialNumber();
    boolean that_present_sensorSerialNumber = true && that.isSetSensorSerialNumber();
    if (this_present_sensorSerialNumber || that_present_sensorSerialNumber) {
      if (!(this_present_sensorSerialNumber && that_present_sensorSerialNumber))
        return false;
      if (!this.sensorSerialNumber.equals(that.sensorSerialNumber))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetSensorManufacturer()) ? 131071 : 524287);
    if (isSetSensorManufacturer())
      hashCode = hashCode * 8191 + sensorManufacturer.hashCode();

    hashCode = hashCode * 8191 + ((isSetSensorModel()) ? 131071 : 524287);
    if (isSetSensorModel())
      hashCode = hashCode * 8191 + sensorModel.hashCode();

    hashCode = hashCode * 8191 + ((isSetSensorSerialNumber()) ? 131071 : 524287);
    if (isSetSensorSerialNumber())
      hashCode = hashCode * 8191 + sensorSerialNumber.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(RfnIdentifier other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetSensorManufacturer(), other.isSetSensorManufacturer());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSensorManufacturer()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.sensorManufacturer, other.sensorManufacturer);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetSensorModel(), other.isSetSensorModel());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSensorModel()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.sensorModel, other.sensorModel);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetSensorSerialNumber(), other.isSetSensorSerialNumber());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSensorSerialNumber()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.sensorSerialNumber, other.sensorSerialNumber);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("RfnIdentifier(");
    boolean first = true;

    sb.append("sensorManufacturer:");
    if (this.sensorManufacturer == null) {
      sb.append("null");
    } else {
      sb.append(this.sensorManufacturer);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("sensorModel:");
    if (this.sensorModel == null) {
      sb.append("null");
    } else {
      sb.append(this.sensorModel);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("sensorSerialNumber:");
    if (this.sensorSerialNumber == null) {
      sb.append("null");
    } else {
      sb.append(this.sensorSerialNumber);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetSensorManufacturer()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'sensorManufacturer' is unset! Struct:" + toString());
    }

    if (!isSetSensorModel()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'sensorModel' is unset! Struct:" + toString());
    }

    if (!isSetSensorSerialNumber()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'sensorSerialNumber' is unset! Struct:" + toString());
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

  private static class RfnIdentifierStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RfnIdentifierStandardScheme getScheme() {
      return new RfnIdentifierStandardScheme();
    }
  }

  private static class RfnIdentifierStandardScheme extends org.apache.thrift.scheme.StandardScheme<RfnIdentifier> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RfnIdentifier struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // SENSOR_MANUFACTURER
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.sensorManufacturer = iprot.readString();
              struct.setSensorManufacturerIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // SENSOR_MODEL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.sensorModel = iprot.readString();
              struct.setSensorModelIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // SENSOR_SERIAL_NUMBER
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.sensorSerialNumber = iprot.readString();
              struct.setSensorSerialNumberIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, RfnIdentifier struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.sensorManufacturer != null) {
        oprot.writeFieldBegin(SENSOR_MANUFACTURER_FIELD_DESC);
        oprot.writeString(struct.sensorManufacturer);
        oprot.writeFieldEnd();
      }
      if (struct.sensorModel != null) {
        oprot.writeFieldBegin(SENSOR_MODEL_FIELD_DESC);
        oprot.writeString(struct.sensorModel);
        oprot.writeFieldEnd();
      }
      if (struct.sensorSerialNumber != null) {
        oprot.writeFieldBegin(SENSOR_SERIAL_NUMBER_FIELD_DESC);
        oprot.writeString(struct.sensorSerialNumber);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RfnIdentifierTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RfnIdentifierTupleScheme getScheme() {
      return new RfnIdentifierTupleScheme();
    }
  }

  private static class RfnIdentifierTupleScheme extends org.apache.thrift.scheme.TupleScheme<RfnIdentifier> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RfnIdentifier struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeString(struct.sensorManufacturer);
      oprot.writeString(struct.sensorModel);
      oprot.writeString(struct.sensorSerialNumber);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RfnIdentifier struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.sensorManufacturer = iprot.readString();
      struct.setSensorManufacturerIsSet(true);
      struct.sensorModel = iprot.readString();
      struct.setSensorModelIsSet(true);
      struct.sensorSerialNumber = iprot.readString();
      struct.setSensorSerialNumberIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

