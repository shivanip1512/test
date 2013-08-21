/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "Message_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* Message::ascii_fingerprint = "8F34A2AE943957281399A0CEAFC79E43";
const uint8_t Message::binary_fingerprint[16] = {0x8F,0x34,0xA2,0xAE,0x94,0x39,0x57,0x28,0x13,0x99,0xA0,0xCE,0xAF,0xC7,0x9E,0x43};

uint32_t Message::read(::apache::thrift::protocol::TProtocol* iprot) {

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
  bool isset__token = false;
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
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_token);
          isset__token = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
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
  if (!isset__token)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__src)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t Message::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
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

  xfer += oprot->writeFieldBegin("_token", ::apache::thrift::protocol::T_I32, 5);
  xfer += oprot->writeI32(this->_token);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_src", ::apache::thrift::protocol::T_STRING, 6);
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
  swap(a._token, b._token);
  swap(a._src, b._src);
}

const char* GenericMessage::ascii_fingerprint = "07A9615F837F7D0A952B595DD3020972";
const uint8_t GenericMessage::binary_fingerprint[16] = {0x07,0xA9,0x61,0x5F,0x83,0x7F,0x7D,0x0A,0x95,0x2B,0x59,0x5D,0xD3,0x02,0x09,0x72};

uint32_t GenericMessage::read(::apache::thrift::protocol::TProtocol* iprot) {

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

}}}} // namespace
