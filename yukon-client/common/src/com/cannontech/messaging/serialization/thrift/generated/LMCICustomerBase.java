/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2019-12-16")
public class LMCICustomerBase implements org.apache.thrift.TBase<LMCICustomerBase, LMCICustomerBase._Fields>, java.io.Serializable, Cloneable, Comparable<LMCICustomerBase> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("LMCICustomerBase");

  private static final org.apache.thrift.protocol.TField _CUSTOMER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_customerId", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField _COMPANY_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("_companyName", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField _CUSTOMER_DEMAND_LEVEL_FIELD_DESC = new org.apache.thrift.protocol.TField("_customerDemandLevel", org.apache.thrift.protocol.TType.DOUBLE, (short)3);
  private static final org.apache.thrift.protocol.TField _CURTAIL_AMOUNT_FIELD_DESC = new org.apache.thrift.protocol.TField("_curtailAmount", org.apache.thrift.protocol.TType.DOUBLE, (short)4);
  private static final org.apache.thrift.protocol.TField _CURTAILMENT_AGREEMENT_FIELD_DESC = new org.apache.thrift.protocol.TField("_curtailmentAgreement", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField _TIME_ZONE_FIELD_DESC = new org.apache.thrift.protocol.TField("_timeZone", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField _CUSTOMER_ORDER_FIELD_DESC = new org.apache.thrift.protocol.TField("_customerOrder", org.apache.thrift.protocol.TType.I32, (short)7);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new LMCICustomerBaseStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new LMCICustomerBaseTupleSchemeFactory();

  private int _customerId; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _companyName; // required
  private double _customerDemandLevel; // required
  private double _curtailAmount; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _curtailmentAgreement; // required
  private @org.apache.thrift.annotation.Nullable java.lang.String _timeZone; // required
  private int _customerOrder; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _CUSTOMER_ID((short)1, "_customerId"),
    _COMPANY_NAME((short)2, "_companyName"),
    _CUSTOMER_DEMAND_LEVEL((short)3, "_customerDemandLevel"),
    _CURTAIL_AMOUNT((short)4, "_curtailAmount"),
    _CURTAILMENT_AGREEMENT((short)5, "_curtailmentAgreement"),
    _TIME_ZONE((short)6, "_timeZone"),
    _CUSTOMER_ORDER((short)7, "_customerOrder");

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
        case 1: // _CUSTOMER_ID
          return _CUSTOMER_ID;
        case 2: // _COMPANY_NAME
          return _COMPANY_NAME;
        case 3: // _CUSTOMER_DEMAND_LEVEL
          return _CUSTOMER_DEMAND_LEVEL;
        case 4: // _CURTAIL_AMOUNT
          return _CURTAIL_AMOUNT;
        case 5: // _CURTAILMENT_AGREEMENT
          return _CURTAILMENT_AGREEMENT;
        case 6: // _TIME_ZONE
          return _TIME_ZONE;
        case 7: // _CUSTOMER_ORDER
          return _CUSTOMER_ORDER;
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
  private static final int ___CUSTOMERDEMANDLEVEL_ISSET_ID = 1;
  private static final int ___CURTAILAMOUNT_ISSET_ID = 2;
  private static final int ___CUSTOMERORDER_ISSET_ID = 3;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._CUSTOMER_ID, new org.apache.thrift.meta_data.FieldMetaData("_customerId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._COMPANY_NAME, new org.apache.thrift.meta_data.FieldMetaData("_companyName", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._CUSTOMER_DEMAND_LEVEL, new org.apache.thrift.meta_data.FieldMetaData("_customerDemandLevel", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields._CURTAIL_AMOUNT, new org.apache.thrift.meta_data.FieldMetaData("_curtailAmount", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields._CURTAILMENT_AGREEMENT, new org.apache.thrift.meta_data.FieldMetaData("_curtailmentAgreement", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._TIME_ZONE, new org.apache.thrift.meta_data.FieldMetaData("_timeZone", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields._CUSTOMER_ORDER, new org.apache.thrift.meta_data.FieldMetaData("_customerOrder", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(LMCICustomerBase.class, metaDataMap);
  }

  public LMCICustomerBase() {
  }

  public LMCICustomerBase(
    int _customerId,
    java.lang.String _companyName,
    double _customerDemandLevel,
    double _curtailAmount,
    java.lang.String _curtailmentAgreement,
    java.lang.String _timeZone,
    int _customerOrder)
  {
    this();
    this._customerId = _customerId;
    set_customerIdIsSet(true);
    this._companyName = _companyName;
    this._customerDemandLevel = _customerDemandLevel;
    set_customerDemandLevelIsSet(true);
    this._curtailAmount = _curtailAmount;
    set_curtailAmountIsSet(true);
    this._curtailmentAgreement = _curtailmentAgreement;
    this._timeZone = _timeZone;
    this._customerOrder = _customerOrder;
    set_customerOrderIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public LMCICustomerBase(LMCICustomerBase other) {
    __isset_bitfield = other.__isset_bitfield;
    this._customerId = other._customerId;
    if (other.isSet_companyName()) {
      this._companyName = other._companyName;
    }
    this._customerDemandLevel = other._customerDemandLevel;
    this._curtailAmount = other._curtailAmount;
    if (other.isSet_curtailmentAgreement()) {
      this._curtailmentAgreement = other._curtailmentAgreement;
    }
    if (other.isSet_timeZone()) {
      this._timeZone = other._timeZone;
    }
    this._customerOrder = other._customerOrder;
  }

  public LMCICustomerBase deepCopy() {
    return new LMCICustomerBase(this);
  }

  @Override
  public void clear() {
    set_customerIdIsSet(false);
    this._customerId = 0;
    this._companyName = null;
    set_customerDemandLevelIsSet(false);
    this._customerDemandLevel = 0.0;
    set_curtailAmountIsSet(false);
    this._curtailAmount = 0.0;
    this._curtailmentAgreement = null;
    this._timeZone = null;
    set_customerOrderIsSet(false);
    this._customerOrder = 0;
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
  public java.lang.String get_companyName() {
    return this._companyName;
  }

  public void set_companyName(@org.apache.thrift.annotation.Nullable java.lang.String _companyName) {
    this._companyName = _companyName;
  }

  public void unset_companyName() {
    this._companyName = null;
  }

  /** Returns true if field _companyName is set (has been assigned a value) and false otherwise */
  public boolean isSet_companyName() {
    return this._companyName != null;
  }

  public void set_companyNameIsSet(boolean value) {
    if (!value) {
      this._companyName = null;
    }
  }

  public double get_customerDemandLevel() {
    return this._customerDemandLevel;
  }

  public void set_customerDemandLevel(double _customerDemandLevel) {
    this._customerDemandLevel = _customerDemandLevel;
    set_customerDemandLevelIsSet(true);
  }

  public void unset_customerDemandLevel() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___CUSTOMERDEMANDLEVEL_ISSET_ID);
  }

  /** Returns true if field _customerDemandLevel is set (has been assigned a value) and false otherwise */
  public boolean isSet_customerDemandLevel() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___CUSTOMERDEMANDLEVEL_ISSET_ID);
  }

  public void set_customerDemandLevelIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___CUSTOMERDEMANDLEVEL_ISSET_ID, value);
  }

  public double get_curtailAmount() {
    return this._curtailAmount;
  }

  public void set_curtailAmount(double _curtailAmount) {
    this._curtailAmount = _curtailAmount;
    set_curtailAmountIsSet(true);
  }

  public void unset_curtailAmount() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___CURTAILAMOUNT_ISSET_ID);
  }

  /** Returns true if field _curtailAmount is set (has been assigned a value) and false otherwise */
  public boolean isSet_curtailAmount() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___CURTAILAMOUNT_ISSET_ID);
  }

  public void set_curtailAmountIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___CURTAILAMOUNT_ISSET_ID, value);
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_curtailmentAgreement() {
    return this._curtailmentAgreement;
  }

  public void set_curtailmentAgreement(@org.apache.thrift.annotation.Nullable java.lang.String _curtailmentAgreement) {
    this._curtailmentAgreement = _curtailmentAgreement;
  }

  public void unset_curtailmentAgreement() {
    this._curtailmentAgreement = null;
  }

  /** Returns true if field _curtailmentAgreement is set (has been assigned a value) and false otherwise */
  public boolean isSet_curtailmentAgreement() {
    return this._curtailmentAgreement != null;
  }

  public void set_curtailmentAgreementIsSet(boolean value) {
    if (!value) {
      this._curtailmentAgreement = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String get_timeZone() {
    return this._timeZone;
  }

  public void set_timeZone(@org.apache.thrift.annotation.Nullable java.lang.String _timeZone) {
    this._timeZone = _timeZone;
  }

  public void unset_timeZone() {
    this._timeZone = null;
  }

  /** Returns true if field _timeZone is set (has been assigned a value) and false otherwise */
  public boolean isSet_timeZone() {
    return this._timeZone != null;
  }

  public void set_timeZoneIsSet(boolean value) {
    if (!value) {
      this._timeZone = null;
    }
  }

  public int get_customerOrder() {
    return this._customerOrder;
  }

  public void set_customerOrder(int _customerOrder) {
    this._customerOrder = _customerOrder;
    set_customerOrderIsSet(true);
  }

  public void unset_customerOrder() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, ___CUSTOMERORDER_ISSET_ID);
  }

  /** Returns true if field _customerOrder is set (has been assigned a value) and false otherwise */
  public boolean isSet_customerOrder() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, ___CUSTOMERORDER_ISSET_ID);
  }

  public void set_customerOrderIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, ___CUSTOMERORDER_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case _CUSTOMER_ID:
      if (value == null) {
        unset_customerId();
      } else {
        set_customerId((java.lang.Integer)value);
      }
      break;

    case _COMPANY_NAME:
      if (value == null) {
        unset_companyName();
      } else {
        set_companyName((java.lang.String)value);
      }
      break;

    case _CUSTOMER_DEMAND_LEVEL:
      if (value == null) {
        unset_customerDemandLevel();
      } else {
        set_customerDemandLevel((java.lang.Double)value);
      }
      break;

    case _CURTAIL_AMOUNT:
      if (value == null) {
        unset_curtailAmount();
      } else {
        set_curtailAmount((java.lang.Double)value);
      }
      break;

    case _CURTAILMENT_AGREEMENT:
      if (value == null) {
        unset_curtailmentAgreement();
      } else {
        set_curtailmentAgreement((java.lang.String)value);
      }
      break;

    case _TIME_ZONE:
      if (value == null) {
        unset_timeZone();
      } else {
        set_timeZone((java.lang.String)value);
      }
      break;

    case _CUSTOMER_ORDER:
      if (value == null) {
        unset_customerOrder();
      } else {
        set_customerOrder((java.lang.Integer)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case _CUSTOMER_ID:
      return get_customerId();

    case _COMPANY_NAME:
      return get_companyName();

    case _CUSTOMER_DEMAND_LEVEL:
      return get_customerDemandLevel();

    case _CURTAIL_AMOUNT:
      return get_curtailAmount();

    case _CURTAILMENT_AGREEMENT:
      return get_curtailmentAgreement();

    case _TIME_ZONE:
      return get_timeZone();

    case _CUSTOMER_ORDER:
      return get_customerOrder();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case _CUSTOMER_ID:
      return isSet_customerId();
    case _COMPANY_NAME:
      return isSet_companyName();
    case _CUSTOMER_DEMAND_LEVEL:
      return isSet_customerDemandLevel();
    case _CURTAIL_AMOUNT:
      return isSet_curtailAmount();
    case _CURTAILMENT_AGREEMENT:
      return isSet_curtailmentAgreement();
    case _TIME_ZONE:
      return isSet_timeZone();
    case _CUSTOMER_ORDER:
      return isSet_customerOrder();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof LMCICustomerBase)
      return this.equals((LMCICustomerBase)that);
    return false;
  }

  public boolean equals(LMCICustomerBase that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present__customerId = true;
    boolean that_present__customerId = true;
    if (this_present__customerId || that_present__customerId) {
      if (!(this_present__customerId && that_present__customerId))
        return false;
      if (this._customerId != that._customerId)
        return false;
    }

    boolean this_present__companyName = true && this.isSet_companyName();
    boolean that_present__companyName = true && that.isSet_companyName();
    if (this_present__companyName || that_present__companyName) {
      if (!(this_present__companyName && that_present__companyName))
        return false;
      if (!this._companyName.equals(that._companyName))
        return false;
    }

    boolean this_present__customerDemandLevel = true;
    boolean that_present__customerDemandLevel = true;
    if (this_present__customerDemandLevel || that_present__customerDemandLevel) {
      if (!(this_present__customerDemandLevel && that_present__customerDemandLevel))
        return false;
      if (this._customerDemandLevel != that._customerDemandLevel)
        return false;
    }

    boolean this_present__curtailAmount = true;
    boolean that_present__curtailAmount = true;
    if (this_present__curtailAmount || that_present__curtailAmount) {
      if (!(this_present__curtailAmount && that_present__curtailAmount))
        return false;
      if (this._curtailAmount != that._curtailAmount)
        return false;
    }

    boolean this_present__curtailmentAgreement = true && this.isSet_curtailmentAgreement();
    boolean that_present__curtailmentAgreement = true && that.isSet_curtailmentAgreement();
    if (this_present__curtailmentAgreement || that_present__curtailmentAgreement) {
      if (!(this_present__curtailmentAgreement && that_present__curtailmentAgreement))
        return false;
      if (!this._curtailmentAgreement.equals(that._curtailmentAgreement))
        return false;
    }

    boolean this_present__timeZone = true && this.isSet_timeZone();
    boolean that_present__timeZone = true && that.isSet_timeZone();
    if (this_present__timeZone || that_present__timeZone) {
      if (!(this_present__timeZone && that_present__timeZone))
        return false;
      if (!this._timeZone.equals(that._timeZone))
        return false;
    }

    boolean this_present__customerOrder = true;
    boolean that_present__customerOrder = true;
    if (this_present__customerOrder || that_present__customerOrder) {
      if (!(this_present__customerOrder && that_present__customerOrder))
        return false;
      if (this._customerOrder != that._customerOrder)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + _customerId;

    hashCode = hashCode * 8191 + ((isSet_companyName()) ? 131071 : 524287);
    if (isSet_companyName())
      hashCode = hashCode * 8191 + _companyName.hashCode();

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_customerDemandLevel);

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(_curtailAmount);

    hashCode = hashCode * 8191 + ((isSet_curtailmentAgreement()) ? 131071 : 524287);
    if (isSet_curtailmentAgreement())
      hashCode = hashCode * 8191 + _curtailmentAgreement.hashCode();

    hashCode = hashCode * 8191 + ((isSet_timeZone()) ? 131071 : 524287);
    if (isSet_timeZone())
      hashCode = hashCode * 8191 + _timeZone.hashCode();

    hashCode = hashCode * 8191 + _customerOrder;

    return hashCode;
  }

  @Override
  public int compareTo(LMCICustomerBase other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

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
    lastComparison = java.lang.Boolean.valueOf(isSet_companyName()).compareTo(other.isSet_companyName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_companyName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._companyName, other._companyName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_customerDemandLevel()).compareTo(other.isSet_customerDemandLevel());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_customerDemandLevel()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._customerDemandLevel, other._customerDemandLevel);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_curtailAmount()).compareTo(other.isSet_curtailAmount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_curtailAmount()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._curtailAmount, other._curtailAmount);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_curtailmentAgreement()).compareTo(other.isSet_curtailmentAgreement());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_curtailmentAgreement()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._curtailmentAgreement, other._curtailmentAgreement);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_timeZone()).compareTo(other.isSet_timeZone());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_timeZone()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._timeZone, other._timeZone);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSet_customerOrder()).compareTo(other.isSet_customerOrder());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_customerOrder()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._customerOrder, other._customerOrder);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("LMCICustomerBase(");
    boolean first = true;

    sb.append("_customerId:");
    sb.append(this._customerId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_companyName:");
    if (this._companyName == null) {
      sb.append("null");
    } else {
      sb.append(this._companyName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_customerDemandLevel:");
    sb.append(this._customerDemandLevel);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_curtailAmount:");
    sb.append(this._curtailAmount);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_curtailmentAgreement:");
    if (this._curtailmentAgreement == null) {
      sb.append("null");
    } else {
      sb.append(this._curtailmentAgreement);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_timeZone:");
    if (this._timeZone == null) {
      sb.append("null");
    } else {
      sb.append(this._timeZone);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_customerOrder:");
    sb.append(this._customerOrder);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSet_customerId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_customerId' is unset! Struct:" + toString());
    }

    if (!isSet_companyName()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_companyName' is unset! Struct:" + toString());
    }

    if (!isSet_customerDemandLevel()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_customerDemandLevel' is unset! Struct:" + toString());
    }

    if (!isSet_curtailAmount()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_curtailAmount' is unset! Struct:" + toString());
    }

    if (!isSet_curtailmentAgreement()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_curtailmentAgreement' is unset! Struct:" + toString());
    }

    if (!isSet_timeZone()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_timeZone' is unset! Struct:" + toString());
    }

    if (!isSet_customerOrder()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_customerOrder' is unset! Struct:" + toString());
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

  private static class LMCICustomerBaseStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public LMCICustomerBaseStandardScheme getScheme() {
      return new LMCICustomerBaseStandardScheme();
    }
  }

  private static class LMCICustomerBaseStandardScheme extends org.apache.thrift.scheme.StandardScheme<LMCICustomerBase> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, LMCICustomerBase struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // _CUSTOMER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._customerId = iprot.readI32();
              struct.set_customerIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _COMPANY_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._companyName = iprot.readString();
              struct.set_companyNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _CUSTOMER_DEMAND_LEVEL
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct._customerDemandLevel = iprot.readDouble();
              struct.set_customerDemandLevelIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _CURTAIL_AMOUNT
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct._curtailAmount = iprot.readDouble();
              struct.set_curtailAmountIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // _CURTAILMENT_AGREEMENT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._curtailmentAgreement = iprot.readString();
              struct.set_curtailmentAgreementIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // _TIME_ZONE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._timeZone = iprot.readString();
              struct.set_timeZoneIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // _CUSTOMER_ORDER
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._customerOrder = iprot.readI32();
              struct.set_customerOrderIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, LMCICustomerBase struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(_CUSTOMER_ID_FIELD_DESC);
      oprot.writeI32(struct._customerId);
      oprot.writeFieldEnd();
      if (struct._companyName != null) {
        oprot.writeFieldBegin(_COMPANY_NAME_FIELD_DESC);
        oprot.writeString(struct._companyName);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_CUSTOMER_DEMAND_LEVEL_FIELD_DESC);
      oprot.writeDouble(struct._customerDemandLevel);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(_CURTAIL_AMOUNT_FIELD_DESC);
      oprot.writeDouble(struct._curtailAmount);
      oprot.writeFieldEnd();
      if (struct._curtailmentAgreement != null) {
        oprot.writeFieldBegin(_CURTAILMENT_AGREEMENT_FIELD_DESC);
        oprot.writeString(struct._curtailmentAgreement);
        oprot.writeFieldEnd();
      }
      if (struct._timeZone != null) {
        oprot.writeFieldBegin(_TIME_ZONE_FIELD_DESC);
        oprot.writeString(struct._timeZone);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_CUSTOMER_ORDER_FIELD_DESC);
      oprot.writeI32(struct._customerOrder);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class LMCICustomerBaseTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public LMCICustomerBaseTupleScheme getScheme() {
      return new LMCICustomerBaseTupleScheme();
    }
  }

  private static class LMCICustomerBaseTupleScheme extends org.apache.thrift.scheme.TupleScheme<LMCICustomerBase> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, LMCICustomerBase struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeI32(struct._customerId);
      oprot.writeString(struct._companyName);
      oprot.writeDouble(struct._customerDemandLevel);
      oprot.writeDouble(struct._curtailAmount);
      oprot.writeString(struct._curtailmentAgreement);
      oprot.writeString(struct._timeZone);
      oprot.writeI32(struct._customerOrder);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, LMCICustomerBase struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct._customerId = iprot.readI32();
      struct.set_customerIdIsSet(true);
      struct._companyName = iprot.readString();
      struct.set_companyNameIsSet(true);
      struct._customerDemandLevel = iprot.readDouble();
      struct.set_customerDemandLevelIsSet(true);
      struct._curtailAmount = iprot.readDouble();
      struct.set_curtailAmountIsSet(true);
      struct._curtailmentAgreement = iprot.readString();
      struct.set_curtailmentAgreementIsSet(true);
      struct._timeZone = iprot.readString();
      struct.set_timeZoneIsSet(true);
      struct._customerOrder = iprot.readI32();
      struct.set_customerOrderIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

