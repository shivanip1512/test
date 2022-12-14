/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef CCSubstations_TYPES_H
#define CCSubstations_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "CCMessage_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class CCSubstationItem;

class CCSubstations;


class CCSubstationItem : public virtual ::apache::thrift::TBase {
 public:

  CCSubstationItem(const CCSubstationItem&);
  CCSubstationItem& operator=(const CCSubstationItem&);
  CCSubstationItem() : _parentId(0), _ovUvDisabledFlag(0), _pfDisplayValue(0), _estPfDisplayValue(0), _saEnabledFlag(0), _saEnabledId(0), _voltReductionFlag(0), _recentlyControlledFlag(0), _childVoltReductionFlag(0) {
  }

  virtual ~CCSubstationItem() noexcept;
   ::Cti::Messaging::Serialization::Thrift::CCPao _baseMessage;
  int32_t _parentId;
  bool _ovUvDisabledFlag;
  std::vector<int32_t>  _subBusIds;
  double _pfDisplayValue;
  double _estPfDisplayValue;
  bool _saEnabledFlag;
  int32_t _saEnabledId;
  bool _voltReductionFlag;
  bool _recentlyControlledFlag;
  bool _childVoltReductionFlag;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCPao& val);

  void __set__parentId(const int32_t val);

  void __set__ovUvDisabledFlag(const bool val);

  void __set__subBusIds(const std::vector<int32_t> & val);

  void __set__pfDisplayValue(const double val);

  void __set__estPfDisplayValue(const double val);

  void __set__saEnabledFlag(const bool val);

  void __set__saEnabledId(const int32_t val);

  void __set__voltReductionFlag(const bool val);

  void __set__recentlyControlledFlag(const bool val);

  void __set__childVoltReductionFlag(const bool val);

  bool operator == (const CCSubstationItem & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_parentId == rhs._parentId))
      return false;
    if (!(_ovUvDisabledFlag == rhs._ovUvDisabledFlag))
      return false;
    if (!(_subBusIds == rhs._subBusIds))
      return false;
    if (!(_pfDisplayValue == rhs._pfDisplayValue))
      return false;
    if (!(_estPfDisplayValue == rhs._estPfDisplayValue))
      return false;
    if (!(_saEnabledFlag == rhs._saEnabledFlag))
      return false;
    if (!(_saEnabledId == rhs._saEnabledId))
      return false;
    if (!(_voltReductionFlag == rhs._voltReductionFlag))
      return false;
    if (!(_recentlyControlledFlag == rhs._recentlyControlledFlag))
      return false;
    if (!(_childVoltReductionFlag == rhs._childVoltReductionFlag))
      return false;
    return true;
  }
  bool operator != (const CCSubstationItem &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const CCSubstationItem & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(CCSubstationItem &a, CCSubstationItem &b);

std::ostream& operator<<(std::ostream& out, const CCSubstationItem& obj);


class CCSubstations : public virtual ::apache::thrift::TBase {
 public:

  CCSubstations(const CCSubstations&);
  CCSubstations& operator=(const CCSubstations&);
  CCSubstations() : _msgInfoBitMask(0) {
  }

  virtual ~CCSubstations() noexcept;
   ::Cti::Messaging::Serialization::Thrift::CCMessage _baseMessage;
  int32_t _msgInfoBitMask;
  std::vector<CCSubstationItem>  _ccSubstations;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCMessage& val);

  void __set__msgInfoBitMask(const int32_t val);

  void __set__ccSubstations(const std::vector<CCSubstationItem> & val);

  bool operator == (const CCSubstations & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_msgInfoBitMask == rhs._msgInfoBitMask))
      return false;
    if (!(_ccSubstations == rhs._ccSubstations))
      return false;
    return true;
  }
  bool operator != (const CCSubstations &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const CCSubstations & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(CCSubstations &a, CCSubstations &b);

std::ostream& operator<<(std::ostream& out, const CCSubstations& obj);

}}}} // namespace

#endif
