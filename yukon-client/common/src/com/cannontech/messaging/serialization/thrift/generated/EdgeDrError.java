/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2022-04-25")
public class EdgeDrError implements org.apache.thrift.TBase<EdgeDrError, EdgeDrError._Fields>, java.io.Serializable, Cloneable, Comparable<EdgeDrError> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("EdgeDrError");

  private static final org.apache.thrift.protocol.TField ERROR_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("errorType", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField ERROR_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("errorMessage", org.apache.thrift.protocol.TType.STRING, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new EdgeDrErrorStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new EdgeDrErrorTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable EdgeDrErrorType errorType; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String errorMessage; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see EdgeDrErrorType
     */
    ERROR_TYPE((short)1, "errorType"),
    ERROR_MESSAGE((short)2, "errorMessage");

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
        case 1: // ERROR_TYPE
          return ERROR_TYPE;
        case 2: // ERROR_MESSAGE
          return ERROR_MESSAGE;
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
    tmpMap.put(_Fields.ERROR_TYPE, new org.apache.thrift.meta_data.FieldMetaData("errorType", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, EdgeDrErrorType.class)));
    tmpMap.put(_Fields.ERROR_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("errorMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(EdgeDrError.class, metaDataMap);
  }

  public EdgeDrError() {
  }

  public EdgeDrError(
    EdgeDrErrorType errorType,
    java.lang.String errorMessage)
  {
    this();
    this.errorType = errorType;
    this.errorMessage = errorMessage;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public EdgeDrError(EdgeDrError other) {
    if (other.isSetErrorType()) {
      this.errorType = other.errorType;
    }
    if (other.isSetErrorMessage()) {
      this.errorMessage = other.errorMessage;
    }
  }

  public EdgeDrError deepCopy() {
    return new EdgeDrError(this);
  }

  @Override
  public void clear() {
    this.errorType = null;
    this.errorMessage = null;
  }

  /**
   * 
   * @see EdgeDrErrorType
   */
  @org.apache.thrift.annotation.Nullable
  public EdgeDrErrorType getErrorType() {
    return this.errorType;
  }

  /**
   * 
   * @see EdgeDrErrorType
   */
  public void setErrorType(@org.apache.thrift.annotation.Nullable EdgeDrErrorType errorType) {
    this.errorType = errorType;
  }

  public void unsetErrorType() {
    this.errorType = null;
  }

  /** Returns true if field errorType is set (has been assigned a value) and false otherwise */
  public boolean isSetErrorType() {
    return this.errorType != null;
  }

  public void setErrorTypeIsSet(boolean value) {
    if (!value) {
      this.errorType = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getErrorMessage() {
    return this.errorMessage;
  }

  public void setErrorMessage(@org.apache.thrift.annotation.Nullable java.lang.String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public void unsetErrorMessage() {
    this.errorMessage = null;
  }

  /** Returns true if field errorMessage is set (has been assigned a value) and false otherwise */
  public boolean isSetErrorMessage() {
    return this.errorMessage != null;
  }

  public void setErrorMessageIsSet(boolean value) {
    if (!value) {
      this.errorMessage = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case ERROR_TYPE:
      if (value == null) {
        unsetErrorType();
      } else {
        setErrorType((EdgeDrErrorType)value);
      }
      break;

    case ERROR_MESSAGE:
      if (value == null) {
        unsetErrorMessage();
      } else {
        setErrorMessage((java.lang.String)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case ERROR_TYPE:
      return getErrorType();

    case ERROR_MESSAGE:
      return getErrorMessage();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case ERROR_TYPE:
      return isSetErrorType();
    case ERROR_MESSAGE:
      return isSetErrorMessage();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof EdgeDrError)
      return this.equals((EdgeDrError)that);
    return false;
  }

  public boolean equals(EdgeDrError that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_errorType = true && this.isSetErrorType();
    boolean that_present_errorType = true && that.isSetErrorType();
    if (this_present_errorType || that_present_errorType) {
      if (!(this_present_errorType && that_present_errorType))
        return false;
      if (!this.errorType.equals(that.errorType))
        return false;
    }

    boolean this_present_errorMessage = true && this.isSetErrorMessage();
    boolean that_present_errorMessage = true && that.isSetErrorMessage();
    if (this_present_errorMessage || that_present_errorMessage) {
      if (!(this_present_errorMessage && that_present_errorMessage))
        return false;
      if (!this.errorMessage.equals(that.errorMessage))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetErrorType()) ? 131071 : 524287);
    if (isSetErrorType())
      hashCode = hashCode * 8191 + errorType.getValue();

    hashCode = hashCode * 8191 + ((isSetErrorMessage()) ? 131071 : 524287);
    if (isSetErrorMessage())
      hashCode = hashCode * 8191 + errorMessage.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(EdgeDrError other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetErrorType()).compareTo(other.isSetErrorType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetErrorType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.errorType, other.errorType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetErrorMessage()).compareTo(other.isSetErrorMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetErrorMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.errorMessage, other.errorMessage);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("EdgeDrError(");
    boolean first = true;

    sb.append("errorType:");
    if (this.errorType == null) {
      sb.append("null");
    } else {
      sb.append(this.errorType);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("errorMessage:");
    if (this.errorMessage == null) {
      sb.append("null");
    } else {
      sb.append(this.errorMessage);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetErrorType()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'errorType' is unset! Struct:" + toString());
    }

    if (!isSetErrorMessage()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'errorMessage' is unset! Struct:" + toString());
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

  private static class EdgeDrErrorStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public EdgeDrErrorStandardScheme getScheme() {
      return new EdgeDrErrorStandardScheme();
    }
  }

  private static class EdgeDrErrorStandardScheme extends org.apache.thrift.scheme.StandardScheme<EdgeDrError> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, EdgeDrError struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ERROR_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.errorType = com.cannontech.messaging.serialization.thrift.generated.EdgeDrErrorType.findByValue(iprot.readI32());
              struct.setErrorTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ERROR_MESSAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.errorMessage = iprot.readString();
              struct.setErrorMessageIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, EdgeDrError struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.errorType != null) {
        oprot.writeFieldBegin(ERROR_TYPE_FIELD_DESC);
        oprot.writeI32(struct.errorType.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.errorMessage != null) {
        oprot.writeFieldBegin(ERROR_MESSAGE_FIELD_DESC);
        oprot.writeString(struct.errorMessage);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class EdgeDrErrorTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public EdgeDrErrorTupleScheme getScheme() {
      return new EdgeDrErrorTupleScheme();
    }
  }

  private static class EdgeDrErrorTupleScheme extends org.apache.thrift.scheme.TupleScheme<EdgeDrError> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, EdgeDrError struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeI32(struct.errorType.getValue());
      oprot.writeString(struct.errorMessage);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, EdgeDrError struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.errorType = com.cannontech.messaging.serialization.thrift.generated.EdgeDrErrorType.findByValue(iprot.readI32());
      struct.setErrorTypeIsSet(true);
      struct.errorMessage = iprot.readString();
      struct.setErrorMessageIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

