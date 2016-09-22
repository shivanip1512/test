/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCCommand_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* CCCommand::ascii_fingerprint = "B3D2273D6CC64D984191CE4F8FAF30C8";
const uint8_t CCCommand::binary_fingerprint[16] = {0xB3,0xD2,0x27,0x3D,0x6C,0xC6,0x4D,0x98,0x41,0x91,0xCE,0x4F,0x8F,0xAF,0x30,0xC8};

uint32_t CCCommand::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__messageId = false;
  bool isset__commandId = false;

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
          xfer += iprot->readI32(this->_commandId);
          isset__commandId = true;
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
  if (!isset__commandId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCCommand::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("CCCommand");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_messageId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_messageId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_commandId", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_commandId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCCommand &a, CCCommand &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._messageId, b._messageId);
  swap(a._commandId, b._commandId);
}

}}}} // namespace
