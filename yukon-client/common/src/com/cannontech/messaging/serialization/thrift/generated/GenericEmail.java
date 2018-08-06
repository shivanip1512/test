/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2018-08-02")
public class GenericEmail implements org.apache.thrift.TBase<GenericEmail, GenericEmail._Fields>, java.io.Serializable, Cloneable, Comparable<GenericEmail> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("GenericEmail");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _TO_FIELD_DESC = new org.apache.thrift.protocol.TField("_to", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField _FROM_FIELD_DESC = new org.apache.thrift.protocol.TField("_from", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField _SUBJECT_FIELD_DESC = new org.apache.thrift.protocol.TField("_subject", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField _BODY_FIELD_DESC = new org.apache.thrift.protocol.TField("_body", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField _BCC_FIELD_DESC = new org.apache.thrift.protocol.TField("_bcc", org.apache.thrift.protocol.TType.STRING, (short)6);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new GenericEmailStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new GenericEmailTupleSchemeFactory();

  private com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage; // required
  private java.lang.String _to; // optional
  private java.lang.String _from; // required
  private java.lang.String _subject; // required
  private java.lang.String _body; // required
  private java.lang.String _bcc; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _TO((short)2, "_to"),
    _FROM((short)3, "_from"),
    _SUBJECT((short)4, "_subject"),
    _BODY((short)5, "_body"),
    _BCC((short)6, "_bcc");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // _BASE_MESSAGE
          return _BASE_MESSAGE;
        case 2: // _TO
          return _TO;
        case 3: // _FROM
          return _FROM;
        case 4: // _SUBJECT
          return _SUBJECT;
        case 5: // _BODY
          return _BODY;
        case 6: // _BCC
          return _BCC;
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
  private static final _Fields optionals[] = {_Fields._TO,_Fields._BCC};
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.Message.class)));
    tmpMap.put(_Fields._TO, new org.apache.thrift.meta_data.FieldMetaData("_to", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._FROM, new org.apache.thrift.meta_data.FieldMetaData("_from", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._SUBJECT, new org.apache.thrift.meta_data.FieldMetaData("_subject", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._BODY, new org.apache.thrift.meta_data.FieldMetaData("_body", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._BCC, new org.apache.thrift.meta_data.FieldMetaData("_bcc", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(GenericEmail.class, metaDataMap);
  }

  public GenericEmail() {
  }

  public GenericEmail(
    com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage,
    java.lang.String _from,
    java.lang.String _subject,
    java.lang.String _body)
  {
    this();
    this._baseMessage = _baseMessage;
    this._from = _from;
    this._subject = _subject;
    this._body = _body;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public GenericEmail(GenericEmail other) {
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message(other._baseMessage);
    }
    if (other.isSet_to()) {
      this._to = other._to;
    }
    if (other.isSet_from()) {
      this._from = other._from;
    }
    if (other.isSet_subject()) {
      this._subject = other._subject;
    }
    if (other.isSet_body()) {
      this._body = other._body;
    }
    if (other.isSet_bcc()) {
      this._bcc = other._bcc;
    }
  }

  public GenericEmail deepCopy() {
    return new GenericEmail(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    this._to = null;
    this._from = null;
    this._subject = null;
    this._body = null;
    this._bcc = null;
  }

  public com.cannontech.messaging.serialization.thrift.generated.Message get_baseMessage() {
    return this._baseMessage;
  }

  public void set_baseMessage(com.cannontech.messaging.serialization.thrift.generated.Message _baseMessage) {
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

  public java.lang.String get_to() {
    return this._to;
  }

  public void set_to(java.lang.String _to) {
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

  public java.lang.String get_from() {
    return this._from;
  }

  public void set_from(java.lang.String _from) {
    this._from = _from;
  }

  public void unset_from() {
    this._from = null;
  }

  /** Returns true if field _from is set (has been assigned a value) and false otherwise */
  public boolean isSet_from() {
    return this._from != null;
  }

  public void set_fromIsSet(boolean value) {
    if (!value) {
      this._from = null;
    }
  }

  public java.lang.String get_subject() {
    return this._subject;
  }

  public void set_subject(java.lang.String _subject) {
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

  public java.lang.String get_body() {
    return this._body;
  }

  public void set_body(java.lang.String _body) {
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

  public java.lang.String get_bcc() {
    return this._bcc;
  }

  public void set_bcc(java.lang.String _bcc) {
    this._bcc = _bcc;
  }

  public void unset_bcc() {
    this._bcc = null;
  }

  /** Returns true if field _bcc is set (has been assigned a value) and false otherwise */
  public boolean isSet_bcc() {
    return this._bcc != null;
  }

  public void set_bccIsSet(boolean value) {
    if (!value) {
      this._bcc = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
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

    case _FROM:
      if (value == null) {
        unset_from();
      } else {
        set_from((java.lang.String)value);
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

    case _BCC:
      if (value == null) {
        unset_bcc();
      } else {
        set_bcc((java.lang.String)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _TO:
      return get_to();

    case _FROM:
      return get_from();

    case _SUBJECT:
      return get_subject();

    case _BODY:
      return get_body();

    case _BCC:
      return get_bcc();

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
    case _FROM:
      return isSet_from();
    case _SUBJECT:
      return isSet_subject();
    case _BODY:
      return isSet_body();
    case _BCC:
      return isSet_bcc();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof GenericEmail)
      return this.equals((GenericEmail)that);
    return false;
  }

  public boolean equals(GenericEmail that) {
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

    boolean this_present__from = true && this.isSet_from();
    boolean that_present__from = true && that.isSet_from();
    if (this_present__from || that_present__from) {
      if (!(this_present__from && that_present__from))
        return false;
      if (!this._from.equals(that._from))
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

    boolean this_present__bcc = true && this.isSet_bcc();
    boolean that_present__bcc = true && that.isSet_bcc();
    if (this_present__bcc || that_present__bcc) {
      if (!(this_present__bcc && that_present__bcc))
        return false;
      if (!this._bcc.equals(that._bcc))
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

    hashCode = hashCode * 8191 + ((isSet_from()) ? 131071 : 524287);
    if (isSet_from())
      hashCode = hashCode * 8191 + _from.hashCode();

    hashCode = hashCode * 8191 + ((isSet_subject()) ? 131071 : 524287);
    if (isSet_subject())
      hashCode = hashCode * 8191 + _subject.hashCode();

    hashCode = hashCode * 8191 + ((isSet_body()) ? 131071 : 524287);
    if (isSet_body())
      hashCode = hashCode * 8191 + _body.hashCode();

    hashCode = hashCode * 8191 + ((isSet_bcc()) ? 131071 : 524287);
    if (isSet_bcc())
      hashCode = hashCode * 8191 + _bcc.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(GenericEmail other) {
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
    lastComparison = java.lang.Boolean.valueOf(isSet_from()).compareTo(other.isSet_from());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_from()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._from, other._from);
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
    lastComparison = java.lang.Boolean.valueOf(isSet_bcc()).compareTo(other.isSet_bcc());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_bcc()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._bcc, other._bcc);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("GenericEmail(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (isSet_to()) {
      if (!first) sb.append(", ");
      sb.append("_to:");
      if (this._to == null) {
        sb.append("null");
      } else {
        sb.append(this._to);
      }
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("_from:");
    if (this._from == null) {
      sb.append("null");
    } else {
      sb.append(this._from);
    }
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
    if (isSet_bcc()) {
      if (!first) sb.append(", ");
      sb.append("_bcc:");
      if (this._bcc == null) {
        sb.append("null");
      } else {
        sb.append(this._bcc);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_baseMessage()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_baseMessage' is unset! Struct:" + toString());
    }

    if (!isSet_from()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_from' is unset! Struct:" + toString());
    }

    if (!isSet_subject()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_subject' is unset! Struct:" + toString());
    }

    if (!isSet_body()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_body' is unset! Struct:" + toString());
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

  private static class GenericEmailStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public GenericEmailStandardScheme getScheme() {
      return new GenericEmailStandardScheme();
    }
  }

  private static class GenericEmailStandardScheme extends org.apache.thrift.scheme.StandardScheme<GenericEmail> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, GenericEmail struct) throws org.apache.thrift.TException {
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
          case 3: // _FROM
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._from = iprot.readString();
              struct.set_fromIsSet(true);
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
          case 6: // _BCC
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._bcc = iprot.readString();
              struct.set_bccIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, GenericEmail struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct._to != null) {
        if (struct.isSet_to()) {
          oprot.writeFieldBegin(_TO_FIELD_DESC);
          oprot.writeString(struct._to);
          oprot.writeFieldEnd();
        }
      }
      if (struct._from != null) {
        oprot.writeFieldBegin(_FROM_FIELD_DESC);
        oprot.writeString(struct._from);
        oprot.writeFieldEnd();
      }
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
      if (struct._bcc != null) {
        if (struct.isSet_bcc()) {
          oprot.writeFieldBegin(_BCC_FIELD_DESC);
          oprot.writeString(struct._bcc);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class GenericEmailTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public GenericEmailTupleScheme getScheme() {
      return new GenericEmailTupleScheme();
    }
  }

  private static class GenericEmailTupleScheme extends org.apache.thrift.scheme.TupleScheme<GenericEmail> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, GenericEmail struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeString(struct._from);
      oprot.writeString(struct._subject);
      oprot.writeString(struct._body);
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSet_to()) {
        optionals.set(0);
      }
      if (struct.isSet_bcc()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSet_to()) {
        oprot.writeString(struct._to);
      }
      if (struct.isSet_bcc()) {
        oprot.writeString(struct._bcc);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, GenericEmail struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.Message();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._from = iprot.readString();
      struct.set_fromIsSet(true);
      struct._subject = iprot.readString();
      struct.set_subjectIsSet(true);
      struct._body = iprot.readString();
      struct.set_bodyIsSet(true);
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct._to = iprot.readString();
        struct.set_toIsSet(true);
      }
      if (incoming.get(1)) {
        struct._bcc = iprot.readString();
        struct.set_bccIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

