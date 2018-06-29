/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "Message_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


Message::~Message() throw() {
}


void Message::__set__messageTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_messageTime = val;
}

void Message::__set__messagePriority(const int32_t val) {
  this->_messagePriority = val;
}

void Message::__set__soe(const int32_t val) {
  this->_soe = val;
}

void Message::__set__usr(const std::string& val) {
  this->_usr = val;
}

void Message::__set__src(const std::string& val) {
  this->_src = val;
}
std::ostream& operator<<(std::ostream& out, const Message& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t Message::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__messageTime = false;
  bool isset__messagePriority = false;
  bool isset__soe = false;
  bool isset__usr = false;
  bool isset__src = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_messageTime);
          isset__messageTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_messagePriority);
          isset__messagePriority = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_soe);
          isset__soe = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_usr);
          isset__usr = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_src);
          isset__src = true;
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

  if (!isset__messageTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__messagePriority)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__soe)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__usr)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__src)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t Message::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("Message");

  xfer += oprot->writeFieldBegin("_messageTime", ::apache::thrift::protocol::T_I64, 1);
  xfer += oprot->writeI64(this->_messageTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_messagePriority", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_messagePriority);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_soe", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_soe);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_usr", ::apache::thrift::protocol::T_STRING, 4);
  xfer += oprot->writeString(this->_usr);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_src", ::apache::thrift::protocol::T_STRING, 5);
  xfer += oprot->writeString(this->_src);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(Message &a, Message &b) {
  using ::std::swap;
  swap(a._messageTime, b._messageTime);
  swap(a._messagePriority, b._messagePriority);
  swap(a._soe, b._soe);
  swap(a._usr, b._usr);
  swap(a._src, b._src);
}

Message::Message(const Message& other0) {
  _messageTime = other0._messageTime;
  _messagePriority = other0._messagePriority;
  _soe = other0._soe;
  _usr = other0._usr;
  _src = other0._src;
}
Message& Message::operator=(const Message& other1) {
  _messageTime = other1._messageTime;
  _messagePriority = other1._messagePriority;
  _soe = other1._soe;
  _usr = other1._usr;
  _src = other1._src;
  return *this;
}
void Message::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "Message(";
  out << "_messageTime=" << to_string(_messageTime);
  out << ", " << "_messagePriority=" << to_string(_messagePriority);
  out << ", " << "_soe=" << to_string(_soe);
  out << ", " << "_usr=" << to_string(_usr);
  out << ", " << "_src=" << to_string(_src);
  out << ")";
}


GenericMessage::~GenericMessage() throw() {
}


void GenericMessage::__set__messageType(const std::string& val) {
  this->_messageType = val;
}

void GenericMessage::__set__payload(const std::string& val) {
  this->_payload = val;
}
std::ostream& operator<<(std::ostream& out, const GenericMessage& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t GenericMessage::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__messageType = false;
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
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_messageType);
          isset__messageType = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readBinary(this->_payload);
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

  if (!isset__messageType)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__payload)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t GenericMessage::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("GenericMessage");

  xfer += oprot->writeFieldBegin("_messageType", ::apache::thrift::protocol::T_STRING, 1);
  xfer += oprot->writeString(this->_messageType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_payload", ::apache::thrift::protocol::T_STRING, 2);
  xfer += oprot->writeBinary(this->_payload);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(GenericMessage &a, GenericMessage &b) {
  using ::std::swap;
  swap(a._messageType, b._messageType);
  swap(a._payload, b._payload);
}

GenericMessage::GenericMessage(const GenericMessage& other2) {
  _messageType = other2._messageType;
  _payload = other2._payload;
}
GenericMessage& GenericMessage::operator=(const GenericMessage& other3) {
  _messageType = other3._messageType;
  _payload = other3._payload;
  return *this;
}
void GenericMessage::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "GenericMessage(";
  out << "_messageType=" << to_string(_messageType);
  out << ", " << "_payload=" << to_string(_payload);
  out << ")";
}

}}}} // namespace
