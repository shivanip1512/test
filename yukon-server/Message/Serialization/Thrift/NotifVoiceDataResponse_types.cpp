/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "NotifVoiceDataResponse_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


NotifVoiceDataResponse::~NotifVoiceDataResponse() noexcept {
}


void NotifVoiceDataResponse::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}

void NotifVoiceDataResponse::__set__callToken(const std::string& val) {
  this->_callToken = val;
}

void NotifVoiceDataResponse::__set__xmlData(const std::string& val) {
  this->_xmlData = val;
}

void NotifVoiceDataResponse::__set__contactId(const int32_t val) {
  this->_contactId = val;
}
std::ostream& operator<<(std::ostream& out, const NotifVoiceDataResponse& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t NotifVoiceDataResponse::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__callToken = false;
  bool isset__xmlData = false;
  bool isset__contactId = false;

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
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_callToken);
          isset__callToken = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_xmlData);
          isset__xmlData = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_contactId);
          isset__contactId = true;
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
  if (!isset__callToken)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__xmlData)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__contactId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t NotifVoiceDataResponse::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("NotifVoiceDataResponse");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_callToken", ::apache::thrift::protocol::T_STRING, 2);
  xfer += oprot->writeString(this->_callToken);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_xmlData", ::apache::thrift::protocol::T_STRING, 3);
  xfer += oprot->writeString(this->_xmlData);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_contactId", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->_contactId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(NotifVoiceDataResponse &a, NotifVoiceDataResponse &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._callToken, b._callToken);
  swap(a._xmlData, b._xmlData);
  swap(a._contactId, b._contactId);
}

NotifVoiceDataResponse::NotifVoiceDataResponse(const NotifVoiceDataResponse& other0) {
  _baseMessage = other0._baseMessage;
  _callToken = other0._callToken;
  _xmlData = other0._xmlData;
  _contactId = other0._contactId;
}
NotifVoiceDataResponse& NotifVoiceDataResponse::operator=(const NotifVoiceDataResponse& other1) {
  _baseMessage = other1._baseMessage;
  _callToken = other1._callToken;
  _xmlData = other1._xmlData;
  _contactId = other1._contactId;
  return *this;
}
void NotifVoiceDataResponse::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "NotifVoiceDataResponse(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_callToken=" << to_string(_callToken);
  out << ", " << "_xmlData=" << to_string(_xmlData);
  out << ", " << "_contactId=" << to_string(_contactId);
  out << ")";
}

}}}} // namespace
