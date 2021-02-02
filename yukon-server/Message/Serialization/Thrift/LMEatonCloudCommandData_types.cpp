/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "LMEatonCloudCommandData_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

int _kLMEatonCloudCycleTypeValues[] = {
  LMEatonCloudCycleType::STANDARD,
  LMEatonCloudCycleType::TRUE_CYCLE,
  LMEatonCloudCycleType::SMART_CYCLE
};
const char* _kLMEatonCloudCycleTypeNames[] = {
  "STANDARD",
  "TRUE_CYCLE",
  "SMART_CYCLE"
};
const std::map<int, const char*> _LMEatonCloudCycleType_VALUES_TO_NAMES(::apache::thrift::TEnumIterator(3, _kLMEatonCloudCycleTypeValues, _kLMEatonCloudCycleTypeNames), ::apache::thrift::TEnumIterator(-1, NULL, NULL));

std::ostream& operator<<(std::ostream& out, const LMEatonCloudCycleType::type& val) {
  std::map<int, const char*>::const_iterator it = _LMEatonCloudCycleType_VALUES_TO_NAMES.find(val);
  if (it != _LMEatonCloudCycleType_VALUES_TO_NAMES.end()) {
    out << it->second;
  } else {
    out << static_cast<int>(val);
  }
  return out;
}

std::string to_string(const LMEatonCloudCycleType::type& val) {
  std::map<int, const char*>::const_iterator it = _LMEatonCloudCycleType_VALUES_TO_NAMES.find(val);
  if (it != _LMEatonCloudCycleType_VALUES_TO_NAMES.end()) {
    return std::string(it->second);
  } else {
    return std::to_string(static_cast<int>(val));
  }
}

int _kLMEatonCloudStopTypeValues[] = {
  LMEatonCloudStopType::RESTORE,
  LMEatonCloudStopType::STOP_CYCLE
};
const char* _kLMEatonCloudStopTypeNames[] = {
  "RESTORE",
  "STOP_CYCLE"
};
const std::map<int, const char*> _LMEatonCloudStopType_VALUES_TO_NAMES(::apache::thrift::TEnumIterator(2, _kLMEatonCloudStopTypeValues, _kLMEatonCloudStopTypeNames), ::apache::thrift::TEnumIterator(-1, NULL, NULL));

std::ostream& operator<<(std::ostream& out, const LMEatonCloudStopType::type& val) {
  std::map<int, const char*>::const_iterator it = _LMEatonCloudStopType_VALUES_TO_NAMES.find(val);
  if (it != _LMEatonCloudStopType_VALUES_TO_NAMES.end()) {
    out << it->second;
  } else {
    out << static_cast<int>(val);
  }
  return out;
}

std::string to_string(const LMEatonCloudStopType::type& val) {
  std::map<int, const char*>::const_iterator it = _LMEatonCloudStopType_VALUES_TO_NAMES.find(val);
  if (it != _LMEatonCloudStopType_VALUES_TO_NAMES.end()) {
    return std::string(it->second);
  } else {
    return std::to_string(static_cast<int>(val));
  }
}


LMEatonCloudScheduledCycleCommand::~LMEatonCloudScheduledCycleCommand() noexcept {
}


void LMEatonCloudScheduledCycleCommand::__set__groupId(const int32_t val) {
  this->_groupId = val;
}

void LMEatonCloudScheduledCycleCommand::__set__controlStartDateTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_controlStartDateTime = val;
}

void LMEatonCloudScheduledCycleCommand::__set__controlEndDateTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_controlEndDateTime = val;
}

void LMEatonCloudScheduledCycleCommand::__set__isRampIn(const bool val) {
  this->_isRampIn = val;
}

void LMEatonCloudScheduledCycleCommand::__set__isRampOut(const bool val) {
  this->_isRampOut = val;
}

void LMEatonCloudScheduledCycleCommand::__set__cyclingOption(const LMEatonCloudCycleType::type val) {
  this->_cyclingOption = val;
}

void LMEatonCloudScheduledCycleCommand::__set__dutyCyclePercentage(const int32_t val) {
  this->_dutyCyclePercentage = val;
}

void LMEatonCloudScheduledCycleCommand::__set__dutyCyclePeriod(const int32_t val) {
  this->_dutyCyclePeriod = val;
}

void LMEatonCloudScheduledCycleCommand::__set__criticality(const int32_t val) {
  this->_criticality = val;
}
std::ostream& operator<<(std::ostream& out, const LMEatonCloudScheduledCycleCommand& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t LMEatonCloudScheduledCycleCommand::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__groupId = false;
  bool isset__controlStartDateTime = false;
  bool isset__controlEndDateTime = false;
  bool isset__isRampIn = false;
  bool isset__isRampOut = false;
  bool isset__cyclingOption = false;
  bool isset__dutyCyclePercentage = false;
  bool isset__dutyCyclePeriod = false;
  bool isset__criticality = false;

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
          xfer += iprot->readI32(this->_groupId);
          isset__groupId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_controlStartDateTime);
          isset__controlStartDateTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_controlEndDateTime);
          isset__controlEndDateTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_isRampIn);
          isset__isRampIn = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_isRampOut);
          isset__isRampOut = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          int32_t ecast0;
          xfer += iprot->readI32(ecast0);
          this->_cyclingOption = (LMEatonCloudCycleType::type)ecast0;
          isset__cyclingOption = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_dutyCyclePercentage);
          isset__dutyCyclePercentage = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_dutyCyclePeriod);
          isset__dutyCyclePeriod = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 9:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_criticality);
          isset__criticality = true;
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

  if (!isset__groupId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__controlStartDateTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__controlEndDateTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__isRampIn)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__isRampOut)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__cyclingOption)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__dutyCyclePercentage)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__dutyCyclePeriod)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__criticality)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t LMEatonCloudScheduledCycleCommand::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("LMEatonCloudScheduledCycleCommand");

  xfer += oprot->writeFieldBegin("_groupId", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32(this->_groupId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_controlStartDateTime", ::apache::thrift::protocol::T_I64, 2);
  xfer += oprot->writeI64(this->_controlStartDateTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_controlEndDateTime", ::apache::thrift::protocol::T_I64, 3);
  xfer += oprot->writeI64(this->_controlEndDateTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_isRampIn", ::apache::thrift::protocol::T_BOOL, 4);
  xfer += oprot->writeBool(this->_isRampIn);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_isRampOut", ::apache::thrift::protocol::T_BOOL, 5);
  xfer += oprot->writeBool(this->_isRampOut);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_cyclingOption", ::apache::thrift::protocol::T_I32, 6);
  xfer += oprot->writeI32((int32_t)this->_cyclingOption);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_dutyCyclePercentage", ::apache::thrift::protocol::T_I32, 7);
  xfer += oprot->writeI32(this->_dutyCyclePercentage);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_dutyCyclePeriod", ::apache::thrift::protocol::T_I32, 8);
  xfer += oprot->writeI32(this->_dutyCyclePeriod);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_criticality", ::apache::thrift::protocol::T_I32, 9);
  xfer += oprot->writeI32(this->_criticality);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(LMEatonCloudScheduledCycleCommand &a, LMEatonCloudScheduledCycleCommand &b) {
  using ::std::swap;
  swap(a._groupId, b._groupId);
  swap(a._controlStartDateTime, b._controlStartDateTime);
  swap(a._controlEndDateTime, b._controlEndDateTime);
  swap(a._isRampIn, b._isRampIn);
  swap(a._isRampOut, b._isRampOut);
  swap(a._cyclingOption, b._cyclingOption);
  swap(a._dutyCyclePercentage, b._dutyCyclePercentage);
  swap(a._dutyCyclePeriod, b._dutyCyclePeriod);
  swap(a._criticality, b._criticality);
}

LMEatonCloudScheduledCycleCommand::LMEatonCloudScheduledCycleCommand(const LMEatonCloudScheduledCycleCommand& other1) {
  _groupId = other1._groupId;
  _controlStartDateTime = other1._controlStartDateTime;
  _controlEndDateTime = other1._controlEndDateTime;
  _isRampIn = other1._isRampIn;
  _isRampOut = other1._isRampOut;
  _cyclingOption = other1._cyclingOption;
  _dutyCyclePercentage = other1._dutyCyclePercentage;
  _dutyCyclePeriod = other1._dutyCyclePeriod;
  _criticality = other1._criticality;
}
LMEatonCloudScheduledCycleCommand& LMEatonCloudScheduledCycleCommand::operator=(const LMEatonCloudScheduledCycleCommand& other2) {
  _groupId = other2._groupId;
  _controlStartDateTime = other2._controlStartDateTime;
  _controlEndDateTime = other2._controlEndDateTime;
  _isRampIn = other2._isRampIn;
  _isRampOut = other2._isRampOut;
  _cyclingOption = other2._cyclingOption;
  _dutyCyclePercentage = other2._dutyCyclePercentage;
  _dutyCyclePeriod = other2._dutyCyclePeriod;
  _criticality = other2._criticality;
  return *this;
}
void LMEatonCloudScheduledCycleCommand::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "LMEatonCloudScheduledCycleCommand(";
  out << "_groupId=" << to_string(_groupId);
  out << ", " << "_controlStartDateTime=" << to_string(_controlStartDateTime);
  out << ", " << "_controlEndDateTime=" << to_string(_controlEndDateTime);
  out << ", " << "_isRampIn=" << to_string(_isRampIn);
  out << ", " << "_isRampOut=" << to_string(_isRampOut);
  out << ", " << "_cyclingOption=" << to_string(_cyclingOption);
  out << ", " << "_dutyCyclePercentage=" << to_string(_dutyCyclePercentage);
  out << ", " << "_dutyCyclePeriod=" << to_string(_dutyCyclePeriod);
  out << ", " << "_criticality=" << to_string(_criticality);
  out << ")";
}


LMEatonCloudStopCommand::~LMEatonCloudStopCommand() noexcept {
}


void LMEatonCloudStopCommand::__set__groupId(const int32_t val) {
  this->_groupId = val;
}

void LMEatonCloudStopCommand::__set__restoreTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_restoreTime = val;
}

void LMEatonCloudStopCommand::__set__stopType(const LMEatonCloudStopType::type val) {
  this->_stopType = val;
}
std::ostream& operator<<(std::ostream& out, const LMEatonCloudStopCommand& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t LMEatonCloudStopCommand::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__groupId = false;
  bool isset__restoreTime = false;
  bool isset__stopType = false;

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
          xfer += iprot->readI32(this->_groupId);
          isset__groupId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_restoreTime);
          isset__restoreTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          int32_t ecast3;
          xfer += iprot->readI32(ecast3);
          this->_stopType = (LMEatonCloudStopType::type)ecast3;
          isset__stopType = true;
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

  if (!isset__groupId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__restoreTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__stopType)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t LMEatonCloudStopCommand::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("LMEatonCloudStopCommand");

  xfer += oprot->writeFieldBegin("_groupId", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32(this->_groupId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_restoreTime", ::apache::thrift::protocol::T_I64, 2);
  xfer += oprot->writeI64(this->_restoreTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_stopType", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32((int32_t)this->_stopType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(LMEatonCloudStopCommand &a, LMEatonCloudStopCommand &b) {
  using ::std::swap;
  swap(a._groupId, b._groupId);
  swap(a._restoreTime, b._restoreTime);
  swap(a._stopType, b._stopType);
}

LMEatonCloudStopCommand::LMEatonCloudStopCommand(const LMEatonCloudStopCommand& other4) {
  _groupId = other4._groupId;
  _restoreTime = other4._restoreTime;
  _stopType = other4._stopType;
}
LMEatonCloudStopCommand& LMEatonCloudStopCommand::operator=(const LMEatonCloudStopCommand& other5) {
  _groupId = other5._groupId;
  _restoreTime = other5._restoreTime;
  _stopType = other5._stopType;
  return *this;
}
void LMEatonCloudStopCommand::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "LMEatonCloudStopCommand(";
  out << "_groupId=" << to_string(_groupId);
  out << ", " << "_restoreTime=" << to_string(_restoreTime);
  out << ", " << "_stopType=" << to_string(_stopType);
  out << ")";
}

}}}} // namespace
