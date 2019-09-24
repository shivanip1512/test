/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-09-24")
public class NotifCustomerEmail implements org.apache.thrift.TBase<NotifCustomerEmail, NotifCustomerEmail._Fields>, java.io.Serializable, Cloneable, Comparable<NotifCustomerEmail> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("NotifCustomerEmail");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _TO_FIELD_DESC = new org.apache.thrift.protocol.TField("_to", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField _CUSTOMER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_customerId", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField _SUBJECT_FIELD_DESC = new org.apache.thrift.protocol.TField("_subject", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField _BODY_FIELD_DESC = new org.apache.thrift.protocol.TField("_body", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField _TO_CC_FIELD_DESC = new org.apache.thrift.protocol.TField("_toCc", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField _TO_BCC_FIELD_DESC = new org.apache.thrift.protocol.TField("_toBcc", org.apache.thrift.protocol.TType.STRING, (short)7);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new NotifCustomerEmailStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new NotifCustomerEmailTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _to; // required
  private int _customerId; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _subject; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _body; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _toCc; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _toBcc; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _TO((short)2, "_to"),
    _CUSTOMER_ID((short)3, "_customerId"),
    _SUBJECT((short)4, "_subject"),
    _BODY((short)5, "_body"),
    _TO_CC((short)6, "_toCc"),
    _TO_BCC((short)7, "_toBcc");

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
        case 2: // _TO
          return _TO;
        case 3: // _CUSTOMER_ID
          return _CUSTOMER_ID;
        case 4: // _SUBJECT
          return _SUBJECT;
        case 5: // _BODY
          return _BODY;
        case 6: // _TO_CC
          return _TO_CC;
        case 7: // _TO_BCC
          return _TO_BCC;
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
  private static final int ___CUSTOMERID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._TO, new org.apache.thrift.meta_data.FieldMetaData("_to", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._CUSTOMER_ID, new org.apache.thrift.meta_data.FieldMetaData("_customerId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._SUBJECT, new org.apache.thrift.meta_data.FieldMetaData("_subject", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._BODY, new org.apache.thrift.meta_data.FieldMetaData("_body", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._TO_CC, new org.apache.thrift.meta_data.FieldMetaData("_toCc", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._TO_BCC, new org.apache.thrift.meta_data.FieldMetaData("_toBcc", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(NotifCustomerEmail.class, metaDataMap);
  }

  public NotifCustomerEmail() {
  }

  public NotifCustomerEmail(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    java.lang.String _to,
    int _customerId,
    java.lang.String _subject,
    java.lang.String _body,
    java.lang.String _toCc,
    java.lang.String _toBcc)
  {
    this();
    this._baseMessage = _baseMessage;
    this._to = _to;
    this._customerId = _customerId;
    set_customerIdIsSet(true);
    this._subject = _subject;
    this._body = _body;
    this._toCc = _toCc;
    this._toBcc = _toBcc;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public NotifCustomerEmail(NotifCustomerEmail other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    if (other.isSet_to()) {
      this._to = other._to;
    }
    this._customerId = other._customerId;
    if (other.isSet_subject()) {
      this._subject = other._subject;
    }
    if (other.isSet_body()) {
      this._body = other._body;
    }
    if (other.isSet_toCc()) {
      this._toCc = other._toCc;
    }
    if (other.isSet_toBcc()) {
      this._toBcc = other._toBcc;
    }
  }

  public NotifCustomerEmail deepCopy() {
    return new NotifCustomerEmail(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    this._to = null;
    set_customerIdIsSet(false);
    this._customerId = 0;
    this._subject = null;
    this._body = null;
    this._toCc = null;
    this._toBcc = null;
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

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_to() {
    return this._to;
  }

  public void set_to(@org.apache.thrift.annotation.Nullable java.lang.String _to) {
    this._to = _to;
  }

  public void unset_to() {
    this._to = null;
  }

  /** Returns true if field _to is set (has been assigned a value) and false otherwise */
  public boolean isSet_to() {
    return this._to != null;
  }

  public void set_toIsSet(boolean value) {
    if (!value) {
      this._to = null;
    }
  }

  public int get_customerId() {
    return this._customerId;
  }

  public void set_customerId(int _customerId) {
    this._customerId = _customerId;
    set_customerIdIsSet(true);
  }

  public void unset_customerId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___CUSTOMERID_ISSET_ID);
  }

  /** Returns true if field _customerId is set (has been assigned a value) and false otherwise */
  public boolean isSet_customerId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___CUSTOMERID_ISSET_ID);
  }

  public void set_customerIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___CUSTOMERID_ISSET_ID, value);
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_subject() {
    return this._subject;
  }

  public void set_subject(@org.apache.thrift.annotation.Nullable java.lang.String _subject) {
    this._subject = _subject;
  }

  public void unset_subject() {
    this._subject = null;
  }

  /** Returns true if field _subject is set (has been assigned a value) and false otherwise */
  public boolean isSet_subject() {
    return this._subject != null;
  }

  public void set_subjectIsSet(boolean value) {
    if (!value) {
      this._subject = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_body() {
    return this._body;
  }

  public void set_body(@org.apache.thrift.annotation.Nullable java.lang.String _body) {
    this._body = _body;
  }

  public void unset_body() {
    this._body = null;
  }

  /** Returns true if field _body is set (has been assigned a value) and false otherwise */
  public boolean isSet_body() {
    return this._body != null;
  }

  public void set_bodyIsSet(boolean value) {
    if (!value) {
      this._body = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_toCc() {
    return this._toCc;
  }

  public void set_toCc(@org.apache.thrift.annotation.Nullable java.lang.String _toCc) {
    this._toCc = _toCc;
  }

  public void unset_toCc() {
    this._toCc = null;
  }

  /** Returns true if field _toCc is set (has been assigned a value) and false otherwise */
  public boolean isSet_toCc() {
    return this._toCc != null;
  }

  public void set_toCcIsSet(boolean value) {
    if (!value) {
      this._toCc = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_toBcc() {
    return this._toBcc;
  }

  public void set_toBcc(@org.apache.thrift.annotation.Nullable java.lang.String _toBcc) {
    this._toBcc = _toBcc;
  }

  public void unset_toBcc() {
    this._toBcc = null;
  }

  /** Returns true if field _toBcc is set (has been assigned a value) and false otherwise */
  public boolean isSet_toBcc() {
    return this._toBcc != null;
  }

  public void set_toBccIsSet(boolean value) {
    if (!value) {
      this._toBcc = null;
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

    case _TO:
      if (value == null) {
        unset_to();
      } else {
        set_to((java.lang.String)value);
      }
      break;

    case _CUSTOMER_ID:
      if (value == null) {
        unset_customerId();
      } else {
        set_customerId((java.lang.Integer)value);
      }
      break;

    case _SUBJECT:
      if (value == null) {
        unset_subject();
      } else {
        set_subject((java.lang.String)value);
      }
      break;

    case _BODY:
      if (value == null) {
        unset_body();
      } else {
        set_body((java.lang.String)value);
      }
      break;

    case _TO_CC:
      if (value == null) {
        unset_toCc();
      } else {
        set_toCc((java.lang.String)value);
      }
      break;

    case _TO_BCC:
      if (value == null) {
        unset_toBcc();
      } else {
        set_toBcc((java.lang.String)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _TO:
      return get_to();

    case _CUSTOMER_ID:
      return get_customerId();

    case _SUBJECT:
      return get_subject();

    case _BODY:
      return get_body();

    case _TO_CC:
      return get_toCc();

    case _TO_BCC:
      return get_toBcc();

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
    case _TO:
      return isSet_to();
    case _CUSTOMER_ID:
      return isSet_customerId();
    case _SUBJECT:
      return isSet_subject();
    case _BODY:
      return isSet_body();
    case _TO_CC:
      return isSet_toCc();
    case _TO_BCC:
      return isSet_toBcc();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof NotifCustomerEmail)
      return this.equals((NotifCustomerEmail)that);
    return false;
  }

  public boolean equals(NotifCustomerEmail that) {
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

    boolean this_present__to = true && this.isSet_to();
    boolean that_present__to = true && that.isSet_to();
    if (this_present__to || that_present__to) {
      if (!(this_present__to && that_present__to))
        return false;
      if (!this._to.equals(that._to))
        return false;
    }

    boolean this_present__customerId = true;
    boolean that_present__customerId = true;
    if (this_present__customerId || that_present__customerId) {
      if (!(this_present__customerId && that_present__customerId))
        return false;
      if (this._customerId != that._customerId)
        return false;
    }

    boolean this_present__subject = true && this.isSet_subject();
    boolean that_present__subject = true && that.isSet_subject();
    if (this_present__subject || that_present__subject) {
      if (!(this_present__subject && that_present__subject))
        return false;
      if (!this._subject.equals(that._subject))
        return false;
    }

    boolean this_present__body = true && this.isSet_body();
    boolean that_present__body = true && that.isSet_body();
    if (this_present__body || that_present__body) {
      if (!(this_present__body && that_present__body))
        return false;
      if (!this._body.equals(that._body))
        return false;
    }

    boolean this_present__toCc = true && this.isSet_toCc();
    boolean that_present__toCc = true && that.isSet_toCc();
    if (this_present__toCc || that_present__toCc) {
      if (!(this_present__toCc && that_present__toCc))
        return false;
      if (!this._toCc.equals(that._toCc))
        return false;
    }

    boolean this_present__toBcc = true && this.isSet_toBcc();
    boolean that_present__toBcc = true && that.isSet_toBcc();
    if (this_present__toBcc || that_present__toBcc) {
      if (!(this_present__toBcc && that_present__toBcc))
        return false;
      if (!this._toBcc.equals(that._toBcc))
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

    hashCode = hashCode * 8191 + ((isSet_to()) ? 131071 : 524287);
    if (isSet_to())
      hashCode = hashCode * 8191 + _to.hashCode();

    hashCode = hashCode * 8191 + _customerId;

    hashCode = hashCode * 8191 + ((isSet_subject()) ? 131071 : 524287);
    if (isSet_subject())
      hashCode = hashCode * 8191 + _subject.hashCode();

    hashCode = hashCode * 8191 + ((isSet_body()) ? 131071 : 524287);
    if (isSet_body())
      hashCode = hashCode * 8191 + _body.hashCode();

    hashCode = hashCode * 8191 + ((isSet_toCc()) ? 131071 : 524287);
    if (isSet_toCc())
      hashCode = hashCode * 8191 + _toCc.hashCode();

    hashCode = hashCode * 8191 + ((isSet_toBcc()) ? 131071 : 524287);
    if (isSet_toBcc())
      hashCode = hashCode * 8191 + _toBcc.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(NotifCustomerEmail other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_to()).compareTo(other.isSet_to());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_to()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._to, other._to);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_customerId()).compareTo(other.isSet_customerId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_customerId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._customerId, other._customerId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_subject()).compareTo(other.isSet_subject());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_subject()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._subject, other._subject);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_body()).compareTo(other.isSet_body());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_body()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._body, other._body);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_toCc()).compareTo(other.isSet_toCc());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_toCc()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._toCc, other._toCc);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_toBcc()).compareTo(other.isSet_toBcc());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_toBcc()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._toBcc, other._toBcc);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("NotifCustomerEmail(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_to:");
    if (this._to == null) {
      sb.append("null");
    } else {
      sb.append(this._to);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_customerId:");
    sb.append(this._customerId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_subject:");
    if (this._subject == null) {
      sb.append("null");
    } else {
      sb.append(this._subject);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_body:");
    if (this._body == null) {
      sb.append("null");
    } else {
      sb.append(this._body);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_toCc:");
    if (this._toCc == null) {
      sb.append("null");
    } else {
      sb.append(this._toCc);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_toBcc:");
    if (this._toBcc == null) {
      sb.append("null");
    } else {
      sb.append(this._toBcc);
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

    if (!isSet_to()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_to' is unset! Struct:" + toString());
    }

    if (!isSet_customerId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_customerId' is unset! Struct:" + toString());
    }

    if (!isSet_subject()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_subject' is unset! Struct:" + toString());
    }

    if (!isSet_body()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_body' is unset! Struct:" + toString());
    }

    if (!isSet_toCc()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_toCc' is unset! Struct:" + toString());
    }

    if (!isSet_toBcc()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_toBcc' is unset! Struct:" + toString());
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

  private static class NotifCustomerEmailStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public NotifCustomerEmailStandardScheme getScheme() {
      return new NotifCustomerEmailStandardScheme();
    }
  }

  private static class NotifCustomerEmailStandardScheme extends org.apache.thrift.scheme.StandardScheme<NotifCustomerEmail> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, NotifCustomerEmail struct) throws org.apache.thrift.TException {
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
          case 2: // _TO
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._to = iprot.readString();
              struct.set_toIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _CUSTOMER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._customerId = iprot.readI32();
              struct.set_customerIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _SUBJECT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._subject = iprot.readString();
              struct.set_subjectIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // _BODY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._body = iprot.readString();
              struct.set_bodyIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // _TO_CC
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._toCc = iprot.readString();
              struct.set_toCcIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // _TO_BCC
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._toBcc = iprot.readString();
              struct.set_toBccIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, NotifCustomerEmail struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct._to != null) {
        oprot.writeFieldBegin(_TO_FIELD_DESC);
        oprot.writeString(struct._to);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_CUSTOMER_ID_FIELD_DESC);
      oprot.writeI32(struct._customerId);
      oprot.writeFieldEnd();
      if (struct._subject != null) {
        oprot.writeFieldBegin(_SUBJECT_FIELD_DESC);
        oprot.writeString(struct._subject);
        oprot.writeFieldEnd();
      }
      if (struct._body != null) {
        oprot.writeFieldBegin(_BODY_FIELD_DESC);
        oprot.writeString(struct._body);
        oprot.writeFieldEnd();
      }
      if (struct._toCc != null) {
        oprot.writeFieldBegin(_TO_CC_FIELD_DESC);
        oprot.writeString(struct._toCc);
        oprot.writeFieldEnd();
      }
      if (struct._toBcc != null) {
        oprot.writeFieldBegin(_TO_BCC_FIELD_DESC);
        oprot.writeString(struct._toBcc);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class NotifCustomerEmailTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public NotifCustomerEmailTupleScheme getScheme() {
      return new NotifCustomerEmailTupleScheme();
    }
  }

  private static class NotifCustomerEmailTupleScheme extends org.apache.thrift.scheme.TupleScheme<NotifCustomerEmail> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, NotifCustomerEmail struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeString(struct._to);
      oprot.writeI32(struct._customerId);
      oprot.writeString(struct._subject);
      oprot.writeString(struct._body);
      oprot.writeString(struct._toCc);
      oprot.writeString(struct._toBcc);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, NotifCustomerEmail struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._to = iprot.readString();
      struct.set_toIsSet(true);
      struct._customerId = iprot.readI32();
      struct.set_customerIdIsSet(true);
      struct._subject = iprot.readString();
      struct.set_subjectIsSet(true);
      struct._body = iprot.readString();
      struct.set_bodyIsSet(true);
      struct._toCc = iprot.readString();
      struct.set_toCcIsSet(true);
      struct._toBcc = iprot.readString();
      struct.set_toBccIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

