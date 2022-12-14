/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCVerifySelectedBank_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


CCVerifySelectedBank::~CCVerifySelectedBank() noexcept {
}


void CCVerifySelectedBank::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCVerifyBanks& val) {
  this->_baseMessage = val;
}

void CCVerifySelectedBank::__set__bankId(const int32_t val) {
  this->_bankId = val;
}
std::ostream& operator<<(std::ostream& out, const CCVerifySelectedBank& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCVerifySelectedBank::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__bankId = false;

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
          xfer += iprot->readI32(this->_bankId);
          isset__bankId = true;
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
  if (!isset__bankId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCVerifySelectedBank::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCVerifySelectedBank");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_bankId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_bankId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCVerifySelectedBank &a, CCVerifySelectedBank &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._bankId, b._bankId);
}

CCVerifySelectedBank::CCVerifySelectedBank(const CCVerifySelectedBank& other0) {
  _baseMessage = other0._baseMessage;
  _bankId = other0._bankId;
}
CCVerifySelectedBank& CCVerifySelectedBank::operator=(const CCVerifySelectedBank& other1) {
  _baseMessage = other1._baseMessage;
  _bankId = other1._bankId;
  return *this;
}
void CCVerifySelectedBank::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCVerifySelectedBank(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_bankId=" << to_string(_bankId);
  out << ")";
}

}}}} // namespace
