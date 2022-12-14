/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef Message_TYPES_H
#define Message_TYPES_H

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

class Message;

class GenericMessage;


class Message : public virtual ::apache::thrift::TBase {
 public:

  Message(const Message&);
  Message& operator=(const Message&);
  Message() : _messageTime(0), _messagePriority(0), _soe(0), _usr(), _src() {
  }

  virtual ~Message() noexcept;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _messageTime;
  int32_t _messagePriority;
  int32_t _soe;
  std::string _usr;
  std::string _src;

  void __set__messageTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val);

  void __set__messagePriority(const int32_t val);

  void __set__soe(const int32_t val);

  void __set__usr(const std::string& val);

  void __set__src(const std::string& val);

  bool operator == (const Message & rhs) const
  {
    if (!(_messageTime == rhs._messageTime))
      return false;
    if (!(_messagePriority == rhs._messagePriority))
      return false;
    if (!(_soe == rhs._soe))
      return false;
    if (!(_usr == rhs._usr))
      return false;
    if (!(_src == rhs._src))
      return false;
    return true;
  }
  bool operator != (const Message &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Message & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(Message &a, Message &b);

std::ostream& operator<<(std::ostream& out, const Message& obj);


class GenericMessage : public virtual ::apache::thrift::TBase {
 public:

  GenericMessage(const GenericMessage&);
  GenericMessage& operator=(const GenericMessage&);
  GenericMessage() : _messageType(), _payload() {
  }

  virtual ~GenericMessage() noexcept;
  std::string _messageType;
  std::string _payload;

  void __set__messageType(const std::string& val);

  void __set__payload(const std::string& val);

  bool operator == (const GenericMessage & rhs) const
  {
    if (!(_messageType == rhs._messageType))
      return false;
    if (!(_payload == rhs._payload))
      return false;
    return true;
  }
  bool operator != (const GenericMessage &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const GenericMessage & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(GenericMessage &a, GenericMessage &b);

std::ostream& operator<<(std::ostream& out, const GenericMessage& obj);

}}}} // namespace

#endif
