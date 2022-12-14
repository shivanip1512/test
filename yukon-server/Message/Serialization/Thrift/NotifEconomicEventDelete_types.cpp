/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "NotifEconomicEventDelete_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


NotifEconomicEventDelete::~NotifEconomicEventDelete() noexcept {
}


void NotifEconomicEventDelete::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}

void NotifEconomicEventDelete::__set__economicEventId(const int32_t val) {
  this->_economicEventId = val;
}

void NotifEconomicEventDelete::__set__deleteStart(const bool val) {
  this->_deleteStart = val;
}

void NotifEconomicEventDelete::__set__deleteStop(const bool val) {
  this->_deleteStop = val;
}
std::ostream& operator<<(std::ostream& out, const NotifEconomicEventDelete& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t NotifEconomicEventDelete::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__economicEventId = false;
  bool isset__deleteStart = false;
  bool isset__deleteStop = false;

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
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_deleteStart);
          isset__deleteStart = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_deleteStop);
          isset__deleteStop = true;
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
  if (!isset__deleteStart)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__deleteStop)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t NotifEconomicEventDelete::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("NotifEconomicEventDelete");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_economicEventId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_economicEventId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_deleteStart", ::apache::thrift::protocol::T_BOOL, 3);
  xfer += oprot->writeBool(this->_deleteStart);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_deleteStop", ::apache::thrift::protocol::T_BOOL, 4);
  xfer += oprot->writeBool(this->_deleteStop);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(NotifEconomicEventDelete &a, NotifEconomicEventDelete &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._economicEventId, b._economicEventId);
  swap(a._deleteStart, b._deleteStart);
  swap(a._deleteStop, b._deleteStop);
}

NotifEconomicEventDelete::NotifEconomicEventDelete(const NotifEconomicEventDelete& other0) {
  _baseMessage = other0._baseMessage;
  _economicEventId = other0._economicEventId;
  _deleteStart = other0._deleteStart;
  _deleteStop = other0._deleteStop;
}
NotifEconomicEventDelete& NotifEconomicEventDelete::operator=(const NotifEconomicEventDelete& other1) {
  _baseMessage = other1._baseMessage;
  _economicEventId = other1._economicEventId;
  _deleteStart = other1._deleteStart;
  _deleteStop = other1._deleteStop;
  return *this;
}
void NotifEconomicEventDelete::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "NotifEconomicEventDelete(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_economicEventId=" << to_string(_economicEventId);
  out << ", " << "_deleteStart=" << to_string(_deleteStart);
  out << ", " << "_deleteStop=" << to_string(_deleteStop);
  out << ")";
}

}}}} // namespace
