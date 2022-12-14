/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "ServerRequest_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


ServerRequest::~ServerRequest() noexcept {
}


void ServerRequest::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}

void ServerRequest::__set__id(const int32_t val) {
  this->_id = val;
}

void ServerRequest::__set__payload(const  ::Cti::Messaging::Serialization::Thrift::GenericMessage& val) {
  this->_payload = val;
}
std::ostream& operator<<(std::ostream& out, const ServerRequest& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t ServerRequest::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__id = false;
  bool isset__payload = false;

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
          xfer += iprot->readI32(this->_id);
          isset__id = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRUCT) {
          xfer += this->_payload.read(iprot);
          isset__payload = true;
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
  if (!isset__id)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__payload)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t ServerRequest::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("ServerRequest");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_id", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_id);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_payload", ::apache::thrift::protocol::T_STRUCT, 3);
  xfer += this->_payload.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(ServerRequest &a, ServerRequest &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._id, b._id);
  swap(a._payload, b._payload);
}

ServerRequest::ServerRequest(const ServerRequest& other0) {
  _baseMessage = other0._baseMessage;
  _id = other0._id;
  _payload = other0._payload;
}
ServerRequest& ServerRequest::operator=(const ServerRequest& other1) {
  _baseMessage = other1._baseMessage;
  _id = other1._id;
  _payload = other1._payload;
  return *this;
}
void ServerRequest::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "ServerRequest(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_id=" << to_string(_id);
  out << ", " << "_payload=" << to_string(_payload);
  out << ")";
}

}}}} // namespace
