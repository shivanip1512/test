/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "MCOverrideRequest_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* MCOverrideRequest::ascii_fingerprint = "5BF8899B42170C0C9A1D3A4B4D1AAE0A";
const uint8_t MCOverrideRequest::binary_fingerprint[16] = {0x5B,0xF8,0x89,0x9B,0x42,0x17,0x0C,0x0C,0x9A,0x1D,0x3A,0x4B,0x4D,0x1A,0xAE,0x0A};

uint32_t MCOverrideRequest::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__action = false;
  bool isset__id = false;
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
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_action);
          isset__action = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_id);
          isset__id = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_startTime);
          isset__startTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
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
  if (!isset__action)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__id)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__startTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__stopTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t MCOverrideRequest::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("MCOverrideRequest");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_action", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_action);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_id", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_id);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_startTime", ::apache::thrift::protocol::T_I64, 4);
  xfer += oprot->writeI64(this->_startTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_stopTime", ::apache::thrift::protocol::T_I64, 5);
  xfer += oprot->writeI64(this->_stopTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(MCOverrideRequest &a, MCOverrideRequest &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._action, b._action);
  swap(a._id, b._id);
  swap(a._startTime, b._startTime);
  swap(a._stopTime, b._stopTime);
}

}}}} // namespace
