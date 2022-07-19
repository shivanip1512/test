/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef MCDeleteSchedule_TYPES_H
#define MCDeleteSchedule_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "Message_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class MCDeleteSchedule;


class MCDeleteSchedule : public virtual ::apache::thrift::TBase {
 public:

  MCDeleteSchedule(const MCDeleteSchedule&);
  MCDeleteSchedule& operator=(const MCDeleteSchedule&);
  MCDeleteSchedule() noexcept
                   : _id(0) {
  }

  virtual ~MCDeleteSchedule() noexcept;
   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  int32_t _id;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val);

  void __set__id(const int32_t val);

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

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot) override;
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const override;

  virtual void printTo(std::ostream& out) const;
};

void swap(MCDeleteSchedule &a, MCDeleteSchedule &b);

std::ostream& operator<<(std::ostream& out, const MCDeleteSchedule& obj);

}}}} // namespace

#endif
