/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCCapBankStates_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


CCState::~CCState() noexcept {
}


void CCState::__set__text(const std::string& val) {
  this->_text = val;
}

void CCState::__set__foregroundColor(const int32_t val) {
  this->_foregroundColor = val;
}

void CCState::__set__backgroundColor(const int32_t val) {
  this->_backgroundColor = val;
}
std::ostream& operator<<(std::ostream& out, const CCState& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCState::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__text = false;
  bool isset__foregroundColor = false;
  bool isset__backgroundColor = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_text);
          isset__text = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_foregroundColor);
          isset__foregroundColor = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_backgroundColor);
          isset__backgroundColor = true;
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

  if (!isset__text)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__foregroundColor)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__backgroundColor)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCState::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCState");

  xfer += oprot->writeFieldBegin("_text", ::apache::thrift::protocol::T_STRING, 1);
  xfer += oprot->writeString(this->_text);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_foregroundColor", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_foregroundColor);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_backgroundColor", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_backgroundColor);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCState &a, CCState &b) {
  using ::std::swap;
  swap(a._text, b._text);
  swap(a._foregroundColor, b._foregroundColor);
  swap(a._backgroundColor, b._backgroundColor);
}

CCState::CCState(const CCState& other0) {
  _text = other0._text;
  _foregroundColor = other0._foregroundColor;
  _backgroundColor = other0._backgroundColor;
}
CCState& CCState::operator=(const CCState& other1) {
  _text = other1._text;
  _foregroundColor = other1._foregroundColor;
  _backgroundColor = other1._backgroundColor;
  return *this;
}
void CCState::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCState(";
  out << "_text=" << to_string(_text);
  out << ", " << "_foregroundColor=" << to_string(_foregroundColor);
  out << ", " << "_backgroundColor=" << to_string(_backgroundColor);
  out << ")";
}


CCCapBankStates::~CCCapBankStates() noexcept {
}


void CCCapBankStates::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCMessage& val) {
  this->_baseMessage = val;
}

void CCCapBankStates::__set__ccCapBankStates(const std::vector<CCState> & val) {
  this->_ccCapBankStates = val;
}
std::ostream& operator<<(std::ostream& out, const CCCapBankStates& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCCapBankStates::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__ccCapBankStates = false;

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
            this->_ccCapBankStates.clear();
            uint32_t _size2;
            ::apache::thrift::protocol::TType _etype5;
            xfer += iprot->readListBegin(_etype5, _size2);
            this->_ccCapBankStates.resize(_size2);
            uint32_t _i6;
            for (_i6 = 0; _i6 < _size2; ++_i6)
            {
              xfer += this->_ccCapBankStates[_i6].read(iprot);
            }
            xfer += iprot->readListEnd();
          }
          isset__ccCapBankStates = true;
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
  if (!isset__ccCapBankStates)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCCapBankStates::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCCapBankStates");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_ccCapBankStates", ::apache::thrift::protocol::T_LIST, 2);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_STRUCT, static_cast<uint32_t>(this->_ccCapBankStates.size()));
    std::vector<CCState> ::const_iterator _iter7;
    for (_iter7 = this->_ccCapBankStates.begin(); _iter7 != this->_ccCapBankStates.end(); ++_iter7)
    {
      xfer += (*_iter7).write(oprot);
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCCapBankStates &a, CCCapBankStates &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._ccCapBankStates, b._ccCapBankStates);
}

CCCapBankStates::CCCapBankStates(const CCCapBankStates& other8) {
  _baseMessage = other8._baseMessage;
  _ccCapBankStates = other8._ccCapBankStates;
}
CCCapBankStates& CCCapBankStates::operator=(const CCCapBankStates& other9) {
  _baseMessage = other9._baseMessage;
  _ccCapBankStates = other9._ccCapBankStates;
  return *this;
}
void CCCapBankStates::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCCapBankStates(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_ccCapBankStates=" << to_string(_ccCapBankStates);
  out << ")";
}

}}}} // namespace
