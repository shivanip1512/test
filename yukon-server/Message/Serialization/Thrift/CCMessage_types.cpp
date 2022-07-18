/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCMessage_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


CCMessage::~CCMessage() noexcept {
}


void CCMessage::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}
std::ostream& operator<<(std::ostream& out, const CCMessage& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCMessage::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;

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
      default:
        xfer += iprot->skip(ftype);
        break;
    }
    xfer += iprot->readFieldEnd();
  }

  xfer += iprot->readStructEnd();

  if (!isset__baseMessage)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCMessage::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCMessage");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCMessage &a, CCMessage &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
}

CCMessage::CCMessage(const CCMessage& other0) {
  _baseMessage = other0._baseMessage;
}
CCMessage& CCMessage::operator=(const CCMessage& other1) {
  _baseMessage = other1._baseMessage;
  return *this;
}
void CCMessage::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCMessage(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ")";
}


CCPao::~CCPao() noexcept {
}


void CCPao::__set__paoId(const int32_t val) {
  this->_paoId = val;
}

void CCPao::__set__paoCategory(const std::string& val) {
  this->_paoCategory = val;
}

void CCPao::__set__paoClass(const std::string& val) {
  this->_paoClass = val;
}

void CCPao::__set__paoName(const std::string& val) {
  this->_paoName = val;
}

void CCPao::__set__paoType(const std::string& val) {
  this->_paoType = val;
}

void CCPao::__set__paoDescription(const std::string& val) {
  this->_paoDescription = val;
}

void CCPao::__set__disableFlag(const bool val) {
  this->_disableFlag = val;
}
std::ostream& operator<<(std::ostream& out, const CCPao& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCPao::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__paoId = false;
  bool isset__paoCategory = false;
  bool isset__paoClass = false;
  bool isset__paoName = false;
  bool isset__paoType = false;
  bool isset__paoDescription = false;
  bool isset__disableFlag = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_paoId);
          isset__paoId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_paoCategory);
          isset__paoCategory = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_paoClass);
          isset__paoClass = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_paoName);
          isset__paoName = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_paoType);
          isset__paoType = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_paoDescription);
          isset__paoDescription = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_disableFlag);
          isset__disableFlag = true;
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

  if (!isset__paoId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__paoCategory)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__paoClass)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__paoName)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__paoType)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__paoDescription)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__disableFlag)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCPao::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCPao");

  xfer += oprot->writeFieldBegin("_paoId", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32(this->_paoId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_paoCategory", ::apache::thrift::protocol::T_STRING, 2);
  xfer += oprot->writeString(this->_paoCategory);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_paoClass", ::apache::thrift::protocol::T_STRING, 3);
  xfer += oprot->writeString(this->_paoClass);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_paoName", ::apache::thrift::protocol::T_STRING, 4);
  xfer += oprot->writeString(this->_paoName);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_paoType", ::apache::thrift::protocol::T_STRING, 5);
  xfer += oprot->writeString(this->_paoType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_paoDescription", ::apache::thrift::protocol::T_STRING, 6);
  xfer += oprot->writeString(this->_paoDescription);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_disableFlag", ::apache::thrift::protocol::T_BOOL, 7);
  xfer += oprot->writeBool(this->_disableFlag);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCPao &a, CCPao &b) {
  using ::std::swap;
  swap(a._paoId, b._paoId);
  swap(a._paoCategory, b._paoCategory);
  swap(a._paoClass, b._paoClass);
  swap(a._paoName, b._paoName);
  swap(a._paoType, b._paoType);
  swap(a._paoDescription, b._paoDescription);
  swap(a._disableFlag, b._disableFlag);
}

CCPao::CCPao(const CCPao& other2) {
  _paoId = other2._paoId;
  _paoCategory = other2._paoCategory;
  _paoClass = other2._paoClass;
  _paoName = other2._paoName;
  _paoType = other2._paoType;
  _paoDescription = other2._paoDescription;
  _disableFlag = other2._disableFlag;
}
CCPao& CCPao::operator=(const CCPao& other3) {
  _paoId = other3._paoId;
  _paoCategory = other3._paoCategory;
  _paoClass = other3._paoClass;
  _paoName = other3._paoName;
  _paoType = other3._paoType;
  _paoDescription = other3._paoDescription;
  _disableFlag = other3._disableFlag;
  return *this;
}
void CCPao::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCPao(";
  out << "_paoId=" << to_string(_paoId);
  out << ", " << "_paoCategory=" << to_string(_paoCategory);
  out << ", " << "_paoClass=" << to_string(_paoClass);
  out << ", " << "_paoName=" << to_string(_paoName);
  out << ", " << "_paoType=" << to_string(_paoType);
  out << ", " << "_paoDescription=" << to_string(_paoDescription);
  out << ", " << "_disableFlag=" << to_string(_disableFlag);
  out << ")";
}

}}}} // namespace
