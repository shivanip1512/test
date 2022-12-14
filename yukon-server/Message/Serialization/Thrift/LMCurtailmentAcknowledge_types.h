/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef LMCurtailmentAcknowledge_TYPES_H
#define LMCurtailmentAcknowledge_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>
#include "LMMessage_types.h"


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift {

class LMCurtailmentAcknowledge;


class LMCurtailmentAcknowledge : public virtual ::apache::thrift::TBase {
 public:

  LMCurtailmentAcknowledge(const LMCurtailmentAcknowledge&);
  LMCurtailmentAcknowledge& operator=(const LMCurtailmentAcknowledge&);
  LMCurtailmentAcknowledge() : _paoId(0), _curtailReferenceId(0), _acknowledgeStatus(), _ipAddressOfAckUser(), _userIdName(), _nameOfAckPerson(), _curtailmentNotes() {
  }

  virtual ~LMCurtailmentAcknowledge() noexcept;
   ::Cti::Messaging::Serialization::Thrift::LMMessage _baseMessage;
  int32_t _paoId;
  int32_t _curtailReferenceId;
  std::string _acknowledgeStatus;
  std::string _ipAddressOfAckUser;
  std::string _userIdName;
  std::string _nameOfAckPerson;
  std::string _curtailmentNotes;

  void __set__baseMessage(const  ::Cti::Messaging::Serialization::Thrift::LMMessage& val);

  void __set__paoId(const int32_t val);

  void __set__curtailReferenceId(const int32_t val);

  void __set__acknowledgeStatus(const std::string& val);

  void __set__ipAddressOfAckUser(const std::string& val);

  void __set__userIdName(const std::string& val);

  void __set__nameOfAckPerson(const std::string& val);

  void __set__curtailmentNotes(const std::string& val);

  bool operator == (const LMCurtailmentAcknowledge & rhs) const
  {
    if (!(_baseMessage == rhs._baseMessage))
      return false;
    if (!(_paoId == rhs._paoId))
      return false;
    if (!(_curtailReferenceId == rhs._curtailReferenceId))
      return false;
    if (!(_acknowledgeStatus == rhs._acknowledgeStatus))
      return false;
    if (!(_ipAddressOfAckUser == rhs._ipAddressOfAckUser))
      return false;
    if (!(_userIdName == rhs._userIdName))
      return false;
    if (!(_nameOfAckPerson == rhs._nameOfAckPerson))
      return false;
    if (!(_curtailmentNotes == rhs._curtailmentNotes))
      return false;
    return true;
  }
  bool operator != (const LMCurtailmentAcknowledge &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const LMCurtailmentAcknowledge & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(LMCurtailmentAcknowledge &a, LMCurtailmentAcknowledge &b);

std::ostream& operator<<(std::ostream& out, const LMCurtailmentAcknowledge& obj);

}}}} // namespace

#endif
