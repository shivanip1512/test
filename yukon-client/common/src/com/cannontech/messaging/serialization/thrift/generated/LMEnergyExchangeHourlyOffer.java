/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public class LMEnergyExchangeHourlyOffer implements org.apache.thrift.TBase<LMEnergyExchangeHourlyOffer, LMEnergyExchangeHourlyOffer._Fields>, java.io.Serializable, Cloneable, Comparable<LMEnergyExchangeHourlyOffer> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("LMEnergyExchangeHourlyOffer");

  private static final org.apache.thrift.protocol.TField _OFFER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_offerId", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField _REVISION_NUMBER_FIELD_DESC = new org.apache.thrift.protocol.TField("_revisionNumber", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField _HOUR_FIELD_DESC = new org.apache.thrift.protocol.TField("_hour", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField _PRICE_FIELD_DESC = new org.apache.thrift.protocol.TField("_price", org.apache.thrift.protocol.TType.DOUBLE, (short)4);
  private static final org.apache.thrift.protocol.TField _AMOUNT_REQUESTED_FIELD_DESC = new org.apache.thrift.protocol.TField("_amountRequested", org.apache.thrift.protocol.TType.DOUBLE, (short)5);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new LMEnergyExchangeHourlyOfferStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new LMEnergyExchangeHourlyOfferTupleSchemeFactory();

  private int _offerId; // required
  private int _revisionNumber; // required
  private int _hour; // required
  private double _price; // required
  private double _amountRequested; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _OFFER_ID((short)1, "_offerId"),
    _REVISION_NUMBER((short)2, "_revisionNumber"),
    _HOUR((short)3, "_hour"),
    _PRICE((short)4, "_price"),
    _AMOUNT_REQUESTED((short)5, "_amountRequested");

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
        case 1: // _OFFER_ID
          return _OFFER_ID;
        case 2: // _REVISION_NUMBER
          return _REVISION_NUMBER;
        case 3: // _HOUR
          return _HOUR;
        case 4: // _PRICE
          return _PRICE;
        case 5: // _AMOUNT_REQUESTED
          return _AMOUNT_REQUESTED;
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
  private static final int ___OFFERID_ISSET_ID = 0;
  private static final int ___REVISIONNUMBER_ISSET_ID = 1;
  private static final int ___HOUR_ISSET_ID = 2;
  private static final int ___PRICE_ISSET_ID = 3;
  private static final int ___AMOUNTREQUESTED_ISSET_ID = 4;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._OFFER_ID, new org.apache.thrift.meta_data.FieldMetaData("_offerId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._REVISION_NUMBER, new org.apache.thrift.meta_data.FieldMetaData("_revisionNumber", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._HOUR, new org.apache.thrift.meta_data.FieldMetaData("_hour", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._PRICE, new org.apache.thrift.meta_data.FieldMetaData("_price", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields._AMOUNT_REQUESTED, new org.apache.thrift.meta_data.FieldMetaData("_amountRequested", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(LMEnergyExchangeHourlyOffer.class, metaDataMap);
  }

  public LMEnergyExchangeHourlyOffer() {
  }

  public LMEnergyExchangeHourlyOffer(
    int _offerId,
    int _revisionNumber,
    int _hour,
    double _price,
    double _amountRequested)
  {
    this();
    this._offerId = _offerId;
    set_offerIdIsSet(true);
    this._revisionNumber = _revisionNumber;
    set_revisionNumberIsSet(true);
    this._hour = _hour;
    set_hourIsSet(true);
    this._price = _price;
    set_priceIsSet(true);
    this._amountRequested = _amountRequested;
    set_amountRequestedIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public LMEnergyExchangeHourlyOffer(LMEnergyExchangeHourlyOffer other) {
    __isset_bitfield = other.__isset_bitfield;
    this._offerId = other._offerId;
    this._revisionNumber = other._revisionNumber;
    this._hour = other._hour;
    this._price = other._price;
    this._amountRequested = other._amountRequested;
  }

  public LMEnergyExchangeHourlyOffer deepCopy() {
    return new LMEnergyExchangeHourlyOffer(this);
  }

  @Override
  public void clear() {
    set_offerIdIsSet(false);
    this._offerId = 0;
    set_revisionNumberIsSet(false);
    this._revisionNumber = 0;
    set_hourIsSet(false);
    this._hour = 0;
    set_priceIsSet(false);
    this._price = 0.0;
    set_amountRequestedIsSet(false);
    this._amountRequested = 0.0;
  }

  public int get_offerId() {
    return this._offerId;
  }

  public void set_offerId(int _offerId) {
    this._offerId = _offerId;
    set_offerIdIsSet(true);
  }

  public void unset_offerId() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___OFFERID_ISSET_ID);
  }

  /** Returns true if field _offerId is set (has been assigned a value) and false otherwise */
  public boolean isSet_offerId() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___OFFERID_ISSET_ID);
  }

  public void set_offerIdIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___OFFERID_ISSET_ID, value);
  }

  public int get_revisionNumber() {
    return this._revisionNumber;
  }

  public void set_revisionNumber(int _revisionNumber) {
    this._revisionNumber = _revisionNumber;
    set_revisionNumberIsSet(true);
  }

  public void unset_revisionNumber() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___REVISIONNUMBER_ISSET_ID);
  }

  /** Returns true if field _revisionNumber is set (has been assigned a value) and false otherwise */
  public boolean isSet_revisionNumber() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___REVISIONNUMBER_ISSET_ID);
  }

  public void set_revisionNumberIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___REVISIONNUMBER_ISSET_ID, value);
  }

  public int get_hour() {
    return this._hour;
  }

  public void set_hour(int _hour) {
    this._hour = _hour;
    set_hourIsSet(true);
  }

  public void unset_hour() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___HOUR_ISSET_ID);
  }

  /** Returns true if field _hour is set (has been assigned a value) and false otherwise */
  public boolean isSet_hour() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___HOUR_ISSET_ID);
  }

  public void set_hourIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___HOUR_ISSET_ID, value);
  }

  public double get_price() {
    return this._price;
  }

  public void set_price(double _price) {
    this._price = _price;
    set_priceIsSet(true);
  }

  public void unset_price() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___PRICE_ISSET_ID);
  }

  /** Returns true if field _price is set (has been assigned a value) and false otherwise */
  public boolean isSet_price() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___PRICE_ISSET_ID);
  }

  public void set_priceIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___PRICE_ISSET_ID, value);
  }

  public double get_amountRequested() {
    return this._amountRequested;
  }

  public void set_amountRequested(double _amountRequested) {
    this._amountRequested = _amountRequested;
    set_amountRequestedIsSet(true);
  }

  public void unset_amountRequested() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___AMOUNTREQUESTED_ISSET_ID);
  }

  /** Returns true if field _amountRequested is set (has been assigned a value) and false otherwise */
  public boolean isSet_amountRequested() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___AMOUNTREQUESTED_ISSET_ID);
  }

  public void set_amountRequestedIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___AMOUNTREQUESTED_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _OFFER_ID:
      if (value == null) {
        unset_offerId();
      } else {
        set_offerId((java.lang.Integer)value);
      }
      break;

    case _REVISION_NUMBER:
      if (value == null) {
        unset_revisionNumber();
      } else {
        set_revisionNumber((java.lang.Integer)value);
      }
      break;

    case _HOUR:
      if (value == null) {
        unset_hour();
      } else {
        set_hour((java.lang.Integer)value);
      }
      break;

    case _PRICE:
      if (value == null) {
        unset_price();
      } else {
        set_price((java.lang.Double)value);
      }
      break;

    case _AMOUNT_REQUESTED:
      if (value == null) {
        unset_amountRequested();
      } else {
        set_amountRequested((java.lang.Double)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _OFFER_ID:
      return get_offerId();

    case _REVISION_NUMBER:
      return get_revisionNumber();

    case _HOUR:
      return get_hour();

    case _PRICE:
      return get_price();

    case _AMOUNT_REQUESTED:
      return get_amountRequested();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case _OFFER_ID:
      return isSet_offerId();
    case _REVISION_NUMBER:
      return isSet_revisionNumber();
    case _HOUR:
      return isSet_hour();
    case _PRICE:
      return isSet_price();
    case _AMOUNT_REQUESTED:
      return isSet_amountRequested();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof LMEnergyExchangeHourlyOffer)
      return this.equals((LMEnergyExchangeHourlyOffer)that);
    return false;
  }

  public boolean equals(LMEnergyExchangeHourlyOffer that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present__offerId = true;
    boolean that_present__offerId = true;
    if (this_present__offerId || that_present__offerId) {
      if (!(this_present__offerId && that_present__offerId))
        return false;
      if (this._offerId != that._offerId)
        return false;
    }

    boolean this_present__revisionNumber = true;
    boolean that_present__revisionNumber = true;
    if (this_present__revisionNumber || that_present__revisionNumber) {
      if (!(this_present__revisionNumber && that_present__revisionNumber))
        return false;
      if (this._revisionNumber != that._revisionNumber)
        return false;
    }

    boolean this_present__hour = true;
    boolean that_present__hour = true;
    if (this_present__hour || that_present__hour) {
      if (!(this_present__hour && that_present__hour))
        return false;
      if (this._hour != that._hour)
        return false;
    }

    boolean this_present__price = true;
    boolean that_present__price = true;
    if (this_present__price || that_present__price) {
      if (!(this_present__price && that_present__price))
        return false;
      if (this._price != that._price)
        return false;
    }

    boolean this_present__amountRequested = true;
    boolean that_present__amountRequested = true;
    if (this_present__amountRequested || that_present__amountRequested) {
      if (!(this_present__amountRequested && that_present__amountRequested))
        return false;
      if (this._amountRequested != that._amountRequested)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + _offerId;

    hashCode = hashCode * 8191 + _revisionNumber;

    hashCode = hashCode * 8191 + _hour;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_price);

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_amountRequested);

    return hashCode;
  }

  @Override
  public int compareTo(LMEnergyExchangeHourlyOffer other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSet_offerId()).compareTo(other.isSet_offerId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_offerId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._offerId, other._offerId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_revisionNumber()).compareTo(other.isSet_revisionNumber());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_revisionNumber()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._revisionNumber, other._revisionNumber);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_hour()).compareTo(other.isSet_hour());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_hour()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._hour, other._hour);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_price()).compareTo(other.isSet_price());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_price()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._price, other._price);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_amountRequested()).compareTo(other.isSet_amountRequested());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_amountRequested()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._amountRequested, other._amountRequested);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("LMEnergyExchangeHourlyOffer(");
    boolean first = true;

    sb.append("_offerId:");
    sb.append(this._offerId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_revisionNumber:");
    sb.append(this._revisionNumber);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_hour:");
    sb.append(this._hour);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_price:");
    sb.append(this._price);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_amountRequested:");
    sb.append(this._amountRequested);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_offerId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_offerId' is unset! Struct:" + toString());
    }

    if (!isSet_revisionNumber()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_revisionNumber' is unset! Struct:" + toString());
    }

    if (!isSet_hour()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_hour' is unset! Struct:" + toString());
    }

    if (!isSet_price()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_price' is unset! Struct:" + toString());
    }

    if (!isSet_amountRequested()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_amountRequested' is unset! Struct:" + toString());
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class LMEnergyExchangeHourlyOfferStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public LMEnergyExchangeHourlyOfferStandardScheme getScheme() {
      return new LMEnergyExchangeHourlyOfferStandardScheme();
    }
  }

  private static class LMEnergyExchangeHourlyOfferStandardScheme extends org.apache.thrift.scheme.StandardScheme<LMEnergyExchangeHourlyOffer> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, LMEnergyExchangeHourlyOffer struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // _OFFER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._offerId = iprot.readI32();
              struct.set_offerIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _REVISION_NUMBER
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._revisionNumber = iprot.readI32();
              struct.set_revisionNumberIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _HOUR
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._hour = iprot.readI32();
              struct.set_hourIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _PRICE
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct._price = iprot.readDouble();
              struct.set_priceIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // _AMOUNT_REQUESTED
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct._amountRequested = iprot.readDouble();
              struct.set_amountRequestedIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, LMEnergyExchangeHourlyOffer struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(_OFFER_ID_FIELD_DESC);
      oprot.writeI32(struct._offerId);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_REVISION_NUMBER_FIELD_DESC);
      oprot.writeI32(struct._revisionNumber);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_HOUR_FIELD_DESC);
      oprot.writeI32(struct._hour);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_PRICE_FIELD_DESC);
      oprot.writeDouble(struct._price);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_AMOUNT_REQUESTED_FIELD_DESC);
      oprot.writeDouble(struct._amountRequested);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class LMEnergyExchangeHourlyOfferTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public LMEnergyExchangeHourlyOfferTupleScheme getScheme() {
      return new LMEnergyExchangeHourlyOfferTupleScheme();
    }
  }

  private static class LMEnergyExchangeHourlyOfferTupleScheme extends org.apache.thrift.scheme.TupleScheme<LMEnergyExchangeHourlyOffer> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, LMEnergyExchangeHourlyOffer struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeI32(struct._offerId);
      oprot.writeI32(struct._revisionNumber);
      oprot.writeI32(struct._hour);
      oprot.writeDouble(struct._price);
      oprot.writeDouble(struct._amountRequested);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, LMEnergyExchangeHourlyOffer struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._offerId = iprot.readI32();
      struct.set_offerIdIsSet(true);
      struct._revisionNumber = iprot.readI32();
      struct.set_revisionNumberIsSet(true);
      struct._hour = iprot.readI32();
      struct.set_hourIsSet(true);
      struct._price = iprot.readDouble();
      struct.set_priceIsSet(true);
      struct._amountRequested = iprot.readDouble();
      struct.set_amountRequestedIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

