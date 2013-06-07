/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef CCDynamicCommand_TYPES_H
#define CCDynamicCommand_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "CCCommand_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class CCDynamicCommand {
 public:

  static const char* ascii_fingerprint; // = "9F750C300C26547A6C6FE9A0996F7551";
  static const uint8_t binary_fingerprint[16]; // = {0x9F,0x75,0x0C,0x30,0x0C,0x26,0x54,0x7A,0x6C,0x6F,0xE9,0xA0,0x99,0x6F,0x75,0x51};

  CCDynamicCommand() : _commandType(0) {
  }

  virtual ~CCDynamicCommand() throw() {}

   ::Cti::Messaging::Serialization::Thrift::CCCommand _baseMessage;
  int32_t _commandType;
  std::map<int32_t, int32_t>  _longParameters;
  std::map<int32_t, double>  _doubleParameters;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCCommand& val) {
    _baseMessage = val;
  }

  void __set__commandType(const int32_t val) {
    _commandType = val;
  }

  void __set__longParameters(const std::map<int32_t, int32_t> & val) {
    _longParameters = val;
  }

  void __set__doubleParameters(const std::map<int32_t, double> & val) {
    _doubleParameters = val;
  }

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

};

void swap(CCDynamicCommand &a, CCDynamicCommand &b);

}}}} // namespace

#endif
