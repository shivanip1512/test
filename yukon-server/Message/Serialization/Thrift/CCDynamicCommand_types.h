/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef CCDynamicCommand_TYPES_H
#define CCDynamicCommand_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "CCCommand_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class CCDynamicCommand;


class CCDynamicCommand : public virtual ::apache::thrift::TBase {
 public:

  CCDynamicCommand(const CCDynamicCommand&);
  CCDynamicCommand& operator=(const CCDynamicCommand&);
  CCDynamicCommand() : _commandType(0) {
  }

  virtual ~CCDynamicCommand() noexcept;
   ::Cti::Messaging::Serialization::Thrift::CCCommand _baseMessage;
  int32_t _commandType;
  std::map<int32_t, int32_t>  _longParameters;
  std::map<int32_t, double>  _doubleParameters;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCCommand& val);

  void __set__commandType(const int32_t val);

  void __set__longParameters(const std::map<int32_t, int32_t> & val);

  void __set__doubleParameters(const std::map<int32_t, double> & val);

  bool operator == (const CCDynamicCommand & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_commandType == rhs._commandType))
      return false;
    if (!(_longParameters == rhs._longParameters))
      return false;
    if (!(_doubleParameters == rhs._doubleParameters))
      return false;
    return true;
  }
  bool operator != (const CCDynamicCommand &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const CCDynamicCommand & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(CCDynamicCommand &a, CCDynamicCommand &b);

std::ostream& operator<<(std::ostream& out, const CCDynamicCommand& obj);

}}}} // namespace

#endif
