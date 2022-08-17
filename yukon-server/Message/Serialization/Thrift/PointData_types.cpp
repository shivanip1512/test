/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "PointData_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


PointData::~PointData() noexcept {
}


void PointData::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
  this->_baseMessage = val;
}

void PointData::__set__id(const int32_t val) {
  this->_id = val;
}

void PointData::__set__type(const int8_t val) {
  this->_type = val;
}

void PointData::__set__quality(const int8_t val) {
  this->_quality = val;
}

void PointData::__set__tags(const int32_t val) {
  this->_tags = val;
}

void PointData::__set__value(const double val) {
  this->_value = val;
}

void PointData::__set__str(const std::string& val) {
  this->_str = val;
}

void PointData::__set__time(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_time = val;
}

void PointData::__set__millis(const int16_t val) {
  this->_millis = val;
}
std::ostream& operator<<(std::ostream& out, const PointData& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t PointData::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__id = false;
  bool isset__type = false;
  bool isset__quality = false;
  bool isset__tags = false;
  bool isset__value = false;
  bool isset__str = false;
  bool isset__time = false;
  bool isset__millis = false;

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
        if (ftype == ::apache::thrift::protocol::T_BYTE) {
          xfer += iprot->readByte(this->_type);
          isset__type = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_BYTE) {
          xfer += iprot->readByte(this->_quality);
          isset__quality = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_tags);
          isset__tags = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_DOUBLE) {
          xfer += iprot->readDouble(this->_value);
          isset__value = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_str);
          isset__str = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_time);
          isset__time = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 9:
        if (ftype == ::apache::thrift::protocol::T_I16) {
          xfer += iprot->readI16(this->_millis);
          isset__millis = true;
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
  if (!isset__type)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__quality)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__tags)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__value)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__str)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__time)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__millis)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t PointData::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("PointData");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_id", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_id);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_type", ::apache::thrift::protocol::T_BYTE, 3);
  xfer += oprot->writeByte(this->_type);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_quality", ::apache::thrift::protocol::T_BYTE, 4);
  xfer += oprot->writeByte(this->_quality);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_tags", ::apache::thrift::protocol::T_I32, 5);
  xfer += oprot->writeI32(this->_tags);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_value", ::apache::thrift::protocol::T_DOUBLE, 6);
  xfer += oprot->writeDouble(this->_value);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_str", ::apache::thrift::protocol::T_STRING, 7);
  xfer += oprot->writeString(this->_str);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_time", ::apache::thrift::protocol::T_I64, 8);
  xfer += oprot->writeI64(this->_time);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_millis", ::apache::thrift::protocol::T_I16, 9);
  xfer += oprot->writeI16(this->_millis);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(PointData &a, PointData &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._id, b._id);
  swap(a._type, b._type);
  swap(a._quality, b._quality);
  swap(a._tags, b._tags);
  swap(a._value, b._value);
  swap(a._str, b._str);
  swap(a._time, b._time);
  swap(a._millis, b._millis);
}

PointData::PointData(const PointData& other0) {
  _baseMessage = other0._baseMessage;
  _id = other0._id;
  _type = other0._type;
  _quality = other0._quality;
  _tags = other0._tags;
  _value = other0._value;
  _str = other0._str;
  _time = other0._time;
  _millis = other0._millis;
}
PointData& PointData::operator=(const PointData& other1) {
  _baseMessage = other1._baseMessage;
  _id = other1._id;
  _type = other1._type;
  _quality = other1._quality;
  _tags = other1._tags;
  _value = other1._value;
  _str = other1._str;
  _time = other1._time;
  _millis = other1._millis;
  return *this;
}
void PointData::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "PointData(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_id=" << to_string(_id);
  out << ", " << "_type=" << to_string(_type);
  out << ", " << "_quality=" << to_string(_quality);
  out << ", " << "_tags=" << to_string(_tags);
  out << ", " << "_value=" << to_string(_value);
  out << ", " << "_str=" << to_string(_str);
  out << ", " << "_time=" << to_string(_time);
  out << ", " << "_millis=" << to_string(_millis);
  out << ")";
}

}}}} // namespace
