/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef LMDynamicTriggerData_TYPES_H
#define LMDynamicTriggerData_TYPES_H

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

class LMDynamicTriggerData;


class LMDynamicTriggerData : public virtual ::apache::thrift::TBase {
 public:

  LMDynamicTriggerData(const LMDynamicTriggerData&);
  LMDynamicTriggerData& operator=(const LMDynamicTriggerData&);
  LMDynamicTriggerData() : _paoId(0), _triggerNumber(0), _pointValue(0), _lastPointValueTimestamp(0), _normalState(0), _threshold(0), _peakPointValue(0), _lastPeakPointValueTimestamp(0), _projectedPointValue(0) {
  }

  virtual ~LMDynamicTriggerData() noexcept;
  int32_t _paoId;
  int32_t _triggerNumber;
  double _pointValue;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _lastPointValueTimestamp;
  int32_t _normalState;
  double _threshold;
  double _peakPointValue;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _lastPeakPointValueTimestamp;
  double _projectedPointValue;

  void __set__paoId(const int32_t val);

  void __set__triggerNumber(const int32_t val);

  void __set__pointValue(const double val);

  void __set__lastPointValueTimestamp(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val);

  void __set__normalState(const int32_t val);

  void __set__threshold(const double val);

  void __set__peakPointValue(const double val);

  void __set__lastPeakPointValueTimestamp(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val);

  void __set__projectedPointValue(const double val);

  bool operator == (const LMDynamicTriggerData & rhs) const
  {
    if (!(_paoId == rhs._paoId))
      return false;
    if (!(_triggerNumber == rhs._triggerNumber))
      return false;
    if (!(_pointValue == rhs._pointValue))
      return false;
    if (!(_lastPointValueTimestamp == rhs._lastPointValueTimestamp))
      return false;
    if (!(_normalState == rhs._normalState))
      return false;
    if (!(_threshold == rhs._threshold))
      return false;
    if (!(_peakPointValue == rhs._peakPointValue))
      return false;
    if (!(_lastPeakPointValueTimestamp == rhs._lastPeakPointValueTimestamp))
      return false;
    if (!(_projectedPointValue == rhs._projectedPointValue))
      return false;
    return true;
  }
  bool operator != (const LMDynamicTriggerData &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const LMDynamicTriggerData & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(LMDynamicTriggerData &a, LMDynamicTriggerData &b);

std::ostream& operator<<(std::ostream& out, const LMDynamicTriggerData& obj);

}}}} // namespace

#endif
