/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef Signal_TYPES_H
#define Signal_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "Message_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class Signal {
 public:

  static const char* ascii_fingerprint; // = "6F6E54C63099F9C060E7EDAA57040E1D";
  static const uint8_t binary_fingerprint[16]; // = {0x6F,0x6E,0x54,0xC6,0x30,0x99,0xF9,0xC0,0x60,0xE7,0xED,0xAA,0x57,0x04,0x0E,0x1D};

  Signal() : _id(0), _logType(0), _signalCategory(0), _text(), _additionalInfo(), _tags(0), _condition(0), _signalMillis(0), _pointValue(0) {
  }

  virtual ~Signal() throw() {}

   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  int32_t _id;
  int32_t _logType;
  int32_t _signalCategory;
  std::string _text;
  std::string _additionalInfo;
  int32_t _tags;
  int32_t _condition;
  int32_t _signalMillis;
  double _pointValue;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
    _baseMessage = val;
  }

  void __set__id(const int32_t val) {
    _id = val;
  }

  void __set__logType(const int32_t val) {
    _logType = val;
  }

  void __set__signalCategory(const int32_t val) {
    _signalCategory = val;
  }

  void __set__text(const std::string& val) {
    _text = val;
  }

  void __set__additionalInfo(const std::string& val) {
    _additionalInfo = val;
  }

  void __set__tags(const int32_t val) {
    _tags = val;
  }

  void __set__condition(const int32_t val) {
    _condition = val;
  }

  void __set__signalMillis(const int32_t val) {
    _signalMillis = val;
  }

  void __set__pointValue(const double val) {
    _pointValue = val;
  }

  bool operator == (const Signal & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_id == rhs._id))
      return false;
    if (!(_logType == rhs._logType))
      return false;
    if (!(_signalCategory == rhs._signalCategory))
      return false;
    if (!(_text == rhs._text))
      return false;
    if (!(_additionalInfo == rhs._additionalInfo))
      return false;
    if (!(_tags == rhs._tags))
      return false;
    if (!(_condition == rhs._condition))
      return false;
    if (!(_signalMillis == rhs._signalMillis))
      return false;
    if (!(_pointValue == rhs._pointValue))
      return false;
    return true;
  }
  bool operator != (const Signal &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Signal & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(Signal &a, Signal &b);

}}}} // namespace

#endif
