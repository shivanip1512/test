/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef CCVerifySelectedBank_TYPES_H
#define CCVerifySelectedBank_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "CCVerifyBanks_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class CCVerifySelectedBank;


class CCVerifySelectedBank : public virtual ::apache::thrift::TBase {
 public:

  CCVerifySelectedBank(const CCVerifySelectedBank&);
  CCVerifySelectedBank& operator=(const CCVerifySelectedBank&);
  CCVerifySelectedBank() : _bankId(0) {
  }

  virtual ~CCVerifySelectedBank() noexcept;
   ::Cti::Messaging::Serialization::Thrift::CCVerifyBanks _baseMessage;
  int32_t _bankId;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCVerifyBanks& val);

  void __set__bankId(const int32_t val);

  bool operator == (const CCVerifySelectedBank & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_bankId == rhs._bankId))
      return false;
    return true;
  }
  bool operator != (const CCVerifySelectedBank &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const CCVerifySelectedBank & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(CCVerifySelectedBank &a, CCVerifySelectedBank &b);

std::ostream& operator<<(std::ostream& out, const CCVerifySelectedBank& obj);

}}}} // namespace

#endif
