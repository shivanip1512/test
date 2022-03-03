/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef LMEatonCloudCommandData_TYPES_H
#define LMEatonCloudCommandData_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "Types_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

struct LMEatonCloudCycleType {
  enum type {
    STANDARD = 0,
    TRUE_CYCLE = 1,
    SMART_CYCLE = 2
  };
};

extern const std::map<int, const char*> _LMEatonCloudCycleType_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const LMEatonCloudCycleType::type& val);

std::string to_string(const LMEatonCloudCycleType::type& val);

struct LMEatonCloudStopType {
  enum type {
    RESTORE = 0,
    STOP_CYCLE = 1
  };
};

extern const std::map<int, const char*> _LMEatonCloudStopType_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const LMEatonCloudStopType::type& val);

std::string to_string(const LMEatonCloudStopType::type& val);

class LMEatonCloudScheduledCycleCommand;

class LMEatonCloudStopCommand;


class LMEatonCloudScheduledCycleCommand : public virtual ::apache::thrift::TBase {
 public:

  LMEatonCloudScheduledCycleCommand(const LMEatonCloudScheduledCycleCommand&);
  LMEatonCloudScheduledCycleCommand& operator=(const LMEatonCloudScheduledCycleCommand&);
  LMEatonCloudScheduledCycleCommand() : _groupId(0), _controlStartDateTime(0), _controlEndDateTime(0), _isRampIn(0), _isRampOut(0), _cyclingOption((LMEatonCloudCycleType::type)0), _dutyCyclePercentage(0), _dutyCyclePeriod(0), _criticality(0), _vRelayId(0) {
  }

  virtual ~LMEatonCloudScheduledCycleCommand() noexcept;
  int32_t _groupId;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _controlStartDateTime;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _controlEndDateTime;
  bool _isRampIn;
  bool _isRampOut;
  LMEatonCloudCycleType::type _cyclingOption;
  int32_t _dutyCyclePercentage;
  int32_t _dutyCyclePeriod;
  int32_t _criticality;
  int32_t _vRelayId;

  void __set__groupId(const int32_t val);

  void __set__controlStartDateTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val);

  void __set__controlEndDateTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val);

  void __set__isRampIn(const bool val);

  void __set__isRampOut(const bool val);

  void __set__cyclingOption(const LMEatonCloudCycleType::type val);

  void __set__dutyCyclePercentage(const int32_t val);

  void __set__dutyCyclePeriod(const int32_t val);

  void __set__criticality(const int32_t val);

  void __set__vRelayId(const int32_t val);

  bool operator == (const LMEatonCloudScheduledCycleCommand & rhs) const
  {
    if (!(_groupId == rhs._groupId))
      return false;
    if (!(_controlStartDateTime == rhs._controlStartDateTime))
      return false;
    if (!(_controlEndDateTime == rhs._controlEndDateTime))
      return false;
    if (!(_isRampIn == rhs._isRampIn))
      return false;
    if (!(_isRampOut == rhs._isRampOut))
      return false;
    if (!(_cyclingOption == rhs._cyclingOption))
      return false;
    if (!(_dutyCyclePercentage == rhs._dutyCyclePercentage))
      return false;
    if (!(_dutyCyclePeriod == rhs._dutyCyclePeriod))
      return false;
    if (!(_criticality == rhs._criticality))
      return false;
    if (!(_vRelayId == rhs._vRelayId))
      return false;
    return true;
  }
  bool operator != (const LMEatonCloudScheduledCycleCommand &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const LMEatonCloudScheduledCycleCommand & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(LMEatonCloudScheduledCycleCommand &a, LMEatonCloudScheduledCycleCommand &b);

std::ostream& operator<<(std::ostream& out, const LMEatonCloudScheduledCycleCommand& obj);


class LMEatonCloudStopCommand : public virtual ::apache::thrift::TBase {
 public:

  LMEatonCloudStopCommand(const LMEatonCloudStopCommand&);
  LMEatonCloudStopCommand& operator=(const LMEatonCloudStopCommand&);
  LMEatonCloudStopCommand() : _groupId(0), _restoreTime(0), _stopType((LMEatonCloudStopType::type)0), _vRelayId(0) {
  }

  virtual ~LMEatonCloudStopCommand() noexcept;
  int32_t _groupId;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _restoreTime;
  LMEatonCloudStopType::type _stopType;
  int32_t _vRelayId;

  void __set__groupId(const int32_t val);

  void __set__restoreTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val);

  void __set__stopType(const LMEatonCloudStopType::type val);

  void __set__vRelayId(const int32_t val);

  bool operator == (const LMEatonCloudStopCommand & rhs) const
  {
    if (!(_groupId == rhs._groupId))
      return false;
    if (!(_restoreTime == rhs._restoreTime))
      return false;
    if (!(_stopType == rhs._stopType))
      return false;
    if (!(_vRelayId == rhs._vRelayId))
      return false;
    return true;
  }
  bool operator != (const LMEatonCloudStopCommand &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const LMEatonCloudStopCommand & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(LMEatonCloudStopCommand &a, LMEatonCloudStopCommand &b);

std::ostream& operator<<(std::ostream& out, const LMEatonCloudStopCommand& obj);

}}}} // namespace

#endif
