/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef CCObjectMove_TYPES_H
#define CCObjectMove_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "CCMessage_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class CCObjectMove {
 public:

  static const char* ascii_fingerprint; // = "FB3360CB613BBC42D2DDBC5F23D09D01";
  static const uint8_t binary_fingerprint[16]; // = {0xFB,0x33,0x60,0xCB,0x61,0x3B,0xBC,0x42,0xD2,0xDD,0xBC,0x5F,0x23,0xD0,0x9D,0x01};

  CCObjectMove() : _permanentFlag(0), _oldParentId(0), _objectId(0), _newParentId(0), _switchingOrder(0), _closeOrder(0), _tripOrder(0) {
  }

  virtual ~CCObjectMove() throw() {}

   ::Cti::Messaging::Serialization::Thrift::CCMessage _baseMessage;
  bool _permanentFlag;
  int32_t _oldParentId;
  int32_t _objectId;
  int32_t _newParentId;
  double _switchingOrder;
  double _closeOrder;
  double _tripOrder;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCMessage& val) {
    _baseMessage = val;
  }

  void __set__permanentFlag(const bool val) {
    _permanentFlag = val;
  }

  void __set__oldParentId(const int32_t val) {
    _oldParentId = val;
  }

  void __set__objectId(const int32_t val) {
    _objectId = val;
  }

  void __set__newParentId(const int32_t val) {
    _newParentId = val;
  }

  void __set__switchingOrder(const double val) {
    _switchingOrder = val;
  }

  void __set__closeOrder(const double val) {
    _closeOrder = val;
  }

  void __set__tripOrder(const double val) {
    _tripOrder = val;
  }

  bool operator == (const CCObjectMove & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_permanentFlag == rhs._permanentFlag))
      return false;
    if (!(_oldParentId == rhs._oldParentId))
      return false;
    if (!(_objectId == rhs._objectId))
      return false;
    if (!(_newParentId == rhs._newParentId))
      return false;
    if (!(_switchingOrder == rhs._switchingOrder))
      return false;
    if (!(_closeOrder == rhs._closeOrder))
      return false;
    if (!(_tripOrder == rhs._tripOrder))
      return false;
    return true;
  }
  bool operator != (const CCObjectMove &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const CCObjectMove & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(CCObjectMove &a, CCObjectMove &b);

}}}} // namespace

#endif
