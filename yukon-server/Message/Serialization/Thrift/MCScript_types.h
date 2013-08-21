/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef MCScript_TYPES_H
#define MCScript_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "Message_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class MCScript {
 public:

  static const char* ascii_fingerprint; // = "22BE89A886CF1AE8D0B16D8E89D2A78D";
  static const uint8_t binary_fingerprint[16]; // = {0x22,0xBE,0x89,0xA8,0x86,0xCF,0x1A,0xE8,0xD0,0xB1,0x6D,0x8E,0x89,0xD2,0xA7,0x8D};

  MCScript() : _name(), _contents() {
  }

  virtual ~MCScript() throw() {}

   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  std::string _name;
  std::string _contents;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
    _baseMessage = val;
  }

  void __set__name(const std::string& val) {
    _name = val;
  }

  void __set__contents(const std::string& val) {
    _contents = val;
  }

  bool operator == (const MCScript & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_name == rhs._name))
      return false;
    if (!(_contents == rhs._contents))
      return false;
    return true;
  }
  bool operator != (const MCScript &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const MCScript & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(MCScript &a, MCScript &b);

}}}} // namespace

#endif
