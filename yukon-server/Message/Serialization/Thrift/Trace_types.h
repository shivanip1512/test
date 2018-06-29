/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef Trace_TYPES_H
#define Trace_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <thrift/stdcxx.h>
#include "Message_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class Trace;


class Trace : public virtual ::apache::thrift::TBase {
 public:

  Trace(const Trace&);
  Trace& operator=(const Trace&);
  Trace() : _end(0), _attributes(0), _trace() {
  }

  virtual ~Trace() throw();
   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  bool _end;
  int32_t _attributes;
  std::string _trace;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val);

  void __set__end(const bool val);

  void __set__attributes(const int32_t val);

  void __set__trace(const std::string& val);

  bool operator == (const Trace & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_end == rhs._end))
      return false;
    if (!(_attributes == rhs._attributes))
      return false;
    if (!(_trace == rhs._trace))
      return false;
    return true;
  }
  bool operator != (const Trace &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Trace & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(Trace &a, Trace &b);

std::ostream& operator<<(std::ostream& out, const Trace& obj);

}}}} // namespace

#endif
