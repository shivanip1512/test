/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "RfnMeterReadRequest_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

int _kRfnMeterReadingReplyTypeValues[] = {
  RfnMeterReadingReplyType::OK,
  RfnMeterReadingReplyType::NO_NODE,
  RfnMeterReadingReplyType::NO_GATEWAY,
  RfnMeterReadingReplyType::FAILURE,
  RfnMeterReadingReplyType::TIMEOUT
};
const char* _kRfnMeterReadingReplyTypeNames[] = {
  "OK",
  "NO_NODE",
  "NO_GATEWAY",
  "FAILURE",
  "TIMEOUT"
};
const std::map<int, const char*> _RfnMeterReadingReplyType_VALUES_TO_NAMES(::apache::thrift::TEnumIterator(5, _kRfnMeterReadingReplyTypeValues, _kRfnMeterReadingReplyTypeNames), ::apache::thrift::TEnumIterator(-1, NULL, NULL));

std::ostream& operator<<(std::ostream& out, const RfnMeterReadingReplyType::type& val) {
  std::map<int, const char*>::const_iterator it = _RfnMeterReadingReplyType_VALUES_TO_NAMES.find(val);
  if (it != _RfnMeterReadingReplyType_VALUES_TO_NAMES.end()) {
    out << it->second;
  } else {
    out << static_cast<int>(val);
  }
  return out;
}

std::string to_string(const RfnMeterReadingReplyType::type& val) {
  std::map<int, const char*>::const_iterator it = _RfnMeterReadingReplyType_VALUES_TO_NAMES.find(val);
  if (it != _RfnMeterReadingReplyType_VALUES_TO_NAMES.end()) {
    return std::string(it->second);
  } else {
    return std::to_string(static_cast<int>(val));
  }
}

int _kRfnMeterReadingDataReplyTypeValues[] = {
  RfnMeterReadingDataReplyType::OK,
  RfnMeterReadingDataReplyType::FAILURE,
  RfnMeterReadingDataReplyType::NETWORK_TIMEOUT,
  RfnMeterReadingDataReplyType::TIMEOUT
};
const char* _kRfnMeterReadingDataReplyTypeNames[] = {
  "OK",
  "FAILURE",
  "NETWORK_TIMEOUT",
  "TIMEOUT"
};
const std::map<int, const char*> _RfnMeterReadingDataReplyType_VALUES_TO_NAMES(::apache::thrift::TEnumIterator(4, _kRfnMeterReadingDataReplyTypeValues, _kRfnMeterReadingDataReplyTypeNames), ::apache::thrift::TEnumIterator(-1, NULL, NULL));

std::ostream& operator<<(std::ostream& out, const RfnMeterReadingDataReplyType::type& val) {
  std::map<int, const char*>::const_iterator it = _RfnMeterReadingDataReplyType_VALUES_TO_NAMES.find(val);
  if (it != _RfnMeterReadingDataReplyType_VALUES_TO_NAMES.end()) {
    out << it->second;
  } else {
    out << static_cast<int>(val);
  }
  return out;
}

std::string to_string(const RfnMeterReadingDataReplyType::type& val) {
  std::map<int, const char*>::const_iterator it = _RfnMeterReadingDataReplyType_VALUES_TO_NAMES.find(val);
  if (it != _RfnMeterReadingDataReplyType_VALUES_TO_NAMES.end()) {
    return std::string(it->second);
  } else {
    return std::to_string(static_cast<int>(val));
  }
}

int _kChannelDataStatusValues[] = {
  ChannelDataStatus::OK,
  ChannelDataStatus::TIMEOUT,
  ChannelDataStatus::FAILURE,
  ChannelDataStatus::LONG
};
const char* _kChannelDataStatusNames[] = {
  "OK",
  "TIMEOUT",
  "FAILURE",
  "LONG"
};
const std::map<int, const char*> _ChannelDataStatus_VALUES_TO_NAMES(::apache::thrift::TEnumIterator(4, _kChannelDataStatusValues, _kChannelDataStatusNames), ::apache::thrift::TEnumIterator(-1, NULL, NULL));

std::ostream& operator<<(std::ostream& out, const ChannelDataStatus::type& val) {
  std::map<int, const char*>::const_iterator it = _ChannelDataStatus_VALUES_TO_NAMES.find(val);
  if (it != _ChannelDataStatus_VALUES_TO_NAMES.end()) {
    out << it->second;
  } else {
    out << static_cast<int>(val);
  }
  return out;
}

std::string to_string(const ChannelDataStatus::type& val) {
  std::map<int, const char*>::const_iterator it = _ChannelDataStatus_VALUES_TO_NAMES.find(val);
  if (it != _ChannelDataStatus_VALUES_TO_NAMES.end()) {
    return std::string(it->second);
  } else {
    return std::to_string(static_cast<int>(val));
  }
}


RfnMeterReadRequest::~RfnMeterReadRequest() noexcept {
}


void RfnMeterReadRequest::__set_rfnIdentifier(const  ::Cti::Messaging::Serialization::Thrift::Rfn::RfnIdentifier& val) {
  this->rfnIdentifier = val;
}
std::ostream& operator<<(std::ostream& out, const RfnMeterReadRequest& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t RfnMeterReadRequest::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset_rfnIdentifier = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_STRUCT) {
          xfer += this->rfnIdentifier.read(iprot);
          isset_rfnIdentifier = true;
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

  if (!isset_rfnIdentifier)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t RfnMeterReadRequest::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("RfnMeterReadRequest");

  xfer += oprot->writeFieldBegin("rfnIdentifier", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->rfnIdentifier.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(RfnMeterReadRequest &a, RfnMeterReadRequest &b) {
  using ::std::swap;
  swap(a.rfnIdentifier, b.rfnIdentifier);
}

RfnMeterReadRequest::RfnMeterReadRequest(const RfnMeterReadRequest& other0) {
  rfnIdentifier = other0.rfnIdentifier;
}
RfnMeterReadRequest& RfnMeterReadRequest::operator=(const RfnMeterReadRequest& other1) {
  rfnIdentifier = other1.rfnIdentifier;
  return *this;
}
void RfnMeterReadRequest::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "RfnMeterReadRequest(";
  out << "rfnIdentifier=" << to_string(rfnIdentifier);
  out << ")";
}


RfnMeterReadReply::~RfnMeterReadReply() noexcept {
}


void RfnMeterReadReply::__set_replyType(const RfnMeterReadingReplyType::type val) {
  this->replyType = val;
}
std::ostream& operator<<(std::ostream& out, const RfnMeterReadReply& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t RfnMeterReadReply::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset_replyType = false;

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
          int32_t ecast2;
          xfer += iprot->readI32(ecast2);
          this->replyType = (RfnMeterReadingReplyType::type)ecast2;
          isset_replyType = true;
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

  if (!isset_replyType)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t RfnMeterReadReply::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("RfnMeterReadReply");

  xfer += oprot->writeFieldBegin("replyType", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32((int32_t)this->replyType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(RfnMeterReadReply &a, RfnMeterReadReply &b) {
  using ::std::swap;
  swap(a.replyType, b.replyType);
}

RfnMeterReadReply::RfnMeterReadReply(const RfnMeterReadReply& other3) {
  replyType = other3.replyType;
}
RfnMeterReadReply& RfnMeterReadReply::operator=(const RfnMeterReadReply& other4) {
  replyType = other4.replyType;
  return *this;
}
void RfnMeterReadReply::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "RfnMeterReadReply(";
  out << "replyType=" << to_string(replyType);
  out << ")";
}


ChannelData::~ChannelData() noexcept {
}


void ChannelData::__set_channelNumber(const int32_t val) {
  this->channelNumber = val;
}

void ChannelData::__set_status(const ChannelDataStatus::type val) {
  this->status = val;
}

void ChannelData::__set_unitOfMeasure(const std::string& val) {
  this->unitOfMeasure = val;
}

void ChannelData::__set_unitOfMeasureModifiers(const std::set<std::string> & val) {
  this->unitOfMeasureModifiers = val;
}

void ChannelData::__set_value(const double val) {
  this->value = val;
}
std::ostream& operator<<(std::ostream& out, const ChannelData& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t ChannelData::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset_channelNumber = false;
  bool isset_status = false;
  bool isset_unitOfMeasure = false;
  bool isset_unitOfMeasureModifiers = false;
  bool isset_value = false;

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
          xfer += iprot->readI32(this->channelNumber);
          isset_channelNumber = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          int32_t ecast5;
          xfer += iprot->readI32(ecast5);
          this->status = (ChannelDataStatus::type)ecast5;
          isset_status = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->unitOfMeasure);
          isset_unitOfMeasure = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_SET) {
          {
            this->unitOfMeasureModifiers.clear();
            uint32_t _size6;
            ::apache::thrift::protocol::TType _etype9;
            xfer += iprot->readSetBegin(_etype9, _size6);
            uint32_t _i10;
            for (_i10 = 0; _i10 < _size6; ++_i10)
            {
              std::string _elem11;
              xfer += iprot->readString(_elem11);
              this->unitOfMeasureModifiers.insert(_elem11);
            }
            xfer += iprot->readSetEnd();
          }
          isset_unitOfMeasureModifiers = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_DOUBLE) {
          xfer += iprot->readDouble(this->value);
          isset_value = true;
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

  if (!isset_channelNumber)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset_status)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset_unitOfMeasure)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset_unitOfMeasureModifiers)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset_value)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t ChannelData::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("ChannelData");

  xfer += oprot->writeFieldBegin("channelNumber", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32(this->channelNumber);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("status", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32((int32_t)this->status);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("unitOfMeasure", ::apache::thrift::protocol::T_STRING, 3);
  xfer += oprot->writeString(this->unitOfMeasure);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("unitOfMeasureModifiers", ::apache::thrift::protocol::T_SET, 4);
  {
    xfer += oprot->writeSetBegin(::apache::thrift::protocol::T_STRING, static_cast<uint32_t>(this->unitOfMeasureModifiers.size()));
    std::set<std::string> ::const_iterator _iter12;
    for (_iter12 = this->unitOfMeasureModifiers.begin(); _iter12 != this->unitOfMeasureModifiers.end(); ++_iter12)
    {
      xfer += oprot->writeString((*_iter12));
    }
    xfer += oprot->writeSetEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("value", ::apache::thrift::protocol::T_DOUBLE, 5);
  xfer += oprot->writeDouble(this->value);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(ChannelData &a, ChannelData &b) {
  using ::std::swap;
  swap(a.channelNumber, b.channelNumber);
  swap(a.status, b.status);
  swap(a.unitOfMeasure, b.unitOfMeasure);
  swap(a.unitOfMeasureModifiers, b.unitOfMeasureModifiers);
  swap(a.value, b.value);
}

ChannelData::ChannelData(const ChannelData& other13) {
  channelNumber = other13.channelNumber;
  status = other13.status;
  unitOfMeasure = other13.unitOfMeasure;
  unitOfMeasureModifiers = other13.unitOfMeasureModifiers;
  value = other13.value;
}
ChannelData& ChannelData::operator=(const ChannelData& other14) {
  channelNumber = other14.channelNumber;
  status = other14.status;
  unitOfMeasure = other14.unitOfMeasure;
  unitOfMeasureModifiers = other14.unitOfMeasureModifiers;
  value = other14.value;
  return *this;
}
void ChannelData::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "ChannelData(";
  out << "channelNumber=" << to_string(channelNumber);
  out << ", " << "status=" << to_string(status);
  out << ", " << "unitOfMeasure=" << to_string(unitOfMeasure);
  out << ", " << "unitOfMeasureModifiers=" << to_string(unitOfMeasureModifiers);
  out << ", " << "value=" << to_string(value);
  out << ")";
}


DatedChannelData::~DatedChannelData() noexcept {
}


void DatedChannelData::__set_channelData(const ChannelData& val) {
  this->channelData = val;
}

void DatedChannelData::__set_timeStamp(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->timeStamp = val;
}

void DatedChannelData::__set_baseChannelData(const ChannelData& val) {
  this->baseChannelData = val;
__isset.baseChannelData = true;
}
std::ostream& operator<<(std::ostream& out, const DatedChannelData& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t DatedChannelData::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset_channelData = false;
  bool isset_timeStamp = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_STRUCT) {
          xfer += this->channelData.read(iprot);
          isset_channelData = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->timeStamp);
          isset_timeStamp = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRUCT) {
          xfer += this->baseChannelData.read(iprot);
          this->__isset.baseChannelData = true;
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

  if (!isset_channelData)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset_timeStamp)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t DatedChannelData::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("DatedChannelData");

  xfer += oprot->writeFieldBegin("channelData", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->channelData.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("timeStamp", ::apache::thrift::protocol::T_I64, 2);
  xfer += oprot->writeI64(this->timeStamp);
  xfer += oprot->writeFieldEnd();

  if (this->__isset.baseChannelData) {
    xfer += oprot->writeFieldBegin("baseChannelData", ::apache::thrift::protocol::T_STRUCT, 3);
    xfer += this->baseChannelData.write(oprot);
    xfer += oprot->writeFieldEnd();
  }
  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(DatedChannelData &a, DatedChannelData &b) {
  using ::std::swap;
  swap(a.channelData, b.channelData);
  swap(a.timeStamp, b.timeStamp);
  swap(a.baseChannelData, b.baseChannelData);
  swap(a.__isset, b.__isset);
}

DatedChannelData::DatedChannelData(const DatedChannelData& other15) {
  channelData = other15.channelData;
  timeStamp = other15.timeStamp;
  baseChannelData = other15.baseChannelData;
  __isset = other15.__isset;
}
DatedChannelData& DatedChannelData::operator=(const DatedChannelData& other16) {
  channelData = other16.channelData;
  timeStamp = other16.timeStamp;
  baseChannelData = other16.baseChannelData;
  __isset = other16.__isset;
  return *this;
}
void DatedChannelData::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "DatedChannelData(";
  out << "channelData=" << to_string(channelData);
  out << ", " << "timeStamp=" << to_string(timeStamp);
  out << ", " << "baseChannelData="; (__isset.baseChannelData ? (out << to_string(baseChannelData)) : (out << "<null>"));
  out << ")";
}


RfnMeterReadingData::~RfnMeterReadingData() noexcept {
}


void RfnMeterReadingData::__set_channelDataList(const std::vector<ChannelData> & val) {
  this->channelDataList = val;
}

void RfnMeterReadingData::__set_datedChannelDataList(const std::vector<DatedChannelData> & val) {
  this->datedChannelDataList = val;
}

void RfnMeterReadingData::__set_rfnIdentifier(const  ::Cti::Messaging::Serialization::Thrift::Rfn::RfnIdentifier& val) {
  this->rfnIdentifier = val;
}

void RfnMeterReadingData::__set_timeStamp(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->timeStamp = val;
}

void RfnMeterReadingData::__set_recordInterval(const int32_t val) {
  this->recordInterval = val;
}
std::ostream& operator<<(std::ostream& out, const RfnMeterReadingData& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t RfnMeterReadingData::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset_channelDataList = false;
  bool isset_datedChannelDataList = false;
  bool isset_rfnIdentifier = false;
  bool isset_timeStamp = false;
  bool isset_recordInterval = false;

  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->channelDataList.clear();
            uint32_t _size17;
            ::apache::thrift::protocol::TType _etype20;
            xfer += iprot->readListBegin(_etype20, _size17);
            this->channelDataList.resize(_size17);
            uint32_t _i21;
            for (_i21 = 0; _i21 < _size17; ++_i21)
            {
              xfer += this->channelDataList[_i21].read(iprot);
            }
            xfer += iprot->readListEnd();
          }
          isset_channelDataList = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->datedChannelDataList.clear();
            uint32_t _size22;
            ::apache::thrift::protocol::TType _etype25;
            xfer += iprot->readListBegin(_etype25, _size22);
            this->datedChannelDataList.resize(_size22);
            uint32_t _i26;
            for (_i26 = 0; _i26 < _size22; ++_i26)
            {
              xfer += this->datedChannelDataList[_i26].read(iprot);
            }
            xfer += iprot->readListEnd();
          }
          isset_datedChannelDataList = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRUCT) {
          xfer += this->rfnIdentifier.read(iprot);
          isset_rfnIdentifier = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->timeStamp);
          isset_timeStamp = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->recordInterval);
          isset_recordInterval = true;
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

  if (!isset_channelDataList)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset_datedChannelDataList)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset_rfnIdentifier)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset_timeStamp)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset_recordInterval)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t RfnMeterReadingData::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("RfnMeterReadingData");

  xfer += oprot->writeFieldBegin("channelDataList", ::apache::thrift::protocol::T_LIST, 1);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_STRUCT, static_cast<uint32_t>(this->channelDataList.size()));
    std::vector<ChannelData> ::const_iterator _iter27;
    for (_iter27 = this->channelDataList.begin(); _iter27 != this->channelDataList.end(); ++_iter27)
    {
      xfer += (*_iter27).write(oprot);
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("datedChannelDataList", ::apache::thrift::protocol::T_LIST, 2);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_STRUCT, static_cast<uint32_t>(this->datedChannelDataList.size()));
    std::vector<DatedChannelData> ::const_iterator _iter28;
    for (_iter28 = this->datedChannelDataList.begin(); _iter28 != this->datedChannelDataList.end(); ++_iter28)
    {
      xfer += (*_iter28).write(oprot);
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("rfnIdentifier", ::apache::thrift::protocol::T_STRUCT, 3);
  xfer += this->rfnIdentifier.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("timeStamp", ::apache::thrift::protocol::T_I64, 4);
  xfer += oprot->writeI64(this->timeStamp);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("recordInterval", ::apache::thrift::protocol::T_I32, 5);
  xfer += oprot->writeI32(this->recordInterval);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(RfnMeterReadingData &a, RfnMeterReadingData &b) {
  using ::std::swap;
  swap(a.channelDataList, b.channelDataList);
  swap(a.datedChannelDataList, b.datedChannelDataList);
  swap(a.rfnIdentifier, b.rfnIdentifier);
  swap(a.timeStamp, b.timeStamp);
  swap(a.recordInterval, b.recordInterval);
}

RfnMeterReadingData::RfnMeterReadingData(const RfnMeterReadingData& other29) {
  channelDataList = other29.channelDataList;
  datedChannelDataList = other29.datedChannelDataList;
  rfnIdentifier = other29.rfnIdentifier;
  timeStamp = other29.timeStamp;
  recordInterval = other29.recordInterval;
}
RfnMeterReadingData& RfnMeterReadingData::operator=(const RfnMeterReadingData& other30) {
  channelDataList = other30.channelDataList;
  datedChannelDataList = other30.datedChannelDataList;
  rfnIdentifier = other30.rfnIdentifier;
  timeStamp = other30.timeStamp;
  recordInterval = other30.recordInterval;
  return *this;
}
void RfnMeterReadingData::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "RfnMeterReadingData(";
  out << "channelDataList=" << to_string(channelDataList);
  out << ", " << "datedChannelDataList=" << to_string(datedChannelDataList);
  out << ", " << "rfnIdentifier=" << to_string(rfnIdentifier);
  out << ", " << "timeStamp=" << to_string(timeStamp);
  out << ", " << "recordInterval=" << to_string(recordInterval);
  out << ")";
}


RfnMeterReadDataReply::~RfnMeterReadDataReply() noexcept {
}


void RfnMeterReadDataReply::__set_replyType(const RfnMeterReadingDataReplyType::type val) {
  this->replyType = val;
}

void RfnMeterReadDataReply::__set_data(const RfnMeterReadingData& val) {
  this->data = val;
}
std::ostream& operator<<(std::ostream& out, const RfnMeterReadDataReply& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t RfnMeterReadDataReply::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset_replyType = false;
  bool isset_data = false;

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
          int32_t ecast31;
          xfer += iprot->readI32(ecast31);
          this->replyType = (RfnMeterReadingDataReplyType::type)ecast31;
          isset_replyType = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_STRUCT) {
          xfer += this->data.read(iprot);
          isset_data = true;
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

  if (!isset_replyType)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset_data)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t RfnMeterReadDataReply::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("RfnMeterReadDataReply");

  xfer += oprot->writeFieldBegin("replyType", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32((int32_t)this->replyType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("data", ::apache::thrift::protocol::T_STRUCT, 2);
  xfer += this->data.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(RfnMeterReadDataReply &a, RfnMeterReadDataReply &b) {
  using ::std::swap;
  swap(a.replyType, b.replyType);
  swap(a.data, b.data);
}

RfnMeterReadDataReply::RfnMeterReadDataReply(const RfnMeterReadDataReply& other32) {
  replyType = other32.replyType;
  data = other32.data;
}
RfnMeterReadDataReply& RfnMeterReadDataReply::operator=(const RfnMeterReadDataReply& other33) {
  replyType = other33.replyType;
  data = other33.data;
  return *this;
}
void RfnMeterReadDataReply::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "RfnMeterReadDataReply(";
  out << "replyType=" << to_string(replyType);
  out << ", " << "data=" << to_string(data);
  out << ")";
}

}}}} // namespace
