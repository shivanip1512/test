/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef LMMessage_TYPES_H
#define LMMessage_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "Message_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class LMMessage {
 public:

  static const char* ascii_fingerprint; // = "4CA40237596CFEBFB24A4AD998459DC7";
  static const uint8_t binary_fingerprint[16]; // = {0x4C,0xA4,0x02,0x37,0x59,0x6C,0xFE,0xBF,0xB2,0x4A,0x4A,0xD9,0x98,0x45,0x9D,0xC7};

  LMMessage() : _message() {
  }

  virtual ~LMMessage() throw() {}

   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  std::string _message;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
    _baseMessage = val;
  }

  void __set__message(const std::string& val) {
    _message = val;
  }

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

};

void swap(LMMessage &a, LMMessage &b);

}}}} // namespace

#endif
