/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "LMDynamicProgramData_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


LMDynamicProgramData::~LMDynamicProgramData() throw() {
}


void LMDynamicProgramData::__set__paoId(const int32_t val) {
  this->_paoId = val;
}

void LMDynamicProgramData::__set__disableFlag(const bool val) {
  this->_disableFlag = val;
}

void LMDynamicProgramData::__set__currentGearNumber(const int32_t val) {
  this->_currentGearNumber = val;
}

void LMDynamicProgramData::__set__lastGroupControlled(const int32_t val) {
  this->_lastGroupControlled = val;
}

void LMDynamicProgramData::__set__programState(const int32_t val) {
  this->_programState = val;
}

void LMDynamicProgramData::__set__reductionTotal(const double val) {
  this->_reductionTotal = val;
}

void LMDynamicProgramData::__set__directStartTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_directStartTime = val;
}

void LMDynamicProgramData::__set__directStopTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_directStopTime = val;
}

void LMDynamicProgramData::__set__notifyActiveTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_notifyActiveTime = val;
}

void LMDynamicProgramData::__set__notifyInactiveTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_notifyInactiveTime = val;
}

void LMDynamicProgramData::__set__startedRampingOutTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_startedRampingOutTime = val;
}
std::ostream& operator<<(std::ostream& out, const LMDynamicProgramData& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t LMDynamicProgramData::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__paoId = false;
  bool isset__disableFlag = false;
  bool isset__currentGearNumber = false;
  bool isset__lastGroupControlled = false;
  bool isset__programState = false;
  bool isset__reductionTotal = false;
  bool isset__directStartTime = false;
  bool isset__directStopTime = false;
  bool isset__notifyActiveTime = false;
  bool isset__notifyInactiveTime = false;
  bool isset__startedRampingOutTime = false;

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
          xfer += iprot->readI32(this->_paoId);
          isset__paoId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_disableFlag);
          isset__disableFlag = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_currentGearNumber);
          isset__currentGearNumber = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_lastGroupControlled);
          isset__lastGroupControlled = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_programState);
          isset__programState = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_DOUBLE) {
          xfer += iprot->readDouble(this->_reductionTotal);
          isset__reductionTotal = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_directStartTime);
          isset__directStartTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_directStopTime);
          isset__directStopTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 9:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_notifyActiveTime);
          isset__notifyActiveTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 10:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_notifyInactiveTime);
          isset__notifyInactiveTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 11:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_startedRampingOutTime);
          isset__startedRampingOutTime = true;
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

  if (!isset__paoId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__disableFlag)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__currentGearNumber)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__lastGroupControlled)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__programState)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__reductionTotal)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__directStartTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__directStopTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__notifyActiveTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__notifyInactiveTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__startedRampingOutTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t LMDynamicProgramData::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("LMDynamicProgramData");

  xfer += oprot->writeFieldBegin("_paoId", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32(this->_paoId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_disableFlag", ::apache::thrift::protocol::T_BOOL, 2);
  xfer += oprot->writeBool(this->_disableFlag);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_currentGearNumber", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_currentGearNumber);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_lastGroupControlled", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->_lastGroupControlled);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_programState", ::apache::thrift::protocol::T_I32, 5);
  xfer += oprot->writeI32(this->_programState);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_reductionTotal", ::apache::thrift::protocol::T_DOUBLE, 6);
  xfer += oprot->writeDouble(this->_reductionTotal);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_directStartTime", ::apache::thrift::protocol::T_I64, 7);
  xfer += oprot->writeI64(this->_directStartTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_directStopTime", ::apache::thrift::protocol::T_I64, 8);
  xfer += oprot->writeI64(this->_directStopTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_notifyActiveTime", ::apache::thrift::protocol::T_I64, 9);
  xfer += oprot->writeI64(this->_notifyActiveTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_notifyInactiveTime", ::apache::thrift::protocol::T_I64, 10);
  xfer += oprot->writeI64(this->_notifyInactiveTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_startedRampingOutTime", ::apache::thrift::protocol::T_I64, 11);
  xfer += oprot->writeI64(this->_startedRampingOutTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(LMDynamicProgramData &a, LMDynamicProgramData &b) {
  using ::std::swap;
  swap(a._paoId, b._paoId);
  swap(a._disableFlag, b._disableFlag);
  swap(a._currentGearNumber, b._currentGearNumber);
  swap(a._lastGroupControlled, b._lastGroupControlled);
  swap(a._programState, b._programState);
  swap(a._reductionTotal, b._reductionTotal);
  swap(a._directStartTime, b._directStartTime);
  swap(a._directStopTime, b._directStopTime);
  swap(a._notifyActiveTime, b._notifyActiveTime);
  swap(a._notifyInactiveTime, b._notifyInactiveTime);
  swap(a._startedRampingOutTime, b._startedRampingOutTime);
}

LMDynamicProgramData::LMDynamicProgramData(const LMDynamicProgramData& other0) {
  _paoId = other0._paoId;
  _disableFlag = other0._disableFlag;
  _currentGearNumber = other0._currentGearNumber;
  _lastGroupControlled = other0._lastGroupControlled;
  _programState = other0._programState;
  _reductionTotal = other0._reductionTotal;
  _directStartTime = other0._directStartTime;
  _directStopTime = other0._directStopTime;
  _notifyActiveTime = other0._notifyActiveTime;
  _notifyInactiveTime = other0._notifyInactiveTime;
  _startedRampingOutTime = other0._startedRampingOutTime;
}
LMDynamicProgramData& LMDynamicProgramData::operator=(const LMDynamicProgramData& other1) {
  _paoId = other1._paoId;
  _disableFlag = other1._disableFlag;
  _currentGearNumber = other1._currentGearNumber;
  _lastGroupControlled = other1._lastGroupControlled;
  _programState = other1._programState;
  _reductionTotal = other1._reductionTotal;
  _directStartTime = other1._directStartTime;
  _directStopTime = other1._directStopTime;
  _notifyActiveTime = other1._notifyActiveTime;
  _notifyInactiveTime = other1._notifyInactiveTime;
  _startedRampingOutTime = other1._startedRampingOutTime;
  return *this;
}
void LMDynamicProgramData::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "LMDynamicProgramData(";
  out << "_paoId=" << to_string(_paoId);
  out << ", " << "_disableFlag=" << to_string(_disableFlag);
  out << ", " << "_currentGearNumber=" << to_string(_currentGearNumber);
  out << ", " << "_lastGroupControlled=" << to_string(_lastGroupControlled);
  out << ", " << "_programState=" << to_string(_programState);
  out << ", " << "_reductionTotal=" << to_string(_reductionTotal);
  out << ", " << "_directStartTime=" << to_string(_directStartTime);
  out << ", " << "_directStopTime=" << to_string(_directStopTime);
  out << ", " << "_notifyActiveTime=" << to_string(_notifyActiveTime);
  out << ", " << "_notifyInactiveTime=" << to_string(_notifyInactiveTime);
  out << ", " << "_startedRampingOutTime=" << to_string(_startedRampingOutTime);
  out << ")";
}

}}}} // namespace
