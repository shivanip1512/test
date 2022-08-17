/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "Signal_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


Signal::~Signal() noexcept {
}


void Signal::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}

void Signal::__set__id(const int32_t val) {
  this->_id = val;
}

void Signal::__set__logType(const int32_t val) {
  this->_logType = val;
}

void Signal::__set__signalCategory(const int32_t val) {
  this->_signalCategory = val;
}

void Signal::__set__text(const std::string& val) {
  this->_text = val;
}

void Signal::__set__additionalInfo(const std::string& val) {
  this->_additionalInfo = val;
}

void Signal::__set__tags(const int32_t val) {
  this->_tags = val;
}

void Signal::__set__condition(const int32_t val) {
  this->_condition = val;
}

void Signal::__set__signalMillis(const int32_t val) {
  this->_signalMillis = val;
}

void Signal::__set__pointValue(const double val) {
  this->_pointValue = val;
}
std::ostream& operator<<(std::ostream& out, const Signal& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t Signal::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__id = false;
  bool isset__logType = false;
  bool isset__signalCategory = false;
  bool isset__text = false;
  bool isset__additionalInfo = false;
  bool isset__tags = false;
  bool isset__condition = false;
  bool isset__signalMillis = false;
  bool isset__pointValue = false;

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
          xfer += iprot->readI32(this->_id);
          isset__id = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_logType);
          isset__logType = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_signalCategory);
          isset__signalCategory = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_text);
          isset__text = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_additionalInfo);
          isset__additionalInfo = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_tags);
          isset__tags = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_condition);
          isset__condition = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 9:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_signalMillis);
          isset__signalMillis = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 10:
        if (ftype == ::apache::thrift::protocol::T_DOUBLE) {
          xfer += iprot->readDouble(this->_pointValue);
          isset__pointValue = true;
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
  if (!isset__id)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__logType)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__signalCategory)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__text)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__additionalInfo)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__tags)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__condition)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__signalMillis)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__pointValue)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t Signal::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("Signal");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_id", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_id);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_logType", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_logType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_signalCategory", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->_signalCategory);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_text", ::apache::thrift::protocol::T_STRING, 5);
  xfer += oprot->writeString(this->_text);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_additionalInfo", ::apache::thrift::protocol::T_STRING, 6);
  xfer += oprot->writeString(this->_additionalInfo);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_tags", ::apache::thrift::protocol::T_I32, 7);
  xfer += oprot->writeI32(this->_tags);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_condition", ::apache::thrift::protocol::T_I32, 8);
  xfer += oprot->writeI32(this->_condition);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_signalMillis", ::apache::thrift::protocol::T_I32, 9);
  xfer += oprot->writeI32(this->_signalMillis);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_pointValue", ::apache::thrift::protocol::T_DOUBLE, 10);
  xfer += oprot->writeDouble(this->_pointValue);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(Signal &a, Signal &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._id, b._id);
  swap(a._logType, b._logType);
  swap(a._signalCategory, b._signalCategory);
  swap(a._text, b._text);
  swap(a._additionalInfo, b._additionalInfo);
  swap(a._tags, b._tags);
  swap(a._condition, b._condition);
  swap(a._signalMillis, b._signalMillis);
  swap(a._pointValue, b._pointValue);
}

Signal::Signal(const Signal& other0) {
  _baseMessage = other0._baseMessage;
  _id = other0._id;
  _logType = other0._logType;
  _signalCategory = other0._signalCategory;
  _text = other0._text;
  _additionalInfo = other0._additionalInfo;
  _tags = other0._tags;
  _condition = other0._condition;
  _signalMillis = other0._signalMillis;
  _pointValue = other0._pointValue;
}
Signal& Signal::operator=(const Signal& other1) {
  _baseMessage = other1._baseMessage;
  _id = other1._id;
  _logType = other1._logType;
  _signalCategory = other1._signalCategory;
  _text = other1._text;
  _additionalInfo = other1._additionalInfo;
  _tags = other1._tags;
  _condition = other1._condition;
  _signalMillis = other1._signalMillis;
  _pointValue = other1._pointValue;
  return *this;
}
void Signal::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "Signal(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_id=" << to_string(_id);
  out << ", " << "_logType=" << to_string(_logType);
  out << ", " << "_signalCategory=" << to_string(_signalCategory);
  out << ", " << "_text=" << to_string(_text);
  out << ", " << "_additionalInfo=" << to_string(_additionalInfo);
  out << ", " << "_tags=" << to_string(_tags);
  out << ", " << "_condition=" << to_string(_condition);
  out << ", " << "_signalMillis=" << to_string(_signalMillis);
  out << ", " << "_pointValue=" << to_string(_pointValue);
  out << ")";
}

}}}} // namespace
