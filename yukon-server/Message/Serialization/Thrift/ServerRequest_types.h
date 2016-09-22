/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef ServerRequest_TYPES_H
#define ServerRequest_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "Message_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class ServerRequest {
 public:

  static const char* ascii_fingerprint; // = "E1D7897280C9D41E3F66E9A847B7A35A";
  static const uint8_t binary_fingerprint[16]; // = {0xE1,0xD7,0x89,0x72,0x80,0xC9,0xD4,0x1E,0x3F,0x66,0xE9,0xA8,0x47,0xB7,0xA3,0x5A};

  ServerRequest() : _id(0) {
  }

  virtual ~ServerRequest() throw() {}

   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  int32_t _id;
   ::Cti::Messaging::Serialization::Thrift::GenericMessage _payload;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
    _baseMessage = val;
  }

  void __set__id(const int32_t val) {
    _id = val;
  }

  void __set__payload(const  ::Cti::Messaging::Serialization::Thrift::GenericMessage& val) {
    _payload = val;
  }

  bool operator == (const ServerRequest & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_id == rhs._id))
      return false;
    if (!(_payload == rhs._payload))
      return false;
    return true;
  }
  bool operator != (const ServerRequest &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const ServerRequest & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(ServerRequest &a, ServerRequest &b);

}}}} // namespace

#endif
