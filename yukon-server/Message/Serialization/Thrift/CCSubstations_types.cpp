/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCSubstations_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


CCSubstationItem::~CCSubstationItem() noexcept {
}


void CCSubstationItem::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCPao& val) {
  this->_baseMessage = val;
}

void CCSubstationItem::__set__parentId(const int32_t val) {
  this->_parentId = val;
}

void CCSubstationItem::__set__ovUvDisabledFlag(const bool val) {
  this->_ovUvDisabledFlag = val;
}

void CCSubstationItem::__set__subBusIds(const std::vector<int32_t> & val) {
  this->_subBusIds = val;
}

void CCSubstationItem::__set__pfDisplayValue(const double val) {
  this->_pfDisplayValue = val;
}

void CCSubstationItem::__set__estPfDisplayValue(const double val) {
  this->_estPfDisplayValue = val;
}

void CCSubstationItem::__set__saEnabledFlag(const bool val) {
  this->_saEnabledFlag = val;
}

void CCSubstationItem::__set__saEnabledId(const int32_t val) {
  this->_saEnabledId = val;
}

void CCSubstationItem::__set__voltReductionFlag(const bool val) {
  this->_voltReductionFlag = val;
}

void CCSubstationItem::__set__recentlyControlledFlag(const bool val) {
  this->_recentlyControlledFlag = val;
}

void CCSubstationItem::__set__childVoltReductionFlag(const bool val) {
  this->_childVoltReductionFlag = val;
}
std::ostream& operator<<(std::ostream& out, const CCSubstationItem& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCSubstationItem::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__parentId = false;
  bool isset__ovUvDisabledFlag = false;
  bool isset__subBusIds = false;
  bool isset__pfDisplayValue = false;
  bool isset__estPfDisplayValue = false;
  bool isset__saEnabledFlag = false;
  bool isset__saEnabledId = false;
  bool isset__voltReductionFlag = false;
  bool isset__recentlyControlledFlag = false;
  bool isset__childVoltReductionFlag = false;

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
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_ovUvDisabledFlag);
          isset__ovUvDisabledFlag = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_subBusIds.clear();
            uint32_t _size0;
            ::apache::thrift::protocol::TType _etype3;
            xfer += iprot->readListBegin(_etype3, _size0);
            this->_subBusIds.resize(_size0);
            uint32_t _i4;
            for (_i4 = 0; _i4 < _size0; ++_i4)
            {
              xfer += iprot->readI32(this->_subBusIds[_i4]);
            }
            xfer += iprot->readListEnd();
          }
          isset__subBusIds = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_DOUBLE) {
          xfer += iprot->readDouble(this->_pfDisplayValue);
          isset__pfDisplayValue = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_DOUBLE) {
          xfer += iprot->readDouble(this->_estPfDisplayValue);
          isset__estPfDisplayValue = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_saEnabledFlag);
          isset__saEnabledFlag = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 9:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_saEnabledId);
          isset__saEnabledId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 10:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_voltReductionFlag);
          isset__voltReductionFlag = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 11:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_recentlyControlledFlag);
          isset__recentlyControlledFlag = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 12:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_childVoltReductionFlag);
          isset__childVoltReductionFlag = true;
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
  if (!isset__ovUvDisabledFlag)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__subBusIds)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__pfDisplayValue)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__estPfDisplayValue)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__saEnabledFlag)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__saEnabledId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__voltReductionFlag)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__recentlyControlledFlag)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__childVoltReductionFlag)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCSubstationItem::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCSubstationItem");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_parentId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_parentId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_ovUvDisabledFlag", ::apache::thrift::protocol::T_BOOL, 3);
  xfer += oprot->writeBool(this->_ovUvDisabledFlag);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_subBusIds", ::apache::thrift::protocol::T_LIST, 5);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_I32, static_cast<uint32_t>(this->_subBusIds.size()));
    std::vector<int32_t> ::const_iterator _iter5;
    for (_iter5 = this->_subBusIds.begin(); _iter5 != this->_subBusIds.end(); ++_iter5)
    {
      xfer += oprot->writeI32((*_iter5));
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_pfDisplayValue", ::apache::thrift::protocol::T_DOUBLE, 6);
  xfer += oprot->writeDouble(this->_pfDisplayValue);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_estPfDisplayValue", ::apache::thrift::protocol::T_DOUBLE, 7);
  xfer += oprot->writeDouble(this->_estPfDisplayValue);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_saEnabledFlag", ::apache::thrift::protocol::T_BOOL, 8);
  xfer += oprot->writeBool(this->_saEnabledFlag);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_saEnabledId", ::apache::thrift::protocol::T_I32, 9);
  xfer += oprot->writeI32(this->_saEnabledId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_voltReductionFlag", ::apache::thrift::protocol::T_BOOL, 10);
  xfer += oprot->writeBool(this->_voltReductionFlag);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_recentlyControlledFlag", ::apache::thrift::protocol::T_BOOL, 11);
  xfer += oprot->writeBool(this->_recentlyControlledFlag);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_childVoltReductionFlag", ::apache::thrift::protocol::T_BOOL, 12);
  xfer += oprot->writeBool(this->_childVoltReductionFlag);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCSubstationItem &a, CCSubstationItem &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._parentId, b._parentId);
  swap(a._ovUvDisabledFlag, b._ovUvDisabledFlag);
  swap(a._subBusIds, b._subBusIds);
  swap(a._pfDisplayValue, b._pfDisplayValue);
  swap(a._estPfDisplayValue, b._estPfDisplayValue);
  swap(a._saEnabledFlag, b._saEnabledFlag);
  swap(a._saEnabledId, b._saEnabledId);
  swap(a._voltReductionFlag, b._voltReductionFlag);
  swap(a._recentlyControlledFlag, b._recentlyControlledFlag);
  swap(a._childVoltReductionFlag, b._childVoltReductionFlag);
}

CCSubstationItem::CCSubstationItem(const CCSubstationItem& other6) {
  _baseMessage = other6._baseMessage;
  _parentId = other6._parentId;
  _ovUvDisabledFlag = other6._ovUvDisabledFlag;
  _subBusIds = other6._subBusIds;
  _pfDisplayValue = other6._pfDisplayValue;
  _estPfDisplayValue = other6._estPfDisplayValue;
  _saEnabledFlag = other6._saEnabledFlag;
  _saEnabledId = other6._saEnabledId;
  _voltReductionFlag = other6._voltReductionFlag;
  _recentlyControlledFlag = other6._recentlyControlledFlag;
  _childVoltReductionFlag = other6._childVoltReductionFlag;
}
CCSubstationItem& CCSubstationItem::operator=(const CCSubstationItem& other7) {
  _baseMessage = other7._baseMessage;
  _parentId = other7._parentId;
  _ovUvDisabledFlag = other7._ovUvDisabledFlag;
  _subBusIds = other7._subBusIds;
  _pfDisplayValue = other7._pfDisplayValue;
  _estPfDisplayValue = other7._estPfDisplayValue;
  _saEnabledFlag = other7._saEnabledFlag;
  _saEnabledId = other7._saEnabledId;
  _voltReductionFlag = other7._voltReductionFlag;
  _recentlyControlledFlag = other7._recentlyControlledFlag;
  _childVoltReductionFlag = other7._childVoltReductionFlag;
  return *this;
}
void CCSubstationItem::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCSubstationItem(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_parentId=" << to_string(_parentId);
  out << ", " << "_ovUvDisabledFlag=" << to_string(_ovUvDisabledFlag);
  out << ", " << "_subBusIds=" << to_string(_subBusIds);
  out << ", " << "_pfDisplayValue=" << to_string(_pfDisplayValue);
  out << ", " << "_estPfDisplayValue=" << to_string(_estPfDisplayValue);
  out << ", " << "_saEnabledFlag=" << to_string(_saEnabledFlag);
  out << ", " << "_saEnabledId=" << to_string(_saEnabledId);
  out << ", " << "_voltReductionFlag=" << to_string(_voltReductionFlag);
  out << ", " << "_recentlyControlledFlag=" << to_string(_recentlyControlledFlag);
  out << ", " << "_childVoltReductionFlag=" << to_string(_childVoltReductionFlag);
  out << ")";
}


CCSubstations::~CCSubstations() noexcept {
}


void CCSubstations::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCMessage& val) {
  this->_baseMessage = val;
}

void CCSubstations::__set__msgInfoBitMask(const int32_t val) {
  this->_msgInfoBitMask = val;
}

void CCSubstations::__set__ccSubstations(const std::vector<CCSubstationItem> & val) {
  this->_ccSubstations = val;
}
std::ostream& operator<<(std::ostream& out, const CCSubstations& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCSubstations::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__msgInfoBitMask = false;
  bool isset__ccSubstations = false;

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
          xfer += iprot->readI32(this->_msgInfoBitMask);
          isset__msgInfoBitMask = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_ccSubstations.clear();
            uint32_t _size8;
            ::apache::thrift::protocol::TType _etype11;
            xfer += iprot->readListBegin(_etype11, _size8);
            this->_ccSubstations.resize(_size8);
            uint32_t _i12;
            for (_i12 = 0; _i12 < _size8; ++_i12)
            {
              xfer += this->_ccSubstations[_i12].read(iprot);
            }
            xfer += iprot->readListEnd();
          }
          isset__ccSubstations = true;
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
  if (!isset__msgInfoBitMask)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__ccSubstations)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCSubstations::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCSubstations");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_msgInfoBitMask", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_msgInfoBitMask);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_ccSubstations", ::apache::thrift::protocol::T_LIST, 3);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_STRUCT, static_cast<uint32_t>(this->_ccSubstations.size()));
    std::vector<CCSubstationItem> ::const_iterator _iter13;
    for (_iter13 = this->_ccSubstations.begin(); _iter13 != this->_ccSubstations.end(); ++_iter13)
    {
      xfer += (*_iter13).write(oprot);
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCSubstations &a, CCSubstations &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._msgInfoBitMask, b._msgInfoBitMask);
  swap(a._ccSubstations, b._ccSubstations);
}

CCSubstations::CCSubstations(const CCSubstations& other14) {
  _baseMessage = other14._baseMessage;
  _msgInfoBitMask = other14._msgInfoBitMask;
  _ccSubstations = other14._ccSubstations;
}
CCSubstations& CCSubstations::operator=(const CCSubstations& other15) {
  _baseMessage = other15._baseMessage;
  _msgInfoBitMask = other15._msgInfoBitMask;
  _ccSubstations = other15._ccSubstations;
  return *this;
}
void CCSubstations::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCSubstations(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_msgInfoBitMask=" << to_string(_msgInfoBitMask);
  out << ", " << "_ccSubstations=" << to_string(_ccSubstations);
  out << ")";
}

}}}} // namespace
