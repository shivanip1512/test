/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef NetworkManagerMessaging_TYPES_H
#define NetworkManagerMessaging_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

struct NetworkManagerMessageLifetime {
  enum type {
    SESSION = 0,
    UNTIL_CANCEL = 1
  };
};

extern const std::map<int, const char*> _NetworkManagerMessageLifetime_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const NetworkManagerMessageLifetime::type& val);

std::string to_string(const NetworkManagerMessageLifetime::type& val);

struct NetworkManagerCancelType {
  enum type {
    MESSAGE_IDS = 0,
    GROUP_IDS = 1
  };
};

extern const std::map<int, const char*> _NetworkManagerCancelType_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const NetworkManagerCancelType::type& val);

std::string to_string(const NetworkManagerCancelType::type& val);

struct NetworkManagerMessageCancelStatus {
  enum type {
    SUCCESS = 0,
    NOT_FOUND = 1
  };
};

extern const std::map<int, const char*> _NetworkManagerMessageCancelStatus_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const NetworkManagerMessageCancelStatus::type& val);

std::string to_string(const NetworkManagerMessageCancelStatus::type& val);

typedef std::map<int64_t, NetworkManagerMessageCancelStatus::type>  MessageStatusPerId;

class NetworkManagerRequestHeader;

class NetworkManagerRequestAck;

class NetworkManagerCancelRequest;

class NetworkManagerCancelRequestAck;

class NetworkManagerCancelResponse;

typedef struct _NetworkManagerRequestHeader__isset {
  _NetworkManagerRequestHeader__isset() : groupId(false), expiration(false), lifetime(true) {}
  bool groupId :1;
  bool expiration :1;
  bool lifetime :1;
} _NetworkManagerRequestHeader__isset;

class NetworkManagerRequestHeader : public virtual ::apache::thrift::TBase {
 public:

  NetworkManagerRequestHeader(const NetworkManagerRequestHeader&);
  NetworkManagerRequestHeader& operator=(const NetworkManagerRequestHeader&);
  NetworkManagerRequestHeader() : clientGuid(), sessionId(0), messageId(0), groupId(0), priority(0), expiration(0), lifetime((NetworkManagerMessageLifetime::type)0) {
    lifetime = (NetworkManagerMessageLifetime::type)0;

  }

  virtual ~NetworkManagerRequestHeader() noexcept;
  std::string clientGuid;
  int64_t sessionId;
  int64_t messageId;
  int64_t groupId;
  int8_t priority;
  int64_t expiration;
  NetworkManagerMessageLifetime::type lifetime;

  _NetworkManagerRequestHeader__isset __isset;

  void __set_clientGuid(const std::string& val);

  void __set_sessionId(const int64_t val);

  void __set_messageId(const int64_t val);

  void __set_groupId(const int64_t val);

  void __set_priority(const int8_t val);

  void __set_expiration(const int64_t val);

  void __set_lifetime(const NetworkManagerMessageLifetime::type val);

  bool operator == (const NetworkManagerRequestHeader & rhs) const
  {
    if (!(clientGuid == rhs.clientGuid))
      return false;
    if (!(sessionId == rhs.sessionId))
      return false;
    if (!(messageId == rhs.messageId))
      return false;
    if (__isset.groupId != rhs.__isset.groupId)
      return false;
    else if (__isset.groupId && !(groupId == rhs.groupId))
      return false;
    if (!(priority == rhs.priority))
      return false;
    if (__isset.expiration != rhs.__isset.expiration)
      return false;
    else if (__isset.expiration && !(expiration == rhs.expiration))
      return false;
    if (__isset.lifetime != rhs.__isset.lifetime)
      return false;
    else if (__isset.lifetime && !(lifetime == rhs.lifetime))
      return false;
    return true;
  }
  bool operator != (const NetworkManagerRequestHeader &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const NetworkManagerRequestHeader & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(NetworkManagerRequestHeader &a, NetworkManagerRequestHeader &b);

std::ostream& operator<<(std::ostream& out, const NetworkManagerRequestHeader& obj);


class NetworkManagerRequestAck : public virtual ::apache::thrift::TBase {
 public:

  NetworkManagerRequestAck(const NetworkManagerRequestAck&);
  NetworkManagerRequestAck& operator=(const NetworkManagerRequestAck&);
  NetworkManagerRequestAck() {
  }

  virtual ~NetworkManagerRequestAck() noexcept;
  NetworkManagerRequestHeader header;

  void __set_header(const NetworkManagerRequestHeader& val);

  bool operator == (const NetworkManagerRequestAck & rhs) const
  {
    if (!(header == rhs.header))
      return false;
    return true;
  }
  bool operator != (const NetworkManagerRequestAck &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const NetworkManagerRequestAck & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(NetworkManagerRequestAck &a, NetworkManagerRequestAck &b);

std::ostream& operator<<(std::ostream& out, const NetworkManagerRequestAck& obj);


class NetworkManagerCancelRequest : public virtual ::apache::thrift::TBase {
 public:

  NetworkManagerCancelRequest(const NetworkManagerCancelRequest&);
  NetworkManagerCancelRequest& operator=(const NetworkManagerCancelRequest&);
  NetworkManagerCancelRequest() : clientGuid(), sessionId(0), type((NetworkManagerCancelType::type)0) {
  }

  virtual ~NetworkManagerCancelRequest() noexcept;
  std::string clientGuid;
  int64_t sessionId;
  NetworkManagerCancelType::type type;
  std::set<int64_t>  ids;

  void __set_clientGuid(const std::string& val);

  void __set_sessionId(const int64_t val);

  void __set_type(const NetworkManagerCancelType::type val);

  void __set_ids(const std::set<int64_t> & val);

  bool operator == (const NetworkManagerCancelRequest & rhs) const
  {
    if (!(clientGuid == rhs.clientGuid))
      return false;
    if (!(sessionId == rhs.sessionId))
      return false;
    if (!(type == rhs.type))
      return false;
    if (!(ids == rhs.ids))
      return false;
    return true;
  }
  bool operator != (const NetworkManagerCancelRequest &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const NetworkManagerCancelRequest & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(NetworkManagerCancelRequest &a, NetworkManagerCancelRequest &b);

std::ostream& operator<<(std::ostream& out, const NetworkManagerCancelRequest& obj);


class NetworkManagerCancelRequestAck : public virtual ::apache::thrift::TBase {
 public:

  NetworkManagerCancelRequestAck(const NetworkManagerCancelRequestAck&);
  NetworkManagerCancelRequestAck& operator=(const NetworkManagerCancelRequestAck&);
  NetworkManagerCancelRequestAck() {
  }

  virtual ~NetworkManagerCancelRequestAck() noexcept;
  NetworkManagerCancelRequest request;

  void __set_request(const NetworkManagerCancelRequest& val);

  bool operator == (const NetworkManagerCancelRequestAck & rhs) const
  {
    if (!(request == rhs.request))
      return false;
    return true;
  }
  bool operator != (const NetworkManagerCancelRequestAck &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const NetworkManagerCancelRequestAck & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(NetworkManagerCancelRequestAck &a, NetworkManagerCancelRequestAck &b);

std::ostream& operator<<(std::ostream& out, const NetworkManagerCancelRequestAck& obj);


class NetworkManagerCancelResponse : public virtual ::apache::thrift::TBase {
 public:

  NetworkManagerCancelResponse(const NetworkManagerCancelResponse&);
  NetworkManagerCancelResponse& operator=(const NetworkManagerCancelResponse&);
  NetworkManagerCancelResponse() : clientGuid(), sessionId(0) {
  }

  virtual ~NetworkManagerCancelResponse() noexcept;
  std::string clientGuid;
  int64_t sessionId;
  MessageStatusPerId messageIds;

  void __set_clientGuid(const std::string& val);

  void __set_sessionId(const int64_t val);

  void __set_messageIds(const MessageStatusPerId& val);

  bool operator == (const NetworkManagerCancelResponse & rhs) const
  {
    if (!(clientGuid == rhs.clientGuid))
      return false;
    if (!(sessionId == rhs.sessionId))
      return false;
    if (!(messageIds == rhs.messageIds))
      return false;
    return true;
  }
  bool operator != (const NetworkManagerCancelResponse &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const NetworkManagerCancelResponse & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(NetworkManagerCancelResponse &a, NetworkManagerCancelResponse &b);

std::ostream& operator<<(std::ostream& out, const NetworkManagerCancelResponse& obj);

}}}} // namespace

#endif
