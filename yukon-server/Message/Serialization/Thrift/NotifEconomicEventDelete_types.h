/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef NotifEconomicEventDelete_TYPES_H
#define NotifEconomicEventDelete_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "Message_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class NotifEconomicEventDelete {
 public:

  static const char* ascii_fingerprint; // = "16AA5E0A19A40A3A8D1575BFFD82B337";
  static const uint8_t binary_fingerprint[16]; // = {0x16,0xAA,0x5E,0x0A,0x19,0xA4,0x0A,0x3A,0x8D,0x15,0x75,0xBF,0xFD,0x82,0xB3,0x37};

  NotifEconomicEventDelete() : _economicEventId(0), _deleteStart(0), _deleteStop(0) {
  }

  virtual ~NotifEconomicEventDelete() throw() {}

   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  int32_t _economicEventId;
  bool _deleteStart;
  bool _deleteStop;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val) {
    _baseMessage = val;
  }

  void __set__economicEventId(const int32_t val) {
    _economicEventId = val;
  }

  void __set__deleteStart(const bool val) {
    _deleteStart = val;
  }

  void __set__deleteStop(const bool val) {
    _deleteStop = val;
  }

  bool operator == (const NotifEconomicEventDelete & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_economicEventId == rhs._economicEventId))
      return false;
    if (!(_deleteStart == rhs._deleteStart))
      return false;
    if (!(_deleteStop == rhs._deleteStop))
      return false;
    return true;
  }
  bool operator != (const NotifEconomicEventDelete &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const NotifEconomicEventDelete & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(NotifEconomicEventDelete &a, NotifEconomicEventDelete &b);

}}}} // namespace

#endif
