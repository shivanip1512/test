/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "PorterDynamicPaoInfo_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift { namespace Porter {

int _kDynamicPaoInfoDurationKeysValues[] = {
  DynamicPaoInfoDurationKeys::RFN_VOLTAGE_PROFILE_INTERVAL,
  DynamicPaoInfoDurationKeys::MCT_IED_LOAD_PROFILE_INTERVAL
};
const char* _kDynamicPaoInfoDurationKeysNames[] = {
  "RFN_VOLTAGE_PROFILE_INTERVAL",
  "MCT_IED_LOAD_PROFILE_INTERVAL"
};
const std::map<int, const char*> _DynamicPaoInfoDurationKeys_VALUES_TO_NAMES(::apache::thrift::TEnumIterator(2, _kDynamicPaoInfoDurationKeysValues, _kDynamicPaoInfoDurationKeysNames), ::apache::thrift::TEnumIterator(-1, NULL, NULL));

std::ostream& operator<<(std::ostream& out, const DynamicPaoInfoDurationKeys::type& val) {
  std::map<int, const char*>::const_iterator it = _DynamicPaoInfoDurationKeys_VALUES_TO_NAMES.find(val);
  if (it != _DynamicPaoInfoDurationKeys_VALUES_TO_NAMES.end()) {
    out << it->second;
  } else {
    out << static_cast<int>(val);
  }
  return out;
}

int _kDynamicPaoInfoTimestampKeysValues[] = {
  DynamicPaoInfoTimestampKeys::RFN_VOLTAGE_PROFILE_ENABLED_UNTIL
};
const char* _kDynamicPaoInfoTimestampKeysNames[] = {
  "RFN_VOLTAGE_PROFILE_ENABLED_UNTIL"
};
const std::map<int, const char*> _DynamicPaoInfoTimestampKeys_VALUES_TO_NAMES(::apache::thrift::TEnumIterator(1, _kDynamicPaoInfoTimestampKeysValues, _kDynamicPaoInfoTimestampKeysNames), ::apache::thrift::TEnumIterator(-1, NULL, NULL));

std::ostream& operator<<(std::ostream& out, const DynamicPaoInfoTimestampKeys::type& val) {
  std::map<int, const char*>::const_iterator it = _DynamicPaoInfoTimestampKeys_VALUES_TO_NAMES.find(val);
  if (it != _DynamicPaoInfoTimestampKeys_VALUES_TO_NAMES.end()) {
    out << it->second;
  } else {
    out << static_cast<int>(val);
  }
  return out;
}

int _kDynamicPaoInfoPercentageKeysValues[] = {
  DynamicPaoInfoPercentageKeys::METER_PROGRAMMING_PROGRESS
};
const char* _kDynamicPaoInfoPercentageKeysNames[] = {
  "METER_PROGRAMMING_PROGRESS"
};
const std::map<int, const char*> _DynamicPaoInfoPercentageKeys_VALUES_TO_NAMES(::apache::thrift::TEnumIterator(1, _kDynamicPaoInfoPercentageKeysValues, _kDynamicPaoInfoPercentageKeysNames), ::apache::thrift::TEnumIterator(-1, NULL, NULL));

std::ostream& operator<<(std::ostream& out, const DynamicPaoInfoPercentageKeys::type& val) {
  std::map<int, const char*>::const_iterator it = _DynamicPaoInfoPercentageKeys_VALUES_TO_NAMES.find(val);
  if (it != _DynamicPaoInfoPercentageKeys_VALUES_TO_NAMES.end()) {
    out << it->second;
  } else {
    out << static_cast<int>(val);
  }
  return out;
}


DynamicPaoInfoRequest::~DynamicPaoInfoRequest() throw() {
}


void DynamicPaoInfoRequest::__set__deviceId(const int32_t val) {
  this->_deviceId = val;
}

void DynamicPaoInfoRequest::__set__durationKeys(const std::set<DynamicPaoInfoDurationKeys::type> & val) {
  this->_durationKeys = val;
}

void DynamicPaoInfoRequest::__set__timestampKeys(const std::set<DynamicPaoInfoTimestampKeys::type> & val) {
  this->_timestampKeys = val;
}

void DynamicPaoInfoRequest::__set__percentageKeys(const std::set<DynamicPaoInfoPercentageKeys::type> & val) {
  this->_percentageKeys = val;
}
std::ostream& operator<<(std::ostream& out, const DynamicPaoInfoRequest& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t DynamicPaoInfoRequest::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__deviceId = false;
  bool isset__durationKeys = false;
  bool isset__timestampKeys = false;
  bool isset__percentageKeys = false;

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
          xfer += iprot->readI32(this->_deviceId);
          isset__deviceId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_SET) {
          {
            this->_durationKeys.clear();
            uint32_t _size0;
            ::apache::thrift::protocol::TType _etype3;
            xfer += iprot->readSetBegin(_etype3, _size0);
            uint32_t _i4;
            for (_i4 = 0; _i4 < _size0; ++_i4)
            {
              DynamicPaoInfoDurationKeys::type _elem5;
              int32_t ecast6;
              xfer += iprot->readI32(ecast6);
              _elem5 = (DynamicPaoInfoDurationKeys::type)ecast6;
              this->_durationKeys.insert(_elem5);
            }
            xfer += iprot->readSetEnd();
          }
          isset__durationKeys = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_SET) {
          {
            this->_timestampKeys.clear();
            uint32_t _size7;
            ::apache::thrift::protocol::TType _etype10;
            xfer += iprot->readSetBegin(_etype10, _size7);
            uint32_t _i11;
            for (_i11 = 0; _i11 < _size7; ++_i11)
            {
              DynamicPaoInfoTimestampKeys::type _elem12;
              int32_t ecast13;
              xfer += iprot->readI32(ecast13);
              _elem12 = (DynamicPaoInfoTimestampKeys::type)ecast13;
              this->_timestampKeys.insert(_elem12);
            }
            xfer += iprot->readSetEnd();
          }
          isset__timestampKeys = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_SET) {
          {
            this->_percentageKeys.clear();
            uint32_t _size14;
            ::apache::thrift::protocol::TType _etype17;
            xfer += iprot->readSetBegin(_etype17, _size14);
            uint32_t _i18;
            for (_i18 = 0; _i18 < _size14; ++_i18)
            {
              DynamicPaoInfoPercentageKeys::type _elem19;
              int32_t ecast20;
              xfer += iprot->readI32(ecast20);
              _elem19 = (DynamicPaoInfoPercentageKeys::type)ecast20;
              this->_percentageKeys.insert(_elem19);
            }
            xfer += iprot->readSetEnd();
          }
          isset__percentageKeys = true;
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

  if (!isset__deviceId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__durationKeys)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__timestampKeys)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__percentageKeys)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t DynamicPaoInfoRequest::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("DynamicPaoInfoRequest");

  xfer += oprot->writeFieldBegin("_deviceId", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32(this->_deviceId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_durationKeys", ::apache::thrift::protocol::T_SET, 2);
  {
    xfer += oprot->writeSetBegin(::apache::thrift::protocol::T_I32, static_cast<uint32_t>(this->_durationKeys.size()));
    std::set<DynamicPaoInfoDurationKeys::type> ::const_iterator _iter21;
    for (_iter21 = this->_durationKeys.begin(); _iter21 != this->_durationKeys.end(); ++_iter21)
    {
      xfer += oprot->writeI32((int32_t)(*_iter21));
    }
    xfer += oprot->writeSetEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_timestampKeys", ::apache::thrift::protocol::T_SET, 3);
  {
    xfer += oprot->writeSetBegin(::apache::thrift::protocol::T_I32, static_cast<uint32_t>(this->_timestampKeys.size()));
    std::set<DynamicPaoInfoTimestampKeys::type> ::const_iterator _iter22;
    for (_iter22 = this->_timestampKeys.begin(); _iter22 != this->_timestampKeys.end(); ++_iter22)
    {
      xfer += oprot->writeI32((int32_t)(*_iter22));
    }
    xfer += oprot->writeSetEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_percentageKeys", ::apache::thrift::protocol::T_SET, 4);
  {
    xfer += oprot->writeSetBegin(::apache::thrift::protocol::T_I32, static_cast<uint32_t>(this->_percentageKeys.size()));
    std::set<DynamicPaoInfoPercentageKeys::type> ::const_iterator _iter23;
    for (_iter23 = this->_percentageKeys.begin(); _iter23 != this->_percentageKeys.end(); ++_iter23)
    {
      xfer += oprot->writeI32((int32_t)(*_iter23));
    }
    xfer += oprot->writeSetEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(DynamicPaoInfoRequest &a, DynamicPaoInfoRequest &b) {
  using ::std::swap;
  swap(a._deviceId, b._deviceId);
  swap(a._durationKeys, b._durationKeys);
  swap(a._timestampKeys, b._timestampKeys);
  swap(a._percentageKeys, b._percentageKeys);
}

DynamicPaoInfoRequest::DynamicPaoInfoRequest(const DynamicPaoInfoRequest& other24) {
  _deviceId = other24._deviceId;
  _durationKeys = other24._durationKeys;
  _timestampKeys = other24._timestampKeys;
  _percentageKeys = other24._percentageKeys;
}
DynamicPaoInfoRequest& DynamicPaoInfoRequest::operator=(const DynamicPaoInfoRequest& other25) {
  _deviceId = other25._deviceId;
  _durationKeys = other25._durationKeys;
  _timestampKeys = other25._timestampKeys;
  _percentageKeys = other25._percentageKeys;
  return *this;
}
void DynamicPaoInfoRequest::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "DynamicPaoInfoRequest(";
  out << "_deviceId=" << to_string(_deviceId);
  out << ", " << "_durationKeys=" << to_string(_durationKeys);
  out << ", " << "_timestampKeys=" << to_string(_timestampKeys);
  out << ", " << "_percentageKeys=" << to_string(_percentageKeys);
  out << ")";
}


DynamicPaoInfoResponse::~DynamicPaoInfoResponse() throw() {
}


void DynamicPaoInfoResponse::__set__deviceId(const int32_t val) {
  this->_deviceId = val;
}

void DynamicPaoInfoResponse::__set__durationValues(const std::map<DynamicPaoInfoDurationKeys::type, int64_t> & val) {
  this->_durationValues = val;
}

void DynamicPaoInfoResponse::__set__timestampValues(const std::map<DynamicPaoInfoTimestampKeys::type, int64_t> & val) {
  this->_timestampValues = val;
}

void DynamicPaoInfoResponse::__set__percentageValues(const std::map<DynamicPaoInfoPercentageKeys::type, double> & val) {
  this->_percentageValues = val;
}
std::ostream& operator<<(std::ostream& out, const DynamicPaoInfoResponse& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t DynamicPaoInfoResponse::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__deviceId = false;
  bool isset__durationValues = false;
  bool isset__timestampValues = false;
  bool isset__percentageValues = false;

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
          xfer += iprot->readI32(this->_deviceId);
          isset__deviceId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_MAP) {
          {
            this->_durationValues.clear();
            uint32_t _size26;
            ::apache::thrift::protocol::TType _ktype27;
            ::apache::thrift::protocol::TType _vtype28;
            xfer += iprot->readMapBegin(_ktype27, _vtype28, _size26);
            uint32_t _i30;
            for (_i30 = 0; _i30 < _size26; ++_i30)
            {
              DynamicPaoInfoDurationKeys::type _key31;
              int32_t ecast33;
              xfer += iprot->readI32(ecast33);
              _key31 = (DynamicPaoInfoDurationKeys::type)ecast33;
              int64_t& _val32 = this->_durationValues[_key31];
              xfer += iprot->readI64(_val32);
            }
            xfer += iprot->readMapEnd();
          }
          isset__durationValues = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_MAP) {
          {
            this->_timestampValues.clear();
            uint32_t _size34;
            ::apache::thrift::protocol::TType _ktype35;
            ::apache::thrift::protocol::TType _vtype36;
            xfer += iprot->readMapBegin(_ktype35, _vtype36, _size34);
            uint32_t _i38;
            for (_i38 = 0; _i38 < _size34; ++_i38)
            {
              DynamicPaoInfoTimestampKeys::type _key39;
              int32_t ecast41;
              xfer += iprot->readI32(ecast41);
              _key39 = (DynamicPaoInfoTimestampKeys::type)ecast41;
              int64_t& _val40 = this->_timestampValues[_key39];
              xfer += iprot->readI64(_val40);
            }
            xfer += iprot->readMapEnd();
          }
          isset__timestampValues = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_MAP) {
          {
            this->_percentageValues.clear();
            uint32_t _size42;
            ::apache::thrift::protocol::TType _ktype43;
            ::apache::thrift::protocol::TType _vtype44;
            xfer += iprot->readMapBegin(_ktype43, _vtype44, _size42);
            uint32_t _i46;
            for (_i46 = 0; _i46 < _size42; ++_i46)
            {
              DynamicPaoInfoPercentageKeys::type _key47;
              int32_t ecast49;
              xfer += iprot->readI32(ecast49);
              _key47 = (DynamicPaoInfoPercentageKeys::type)ecast49;
              double& _val48 = this->_percentageValues[_key47];
              xfer += iprot->readDouble(_val48);
            }
            xfer += iprot->readMapEnd();
          }
          isset__percentageValues = true;
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

  if (!isset__deviceId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__durationValues)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__timestampValues)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__percentageValues)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t DynamicPaoInfoResponse::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("DynamicPaoInfoResponse");

  xfer += oprot->writeFieldBegin("_deviceId", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32(this->_deviceId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_durationValues", ::apache::thrift::protocol::T_MAP, 2);
  {
    xfer += oprot->writeMapBegin(::apache::thrift::protocol::T_I32, ::apache::thrift::protocol::T_I64, static_cast<uint32_t>(this->_durationValues.size()));
    std::map<DynamicPaoInfoDurationKeys::type, int64_t> ::const_iterator _iter50;
    for (_iter50 = this->_durationValues.begin(); _iter50 != this->_durationValues.end(); ++_iter50)
    {
      xfer += oprot->writeI32((int32_t)_iter50->first);
      xfer += oprot->writeI64(_iter50->second);
    }
    xfer += oprot->writeMapEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_timestampValues", ::apache::thrift::protocol::T_MAP, 3);
  {
    xfer += oprot->writeMapBegin(::apache::thrift::protocol::T_I32, ::apache::thrift::protocol::T_I64, static_cast<uint32_t>(this->_timestampValues.size()));
    std::map<DynamicPaoInfoTimestampKeys::type, int64_t> ::const_iterator _iter51;
    for (_iter51 = this->_timestampValues.begin(); _iter51 != this->_timestampValues.end(); ++_iter51)
    {
      xfer += oprot->writeI32((int32_t)_iter51->first);
      xfer += oprot->writeI64(_iter51->second);
    }
    xfer += oprot->writeMapEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_percentageValues", ::apache::thrift::protocol::T_MAP, 4);
  {
    xfer += oprot->writeMapBegin(::apache::thrift::protocol::T_I32, ::apache::thrift::protocol::T_DOUBLE, static_cast<uint32_t>(this->_percentageValues.size()));
    std::map<DynamicPaoInfoPercentageKeys::type, double> ::const_iterator _iter52;
    for (_iter52 = this->_percentageValues.begin(); _iter52 != this->_percentageValues.end(); ++_iter52)
    {
      xfer += oprot->writeI32((int32_t)_iter52->first);
      xfer += oprot->writeDouble(_iter52->second);
    }
    xfer += oprot->writeMapEnd();
  }
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(DynamicPaoInfoResponse &a, DynamicPaoInfoResponse &b) {
  using ::std::swap;
  swap(a._deviceId, b._deviceId);
  swap(a._durationValues, b._durationValues);
  swap(a._timestampValues, b._timestampValues);
  swap(a._percentageValues, b._percentageValues);
}

DynamicPaoInfoResponse::DynamicPaoInfoResponse(const DynamicPaoInfoResponse& other53) {
  _deviceId = other53._deviceId;
  _durationValues = other53._durationValues;
  _timestampValues = other53._timestampValues;
  _percentageValues = other53._percentageValues;
}
DynamicPaoInfoResponse& DynamicPaoInfoResponse::operator=(const DynamicPaoInfoResponse& other54) {
  _deviceId = other54._deviceId;
  _durationValues = other54._durationValues;
  _timestampValues = other54._timestampValues;
  _percentageValues = other54._percentageValues;
  return *this;
}
void DynamicPaoInfoResponse::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "DynamicPaoInfoResponse(";
  out << "_deviceId=" << to_string(_deviceId);
  out << ", " << "_durationValues=" << to_string(_durationValues);
  out << ", " << "_timestampValues=" << to_string(_timestampValues);
  out << ", " << "_percentageValues=" << to_string(_percentageValues);
  out << ")";
}

}}}}} // namespace
