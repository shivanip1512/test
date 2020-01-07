/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCVerifyInactiveBanks_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


CCVerifyInactiveBanks::~CCVerifyInactiveBanks() noexcept {
}


void CCVerifyInactiveBanks::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCVerifyBanks& val) {
  this->_baseMessage = val;
}

void CCVerifyInactiveBanks::__set__bankInactiveTime(const int32_t val) {
  this->_bankInactiveTime = val;
}
std::ostream& operator<<(std::ostream& out, const CCVerifyInactiveBanks& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCVerifyInactiveBanks::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__bankInactiveTime = false;

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
          xfer += iprot->readI32(this->_bankInactiveTime);
          isset__bankInactiveTime = true;
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
  if (!isset__bankInactiveTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCVerifyInactiveBanks::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCVerifyInactiveBanks");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_bankInactiveTime", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_bankInactiveTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCVerifyInactiveBanks &a, CCVerifyInactiveBanks &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._bankInactiveTime, b._bankInactiveTime);
}

CCVerifyInactiveBanks::CCVerifyInactiveBanks(const CCVerifyInactiveBanks& other0) {
  _baseMessage = other0._baseMessage;
  _bankInactiveTime = other0._bankInactiveTime;
}
CCVerifyInactiveBanks& CCVerifyInactiveBanks::operator=(const CCVerifyInactiveBanks& other1) {
  _baseMessage = other1._baseMessage;
  _bankInactiveTime = other1._bankInactiveTime;
  return *this;
}
void CCVerifyInactiveBanks::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCVerifyInactiveBanks(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_bankInactiveTime=" << to_string(_bankInactiveTime);
  out << ")";
}

}}}} // namespace
