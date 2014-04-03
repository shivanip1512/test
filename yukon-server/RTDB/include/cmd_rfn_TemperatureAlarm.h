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
                    alarmRepeatCount,
                    alarmHighTempThreshold;
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

