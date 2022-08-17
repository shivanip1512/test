/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "LMDynamicControlAreaData_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


LMDynamicControlAreaData::~LMDynamicControlAreaData() noexcept {
}


void LMDynamicControlAreaData::__set__paoId(const int32_t val) {
  this->_paoId = val;
}

void LMDynamicControlAreaData::__set__disableFlag(const int32_t val) {
  this->_disableFlag = val;
}

void LMDynamicControlAreaData::__set__nextCheckTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_nextCheckTime = val;
}

void LMDynamicControlAreaData::__set__controlAreaState(const int32_t val) {
  this->_controlAreaState = val;
}

void LMDynamicControlAreaData::__set__currentPriority(const int32_t val) {
  this->_currentPriority = val;
}

void LMDynamicControlAreaData::__set__currentDailyStartTime(const int32_t val) {
  this->_currentDailyStartTime = val;
}

void LMDynamicControlAreaData::__set__currentDailyStopTime(const int32_t val) {
  this->_currentDailyStopTime = val;
}

void LMDynamicControlAreaData::__set__triggers(const std::vector< ::Cti::Messaging::Serialization::Thrift::LMDynamicTriggerData> & val) {
  this->_triggers = val;
}
std::ostream& operator<<(std::ostream& out, const LMDynamicControlAreaData& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t LMDynamicControlAreaData::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__paoId = false;
  bool isset__disableFlag = false;
  bool isset__nextCheckTime = false;
  bool isset__controlAreaState = false;
  bool isset__currentPriority = false;
  bool isset__currentDailyStartTime = false;
  bool isset__currentDailyStopTime = false;
  bool isset__triggers = false;

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
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_disableFlag);
          isset__disableFlag = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_nextCheckTime);
          isset__nextCheckTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_controlAreaState);
          isset__controlAreaState = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_currentPriority);
          isset__currentPriority = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_currentDailyStartTime);
          isset__currentDailyStartTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_currentDailyStopTime);
          isset__currentDailyStopTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_triggers.clear();
            uint32_t _size0;
            ::apache::thrift::protocol::TType _etype3;
            xfer += iprot->readListBegin(_etype3, _size0);
            this->_triggers.resize(_size0);
            uint32_t _i4;
            for (_i4 = 0; _i4 < _size0; ++_i4)
            {
              xfer += this->_triggers[_i4].read(iprot);
            }
            xfer += iprot->readListEnd();
          }
          isset__triggers = true;
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
  if (!isset__nextCheckTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__controlAreaState)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__currentPriority)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__currentDailyStartTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__currentDailyStopTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__triggers)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t LMDynamicControlAreaData::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("LMDynamicControlAreaData");

  xfer += oprot->writeFieldBegin("_paoId", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32(this->_paoId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_disableFlag", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_disableFlag);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_nextCheckTime", ::apache::thrift::protocol::T_I64, 3);
  xfer += oprot->writeI64(this->_nextCheckTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_controlAreaState", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->_controlAreaState);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_currentPriority", ::apache::thrift::protocol::T_I32, 5);
  xfer += oprot->writeI32(this->_currentPriority);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_currentDailyStartTime", ::apache::thrift::protocol::T_I32, 6);
  xfer += oprot->writeI32(this->_currentDailyStartTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_currentDailyStopTime", ::apache::thrift::protocol::T_I32, 7);
  xfer += oprot->writeI32(this->_currentDailyStopTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_triggers", ::apache::thrift::protocol::T_LIST, 8);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_STRUCT, static_cast<uint32_t>(this->_triggers.size()));
    std::vector< ::Cti::Messaging::Serialization::Thrift::LMDynamicTriggerData> ::const_iterator _iter5;
    for (_iter5 = this->_triggers.begin(); _iter5 != this->_triggers.end(); ++_iter5)
    {
      xfer += (*_iter5).write(oprot);
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(LMDynamicControlAreaData &a, LMDynamicControlAreaData &b) {
  using ::std::swap;
  swap(a._paoId, b._paoId);
  swap(a._disableFlag, b._disableFlag);
  swap(a._nextCheckTime, b._nextCheckTime);
  swap(a._controlAreaState, b._controlAreaState);
  swap(a._currentPriority, b._currentPriority);
  swap(a._currentDailyStartTime, b._currentDailyStartTime);
  swap(a._currentDailyStopTime, b._currentDailyStopTime);
  swap(a._triggers, b._triggers);
}

LMDynamicControlAreaData::LMDynamicControlAreaData(const LMDynamicControlAreaData& other6) {
  _paoId = other6._paoId;
  _disableFlag = other6._disableFlag;
  _nextCheckTime = other6._nextCheckTime;
  _controlAreaState = other6._controlAreaState;
  _currentPriority = other6._currentPriority;
  _currentDailyStartTime = other6._currentDailyStartTime;
  _currentDailyStopTime = other6._currentDailyStopTime;
  _triggers = other6._triggers;
}
LMDynamicControlAreaData& LMDynamicControlAreaData::operator=(const LMDynamicControlAreaData& other7) {
  _paoId = other7._paoId;
  _disableFlag = other7._disableFlag;
  _nextCheckTime = other7._nextCheckTime;
  _controlAreaState = other7._controlAreaState;
  _currentPriority = other7._currentPriority;
  _currentDailyStartTime = other7._currentDailyStartTime;
  _currentDailyStopTime = other7._currentDailyStopTime;
  _triggers = other7._triggers;
  return *this;
}
void LMDynamicControlAreaData::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "LMDynamicControlAreaData(";
  out << "_paoId=" << to_string(_paoId);
  out << ", " << "_disableFlag=" << to_string(_disableFlag);
  out << ", " << "_nextCheckTime=" << to_string(_nextCheckTime);
  out << ", " << "_controlAreaState=" << to_string(_controlAreaState);
  out << ", " << "_currentPriority=" << to_string(_currentPriority);
  out << ", " << "_currentDailyStartTime=" << to_string(_currentDailyStartTime);
  out << ", " << "_currentDailyStopTime=" << to_string(_currentDailyStopTime);
  out << ", " << "_triggers=" << to_string(_triggers);
  out << ")";
}

}}}} // namespace
