/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "Multi_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


Multi::~Multi() noexcept {
}


void Multi::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}

void Multi::__set__bag(const std::vector< ::Cti::Messaging::Serialization::Thrift::GenericMessage> & val) {
  this->_bag = val;
}
std::ostream& operator<<(std::ostream& out, const Multi& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t Multi::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__bag = false;

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
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_bag.clear();
            uint32_t _size0;
            ::apache::thrift::protocol::TType _etype3;
            xfer += iprot->readListBegin(_etype3, _size0);
            this->_bag.resize(_size0);
            uint32_t _i4;
            for (_i4 = 0; _i4 < _size0; ++_i4)
            {
              xfer += this->_bag[_i4].read(iprot);
            }
            xfer += iprot->readListEnd();
          }
          isset__bag = true;
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
  if (!isset__bag)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t Multi::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("Multi");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_bag", ::apache::thrift::protocol::T_LIST, 2);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_STRUCT, static_cast<uint32_t>(this->_bag.size()));
    std::vector< ::Cti::Messaging::Serialization::Thrift::GenericMessage> ::const_iterator _iter5;
    for (_iter5 = this->_bag.begin(); _iter5 != this->_bag.end(); ++_iter5)
    {
      xfer += (*_iter5).write(oprot);
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(Multi &a, Multi &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._bag, b._bag);
}

Multi::Multi(const Multi& other6) {
  _baseMessage = other6._baseMessage;
  _bag = other6._bag;
}
Multi& Multi::operator=(const Multi& other7) {
  _baseMessage = other7._baseMessage;
  _bag = other7._bag;
  return *this;
}
void Multi::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "Multi(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_bag=" << to_string(_bag);
  out << ")";
}

}}}} // namespace
