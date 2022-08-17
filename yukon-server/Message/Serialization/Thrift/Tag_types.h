/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef Tag_TYPES_H
#define Tag_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "Message_types.h"
#include "Types_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class Tag;


class Tag : public virtual ::apache::thrift::TBase {
 public:

  Tag(const Tag&);
  Tag& operator=(const Tag&);
  Tag() : _instanceId(0), _pointId(0), _tagId(0), _descriptionStr(), _action(0), _tagTime(0), _referenceStr(), _taggedForStr(), _clientMsgId(0) {
  }

  virtual ~Tag() noexcept;
   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  int32_t _instanceId;
  int32_t _pointId;
  int32_t _tagId;
  std::string _descriptionStr;
  int32_t _action;
   ::Cti::Messaging::Serialization::Thrift::Timestamp _tagTime;
  std::string _referenceStr;
  std::string _taggedForStr;
  int32_t _clientMsgId;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val);

  void __set__instanceId(const int32_t val);

  void __set__pointId(const int32_t val);

  void __set__tagId(const int32_t val);

  void __set__descriptionStr(const std::string& val);

  void __set__action(const int32_t val);

  void __set__tagTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val);

  void __set__referenceStr(const std::string& val);

  void __set__taggedForStr(const std::string& val);

  void __set__clientMsgId(const int32_t val);

  bool operator == (const Tag & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_instanceId == rhs._instanceId))
      return false;
    if (!(_pointId == rhs._pointId))
      return false;
    if (!(_tagId == rhs._tagId))
      return false;
    if (!(_descriptionStr == rhs._descriptionStr))
      return false;
    if (!(_action == rhs._action))
      return false;
    if (!(_tagTime == rhs._tagTime))
      return false;
    if (!(_referenceStr == rhs._referenceStr))
      return false;
    if (!(_taggedForStr == rhs._taggedForStr))
      return false;
    if (!(_clientMsgId == rhs._clientMsgId))
      return false;
    return true;
  }
  bool operator != (const Tag &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Tag & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(Tag &a, Tag &b);

std::ostream& operator<<(std::ostream& out, const Tag& obj);

}}}} // namespace

#endif
