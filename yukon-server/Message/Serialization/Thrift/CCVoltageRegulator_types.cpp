/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCVoltageRegulator_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* CCVoltageRegulatorItem::ascii_fingerprint = "D87F3EE58AAEAB56DCFDFAC674FC0E27";
const uint8_t CCVoltageRegulatorItem::binary_fingerprint[16] = {0xD8,0x7F,0x3E,0xE5,0x8A,0xAE,0xAB,0x56,0xDC,0xFD,0xFA,0xC6,0x74,0xFC,0x0E,0x27};

uint32_t CCVoltageRegulatorItem::read(::apache::thrift::protocol::TProtocol* iprot) {

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

const char* CCVoltageRegulator::ascii_fingerprint = "C1EF3CA1AB399EFA499138D224A62CBE";
const uint8_t CCVoltageRegulator::binary_fingerprint[16] = {0xC1,0xEF,0x3C,0xA1,0xAB,0x39,0x9E,0xFA,0x49,0x91,0x38,0xD2,0x24,0xA6,0x2C,0xBE};

uint32_t CCVoltageRegulator::read(::apache::thrift::protocol::TProtocol* iprot) {

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
            uint32_t _size0;
            ::apache::thrift::protocol::TType _etype3;
            xfer += iprot->readListBegin(_etype3, _size0);
            this->_regulators.resize(_size0);
            uint32_t _i4;
            for (_i4 = 0; _i4 < _size0; ++_i4)
            {
              xfer += this->_regulators[_i4].read(iprot);
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
  xfer += oprot->writeStructBegin("CCVoltageRegulator");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_regulators", ::apache::thrift::protocol::T_LIST, 2);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_STRUCT, static_cast<uint32_t>(this->_regulators.size()));
    std::vector<CCVoltageRegulatorItem> ::const_iterator _iter5;
    for (_iter5 = this->_regulators.begin(); _iter5 != this->_regulators.end(); ++_iter5)
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

void swap(CCVoltageRegulator &a, CCVoltageRegulator &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._regulators, b._regulators);
}

}}}} // namespace
