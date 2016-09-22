/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "LMManualControlResponse_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* LMConstraintViolation::ascii_fingerprint = "869476CDD019ED62DB5C09DABA4F105F";
const uint8_t LMConstraintViolation::binary_fingerprint[16] = {0x86,0x94,0x76,0xCD,0xD0,0x19,0xED,0x62,0xDB,0x5C,0x09,0xDA,0xBA,0x4F,0x10,0x5F};

uint32_t LMConstraintViolation::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__errorCode = false;
  bool isset__doubleParams = false;
  bool isset__integerParams = false;
  bool isset__stringParams = false;
  bool isset__datetimeParams = false;

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
          xfer += iprot->readI32(this->_errorCode);
          isset__errorCode = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_doubleParams.clear();
            uint32_t _size0;
            ::apache::thrift::protocol::TType _etype3;
            xfer += iprot->readListBegin(_etype3, _size0);
            this->_doubleParams.resize(_size0);
            uint32_t _i4;
            for (_i4 = 0; _i4 < _size0; ++_i4)
            {
              xfer += iprot->readDouble(this->_doubleParams[_i4]);
            }
            xfer += iprot->readListEnd();
          }
          isset__doubleParams = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_integerParams.clear();
            uint32_t _size5;
            ::apache::thrift::protocol::TType _etype8;
            xfer += iprot->readListBegin(_etype8, _size5);
            this->_integerParams.resize(_size5);
            uint32_t _i9;
            for (_i9 = 0; _i9 < _size5; ++_i9)
            {
              xfer += iprot->readI32(this->_integerParams[_i9]);
            }
            xfer += iprot->readListEnd();
          }
          isset__integerParams = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_stringParams.clear();
            uint32_t _size10;
            ::apache::thrift::protocol::TType _etype13;
            xfer += iprot->readListBegin(_etype13, _size10);
            this->_stringParams.resize(_size10);
            uint32_t _i14;
            for (_i14 = 0; _i14 < _size10; ++_i14)
            {
              xfer += iprot->readString(this->_stringParams[_i14]);
            }
            xfer += iprot->readListEnd();
          }
          isset__stringParams = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_datetimeParams.clear();
            uint32_t _size15;
            ::apache::thrift::protocol::TType _etype18;
            xfer += iprot->readListBegin(_etype18, _size15);
            this->_datetimeParams.resize(_size15);
            uint32_t _i19;
            for (_i19 = 0; _i19 < _size15; ++_i19)
            {
              xfer += iprot->readI64(this->_datetimeParams[_i19]);
            }
            xfer += iprot->readListEnd();
          }
          isset__datetimeParams = true;
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

  if (!isset__errorCode)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__doubleParams)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__integerParams)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__stringParams)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__datetimeParams)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t LMConstraintViolation::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("LMConstraintViolation");

  xfer += oprot->writeFieldBegin("_errorCode", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32(this->_errorCode);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_doubleParams", ::apache::thrift::protocol::T_LIST, 2);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_DOUBLE, static_cast<uint32_t>(this->_doubleParams.size()));
    std::vector<double> ::const_iterator _iter20;
    for (_iter20 = this->_doubleParams.begin(); _iter20 != this->_doubleParams.end(); ++_iter20)
    {
      xfer += oprot->writeDouble((*_iter20));
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_integerParams", ::apache::thrift::protocol::T_LIST, 3);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_I32, static_cast<uint32_t>(this->_integerParams.size()));
    std::vector<int32_t> ::const_iterator _iter21;
    for (_iter21 = this->_integerParams.begin(); _iter21 != this->_integerParams.end(); ++_iter21)
    {
      xfer += oprot->writeI32((*_iter21));
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_stringParams", ::apache::thrift::protocol::T_LIST, 4);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_STRING, static_cast<uint32_t>(this->_stringParams.size()));
    std::vector<std::string> ::const_iterator _iter22;
    for (_iter22 = this->_stringParams.begin(); _iter22 != this->_stringParams.end(); ++_iter22)
    {
      xfer += oprot->writeString((*_iter22));
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_datetimeParams", ::apache::thrift::protocol::T_LIST, 5);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_I64, static_cast<uint32_t>(this->_datetimeParams.size()));
    std::vector< ::Cti::Messaging::Serialization::Thrift::Timestamp> ::const_iterator _iter23;
    for (_iter23 = this->_datetimeParams.begin(); _iter23 != this->_datetimeParams.end(); ++_iter23)
    {
      xfer += oprot->writeI64((*_iter23));
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(LMConstraintViolation &a, LMConstraintViolation &b) {
  using ::std::swap;
  swap(a._errorCode, b._errorCode);
  swap(a._doubleParams, b._doubleParams);
  swap(a._integerParams, b._integerParams);
  swap(a._stringParams, b._stringParams);
  swap(a._datetimeParams, b._datetimeParams);
}

const char* LMManualControlResponse::ascii_fingerprint = "F425FDEBF3F494680FC8117D3BE7F6FF";
const uint8_t LMManualControlResponse::binary_fingerprint[16] = {0xF4,0x25,0xFD,0xEB,0xF3,0xF4,0x94,0x68,0x0F,0xC8,0x11,0x7D,0x3B,0xE7,0xF6,0xFF};

uint32_t LMManualControlResponse::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__paoId = false;
  bool isset__constraintViolations = false;
  bool isset__bestFitAction = false;

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
          xfer += iprot->readI32(this->_paoId);
          isset__paoId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_constraintViolations.clear();
            uint32_t _size24;
            ::apache::thrift::protocol::TType _etype27;
            xfer += iprot->readListBegin(_etype27, _size24);
            this->_constraintViolations.resize(_size24);
            uint32_t _i28;
            for (_i28 = 0; _i28 < _size24; ++_i28)
            {
              xfer += this->_constraintViolations[_i28].read(iprot);
            }
            xfer += iprot->readListEnd();
          }
          isset__constraintViolations = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_bestFitAction);
          isset__bestFitAction = true;
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
  if (!isset__paoId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__constraintViolations)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__bestFitAction)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t LMManualControlResponse::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("LMManualControlResponse");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_paoId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_paoId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_constraintViolations", ::apache::thrift::protocol::T_LIST, 3);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_STRUCT, static_cast<uint32_t>(this->_constraintViolations.size()));
    std::vector<LMConstraintViolation> ::const_iterator _iter29;
    for (_iter29 = this->_constraintViolations.begin(); _iter29 != this->_constraintViolations.end(); ++_iter29)
    {
      xfer += (*_iter29).write(oprot);
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_bestFitAction", ::apache::thrift::protocol::T_STRING, 4);
  xfer += oprot->writeString(this->_bestFitAction);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(LMManualControlResponse &a, LMManualControlResponse &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._paoId, b._paoId);
  swap(a._constraintViolations, b._constraintViolations);
  swap(a._bestFitAction, b._bestFitAction);
}

}}}} // namespace
