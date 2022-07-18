/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "Command_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


Command::~Command() noexcept {
}


void Command::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}

void Command::__set__operation(const int32_t val) {
  this->_operation = val;
}

void Command::__set__opString(const std::string& val) {
  this->_opString = val;
}

void Command::__set__opArgCount(const int32_t val) {
  this->_opArgCount = val;
}

void Command::__set__opArgList(const std::vector<int32_t> & val) {
  this->_opArgList = val;
}
std::ostream& operator<<(std::ostream& out, const Command& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t Command::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__operation = false;
  bool isset__opString = false;
  bool isset__opArgCount = false;
  bool isset__opArgList = false;

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
          xfer += iprot->readI32(this->_operation);
          isset__operation = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_opString);
          isset__opString = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_opArgCount);
          isset__opArgCount = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_opArgList.clear();
            uint32_t _size0;
            ::apache::thrift::protocol::TType _etype3;
            xfer += iprot->readListBegin(_etype3, _size0);
            this->_opArgList.resize(_size0);
            uint32_t _i4;
            for (_i4 = 0; _i4 < _size0; ++_i4)
            {
              xfer += iprot->readI32(this->_opArgList[_i4]);
            }
            xfer += iprot->readListEnd();
          }
          isset__opArgList = true;
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
  if (!isset__operation)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__opString)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__opArgCount)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__opArgList)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t Command::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("Command");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_operation", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_operation);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_opString", ::apache::thrift::protocol::T_STRING, 3);
  xfer += oprot->writeString(this->_opString);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_opArgCount", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->_opArgCount);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_opArgList", ::apache::thrift::protocol::T_LIST, 5);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_I32, static_cast<uint32_t>(this->_opArgList.size()));
    std::vector<int32_t> ::const_iterator _iter5;
    for (_iter5 = this->_opArgList.begin(); _iter5 != this->_opArgList.end(); ++_iter5)
    {
      xfer += oprot->writeI32((*_iter5));
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(Command &a, Command &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._operation, b._operation);
  swap(a._opString, b._opString);
  swap(a._opArgCount, b._opArgCount);
  swap(a._opArgList, b._opArgList);
}

Command::Command(const Command& other6) {
  _baseMessage = other6._baseMessage;
  _operation = other6._operation;
  _opString = other6._opString;
  _opArgCount = other6._opArgCount;
  _opArgList = other6._opArgList;
}
Command& Command::operator=(const Command& other7) {
  _baseMessage = other7._baseMessage;
  _operation = other7._operation;
  _opString = other7._opString;
  _opArgCount = other7._opArgCount;
  _opArgList = other7._opArgList;
  return *this;
}
void Command::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "Command(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_operation=" << to_string(_operation);
  out << ", " << "_opString=" << to_string(_opString);
  out << ", " << "_opArgCount=" << to_string(_opArgCount);
  out << ", " << "_opArgList=" << to_string(_opArgList);
  out << ")";
}

}}}} // namespace
