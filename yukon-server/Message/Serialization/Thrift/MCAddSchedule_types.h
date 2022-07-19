/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef MCAddSchedule_TYPES_H
#define MCAddSchedule_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "Message_types.h"
#include "MCSchedule_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class MCAddSchedule;


class MCAddSchedule : public virtual ::apache::thrift::TBase {
 public:

  MCAddSchedule(const MCAddSchedule&);
  MCAddSchedule& operator=(const MCAddSchedule&);
  MCAddSchedule() noexcept
                : _script() {
  }

  virtual ~MCAddSchedule() noexcept;
   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
   ::Cti::Messaging::Serialization::Thrift::MCSchedule _schedule;
  std::string _script;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val);

  void __set__schedule(const  ::Cti::Messaging::Serialization::Thrift::MCSchedule& val);

  void __set__script(const std::string& val);

  bool operator == (const MCAddSchedule & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_schedule == rhs._schedule))
      return false;
    if (!(_script == rhs._script))
      return false;
    return true;
  }
  bool operator != (const MCAddSchedule &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const MCAddSchedule & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot) override;
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const override;

  virtual void printTo(std::ostream& out) const;
};

void swap(MCAddSchedule &a, MCAddSchedule &b);

std::ostream& operator<<(std::ostream& out, const MCAddSchedule& obj);

}}}} // namespace

#endif
