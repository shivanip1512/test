/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef NotifLMControl_TYPES_H
#define NotifLMControl_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "Message_types.h"
#include "Types_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class NotifLMControl {
 public:

  static const char* ascii_fingerprint; // = "253D0364D98DF8B4DA7C1B4CD349BBEA";
  static const uint8_t binary_fingerprint[16]; // = {0x25,0x3D,0x03,0x64,0xD9,0x8D,0xF8,0xB4,0xDA,0x7C,0x1B,0x4C,0xD3,0x49,0xBB,0xEA};

  NotifLMControl() : _notifType(0), _programId(0), _startTime(0), _stopTime(0) {
  }

  virtual ~NotifLMControl() throw() {}

   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  std::vector<int32_t>  _notifGroupIds;
  int32_t _notifType;
  int32_t _programId;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _startTime;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _stopTime;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
    _baseMessage = val;
  }

  void __set__notifGroupIds(const std::vector<int32_t> & val) {
    _notifGroupIds = val;
  }

  void __set__notifType(const int32_t val) {
    _notifType = val;
  }

  void __set__programId(const int32_t val) {
    _programId = val;
  }

  void __set__startTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
    _startTime = val;
  }

  void __set__stopTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
    _stopTime = val;
  }

  bool operator == (const NotifLMControl & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_notifGroupIds == rhs._notifGroupIds))
      return false;
    if (!(_notifType == rhs._notifType))
      return false;
    if (!(_programId == rhs._programId))
      return false;
    if (!(_startTime == rhs._startTime))
      return false;
    if (!(_stopTime == rhs._stopTime))
      return false;
    return true;
  }
  bool operator != (const NotifLMControl &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const NotifLMControl & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(NotifLMControl &a, NotifLMControl &b);

}}}} // namespace

#endif
