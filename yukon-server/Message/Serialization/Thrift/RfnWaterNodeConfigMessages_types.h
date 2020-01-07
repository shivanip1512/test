/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef RfnWaterNodeConfigMessages_TYPES_H
#define RfnWaterNodeConfigMessages_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "Types_types.h"
#include "RfnE2eData_types.h"
#include "NetworkManagerMessaging_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

struct SetChannelConfigReplyType {
  enum type {
    SUCCESS = 0,
    INVALID_DEVICE = 1,
    NO_NODE = 2,
    NO_GATEWAY = 3,
    FAILURE = 4
  };
};

extern const std::map<int, const char*> _SetChannelConfigReplyType_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const SetChannelConfigReplyType::type& val);

std::string to_string(const SetChannelConfigReplyType::type& val);

struct GetChannelConfigReplyType {
  enum type {
    SUCCESS = 0,
    INVALID_DEVICE = 1,
    NO_NODE = 2,
    FAILURE = 3
  };
};

extern const std::map<int, const char*> _GetChannelConfigReplyType_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const GetChannelConfigReplyType::type& val);

std::string to_string(const GetChannelConfigReplyType::type& val);

class RfnSetChannelConfigRequest;

class RfnSetChannelConfigReply;

class RfnGetChannelConfigRequest;

class ChannelInfo;

class RfnGetChannelConfigReply;

typedef struct _RfnSetChannelConfigRequest__isset {
  _RfnSetChannelConfigRequest__isset() : header(false) {}
  bool header :1;
} _RfnSetChannelConfigRequest__isset;

class RfnSetChannelConfigRequest : public virtual ::apache::thrift::TBase {
 public:

  RfnSetChannelConfigRequest(const RfnSetChannelConfigRequest&);
  RfnSetChannelConfigRequest& operator=(const RfnSetChannelConfigRequest&);
  RfnSetChannelConfigRequest() : reportingInterval(0), recordingInterval(0) {
  }

  virtual ~RfnSetChannelConfigRequest() noexcept;
   ::Cti::Messaging::Serialization::Thrift::RfnIdentifier rfnIdentifier;
  int32_t reportingInterval;
  int32_t recordingInterval;
   ::Cti::Messaging::Serialization::Thrift::NetworkManagerRequestHeader header;

  _RfnSetChannelConfigRequest__isset __isset;

  void __set_rfnIdentifier(const  ::Cti::Messaging::Serialization::Thrift::RfnIdentifier& val);

  void __set_reportingInterval(const int32_t val);

  void __set_recordingInterval(const int32_t val);

  void __set_header(const  ::Cti::Messaging::Serialization::Thrift::NetworkManagerRequestHeader& val);

  bool operator == (const RfnSetChannelConfigRequest & rhs) const
  {
    if (!(rfnIdentifier == rhs.rfnIdentifier))
      return false;
    if (!(reportingInterval == rhs.reportingInterval))
      return false;
    if (!(recordingInterval == rhs.recordingInterval))
      return false;
    if (__isset.header != rhs.__isset.header)
      return false;
    else if (__isset.header && !(header == rhs.header))
      return false;
    return true;
  }
  bool operator != (const RfnSetChannelConfigRequest &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const RfnSetChannelConfigRequest & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(RfnSetChannelConfigRequest &a, RfnSetChannelConfigRequest &b);

std::ostream& operator<<(std::ostream& out, const RfnSetChannelConfigRequest& obj);


class RfnSetChannelConfigReply : public virtual ::apache::thrift::TBase {
 public:

  RfnSetChannelConfigReply(const RfnSetChannelConfigReply&);
  RfnSetChannelConfigReply& operator=(const RfnSetChannelConfigReply&);
  RfnSetChannelConfigReply() : reply((SetChannelConfigReplyType::type)0) {
  }

  virtual ~RfnSetChannelConfigReply() noexcept;
  SetChannelConfigReplyType::type reply;
   ::Cti::Messaging::Serialization::Thrift::RfnIdentifier rfnIdentifier;

  void __set_reply(const SetChannelConfigReplyType::type val);

  void __set_rfnIdentifier(const  ::Cti::Messaging::Serialization::Thrift::RfnIdentifier& val);

  bool operator == (const RfnSetChannelConfigReply & rhs) const
  {
    if (!(reply == rhs.reply))
      return false;
    if (!(rfnIdentifier == rhs.rfnIdentifier))
      return false;
    return true;
  }
  bool operator != (const RfnSetChannelConfigReply &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const RfnSetChannelConfigReply & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(RfnSetChannelConfigReply &a, RfnSetChannelConfigReply &b);

std::ostream& operator<<(std::ostream& out, const RfnSetChannelConfigReply& obj);


class RfnGetChannelConfigRequest : public virtual ::apache::thrift::TBase {
 public:

  RfnGetChannelConfigRequest(const RfnGetChannelConfigRequest&);
  RfnGetChannelConfigRequest& operator=(const RfnGetChannelConfigRequest&);
  RfnGetChannelConfigRequest() {
  }

  virtual ~RfnGetChannelConfigRequest() noexcept;
   ::Cti::Messaging::Serialization::Thrift::RfnIdentifier rfnIdentifier;

  void __set_rfnIdentifier(const  ::Cti::Messaging::Serialization::Thrift::RfnIdentifier& val);

  bool operator == (const RfnGetChannelConfigRequest & rhs) const
  {
    if (!(rfnIdentifier == rhs.rfnIdentifier))
      return false;
    return true;
  }
  bool operator != (const RfnGetChannelConfigRequest &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const RfnGetChannelConfigRequest & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(RfnGetChannelConfigRequest &a, RfnGetChannelConfigRequest &b);

std::ostream& operator<<(std::ostream& out, const RfnGetChannelConfigRequest& obj);


class ChannelInfo : public virtual ::apache::thrift::TBase {
 public:

  ChannelInfo(const ChannelInfo&);
  ChannelInfo& operator=(const ChannelInfo&);
  ChannelInfo() : UOM(), channelNum(0), enabled(0) {
  }

  virtual ~ChannelInfo() noexcept;
  std::string UOM;
  std::set<std::string>  uomModifier;
  int16_t channelNum;
  bool enabled;

  void __set_UOM(const std::string& val);

  void __set_uomModifier(const std::set<std::string> & val);

  void __set_channelNum(const int16_t val);

  void __set_enabled(const bool val);

  bool operator == (const ChannelInfo & rhs) const
  {
    if (!(UOM == rhs.UOM))
      return false;
    if (!(uomModifier == rhs.uomModifier))
      return false;
    if (!(channelNum == rhs.channelNum))
      return false;
    if (!(enabled == rhs.enabled))
      return false;
    return true;
  }
  bool operator != (const ChannelInfo &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const ChannelInfo & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(ChannelInfo &a, ChannelInfo &b);

std::ostream& operator<<(std::ostream& out, const ChannelInfo& obj);


class RfnGetChannelConfigReply : public virtual ::apache::thrift::TBase {
 public:

  RfnGetChannelConfigReply(const RfnGetChannelConfigReply&);
  RfnGetChannelConfigReply& operator=(const RfnGetChannelConfigReply&);
  RfnGetChannelConfigReply() : timestamp(0), recordingInterval(0), reportingInterval(0), reply((GetChannelConfigReplyType::type)0) {
  }

  virtual ~RfnGetChannelConfigReply() noexcept;
   ::Cti::Messaging::Serialization::Thrift::Timestamp timestamp;
  std::set<ChannelInfo>  channelInfo;
   ::Cti::Messaging::Serialization::Thrift::RfnIdentifier rfnIdentifier;
  int32_t recordingInterval;
  int32_t reportingInterval;
  GetChannelConfigReplyType::type reply;

  void __set_timestamp(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val);

  void __set_channelInfo(const std::set<ChannelInfo> & val);

  void __set_rfnIdentifier(const  ::Cti::Messaging::Serialization::Thrift::RfnIdentifier& val);

  void __set_recordingInterval(const int32_t val);

  void __set_reportingInterval(const int32_t val);

  void __set_reply(const GetChannelConfigReplyType::type val);

  bool operator == (const RfnGetChannelConfigReply & rhs) const
  {
    if (!(timestamp == rhs.timestamp))
      return false;
    if (!(channelInfo == rhs.channelInfo))
      return false;
    if (!(rfnIdentifier == rhs.rfnIdentifier))
      return false;
    if (!(recordingInterval == rhs.recordingInterval))
      return false;
    if (!(reportingInterval == rhs.reportingInterval))
      return false;
    if (!(reply == rhs.reply))
      return false;
    return true;
  }
  bool operator != (const RfnGetChannelConfigReply &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const RfnGetChannelConfigReply & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(RfnGetChannelConfigReply &a, RfnGetChannelConfigReply &b);

std::ostream& operator<<(std::ostream& out, const RfnGetChannelConfigReply& obj);

}}}} // namespace

#endif
