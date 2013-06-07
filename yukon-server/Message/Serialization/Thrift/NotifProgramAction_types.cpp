/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "NotifProgramAction_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* NotifProgramAction::ascii_fingerprint = "864ED778980289D8985AA311617B6A39";
const uint8_t NotifProgramAction::binary_fingerprint[16] = {0x86,0x4E,0xD7,0x78,0x98,0x02,0x89,0xD8,0x98,0x5A,0xA3,0x11,0x61,0x7B,0x6A,0x39};

uint32_t NotifProgramAction::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__programId = false;
  bool isset__eventDisplayName = false;
  bool isset__action = false;
  bool isset__startTime = false;
  bool isset__stopTime = false;
  bool isset__notificationTime = false;
  bool isset__customerIds = false;

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
          xfer += iprot->readI32(this->_programId);
          isset__programId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_eventDisplayName);
          isset__eventDisplayName = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_action);
          isset__action = true;
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
      case 7:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_notificationTime);
          isset__notificationTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_customerIds.clear();
            uint32_t _size0;
            ::apache::thrift::protocol::TType _etype3;
            xfer += iprot->readListBegin(_etype3, _size0);
            this->_customerIds.resize(_size0);
            uint32_t _i4;
            for (_i4 = 0; _i4 < _size0; ++_i4)
            {
              xfer += iprot->readI32(this->_customerIds[_i4]);
            }
            xfer += iprot->readListEnd();
          }
          isset__customerIds = true;
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
  if (!isset__programId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__eventDisplayName)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__action)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__startTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__stopTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__notificationTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__customerIds)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t NotifProgramAction::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("NotifProgramAction");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_programId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_programId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_eventDisplayName", ::apache::thrift::protocol::T_STRING, 3);
  xfer += oprot->writeString(this->_eventDisplayName);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_action", ::apache::thrift::protocol::T_STRING, 4);
  xfer += oprot->writeString(this->_action);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_startTime", ::apache::thrift::protocol::T_I64, 5);
  xfer += oprot->writeI64(this->_startTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_stopTime", ::apache::thrift::protocol::T_I64, 6);
  xfer += oprot->writeI64(this->_stopTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_notificationTime", ::apache::thrift::protocol::T_I64, 7);
  xfer += oprot->writeI64(this->_notificationTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_customerIds", ::apache::thrift::protocol::T_LIST, 8);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_I32, static_cast<uint32_t>(this->_customerIds.size()));
    std::vector<int32_t> ::const_iterator _iter5;
    for (_iter5 = this->_customerIds.begin(); _iter5 != this->_customerIds.end(); ++_iter5)
    {
      xfer += oprot->writeI32((*_iter5));
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(NotifProgramAction &a, NotifProgramAction &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._programId, b._programId);
  swap(a._eventDisplayName, b._eventDisplayName);
  swap(a._action, b._action);
  swap(a._startTime, b._startTime);
  swap(a._stopTime, b._stopTime);
  swap(a._notificationTime, b._notificationTime);
  swap(a._customerIds, b._customerIds);
}

}}}} // namespace
