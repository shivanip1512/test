/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCItemCommand_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


CCItemCommand::~CCItemCommand() throw() {
}


void CCItemCommand::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCCommand& val) {
  this->_baseMessage = val;
}

void CCItemCommand::__set__itemId(const int32_t val) {
  this->_itemId = val;
}
std::ostream& operator<<(std::ostream& out, const CCItemCommand& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCItemCommand::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__itemId = false;

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
          xfer += iprot->readI32(this->_itemId);
          isset__itemId = true;
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
  if (!isset__itemId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCItemCommand::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCItemCommand");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_itemId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_itemId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCItemCommand &a, CCItemCommand &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._itemId, b._itemId);
}

CCItemCommand::CCItemCommand(const CCItemCommand& other0) {
  _baseMessage = other0._baseMessage;
  _itemId = other0._itemId;
}
CCItemCommand& CCItemCommand::operator=(const CCItemCommand& other1) {
  _baseMessage = other1._baseMessage;
  _itemId = other1._itemId;
  return *this;
}
void CCItemCommand::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCItemCommand(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_itemId=" << to_string(_itemId);
  out << ")";
}

}}}} // namespace
