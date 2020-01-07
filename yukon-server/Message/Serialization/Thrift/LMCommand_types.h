/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef LMCommand_TYPES_H
#define LMCommand_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "LMMessage_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class LMCommand;


class LMCommand : public virtual ::apache::thrift::TBase {
 public:

  LMCommand(const LMCommand&);
  LMCommand& operator=(const LMCommand&);
  LMCommand() : _command(0), _paoId(0), _number(0), _value(0), _count(0), _auxId(0) {
  }

  virtual ~LMCommand() noexcept;
   ::Cti::Messaging::Serialization::Thrift::LMMessage _baseMessage;
  int32_t _command;
  int32_t _paoId;
  int32_t _number;
  double _value;
  int32_t _count;
  int32_t _auxId;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::LMMessage& val);

  void __set__command(const int32_t val);

  void __set__paoId(const int32_t val);

  void __set__number(const int32_t val);

  void __set__value(const double val);

  void __set__count(const int32_t val);

  void __set__auxId(const int32_t val);

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

  virtual void printTo(std::ostream& out) const;
};

void swap(LMCommand &a, LMCommand &b);

std::ostream& operator<<(std::ostream& out, const LMCommand& obj);

}}}} // namespace

#endif
