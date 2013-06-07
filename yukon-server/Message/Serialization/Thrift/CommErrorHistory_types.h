/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef CommErrorHistory_TYPES_H
#define CommErrorHistory_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "Message_types.h"
#include "Types_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class CommErrorHistory {
 public:

  static const char* ascii_fingerprint; // = "ABC864565CD62AC2D6BF6D6D30C6299D";
  static const uint8_t binary_fingerprint[16]; // = {0xAB,0xC8,0x64,0x56,0x5C,0xD6,0x2A,0xC2,0xD6,0xBF,0x6D,0x6D,0x30,0xC6,0x29,0x9D};

  CommErrorHistory() : _commErrorId(0), _paoId(0), _dateTime(0), _errorType(0), _errorNumber(0), _command(), _outMessage(), _inMessage() {
  }

  virtual ~CommErrorHistory() throw() {}

   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  int32_t _commErrorId;
  int32_t _paoId;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _dateTime;
  int32_t _errorType;
  int32_t _errorNumber;
  std::string _command;
  std::string _outMessage;
  std::string _inMessage;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
    _baseMessage = val;
  }

  void __set__commErrorId(const int32_t val) {
    _commErrorId = val;
  }

  void __set__paoId(const int32_t val) {
    _paoId = val;
  }

  void __set__dateTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
    _dateTime = val;
  }

  void __set__errorType(const int32_t val) {
    _errorType = val;
  }

  void __set__errorNumber(const int32_t val) {
    _errorNumber = val;
  }

  void __set__command(const std::string& val) {
    _command = val;
  }

  void __set__outMessage(const std::string& val) {
    _outMessage = val;
  }

  void __set__inMessage(const std::string& val) {
    _inMessage = val;
  }

  bool operator == (const CommErrorHistory & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_commErrorId == rhs._commErrorId))
      return false;
    if (!(_paoId == rhs._paoId))
      return false;
    if (!(_dateTime == rhs._dateTime))
      return false;
    if (!(_errorType == rhs._errorType))
      return false;
    if (!(_errorNumber == rhs._errorNumber))
      return false;
    if (!(_command == rhs._command))
      return false;
    if (!(_outMessage == rhs._outMessage))
      return false;
    if (!(_inMessage == rhs._inMessage))
      return false;
    return true;
  }
  bool operator != (const CommErrorHistory &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const CommErrorHistory & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(CommErrorHistory &a, CommErrorHistory &b);

}}}} // namespace

#endif
