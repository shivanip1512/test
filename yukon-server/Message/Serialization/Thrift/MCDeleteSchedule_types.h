/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef MCDeleteSchedule_TYPES_H
#define MCDeleteSchedule_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "Message_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class MCDeleteSchedule {
 public:

  static const char* ascii_fingerprint; // = "002C46FA3FE9175635CCC62EB54AEA66";
  static const uint8_t binary_fingerprint[16]; // = {0x00,0x2C,0x46,0xFA,0x3F,0xE9,0x17,0x56,0x35,0xCC,0xC6,0x2E,0xB5,0x4A,0xEA,0x66};

  MCDeleteSchedule() : _id(0) {
  }

  virtual ~MCDeleteSchedule() throw() {}

   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  int32_t _id;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
    _baseMessage = val;
  }

  void __set__id(const int32_t val) {
    _id = val;
  }

  bool operator == (const MCDeleteSchedule & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_id == rhs._id))
      return false;
    return true;
  }
  bool operator != (const MCDeleteSchedule &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const MCDeleteSchedule & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(MCDeleteSchedule &a, MCDeleteSchedule &b);

}}}} // namespace

#endif
