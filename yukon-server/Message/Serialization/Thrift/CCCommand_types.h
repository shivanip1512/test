/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef CCCommand_TYPES_H
#define CCCommand_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "Message_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class CCCommand;


class CCCommand : public virtual ::apache::thrift::TBase {
 public:

  CCCommand(const CCCommand&);
  CCCommand& operator=(const CCCommand&);
  CCCommand() : _messageId(0), _commandId(0) {
  }

  virtual ~CCCommand() noexcept;
   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  int32_t _messageId;
  int32_t _commandId;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val);

  void __set__messageId(const int32_t val);

  void __set__commandId(const int32_t val);

  bool operator == (const CCCommand & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_messageId == rhs._messageId))
      return false;
    if (!(_commandId == rhs._commandId))
      return false;
    return true;
  }
  bool operator != (const CCCommand &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const CCCommand & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(CCCommand &a, CCCommand &b);

std::ostream& operator<<(std::ostream& out, const CCCommand& obj);

}}}} // namespace

#endif
