#pragma once

#include "cmd_rfn.h"
#include "ctidate.h"


namespace Cti        {
namespace Devices    {
namespace Commands   {


class IM_EX_DEVDB RfnOvUvConfigurationCommand : public RfnCommand
{
protected:

    enum Operation
    {
        Operation_SetOvUvAlarmProcessingState           = 0x24,
        Operation_SetSetThreshold                       = 0x25,
        Operation_SetOvUvNewAlarmReportingInterval      = 0x26,
        Operation_SetOvUvAlarmRepeatInterval            = 0x27,
        Operation_SetOvUvAlarmRepeatCount               = 0x28,
        Operation_OvUvConfigurationResponse             = 0x29,

        Operation_GetOvUvAlarmConfigurationInfo         = 0x34,
        Operation_GetOvUvAlarmConfigurationInfoResponse = 0x35
    };

    RfnOvUvConfigurationCommand( const Operation operationCode );

    virtual unsigned char getCommandCode() const;

    virtual unsigned char getOperation() const;

    virtual Bytes getCommandHeader();

    virtual unsigned char getApplicationServiceId() const;

    const Operation _operationCode;

public:

    enum MeterID
    {
        LGFocusAL       = 0x02,
        LGFocusAX       = 0x03,
        CentronC2SX     = 0x04,
        Unspecified     = 0xff
    };

    enum EventID
    {
        OverVoltage     = 2022,
        UnderVoltage    = 2023
    };

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response );

    virtual RfnCommandResult error( const CtiTime now,
                                    const YukonError_t error_code );
};


class IM_EX_DEVDB RfnSetOvUvAlarmProcessingStateCommand : public RfnOvUvConfigurationCommand
{
public:

    enum AlarmStates
    {
        DisableOvUv,
        EnableOvUv
    };

    RfnSetOvUvAlarmProcessingStateCommand( const AlarmStates alarmState );

protected:

    virtual Bytes getCommandData();

private:

    AlarmStates _state;
};


class IM_EX_DEVDB RfnSetOvUvNewAlarmReportIntervalCommand : public RfnOvUvConfigurationCommand
{
public:

    RfnSetOvUvNewAlarmReportIntervalCommand( const unsigned interval_minutes );

protected:

    virtual Bytes getCommandData();

private:

    unsigned _reportingInterval;
};


class IM_EX_DEVDB RfnSetOvUvAlarmRepeatIntervalCommand : public RfnOvUvConfigurationCommand
{
public:

    RfnSetOvUvAlarmRepeatIntervalCommand( const unsigned interval_minutes );

protected:

    virtual Bytes getCommandData();

private:

    unsigned _repeatInterval;
};


class IM_EX_DEVDB RfnSetOvUvAlarmRepeatCountCommand : public RfnOvUvConfigurationCommand
{
public:

    RfnSetOvUvAlarmRepeatCountCommand( const unsigned repeat_count );

protected:

    virtual Bytes getCommandData();

private:

    unsigned _repeatCount;
};


class IM_EX_DEVDB RfnSetOvUvSetThresholdCommand : public RfnOvUvConfigurationCommand
{
public:

    RfnSetOvUvSetThresholdCommand( const MeterID meter_id, const EventID event_id, const double threshold_volts );

protected:

    virtual Bytes getCommandData();

private:

    MeterID     _meterID;
    EventID     _eventID;
    unsigned    _thresholdValue;
};


class IM_EX_DEVDB RfnGetOvUvAlarmConfigurationCommand : public RfnOvUvConfigurationCommand
{
public:

    struct AlarmConfiguration
    {
        bool                    ovuvEnabled;
        unsigned                ovuvAlarmReportingInterval,
                                ovuvAlarmRepeatInterval,
                                ovuvAlarmRepeatCount;
        boost::optional<double> ovThreshold,
                                uvThreshold;
    };

    struct ResultHandler
    {
        virtual void handleResult( const RfnGetOvUvAlarmConfigurationCommand & cmd ) = 0;
    };

    RfnGetOvUvAlarmConfigurationCommand( ResultHandler & rh, const MeterID meter_id, const EventID event_id );

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response );

    AlarmConfiguration  getAlarmConfiguration() const;

protected:

    virtual Bytes getCommandData();

private:

    ResultHandler & _rh;

    AlarmConfiguration  _alarmConfig;

    MeterID     _meterID;
    EventID     _eventID;
};






}
}
}

