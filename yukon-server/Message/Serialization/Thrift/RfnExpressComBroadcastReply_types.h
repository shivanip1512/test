/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef RfnExpressComBroadcastReply_TYPES_H
#define RfnExpressComBroadcastReply_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

struct RfnExpressComBroadcastReplyType {
  enum type {
    SUCCESS = 0,
    FAILURE = 1,
    NETWORK_TIMEOUT = 2,
    TIMEOUT = 3
  };
};

extern const std::map<int, const char*> _RfnExpressComBroadcastReplyType_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const RfnExpressComBroadcastReplyType::type& val);

std::string to_string(const RfnExpressComBroadcastReplyType::type& val);

class RfnExpressComBroadcastReply;


class RfnExpressComBroadcastReply : public virtual ::apache::thrift::TBase {
 public:

  RfnExpressComBroadcastReply(const RfnExpressComBroadcastReply&);
  RfnExpressComBroadcastReply& operator=(const RfnExpressComBroadcastReply&);
  RfnExpressComBroadcastReply() noexcept {
  }

  virtual ~RfnExpressComBroadcastReply() noexcept;
  std::map<int64_t, RfnExpressComBroadcastReplyType::type>  status;

  void __set_status(const std::map<int64_t, RfnExpressComBroadcastReplyType::type> & val);

  bool operator == (const RfnExpressComBroadcastReply & rhs) const
  {
    if (!(status == rhs.status))
      return false;
    return true;
  }
  bool operator != (const RfnExpressComBroadcastReply &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const RfnExpressComBroadcastReply & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot) override;
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const override;

  virtual void printTo(std::ostream& out) const;
};

void swap(RfnExpressComBroadcastReply &a, RfnExpressComBroadcastReply &b);

std::ostream& operator<<(std::ostream& out, const RfnExpressComBroadcastReply& obj);

}}}} // namespace

#endif
