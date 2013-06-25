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

            VoltageXAttribute,
            VoltageYAttribute,
            TerminateAttribute,
            AutoBlockEnableAttribute,
            HeartbeatTimerConfigAttribute,

            CbcVoltageAttribute,
            HighVoltageAttribute,
            LowVoltageAttribute,
            DeltaVoltageAttribute,
            AnalogInput1Attribute,
            TemperatureAttribute,
            RSSIAttribute,
            IgnoredReasonAttribute,

            VoltageControlAttribute,
            UvThresholdAttribute,
            OvThresholdAttribute,
            OVUVTrackTimeAttribute,
            NeutralCurrentSensorAttribute,
            NeutralCurrentAlarmThresholdAttribute,
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
            OvCountAttribute,
            CloseOpCountAttribute,
            OpenOpCountAttribute
        };

        static const PointAttribute Unknown;
        static const PointAttribute TapDown;
        static const PointAttribute TapUp;
        static const PointAttribute Voltage;
        static const PointAttribute AutoRemoteControl;
        static const PointAttribute TapPosition;
        static const PointAttribute KeepAlive;

        static const PointAttribute VoltageX;
        static const PointAttribute VoltageY;
        static const PointAttribute Terminate;
        static const PointAttribute AutoBlockEnable;
        static const PointAttribute HeartbeatTimerConfig;

        static const PointAttribute CbcVoltage;
        static const PointAttribute HighVoltage;
        static const PointAttribute LowVoltage;
        static const PointAttribute DeltaVoltage;
        static const PointAttribute AnalogInput1;
        static const PointAttribute Temperature;
        static const PointAttribute RSSI;
        static const PointAttribute IgnoredReason;

        static const PointAttribute VoltageControl;
        static const PointAttribute UvThreshold;
        static const PointAttribute OvThreshold;
        static const PointAttribute OVUVTrackTime;
        static const PointAttribute NeutralCurrentSensor;
        static const PointAttribute NeutralCurrentAlarmThreshold;
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
        static const PointAttribute CloseOpCount;
        static const PointAttribute OpenOpCount;


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
        std::string _dbName;

        typedef std::map<std::string,PointAttribute*> AttributeMap;
        static AttributeMap nameToAttributeMap;
};



class IM_EX_CTIBASE Attribute
{
    Attribute( const std::string & name );

    std::string _name;
    unsigned    _value;

    typedef std::map<std::string, Attribute*>   NameToAttributeMap;

    static NameToAttributeMap   _lookup;
    static unsigned             _seed;

public:

    static const Attribute & Lookup( const std::string & name );

    bool operator==( const Attribute & rhs ) const;

    bool operator<( const Attribute & rhs ) const;


    static const Attribute  Unknown;
    static const Attribute  BlinkCount;
    static const Attribute  ClockError;
    static const Attribute  ConfigurationError;
    static const Attribute  ControlPoint;
    static const Attribute  ControlStatus;
    static const Attribute  CrystalOscillatorError;
    static const Attribute  Current;
    static const Attribute  CurrentLoss;
    static const Attribute  CurrentPhaseA;
    static const Attribute  CurrentPhaseB;
    static const Attribute  CurrentPhaseC;
    static const Attribute  CurrentWithoutVoltageFlag;
    static const Attribute  DeliveredKwLoadProfile;
    static const Attribute  DeliveredKwh;
    static const Attribute  DeliveredKwhPerInterval;
    static const Attribute  Demand;
    static const Attribute  DemandOverload;
    static const Attribute  DemandReadsAndReset;
    static const Attribute  DemandThresholdExceededWarning;
    static const Attribute  DisconnectStatus;
    static const Attribute  DisplayLockedByWarning;
    static const Attribute  EepromAccessError;
    static const Attribute  EncryptionKeyTableCrcError;
    static const Attribute  EndOfCalendarWarning;
    static const Attribute  EnergyAccumulatedWhileInStandbyMode;
    static const Attribute  EnergyGenerated;
    static const Attribute  FaultStatus;
    static const Attribute  ForwardCapacitiveKvarhPerInterval;
    static const Attribute  ForwardInductiveKvarh;
    static const Attribute  ForwardInductiveKvarhPerInterval;
    static const Attribute  GeneralAlarmFlag;
    static const Attribute  GeneratedKwhPerInterval;
    static const Attribute  IedDemandResetCount;
    static const Attribute  ImproperMeterEngineOperationWarning;
    static const Attribute  InternalCommunicationError;
    static const Attribute  InternalErrorFlag;
    static const Attribute  InvalidService;
    static const Attribute  Kvar;
    static const Attribute  Kvarh;
    static const Attribute  LineFrequencyWarning;
    static const Attribute  LmGroupStatus;
    static const Attribute  LoadProfile;
    static const Attribute  LoadSideVoltageDetectedFlag;
    static const Attribute  LoadSideVoltageIsMissing;
    static const Attribute  LossOfAllCurrent;
    static const Attribute  LossOfPhaseACurrent;
    static const Attribute  LossOfPhaseCCurrent;
    static const Attribute  LowBatteryWarning;
    static const Attribute  LowLossPotential;
    static const Attribute  MaximumVoltage;
    static const Attribute  MeasurementError;
    static const Attribute  MeterBoxCoverRemovalFlag;
    static const Attribute  MinimumVoltage;
    static const Attribute  NetKwLoadProfile;
    static const Attribute  NetKwh;
    static const Attribute  NetKwhPerInterval;
    static const Attribute  NeutralCurrent;
    static const Attribute  NonVolatileMemFailure;
    static const Attribute  OutOfVoltageFlag;
    static const Attribute  OutageLog;
    static const Attribute  OutageStatus;
    static const Attribute  OverVoltage;
    static const Attribute  OverVoltageMeasured;
    static const Attribute  OverVoltageThreshold;
    static const Attribute  PasswordTableCrcError;
    static const Attribute  PeakDemand;
    static const Attribute  PeakKvar;
    static const Attribute  Phase;
    static const Attribute  PotentialIndicatorWarning;
    static const Attribute  PowerFactor;
    static const Attribute  PowerFactorPhaseA;
    static const Attribute  PowerFactorPhaseB;
    static const Attribute  PowerFactorPhaseC;
    static const Attribute  PowerFailDataSaveError;
    static const Attribute  PowerFailFlag;
    static const Attribute  PqmTestFailureWarning;
    static const Attribute  ProfileChannel2;
    static const Attribute  ProfileChannel3;
    static const Attribute  RamError;
    static const Attribute  ReceivedKwLoadProfile;
    static const Attribute  ReceivedKwh;
    static const Attribute  ReceivedKwhPerInterval;
    static const Attribute  RecordingInterval;
    static const Attribute  Relay1LoadSize;
    static const Attribute  Relay1RemainingControl;
    static const Attribute  Relay1RunTimeDataLog;
    static const Attribute  Relay1ShedTimeDataLog;
    static const Attribute  Relay2LoadSize;
    static const Attribute  Relay2RemainingControl;
    static const Attribute  Relay2RunTimeDataLog;
    static const Attribute  Relay2ShedTimeDataLog;
    static const Attribute  Relay3LoadSize;
    static const Attribute  Relay3RemainingControl;
    static const Attribute  Relay3RunTimeDataLog;
    static const Attribute  Relay3ShedTimeDataLog;
    static const Attribute  Relay4RemainingControl;
    static const Attribute  Relay4RunTimeDataLog;
    static const Attribute  Relay4ShedTimeDataLog;
    static const Attribute  ReportingInterval;
    static const Attribute  ReverseCapacitiveKvarhPerInterval;
    static const Attribute  ReverseInductiveKvarh;
    static const Attribute  ReverseInductiveKvarhPerInterval;
    static const Attribute  ReversePowerFlag;
    static const Attribute  ReversedAggregate;
    static const Attribute  ReversedPhaseA;
    static const Attribute  ReversedPhaseC;
    static const Attribute  RfDemandResetStatus;
    static const Attribute  RfnBlinkCount;
    static const Attribute  RfnBlinkRestoreCount;
    static const Attribute  RfnOutageCount;
    static const Attribute  RfnOutageRestoreCount;
    static const Attribute  RomError;
    static const Attribute  SecurityConfigurationError;
    static const Attribute  SelfCheckError;
    static const Attribute  ServiceCurrentTestFailureWarning;
    static const Attribute  ServiceDisconnectSwitchError;
    static const Attribute  ServiceDisconnectSwitchOpen;
    static const Attribute  ServiceDisconnectSwitchSensorError;
    static const Attribute  ServiceStatus;
    static const Attribute  StuckSwitch;
    static const Attribute  SumKvaLoadProfile;
    static const Attribute  SumKvah;
    static const Attribute  SumKvahPerInterval;
    static const Attribute  SumKvarLoadProfile;
    static const Attribute  SumKvarh;
    static const Attribute  SumKvarhPerInterval;
    static const Attribute  SumKwLoadProfile;
    static const Attribute  SumKwh;
    static const Attribute  SumKwhPerInterval;
    static const Attribute  TableCrcError;
    static const Attribute  TamperFlag;
    static const Attribute  TemporaryOutOfService;
    static const Attribute  TimeAdjustment;
    static const Attribute  TotalLufCount;
    static const Attribute  TotalLuvCount;
    static const Attribute  TouRateAEnergyGenerated;
    static const Attribute  TouRateAPeakDemand;
    static const Attribute  TouRateAUsage;
    static const Attribute  TouRateBEnergyGenerated;
    static const Attribute  TouRateBPeakDemand;
    static const Attribute  TouRateBUsage;
    static const Attribute  TouRateCEnergyGenerated;
    static const Attribute  TouRateCPeakDemand;
    static const Attribute  TouRateCUsage;
    static const Attribute  TouRateDEnergyGenerated;
    static const Attribute  TouRateDPeakDemand;
    static const Attribute  TouRateDUsage;
    static const Attribute  Unconfigured;
    static const Attribute  UnderVoltage;
    static const Attribute  UnderVoltageMeasured;
    static const Attribute  UnderVoltageThreshold;
    static const Attribute  Unprogrammed;
    static const Attribute  Usage;
    static const Attribute  UsagePerInterval;
    static const Attribute  UsageWater;
    static const Attribute  UserProgrammableTemperatureThresholdExceeded;
    static const Attribute  Voltage;
    static const Attribute  VoltageAlerts;
    static const Attribute  VoltageLoss;
    static const Attribute  VoltageOutOfLimitsFlag;
    static const Attribute  VoltagePhaseA;
    static const Attribute  VoltagePhaseAOut;
    static const Attribute  VoltagePhaseB;
    static const Attribute  VoltagePhaseBOut;
    static const Attribute  VoltagePhaseC;
    static const Attribute  VoltagePhaseCOut;
    static const Attribute  VoltagePhaseError;
    static const Attribute  VoltageProfile;
    static const Attribute  WaterUsagePerInterval;
    static const Attribute  WattHourPulseFailure;
    static const Attribute  ZeroUsageFlag;
    static const Attribute  ZigbeeLinkStatus;


};


