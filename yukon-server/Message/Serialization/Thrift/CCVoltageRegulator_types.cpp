/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCVoltageRegulator_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


CCVoltageRegulatorItem::~CCVoltageRegulatorItem() noexcept {
}


void CCVoltageRegulatorItem::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCPao& val) {
  this->_baseMessage = val;
}

void CCVoltageRegulatorItem::__set__parentId(const int32_t val) {
  this->_parentId = val;
}

void CCVoltageRegulatorItem::__set__lastTapOperation(const int32_t val) {
  this->_lastTapOperation = val;
}

void CCVoltageRegulatorItem::__set__lastTapOperationTime(const  ::Cti::Messaging::Serialization::Thrift::Timestamp val) {
  this->_lastTapOperationTime = val;
}

void CCVoltageRegulatorItem::__set__regulatorType(const int32_t val) {
  this->_regulatorType = val;
}

void CCVoltageRegulatorItem::__set__recentTapOperation(const bool val) {
  this->_recentTapOperation = val;
}

void CCVoltageRegulatorItem::__set__lastOperatingMode(const int32_t val) {
  this->_lastOperatingMode = val;
}

void CCVoltageRegulatorItem::__set__lastCommandedOperatingMode(const int32_t val) {
  this->_lastCommandedOperatingMode = val;
}
std::ostream& operator<<(std::ostream& out, const CCVoltageRegulatorItem& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCVoltageRegulatorItem::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__parentId = false;
  bool isset__lastTapOperation = false;
  bool isset__lastTapOperationTime = false;
  bool isset__regulatorType = false;
  bool isset__recentTapOperation = false;
  bool isset__lastOperatingMode = false;
  bool isset__lastCommandedOperatingMode = false;

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
          xfer += this->_baseMessage.read(iprot);
          isset__baseMessage = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_parentId);
          isset__parentId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_lastTapOperation);
          isset__lastTapOperation = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_lastTapOperationTime);
          isset__lastTapOperationTime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_regulatorType);
          isset__regulatorType = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_recentTapOperation);
          isset__recentTapOperation = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_lastOperatingMode);
          isset__lastOperatingMode = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_lastCommandedOperatingMode);
          isset__lastCommandedOperatingMode = true;
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

  if (!isset__baseMessage)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__parentId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__lastTapOperation)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__lastTapOperationTime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__regulatorType)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__recentTapOperation)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__lastOperatingMode)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__lastCommandedOperatingMode)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCVoltageRegulatorItem::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCVoltageRegulatorItem");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_parentId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_parentId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_lastTapOperation", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_lastTapOperation);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_lastTapOperationTime", ::apache::thrift::protocol::T_I64, 4);
  xfer += oprot->writeI64(this->_lastTapOperationTime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_regulatorType", ::apache::thrift::protocol::T_I32, 5);
  xfer += oprot->writeI32(this->_regulatorType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_recentTapOperation", ::apache::thrift::protocol::T_BOOL, 6);
  xfer += oprot->writeBool(this->_recentTapOperation);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_lastOperatingMode", ::apache::thrift::protocol::T_I32, 7);
  xfer += oprot->writeI32(this->_lastOperatingMode);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_lastCommandedOperatingMode", ::apache::thrift::protocol::T_I32, 8);
  xfer += oprot->writeI32(this->_lastCommandedOperatingMode);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCVoltageRegulatorItem &a, CCVoltageRegulatorItem &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._parentId, b._parentId);
  swap(a._lastTapOperation, b._lastTapOperation);
  swap(a._lastTapOperationTime, b._lastTapOperationTime);
  swap(a._regulatorType, b._regulatorType);
  swap(a._recentTapOperation, b._recentTapOperation);
  swap(a._lastOperatingMode, b._lastOperatingMode);
  swap(a._lastCommandedOperatingMode, b._lastCommandedOperatingMode);
}

CCVoltageRegulatorItem::CCVoltageRegulatorItem(const CCVoltageRegulatorItem& other0) {
  _baseMessage = other0._baseMessage;
  _parentId = other0._parentId;
  _lastTapOperation = other0._lastTapOperation;
  _lastTapOperationTime = other0._lastTapOperationTime;
  _regulatorType = other0._regulatorType;
  _recentTapOperation = other0._recentTapOperation;
  _lastOperatingMode = other0._lastOperatingMode;
  _lastCommandedOperatingMode = other0._lastCommandedOperatingMode;
}
CCVoltageRegulatorItem& CCVoltageRegulatorItem::operator=(const CCVoltageRegulatorItem& other1) {
  _baseMessage = other1._baseMessage;
  _parentId = other1._parentId;
  _lastTapOperation = other1._lastTapOperation;
  _lastTapOperationTime = other1._lastTapOperationTime;
  _regulatorType = other1._regulatorType;
  _recentTapOperation = other1._recentTapOperation;
  _lastOperatingMode = other1._lastOperatingMode;
  _lastCommandedOperatingMode = other1._lastCommandedOperatingMode;
  return *this;
}
void CCVoltageRegulatorItem::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCVoltageRegulatorItem(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_parentId=" << to_string(_parentId);
  out << ", " << "_lastTapOperation=" << to_string(_lastTapOperation);
  out << ", " << "_lastTapOperationTime=" << to_string(_lastTapOperationTime);
  out << ", " << "_regulatorType=" << to_string(_regulatorType);
  out << ", " << "_recentTapOperation=" << to_string(_recentTapOperation);
  out << ", " << "_lastOperatingMode=" << to_string(_lastOperatingMode);
  out << ", " << "_lastCommandedOperatingMode=" << to_string(_lastCommandedOperatingMode);
  out << ")";
}


CCVoltageRegulator::~CCVoltageRegulator() noexcept {
}


void CCVoltageRegulator::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCMessage& val) {
  this->_baseMessage = val;
}

void CCVoltageRegulator::__set__regulators(const std::vector<CCVoltageRegulatorItem> & val) {
  this->_regulators = val;
}
std::ostream& operator<<(std::ostream& out, const CCVoltageRegulator& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCVoltageRegulator::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__regulators = false;

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
          xfer += this->_baseMessage.read(iprot);
          isset__baseMessage = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_regulators.clear();
            uint32_t _size2;
            ::apache::thrift::protocol::TType _etype5;
            xfer += iprot->readListBegin(_etype5, _size2);
            this->_regulators.resize(_size2);
            uint32_t _i6;
            for (_i6 = 0; _i6 < _size2; ++_i6)
            {
              xfer += this->_regulators[_i6].read(iprot);
            }
            xfer += iprot->readListEnd();
          }
          isset__regulators = true;
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

  if (!isset__baseMessage)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__regulators)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCVoltageRegulator::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCVoltageRegulator");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_regulators", ::apache::thrift::protocol::T_LIST, 2);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_STRUCT, static_cast<uint32_t>(this->_regulators.size()));
    std::vector<CCVoltageRegulatorItem> ::const_iterator _iter7;
    for (_iter7 = this->_regulators.begin(); _iter7 != this->_regulators.end(); ++_iter7)
    {
      xfer += (*_iter7).write(oprot);
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCVoltageRegulator &a, CCVoltageRegulator &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._regulators, b._regulators);
}

CCVoltageRegulator::CCVoltageRegulator(const CCVoltageRegulator& other8) {
  _baseMessage = other8._baseMessage;
  _regulators = other8._regulators;
}
CCVoltageRegulator& CCVoltageRegulator::operator=(const CCVoltageRegulator& other9) {
  _baseMessage = other9._baseMessage;
  _regulators = other9._regulators;
  return *this;
}
void CCVoltageRegulator::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCVoltageRegulator(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_regulators=" << to_string(_regulators);
  out << ")";
}

}}}} // namespace
