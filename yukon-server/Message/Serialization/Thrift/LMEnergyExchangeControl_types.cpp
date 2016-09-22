/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "LMEnergyExchangeControl_types.h"

#include <algorithm>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

const char* LMEnergyExchangeControl::ascii_fingerprint = "F2FED4BD5CCAC11779B20AEF73FC9568";
const uint8_t LMEnergyExchangeControl::binary_fingerprint[16] = {0xF2,0xFE,0xD4,0xBD,0x5C,0xCA,0xC1,0x17,0x79,0xB2,0x0A,0xEF,0x73,0xFC,0x95,0x68};

uint32_t LMEnergyExchangeControl::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__command = false;
  bool isset__paoId = false;
  bool isset__offerId = false;
  bool isset__offerDate = false;
  bool isset__notificationDatetime = false;
  bool isset__expirationDatetime = false;
  bool isset__additionalInfo = false;
  bool isset__amountsRequested = false;
  bool isset__pricesOffered = false;

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
          xfer += iprot->readI32(this->_command);
          isset__command = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_paoId);
          isset__paoId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_offerId);
          isset__offerId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_offerDate);
          isset__offerDate = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_notificationDatetime);
          isset__notificationDatetime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->_expirationDatetime);
          isset__expirationDatetime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_additionalInfo);
          isset__additionalInfo = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 9:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_amountsRequested.clear();
            uint32_t _size0;
            ::apache::thrift::protocol::TType _etype3;
            xfer += iprot->readListBegin(_etype3, _size0);
            this->_amountsRequested.resize(_size0);
            uint32_t _i4;
            for (_i4 = 0; _i4 < _size0; ++_i4)
            {
              xfer += iprot->readDouble(this->_amountsRequested[_i4]);
            }
            xfer += iprot->readListEnd();
          }
          isset__amountsRequested = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 10:
        if (ftype == ::apache::thrift::protocol::T_LIST) {
          {
            this->_pricesOffered.clear();
            uint32_t _size5;
            ::apache::thrift::protocol::TType _etype8;
            xfer += iprot->readListBegin(_etype8, _size5);
            this->_pricesOffered.resize(_size5);
            uint32_t _i9;
            for (_i9 = 0; _i9 < _size5; ++_i9)
            {
              xfer += iprot->readI32(this->_pricesOffered[_i9]);
            }
            xfer += iprot->readListEnd();
          }
          isset__pricesOffered = true;
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
  if (!isset__command)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__paoId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__offerId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__offerDate)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__notificationDatetime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__expirationDatetime)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__additionalInfo)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__amountsRequested)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__pricesOffered)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t LMEnergyExchangeControl::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("LMEnergyExchangeControl");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_command", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_command);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_paoId", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_paoId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_offerId", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->_offerId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_offerDate", ::apache::thrift::protocol::T_I64, 5);
  xfer += oprot->writeI64(this->_offerDate);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_notificationDatetime", ::apache::thrift::protocol::T_I64, 6);
  xfer += oprot->writeI64(this->_notificationDatetime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_expirationDatetime", ::apache::thrift::protocol::T_I64, 7);
  xfer += oprot->writeI64(this->_expirationDatetime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_additionalInfo", ::apache::thrift::protocol::T_STRING, 8);
  xfer += oprot->writeString(this->_additionalInfo);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_amountsRequested", ::apache::thrift::protocol::T_LIST, 9);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_DOUBLE, static_cast<uint32_t>(this->_amountsRequested.size()));
    std::vector<double> ::const_iterator _iter10;
    for (_iter10 = this->_amountsRequested.begin(); _iter10 != this->_amountsRequested.end(); ++_iter10)
    {
      xfer += oprot->writeDouble((*_iter10));
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_pricesOffered", ::apache::thrift::protocol::T_LIST, 10);
  {
    xfer += oprot->writeListBegin(::apache::thrift::protocol::T_I32, static_cast<uint32_t>(this->_pricesOffered.size()));
    std::vector<int32_t> ::const_iterator _iter11;
    for (_iter11 = this->_pricesOffered.begin(); _iter11 != this->_pricesOffered.end(); ++_iter11)
    {
      xfer += oprot->writeI32((*_iter11));
    }
    xfer += oprot->writeListEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(LMEnergyExchangeControl &a, LMEnergyExchangeControl &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._command, b._command);
  swap(a._paoId, b._paoId);
  swap(a._offerId, b._offerId);
  swap(a._offerDate, b._offerDate);
  swap(a._notificationDatetime, b._notificationDatetime);
  swap(a._expirationDatetime, b._expirationDatetime);
  swap(a._additionalInfo, b._additionalInfo);
  swap(a._amountsRequested, b._amountsRequested);
  swap(a._pricesOffered, b._pricesOffered);
}

}}}} // namespace
