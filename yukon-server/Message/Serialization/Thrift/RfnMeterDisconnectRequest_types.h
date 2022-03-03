/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef RfnMeterDisconnectRequest_TYPES_H
#define RfnMeterDisconnectRequest_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "NetworkManagerMessaging_types.h"
#include "RfnAddressing_types.h"
#include "Types_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

struct RfnMeterDisconnectCmdType {
  enum type {
    ARM = 0,
    RESUME = 1,
    TERMINATE = 2,
    QUERY = 3
  };
};

extern const std::map<int, const char*> _RfnMeterDisconnectCmdType_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const RfnMeterDisconnectCmdType::type& val);

std::string to_string(const RfnMeterDisconnectCmdType::type& val);

struct RfnMeterDisconnectInitialReplyType {
  enum type {
    OK = 0,
    NO_NODE = 1,
    NO_GATEWAY = 2,
    FAILURE = 3,
    TIMEOUT = 4
  };
};

extern const std::map<int, const char*> _RfnMeterDisconnectInitialReplyType_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const RfnMeterDisconnectInitialReplyType::type& val);

std::string to_string(const RfnMeterDisconnectInitialReplyType::type& val);

struct RfnMeterDisconnectConfirmationReplyType {
  enum type {
    SUCCESS = 0,
    FAILURE = 1,
    FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD = 2,
    FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT = 3,
    FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT = 4,
    FAILED_UNEXPECTED_STATUS = 5,
    NOT_SUPPORTED = 6,
    NETWORK_TIMEOUT = 7,
    TIMEOUT = 8
  };
};

extern const std::map<int, const char*> _RfnMeterDisconnectConfirmationReplyType_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const RfnMeterDisconnectConfirmationReplyType::type& val);

std::string to_string(const RfnMeterDisconnectConfirmationReplyType::type& val);

struct RfnMeterDisconnectState {
  enum type {
    UNKNOWN = 0,
    CONNECTED = 1,
    DISCONNECTED = 2,
    ARMED = 3,
    DISCONNECTED_DEMAND_THRESHOLD_ACTIVE = 4,
    CONNECTED_DEMAND_THRESHOLD_ACTIVE = 5,
    DISCONNECTED_CYCLING_ACTIVE = 6,
    CONNECTED_CYCLING_ACTIVE = 7
  };
};

extern const std::map<int, const char*> _RfnMeterDisconnectState_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const RfnMeterDisconnectState::type& val);

std::string to_string(const RfnMeterDisconnectState::type& val);

class RfnMeterDisconnectRequest;

class RfnMeterDisconnectInitialReply;

class RfnMeterDisconnectConfirmationReply;


class RfnMeterDisconnectRequest : public virtual ::apache::thrift::TBase {
 public:

  RfnMeterDisconnectRequest(const RfnMeterDisconnectRequest&);
  RfnMeterDisconnectRequest& operator=(const RfnMeterDisconnectRequest&);
  RfnMeterDisconnectRequest() : action((RfnMeterDisconnectCmdType::type)0) {
  }

  virtual ~RfnMeterDisconnectRequest() noexcept;
   ::Cti::Messaging::Serialization::Thrift::Rfn::RfnIdentifier rfnIdentifier;
  RfnMeterDisconnectCmdType::type action;

  void __set_rfnIdentifier(const  ::Cti::Messaging::Serialization::Thrift::Rfn::RfnIdentifier& val);

  void __set_action(const RfnMeterDisconnectCmdType::type val);

  bool operator == (const RfnMeterDisconnectRequest & rhs) const
  {
    if (!(rfnIdentifier == rhs.rfnIdentifier))
      return false;
    if (!(action == rhs.action))
      return false;
    return true;
  }
  bool operator != (const RfnMeterDisconnectRequest &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const RfnMeterDisconnectRequest & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(RfnMeterDisconnectRequest &a, RfnMeterDisconnectRequest &b);

std::ostream& operator<<(std::ostream& out, const RfnMeterDisconnectRequest& obj);


class RfnMeterDisconnectInitialReply : public virtual ::apache::thrift::TBase {
 public:

  RfnMeterDisconnectInitialReply(const RfnMeterDisconnectInitialReply&);
  RfnMeterDisconnectInitialReply& operator=(const RfnMeterDisconnectInitialReply&);
  RfnMeterDisconnectInitialReply() : replyType((RfnMeterDisconnectInitialReplyType::type)0) {
  }

  virtual ~RfnMeterDisconnectInitialReply() noexcept;
  RfnMeterDisconnectInitialReplyType::type replyType;

  void __set_replyType(const RfnMeterDisconnectInitialReplyType::type val);

  bool operator == (const RfnMeterDisconnectInitialReply & rhs) const
  {
    if (!(replyType == rhs.replyType))
      return false;
    return true;
  }
  bool operator != (const RfnMeterDisconnectInitialReply &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const RfnMeterDisconnectInitialReply & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(RfnMeterDisconnectInitialReply &a, RfnMeterDisconnectInitialReply &b);

std::ostream& operator<<(std::ostream& out, const RfnMeterDisconnectInitialReply& obj);


class RfnMeterDisconnectConfirmationReply : public virtual ::apache::thrift::TBase {
 public:

  RfnMeterDisconnectConfirmationReply(const RfnMeterDisconnectConfirmationReply&);
  RfnMeterDisconnectConfirmationReply& operator=(const RfnMeterDisconnectConfirmationReply&);
  RfnMeterDisconnectConfirmationReply() : replyType((RfnMeterDisconnectConfirmationReplyType::type)0), state((RfnMeterDisconnectState::type)0) {
  }

  virtual ~RfnMeterDisconnectConfirmationReply() noexcept;
  RfnMeterDisconnectConfirmationReplyType::type replyType;
  RfnMeterDisconnectState::type state;

  void __set_replyType(const RfnMeterDisconnectConfirmationReplyType::type val);

  void __set_state(const RfnMeterDisconnectState::type val);

  bool operator == (const RfnMeterDisconnectConfirmationReply & rhs) const
  {
    if (!(replyType == rhs.replyType))
      return false;
    if (!(state == rhs.state))
      return false;
    return true;
  }
  bool operator != (const RfnMeterDisconnectConfirmationReply &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const RfnMeterDisconnectConfirmationReply & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(RfnMeterDisconnectConfirmationReply &a, RfnMeterDisconnectConfirmationReply &b);

std::ostream& operator<<(std::ostream& out, const RfnMeterDisconnectConfirmationReply& obj);

}}}} // namespace

#endif
