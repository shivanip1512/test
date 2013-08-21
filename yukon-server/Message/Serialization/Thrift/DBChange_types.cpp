/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "DBChange_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* DBChange::ascii_fingerprint = "EE16CE714C42928E2F24F5932BCB1AAA";
const uint8_t DBChange::binary_fingerprint[16] = {0xEE,0x16,0xCE,0x71,0x4C,0x42,0x92,0x8E,0x2F,0x24,0xF5,0x93,0x2B,0xCB,0x1A,0xAA};

uint32_t DBChange::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__id = false;
  bool isset__database = false;
  bool isset__category = false;
  bool isset__objecttype = false;
  bool isset__typeofchange = false;

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
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_database);
          isset__database = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_category);
          isset__category = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_objecttype);
          isset__objecttype = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_typeofchange);
          isset__typeofchange = true;
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
  if (!isset__database)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__category)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__objecttype)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__typeofchange)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t DBChange::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("DBChange");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_id", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_id);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_database", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_database);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_category", ::apache::thrift::protocol::T_STRING, 4);
  xfer += oprot->writeString(this->_category);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_objecttype", ::apache::thrift::protocol::T_STRING, 5);
  xfer += oprot->writeString(this->_objecttype);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_typeofchange", ::apache::thrift::protocol::T_I32, 6);
  xfer += oprot->writeI32(this->_typeofchange);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(DBChange &a, DBChange &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._id, b._id);
  swap(a._database, b._database);
  swap(a._category, b._category);
  swap(a._objecttype, b._objecttype);
  swap(a._typeofchange, b._typeofchange);
}

}}}} // namespace
