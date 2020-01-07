/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "MCInfo_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


MCInfo::~MCInfo() noexcept {
}


void MCInfo::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}

void MCInfo::__set__id(const int32_t val) {
  this->_id = val;
}

void MCInfo::__set__info(const std::string& val) {
  this->_info = val;
}
std::ostream& operator<<(std::ostream& out, const MCInfo& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t MCInfo::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__id = false;
  bool isset__info = false;

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
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_info);
          isset__info = true;
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
  if (!isset__info)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t MCInfo::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("MCInfo");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_id", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_id);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_info", ::apache::thrift::protocol::T_STRING, 3);
  xfer += oprot->writeString(this->_info);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(MCInfo &a, MCInfo &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._id, b._id);
  swap(a._info, b._info);
}

MCInfo::MCInfo(const MCInfo& other0) {
  _baseMessage = other0._baseMessage;
  _id = other0._id;
  _info = other0._info;
}
MCInfo& MCInfo::operator=(const MCInfo& other1) {
  _baseMessage = other1._baseMessage;
  _id = other1._id;
  _info = other1._info;
  return *this;
}
void MCInfo::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "MCInfo(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_id=" << to_string(_id);
  out << ", " << "_info=" << to_string(_info);
  out << ")";
}

}}}} // namespace
