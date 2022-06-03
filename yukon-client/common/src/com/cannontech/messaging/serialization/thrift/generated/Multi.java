/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.16.0)", date = "2022-05-31")
public class Multi implements org.apache.thrift.TBase<Multi, Multi._Fields>, java.io.Serializable, Cloneable, Comparable<Multi> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Multi");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _BAG_FIELD_DESC = new org.apache.thrift.protocol.TField("_bag", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new MultiStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new MultiTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private @org.apache.thrift.annotation.Nullable java.util.List<com.cannontech.messaging.serialization.thrift.generated.GenericMessage> _bag; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _BAG((short)2, "_bag");

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
        case 2: // _BAG
          return _BAG;
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
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._BAG, new org.apache.thrift.meta_data.FieldMetaData("_bag", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.GenericMessage.class))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Multi.class, metaDataMap);
  }

  public Multi() {
  }

  public Multi(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    java.util.List<com.cannontech.messaging.serialization.thrift.generated.GenericMessage> _bag)
  {
    this();
    this._baseMessage = _baseMessage;
    this._bag = _bag;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Multi(Multi other) {
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    if (other.isSet_bag()) {
      java.util.List<com.cannontech.messaging.serialization.thrift.generated.GenericMessage> __this___bag = new java.util.ArrayList<com.cannontech.messaging.serialization.thrift.generated.GenericMessage>(other._bag.size());
      for (com.cannontech.messaging.serialization.thrift.generated.GenericMessage other_element : other._bag) {
        __this___bag.add(new com.cannontech.messaging.serialization.thrift.generated.GenericMessage(other_element));
      }
      this._bag = __this___bag;
    }
  }

  public Multi deepCopy() {
    return new Multi(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    this._bag = null;
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

  public int get_bagSize() {
    return (this._bag == null) ? 0 : this._bag.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<com.cannontech.messaging.serialization.thrift.generated.GenericMessage> get_bagIterator() {
    return (this._bag == null) ? null : this._bag.iterator();
  }

  public void addTo_bag(com.cannontech.messaging.serialization.thrift.generated.GenericMessage elem) {
    if (this._bag == null) {
      this._bag = new java.util.ArrayList<com.cannontech.messaging.serialization.thrift.generated.GenericMessage>();
    }
    this._bag.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<com.cannontech.messaging.serialization.thrift.generated.GenericMessage> get_bag() {
    return this._bag;
  }

  public void set_bag(@org.apache.thrift.annotation.Nullable java.util.List<com.cannontech.messaging.serialization.thrift.generated.GenericMessage> _bag) {
    this._bag = _bag;
  }

  public void unset_bag() {
    this._bag = null;
  }

  /** Returns true if field _bag is set (has been assigned a value) and false otherwise */
  public boolean isSet_bag() {
    return this._bag != null;
  }

  public void set_bagIsSet(boolean value) {
    if (!value) {
      this._bag = null;
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

    case _BAG:
      if (value == null) {
        unset_bag();
      } else {
        set_bag((java.util.List<com.cannontech.messaging.serialization.thrift.generated.GenericMessage>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _BAG:
      return get_bag();

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
    case _BAG:
      return isSet_bag();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof Multi)
      return this.equals((Multi)that);
    return false;
  }

  public boolean equals(Multi that) {
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

    boolean this_present__bag = true && this.isSet_bag();
    boolean that_present__bag = true && that.isSet_bag();
    if (this_present__bag || that_present__bag) {
      if (!(this_present__bag && that_present__bag))
        return false;
      if (!this._bag.equals(that._bag))
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

    hashCode = hashCode * 8191 + ((isSet_bag()) ? 131071 : 524287);
    if (isSet_bag())
      hashCode = hashCode * 8191 + _bag.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(Multi other) {
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
    lastComparison = java.lang.Boolean.compare(isSet_bag(), other.isSet_bag());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_bag()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._bag, other._bag);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("Multi(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_bag:");
    if (this._bag == null) {
      sb.append("null");
    } else {
      sb.append(this._bag);
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

    if (!isSet_bag()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_bag' is unset! Struct:" + toString());
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

  private static class MultiStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public MultiStandardScheme getScheme() {
      return new MultiStandardScheme();
    }
  }

  private static class MultiStandardScheme extends org.apache.thrift.scheme.StandardScheme<Multi> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Multi struct) throws org.apache.thrift.TException {
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
          case 2: // _BAG
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct._bag = new java.util.ArrayList<com.cannontech.messaging.serialization.thrift.generated.GenericMessage>(_list0.size);
                @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.GenericMessage _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = new com.cannontech.messaging.serialization.thrift.generated.GenericMessage();
                  _elem1.read(iprot);
                  struct._bag.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.set_bagIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, Multi struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct._bag != null) {
        oprot.writeFieldBegin(_BAG_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct._bag.size()));
          for (com.cannontech.messaging.serialization.thrift.generated.GenericMessage _iter3 : struct._bag)
          {
            _iter3.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MultiTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public MultiTupleScheme getScheme() {
      return new MultiTupleScheme();
    }
  }

  private static class MultiTupleScheme extends org.apache.thrift.scheme.TupleScheme<Multi> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Multi struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      {
        oprot.writeI32(struct._bag.size());
        for (com.cannontech.messaging.serialization.thrift.generated.GenericMessage _iter4 : struct._bag)
        {
          _iter4.write(oprot);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Multi struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      {
        org.apache.thrift.protocol.TList _list5 = iprot.readListBegin(org.apache.thrift.protocol.TType.STRUCT);
        struct._bag = new java.util.ArrayList<com.cannontech.messaging.serialization.thrift.generated.GenericMessage>(_list5.size);
        @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.GenericMessage _elem6;
        for (int _i7 = 0; _i7 < _list5.size; ++_i7)
        {
          _elem6 = new com.cannontech.messaging.serialization.thrift.generated.GenericMessage();
          _elem6.read(iprot);
          struct._bag.add(_elem6);
        }
      }
      struct.set_bagIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

