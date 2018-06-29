/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "MCOverrideRequest_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


MCOverrideRequest::~MCOverrideRequest() throw() {
}


void MCOverrideRequest::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}

void MCOverrideRequest::__set__action(const int32_t val) {
  this->_action = val;
}

void MCOverrideRequest::__set__id(const int32_t val) {
  this->_id = val;
}

void MCOverrideRequest::__set__startTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_startTime = val;
}

void MCOverrideRequest::__set__stopTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_stopTime = val;
}
std::ostream& operator<<(std::ostream& out, const MCOverrideRequest& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t MCOverrideRequest::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
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
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
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

MCOverrideRequest::MCOverrideRequest(const MCOverrideRequest& other0) {
  _baseMessage = other0._baseMessage;
  _action = other0._action;
  _id = other0._id;
  _startTime = other0._startTime;
  _stopTime = other0._stopTime;
}
MCOverrideRequest& MCOverrideRequest::operator=(const MCOverrideRequest& other1) {
  _baseMessage = other1._baseMessage;
  _action = other1._action;
  _id = other1._id;
  _startTime = other1._startTime;
  _stopTime = other1._stopTime;
  return *this;
}
void MCOverrideRequest::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "MCOverrideRequest(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_action=" << to_string(_action);
  out << ", " << "_id=" << to_string(_id);
  out << ", " << "_startTime=" << to_string(_startTime);
  out << ", " << "_stopTime=" << to_string(_stopTime);
  out << ")";
}

}}}} // namespace
