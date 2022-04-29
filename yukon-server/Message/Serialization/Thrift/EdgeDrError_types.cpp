/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "EdgeDrError_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

int _kEdgeDrErrorTypeValues[] = {
  EdgeDrErrorType::TIMEOUT
};
const char* _kEdgeDrErrorTypeNames[] = {
  "TIMEOUT"
};
const std::map<int, const char*> _EdgeDrErrorType_VALUES_TO_NAMES(::apache::thrift::TEnumIterator(1, _kEdgeDrErrorTypeValues, _kEdgeDrErrorTypeNames), ::apache::thrift::TEnumIterator(-1, NULL, NULL));

std::ostream& operator<<(std::ostream& out, const EdgeDrErrorType::type& val) {
  std::map<int, const char*>::const_iterator it = _EdgeDrErrorType_VALUES_TO_NAMES.find(val);
  if (it != _EdgeDrErrorType_VALUES_TO_NAMES.end()) {
    out << it->second;
  } else {
    out << static_cast<int>(val);
  }
  return out;
}

std::string to_string(const EdgeDrErrorType::type& val) {
  std::map<int, const char*>::const_iterator it = _EdgeDrErrorType_VALUES_TO_NAMES.find(val);
  if (it != _EdgeDrErrorType_VALUES_TO_NAMES.end()) {
    return std::string(it->second);
  } else {
    return std::to_string(static_cast<int>(val));
  }
}


EdgeDrError::~EdgeDrError() noexcept {
}


void EdgeDrError::__set_errorType(const EdgeDrErrorType::type val) {
  this->errorType = val;
}

void EdgeDrError::__set_errorMessage(const std::string& val) {
  this->errorMessage = val;
}
std::ostream& operator<<(std::ostream& out, const EdgeDrError& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t EdgeDrError::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset_errorType = false;
  bool isset_errorMessage = false;

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
          int32_t ecast0;
          xfer += iprot->readI32(ecast0);
          this->errorType = (EdgeDrErrorType::type)ecast0;
          isset_errorType = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->errorMessage);
          isset_errorMessage = true;
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

  if (!isset_errorType)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset_errorMessage)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t EdgeDrError::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("EdgeDrError");

  xfer += oprot->writeFieldBegin("errorType", ::apache::thrift::protocol::T_I32, 1);
  xfer += oprot->writeI32((int32_t)this->errorType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("errorMessage", ::apache::thrift::protocol::T_STRING, 2);
  xfer += oprot->writeString(this->errorMessage);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(EdgeDrError &a, EdgeDrError &b) {
  using ::std::swap;
  swap(a.errorType, b.errorType);
  swap(a.errorMessage, b.errorMessage);
}

EdgeDrError::EdgeDrError(const EdgeDrError& other1) {
  errorType = other1.errorType;
  errorMessage = other1.errorMessage;
}
EdgeDrError& EdgeDrError::operator=(const EdgeDrError& other2) {
  errorType = other2.errorType;
  errorMessage = other2.errorMessage;
  return *this;
}
void EdgeDrError::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "EdgeDrError(";
  out << "errorType=" << to_string(errorType);
  out << ", " << "errorMessage=" << to_string(errorMessage);
  out << ")";
}

}}}} // namespace
