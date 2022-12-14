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
    FAILURE_REQUEST_REJECTED_REASON_UNKNOWN = 2,
    FAILURE_SERVICE_NOT_SUPPORTED = 3,
    FAILURE_INSUFFICIENT_SECURITY_CLEARANCE = 4,
    FAILURE_OPERATION_NOT_POSSIBLE = 5,
    FAILURE_INAPPROPRIATE_ACTION_REQUESTED = 6,
    FAILURE_DEVICE_BUSY = 7,
    FAILURE_DATA_NOT_READY = 8,
    FAILURE_DATA_LOCKED = 9,
    FAILURE_RENEGOTIATE_REQUEST = 10,
    FAILURE_INVALID_STATE = 11,
    FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD = 12,
    FAILURE_ARM_REJECTED_SWITCH_NOT_OPEN = 13,
    FAILURE_METER_IN_TEST_MODE = 14,
    FAILURE_CLOSE_PRESSED_BUT_METER_NOT_ARMED = 15,
    FAILURE_METER_NOT_CAPABLE_OF_SERVICE_DISCONNECT = 16,
    FAILURE_SERVICE_DISCONNECT_NOT_ENABLED = 17,
    FAILURE_SERVICE_DISCONNECT_IS_CHARGING = 18,
    FAILURE_SERVICE_DISCONNECT_ALREADY_OPERATING = 19,
    FAILURE_CAPACITOR_DISCHARGE_NOT_DETECTED = 20,
    FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT = 21,
    FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT = 22,
    FAILED_UNEXPECTED_STATUS = 23,
    NOT_SUPPORTED = 24,
    NETWORK_TIMEOUT = 25,
    TIMEOUT = 26
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
