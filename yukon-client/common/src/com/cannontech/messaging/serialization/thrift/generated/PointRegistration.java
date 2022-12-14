/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public class PointRegistration implements org.apache.thrift.TBase<PointRegistration, PointRegistration._Fields>, java.io.Serializable, Cloneable, Comparable<PointRegistration> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("PointRegistration");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _REG_FLAGS_FIELD_DESC = new org.apache.thrift.protocol.TField("_regFlags", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField _POINT_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("_pointList", org.apache.thrift.protocol.TType.LIST, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new PointRegistrationStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new PointRegistrationTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private int _regFlags; // required
  private @org.apache.thrift.annotation.Nullable java.util.List<java.lang.Integer> _pointList; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _REG_FLAGS((short)2, "_regFlags"),
    _POINT_LIST((short)3, "_pointList");

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
        case 1: // _BASE_MESSAGE
          return _BASE_MESSAGE;
        case 2: // _REG_FLAGS
          return _REG_FLAGS;
        case 3: // _POINT_LIST
          return _POINT_LIST;
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
  private static final int ___REGFLAGS_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._REG_FLAGS, new org.apache.thrift.meta_data.FieldMetaData("_regFlags", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._POINT_LIST, new org.apache.thrift.meta_data.FieldMetaData("_pointList", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(PointRegistration.class, metaDataMap);
  }

  public PointRegistration() {
  }

  public PointRegistration(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    int _regFlags,
    java.util.List<java.lang.Integer> _pointList)
  {
    this();
    this._baseMessage = _baseMessage;
    this._regFlags = _regFlags;
    set_regFlagsIsSet(true);
    this._pointList = _pointList;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PointRegistration(PointRegistration other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    this._regFlags = other._regFlags;
    if (other.isSet_pointList()) {
      java.util.List<java.lang.Integer> __this___pointList = new java.util.ArrayList<java.lang.Integer>(other._pointList);
      this._pointList = __this___pointList;
    }
  }

  public PointRegistration deepCopy() {
    return new PointRegistration(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    set_regFlagsIsSet(false);
    this._regFlags = 0;
    this._pointList = null;
  }

  @org.apache.thrift.annotation.Nullable
  public com.cannontech.messaging.serialization.thrift.generated.Message get_baseMessage() {
    return this._baseMessage;
  }

  public void set_baseMessage(@org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage) {
    this._baseMessage = _baseMessage;
  }

  public void unset_baseMessage() {
    this._baseMessage = null;
  }

  /** Returns true if field _baseMessage is set (has been assigned a value) and false otherwise */
  public boolean isSet_baseMessage() {
    return this._baseMessage != null;
  }

  public void set_baseMessageIsSet(boolean value) {
    if (!value) {
      this._baseMessage = null;
    }
  }

  public int get_regFlags() {
    return this._regFlags;
  }

  public void set_regFlags(int _regFlags) {
    this._regFlags = _regFlags;
    set_regFlagsIsSet(true);
  }

  public void unset_regFlags() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___REGFLAGS_ISSET_ID);
  }

  /** Returns true if field _regFlags is set (has been assigned a value) and false otherwise */
  public boolean isSet_regFlags() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___REGFLAGS_ISSET_ID);
  }

  public void set_regFlagsIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___REGFLAGS_ISSET_ID, value);
  }

  public int get_pointListSize() {
    return (this._pointList == null) ? 0 : this._pointList.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<java.lang.Integer> get_pointListIterator() {
    return (this._pointList == null) ? null : this._pointList.iterator();
  }

  public void addTo_pointList(int elem) {
    if (this._pointList == null) {
      this._pointList = new java.util.ArrayList<java.lang.Integer>();
    }
    this._pointList.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<java.lang.Integer> get_pointList() {
    return this._pointList;
  }

  public void set_pointList(@org.apache.thrift.annotation.Nullable java.util.List<java.lang.Integer> _pointList) {
    this._pointList = _pointList;
  }

  public void unset_pointList() {
    this._pointList = null;
  }

  /** Returns true if field _pointList is set (has been assigned a value) and false otherwise */
  public boolean isSet_pointList() {
    return this._pointList != null;
  }

  public void set_pointListIsSet(boolean value) {
    if (!value) {
      this._pointList = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _BASE_MESSAGE:
      if (value == null) {
        unset_baseMessage();
      } else {
        set_baseMessage((com.cannontech.messaging.serialization.thrift.generated.Message)value);
      }
      break;

    case _REG_FLAGS:
      if (value == null) {
        unset_regFlags();
      } else {
        set_regFlags((java.lang.Integer)value);
      }
      break;

    case _POINT_LIST:
      if (value == null) {
        unset_pointList();
      } else {
        set_pointList((java.util.List<java.lang.Integer>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _REG_FLAGS:
      return get_regFlags();

    case _POINT_LIST:
      return get_pointList();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case _BASE_MESSAGE:
      return isSet_baseMessage();
    case _REG_FLAGS:
      return isSet_regFlags();
    case _POINT_LIST:
      return isSet_pointList();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof PointRegistration)
      return this.equals((PointRegistration)that);
    return false;
  }

  public boolean equals(PointRegistration that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present__baseMessage = true && this.isSet_baseMessage();
    boolean that_present__baseMessage = true && that.isSet_baseMessage();
    if (this_present__baseMessage || that_present__baseMessage) {
      if (!(this_present__baseMessage && that_present__baseMessage))
        return false;
      if (!this._baseMessage.equals(that._baseMessage))
        return false;
    }

    boolean this_present__regFlags = true;
    boolean that_present__regFlags = true;
    if (this_present__regFlags || that_present__regFlags) {
      if (!(this_present__regFlags && that_present__regFlags))
        return false;
      if (this._regFlags != that._regFlags)
        return false;
    }

    boolean this_present__pointList = true && this.isSet_pointList();
    boolean that_present__pointList = true && that.isSet_pointList();
    if (this_present__pointList || that_present__pointList) {
      if (!(this_present__pointList && that_present__pointList))
        return false;
      if (!this._pointList.equals(that._pointList))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSet_baseMessage()) ? 131071 : 524287);
    if (isSet_baseMessage())
      hashCode = hashCode * 8191 + _baseMessage.hashCode();

    hashCode = hashCode * 8191 + _regFlags;

    hashCode = hashCode * 8191 + ((isSet_pointList()) ? 131071 : 524287);
    if (isSet_pointList())
      hashCode = hashCode * 8191 + _pointList.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(PointRegistration other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSet_baseMessage()).compareTo(other.isSet_baseMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_baseMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._baseMessage, other._baseMessage);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_regFlags()).compareTo(other.isSet_regFlags());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_regFlags()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._regFlags, other._regFlags);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_pointList()).compareTo(other.isSet_pointList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_pointList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._pointList, other._pointList);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("PointRegistration(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_regFlags:");
    sb.append(this._regFlags);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_pointList:");
    if (this._pointList == null) {
      sb.append("null");
    } else {
      sb.append(this._pointList);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_baseMessage()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_baseMessage' is unset! Struct:" + toString());
    }

    if (!isSet_regFlags()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_regFlags' is unset! Struct:" + toString());
    }

    if (!isSet_pointList()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_pointList' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (_baseMessage != null) {
      _baseMessage.validate();
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

  private static class PointRegistrationStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public PointRegistrationStandardScheme getScheme() {
      return new PointRegistrationStandardScheme();
    }
  }

  private static class PointRegistrationStandardScheme extends org.apache.thrift.scheme.StandardScheme<PointRegistration> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, PointRegistration struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // _BASE_MESSAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
              struct._baseMessage.read(iprot);
              struct.set_baseMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _REG_FLAGS
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._regFlags = iprot.readI32();
              struct.set_regFlagsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _POINT_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct._pointList = new java.util.ArrayList<java.lang.Integer>(_list0.size);
                int _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = iprot.readI32();
                  struct._pointList.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.set_pointListIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, PointRegistration struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_REG_FLAGS_FIELD_DESC);
      oprot.writeI32(struct._regFlags);
      oprot.writeFieldEnd();
      if (struct._pointList != null) {
        oprot.writeFieldBegin(_POINT_LIST_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, struct._pointList.size()));
          for (int _iter3 : struct._pointList)
          {
            oprot.writeI32(_iter3);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class PointRegistrationTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public PointRegistrationTupleScheme getScheme() {
      return new PointRegistrationTupleScheme();
    }
  }

  private static class PointRegistrationTupleScheme extends org.apache.thrift.scheme.TupleScheme<PointRegistration> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, PointRegistration struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeI32(struct._regFlags);
      {
        oprot.writeI32(struct._pointList.size());
        for (int _iter4 : struct._pointList)
        {
          oprot.writeI32(_iter4);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, PointRegistration struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._regFlags = iprot.readI32();
      struct.set_regFlagsIsSet(true);
      {
        org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, iprot.readI32());
        struct._pointList = new java.util.ArrayList<java.lang.Integer>(_list5.size);
        int _elem6;
        for (int _i7 = 0; _i7 < _list5.size; ++_i7)
        {
          _elem6 = iprot.readI32();
          struct._pointList.add(_elem6);
        }
      }
      struct.set_pointListIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

