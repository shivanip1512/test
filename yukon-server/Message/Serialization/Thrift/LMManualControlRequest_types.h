/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef LMManualControlRequest_TYPES_H
#define LMManualControlRequest_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "LMMessage_types.h"
#include "Types_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class LMManualControlRequest {
 public:

  static const char* ascii_fingerprint; // = "67DC6019ED67746C4140FFA375E80620";
  static const uint8_t binary_fingerprint[16]; // = {0x67,0xDC,0x60,0x19,0xED,0x67,0x74,0x6C,0x41,0x40,0xFF,0xA3,0x75,0xE8,0x06,0x20};

  LMManualControlRequest() : _command(0), _paoId(0), _notifyTime(0), _startTime(0), _stopTime(0), _startGear(0), _startPriority(0), _additionalInfo(), _constraintCmd(0) {
  }

  virtual ~LMManualControlRequest() throw() {}

   ::Cti::Messaging::Serialization::Thrift::LMMessage _baseMessage;
  int32_t _command;
  int32_t _paoId;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _notifyTime;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _startTime;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _stopTime;
  int32_t _startGear;
  int32_t _startPriority;
  std::string _additionalInfo;
  int32_t _constraintCmd;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::LMMessage& val) {
    _baseMessage = val;
  }

  void __set__command(const int32_t val) {
    _command = val;
  }

  void __set__paoId(const int32_t val) {
    _paoId = val;
  }

  void __set__notifyTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
    _notifyTime = val;
  }

  void __set__startTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
    _startTime = val;
  }

  void __set__stopTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
    _stopTime = val;
  }

  void __set__startGear(const int32_t val) {
    _startGear = val;
  }

  void __set__startPriority(const int32_t val) {
    _startPriority = val;
  }

  void __set__additionalInfo(const std::string& val) {
    _additionalInfo = val;
  }

  void __set__constraintCmd(const int32_t val) {
    _constraintCmd = val;
  }

  bool operator == (const LMManualControlRequest & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_command == rhs._command))
      return false;
    if (!(_paoId == rhs._paoId))
      return false;
    if (!(_notifyTime == rhs._notifyTime))
      return false;
    if (!(_startTime == rhs._startTime))
      return false;
    if (!(_stopTime == rhs._stopTime))
      return false;
    if (!(_startGear == rhs._startGear))
      return false;
    if (!(_startPriority == rhs._startPriority))
      return false;
    if (!(_additionalInfo == rhs._additionalInfo))
      return false;
    if (!(_constraintCmd == rhs._constraintCmd))
      return false;
    return true;
  }
  bool operator != (const LMManualControlRequest &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const LMManualControlRequest & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(LMManualControlRequest &a, LMManualControlRequest &b);

}}}} // namespace

#endif
