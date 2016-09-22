/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef LMEnergyExchangeAccept_TYPES_H
#define LMEnergyExchangeAccept_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include "LMMessage_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {


class LMEnergyExchangeAccept {
 public:

  static const char* ascii_fingerprint; // = "84592633E4F56D5C628D6E79667759F3";
  static const uint8_t binary_fingerprint[16]; // = {0x84,0x59,0x26,0x33,0xE4,0xF5,0x6D,0x5C,0x62,0x8D,0x6E,0x79,0x66,0x77,0x59,0xF3};

  LMEnergyExchangeAccept() : _paoId(0), _offerId(0), _revisionNumber(0), _acceptStatus(), _ipAddressOfAcceptUser(), _userIdName(), _nameOfAcceptPerson(), _energyExchangeNotes() {
  }

  virtual ~LMEnergyExchangeAccept() throw() {}

   ::Cti::Messaging::Serialization::Thrift::LMMessage _baseMessage;
  int32_t _paoId;
  int32_t _offerId;
  int32_t _revisionNumber;
  std::string _acceptStatus;
  std::string _ipAddressOfAcceptUser;
  std::string _userIdName;
  std::string _nameOfAcceptPerson;
  std::string _energyExchangeNotes;
  std::vector<double>  _amountsCommitted;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::LMMessage& val) {
    _baseMessage = val;
  }

  void __set__paoId(const int32_t val) {
    _paoId = val;
  }

  void __set__offerId(const int32_t val) {
    _offerId = val;
  }

  void __set__revisionNumber(const int32_t val) {
    _revisionNumber = val;
  }

  void __set__acceptStatus(const std::string& val) {
    _acceptStatus = val;
  }

  void __set__ipAddressOfAcceptUser(const std::string& val) {
    _ipAddressOfAcceptUser = val;
  }

  void __set__userIdName(const std::string& val) {
    _userIdName = val;
  }

  void __set__nameOfAcceptPerson(const std::string& val) {
    _nameOfAcceptPerson = val;
  }

  void __set__energyExchangeNotes(const std::string& val) {
    _energyExchangeNotes = val;
  }

  void __set__amountsCommitted(const std::vector<double> & val) {
    _amountsCommitted = val;
  }

  bool operator == (const LMEnergyExchangeAccept & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_paoId == rhs._paoId))
      return false;
    if (!(_offerId == rhs._offerId))
      return false;
    if (!(_revisionNumber == rhs._revisionNumber))
      return false;
    if (!(_acceptStatus == rhs._acceptStatus))
      return false;
    if (!(_ipAddressOfAcceptUser == rhs._ipAddressOfAcceptUser))
      return false;
    if (!(_userIdName == rhs._userIdName))
      return false;
    if (!(_nameOfAcceptPerson == rhs._nameOfAcceptPerson))
      return false;
    if (!(_energyExchangeNotes == rhs._energyExchangeNotes))
      return false;
    if (!(_amountsCommitted == rhs._amountsCommitted))
      return false;
    return true;
  }
  bool operator != (const LMEnergyExchangeAccept &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const LMEnergyExchangeAccept & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(LMEnergyExchangeAccept &a, LMEnergyExchangeAccept &b);

}}}} // namespace

#endif
