/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef LMCommand_TYPES_H
#define LMCommand_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "LMMessage_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class LMCommand {
 public:

  static const char* ascii_fingerprint; // = "28C3B7B7507AE231E3ABC1D4903DC781";
  static const uint8_t binary_fingerprint[16]; // = {0x28,0xC3,0xB7,0xB7,0x50,0x7A,0xE2,0x31,0xE3,0xAB,0xC1,0xD4,0x90,0x3D,0xC7,0x81};

  LMCommand() : _command(0), _paoId(0), _number(0), _value(0), _count(0), _auxId(0) {
  }

  virtual ~LMCommand() throw() {}

   ::Cti::Messaging::Serialization::Thrift::LMMessage _baseMessage;
  int32_t _command;
  int32_t _paoId;
  int32_t _number;
  double _value;
  int32_t _count;
  int32_t _auxId;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::LMMessage& val) {
    _baseMessage = val;
  }

  void __set__command(const int32_t val) {
    _command = val;
  }

  void __set__paoId(const int32_t val) {
    _paoId = val;
  }

  void __set__number(const int32_t val) {
    _number = val;
  }

  void __set__value(const double val) {
    _value = val;
  }

  void __set__count(const int32_t val) {
    _count = val;
  }

  void __set__auxId(const int32_t val) {
    _auxId = val;
  }

  bool operator == (const LMCommand & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_command == rhs._command))
      return false;
    if (!(_paoId == rhs._paoId))
      return false;
    if (!(_number == rhs._number))
      return false;
    if (!(_value == rhs._value))
      return false;
    if (!(_count == rhs._count))
      return false;
    if (!(_auxId == rhs._auxId))
      return false;
    return true;
  }
  bool operator != (const LMCommand &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const LMCommand & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(LMCommand &a, LMCommand &b);

}}}} // namespace

#endif
