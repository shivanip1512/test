#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"
#include "dlldefs.h"

#include <string>
#include <map>

class IM_EX_CTIBASE PointAttribute
{
    public:
        enum Attribute
        {
            UnknownAttribute,
            TapDownAttribute,
            TapUpAttribute,
            VoltageAttribute,
            AutoRemoteControlAttribute,
            TapPositionAttribute,
            KeepAliveAttribute,

            CbcVoltageAttribute,
            HighVoltageAttribute,
            LowVoltageAttribute,
            DeltaVoltageAttribute,
            AnalogInput1Attribute,
            TemperatureAttribute,
            RSSIAttribute,
            IgnoredReasonAttribute,

            VoltageControlAttribute,
            UvSetPointAttribute,
            OvSetPointAttribute,
            OVUVTrackTimeAttribute,
            NeutralCurrentSensorAttribute,
            NeutralCurrentAlarmSetPointAttribute,
            TimeTempSeasonOneAttribute,
            TimeTempSeasonTwoAttribute,
            VarControlAttribute,
            UDPIpAddressAttribute,
            UDPPortNumberAttribute,

            CapacitorBankStateAttribute,
            ReCloseBlockedAttribute,
            ControlModeAttribute,
            AutoVoltControlAttribute,
            LastControlLocalAttribute,
            LastControlRemoteAttribute,
            LastControlOvUvAttribute,
            LastControlNeutralFaultAttribute,
            LastControlScheduledAttribute,
            LastControlDigitalAttribute,
            LastControlAnalogAttribute,
            LastControlTemperatureAttribute,
            OvConditionAttribute,
            UvConditionAttribute,
            OpFailedNeutralCurrentAttribute,
            NeutralCurrentFaultAttribute,
            BadRelayAttribute,
            DailyMaxOpsAttribute,
            VoltageDeltaAbnormalAttribute,
            TempAlarmAttribute,
            DSTActiveAttribute,
            NeutralLockoutAttribute,
            IgnoredIndicatorAttribute,

            TotalOpCountAttribute,
            UvCountAttribute,
            OvCountAttribute
        };

        static const PointAttribute Unknown;
        static const PointAttribute TapDown;
        static const PointAttribute TapUp;
        static const PointAttribute Voltage;
        static const PointAttribute AutoRemoteControl;
        static const PointAttribute TapPosition;
        static const PointAttribute KeepAlive;

        static const PointAttribute CbcVoltage;
        static const PointAttribute HighVoltage;
        static const PointAttribute LowVoltage;
        static const PointAttribute DeltaVoltage;
        static const PointAttribute AnalogInput1;
        static const PointAttribute Temperature;
        static const PointAttribute RSSI;
        static const PointAttribute IgnoredReason;

        static const PointAttribute VoltageControl;
        static const PointAttribute UvSetPoint;
        static const PointAttribute OvSetPoint;
        static const PointAttribute OVUVTrackTime;
        static const PointAttribute NeutralCurrentSensor;
        static const PointAttribute NeutralCurrentAlarmSetPoint;
        static const PointAttribute TimeTempSeasonOne;
        static const PointAttribute TimeTempSeasonTwo;
        static const PointAttribute VarControl;
        static const PointAttribute UDPIpAddress;
        static const PointAttribute UDPPortNumber;

        static const PointAttribute CapacitorBankState;
        static const PointAttribute ReCloseBlocked;
        static const PointAttribute ControlMode;
        static const PointAttribute AutoVoltControl;
        static const PointAttribute LastControlLocal;
        static const PointAttribute LastControlRemote;
        static const PointAttribute LastControlOvUv;
        static const PointAttribute LastControlNeutralFault;
        static const PointAttribute LastControlScheduled;
        static const PointAttribute LastControlDigital;
        static const PointAttribute LastControlAnalog;
        static const PointAttribute LastControlTemperature;
        static const PointAttribute OvCondition;
        static const PointAttribute UvCondition;
        static const PointAttribute OpFailedNeutralCurrent;
        static const PointAttribute NeutralCurrentFault;
        static const PointAttribute BadRelay;
        static const PointAttribute DailyMaxOps;
        static const PointAttribute VoltageDeltaAbnormal;
        static const PointAttribute TempAlarm;
        static const PointAttribute DSTActive;
        static const PointAttribute NeutralLockout;
        static const PointAttribute IgnoredIndicator;

        static const PointAttribute TotalOpCount;
        static const PointAttribute UvCount;
        static const PointAttribute OvCount;


        std::string name() const;
        Attribute value() const;

        static const PointAttribute& valueOf(const std::string& name);

        const bool operator == (const PointAttribute& rhs) const;
        const bool operator <  (const PointAttribute& rhs) const;

    private:

        /**
         * This should never be called in code. Attributes will be built
         * up statically
         */
        PointAttribute(Attribute value, const std::string& name);

        Attribute _value;
        std::string _name;

        typedef std::map<std::string,PointAttribute*> AttributeMap;
        static AttributeMap nameToAttributeMap;
};
