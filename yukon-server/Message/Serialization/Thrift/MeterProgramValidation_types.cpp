/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "MeterProgramValidation_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift { namespace Porter {


MeterProgramValidationRequest::~MeterProgramValidationRequest() noexcept {
}


void MeterProgramValidationRequest::__set__meterProgramGuid(const std::string& val) {
  this->_meterProgramGuid = val;
}
std::ostream& operator<<(std::ostream& out, const MeterProgramValidationRequest& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t MeterProgramValidationRequest::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__meterProgramGuid = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_meterProgramGuid);
          isset__meterProgramGuid = true;
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

  if (!isset__meterProgramGuid)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t MeterProgramValidationRequest::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("MeterProgramValidationRequest");

  xfer += oprot->writeFieldBegin("_meterProgramGuid", ::apache::thrift::protocol::T_STRING, 1);
  xfer += oprot->writeString(this->_meterProgramGuid);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(MeterProgramValidationRequest &a, MeterProgramValidationRequest &b) {
  using ::std::swap;
  swap(a._meterProgramGuid, b._meterProgramGuid);
}

MeterProgramValidationRequest::MeterProgramValidationRequest(const MeterProgramValidationRequest& other0) {
  _meterProgramGuid = other0._meterProgramGuid;
}
MeterProgramValidationRequest& MeterProgramValidationRequest::operator=(const MeterProgramValidationRequest& other1) {
  _meterProgramGuid = other1._meterProgramGuid;
  return *this;
}
void MeterProgramValidationRequest::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "MeterProgramValidationRequest(";
  out << "_meterProgramGuid=" << to_string(_meterProgramGuid);
  out << ")";
}


MeterProgramValidationResponse::~MeterProgramValidationResponse() noexcept {
}


void MeterProgramValidationResponse::__set__meterProgramGuid(const std::string& val) {
  this->_meterProgramGuid = val;
}

void MeterProgramValidationResponse::__set__status(const int32_t val) {
  this->_status = val;
}
std::ostream& operator<<(std::ostream& out, const MeterProgramValidationResponse& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t MeterProgramValidationResponse::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__meterProgramGuid = false;
  bool isset__status = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_meterProgramGuid);
          isset__meterProgramGuid = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_status);
          isset__status = true;
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

  if (!isset__meterProgramGuid)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__status)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t MeterProgramValidationResponse::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("MeterProgramValidationResponse");

  xfer += oprot->writeFieldBegin("_meterProgramGuid", ::apache::thrift::protocol::T_STRING, 1);
  xfer += oprot->writeString(this->_meterProgramGuid);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_status", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_status);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(MeterProgramValidationResponse &a, MeterProgramValidationResponse &b) {
  using ::std::swap;
  swap(a._meterProgramGuid, b._meterProgramGuid);
  swap(a._status, b._status);
}

MeterProgramValidationResponse::MeterProgramValidationResponse(const MeterProgramValidationResponse& other2) {
  _meterProgramGuid = other2._meterProgramGuid;
  _status = other2._status;
}
MeterProgramValidationResponse& MeterProgramValidationResponse::operator=(const MeterProgramValidationResponse& other3) {
  _meterProgramGuid = other3._meterProgramGuid;
  _status = other3._status;
  return *this;
}
void MeterProgramValidationResponse::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "MeterProgramValidationResponse(";
  out << "_meterProgramGuid=" << to_string(_meterProgramGuid);
  out << ", " << "_status=" << to_string(_status);
  out << ")";
}

}}}}} // namespace
