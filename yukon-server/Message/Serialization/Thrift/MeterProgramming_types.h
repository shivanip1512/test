/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef MeterProgramming_TYPES_H
#define MeterProgramming_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "RfnAddressing_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift { namespace MeterProgramming {

struct Source {
  enum type {
    PORTER = 0,
    WS_COLLECTION_ACTION = 1,
    SM_STATUS_ARCHIVE = 2,
    SM_CONFIG_FAILURE = 3
  };
};

extern const std::map<int, const char*> _Source_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const Source::type& val);

std::string to_string(const Source::type& val);

struct ProgrammingStatus {
  enum type {
    IDLE = 0,
    UPLOADING = 1,
    CONFIRMING = 2,
    FAILED = 3,
    INITIATING = 4,
    CANCELED = 5,
    MISMATCHED = 6
  };
};

extern const std::map<int, const char*> _ProgrammingStatus_VALUES_TO_NAMES;

std::ostream& operator<<(std::ostream& out, const ProgrammingStatus::type& val);

std::string to_string(const ProgrammingStatus::type& val);

class MeterProgramStatusArchiveRequest;

typedef struct _MeterProgramStatusArchiveRequest__isset {
  _MeterProgramStatusArchiveRequest__isset() : configurationId(false) {}
  bool configurationId :1;
} _MeterProgramStatusArchiveRequest__isset;

class MeterProgramStatusArchiveRequest : public virtual ::apache::thrift::TBase {
 public:

  MeterProgramStatusArchiveRequest(const MeterProgramStatusArchiveRequest&);
  MeterProgramStatusArchiveRequest& operator=(const MeterProgramStatusArchiveRequest&);
  MeterProgramStatusArchiveRequest() noexcept
                                   : configurationId(),
                                     status(static_cast<ProgrammingStatus::type>(0)),
                                     error(0),
                                     timeStamp(0),
                                     source(static_cast<Source::type>(0)) {
  }

  virtual ~MeterProgramStatusArchiveRequest() noexcept;
   ::Cti::Messaging::Serialization::Thrift::Rfn::RfnIdentifier rfnIdentifier;
  std::string configurationId;
  /**
   * 
   * @see ProgrammingStatus
   */
  ProgrammingStatus::type status;
  int32_t error;
  int64_t timeStamp;
  /**
   * 
   * @see Source
   */
  Source::type source;

  _MeterProgramStatusArchiveRequest__isset __isset;

  void __set_rfnIdentifier(const  ::Cti::Messaging::Serialization::Thrift::Rfn::RfnIdentifier& val);

  void __set_configurationId(const std::string& val);

  void __set_status(const ProgrammingStatus::type val);

  void __set_error(const int32_t val);

  void __set_timeStamp(const int64_t val);

  void __set_source(const Source::type val);

  bool operator == (const MeterProgramStatusArchiveRequest & rhs) const
  {
    if (!(rfnIdentifier == rhs.rfnIdentifier))
      return false;
    if (__isset.configurationId != rhs.__isset.configurationId)
      return false;
    else if (__isset.configurationId && !(configurationId == rhs.configurationId))
      return false;
    if (!(status == rhs.status))
      return false;
    if (!(error == rhs.error))
      return false;
    if (!(timeStamp == rhs.timeStamp))
      return false;
    if (!(source == rhs.source))
      return false;
    return true;
  }
  bool operator != (const MeterProgramStatusArchiveRequest &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const MeterProgramStatusArchiveRequest & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot) override;
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const override;

  virtual void printTo(std::ostream& out) const;
};

void swap(MeterProgramStatusArchiveRequest &a, MeterProgramStatusArchiveRequest &b);

std::ostream& operator<<(std::ostream& out, const MeterProgramStatusArchiveRequest& obj);

}}}}} // namespace

#endif
