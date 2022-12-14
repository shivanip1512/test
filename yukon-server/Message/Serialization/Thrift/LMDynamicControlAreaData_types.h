/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef LMDynamicControlAreaData_TYPES_H
#define LMDynamicControlAreaData_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "LMDynamicTriggerData_types.h"
#include "Types_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class LMDynamicControlAreaData;


class LMDynamicControlAreaData : public virtual ::apache::thrift::TBase {
 public:

  LMDynamicControlAreaData(const LMDynamicControlAreaData&);
  LMDynamicControlAreaData& operator=(const LMDynamicControlAreaData&);
  LMDynamicControlAreaData() : _paoId(0), _disableFlag(0), _nextCheckTime(0), _controlAreaState(0), _currentPriority(0), _currentDailyStartTime(0), _currentDailyStopTime(0) {
  }

  virtual ~LMDynamicControlAreaData() noexcept;
  int32_t _paoId;
  int32_t _disableFlag;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _nextCheckTime;
  int32_t _controlAreaState;
  int32_t _currentPriority;
  int32_t _currentDailyStartTime;
  int32_t _currentDailyStopTime;
  std::vector< ::Cti::Messaging::Serialization::Thrift::LMDynamicTriggerData>  _triggers;

  void __set__paoId(const int32_t val);

  void __set__disableFlag(const int32_t val);

  void __set__nextCheckTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val);

  void __set__controlAreaState(const int32_t val);

  void __set__currentPriority(const int32_t val);

  void __set__currentDailyStartTime(const int32_t val);

  void __set__currentDailyStopTime(const int32_t val);

  void __set__triggers(const std::vector< ::Cti::Messaging::Serialization::Thrift::LMDynamicTriggerData> & val);

  bool operator == (const LMDynamicControlAreaData & rhs) const
  {
    if (!(_paoId == rhs._paoId))
      return false;
    if (!(_disableFlag == rhs._disableFlag))
      return false;
    if (!(_nextCheckTime == rhs._nextCheckTime))
      return false;
    if (!(_controlAreaState == rhs._controlAreaState))
      return false;
    if (!(_currentPriority == rhs._currentPriority))
      return false;
    if (!(_currentDailyStartTime == rhs._currentDailyStartTime))
      return false;
    if (!(_currentDailyStopTime == rhs._currentDailyStopTime))
      return false;
    if (!(_triggers == rhs._triggers))
      return false;
    return true;
  }
  bool operator != (const LMDynamicControlAreaData &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const LMDynamicControlAreaData & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(LMDynamicControlAreaData &a, LMDynamicControlAreaData &b);

std::ostream& operator<<(std::ostream& out, const LMDynamicControlAreaData& obj);

}}}} // namespace

#endif
