/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef NotifAlarm_TYPES_H
#define NotifAlarm_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "Message_types.h"
#include "Types_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class NotifAlarm {
 public:

  static const char* ascii_fingerprint; // = "3271EE90AB14A73F013B6B230554E226";
  static const uint8_t binary_fingerprint[16]; // = {0x32,0x71,0xEE,0x90,0xAB,0x14,0xA7,0x3F,0x01,0x3B,0x6B,0x23,0x05,0x54,0xE2,0x26};

  NotifAlarm() : _categoryId(0), _pointId(0), _condition(0), _value(0), _alarmTimestamp(0), _acknowledged(0), _abnormal(0) {
  }

  virtual ~NotifAlarm() throw() {}

   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  std::vector<int32_t>  _notifGroupIds;
  int32_t _categoryId;
  int32_t _pointId;
  int32_t _condition;
  double _value;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _alarmTimestamp;
  bool _acknowledged;
  bool _abnormal;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
    _baseMessage = val;
  }

  void __set__notifGroupIds(const std::vector<int32_t> & val) {
    _notifGroupIds = val;
  }

  void __set__categoryId(const int32_t val) {
    _categoryId = val;
  }

  void __set__pointId(const int32_t val) {
    _pointId = val;
  }

  void __set__condition(const int32_t val) {
    _condition = val;
  }

  void __set__value(const double val) {
    _value = val;
  }

  void __set__alarmTimestamp(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
    _alarmTimestamp = val;
  }

  void __set__acknowledged(const bool val) {
    _acknowledged = val;
  }

  void __set__abnormal(const bool val) {
    _abnormal = val;
  }

  bool operator == (const NotifAlarm & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_notifGroupIds == rhs._notifGroupIds))
      return false;
    if (!(_categoryId == rhs._categoryId))
      return false;
    if (!(_pointId == rhs._pointId))
      return false;
    if (!(_condition == rhs._condition))
      return false;
    if (!(_value == rhs._value))
      return false;
    if (!(_alarmTimestamp == rhs._alarmTimestamp))
      return false;
    if (!(_acknowledged == rhs._acknowledged))
      return false;
    if (!(_abnormal == rhs._abnormal))
      return false;
    return true;
  }
  bool operator != (const NotifAlarm &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const NotifAlarm & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(NotifAlarm &a, NotifAlarm &b);

}}}} // namespace

#endif
