/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef RfnE2eData_TYPES_H
#define RfnE2eData_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "NetworkManagerMessaging_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

struct RfnE2eProtocol {
  enum type {
    APPLICATION = 0,
    NETWORK = 1,
    LINK = 2
  };
};

extern const std::map<int, const char*> _RfnE2eProtocol_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const RfnE2eProtocol::type& val);

std::string to_string(const RfnE2eProtocol::type& val);

struct RfnE2eMessagePriority {
  enum type {
    APP_LO = 0,
    APP_HI = 1
  };
};

extern const std::map<int, const char*> _RfnE2eMessagePriority_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const RfnE2eMessagePriority::type& val);

std::string to_string(const RfnE2eMessagePriority::type& val);

struct RfnE2eDataReplyType {
  enum type {
    OK = 0,
    DESTINATION_DEVICE_ADDRESS_UNKNOWN = 1,
    DESTINATION_NETWORK_UNAVAILABLE = 2,
    PMTU_LENGTH_EXCEEDED = 3,
    E2E_PROTOCOL_TYPE_NOT_SUPPORTED = 4,
    NETWORK_SERVER_IDENTIFIER_INVALID = 5,
    APPLICATION_SERVICE_IDENTIFIER_INVALID = 6,
    NETWORK_LOAD_CONTROL = 7,
    NETWORK_SERVICE_FAILURE = 8,
    REQUEST_CANCELED = 9,
    REQUEST_EXPIRED = 10
  };
};

extern const std::map<int, const char*> _RfnE2eDataReplyType_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const RfnE2eDataReplyType::type& val);

std::string to_string(const RfnE2eDataReplyType::type& val);

class RfnIdentifier;

class RfnE2eDataRequest;

class RfnE2eDataIndication;

class RfnE2eDataConfirm;


class RfnIdentifier : public virtual ::apache::thrift::TBase {
 public:

  RfnIdentifier(const RfnIdentifier&);
  RfnIdentifier& operator=(const RfnIdentifier&);
  RfnIdentifier() : sensorManufacturer(), sensorModel(), sensorSerialNumber() {
  }

  virtual ~RfnIdentifier() noexcept;
  std::string sensorManufacturer;
  std::string sensorModel;
  std::string sensorSerialNumber;

  void __set_sensorManufacturer(const std::string& val);

  void __set_sensorModel(const std::string& val);

  void __set_sensorSerialNumber(const std::string& val);

  bool operator == (const RfnIdentifier & rhs) const
  {
    if (!(sensorManufacturer == rhs.sensorManufacturer))
      return false;
    if (!(sensorModel == rhs.sensorModel))
      return false;
    if (!(sensorSerialNumber == rhs.sensorSerialNumber))
      return false;
    return true;
  }
  bool operator != (const RfnIdentifier &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const RfnIdentifier & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(RfnIdentifier &a, RfnIdentifier &b);

std::ostream& operator<<(std::ostream& out, const RfnIdentifier& obj);

typedef struct _RfnE2eDataRequest__isset {
  _RfnE2eDataRequest__isset() : security(false), header(false) {}
  bool security :1;
  bool header :1;
} _RfnE2eDataRequest__isset;

class RfnE2eDataRequest : public virtual ::apache::thrift::TBase {
 public:

  RfnE2eDataRequest(const RfnE2eDataRequest&);
  RfnE2eDataRequest& operator=(const RfnE2eDataRequest&);
  RfnE2eDataRequest() : e2eProtocol((RfnE2eProtocol::type)0), applicationServiceId(0), priority((RfnE2eMessagePriority::type)0), security(), payload() {
  }

  virtual ~RfnE2eDataRequest() noexcept;
  RfnE2eProtocol::type e2eProtocol;
  int8_t applicationServiceId;
  RfnIdentifier rfnIdentifier;
  RfnE2eMessagePriority::type priority;
  std::string security;
  std::string payload;
   ::Cti::Messaging::Serialization::Thrift::NetworkManagerRequestHeader header;

  _RfnE2eDataRequest__isset __isset;

  void __set_e2eProtocol(const RfnE2eProtocol::type val);

  void __set_applicationServiceId(const int8_t val);

  void __set_rfnIdentifier(const RfnIdentifier& val);

  void __set_priority(const RfnE2eMessagePriority::type val);

  void __set_security(const std::string& val);

  void __set_payload(const std::string& val);

  void __set_header(const  ::Cti::Messaging::Serialization::Thrift::NetworkManagerRequestHeader& val);

  bool operator == (const RfnE2eDataRequest & rhs) const
  {
    if (!(e2eProtocol == rhs.e2eProtocol))
      return false;
    if (!(applicationServiceId == rhs.applicationServiceId))
      return false;
    if (!(rfnIdentifier == rhs.rfnIdentifier))
      return false;
    if (!(priority == rhs.priority))
      return false;
    if (__isset.security != rhs.__isset.security)
      return false;
    else if (__isset.security && !(security == rhs.security))
      return false;
    if (!(payload == rhs.payload))
      return false;
    if (__isset.header != rhs.__isset.header)
      return false;
    else if (__isset.header && !(header == rhs.header))
      return false;
    return true;
  }
  bool operator != (const RfnE2eDataRequest &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const RfnE2eDataRequest & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(RfnE2eDataRequest &a, RfnE2eDataRequest &b);

std::ostream& operator<<(std::ostream& out, const RfnE2eDataRequest& obj);

typedef struct _RfnE2eDataIndication__isset {
  _RfnE2eDataIndication__isset() : security(false) {}
  bool security :1;
} _RfnE2eDataIndication__isset;

class RfnE2eDataIndication : public virtual ::apache::thrift::TBase {
 public:

  RfnE2eDataIndication(const RfnE2eDataIndication&);
  RfnE2eDataIndication& operator=(const RfnE2eDataIndication&);
  RfnE2eDataIndication() : e2eProtocol((RfnE2eProtocol::type)0), applicationServiceId(0), priority((RfnE2eMessagePriority::type)0), security(), payload() {
  }

  virtual ~RfnE2eDataIndication() noexcept;
  RfnE2eProtocol::type e2eProtocol;
  int8_t applicationServiceId;
  RfnIdentifier rfnIdentifier;
  RfnE2eMessagePriority::type priority;
  std::string security;
  std::string payload;

  _RfnE2eDataIndication__isset __isset;

  void __set_e2eProtocol(const RfnE2eProtocol::type val);

  void __set_applicationServiceId(const int8_t val);

  void __set_rfnIdentifier(const RfnIdentifier& val);

  void __set_priority(const RfnE2eMessagePriority::type val);

  void __set_security(const std::string& val);

  void __set_payload(const std::string& val);

  bool operator == (const RfnE2eDataIndication & rhs) const
  {
    if (!(e2eProtocol == rhs.e2eProtocol))
      return false;
    if (!(applicationServiceId == rhs.applicationServiceId))
      return false;
    if (!(rfnIdentifier == rhs.rfnIdentifier))
      return false;
    if (!(priority == rhs.priority))
      return false;
    if (__isset.security != rhs.__isset.security)
      return false;
    else if (__isset.security && !(security == rhs.security))
      return false;
    if (!(payload == rhs.payload))
      return false;
    return true;
  }
  bool operator != (const RfnE2eDataIndication &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const RfnE2eDataIndication & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(RfnE2eDataIndication &a, RfnE2eDataIndication &b);

std::ostream& operator<<(std::ostream& out, const RfnE2eDataIndication& obj);

typedef struct _RfnE2eDataConfirm__isset {
  _RfnE2eDataConfirm__isset() : header(false) {}
  bool header :1;
} _RfnE2eDataConfirm__isset;

class RfnE2eDataConfirm : public virtual ::apache::thrift::TBase {
 public:

  RfnE2eDataConfirm(const RfnE2eDataConfirm&);
  RfnE2eDataConfirm& operator=(const RfnE2eDataConfirm&);
  RfnE2eDataConfirm() : e2eProtocol((RfnE2eProtocol::type)0), applicationServiceId(0), replyType((RfnE2eDataReplyType::type)0) {
  }

  virtual ~RfnE2eDataConfirm() noexcept;
  RfnE2eProtocol::type e2eProtocol;
  int8_t applicationServiceId;
  RfnIdentifier rfnIdentifier;
  RfnE2eDataReplyType::type replyType;
   ::Cti::Messaging::Serialization::Thrift::NetworkManagerRequestHeader header;

  _RfnE2eDataConfirm__isset __isset;

  void __set_e2eProtocol(const RfnE2eProtocol::type val);

  void __set_applicationServiceId(const int8_t val);

  void __set_rfnIdentifier(const RfnIdentifier& val);

  void __set_replyType(const RfnE2eDataReplyType::type val);

  void __set_header(const  ::Cti::Messaging::Serialization::Thrift::NetworkManagerRequestHeader& val);

  bool operator == (const RfnE2eDataConfirm & rhs) const
  {
    if (!(e2eProtocol == rhs.e2eProtocol))
      return false;
    if (!(applicationServiceId == rhs.applicationServiceId))
      return false;
    if (!(rfnIdentifier == rhs.rfnIdentifier))
      return false;
    if (!(replyType == rhs.replyType))
      return false;
    if (__isset.header != rhs.__isset.header)
      return false;
    else if (__isset.header && !(header == rhs.header))
      return false;
    return true;
  }
  bool operator != (const RfnE2eDataConfirm &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const RfnE2eDataConfirm & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(RfnE2eDataConfirm &a, RfnE2eDataConfirm &b);

std::ostream& operator<<(std::ostream& out, const RfnE2eDataConfirm& obj);

}}}} // namespace

#endif
