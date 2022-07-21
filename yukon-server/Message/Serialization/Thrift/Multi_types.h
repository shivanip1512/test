/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef Multi_TYPES_H
#define Multi_TYPES_H

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

class Multi;


class Multi : public virtual ::apache::thrift::TBase {
 public:

  Multi(const Multi&);
  Multi& operator=(const Multi&);
  Multi() noexcept {
  }

  virtual ~Multi() noexcept;
   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  std::vector< ::Cti::Messaging::Serialization::Thrift::GenericMessage>  _bag;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val);

  void __set__bag(const std::vector< ::Cti::Messaging::Serialization::Thrift::GenericMessage> & val);

  bool operator == (const Multi & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_bag == rhs._bag))
      return false;
    return true;
  }
  bool operator != (const Multi &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Multi & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot) override;
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const override;

  virtual void printTo(std::ostream& out) const;
};

void swap(Multi &a, Multi &b);

std::ostream& operator<<(std::ostream& out, const Multi& obj);

}}}} // namespace

#endif
