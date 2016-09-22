/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef CCVerifyInactiveBanks_TYPES_H
#define CCVerifyInactiveBanks_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "CCVerifyBanks_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class CCVerifyInactiveBanks {
 public:

  static const char* ascii_fingerprint; // = "6F993B99C686AEFF646EF5D3310105CC";
  static const uint8_t binary_fingerprint[16]; // = {0x6F,0x99,0x3B,0x99,0xC6,0x86,0xAE,0xFF,0x64,0x6E,0xF5,0xD3,0x31,0x01,0x05,0xCC};

  CCVerifyInactiveBanks() : _bankInactiveTime(0) {
  }

  virtual ~CCVerifyInactiveBanks() throw() {}

   ::Cti::Messaging::Serialization::Thrift::CCVerifyBanks _baseMessage;
  int32_t _bankInactiveTime;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCVerifyBanks& val) {
    _baseMessage = val;
  }

  void __set__bankInactiveTime(const int32_t val) {
    _bankInactiveTime = val;
  }

  bool operator == (const CCVerifyInactiveBanks & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_bankInactiveTime == rhs._bankInactiveTime))
      return false;
    return true;
  }
  bool operator != (const CCVerifyInactiveBanks &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const CCVerifyInactiveBanks & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(CCVerifyInactiveBanks &a, CCVerifyInactiveBanks &b);

}}}} // namespace

#endif
