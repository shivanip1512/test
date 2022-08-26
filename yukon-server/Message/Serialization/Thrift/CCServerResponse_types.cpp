/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCServerResponse_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


CCServerResponse::~CCServerResponse() noexcept {
}


void CCServerResponse::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}

void CCServerResponse::__set__messageId(const int32_t val) {
  this->_messageId = val;
}

void CCServerResponse::__set__responseType(const int32_t val) {
  this->_responseType = val;
}

void CCServerResponse::__set__response(const std::string& val) {
  this->_response = val;
}
std::ostream& operator<<(std::ostream& out, const CCServerResponse& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCServerResponse::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__messageId = false;
  bool isset__responseType = false;
  bool isset__response = false;

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
          xfer += iprot->readI32(this->_messageId);
          isset__messageId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_responseType);
          isset__responseType = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_response);
          isset__response = true;
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
  if (!isset__messageId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__responseType)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__response)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCServerResponse::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCServerResponse");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_messageId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_messageId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_responseType", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_responseType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_response", ::apache::thrift::protocol::T_STRING, 4);
  xfer += oprot->writeString(this->_response);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCServerResponse &a, CCServerResponse &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._messageId, b._messageId);
  swap(a._responseType, b._responseType);
  swap(a._response, b._response);
}

CCServerResponse::CCServerResponse(const CCServerResponse& other0) {
  _baseMessage = other0._baseMessage;
  _messageId = other0._messageId;
  _responseType = other0._responseType;
  _response = other0._response;
}
CCServerResponse& CCServerResponse::operator=(const CCServerResponse& other1) {
  _baseMessage = other1._baseMessage;
  _messageId = other1._messageId;
  _responseType = other1._responseType;
  _response = other1._response;
  return *this;
}
void CCServerResponse::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCServerResponse(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_messageId=" << to_string(_messageId);
  out << ", " << "_responseType=" << to_string(_responseType);
  out << ", " << "_response=" << to_string(_response);
  out << ")";
}

}}}} // namespace
