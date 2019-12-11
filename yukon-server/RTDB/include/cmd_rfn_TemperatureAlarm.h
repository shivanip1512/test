#pragma once

#include "cmd_rfn_Individual.h"

namespace Cti        {
namespace Devices    {
namespace Commands   {

class IM_EX_DEVDB RfnTemperatureAlarmCommand : public RfnTwoWayCommand,
       InvokerFor<RfnTemperatureAlarmCommand>
{
public:

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response ) = 0;

    struct AlarmConfiguration
    {
        bool        alarmEnabled;
        unsigned    alarmRepeatInterval,
                    alarmRepeatCount;
        int         alarmHighTempThreshold;
    };

    virtual boost::optional<AlarmConfiguration> getAlarmConfiguration() const = 0;

    bool isSupported() const;

protected:

    enum CommandCode
    {
        CommandCode_Request         = 0x88,
        CommandCode_Response        = 0x89
    };

    enum Operation
    {
        Operation_SetConfiguration  = 0x00,
        Operation_GetConfiguration  = 0x01
    };

    enum TlvType
    {
        TlvType_TemperatureAlarmConfiguration  = 0x01
    };

    enum AlarmState
    {
        AlarmState_AlarmDisabled    = 0x00,
        AlarmState_AlarmEnabled     = 0x01
    };

    RfnTemperatureAlarmCommand( const Operation operation );

    ASID getApplicationServiceId() const override;

    unsigned char getCommandCode() const override;

    unsigned char getOperation() const override;

    Bytes getCommandData() override;

    const Operation _operation;

    RfnCommandResult decodeResponseHeader( const CtiTime now,
                                           const RfnResponsePayload & response );

    struct TemperatureConfig
    {
        AlarmConfiguration value;
        std::string description;
    };

    static TemperatureConfig decodeAlarmConfigTlv(const TypeLengthValue &tlv);

    enum class CommandStatus
    {
        Invalid,
        Success,
        Failure,
        Unsupported
    };

    CommandStatus commandStatus() const;

private:

    CommandStatus _commandStatus = CommandStatus::Invalid;
};


////


class IM_EX_DEVDB RfnSetTemperatureAlarmConfigurationCommand : public RfnTemperatureAlarmCommand
{
public:

    RfnSetTemperatureAlarmConfigurationCommand( const AlarmConfiguration & configuration );

    Bytes getCommandData() override;

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response ) override;

    std::string getCommandName() override;

    boost::optional<AlarmConfiguration> getAlarmConfiguration() const override;

protected:

    AlarmConfiguration _configuration;
};


////


class IM_EX_DEVDB RfnGetTemperatureAlarmConfigurationCommand : public RfnTemperatureAlarmCommand
{
public:

    RfnGetTemperatureAlarmConfigurationCommand();

    RfnCommandResult decodeCommand( const CtiTime now,
                                    const RfnResponsePayload & response ) override;

    std::string getCommandName() override;

    boost::optional<AlarmConfiguration> getAlarmConfiguration() const override;

protected:

    boost::optional<AlarmConfiguration> _configuration;
};


}
}
}

