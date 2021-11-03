/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2021-11-03")
public class RfnMeterReadingData implements org.apache.thrift.TBase<RfnMeterReadingData, RfnMeterReadingData._Fields>, java.io.Serializable, Cloneable, Comparable<RfnMeterReadingData> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RfnMeterReadingData");

  private static final org.apache.thrift.protocol.TField CHANNEL_DATA_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("channelDataList", org.apache.thrift.protocol.TType.LIST, (short)1);
  private static final org.apache.thrift.protocol.TField DATED_CHANNEL_DATA_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("datedChannelDataList", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField RFN_IDENTIFIER_FIELD_DESC = new org.apache.thrift.protocol.TField("rfnIdentifier", org.apache.thrift.protocol.TType.STRUCT, (short)3);
  private static final org.apache.thrift.protocol.TField TIME_STAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("timeStamp", org.apache.thrift.protocol.TType.I64, (short)4);
  private static final org.apache.thrift.protocol.TField RECORD_INTERVAL_FIELD_DESC = new org.apache.thrift.protocol.TField("recordInterval", org.apache.thrift.protocol.TType.I32, (short)5);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new RfnMeterReadingDataStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new RfnMeterReadingDataTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable java.util.List<ChannelData> channelDataList; // required
  private @org.apache.thrift.annotation.Nullable java.util.List<DatedChannelData> datedChannelDataList; // required
  private @org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier rfnIdentifier; // required
  private long timeStamp; // required
  private int recordInterval; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    CHANNEL_DATA_LIST((short)1, "channelDataList"),
    DATED_CHANNEL_DATA_LIST((short)2, "datedChannelDataList"),
    RFN_IDENTIFIER((short)3, "rfnIdentifier"),
    TIME_STAMP((short)4, "timeStamp"),
    RECORD_INTERVAL((short)5, "recordInterval");

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
        case 1: // CHANNEL_DATA_LIST
          return CHANNEL_DATA_LIST;
        case 2: // DATED_CHANNEL_DATA_LIST
          return DATED_CHANNEL_DATA_LIST;
        case 3: // RFN_IDENTIFIER
          return RFN_IDENTIFIER;
        case 4: // TIME_STAMP
          return TIME_STAMP;
        case 5: // RECORD_INTERVAL
          return RECORD_INTERVAL;
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
  private static final int __TIMESTAMP_ISSET_ID = 0;
  private static final int __RECORDINTERVAL_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.CHANNEL_DATA_LIST, new org.apache.thrift.meta_data.FieldMetaData("channelDataList", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ChannelData.class))));
    tmpMap.put(_Fields.DATED_CHANNEL_DATA_LIST, new org.apache.thrift.meta_data.FieldMetaData("datedChannelDataList", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, DatedChannelData.class))));
    tmpMap.put(_Fields.RFN_IDENTIFIER, new org.apache.thrift.meta_data.FieldMetaData("rfnIdentifier", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier.class)));
    tmpMap.put(_Fields.TIME_STAMP, new org.apache.thrift.meta_data.FieldMetaData("timeStamp", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Timestamp")));
    tmpMap.put(_Fields.RECORD_INTERVAL, new org.apache.thrift.meta_data.FieldMetaData("recordInterval", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RfnMeterReadingData.class, metaDataMap);
  }

  public RfnMeterReadingData() {
  }

  public RfnMeterReadingData(
    java.util.List<ChannelData> channelDataList,
    java.util.List<DatedChannelData> datedChannelDataList,
    com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier rfnIdentifier,
    long timeStamp,
    int recordInterval)
  {
    this();
    this.channelDataList = channelDataList;
    this.datedChannelDataList = datedChannelDataList;
    this.rfnIdentifier = rfnIdentifier;
    this.timeStamp = timeStamp;
    setTimeStampIsSet(true);
    this.recordInterval = recordInterval;
    setRecordIntervalIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RfnMeterReadingData(RfnMeterReadingData other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetChannelDataList()) {
      java.util.List<ChannelData> __this__channelDataList = new java.util.ArrayList<ChannelData>(other.channelDataList.size());
      for (ChannelData other_element : other.channelDataList) {
        __this__channelDataList.add(new ChannelData(other_element));
      }
      this.channelDataList = __this__channelDataList;
    }
    if (other.isSetDatedChannelDataList()) {
      java.util.List<DatedChannelData> __this__datedChannelDataList = new java.util.ArrayList<DatedChannelData>(other.datedChannelDataList.size());
      for (DatedChannelData other_element : other.datedChannelDataList) {
        __this__datedChannelDataList.add(new DatedChannelData(other_element));
      }
      this.datedChannelDataList = __this__datedChannelDataList;
    }
    if (other.isSetRfnIdentifier()) {
      this.rfnIdentifier = new com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier(other.rfnIdentifier);
    }
    this.timeStamp = other.timeStamp;
    this.recordInterval = other.recordInterval;
  }

  public RfnMeterReadingData deepCopy() {
    return new RfnMeterReadingData(this);
  }

  @Override
  public void clear() {
    this.channelDataList = null;
    this.datedChannelDataList = null;
    this.rfnIdentifier = null;
    setTimeStampIsSet(false);
    this.timeStamp = 0;
    setRecordIntervalIsSet(false);
    this.recordInterval = 0;
  }

  public int getChannelDataListSize() {
    return (this.channelDataList == null) ? 0 : this.channelDataList.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<ChannelData> getChannelDataListIterator() {
    return (this.channelDataList == null) ? null : this.channelDataList.iterator();
  }

  public void addToChannelDataList(ChannelData elem) {
    if (this.channelDataList == null) {
      this.channelDataList = new java.util.ArrayList<ChannelData>();
    }
    this.channelDataList.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<ChannelData> getChannelDataList() {
    return this.channelDataList;
  }

  public void setChannelDataList(@org.apache.thrift.annotation.Nullable java.util.List<ChannelData> channelDataList) {
    this.channelDataList = channelDataList;
  }

  public void unsetChannelDataList() {
    this.channelDataList = null;
  }

  /** Returns true if field channelDataList is set (has been assigned a value) and false otherwise */
  public boolean isSetChannelDataList() {
    return this.channelDataList != null;
  }

  public void setChannelDataListIsSet(boolean value) {
    if (!value) {
      this.channelDataList = null;
    }
  }

  public int getDatedChannelDataListSize() {
    return (this.datedChannelDataList == null) ? 0 : this.datedChannelDataList.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<DatedChannelData> getDatedChannelDataListIterator() {
    return (this.datedChannelDataList == null) ? null : this.datedChannelDataList.iterator();
  }

  public void addToDatedChannelDataList(DatedChannelData elem) {
    if (this.datedChannelDataList == null) {
      this.datedChannelDataList = new java.util.ArrayList<DatedChannelData>();
    }
    this.datedChannelDataList.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.List<DatedChannelData> getDatedChannelDataList() {
    return this.datedChannelDataList;
  }

  public void setDatedChannelDataList(@org.apache.thrift.annotation.Nullable java.util.List<DatedChannelData> datedChannelDataList) {
    this.datedChannelDataList = datedChannelDataList;
  }

  public void unsetDatedChannelDataList() {
    this.datedChannelDataList = null;
  }

  /** Returns true if field datedChannelDataList is set (has been assigned a value) and false otherwise */
  public boolean isSetDatedChannelDataList() {
    return this.datedChannelDataList != null;
  }

  public void setDatedChannelDataListIsSet(boolean value) {
    if (!value) {
      this.datedChannelDataList = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier getRfnIdentifier() {
    return this.rfnIdentifier;
  }

  public void setRfnIdentifier(@org.apache.thrift.annotation.Nullable com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier rfnIdentifier) {
    this.rfnIdentifier = rfnIdentifier;
  }

  public void unsetRfnIdentifier() {
    this.rfnIdentifier = null;
  }

  /** Returns true if field rfnIdentifier is set (has been assigned a value) and false otherwise */
  public boolean isSetRfnIdentifier() {
    return this.rfnIdentifier != null;
  }

  public void setRfnIdentifierIsSet(boolean value) {
    if (!value) {
      this.rfnIdentifier = null;
    }
  }

  public long getTimeStamp() {
    return this.timeStamp;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
    setTimeStampIsSet(true);
  }

  public void unsetTimeStamp() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __TIMESTAMP_ISSET_ID);
  }

  /** Returns true if field timeStamp is set (has been assigned a value) and false otherwise */
  public boolean isSetTimeStamp() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __TIMESTAMP_ISSET_ID);
  }

  public void setTimeStampIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __TIMESTAMP_ISSET_ID, value);
  }

  public int getRecordInterval() {
    return this.recordInterval;
  }

  public void setRecordInterval(int recordInterval) {
    this.recordInterval = recordInterval;
    setRecordIntervalIsSet(true);
  }

  public void unsetRecordInterval() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __RECORDINTERVAL_ISSET_ID);
  }

  /** Returns true if field recordInterval is set (has been assigned a value) and false otherwise */
  public boolean isSetRecordInterval() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __RECORDINTERVAL_ISSET_ID);
  }

  public void setRecordIntervalIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __RECORDINTERVAL_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case CHANNEL_DATA_LIST:
      if (value == null) {
        unsetChannelDataList();
      } else {
        setChannelDataList((java.util.List<ChannelData>)value);
      }
      break;

    case DATED_CHANNEL_DATA_LIST:
      if (value == null) {
        unsetDatedChannelDataList();
      } else {
        setDatedChannelDataList((java.util.List<DatedChannelData>)value);
      }
      break;

    case RFN_IDENTIFIER:
      if (value == null) {
        unsetRfnIdentifier();
      } else {
        setRfnIdentifier((com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier)value);
      }
      break;

    case TIME_STAMP:
      if (value == null) {
        unsetTimeStamp();
      } else {
        setTimeStamp((java.lang.Long)value);
      }
      break;

    case RECORD_INTERVAL:
      if (value == null) {
        unsetRecordInterval();
      } else {
        setRecordInterval((java.lang.Integer)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case CHANNEL_DATA_LIST:
      return getChannelDataList();

    case DATED_CHANNEL_DATA_LIST:
      return getDatedChannelDataList();

    case RFN_IDENTIFIER:
      return getRfnIdentifier();

    case TIME_STAMP:
      return getTimeStamp();

    case RECORD_INTERVAL:
      return getRecordInterval();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case CHANNEL_DATA_LIST:
      return isSetChannelDataList();
    case DATED_CHANNEL_DATA_LIST:
      return isSetDatedChannelDataList();
    case RFN_IDENTIFIER:
      return isSetRfnIdentifier();
    case TIME_STAMP:
      return isSetTimeStamp();
    case RECORD_INTERVAL:
      return isSetRecordInterval();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof RfnMeterReadingData)
      return this.equals((RfnMeterReadingData)that);
    return false;
  }

  public boolean equals(RfnMeterReadingData that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_channelDataList = true && this.isSetChannelDataList();
    boolean that_present_channelDataList = true && that.isSetChannelDataList();
    if (this_present_channelDataList || that_present_channelDataList) {
      if (!(this_present_channelDataList && that_present_channelDataList))
        return false;
      if (!this.channelDataList.equals(that.channelDataList))
        return false;
    }

    boolean this_present_datedChannelDataList = true && this.isSetDatedChannelDataList();
    boolean that_present_datedChannelDataList = true && that.isSetDatedChannelDataList();
    if (this_present_datedChannelDataList || that_present_datedChannelDataList) {
      if (!(this_present_datedChannelDataList && that_present_datedChannelDataList))
        return false;
      if (!this.datedChannelDataList.equals(that.datedChannelDataList))
        return false;
    }

    boolean this_present_rfnIdentifier = true && this.isSetRfnIdentifier();
    boolean that_present_rfnIdentifier = true && that.isSetRfnIdentifier();
    if (this_present_rfnIdentifier || that_present_rfnIdentifier) {
      if (!(this_present_rfnIdentifier && that_present_rfnIdentifier))
        return false;
      if (!this.rfnIdentifier.equals(that.rfnIdentifier))
        return false;
    }

    boolean this_present_timeStamp = true;
    boolean that_present_timeStamp = true;
    if (this_present_timeStamp || that_present_timeStamp) {
      if (!(this_present_timeStamp && that_present_timeStamp))
        return false;
      if (this.timeStamp != that.timeStamp)
        return false;
    }

    boolean this_present_recordInterval = true;
    boolean that_present_recordInterval = true;
    if (this_present_recordInterval || that_present_recordInterval) {
      if (!(this_present_recordInterval && that_present_recordInterval))
        return false;
      if (this.recordInterval != that.recordInterval)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetChannelDataList()) ? 131071 : 524287);
    if (isSetChannelDataList())
      hashCode = hashCode * 8191 + channelDataList.hashCode();

    hashCode = hashCode * 8191 + ((isSetDatedChannelDataList()) ? 131071 : 524287);
    if (isSetDatedChannelDataList())
      hashCode = hashCode * 8191 + datedChannelDataList.hashCode();

    hashCode = hashCode * 8191 + ((isSetRfnIdentifier()) ? 131071 : 524287);
    if (isSetRfnIdentifier())
      hashCode = hashCode * 8191 + rfnIdentifier.hashCode();

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(timeStamp);

    hashCode = hashCode * 8191 + recordInterval;

    return hashCode;
  }

  @Override
  public int compareTo(RfnMeterReadingData other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetChannelDataList()).compareTo(other.isSetChannelDataList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetChannelDataList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.channelDataList, other.channelDataList);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetDatedChannelDataList()).compareTo(other.isSetDatedChannelDataList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDatedChannelDataList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.datedChannelDataList, other.datedChannelDataList);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetRfnIdentifier()).compareTo(other.isSetRfnIdentifier());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRfnIdentifier()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rfnIdentifier, other.rfnIdentifier);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetTimeStamp()).compareTo(other.isSetTimeStamp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTimeStamp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.timeStamp, other.timeStamp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetRecordInterval()).compareTo(other.isSetRecordInterval());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRecordInterval()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.recordInterval, other.recordInterval);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("RfnMeterReadingData(");
    boolean first = true;

    sb.append("channelDataList:");
    if (this.channelDataList == null) {
      sb.append("null");
    } else {
      sb.append(this.channelDataList);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("datedChannelDataList:");
    if (this.datedChannelDataList == null) {
      sb.append("null");
    } else {
      sb.append(this.datedChannelDataList);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("rfnIdentifier:");
    if (this.rfnIdentifier == null) {
      sb.append("null");
    } else {
      sb.append(this.rfnIdentifier);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("timeStamp:");
    sb.append(this.timeStamp);
    first = false;
    if (!first) sb.append(", ");
    sb.append("recordInterval:");
    sb.append(this.recordInterval);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetChannelDataList()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'channelDataList' is unset! Struct:" + toString());
    }

    if (!isSetDatedChannelDataList()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'datedChannelDataList' is unset! Struct:" + toString());
    }

    if (!isSetRfnIdentifier()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'rfnIdentifier' is unset! Struct:" + toString());
    }

    if (!isSetTimeStamp()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'timeStamp' is unset! Struct:" + toString());
    }

    if (!isSetRecordInterval()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'recordInterval' is unset! Struct:" + toString());
    }

    // check for sub-struct validity
    if (rfnIdentifier != null) {
      rfnIdentifier.validate();
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

  private static class RfnMeterReadingDataStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RfnMeterReadingDataStandardScheme getScheme() {
      return new RfnMeterReadingDataStandardScheme();
    }
  }

  private static class RfnMeterReadingDataStandardScheme extends org.apache.thrift.scheme.StandardScheme<RfnMeterReadingData> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RfnMeterReadingData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // CHANNEL_DATA_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                struct.channelDataList = new java.util.ArrayList<ChannelData>(_list8.size);
                @org.apache.thrift.annotation.Nullable ChannelData _elem9;
                for (int _i10 = 0; _i10 < _list8.size; ++_i10)
                {
                  _elem9 = new ChannelData();
                  _elem9.read(iprot);
                  struct.channelDataList.add(_elem9);
                }
                iprot.readListEnd();
              }
              struct.setChannelDataListIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // DATED_CHANNEL_DATA_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list11 = iprot.readListBegin();
                struct.datedChannelDataList = new java.util.ArrayList<DatedChannelData>(_list11.size);
                @org.apache.thrift.annotation.Nullable DatedChannelData _elem12;
                for (int _i13 = 0; _i13 < _list11.size; ++_i13)
                {
                  _elem12 = new DatedChannelData();
                  _elem12.read(iprot);
                  struct.datedChannelDataList.add(_elem12);
                }
                iprot.readListEnd();
              }
              struct.setDatedChannelDataListIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // RFN_IDENTIFIER
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.rfnIdentifier = new com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier();
              struct.rfnIdentifier.read(iprot);
              struct.setRfnIdentifierIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // TIME_STAMP
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.timeStamp = iprot.readI64();
              struct.setTimeStampIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // RECORD_INTERVAL
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.recordInterval = iprot.readI32();
              struct.setRecordIntervalIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, RfnMeterReadingData struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.channelDataList != null) {
        oprot.writeFieldBegin(CHANNEL_DATA_LIST_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.channelDataList.size()));
          for (ChannelData _iter14 : struct.channelDataList)
          {
            _iter14.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.datedChannelDataList != null) {
        oprot.writeFieldBegin(DATED_CHANNEL_DATA_LIST_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.datedChannelDataList.size()));
          for (DatedChannelData _iter15 : struct.datedChannelDataList)
          {
            _iter15.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.rfnIdentifier != null) {
        oprot.writeFieldBegin(RFN_IDENTIFIER_FIELD_DESC);
        struct.rfnIdentifier.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(TIME_STAMP_FIELD_DESC);
      oprot.writeI64(struct.timeStamp);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(RECORD_INTERVAL_FIELD_DESC);
      oprot.writeI32(struct.recordInterval);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RfnMeterReadingDataTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RfnMeterReadingDataTupleScheme getScheme() {
      return new RfnMeterReadingDataTupleScheme();
    }
  }

  private static class RfnMeterReadingDataTupleScheme extends org.apache.thrift.scheme.TupleScheme<RfnMeterReadingData> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RfnMeterReadingData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      {
        oprot.writeI32(struct.channelDataList.size());
        for (ChannelData _iter16 : struct.channelDataList)
        {
          _iter16.write(oprot);
        }
      }
      {
        oprot.writeI32(struct.datedChannelDataList.size());
        for (DatedChannelData _iter17 : struct.datedChannelDataList)
        {
          _iter17.write(oprot);
        }
      }
      struct.rfnIdentifier.write(oprot);
      oprot.writeI64(struct.timeStamp);
      oprot.writeI32(struct.recordInterval);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RfnMeterReadingData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      {
        org.apache.thrift.protocol.TList _list18 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.channelDataList = new java.util.ArrayList<ChannelData>(_list18.size);
        @org.apache.thrift.annotation.Nullable ChannelData _elem19;
        for (int _i20 = 0; _i20 < _list18.size; ++_i20)
        {
          _elem19 = new ChannelData();
          _elem19.read(iprot);
          struct.channelDataList.add(_elem19);
        }
      }
      struct.setChannelDataListIsSet(true);
      {
        org.apache.thrift.protocol.TList _list21 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.datedChannelDataList = new java.util.ArrayList<DatedChannelData>(_list21.size);
        @org.apache.thrift.annotation.Nullable DatedChannelData _elem22;
        for (int _i23 = 0; _i23 < _list21.size; ++_i23)
        {
          _elem22 = new DatedChannelData();
          _elem22.read(iprot);
          struct.datedChannelDataList.add(_elem22);
        }
      }
      struct.setDatedChannelDataListIsSet(true);
      struct.rfnIdentifier = new com.cannontech.messaging.serialization.thrift.generated.rfn.RfnIdentifier();
      struct.rfnIdentifier.read(iprot);
      struct.setRfnIdentifierIsSet(true);
      struct.timeStamp = iprot.readI64();
      struct.setTimeStampIsSet(true);
      struct.recordInterval = iprot.readI32();
      struct.setRecordIntervalIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

