/**
 * Autogenerated by Thrift Compiler (0.16.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "CCObjectMove_types.h"

#include <algorithm>
#include <ostream>

#include <thrift/TToString.h>

namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


CCObjectMove::~CCObjectMove() noexcept {
}


void CCObjectMove::__set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::CCMessage& val) {
  this->_baseMessage = val;
}

void CCObjectMove::__set__permanentFlag(const bool val) {
  this->_permanentFlag = val;
}

void CCObjectMove::__set__oldParentId(const int32_t val) {
  this->_oldParentId = val;
}

void CCObjectMove::__set__objectId(const int32_t val) {
  this->_objectId = val;
}

void CCObjectMove::__set__newParentId(const int32_t val) {
  this->_newParentId = val;
}

void CCObjectMove::__set__switchingOrder(const double val) {
  this->_switchingOrder = val;
}

void CCObjectMove::__set__closeOrder(const double val) {
  this->_closeOrder = val;
}

void CCObjectMove::__set__tripOrder(const double val) {
  this->_tripOrder = val;
}
std::ostream& operator<<(std::ostream& out, const CCObjectMove& obj)
{
  obj.printTo(out);
  return out;
}


uint32_t CCObjectMove::read(::apache::thrift::protocol::TProtocol* iprot) {

  ::apache::thrift::protocol::TInputRecursionTracker tracker(*iprot);
  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;

  bool isset__baseMessage = false;
  bool isset__permanentFlag = false;
  bool isset__oldParentId = false;
  bool isset__objectId = false;
  bool isset__newParentId = false;
  bool isset__switchingOrder = false;
  bool isset__closeOrder = false;
  bool isset__tripOrder = false;

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
          xfer += iprot->readBool(this->_permanentFlag);
          isset__permanentFlag = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_oldParentId);
          isset__oldParentId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_objectId);
          isset__objectId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->_newParentId);
          isset__newParentId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_DOUBLE) {
          xfer += iprot->readDouble(this->_switchingOrder);
          isset__switchingOrder = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_DOUBLE) {
          xfer += iprot->readDouble(this->_closeOrder);
          isset__closeOrder = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_DOUBLE) {
          xfer += iprot->readDouble(this->_tripOrder);
          isset__tripOrder = true;
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
  if (!isset__permanentFlag)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__oldParentId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__objectId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__newParentId)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__switchingOrder)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__closeOrder)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  if (!isset__tripOrder)
    throw TProtocolException(TProtocolException::INVALID_DATA);
  return xfer;
}

uint32_t CCObjectMove::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  ::apache::thrift::protocol::TOutputRecursionTracker tracker(*oprot);
  xfer += oprot->writeStructBegin("CCObjectMove");

  xfer += oprot->writeFieldBegin("_baseMessage", ::apache::thrift::protocol::T_STRUCT, 1);
  xfer += this->_baseMessage.write(oprot);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_permanentFlag", ::apache::thrift::protocol::T_BOOL, 2);
  xfer += oprot->writeBool(this->_permanentFlag);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_oldParentId", ::apache::thrift::protocol::T_I32, 3);
  xfer += oprot->writeI32(this->_oldParentId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_objectId", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->_objectId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_newParentId", ::apache::thrift::protocol::T_I32, 5);
  xfer += oprot->writeI32(this->_newParentId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_switchingOrder", ::apache::thrift::protocol::T_DOUBLE, 6);
  xfer += oprot->writeDouble(this->_switchingOrder);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_closeOrder", ::apache::thrift::protocol::T_DOUBLE, 7);
  xfer += oprot->writeDouble(this->_closeOrder);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("_tripOrder", ::apache::thrift::protocol::T_DOUBLE, 8);
  xfer += oprot->writeDouble(this->_tripOrder);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(CCObjectMove &a, CCObjectMove &b) {
  using ::std::swap;
  swap(a._baseMessage, b._baseMessage);
  swap(a._permanentFlag, b._permanentFlag);
  swap(a._oldParentId, b._oldParentId);
  swap(a._objectId, b._objectId);
  swap(a._newParentId, b._newParentId);
  swap(a._switchingOrder, b._switchingOrder);
  swap(a._closeOrder, b._closeOrder);
  swap(a._tripOrder, b._tripOrder);
}

CCObjectMove::CCObjectMove(const CCObjectMove& other0) {
  _baseMessage = other0._baseMessage;
  _permanentFlag = other0._permanentFlag;
  _oldParentId = other0._oldParentId;
  _objectId = other0._objectId;
  _newParentId = other0._newParentId;
  _switchingOrder = other0._switchingOrder;
  _closeOrder = other0._closeOrder;
  _tripOrder = other0._tripOrder;
}
CCObjectMove& CCObjectMove::operator=(const CCObjectMove& other1) {
  _baseMessage = other1._baseMessage;
  _permanentFlag = other1._permanentFlag;
  _oldParentId = other1._oldParentId;
  _objectId = other1._objectId;
  _newParentId = other1._newParentId;
  _switchingOrder = other1._switchingOrder;
  _closeOrder = other1._closeOrder;
  _tripOrder = other1._tripOrder;
  return *this;
}
void CCObjectMove::printTo(std::ostream& out) const {
  using ::apache::thrift::to_string;
  out << "CCObjectMove(";
  out << "_baseMessage=" << to_string(_baseMessage);
  out << ", " << "_permanentFlag=" << to_string(_permanentFlag);
  out << ", " << "_oldParentId=" << to_string(_oldParentId);
  out << ", " << "_objectId=" << to_string(_objectId);
  out << ", " << "_newParentId=" << to_string(_newParentId);
  out << ", " << "_switchingOrder=" << to_string(_switchingOrder);
  out << ", " << "_closeOrder=" << to_string(_closeOrder);
  out << ", " << "_tripOrder=" << to_string(_tripOrder);
  out << ")";
}

}}}} // namespace
