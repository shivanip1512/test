/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCChangeOpState_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


CCChangeOpState::~CCChangeOpState() noexcept {
}


void CCChangeOpState::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCItemCommand& val) {
  this->_baseMessage = val;
}

void CCChangeOpState::__set__opStateName(const std::string& val) {
  this->_opStateName = val;
}
std::ostream& operator<<(std::ostream& out, const CCChangeOpState& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCChangeOpState::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__opStateName = false;

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
          xfer += iprot->readString(this->_opStateName);
          isset__opStateName = true;
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
  if (!isset__opStateName)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCChangeOpState::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCChangeOpState");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_opStateName", ::apache::thrift::protocol::T_STRING, 2);
  xfer += oprot->writeString(this->_opStateName);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCChangeOpState &a, CCChangeOpState &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._opStateName, b._opStateName);
}

CCChangeOpState::CCChangeOpState(const CCChangeOpState& other0) {
  _baseMessage = other0._baseMessage;
  _opStateName = other0._opStateName;
}
CCChangeOpState& CCChangeOpState::operator=(const CCChangeOpState& other1) {
  _baseMessage = other1._baseMessage;
  _opStateName = other1._opStateName;
  return *this;
}
void CCChangeOpState::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCChangeOpState(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_opStateName=" << to_string(_opStateName);
  out << ")";
}

}}}} // namespace
