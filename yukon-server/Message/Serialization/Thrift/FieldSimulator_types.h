/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef FieldSimulator_TYPES_H
#define FieldSimulator_TYPES_H

#include <iosfwd>

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/TBase.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <functional>
#include <memory>


namespace Cti { namespace Messaging { namespace Serialization { namespace Thrift { namespace FieldSimulator {

class FieldSimulatorSettings;

class FieldSimulatorStatusRequest;

class FieldSimulatorStatusResponse;

class FieldSimulatorConfigurationRequest;

class FieldSimulatorConfigurationResponse;


class FieldSimulatorSettings : public virtual ::apache::thrift::TBase {
 public:

  FieldSimulatorSettings(const FieldSimulatorSettings&);
  FieldSimulatorSettings& operator=(const FieldSimulatorSettings&);
  FieldSimulatorSettings() : _deviceGroup(), _deviceConfigFailureRate(0) {
  }

  virtual ~FieldSimulatorSettings() noexcept;
  std::string _deviceGroup;
  int32_t _deviceConfigFailureRate;

  void __set__deviceGroup(const std::string& val);

  void __set__deviceConfigFailureRate(const int32_t val);

  bool operator == (const FieldSimulatorSettings & rhs) const
  {
    if (!(_deviceGroup == rhs._deviceGroup))
      return false;
    if (!(_deviceConfigFailureRate == rhs._deviceConfigFailureRate))
      return false;
    return true;
  }
  bool operator != (const FieldSimulatorSettings &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const FieldSimulatorSettings & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(FieldSimulatorSettings &a, FieldSimulatorSettings &b);

std::ostream& operator<<(std::ostream& out, const FieldSimulatorSettings& obj);


class FieldSimulatorStatusRequest : public virtual ::apache::thrift::TBase {
 public:

  FieldSimulatorStatusRequest(const FieldSimulatorStatusRequest&);
  FieldSimulatorStatusRequest& operator=(const FieldSimulatorStatusRequest&);
  FieldSimulatorStatusRequest() {
  }

  virtual ~FieldSimulatorStatusRequest() noexcept;

  bool operator == (const FieldSimulatorStatusRequest & /* rhs */) const
  {
    return true;
  }
  bool operator != (const FieldSimulatorStatusRequest &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const FieldSimulatorStatusRequest & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(FieldSimulatorStatusRequest &a, FieldSimulatorStatusRequest &b);

std::ostream& operator<<(std::ostream& out, const FieldSimulatorStatusRequest& obj);


class FieldSimulatorStatusResponse : public virtual ::apache::thrift::TBase {
 public:

  FieldSimulatorStatusResponse(const FieldSimulatorStatusResponse&);
  FieldSimulatorStatusResponse& operator=(const FieldSimulatorStatusResponse&);
  FieldSimulatorStatusResponse() {
  }

  virtual ~FieldSimulatorStatusResponse() noexcept;
  FieldSimulatorSettings _settings;

  void __set__settings(const FieldSimulatorSettings& val);

  bool operator == (const FieldSimulatorStatusResponse & rhs) const
  {
    if (!(_settings == rhs._settings))
      return false;
    return true;
  }
  bool operator != (const FieldSimulatorStatusResponse &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const FieldSimulatorStatusResponse & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(FieldSimulatorStatusResponse &a, FieldSimulatorStatusResponse &b);

std::ostream& operator<<(std::ostream& out, const FieldSimulatorStatusResponse& obj);


class FieldSimulatorConfigurationRequest : public virtual ::apache::thrift::TBase {
 public:

  FieldSimulatorConfigurationRequest(const FieldSimulatorConfigurationRequest&);
  FieldSimulatorConfigurationRequest& operator=(const FieldSimulatorConfigurationRequest&);
  FieldSimulatorConfigurationRequest() {
  }

  virtual ~FieldSimulatorConfigurationRequest() noexcept;
  FieldSimulatorSettings _settings;

  void __set__settings(const FieldSimulatorSettings& val);

  bool operator == (const FieldSimulatorConfigurationRequest & rhs) const
  {
    if (!(_settings == rhs._settings))
      return false;
    return true;
  }
  bool operator != (const FieldSimulatorConfigurationRequest &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const FieldSimulatorConfigurationRequest & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(FieldSimulatorConfigurationRequest &a, FieldSimulatorConfigurationRequest &b);

std::ostream& operator<<(std::ostream& out, const FieldSimulatorConfigurationRequest& obj);


class FieldSimulatorConfigurationResponse : public virtual ::apache::thrift::TBase {
 public:

  FieldSimulatorConfigurationResponse(const FieldSimulatorConfigurationResponse&);
  FieldSimulatorConfigurationResponse& operator=(const FieldSimulatorConfigurationResponse&);
  FieldSimulatorConfigurationResponse() : _success(0) {
  }

  virtual ~FieldSimulatorConfigurationResponse() noexcept;
  bool _success;
  FieldSimulatorSettings _settings;

  void __set__success(const bool val);

  void __set__settings(const FieldSimulatorSettings& val);

  bool operator == (const FieldSimulatorConfigurationResponse & rhs) const
  {
    if (!(_success == rhs._success))
      return false;
    if (!(_settings == rhs._settings))
      return false;
    return true;
  }
  bool operator != (const FieldSimulatorConfigurationResponse &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const FieldSimulatorConfigurationResponse & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

  virtual void printTo(std::ostream& out) const;
};

void swap(FieldSimulatorConfigurationResponse &a, FieldSimulatorConfigurationResponse &b);

std::ostream& operator<<(std::ostream& out, const FieldSimulatorConfigurationResponse& obj);

}}}}} // namespace

#endif
