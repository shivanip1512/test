/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef ServerResponse_TYPES_H
#define ServerResponse_TYPES_H

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

class ServerResponse;


class ServerResponse : public virtual ::apache::thrift::TBase {
 public:

  ServerResponse(const ServerResponse&);
  ServerResponse& operator=(const ServerResponse&);
  ServerResponse() : _id(0), _status(0), _message(), _hasPayload(0) {
  }

  virtual ~ServerResponse() noexcept;
   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  int32_t _id;
  int32_t _status;
  std::string _message;
  bool _hasPayload;
   ::Cti::Messaging::Serialization::Thrift::GenericMessage _payload;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val);

  void __set__id(const int32_t val);

  void __set__status(const int32_t val);

  void __set__message(const std::string& val);

  void __set__hasPayload(const bool val);

  void __set__payload(const  ::Cti::Messaging::Serialization::Thrift::GenericMessage& val);

  bool operator == (const ServerResponse & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_id == rhs._id))
      return false;
    if (!(_status == rhs._status))
      return false;
    if (!(_message == rhs._message))
      return false;
    if (!(_hasPayload == rhs._hasPayload))
      return false;
    if (!(_payload == rhs._payload))
      return false;
    return true;
  }
  bool operator != (const ServerResponse &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const ServerResponse & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(ServerResponse &a, ServerResponse &b);

std::ostream& operator<<(std::ostream& out, const ServerResponse& obj);

}}}} // namespace

#endif
