/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.16.0)", date = "2022-05-31")
public class ChannelInfo implements org.apache.thrift.TBase<ChannelInfo, ChannelInfo._Fields>, java.io.Serializable, Cloneable, Comparable<ChannelInfo> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ChannelInfo");

  private static final org.apache.thrift.protocol.TField UOM_FIELD_DESC = new org.apache.thrift.protocol.TField("UOM", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField UOM_MODIFIER_FIELD_DESC = new org.apache.thrift.protocol.TField("uomModifier", org.apache.thrift.protocol.TType.SET, (short)2);
  private static final org.apache.thrift.protocol.TField CHANNEL_NUM_FIELD_DESC = new org.apache.thrift.protocol.TField("channelNum", org.apache.thrift.protocol.TType.I16, (short)3);
  private static final org.apache.thrift.protocol.TField ENABLED_FIELD_DESC = new org.apache.thrift.protocol.TField("enabled", org.apache.thrift.protocol.TType.BOOL, (short)4);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new ChannelInfoStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new ChannelInfoTupleSchemeFactory();

  private @org.apache.thrift.annotation.Nullable java.lang.String UOM; // required
  private @org.apache.thrift.annotation.Nullable java.util.Set<java.lang.String> uomModifier; // required
  private short channelNum; // required
  private boolean enabled; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    UOM((short)1, "UOM"),
    UOM_MODIFIER((short)2, "uomModifier"),
    CHANNEL_NUM((short)3, "channelNum"),
    ENABLED((short)4, "enabled");

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
        case 1: // UOM
          return UOM;
        case 2: // UOM_MODIFIER
          return UOM_MODIFIER;
        case 3: // CHANNEL_NUM
          return CHANNEL_NUM;
        case 4: // ENABLED
          return ENABLED;
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
  private static final int __CHANNELNUM_ISSET_ID = 0;
  private static final int __ENABLED_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.UOM, new org.apache.thrift.meta_data.FieldMetaData("UOM", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.UOM_MODIFIER, new org.apache.thrift.meta_data.FieldMetaData("uomModifier", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.CHANNEL_NUM, new org.apache.thrift.meta_data.FieldMetaData("channelNum", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I16)));
    tmpMap.put(_Fields.ENABLED, new org.apache.thrift.meta_data.FieldMetaData("enabled", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ChannelInfo.class, metaDataMap);
  }

  public ChannelInfo() {
  }

  public ChannelInfo(
    java.lang.String UOM,
    java.util.Set<java.lang.String> uomModifier,
    short channelNum,
    boolean enabled)
  {
    this();
    this.UOM = UOM;
    this.uomModifier = uomModifier;
    this.channelNum = channelNum;
    setChannelNumIsSet(true);
    this.enabled = enabled;
    setEnabledIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ChannelInfo(ChannelInfo other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetUOM()) {
      this.UOM = other.UOM;
    }
    if (other.isSetUomModifier()) {
      java.util.Set<java.lang.String> __this__uomModifier = new java.util.HashSet<java.lang.String>(other.uomModifier);
      this.uomModifier = __this__uomModifier;
    }
    this.channelNum = other.channelNum;
    this.enabled = other.enabled;
  }

  public ChannelInfo deepCopy() {
    return new ChannelInfo(this);
  }

  @Override
  public void clear() {
    this.UOM = null;
    this.uomModifier = null;
    setChannelNumIsSet(false);
    this.channelNum = 0;
    setEnabledIsSet(false);
    this.enabled = false;
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getUOM() {
    return this.UOM;
  }

  public void setUOM(@org.apache.thrift.annotation.Nullable java.lang.String UOM) {
    this.UOM = UOM;
  }

  public void unsetUOM() {
    this.UOM = null;
  }

  /** Returns true if field UOM is set (has been assigned a value) and false otherwise */
  public boolean isSetUOM() {
    return this.UOM != null;
  }

  public void setUOMIsSet(boolean value) {
    if (!value) {
      this.UOM = null;
    }
  }

  public int getUomModifierSize() {
    return (this.uomModifier == null) ? 0 : this.uomModifier.size();
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Iterator<java.lang.String> getUomModifierIterator() {
    return (this.uomModifier == null) ? null : this.uomModifier.iterator();
  }

  public void addToUomModifier(java.lang.String elem) {
    if (this.uomModifier == null) {
      this.uomModifier = new java.util.HashSet<java.lang.String>();
    }
    this.uomModifier.add(elem);
  }

  @org.apache.thrift.annotation.Nullable
  public java.util.Set<java.lang.String> getUomModifier() {
    return this.uomModifier;
  }

  public void setUomModifier(@org.apache.thrift.annotation.Nullable java.util.Set<java.lang.String> uomModifier) {
    this.uomModifier = uomModifier;
  }

  public void unsetUomModifier() {
    this.uomModifier = null;
  }

  /** Returns true if field uomModifier is set (has been assigned a value) and false otherwise */
  public boolean isSetUomModifier() {
    return this.uomModifier != null;
  }

  public void setUomModifierIsSet(boolean value) {
    if (!value) {
      this.uomModifier = null;
    }
  }

  public short getChannelNum() {
    return this.channelNum;
  }

  public void setChannelNum(short channelNum) {
    this.channelNum = channelNum;
    setChannelNumIsSet(true);
  }

  public void unsetChannelNum() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __CHANNELNUM_ISSET_ID);
  }

  /** Returns true if field channelNum is set (has been assigned a value) and false otherwise */
  public boolean isSetChannelNum() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __CHANNELNUM_ISSET_ID);
  }

  public void setChannelNumIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __CHANNELNUM_ISSET_ID, value);
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    setEnabledIsSet(true);
  }

  public void unsetEnabled() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __ENABLED_ISSET_ID);
  }

  /** Returns true if field enabled is set (has been assigned a value) and false otherwise */
  public boolean isSetEnabled() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __ENABLED_ISSET_ID);
  }

  public void setEnabledIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __ENABLED_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case UOM:
      if (value == null) {
        unsetUOM();
      } else {
        setUOM((java.lang.String)value);
      }
      break;

    case UOM_MODIFIER:
      if (value == null) {
        unsetUomModifier();
      } else {
        setUomModifier((java.util.Set<java.lang.String>)value);
      }
      break;

    case CHANNEL_NUM:
      if (value == null) {
        unsetChannelNum();
      } else {
        setChannelNum((java.lang.Short)value);
      }
      break;

    case ENABLED:
      if (value == null) {
        unsetEnabled();
      } else {
        setEnabled((java.lang.Boolean)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case UOM:
      return getUOM();

    case UOM_MODIFIER:
      return getUomModifier();

    case CHANNEL_NUM:
      return getChannelNum();

    case ENABLED:
      return isEnabled();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case UOM:
      return isSetUOM();
    case UOM_MODIFIER:
      return isSetUomModifier();
    case CHANNEL_NUM:
      return isSetChannelNum();
    case ENABLED:
      return isSetEnabled();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof ChannelInfo)
      return this.equals((ChannelInfo)that);
    return false;
  }

  public boolean equals(ChannelInfo that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_UOM = true && this.isSetUOM();
    boolean that_present_UOM = true && that.isSetUOM();
    if (this_present_UOM || that_present_UOM) {
      if (!(this_present_UOM && that_present_UOM))
        return false;
      if (!this.UOM.equals(that.UOM))
        return false;
    }

    boolean this_present_uomModifier = true && this.isSetUomModifier();
    boolean that_present_uomModifier = true && that.isSetUomModifier();
    if (this_present_uomModifier || that_present_uomModifier) {
      if (!(this_present_uomModifier && that_present_uomModifier))
        return false;
      if (!this.uomModifier.equals(that.uomModifier))
        return false;
    }

    boolean this_present_channelNum = true;
    boolean that_present_channelNum = true;
    if (this_present_channelNum || that_present_channelNum) {
      if (!(this_present_channelNum && that_present_channelNum))
        return false;
      if (this.channelNum != that.channelNum)
        return false;
    }

    boolean this_present_enabled = true;
    boolean that_present_enabled = true;
    if (this_present_enabled || that_present_enabled) {
      if (!(this_present_enabled && that_present_enabled))
        return false;
      if (this.enabled != that.enabled)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetUOM()) ? 131071 : 524287);
    if (isSetUOM())
      hashCode = hashCode * 8191 + UOM.hashCode();

    hashCode = hashCode * 8191 + ((isSetUomModifier()) ? 131071 : 524287);
    if (isSetUomModifier())
      hashCode = hashCode * 8191 + uomModifier.hashCode();

    hashCode = hashCode * 8191 + channelNum;

    hashCode = hashCode * 8191 + ((enabled) ? 131071 : 524287);

    return hashCode;
  }

  @Override
  public int compareTo(ChannelInfo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetUOM(), other.isSetUOM());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUOM()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.UOM, other.UOM);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetUomModifier(), other.isSetUomModifier());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUomModifier()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.uomModifier, other.uomModifier);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetChannelNum(), other.isSetChannelNum());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetChannelNum()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.channelNum, other.channelNum);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetEnabled(), other.isSetEnabled());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEnabled()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.enabled, other.enabled);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("ChannelInfo(");
    boolean first = true;

    sb.append("UOM:");
    if (this.UOM == null) {
      sb.append("null");
    } else {
      sb.append(this.UOM);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("uomModifier:");
    if (this.uomModifier == null) {
      sb.append("null");
    } else {
      sb.append(this.uomModifier);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("channelNum:");
    sb.append(this.channelNum);
    first = false;
    if (!first) sb.append(", ");
    sb.append("enabled:");
    sb.append(this.enabled);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (!isSetUOM()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'UOM' is unset! Struct:" + toString());
    }

    if (!isSetUomModifier()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'uomModifier' is unset! Struct:" + toString());
    }

    if (!isSetChannelNum()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'channelNum' is unset! Struct:" + toString());
    }

    if (!isSetEnabled()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'enabled' is unset! Struct:" + toString());
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

  private static class ChannelInfoStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ChannelInfoStandardScheme getScheme() {
      return new ChannelInfoStandardScheme();
    }
  }

  private static class ChannelInfoStandardScheme extends org.apache.thrift.scheme.StandardScheme<ChannelInfo> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ChannelInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // UOM
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.UOM = iprot.readString();
              struct.setUOMIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // UOM_MODIFIER
            if (schemeField.type == org.apache.thrift.protocol.TType.SET) {
              {
                org.apache.thrift.protocol.TSet _set0 = iprot.readSetBegin();
                struct.uomModifier = new java.util.HashSet<java.lang.String>(2*_set0.size);
                @org.apache.thrift.annotation.Nullable java.lang.String _elem1;
                for (int _i2 = 0; _i2 < _set0.size; ++_i2)
                {
                  _elem1 = iprot.readString();
                  struct.uomModifier.add(_elem1);
                }
                iprot.readSetEnd();
              }
              struct.setUomModifierIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // CHANNEL_NUM
            if (schemeField.type == org.apache.thrift.protocol.TType.I16) {
              struct.channelNum = iprot.readI16();
              struct.setChannelNumIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // ENABLED
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.enabled = iprot.readBool();
              struct.setEnabledIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, ChannelInfo struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.UOM != null) {
        oprot.writeFieldBegin(UOM_FIELD_DESC);
        oprot.writeString(struct.UOM);
        oprot.writeFieldEnd();
      }
      if (struct.uomModifier != null) {
        oprot.writeFieldBegin(UOM_MODIFIER_FIELD_DESC);
        {
          oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.STRING, struct.uomModifier.size()));
          for (java.lang.String _iter3 : struct.uomModifier)
          {
            oprot.writeString(_iter3);
          }
          oprot.writeSetEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(CHANNEL_NUM_FIELD_DESC);
      oprot.writeI16(struct.channelNum);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(ENABLED_FIELD_DESC);
      oprot.writeBool(struct.enabled);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ChannelInfoTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ChannelInfoTupleScheme getScheme() {
      return new ChannelInfoTupleScheme();
    }
  }

  private static class ChannelInfoTupleScheme extends org.apache.thrift.scheme.TupleScheme<ChannelInfo> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ChannelInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeString(struct.UOM);
      {
        oprot.writeI32(struct.uomModifier.size());
        for (java.lang.String _iter4 : struct.uomModifier)
        {
          oprot.writeString(_iter4);
        }
      }
      oprot.writeI16(struct.channelNum);
      oprot.writeBool(struct.enabled);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ChannelInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.UOM = iprot.readString();
      struct.setUOMIsSet(true);
      {
        org.apache.thrift.protocol.TSet _set5 = iprot.readSetBegin(org.apache.thrift.protocol.TType.STRING);
        struct.uomModifier = new java.util.HashSet<java.lang.String>(2*_set5.size);
        @org.apache.thrift.annotation.Nullable java.lang.String _elem6;
        for (int _i7 = 0; _i7 < _set5.size; ++_i7)
        {
          _elem6 = iprot.readString();
          struct.uomModifier.add(_elem6);
        }
      }
      struct.setUomModifierIsSet(true);
      struct.channelNum = iprot.readI16();
      struct.setChannelNumIsSet(true);
      struct.enabled = iprot.readBool();
      struct.setEnabledIsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

