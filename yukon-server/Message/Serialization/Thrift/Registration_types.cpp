/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "Registration_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* Registration::ascii_fingerprint = "8B66031ACDC8DED3DC5A35ED21FC26AA";
const uint8_t Registration::binary_fingerprint[16] = {0x8B,0x66,0x03,0x1A,0xCD,0xC8,0xDE,0xD3,0xDC,0x5A,0x35,0xED,0x21,0xFC,0x26,0xAA};

uint32_t Registration::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__appName = false;
  bool isset__appId = false;
  bool isset__appIsUnique = false;
  bool isset__appExpirationDelay = false;

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
          xfer += iprot->readString(this->_appName);
          isset__appName = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_appId);
          isset__appId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_appIsUnique);
          isset__appIsUnique = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_appExpirationDelay);
          isset__appExpirationDelay = true;
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
  if (!isset__appName)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__appId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__appIsUnique)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__appExpirationDelay)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t Registration::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("Registration");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_appName", ::apache::thrift::protocol::T_STRING, 2);
  xfer += oprot->writeString(this->_appName);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_appId", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_appId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_appIsUnique", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->_appIsUnique);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_appExpirationDelay", ::apache::thrift::protocol::T_I32, 5);
  xfer += oprot->writeI32(this->_appExpirationDelay);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(Registration &a, Registration &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._appName, b._appName);
  swap(a._appId, b._appId);
  swap(a._appIsUnique, b._appIsUnique);
  swap(a._appExpirationDelay, b._appExpirationDelay);
}

}}}} // namespace
