/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.cannontech.messaging.serialization.thrift.generated;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LMManualControlResponse implements org.apache.thrift.TBase<LMManualControlResponse, LMManualControlResponse._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("LMManualControlResponse");

  private static final org.apache.thrift.protocol.TField _BASE_MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("_baseMessage", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField _PAO_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("_paoId", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField _CONSTRAINT_VIOLATIONS_FIELD_DESC = new org.apache.thrift.protocol.TField("_constraintViolations", org.apache.thrift.protocol.TType.LIST, (short)3);
  private static final org.apache.thrift.protocol.TField _BEST_FIT_ACTION_FIELD_DESC = new org.apache.thrift.protocol.TField("_bestFitAction", org.apache.thrift.protocol.TType.STRING, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new LMManualControlResponseStandardSchemeFactory());
    schemes.put(TupleScheme.class, new LMManualControlResponseTupleSchemeFactory());
  }

  private com.cannontech.messaging.serialization.thrift.generated.LMMessage _baseMessage; // required
  private int _paoId; // required
  private List<LMConstraintViolation> _constraintViolations; // required
  private String _bestFitAction; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    _BASE_MESSAGE((short)1, "_baseMessage"),
    _PAO_ID((short)2, "_paoId"),
    _CONSTRAINT_VIOLATIONS((short)3, "_constraintViolations"),
    _BEST_FIT_ACTION((short)4, "_bestFitAction");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
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
        case 2: // _PAO_ID
          return _PAO_ID;
        case 3: // _CONSTRAINT_VIOLATIONS
          return _CONSTRAINT_VIOLATIONS;
        case 4: // _BEST_FIT_ACTION
          return _BEST_FIT_ACTION;
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
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int ___PAOID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields._BASE_MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("_baseMessage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, com.cannontech.messaging.serialization.thrift.generated.LMMessage.class)));
    tmpMap.put(_Fields._PAO_ID, new org.apache.thrift.meta_data.FieldMetaData("_paoId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields._CONSTRAINT_VIOLATIONS, new org.apache.thrift.meta_data.FieldMetaData("_constraintViolations", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, LMConstraintViolation.class))));
    tmpMap.put(_Fields._BEST_FIT_ACTION, new org.apache.thrift.meta_data.FieldMetaData("_bestFitAction", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(LMManualControlResponse.class, metaDataMap);
  }

  public LMManualControlResponse() {
  }

  public LMManualControlResponse(
    com.cannontech.messaging.serialization.thrift.generated.LMMessage _baseMessage,
    int _paoId,
    List<LMConstraintViolation> _constraintViolations,
    String _bestFitAction)
  {
    this();
    this._baseMessage = _baseMessage;
    this._paoId = _paoId;
    set_paoIdIsSet(true);
    this._constraintViolations = _constraintViolations;
    this._bestFitAction = _bestFitAction;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public LMManualControlResponse(LMManualControlResponse other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSet_baseMessage()) {
      this._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.LMMessage(other._baseMessage);
    }
    this._paoId = other._paoId;
    if (other.isSet_constraintViolations()) {
      List<LMConstraintViolation> __this___constraintViolations = new ArrayList<LMConstraintViolation>();
      for (LMConstraintViolation other_element : other._constraintViolations) {
        __this___constraintViolations.add(new LMConstraintViolation(other_element));
      }
      this._constraintViolations = __this___constraintViolations;
    }
    if (other.isSet_bestFitAction()) {
      this._bestFitAction = other._bestFitAction;
    }
  }

  public LMManualControlResponse deepCopy() {
    return new LMManualControlResponse(this);
  }

  @Override
  public void clear() {
    this._baseMessage = null;
    set_paoIdIsSet(false);
    this._paoId = 0;
    this._constraintViolations = null;
    this._bestFitAction = null;
  }

  public com.cannontech.messaging.serialization.thrift.generated.LMMessage get_baseMessage() {
    return this._baseMessage;
  }

  public void set_baseMessage(com.cannontech.messaging.serialization.thrift.generated.LMMessage _baseMessage) {
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

  public int get_paoId() {
    return this._paoId;
  }

  public void set_paoId(int _paoId) {
    this._paoId = _paoId;
    set_paoIdIsSet(true);
  }

  public void unset_paoId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, ___PAOID_ISSET_ID);
  }

  /** Returns true if field _paoId is set (has been assigned a value) and false otherwise */
  public boolean isSet_paoId() {
    return EncodingUtils.testBit(__isset_bitfield, ___PAOID_ISSET_ID);
  }

  public void set_paoIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, ___PAOID_ISSET_ID, value);
  }

  public int get_constraintViolationsSize() {
    return (this._constraintViolations == null) ? 0 : this._constraintViolations.size();
  }

  public java.util.Iterator<LMConstraintViolation> get_constraintViolationsIterator() {
    return (this._constraintViolations == null) ? null : this._constraintViolations.iterator();
  }

  public void addTo_constraintViolations(LMConstraintViolation elem) {
    if (this._constraintViolations == null) {
      this._constraintViolations = new ArrayList<LMConstraintViolation>();
    }
    this._constraintViolations.add(elem);
  }

  public List<LMConstraintViolation> get_constraintViolations() {
    return this._constraintViolations;
  }

  public void set_constraintViolations(List<LMConstraintViolation> _constraintViolations) {
    this._constraintViolations = _constraintViolations;
  }

  public void unset_constraintViolations() {
    this._constraintViolations = null;
  }

  /** Returns true if field _constraintViolations is set (has been assigned a value) and false otherwise */
  public boolean isSet_constraintViolations() {
    return this._constraintViolations != null;
  }

  public void set_constraintViolationsIsSet(boolean value) {
    if (!value) {
      this._constraintViolations = null;
    }
  }

  public String get_bestFitAction() {
    return this._bestFitAction;
  }

  public void set_bestFitAction(String _bestFitAction) {
    this._bestFitAction = _bestFitAction;
  }

  public void unset_bestFitAction() {
    this._bestFitAction = null;
  }

  /** Returns true if field _bestFitAction is set (has been assigned a value) and false otherwise */
  public boolean isSet_bestFitAction() {
    return this._bestFitAction != null;
  }

  public void set_bestFitActionIsSet(boolean value) {
    if (!value) {
      this._bestFitAction = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case _BASE_MESSAGE:
      if (value == null) {
        unset_baseMessage();
      } else {
        set_baseMessage((com.cannontech.messaging.serialization.thrift.generated.LMMessage)value);
      }
      break;

    case _PAO_ID:
      if (value == null) {
        unset_paoId();
      } else {
        set_paoId((Integer)value);
      }
      break;

    case _CONSTRAINT_VIOLATIONS:
      if (value == null) {
        unset_constraintViolations();
      } else {
        set_constraintViolations((List<LMConstraintViolation>)value);
      }
      break;

    case _BEST_FIT_ACTION:
      if (value == null) {
        unset_bestFitAction();
      } else {
        set_bestFitAction((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case _BASE_MESSAGE:
      return get_baseMessage();

    case _PAO_ID:
      return Integer.valueOf(get_paoId());

    case _CONSTRAINT_VIOLATIONS:
      return get_constraintViolations();

    case _BEST_FIT_ACTION:
      return get_bestFitAction();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case _BASE_MESSAGE:
      return isSet_baseMessage();
    case _PAO_ID:
      return isSet_paoId();
    case _CONSTRAINT_VIOLATIONS:
      return isSet_constraintViolations();
    case _BEST_FIT_ACTION:
      return isSet_bestFitAction();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof LMManualControlResponse)
      return this.equals((LMManualControlResponse)that);
    return false;
  }

  public boolean equals(LMManualControlResponse that) {
    if (that == null)
      return false;

    boolean this_present__baseMessage = true && this.isSet_baseMessage();
    boolean that_present__baseMessage = true && that.isSet_baseMessage();
    if (this_present__baseMessage || that_present__baseMessage) {
      if (!(this_present__baseMessage && that_present__baseMessage))
        return false;
      if (!this._baseMessage.equals(that._baseMessage))
        return false;
    }

    boolean this_present__paoId = true;
    boolean that_present__paoId = true;
    if (this_present__paoId || that_present__paoId) {
      if (!(this_present__paoId && that_present__paoId))
        return false;
      if (this._paoId != that._paoId)
        return false;
    }

    boolean this_present__constraintViolations = true && this.isSet_constraintViolations();
    boolean that_present__constraintViolations = true && that.isSet_constraintViolations();
    if (this_present__constraintViolations || that_present__constraintViolations) {
      if (!(this_present__constraintViolations && that_present__constraintViolations))
        return false;
      if (!this._constraintViolations.equals(that._constraintViolations))
        return false;
    }

    boolean this_present__bestFitAction = true && this.isSet_bestFitAction();
    boolean that_present__bestFitAction = true && that.isSet_bestFitAction();
    if (this_present__bestFitAction || that_present__bestFitAction) {
      if (!(this_present__bestFitAction && that_present__bestFitAction))
        return false;
      if (!this._bestFitAction.equals(that._bestFitAction))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(LMManualControlResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    LMManualControlResponse typedOther = (LMManualControlResponse)other;

    lastComparison = Boolean.valueOf(isSet_baseMessage()).compareTo(typedOther.isSet_baseMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_baseMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._baseMessage, typedOther._baseMessage);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSet_paoId()).compareTo(typedOther.isSet_paoId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_paoId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._paoId, typedOther._paoId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSet_constraintViolations()).compareTo(typedOther.isSet_constraintViolations());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_constraintViolations()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._constraintViolations, typedOther._constraintViolations);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSet_bestFitAction()).compareTo(typedOther.isSet_bestFitAction());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSet_bestFitAction()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this._bestFitAction, typedOther._bestFitAction);
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
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("LMManualControlResponse(");
    boolean first = true;

    sb.append("_baseMessage:");
    if (this._baseMessage == null) {
      sb.append("null");
    } else {
      sb.append(this._baseMessage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_paoId:");
    sb.append(this._paoId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("_constraintViolations:");
    if (this._constraintViolations == null) {
      sb.append("null");
    } else {
      sb.append(this._constraintViolations);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("_bestFitAction:");
    if (this._bestFitAction == null) {
      sb.append("null");
    } else {
      sb.append(this._bestFitAction);
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

    if (!isSet_paoId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_paoId' is unset! Struct:" + toString());
    }

    if (!isSet_constraintViolations()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_constraintViolations' is unset! Struct:" + toString());
    }

    if (!isSet_bestFitAction()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field '_bestFitAction' is unset! Struct:" + toString());
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

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class LMManualControlResponseStandardSchemeFactory implements SchemeFactory {
    public LMManualControlResponseStandardScheme getScheme() {
      return new LMManualControlResponseStandardScheme();
    }
  }

  private static class LMManualControlResponseStandardScheme extends StandardScheme<LMManualControlResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, LMManualControlResponse struct) throws org.apache.thrift.TException {
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
              struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.LMMessage();
              struct._baseMessage.read(iprot);
              struct.set_baseMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // _PAO_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct._paoId = iprot.readI32();
              struct.set_paoIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // _CONSTRAINT_VIOLATIONS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list32 = iprot.readListBegin();
                struct._constraintViolations = new ArrayList<LMConstraintViolation>(_list32.size);
                for (int _i33 = 0; _i33 < _list32.size; ++_i33)
                {
                  LMConstraintViolation _elem34; // required
                  _elem34 = new LMConstraintViolation();
                  _elem34.read(iprot);
                  struct._constraintViolations.add(_elem34);
                }
                iprot.readListEnd();
              }
              struct.set_constraintViolationsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // _BEST_FIT_ACTION
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct._bestFitAction = iprot.readString();
              struct.set_bestFitActionIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, LMManualControlResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct._baseMessage != null) {
        oprot.writeFieldBegin(_BASE_MESSAGE_FIELD_DESC);
        struct._baseMessage.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(_PAO_ID_FIELD_DESC);
      oprot.writeI32(struct._paoId);
      oprot.writeFieldEnd();
      if (struct._constraintViolations != null) {
        oprot.writeFieldBegin(_CONSTRAINT_VIOLATIONS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct._constraintViolations.size()));
          for (LMConstraintViolation _iter35 : struct._constraintViolations)
          {
            _iter35.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct._bestFitAction != null) {
        oprot.writeFieldBegin(_BEST_FIT_ACTION_FIELD_DESC);
        oprot.writeString(struct._bestFitAction);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class LMManualControlResponseTupleSchemeFactory implements SchemeFactory {
    public LMManualControlResponseTupleScheme getScheme() {
      return new LMManualControlResponseTupleScheme();
    }
  }

  private static class LMManualControlResponseTupleScheme extends TupleScheme<LMManualControlResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, LMManualControlResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      struct._baseMessage.write(oprot);
      oprot.writeI32(struct._paoId);
      {
        oprot.writeI32(struct._constraintViolations.size());
        for (LMConstraintViolation _iter36 : struct._constraintViolations)
        {
          _iter36.write(oprot);
        }
      }
      oprot.writeString(struct._bestFitAction);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, LMManualControlResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct._baseMessage = new com.cannontech.messaging.serialization.thrift.generated.LMMessage();
      struct._baseMessage.read(iprot);
      struct.set_baseMessageIsSet(true);
      struct._paoId = iprot.readI32();
      struct.set_paoIdIsSet(true);
      {
        org.apache.thrift.protocol.TList _list37 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct._constraintViolations = new ArrayList<LMConstraintViolation>(_list37.size);
        for (int _i38 = 0; _i38 < _list37.size; ++_i38)
        {
          LMConstraintViolation _elem39; // required
          _elem39 = new LMConstraintViolation();
          _elem39.read(iprot);
          struct._constraintViolations.add(_elem39);
        }
      }
      struct.set_constraintViolationsIsSet(true);
      struct._bestFitAction = iprot.readString();
      struct.set_bestFitActionIsSet(true);
    }
  }

}

