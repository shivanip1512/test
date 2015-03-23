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

    const Operation _operationCode;

public:

    //  defined in http://portal.cooperpowereas.net/sites/Ops/MarylandEngineeringGroup/Shared%20Documents/PSGT-Firmware/Features/Centron/Design/EventAndAlarmMasterList.doc
    enum MeterID
    {
        //ElsterA3      = 0x01,
        LGFocusAL       = 0x02,
        LGFocusAX       = 0x03,
        CentronC2SX     = 0x04,
        //Relays        = 0x05
        CentronC1SX     = 0x06,
        //ItronSentinel = 0x07
        Unspecified     = 0xff
    };

    enum EventID
    {
        OverVoltage     = 2022,
        UnderVoltage    = 2023
    };

    ASID getApplicationServiceId() const override;

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response );
};


class IM_EX_DEVDB RfnSetOvUvAlarmProcessingStateCommand : public RfnOvUvConfigurationCommand
{
public:

    enum AlarmStates
    {
        DisableOvUv,
        EnableOvUv
    }
    const alarmState;

    RfnSetOvUvAlarmProcessingStateCommand( const AlarmStates alarmState );

    virtual void invokeResultHandler( ResultHandler &rh ) const;

protected:

    virtual Bytes getCommandData();
};


class IM_EX_DEVDB RfnSetOvUvNewAlarmReportIntervalCommand : public RfnOvUvConfigurationCommand
{
public:

    RfnSetOvUvNewAlarmReportIntervalCommand( const unsigned interval_minutes );

    virtual void invokeResultHandler( ResultHandler &rh ) const;

    const unsigned reportingInterval;

protected:

    virtual Bytes getCommandData();
};


class IM_EX_DEVDB RfnSetOvUvAlarmRepeatIntervalCommand : public RfnOvUvConfigurationCommand
{
public:

    RfnSetOvUvAlarmRepeatIntervalCommand( const unsigned interval_minutes );

    virtual void invokeResultHandler( ResultHandler &rh ) const;

    const unsigned repeatInterval;

protected:

    virtual Bytes getCommandData();
};


class IM_EX_DEVDB RfnSetOvUvAlarmRepeatCountCommand : public RfnOvUvConfigurationCommand
{
public:

    RfnSetOvUvAlarmRepeatCountCommand( const unsigned repeat_count );

    virtual void invokeResultHandler( ResultHandler &rh ) const;

    const unsigned repeatCount;

protected:

    virtual Bytes getCommandData();
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

struct IM_EX_DEVDB RfnSetOvUvSetOverVoltageThresholdCommand : public RfnSetOvUvSetThresholdCommand
{
    RfnSetOvUvSetOverVoltageThresholdCommand( const MeterID meter_id, const double threshold_volts );

    virtual void invokeResultHandler( ResultHandler &rh ) const;

    const double ovThresholdValue;
};

struct IM_EX_DEVDB RfnSetOvUvSetUnderVoltageThresholdCommand : public RfnSetOvUvSetThresholdCommand
{
    RfnSetOvUvSetUnderVoltageThresholdCommand( const MeterID meter_id, const double threshold_volts );

    virtual void invokeResultHandler( ResultHandler &rh ) const;

    const double uvThresholdValue;
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

    virtual void invokeResultHandler( ResultHandler &rh ) const;

    RfnGetOvUvAlarmConfigurationCommand( const MeterID meter_id, const EventID event_id );

    virtual RfnCommandResult decodeCommand( const CtiTime now,
                                            const RfnResponsePayload & response );

    AlarmConfiguration  getAlarmConfiguration() const;

protected:

    virtual Bytes getCommandData();

private:

    AlarmConfiguration  _alarmConfig;

    MeterID     _meterID;
    EventID     _eventID;
};






}
}
}

