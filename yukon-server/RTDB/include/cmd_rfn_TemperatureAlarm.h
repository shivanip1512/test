#pragma once

#include "cmd_rfn.h"



namespace Cti        {
namespace Devices    {
namespace Commands   {


class IM_EX_DEVDB RfnTemperatureAlarmCommand : public RfnCommand
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

    enum AlarmStatus
    {
        AlarmStatus_Unsupported     = 0x02
    };

    RfnTemperatureAlarmCommand( const Operation operation );

    RfnTemperatureAlarmCommand( const Operation operation, const AlarmConfiguration & configuration );

    virtual ASID getApplicationServiceId() const;

    virtual unsigned char getCommandCode() const;

    virtual unsigned char getOperation() const;

    virtual Bytes getCommandData();

    const Operation _operation;

    RfnCommandResult decodeResponseHeader( const CtiTime now,
                                           const RfnResponsePayload & response );

    bool _isSupported;

    AlarmConfiguration  _configuration;
};


////


class IM_EX_DEVDB RfnSetTemperatureAlarmConfigurationCommand : public RfnTemperatureAlarmCommand
{
public:

    RfnSetTemperatureAlarmConfigurationCommand( const AlarmConfiguration & configuration );

    virtual Bytes getCommandData();

    virtual void invokeResultHandler( ResultHandler & rh ) const;

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response );
protected:

    enum ConfigurationLimits
    {
        Limit_HighTempThresholdMinimum  = -40,
        Limit_HighTempThresholdMaximum  = 185,
        Limit_RepeatIntervalMinimum     = 0,
        Limit_RepeatIntervalMaximum     = 255,
        Limit_RepeatCountMinimum        = 0,
        Limit_RepeatCountMaximum        = 255
    };
};


////


class IM_EX_DEVDB RfnGetTemperatureAlarmConfigurationCommand : public RfnTemperatureAlarmCommand
{
public:

    RfnGetTemperatureAlarmConfigurationCommand();

    AlarmConfiguration getAlarmConfiguration() const;

    virtual void invokeResultHandler( ResultHandler & rh ) const;

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response );
};


}
}
}

