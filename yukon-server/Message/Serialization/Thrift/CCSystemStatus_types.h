/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef CCSystemStatus_TYPES_H
#define CCSystemStatus_TYPES_H

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

class CCSystemStatus;


class CCSystemStatus : public virtual ::apache::thrift::TBase {
 public:

  CCSystemStatus(const CCSystemStatus&);
  CCSystemStatus& operator=(const CCSystemStatus&);
  CCSystemStatus() : _systemState(0) {
  }

  virtual ~CCSystemStatus() noexcept;
   ::Cti::Messaging::Serialization::Thrift::CCMessage _baseMessage;
  bool _systemState;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCMessage& val);

  void __set__systemState(const bool val);

  bool operator == (const CCSystemStatus & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_systemState == rhs._systemState))
      return false;
    return true;
  }
  bool operator != (const CCSystemStatus &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const CCSystemStatus & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(CCSystemStatus &a, CCSystemStatus &b);

std::ostream& operator<<(std::ostream& out, const CCSystemStatus& obj);

}}}} // namespace

#endif
