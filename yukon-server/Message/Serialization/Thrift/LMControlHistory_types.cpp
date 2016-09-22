/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "LMControlHistory_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* LMControlHistory::ascii_fingerprint = "C89A23896D05209BC3AC57BEAD75047E";
const uint8_t LMControlHistory::binary_fingerprint[16] = {0xC8,0x9A,0x23,0x89,0x6D,0x05,0x20,0x9B,0xC3,0xAC,0x57,0xBE,0xAD,0x75,0x04,0x7E};

uint32_t LMControlHistory::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__paoId = false;
  bool isset__pointId = false;
  bool isset__rawState = false;
  bool isset__startDateTime = false;
  bool isset__controlDuration = false;
  bool isset__reductionRatio = false;
  bool isset__controlType = false;
  bool isset__activeRestore = false;
  bool isset__reductionValue = false;
  bool isset__controlPriority = false;
  bool isset__associationKey = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_STRUCT) {
          xfer += this->_baseMessage.read(iprot);
          isset__baseMessage = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_paoId);
          isset__paoId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_pointId);
          isset__pointId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_rawState);
          isset__rawState = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_startDateTime);
          isset__startDateTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_controlDuration);
          isset__controlDuration = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_reductionRatio);
          isset__reductionRatio = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_controlType);
          isset__controlType = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 9:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_activeRestore);
          isset__activeRestore = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 10:
        if (ftype == ::apache::thrift::protocol::T_DOUBLE) {
          xfer += iprot->readDouble(this->_reductionValue);
          isset__reductionValue = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 11:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_controlPriority);
          isset__controlPriority = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 12:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_associationKey);
          isset__associationKey = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      default:
        xfer += iprot->skip(ftype);
        break;
    }
    xfer += iprot->readFieldEnd();
  }

  xfer += iprot->readStructEnd();

  if (!isset__baseMessage)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__paoId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__pointId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__rawState)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__startDateTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__controlDuration)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__reductionRatio)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__controlType)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__activeRestore)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__reductionValue)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__controlPriority)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__associationKey)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t LMControlHistory::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("LMControlHistory");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_paoId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_paoId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_pointId", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_pointId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_rawState", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->_rawState);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_startDateTime", ::apache::thrift::protocol::T_I64, 5);
  xfer += oprot->writeI64(this->_startDateTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_controlDuration", ::apache::thrift::protocol::T_I32, 6);
  xfer += oprot->writeI32(this->_controlDuration);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_reductionRatio", ::apache::thrift::protocol::T_I32, 7);
  xfer += oprot->writeI32(this->_reductionRatio);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_controlType", ::apache::thrift::protocol::T_STRING, 8);
  xfer += oprot->writeString(this->_controlType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_activeRestore", ::apache::thrift::protocol::T_STRING, 9);
  xfer += oprot->writeString(this->_activeRestore);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_reductionValue", ::apache::thrift::protocol::T_DOUBLE, 10);
  xfer += oprot->writeDouble(this->_reductionValue);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_controlPriority", ::apache::thrift::protocol::T_I32, 11);
  xfer += oprot->writeI32(this->_controlPriority);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_associationKey", ::apache::thrift::protocol::T_I32, 12);
  xfer += oprot->writeI32(this->_associationKey);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(LMControlHistory &a, LMControlHistory &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._paoId, b._paoId);
  swap(a._pointId, b._pointId);
  swap(a._rawState, b._rawState);
  swap(a._startDateTime, b._startDateTime);
  swap(a._controlDuration, b._controlDuration);
  swap(a._reductionRatio, b._reductionRatio);
  swap(a._controlType, b._controlType);
  swap(a._activeRestore, b._activeRestore);
  swap(a._reductionValue, b._reductionValue);
  swap(a._controlPriority, b._controlPriority);
  swap(a._associationKey, b._associationKey);
}

}}}} // namespace
