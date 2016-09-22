/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef MCUpdateSchedule_TYPES_H
#define MCUpdateSchedule_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "Message_types.h"
#include "MCSchedule_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class MCUpdateSchedule {
 public:

  static const char* ascii_fingerprint; // = "DA8D51555DA527AACB238E53366D9687";
  static const uint8_t binary_fingerprint[16]; // = {0xDA,0x8D,0x51,0x55,0x5D,0xA5,0x27,0xAA,0xCB,0x23,0x8E,0x53,0x36,0x6D,0x96,0x87};

  MCUpdateSchedule() : _script() {
  }

  virtual ~MCUpdateSchedule() throw() {}

   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
   ::Cti::Messaging::Serialization::Thrift::MCSchedule _schedule;
  std::string _script;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
    _baseMessage = val;
  }

  void __set__schedule(const  ::Cti::Messaging::Serialization::Thrift::MCSchedule& val) {
    _schedule = val;
  }

  void __set__script(const std::string& val) {
    _script = val;
  }

  bool operator == (const MCUpdateSchedule & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_schedule == rhs._schedule))
      return false;
    if (!(_script == rhs._script))
      return false;
    return true;
  }
  bool operator != (const MCUpdateSchedule &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const MCUpdateSchedule & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(MCUpdateSchedule &a, MCUpdateSchedule &b);

}}}} // namespace

#endif
