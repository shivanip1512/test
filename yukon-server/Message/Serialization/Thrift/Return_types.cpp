/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "Return_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


Return::~Return() noexcept {
}


void Return::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::Multi& val) {
  this->_baseMessage = val;
}

void Return::__set__deviceId(const int32_t val) {
  this->_deviceId = val;
}

void Return::__set__commandString(const std::string& val) {
  this->_commandString = val;
}

void Return::__set__resultString(const std::string& val) {
  this->_resultString = val;
}

void Return::__set__status(const int32_t val) {
  this->_status = val;
}

void Return::__set__routeId(const int32_t val) {
  this->_routeId = val;
}

void Return::__set__macroOffset(const int32_t val) {
  this->_macroOffset = val;
}

void Return::__set__attemptNum(const int32_t val) {
  this->_attemptNum = val;
}

void Return::__set__groupMessageId(const int32_t val) {
  this->_groupMessageId = val;
}

void Return::__set__userMessageId(const int32_t val) {
  this->_userMessageId = val;
}

void Return::__set__expectMore(const bool val) {
  this->_expectMore = val;
}
std::ostream& operator<<(std::ostream& out, const Return& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t Return::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__deviceId = false;
  bool isset__commandString = false;
  bool isset__resultString = false;
  bool isset__status = false;
  bool isset__routeId = false;
  bool isset__macroOffset = false;
  bool isset__attemptNum = false;
  bool isset__groupMessageId = false;
  bool isset__userMessageId = false;
  bool isset__expectMore = false;

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
          xfer += iprot->readI32(this->_deviceId);
          isset__deviceId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_commandString);
          isset__commandString = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->_resultString);
          isset__resultString = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_status);
          isset__status = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_routeId);
          isset__routeId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_macroOffset);
          isset__macroOffset = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_attemptNum);
          isset__attemptNum = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 9:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_groupMessageId);
          isset__groupMessageId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 10:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_userMessageId);
          isset__userMessageId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 11:
        if (ftype == ::apache::thrift::protocol::T_BOOL) {
          xfer += iprot->readBool(this->_expectMore);
          isset__expectMore = true;
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
  if (!isset__deviceId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__commandString)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__resultString)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__status)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__routeId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__macroOffset)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__attemptNum)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__groupMessageId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__userMessageId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__expectMore)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t Return::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("Return");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_deviceId", ::apache::thrift::protocol::T_I32, 2);
  xfer += oprot->writeI32(this->_deviceId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_commandString", ::apache::thrift::protocol::T_STRING, 3);
  xfer += oprot->writeString(this->_commandString);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_resultString", ::apache::thrift::protocol::T_STRING, 4);
  xfer += oprot->writeString(this->_resultString);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_status", ::apache::thrift::protocol::T_I32, 5);
  xfer += oprot->writeI32(this->_status);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_routeId", ::apache::thrift::protocol::T_I32, 6);
  xfer += oprot->writeI32(this->_routeId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_macroOffset", ::apache::thrift::protocol::T_I32, 7);
  xfer += oprot->writeI32(this->_macroOffset);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_attemptNum", ::apache::thrift::protocol::T_I32, 8);
  xfer += oprot->writeI32(this->_attemptNum);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_groupMessageId", ::apache::thrift::protocol::T_I32, 9);
  xfer += oprot->writeI32(this->_groupMessageId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_userMessageId", ::apache::thrift::protocol::T_I32, 10);
  xfer += oprot->writeI32(this->_userMessageId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_expectMore", ::apache::thrift::protocol::T_BOOL, 11);
  xfer += oprot->writeBool(this->_expectMore);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(Return &a, Return &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._deviceId, b._deviceId);
  swap(a._commandString, b._commandString);
  swap(a._resultString, b._resultString);
  swap(a._status, b._status);
  swap(a._routeId, b._routeId);
  swap(a._macroOffset, b._macroOffset);
  swap(a._attemptNum, b._attemptNum);
  swap(a._groupMessageId, b._groupMessageId);
  swap(a._userMessageId, b._userMessageId);
  swap(a._expectMore, b._expectMore);
}

Return::Return(const Return& other0) {
  _baseMessage = other0._baseMessage;
  _deviceId = other0._deviceId;
  _commandString = other0._commandString;
  _resultString = other0._resultString;
  _status = other0._status;
  _routeId = other0._routeId;
  _macroOffset = other0._macroOffset;
  _attemptNum = other0._attemptNum;
  _groupMessageId = other0._groupMessageId;
  _userMessageId = other0._userMessageId;
  _expectMore = other0._expectMore;
}
Return& Return::operator=(const Return& other1) {
  _baseMessage = other1._baseMessage;
  _deviceId = other1._deviceId;
  _commandString = other1._commandString;
  _resultString = other1._resultString;
  _status = other1._status;
  _routeId = other1._routeId;
  _macroOffset = other1._macroOffset;
  _attemptNum = other1._attemptNum;
  _groupMessageId = other1._groupMessageId;
  _userMessageId = other1._userMessageId;
  _expectMore = other1._expectMore;
  return *this;
}
void Return::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "Return(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_deviceId=" << to_string(_deviceId);
  out << ", " << "_commandString=" << to_string(_commandString);
  out << ", " << "_resultString=" << to_string(_resultString);
  out << ", " << "_status=" << to_string(_status);
  out << ", " << "_routeId=" << to_string(_routeId);
  out << ", " << "_macroOffset=" << to_string(_macroOffset);
  out << ", " << "_attemptNum=" << to_string(_attemptNum);
  out << ", " << "_groupMessageId=" << to_string(_groupMessageId);
  out << ", " << "_userMessageId=" << to_string(_userMessageId);
  out << ", " << "_expectMore=" << to_string(_expectMore);
  out << ")";
}

}}}} // namespace
