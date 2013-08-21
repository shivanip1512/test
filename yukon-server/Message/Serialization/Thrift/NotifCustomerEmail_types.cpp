/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "NotifCustomerEmail_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* NotifCustomerEmail::ascii_fingerprint = "9628938E92DC76069D2A9977D3E3E9D1";
const uint8_t NotifCustomerEmail::binary_fingerprint[16] = {0x96,0x28,0x93,0x8E,0x92,0xDC,0x76,0x06,0x9D,0x2A,0x99,0x77,0xD3,0xE3,0xE9,0xD1};

uint32_t NotifCustomerEmail::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__to = false;
  bool isset__customerId = false;
  bool isset__subject = false;
  bool isset__body = false;
  bool isset__toCc = false;
  bool isset__toBcc = false;

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
          xfer += iprot->readString(this->_to);
          isset__to = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_customerId);
          isset__customerId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_subject);
          isset__subject = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_body);
          isset__body = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_toCc);
          isset__toCc = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_toBcc);
          isset__toBcc = true;
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
  if (!isset__to)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__customerId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__subject)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__body)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__toCc)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__toBcc)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t NotifCustomerEmail::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("NotifCustomerEmail");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_to", ::apache::thrift::protocol::T_STRING, 2);
  xfer += oprot->writeString(this->_to);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_customerId", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_customerId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_subject", ::apache::thrift::protocol::T_STRING, 4);
  xfer += oprot->writeString(this->_subject);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_body", ::apache::thrift::protocol::T_STRING, 5);
  xfer += oprot->writeString(this->_body);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_toCc", ::apache::thrift::protocol::T_STRING, 6);
  xfer += oprot->writeString(this->_toCc);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_toBcc", ::apache::thrift::protocol::T_STRING, 7);
  xfer += oprot->writeString(this->_toBcc);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(NotifCustomerEmail &a, NotifCustomerEmail &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._to, b._to);
  swap(a._customerId, b._customerId);
  swap(a._subject, b._subject);
  swap(a._body, b._body);
  swap(a._toCc, b._toCc);
  swap(a._toBcc, b._toBcc);
}

}}}} // namespace
