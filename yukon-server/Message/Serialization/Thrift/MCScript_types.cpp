/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "MCScript_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* MCScript::ascii_fingerprint = "71A6D597310848C59D8E6F647C8CF739";
const uint8_t MCScript::binary_fingerprint[16] = {0x71,0xA6,0xD5,0x97,0x31,0x08,0x48,0xC5,0x9D,0x8E,0x6F,0x64,0x7C,0x8C,0xF7,0x39};

uint32_t MCScript::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__name = false;
  bool isset__contents = false;

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
          xfer += iprot->readString(this->_name);
          isset__name = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_contents);
          isset__contents = true;
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
  if (!isset__name)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__contents)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t MCScript::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("MCScript");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_name", ::apache::thrift::protocol::T_STRING, 2);
  xfer += oprot->writeString(this->_name);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_contents", ::apache::thrift::protocol::T_STRING, 3);
  xfer += oprot->writeString(this->_contents);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(MCScript &a, MCScript &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._name, b._name);
  swap(a._contents, b._contents);
}

}}}} // namespace
