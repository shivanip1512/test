/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "RfnDataStreamingUpdate_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


RfnDataStreamingUpdate::~RfnDataStreamingUpdate() noexcept {
}


void RfnDataStreamingUpdate::__set_paoId(const int32_t val) {
  this->paoId = val;
}

void RfnDataStreamingUpdate::__set_json(const std::string& val) {
  this->json = val;
}
std::ostream& operator<<(std::ostream& out, const RfnDataStreamingUpdate& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t RfnDataStreamingUpdate::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset_paoId = false;
  bool isset_json = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->paoId);
          isset_paoId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->json);
          isset_json = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      default:
        xfer += iprot->skip(ftype);
        break;
    }
    xfer += iprot->readFieldEnd();
  }

  xfer += iprot->readStructEnd();

  if (!isset_paoId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset_json)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t RfnDataStreamingUpdate::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("RfnDataStreamingUpdate");

  xfer += oprot->writeFieldBegin("paoId", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32(this->paoId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("json", ::apache::thrift::protocol::T_STRING, 2);
  xfer += oprot->writeString(this->json);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(RfnDataStreamingUpdate &a, RfnDataStreamingUpdate &b) {
  using ::std::swap;
  swap(a.paoId, b.paoId);
  swap(a.json, b.json);
}

RfnDataStreamingUpdate::RfnDataStreamingUpdate(const RfnDataStreamingUpdate& other0) {
  paoId = other0.paoId;
  json = other0.json;
}
RfnDataStreamingUpdate& RfnDataStreamingUpdate::operator=(const RfnDataStreamingUpdate& other1) {
  paoId = other1.paoId;
  json = other1.json;
  return *this;
}
void RfnDataStreamingUpdate::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "RfnDataStreamingUpdate(";
  out << "paoId=" << to_string(paoId);
  out << ", " << "json=" << to_string(json);
  out << ")";
}


RfnDataStreamingUpdateReply::~RfnDataStreamingUpdateReply() noexcept {
}


void RfnDataStreamingUpdateReply::__set_success(const bool val) {
  this->success = val;
}
std::ostream& operator<<(std::ostream& out, const RfnDataStreamingUpdateReply& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t RfnDataStreamingUpdateReply::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset_success = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->success);
          isset_success = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      default:
        xfer += iprot->skip(ftype);
        break;
    }
    xfer += iprot->readFieldEnd();
  }

  xfer += iprot->readStructEnd();

  if (!isset_success)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t RfnDataStreamingUpdateReply::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("RfnDataStreamingUpdateReply");

  xfer += oprot->writeFieldBegin("success", ::apache::thrift::protocol::T_BOOL, 1);
  xfer += oprot->writeBool(this->success);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(RfnDataStreamingUpdateReply &a, RfnDataStreamingUpdateReply &b) {
  using ::std::swap;
  swap(a.success, b.success);
}

RfnDataStreamingUpdateReply::RfnDataStreamingUpdateReply(const RfnDataStreamingUpdateReply& other2) {
  success = other2.success;
}
RfnDataStreamingUpdateReply& RfnDataStreamingUpdateReply::operator=(const RfnDataStreamingUpdateReply& other3) {
  success = other3.success;
  return *this;
}
void RfnDataStreamingUpdateReply::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "RfnDataStreamingUpdateReply(";
  out << "success=" << to_string(success);
  out << ")";
}

}}}} // namespace
