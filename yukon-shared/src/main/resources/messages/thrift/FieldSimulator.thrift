namespace cpp Cti.Messaging.Serialization.Thrift.FieldSimulator
namespace java com.cannontech.messaging.serialization.thrift.generated.fieldSimulator

struct FieldSimulatorSettings {
    1: required     string        _deviceGroup;
    2: required     i32           _deviceConfigFailureRate;
}

struct FieldSimulatorStatusRequest {
}

struct FieldSimulatorStatusResponse {
    1: required     FieldSimulatorSettings    _settings;
}

struct FieldSimulatorConfigurationRequest {
    1: required     FieldSimulatorSettings    _settings;
}

struct FieldSimulatorConfigurationResponse {
    1: required     bool                      _success;
    2: required     FieldSimulatorSettings    _settings;
}

