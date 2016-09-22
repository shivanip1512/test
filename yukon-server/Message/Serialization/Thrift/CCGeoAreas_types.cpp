/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCGeoAreas_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* CCArea::ascii_fingerprint = "354A95A629FF6F8D8412BB5D265CA11A";
const uint8_t CCArea::binary_fingerprint[16] = {0x35,0x4A,0x95,0xA6,0x29,0xFF,0x6F,0x8D,0x84,0x12,0xBB,0x5D,0x26,0x5C,0xA1,0x1A};

uint32_t CCArea::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__ovUvDisabledFlag = false;
  bool isset__substationIds = false;
  bool isset__pfDisplayValue = false;
  bool isset__estPfDisplayValue = false;
  bool isset__voltReductionControlValue = false;
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
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_ovUvDisabledFlag);
          isset__ovUvDisabledFlag = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_substationIds.clear();
            uint32_t _size0;
            ::apache::thrift::protocol::TType _etype3;
            xfer += iprot->readListBegin(_etype3, _size0);
            this->_substationIds.resize(_size0);
            uint32_t _i4;
            for (_i4 = 0; _i4 < _size0; ++_i4)
            {
              xfer += iprot->readI32(this->_substationIds[_i4]);
            }
            xfer += iprot->readListEnd();
          }
          isset__substationIds = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_DOUBLE) {
          xfer += iprot->readDouble(this->_pfDisplayValue);
          isset__pfDisplayValue = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_DOUBLE) {
          xfer += iprot->readDouble(this->_estPfDisplayValue);
          isset__estPfDisplayValue = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_voltReductionControlValue);
          isset__voltReductionControlValue = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
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
  if (!isset__ovUvDisabledFlag)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__substationIds)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__pfDisplayValue)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__estPfDisplayValue)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__voltReductionControlValue)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__childVoltReductionFlag)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCArea::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("CCArea");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_ovUvDisabledFlag", ::apache::thrift::protocol::T_BOOL, 2);
  xfer += oprot->writeBool(this->_ovUvDisabledFlag);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_substationIds", ::apache::thrift::protocol::T_LIST, 3);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_I32, static_cast<uint32_t>(this->_substationIds.size()));
    std::vector<int32_t> ::const_iterator _iter5;
    for (_iter5 = this->_substationIds.begin(); _iter5 != this->_substationIds.end(); ++_iter5)
    {
      xfer += oprot->writeI32((*_iter5));
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_pfDisplayValue", ::apache::thrift::protocol::T_DOUBLE, 4);
  xfer += oprot->writeDouble(this->_pfDisplayValue);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_estPfDisplayValue", ::apache::thrift::protocol::T_DOUBLE, 5);
  xfer += oprot->writeDouble(this->_estPfDisplayValue);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_voltReductionControlValue", ::apache::thrift::protocol::T_BOOL, 6);
  xfer += oprot->writeBool(this->_voltReductionControlValue);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_childVoltReductionFlag", ::apache::thrift::protocol::T_BOOL, 7);
  xfer += oprot->writeBool(this->_childVoltReductionFlag);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCArea &a, CCArea &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._ovUvDisabledFlag, b._ovUvDisabledFlag);
  swap(a._substationIds, b._substationIds);
  swap(a._pfDisplayValue, b._pfDisplayValue);
  swap(a._estPfDisplayValue, b._estPfDisplayValue);
  swap(a._voltReductionControlValue, b._voltReductionControlValue);
  swap(a._childVoltReductionFlag, b._childVoltReductionFlag);
}

const char* CCGeoAreas::ascii_fingerprint = "FC0D48D2A6CC32140759C9F73F947ACC";
const uint8_t CCGeoAreas::binary_fingerprint[16] = {0xFC,0x0D,0x48,0xD2,0xA6,0xCC,0x32,0x14,0x07,0x59,0xC9,0xF7,0x3F,0x94,0x7A,0xCC};

uint32_t CCGeoAreas::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__msgInfoBitMask = false;
  bool isset__ccGeoAreas = false;

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
            this->_ccGeoAreas.clear();
            uint32_t _size6;
            ::apache::thrift::protocol::TType _etype9;
            xfer += iprot->readListBegin(_etype9, _size6);
            this->_ccGeoAreas.resize(_size6);
            uint32_t _i10;
            for (_i10 = 0; _i10 < _size6; ++_i10)
            {
              xfer += this->_ccGeoAreas[_i10].read(iprot);
            }
            xfer += iprot->readListEnd();
          }
          isset__ccGeoAreas = true;
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
  if (!isset__ccGeoAreas)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCGeoAreas::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("CCGeoAreas");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_msgInfoBitMask", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_msgInfoBitMask);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_ccGeoAreas", ::apache::thrift::protocol::T_LIST, 3);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_STRUCT, static_cast<uint32_t>(this->_ccGeoAreas.size()));
    std::vector<CCArea> ::const_iterator _iter11;
    for (_iter11 = this->_ccGeoAreas.begin(); _iter11 != this->_ccGeoAreas.end(); ++_iter11)
    {
      xfer += (*_iter11).write(oprot);
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCGeoAreas &a, CCGeoAreas &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._msgInfoBitMask, b._msgInfoBitMask);
  swap(a._ccGeoAreas, b._ccGeoAreas);
}

}}}} // namespace
