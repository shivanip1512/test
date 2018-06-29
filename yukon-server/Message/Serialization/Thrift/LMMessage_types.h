/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef LMMessage_TYPES_H
#define LMMessage_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <thrift/stdcxx.h>
#include "Message_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class LMMessage;


class LMMessage : public virtual ::apache::thrift::TBase {
 public:

  LMMessage(const LMMessage&);
  LMMessage& operator=(const LMMessage&);
  LMMessage() : _message() {
  }

  virtual ~LMMessage() throw();
   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  std::string _message;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val);

  void __set__message(const std::string& val);

  bool operator == (const LMMessage & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_message == rhs._message))
      return false;
    return true;
  }
  bool operator != (const LMMessage &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const LMMessage & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(LMMessage &a, LMMessage &b);

std::ostream& operator<<(std::ostream& out, const LMMessage& obj);

}}}} // namespace

#endif
