/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.16.0)", date = "2022-05-31")
public class CCSpecialAreas implements org.apache.thrift.TBase<CCSpecialAreas, CCSpecialAreas._Fields>, java.io.Serializable, Cloneable, Comparable<CCSpecialAreas> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("CCSpecialAreas");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _CC_SPECIAL_AREAS_FIELD_DESC = new org.apache.thrift.protocol.TField("_ccSpecialAreas", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new CCSpecialAreasStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new CCSpecialAreasTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.CCMessage _baseMessage; // required
  private @org.apache.thrift.annotation.Nullable java.util.List<CCSpecial> _ccSpecialAreas; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _CC_SPECIAL_AREAS((short)2, "_ccSpecialAreas");

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
        case 2: // _CC_SPECIAL_AREAS
          return _CC_SPECIAL_AREAS;
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
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.CCMessage.class)));
    tmpMap.put(_Fields._CC_SPECIAL_AREAS, new org.apache.thrift.meta_data.FieldMetaData("_ccSpecialAreas", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, CCSpecial.class))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(CCSpecialAreas.class, metaDataMap);
  }

  public CCSpecialAreas() {
  }

  public CCSpecialAreas(
    com.cannontech.messaging.serialization.thrift.generated.CCMessage _baseMessage,
    java.util.List<CCSpecial> _ccSpecialAreas)
  {
    this();
    this._baseMessage = _baseMessage;
    this._ccSpecialAreas = _ccSpecialAreas;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CCSpecialAreas(CCSpecialAreas other) {
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.CCMessage(other._baseMessage);
    }
    if (other.isSet_ccSpecialAreas()) {
      java.util.List<CCSpecial> __this___ccSpecialAreas = new java.util.ArrayList<CCSpecial>(other._ccSpecialAreas.size());
      for (CCSpecial other_element : other._ccSpecialAreas) {
        __this___ccSpecialAreas.add(new CCSpecial(other_element));
      }
      this._ccSpecialAreas = __this___ccSpecialAreas;
    }
  }

  public CCSpecialAreas deepCopy() {
    return new CCSpecialAreas(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    this._ccSpecialAreas = null;
  }

  @org.apache.thrift.annotation.Nullable
  public com.cannontech.messaging.serialization.thrift.generated.CCMessage get_baseMessage() {
    return this._baseMessage;
  }

  public void set_baseMessage(@org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.CCMessage _baseMessage) {
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

  public int get_ccSpecialAreasSize() {
    return (this._ccSpecialAreas == null) ? 0 : this._ccSpecialAreas.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<CCSpecial> get_ccSpecialAreasIterator() {
    return (this._ccSpecialAreas == null) ? null : this._ccSpecialAreas.iterator();
  }

  public void addTo_ccSpecialAreas(CCSpecial elem) {
    if (this._ccSpecialAreas == null) {
      this._ccSpecialAreas = new java.util.ArrayList<CCSpecial>();
    }
    this._ccSpecialAreas.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<CCSpecial> get_ccSpecialAreas() {
    return this._ccSpecialAreas;
  }

  public void set_ccSpecialAreas(@org.apache.thrift.annotation.Nullable java.util.List<CCSpecial> _ccSpecialAreas) {
    this._ccSpecialAreas = _ccSpecialAreas;
  }

  public void unset_ccSpecialAreas() {
    this._ccSpecialAreas = null;
  }

  /** Returns true if field _ccSpecialAreas is set (has been assigned a value) and false otherwise */
  public boolean isSet_ccSpecialAreas() {
    return this._ccSpecialAreas != null;
  }

  public void set_ccSpecialAreasIsSet(boolean value) {
    if (!value) {
      this._ccSpecialAreas = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _BASE_MESSAGE:
      if (value == null) {
        unset_baseMessage();
      } else {
        set_baseMessage((com.cannontech.messaging.serialization.thrift.generated.CCMessage)value);
      }
      break;

    case _CC_SPECIAL_AREAS:
      if (value == null) {
        unset_ccSpecialAreas();
      } else {
        set_ccSpecialAreas((java.util.List<CCSpecial>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _CC_SPECIAL_AREAS:
      return get_ccSpecialAreas();

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
    case _CC_SPECIAL_AREAS:
      return isSet_ccSpecialAreas();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof CCSpecialAreas)
      return this.equals((CCSpecialAreas)that);
    return false;
  }

  public boolean equals(CCSpecialAreas that) {
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

    boolean this_present__ccSpecialAreas = true && this.isSet_ccSpecialAreas();
    boolean that_present__ccSpecialAreas = true && that.isSet_ccSpecialAreas();
    if (this_present__ccSpecialAreas || that_present__ccSpecialAreas) {
      if (!(this_present__ccSpecialAreas && that_present__ccSpecialAreas))
        return false;
      if (!this._ccSpecialAreas.equals(that._ccSpecialAreas))
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

    hashCode = hashCode * 8191 + ((isSet_ccSpecialAreas()) ? 131071 : 524287);
    if (isSet_ccSpecialAreas())
      hashCode = hashCode * 8191 + _ccSpecialAreas.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(CCSpecialAreas other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSet_baseMessage(), other.isSet_baseMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_baseMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._baseMessage, other._baseMessage);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSet_ccSpecialAreas(), other.isSet_ccSpecialAreas());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_ccSpecialAreas()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._ccSpecialAreas, other._ccSpecialAreas);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("CCSpecialAreas(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_ccSpecialAreas:");
    if (this._ccSpecialAreas == null) {
      sb.append("null");
    } else {
      sb.append(this._ccSpecialAreas);
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

    if (!isSet_ccSpecialAreas()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_ccSpecialAreas' is unset! Struct:" + toString());
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class CCSpecialAreasStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CCSpecialAreasStandardScheme getScheme() {
      return new CCSpecialAreasStandardScheme();
    }
  }

  private static class CCSpecialAreasStandardScheme extends org.apache.thrift.scheme.StandardScheme<CCSpecialAreas> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, CCSpecialAreas struct) throws org.apache.thrift.TException {
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
              struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.CCMessage();
              struct._baseMessage.read(iprot);
              struct.set_baseMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _CC_SPECIAL_AREAS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                struct._ccSpecialAreas = new java.util.ArrayList<CCSpecial>(_list8.size);
                @org.apache.thrift.annotation.Nullable CCSpecial _elem9;
                for (int _i10 = 0; _i10 < _list8.size; ++_i10)
                {
                  _elem9 = new CCSpecial();
                  _elem9.read(iprot);
                  struct._ccSpecialAreas.add(_elem9);
                }
                iprot.readListEnd();
              }
              struct.set_ccSpecialAreasIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, CCSpecialAreas struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct._ccSpecialAreas != null) {
        oprot.writeFieldBegin(_CC_SPECIAL_AREAS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct._ccSpecialAreas.size()));
          for (CCSpecial _iter11 : struct._ccSpecialAreas)
          {
            _iter11.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CCSpecialAreasTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public CCSpecialAreasTupleScheme getScheme() {
      return new CCSpecialAreasTupleScheme();
    }
  }

  private static class CCSpecialAreasTupleScheme extends org.apache.thrift.scheme.TupleScheme<CCSpecialAreas> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, CCSpecialAreas struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      {
        oprot.writeI32(struct._ccSpecialAreas.size());
        for (CCSpecial _iter12 : struct._ccSpecialAreas)
        {
          _iter12.write(oprot);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, CCSpecialAreas struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.CCMessage();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      {
        org.apache.thrift.protocol.TList _list13 = iprot.readListBegin(org.apache.thrift.protocol.TType.STRUCT);
        struct._ccSpecialAreas = new java.util.ArrayList<CCSpecial>(_list13.size);
        @org.apache.thrift.annotation.Nullable CCSpecial _elem14;
        for (int _i15 = 0; _i15 < _list13.size; ++_i15)
        {
          _elem14 = new CCSpecial();
          _elem14.read(iprot);
          struct._ccSpecialAreas.add(_elem14);
        }
      }
      struct.set_ccSpecialAreasIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

