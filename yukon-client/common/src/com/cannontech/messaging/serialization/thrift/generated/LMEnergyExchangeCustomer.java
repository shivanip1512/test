/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-09-24")
public class LMEnergyExchangeCustomer implements org.apache.thrift.TBase<LMEnergyExchangeCustomer, LMEnergyExchangeCustomer._Fields>, java.io.Serializable, Cloneable, Comparable<LMEnergyExchangeCustomer> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("LMEnergyExchangeCustomer");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _LM_ENERGY_EXCHANGE_CUSTOMER_REPLIES_FIELD_DESC = new org.apache.thrift.protocol.TField("_lmEnergyExchangeCustomerReplies", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new LMEnergyExchangeCustomerStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new LMEnergyExchangeCustomerTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable LMCICustomerBase _baseMessage; // required
  private @org.apache.thrift.annotation.Nullable java.util.List<LMEnergyExchangeCustomerReply> _lmEnergyExchangeCustomerReplies; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _LM_ENERGY_EXCHANGE_CUSTOMER_REPLIES((short)2, "_lmEnergyExchangeCustomerReplies");

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
        case 2: // _LM_ENERGY_EXCHANGE_CUSTOMER_REPLIES
          return _LM_ENERGY_EXCHANGE_CUSTOMER_REPLIES;
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
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, LMCICustomerBase.class)));
    tmpMap.put(_Fields._LM_ENERGY_EXCHANGE_CUSTOMER_REPLIES, new org.apache.thrift.meta_data.FieldMetaData("_lmEnergyExchangeCustomerReplies", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, LMEnergyExchangeCustomerReply.class))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(LMEnergyExchangeCustomer.class, metaDataMap);
  }

  public LMEnergyExchangeCustomer() {
  }

  public LMEnergyExchangeCustomer(
    LMCICustomerBase _baseMessage,
    java.util.List<LMEnergyExchangeCustomerReply> _lmEnergyExchangeCustomerReplies)
  {
    this();
    this._baseMessage = _baseMessage;
    this._lmEnergyExchangeCustomerReplies = _lmEnergyExchangeCustomerReplies;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public LMEnergyExchangeCustomer(LMEnergyExchangeCustomer other) {
    if (other.isSet_baseMessage()) {
      this._baseMessage = new LMCICustomerBase(other._baseMessage);
    }
    if (other.isSet_lmEnergyExchangeCustomerReplies()) {
      java.util.List<LMEnergyExchangeCustomerReply> __this___lmEnergyExchangeCustomerReplies = new java.util.ArrayList<LMEnergyExchangeCustomerReply>(other._lmEnergyExchangeCustomerReplies.size());
      for (LMEnergyExchangeCustomerReply other_element : other._lmEnergyExchangeCustomerReplies) {
        __this___lmEnergyExchangeCustomerReplies.add(new LMEnergyExchangeCustomerReply(other_element));
      }
      this._lmEnergyExchangeCustomerReplies = __this___lmEnergyExchangeCustomerReplies;
    }
  }

  public LMEnergyExchangeCustomer deepCopy() {
    return new LMEnergyExchangeCustomer(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    this._lmEnergyExchangeCustomerReplies = null;
  }

  @org.apache.thrift.annotation.Nullable
  public LMCICustomerBase get_baseMessage() {
    return this._baseMessage;
  }

  public void set_baseMessage(@org.apache.thrift.annotation.Nullable LMCICustomerBase _baseMessage) {
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

  public int get_lmEnergyExchangeCustomerRepliesSize() {
    return (this._lmEnergyExchangeCustomerReplies == null) ? 0 : this._lmEnergyExchangeCustomerReplies.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<LMEnergyExchangeCustomerReply> get_lmEnergyExchangeCustomerRepliesIterator() {
    return (this._lmEnergyExchangeCustomerReplies == null) ? null : this._lmEnergyExchangeCustomerReplies.iterator();
  }

  public void addTo_lmEnergyExchangeCustomerReplies(LMEnergyExchangeCustomerReply elem) {
    if (this._lmEnergyExchangeCustomerReplies == null) {
      this._lmEnergyExchangeCustomerReplies = new java.util.ArrayList<LMEnergyExchangeCustomerReply>();
    }
    this._lmEnergyExchangeCustomerReplies.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<LMEnergyExchangeCustomerReply> get_lmEnergyExchangeCustomerReplies() {
    return this._lmEnergyExchangeCustomerReplies;
  }

  public void set_lmEnergyExchangeCustomerReplies(@org.apache.thrift.annotation.Nullable java.util.List<LMEnergyExchangeCustomerReply> _lmEnergyExchangeCustomerReplies) {
    this._lmEnergyExchangeCustomerReplies = _lmEnergyExchangeCustomerReplies;
  }

  public void unset_lmEnergyExchangeCustomerReplies() {
    this._lmEnergyExchangeCustomerReplies = null;
  }

  /** Returns true if field _lmEnergyExchangeCustomerReplies is set (has been assigned a value) and false otherwise */
  public boolean isSet_lmEnergyExchangeCustomerReplies() {
    return this._lmEnergyExchangeCustomerReplies != null;
  }

  public void set_lmEnergyExchangeCustomerRepliesIsSet(boolean value) {
    if (!value) {
      this._lmEnergyExchangeCustomerReplies = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _BASE_MESSAGE:
      if (value == null) {
        unset_baseMessage();
      } else {
        set_baseMessage((LMCICustomerBase)value);
      }
      break;

    case _LM_ENERGY_EXCHANGE_CUSTOMER_REPLIES:
      if (value == null) {
        unset_lmEnergyExchangeCustomerReplies();
      } else {
        set_lmEnergyExchangeCustomerReplies((java.util.List<LMEnergyExchangeCustomerReply>)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _LM_ENERGY_EXCHANGE_CUSTOMER_REPLIES:
      return get_lmEnergyExchangeCustomerReplies();

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
    case _LM_ENERGY_EXCHANGE_CUSTOMER_REPLIES:
      return isSet_lmEnergyExchangeCustomerReplies();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof LMEnergyExchangeCustomer)
      return this.equals((LMEnergyExchangeCustomer)that);
    return false;
  }

  public boolean equals(LMEnergyExchangeCustomer that) {
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

    boolean this_present__lmEnergyExchangeCustomerReplies = true && this.isSet_lmEnergyExchangeCustomerReplies();
    boolean that_present__lmEnergyExchangeCustomerReplies = true && that.isSet_lmEnergyExchangeCustomerReplies();
    if (this_present__lmEnergyExchangeCustomerReplies || that_present__lmEnergyExchangeCustomerReplies) {
      if (!(this_present__lmEnergyExchangeCustomerReplies && that_present__lmEnergyExchangeCustomerReplies))
        return false;
      if (!this._lmEnergyExchangeCustomerReplies.equals(that._lmEnergyExchangeCustomerReplies))
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

    hashCode = hashCode * 8191 + ((isSet_lmEnergyExchangeCustomerReplies()) ? 131071 : 524287);
    if (isSet_lmEnergyExchangeCustomerReplies())
      hashCode = hashCode * 8191 + _lmEnergyExchangeCustomerReplies.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(LMEnergyExchangeCustomer other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_lmEnergyExchangeCustomerReplies()).compareTo(other.isSet_lmEnergyExchangeCustomerReplies());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_lmEnergyExchangeCustomerReplies()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._lmEnergyExchangeCustomerReplies, other._lmEnergyExchangeCustomerReplies);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("LMEnergyExchangeCustomer(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_lmEnergyExchangeCustomerReplies:");
    if (this._lmEnergyExchangeCustomerReplies == null) {
      sb.append("null");
    } else {
      sb.append(this._lmEnergyExchangeCustomerReplies);
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

    if (!isSet_lmEnergyExchangeCustomerReplies()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_lmEnergyExchangeCustomerReplies' is unset! Struct:" + toString());
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

  private static class LMEnergyExchangeCustomerStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public LMEnergyExchangeCustomerStandardScheme getScheme() {
      return new LMEnergyExchangeCustomerStandardScheme();
    }
  }

  private static class LMEnergyExchangeCustomerStandardScheme extends org.apache.thrift.scheme.StandardScheme<LMEnergyExchangeCustomer> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, LMEnergyExchangeCustomer struct) throws org.apache.thrift.TException {
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
              struct._baseMessage = new LMCICustomerBase();
              struct._baseMessage.read(iprot);
              struct.set_baseMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _LM_ENERGY_EXCHANGE_CUSTOMER_REPLIES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list72 = iprot.readListBegin();
                struct._lmEnergyExchangeCustomerReplies = new java.util.ArrayList<LMEnergyExchangeCustomerReply>(_list72.size);
                @org.apache.thrift.annotation.Nullable LMEnergyExchangeCustomerReply _elem73;
                for (int _i74 = 0; _i74 < _list72.size; ++_i74)
                {
                  _elem73 = new LMEnergyExchangeCustomerReply();
                  _elem73.read(iprot);
                  struct._lmEnergyExchangeCustomerReplies.add(_elem73);
                }
                iprot.readListEnd();
              }
              struct.set_lmEnergyExchangeCustomerRepliesIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, LMEnergyExchangeCustomer struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct._lmEnergyExchangeCustomerReplies != null) {
        oprot.writeFieldBegin(_LM_ENERGY_EXCHANGE_CUSTOMER_REPLIES_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct._lmEnergyExchangeCustomerReplies.size()));
          for (LMEnergyExchangeCustomerReply _iter75 : struct._lmEnergyExchangeCustomerReplies)
          {
            _iter75.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class LMEnergyExchangeCustomerTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public LMEnergyExchangeCustomerTupleScheme getScheme() {
      return new LMEnergyExchangeCustomerTupleScheme();
    }
  }

  private static class LMEnergyExchangeCustomerTupleScheme extends org.apache.thrift.scheme.TupleScheme<LMEnergyExchangeCustomer> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, LMEnergyExchangeCustomer struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      {
        oprot.writeI32(struct._lmEnergyExchangeCustomerReplies.size());
        for (LMEnergyExchangeCustomerReply _iter76 : struct._lmEnergyExchangeCustomerReplies)
        {
          _iter76.write(oprot);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, LMEnergyExchangeCustomer struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new LMCICustomerBase();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      {
        org.apache.thrift.protocol.TList _list77 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct._lmEnergyExchangeCustomerReplies = new java.util.ArrayList<LMEnergyExchangeCustomerReply>(_list77.size);
        @org.apache.thrift.annotation.Nullable LMEnergyExchangeCustomerReply _elem78;
        for (int _i79 = 0; _i79 < _list77.size; ++_i79)
        {
          _elem78 = new LMEnergyExchangeCustomerReply();
          _elem78.read(iprot);
          struct._lmEnergyExchangeCustomerReplies.add(_elem78);
        }
      }
      struct.set_lmEnergyExchangeCustomerRepliesIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

