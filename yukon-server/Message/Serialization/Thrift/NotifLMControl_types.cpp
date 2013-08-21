/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "NotifLMControl_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* NotifLMControl::ascii_fingerprint = "45C547D2A9BD1CBDB5D71FB97BFEA5D9";
const uint8_t NotifLMControl::binary_fingerprint[16] = {0x45,0xC5,0x47,0xD2,0xA9,0xBD,0x1C,0xBD,0xB5,0xD7,0x1F,0xB9,0x7B,0xFE,0xA5,0xD9};

uint32_t NotifLMControl::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__notifGroupIds = false;
  bool isset__notifType = false;
  bool isset__programId = false;
  bool isset__startTime = false;
  bool isset__stopTime = false;

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
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_notifGroupIds.clear();
            uint32_t _size0;
            ::apache::thrift::protocol::TType _etype3;
            xfer += iprot->readListBegin(_etype3, _size0);
            this->_notifGroupIds.resize(_size0);
            uint32_t _i4;
            for (_i4 = 0; _i4 < _size0; ++_i4)
            {
              xfer += iprot->readI32(this->_notifGroupIds[_i4]);
            }
            xfer += iprot->readListEnd();
          }
          isset__notifGroupIds = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_notifType);
          isset__notifType = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_programId);
          isset__programId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_startTime);
          isset__startTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_stopTime);
          isset__stopTime = true;
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
  if (!isset__notifGroupIds)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__notifType)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__programId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__startTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__stopTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t NotifLMControl::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("NotifLMControl");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_notifGroupIds", ::apache::thrift::protocol::T_LIST, 2);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_I32, static_cast<uint32_t>(this->_notifGroupIds.size()));
    std::vector<int32_t> ::const_iterator _iter5;
    for (_iter5 = this->_notifGroupIds.begin(); _iter5 != this->_notifGroupIds.end(); ++_iter5)
    {
      xfer += oprot->writeI32((*_iter5));
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_notifType", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_notifType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_programId", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->_programId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_startTime", ::apache::thrift::protocol::T_I64, 5);
  xfer += oprot->writeI64(this->_startTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_stopTime", ::apache::thrift::protocol::T_I64, 6);
  xfer += oprot->writeI64(this->_stopTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(NotifLMControl &a, NotifLMControl &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._notifGroupIds, b._notifGroupIds);
  swap(a._notifType, b._notifType);
  swap(a._programId, b._programId);
  swap(a._startTime, b._startTime);
  swap(a._stopTime, b._stopTime);
}

}}}} // namespace
