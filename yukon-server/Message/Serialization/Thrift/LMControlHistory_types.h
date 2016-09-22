/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef LMControlHistory_TYPES_H
#define LMControlHistory_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "Message_types.h"
#include "Types_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class LMControlHistory {
 public:

  static const char* ascii_fingerprint; // = "C89A23896D05209BC3AC57BEAD75047E";
  static const uint8_t binary_fingerprint[16]; // = {0xC8,0x9A,0x23,0x89,0x6D,0x05,0x20,0x9B,0xC3,0xAC,0x57,0xBE,0xAD,0x75,0x04,0x7E};

  LMControlHistory() : _paoId(0), _pointId(0), _rawState(0), _startDateTime(0), _controlDuration(0), _reductionRatio(0), _controlType(), _activeRestore(), _reductionValue(0), _controlPriority(0), _associationKey(0) {
  }

  virtual ~LMControlHistory() throw() {}

   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  int32_t _paoId;
  int32_t _pointId;
  int32_t _rawState;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _startDateTime;
  int32_t _controlDuration;
  int32_t _reductionRatio;
  std::string _controlType;
  std::string _activeRestore;
  double _reductionValue;
  int32_t _controlPriority;
  int32_t _associationKey;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
    _baseMessage = val;
  }

  void __set__paoId(const int32_t val) {
    _paoId = val;
  }

  void __set__pointId(const int32_t val) {
    _pointId = val;
  }

  void __set__rawState(const int32_t val) {
    _rawState = val;
  }

  void __set__startDateTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
    _startDateTime = val;
  }

  void __set__controlDuration(const int32_t val) {
    _controlDuration = val;
  }

  void __set__reductionRatio(const int32_t val) {
    _reductionRatio = val;
  }

  void __set__controlType(const std::string& val) {
    _controlType = val;
  }

  void __set__activeRestore(const std::string& val) {
    _activeRestore = val;
  }

  void __set__reductionValue(const double val) {
    _reductionValue = val;
  }

  void __set__controlPriority(const int32_t val) {
    _controlPriority = val;
  }

  void __set__associationKey(const int32_t val) {
    _associationKey = val;
  }

  bool operator == (const LMControlHistory & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_paoId == rhs._paoId))
      return false;
    if (!(_pointId == rhs._pointId))
      return false;
    if (!(_rawState == rhs._rawState))
      return false;
    if (!(_startDateTime == rhs._startDateTime))
      return false;
    if (!(_controlDuration == rhs._controlDuration))
      return false;
    if (!(_reductionRatio == rhs._reductionRatio))
      return false;
    if (!(_controlType == rhs._controlType))
      return false;
    if (!(_activeRestore == rhs._activeRestore))
      return false;
    if (!(_reductionValue == rhs._reductionValue))
      return false;
    if (!(_controlPriority == rhs._controlPriority))
      return false;
    if (!(_associationKey == rhs._associationKey))
      return false;
    return true;
  }
  bool operator != (const LMControlHistory &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const LMControlHistory & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(LMControlHistory &a, LMControlHistory &b);

}}}} // namespace

#endif
