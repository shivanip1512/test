/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef NotifVoiceDataRequest_TYPES_H
#define NotifVoiceDataRequest_TYPES_H

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

class NotifVoiceDataRequest;


class NotifVoiceDataRequest : public virtual ::apache::thrift::TBase {
 public:

  NotifVoiceDataRequest(const NotifVoiceDataRequest&);
  NotifVoiceDataRequest& operator=(const NotifVoiceDataRequest&);
  NotifVoiceDataRequest() : _callToken() {
  }

  virtual ~NotifVoiceDataRequest() noexcept;
   ::Cti::Messaging::Serialization::Thrift::Message _baseMessage;
  std::string _callToken;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Message& val);

  void __set__callToken(const std::string& val);

  bool operator == (const NotifVoiceDataRequest & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_callToken == rhs._callToken))
      return false;
    return true;
  }
  bool operator != (const NotifVoiceDataRequest &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const NotifVoiceDataRequest & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(NotifVoiceDataRequest &a, NotifVoiceDataRequest &b);

std::ostream& operator<<(std::ostream& out, const NotifVoiceDataRequest& obj);

}}}} // namespace

#endif
