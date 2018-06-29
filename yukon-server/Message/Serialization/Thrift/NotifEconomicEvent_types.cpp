/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "NotifEconomicEvent_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


NotifEconomicEvent::~NotifEconomicEvent() throw() {
}


void NotifEconomicEvent::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}

void NotifEconomicEvent::__set__economicEventId(const int32_t val) {
  this->_economicEventId = val;
}

void NotifEconomicEvent::__set__revisionNumber(const int32_t val) {
  this->_revisionNumber = val;
}

void NotifEconomicEvent::__set__action(const int32_t val) {
  this->_action = val;
}
std::ostream& operator<<(std::ostream& out, const NotifEconomicEvent& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t NotifEconomicEvent::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__economicEventId = false;
  bool isset__revisionNumber = false;
  bool isset__action = false;

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
          xfer += iprot->readI32(this->_economicEventId);
          isset__economicEventId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_revisionNumber);
          isset__revisionNumber = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_action);
          isset__action = true;
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
  if (!isset__economicEventId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__revisionNumber)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__action)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t NotifEconomicEvent::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("NotifEconomicEvent");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_economicEventId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_economicEventId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_revisionNumber", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_revisionNumber);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_action", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->_action);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(NotifEconomicEvent &a, NotifEconomicEvent &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._economicEventId, b._economicEventId);
  swap(a._revisionNumber, b._revisionNumber);
  swap(a._action, b._action);
}

NotifEconomicEvent::NotifEconomicEvent(const NotifEconomicEvent& other0) {
  _baseMessage = other0._baseMessage;
  _economicEventId = other0._economicEventId;
  _revisionNumber = other0._revisionNumber;
  _action = other0._action;
}
NotifEconomicEvent& NotifEconomicEvent::operator=(const NotifEconomicEvent& other1) {
  _baseMessage = other1._baseMessage;
  _economicEventId = other1._economicEventId;
  _revisionNumber = other1._revisionNumber;
  _action = other1._action;
  return *this;
}
void NotifEconomicEvent::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "NotifEconomicEvent(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_economicEventId=" << to_string(_economicEventId);
  out << ", " << "_revisionNumber=" << to_string(_revisionNumber);
  out << ", " << "_action=" << to_string(_action);
  out << ")";
}

}}}} // namespace
