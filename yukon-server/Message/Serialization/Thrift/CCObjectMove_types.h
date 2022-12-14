/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef CCObjectMove_TYPES_H
#define CCObjectMove_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "CCMessage_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class CCObjectMove;


class CCObjectMove : public virtual ::apache::thrift::TBase {
 public:

  CCObjectMove(const CCObjectMove&);
  CCObjectMove& operator=(const CCObjectMove&);
  CCObjectMove() : _permanentFlag(0), _oldParentId(0), _objectId(0), _newParentId(0), _switchingOrder(0), _closeOrder(0), _tripOrder(0) {
  }

  virtual ~CCObjectMove() noexcept;
   ::Cti::Messaging::Serialization::Thrift::CCMessage _baseMessage;
  bool _permanentFlag;
  int32_t _oldParentId;
  int32_t _objectId;
  int32_t _newParentId;
  double _switchingOrder;
  double _closeOrder;
  double _tripOrder;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCMessage& val);

  void __set__permanentFlag(const bool val);

  void __set__oldParentId(const int32_t val);

  void __set__objectId(const int32_t val);

  void __set__newParentId(const int32_t val);

  void __set__switchingOrder(const double val);

  void __set__closeOrder(const double val);

  void __set__tripOrder(const double val);

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

  virtual void printTo(std::ostream& out) const;
};

void swap(CCObjectMove &a, CCObjectMove &b);

std::ostream& operator<<(std::ostream& out, const CCObjectMove& obj);

}}}} // namespace

#endif
