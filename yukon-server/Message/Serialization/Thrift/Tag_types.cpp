/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "Tag_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


Tag::~Tag() throw() {
}


void Tag::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}

void Tag::__set__instanceId(const int32_t val) {
  this->_instanceId = val;
}

void Tag::__set__pointId(const int32_t val) {
  this->_pointId = val;
}

void Tag::__set__tagId(const int32_t val) {
  this->_tagId = val;
}

void Tag::__set__descriptionStr(const std::string& val) {
  this->_descriptionStr = val;
}

void Tag::__set__action(const int32_t val) {
  this->_action = val;
}

void Tag::__set__tagTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_tagTime = val;
}

void Tag::__set__referenceStr(const std::string& val) {
  this->_referenceStr = val;
}

void Tag::__set__taggedForStr(const std::string& val) {
  this->_taggedForStr = val;
}

void Tag::__set__clientMsgId(const int32_t val) {
  this->_clientMsgId = val;
}
std::ostream& operator<<(std::ostream& out, const Tag& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t Tag::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__instanceId = false;
  bool isset__pointId = false;
  bool isset__tagId = false;
  bool isset__descriptionStr = false;
  bool isset__action = false;
  bool isset__tagTime = false;
  bool isset__referenceStr = false;
  bool isset__taggedForStr = false;
  bool isset__clientMsgId = false;

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
          xfer += iprot->readI32(this->_instanceId);
          isset__instanceId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_pointId);
          isset__pointId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_tagId);
          isset__tagId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_descriptionStr);
          isset__descriptionStr = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_action);
          isset__action = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_tagTime);
          isset__tagTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_referenceStr);
          isset__referenceStr = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 9:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_taggedForStr);
          isset__taggedForStr = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 10:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_clientMsgId);
          isset__clientMsgId = true;
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
  if (!isset__instanceId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__pointId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__tagId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__descriptionStr)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__action)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__tagTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__referenceStr)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__taggedForStr)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__clientMsgId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t Tag::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("Tag");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_instanceId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_instanceId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_pointId", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_pointId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_tagId", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->_tagId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_descriptionStr", ::apache::thrift::protocol::T_STRING, 5);
  xfer += oprot->writeString(this->_descriptionStr);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_action", ::apache::thrift::protocol::T_I32, 6);
  xfer += oprot->writeI32(this->_action);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_tagTime", ::apache::thrift::protocol::T_I64, 7);
  xfer += oprot->writeI64(this->_tagTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_referenceStr", ::apache::thrift::protocol::T_STRING, 8);
  xfer += oprot->writeString(this->_referenceStr);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_taggedForStr", ::apache::thrift::protocol::T_STRING, 9);
  xfer += oprot->writeString(this->_taggedForStr);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_clientMsgId", ::apache::thrift::protocol::T_I32, 10);
  xfer += oprot->writeI32(this->_clientMsgId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(Tag &a, Tag &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._instanceId, b._instanceId);
  swap(a._pointId, b._pointId);
  swap(a._tagId, b._tagId);
  swap(a._descriptionStr, b._descriptionStr);
  swap(a._action, b._action);
  swap(a._tagTime, b._tagTime);
  swap(a._referenceStr, b._referenceStr);
  swap(a._taggedForStr, b._taggedForStr);
  swap(a._clientMsgId, b._clientMsgId);
}

Tag::Tag(const Tag& other0) {
  _baseMessage = other0._baseMessage;
  _instanceId = other0._instanceId;
  _pointId = other0._pointId;
  _tagId = other0._tagId;
  _descriptionStr = other0._descriptionStr;
  _action = other0._action;
  _tagTime = other0._tagTime;
  _referenceStr = other0._referenceStr;
  _taggedForStr = other0._taggedForStr;
  _clientMsgId = other0._clientMsgId;
}
Tag& Tag::operator=(const Tag& other1) {
  _baseMessage = other1._baseMessage;
  _instanceId = other1._instanceId;
  _pointId = other1._pointId;
  _tagId = other1._tagId;
  _descriptionStr = other1._descriptionStr;
  _action = other1._action;
  _tagTime = other1._tagTime;
  _referenceStr = other1._referenceStr;
  _taggedForStr = other1._taggedForStr;
  _clientMsgId = other1._clientMsgId;
  return *this;
}
void Tag::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "Tag(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_instanceId=" << to_string(_instanceId);
  out << ", " << "_pointId=" << to_string(_pointId);
  out << ", " << "_tagId=" << to_string(_tagId);
  out << ", " << "_descriptionStr=" << to_string(_descriptionStr);
  out << ", " << "_action=" << to_string(_action);
  out << ", " << "_tagTime=" << to_string(_tagTime);
  out << ", " << "_referenceStr=" << to_string(_referenceStr);
  out << ", " << "_taggedForStr=" << to_string(_taggedForStr);
  out << ", " << "_clientMsgId=" << to_string(_clientMsgId);
  out << ")";
}

}}}} // namespace
