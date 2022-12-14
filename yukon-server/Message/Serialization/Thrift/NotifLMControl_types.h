/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef NotifLMControl_TYPES_H
#define NotifLMControl_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "Message_types.h"
#include "Types_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class NotifLMControl;


class NotifLMControl : public virtual ::apache::thrift::TBase {
 public:

  NotifLMControl(const NotifLMControl&);
  NotifLMControl& operator=(const NotifLMControl&);
  NotifLMControl() : _notifType(0), _programId(0), _startTime(0), _stopTime(0) {
  }

  virtual ~NotifLMControl() noexcept;
   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  std::vector<int32_t>  _notifGroupIds;
  int32_t _notifType;
  int32_t _programId;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _startTime;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _stopTime;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val);

  void __set__notifGroupIds(const std::vector<int32_t> & val);

  void __set__notifType(const int32_t val);

  void __set__programId(const int32_t val);

  void __set__startTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val);

  void __set__stopTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val);

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

  virtual void printTo(std::ostream& out) const;
};

void swap(NotifLMControl &a, NotifLMControl &b);

std::ostream& operator<<(std::ostream& out, const NotifLMControl& obj);

}}}} // namespace

#endif
