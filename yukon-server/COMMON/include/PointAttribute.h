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
            OpenOpCountAttribute,

            LastControlReasonAttribute,

            ForwardSetPointAttribute,
            ForwardBandwidthAttribute
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

        static const PointAttribute LastControlReason;

        static const PointAttribute ForwardSetPoint;
        static const PointAttribute ForwardBandwidth;

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

    std::string getName() const { return _name; }

    bool operator==( const Attribute & rhs ) const;

    bool operator<( const Attribute & rhs ) const;

    //  this ordering should match BuiltInAttribute.java
    static const Attribute  Unknown;
    static const Attribute  BlinkCount;
    static const Attribute  CommunicationStatus;
    static const Attribute  ControlPoint;
    static const Attribute  ControlStatus;
    static const Attribute  CurrentNeutral;
    static const Attribute  Current;
    static const Attribute  CurrentPhaseA;
    static const Attribute  CurrentPhaseB;
    static const Attribute  CurrentPhaseC;
    static const Attribute  CurrentAnglePhaseA;
    static const Attribute  CurrentAnglePhaseB;
    static const Attribute  CurrentAnglePhaseC;
    static const Attribute  CurrentWithoutVoltage;
    static const Attribute  Demand;
    static const Attribute  ReceivedDemand;
    static const Attribute  DemandPeakkVACoin;
    static const Attribute  DisconnectStatus;
    static const Attribute  FaultStatus;
    static const Attribute  ForwardInductivekVArh;
    static const Attribute  GeneralAlarmFlag;
    static const Attribute  IEDDemandResetCount;
    static const Attribute  kVAh;
    static const Attribute  kVAhRateA;
    static const Attribute  kVAhRateB;
    static const Attribute  kVAhRateC;
    static const Attribute  kVAhRateD;
    static const Attribute  NetkVAhRateA;
    static const Attribute  NetkVAhRateB;
    static const Attribute  NetkVAhRateC;
    static const Attribute  NetkVAhRateD;
    static const Attribute  ReceivedkVAhRateA;
    static const Attribute  ReceivedkVAhRateB;
    static const Attribute  ReceivedkVAhRateC;
    static const Attribute  ReceivedkVAhRateD;
    static const Attribute  SumkVAhRateA;
    static const Attribute  SumkVAhRateB;
    static const Attribute  SumkVAhRateC;
    static const Attribute  SumkVAhRateD;
    static const Attribute  kVAr;
    static const Attribute  ReceivedkVAr;
    static const Attribute  kVArh;
    static const Attribute  kVArhRateA;
    static const Attribute  kVArhRateB;
    static const Attribute  kVArhRateC;
    static const Attribute  kVArhRateD;
    static const Attribute  LMGroupStatus;
    static const Attribute  LoadProfile;
    static const Attribute  LoadSideVoltageDetected;
    static const Attribute  MeterBoxCoverRemoval;
    static const Attribute  MaximumVoltage;
    static const Attribute  MaximumVoltageFrozen;
    static const Attribute  MinimumVoltage;
    static const Attribute  MinimumVoltageFrozen;
    static const Attribute  OutageLog;
    static const Attribute  OutageStatus;
    static const Attribute  OutofVoltage;
    static const Attribute  VoltageOutofLimits;
    static const Attribute  OverVoltage;
    static const Attribute  OverVoltageMeasured;
    static const Attribute  OverVoltageThreshold;
    static const Attribute  PeakDemand;
    static const Attribute  NetPeakDemand;
    static const Attribute  SumPeakDemand;
    static const Attribute  PeakDemandFrozen;
    static const Attribute  PeakDemandRateA;
    static const Attribute  PeakDemandFrozenRateA;
    static const Attribute  PeakDemandRateB;
    static const Attribute  PeakDemandFrozenRateB;
    static const Attribute  PeakDemandRateC;
    static const Attribute  PeakDemandFrozenRateC;
    static const Attribute  PeakDemandRateD;
    static const Attribute  PeakDemandFrozenRateD;
    static const Attribute  PeakDemandRateE;
    static const Attribute  NetPeakDemandRateA;
    static const Attribute  NetPeakDemandRateB;
    static const Attribute  NetPeakDemandRateC;
    static const Attribute  NetPeakDemandRateD;
    static const Attribute  SumPeakDemandRateA;
    static const Attribute  SumPeakDemandRateB;
    static const Attribute  SumPeakDemandRateC;
    static const Attribute  SumPeakDemandRateD;
    static const Attribute  ReceivedPeakDemand;
    static const Attribute  ReceivedPeakDemandRateA;
    static const Attribute  ReceivedPeakDemandRateB;
    static const Attribute  ReceivedPeakDemandRateC;
    static const Attribute  ReceivedPeakDemandRateD;
    static const Attribute  DeliveredkVA;
    static const Attribute  ReceivedkVA;
    static const Attribute  SumkVA;
    static const Attribute  PeakkVA;
    static const Attribute  PeakkVARateA;
    static const Attribute  PeakkVARateB;
    static const Attribute  PeakkVARateC;
    static const Attribute  PeakkVARateD;
    static const Attribute  PeakkVACoincidental;
    static const Attribute  ReceivedPeakkVA;
    static const Attribute  ReceivedPeakkVARateA;
    static const Attribute  ReceivedPeakkVARateB;
    static const Attribute  ReceivedPeakkVARateC;
    static const Attribute  ReceivedPeakkVARateD;
    static const Attribute  SumPeakkVA;
    static const Attribute  SumPeakkVARateA;
    static const Attribute  SumPeakkVARateB;
    static const Attribute  SumPeakkVARateC;
    static const Attribute  SumPeakkVARateD;
    static const Attribute  PeakkVAr;
    static const Attribute  PeakkVArCoincidental;
    static const Attribute  PeakkVArRateA;
    static const Attribute  PeakkVArRateB;
    static const Attribute  PeakkVArRateC;
    static const Attribute  PeakkVArRateD;
    static const Attribute  ReceivedPeakkVAr;
    static const Attribute  ReceivedPeakkVArRateA;
    static const Attribute  ReceivedPeakkVArRateB;
    static const Attribute  ReceivedPeakkVArRateC;
    static const Attribute  ReceivedPeakkVArRateD;
    static const Attribute  SumPeakkVAr;
    static const Attribute  SumPeakkVArRateA;
    static const Attribute  SumPeakkVArRateB;
    static const Attribute  SumPeakkVArRateC;
    static const Attribute  SumPeakkVArRateD;
    static const Attribute  PeakkVArh;
    static const Attribute  PeakkVArhCoincidental;
    static const Attribute  Phase;
    static const Attribute  PowerFactor;
    static const Attribute  PowerFactorCoincidental;
    static const Attribute  PowerFactorPhaseA;
    static const Attribute  PowerFactorPhaseB;
    static const Attribute  PowerFactorPhaseC;
    static const Attribute  PowerFailFlag;
    static const Attribute  ProfileChannel2;
    static const Attribute  ProfileChannel3;
    static const Attribute  RecordingInterval;
    static const Attribute  RelativeHumidity;
    static const Attribute  Relay1kWLoadSize;
    static const Attribute  Relay1RemainingControlTime;
    static const Attribute  Relay1RunTime;
    static const Attribute  Relay1ShedTime;
    static const Attribute  Relay2kWLoadSize;
    static const Attribute  Relay2RemainingControlTime;
    static const Attribute  Relay2RunTime;
    static const Attribute  Relay2ShedTime;
    static const Attribute  Relay3kWLoadSize;
    static const Attribute  Relay3RemainingControlTime;
    static const Attribute  Relay3RunTime;
    static const Attribute  Relay3ShedTime;
    static const Attribute  Relay4RemainingControlTime;
    static const Attribute  Relay4RunTime;
    static const Attribute  Relay4ShedTime;
    static const Attribute  ReportingInterval;
    static const Attribute  ReverseInductivekVArh;
    static const Attribute  ReversePowerFlag;
    static const Attribute  RFDemandResetStatus;
    static const Attribute  ServiceStatus;
    static const Attribute  TamperFlag;
    static const Attribute  Temperature;
    static const Attribute  TemperatureofDevice;
    static const Attribute  TotalLUFEventCount;
    static const Attribute  TotalLUVEventCount;
    static const Attribute  UnderVoltage;
    static const Attribute  UnderVoltageMeasured;
    static const Attribute  UnderVoltageThreshold;
    static const Attribute  UsageReading;
    static const Attribute  UsageFrozen;
    static const Attribute  UsageRateA;
    static const Attribute  UsageRateB;
    static const Attribute  UsageRateC;
    static const Attribute  UsageRateD;
    static const Attribute  UsageRateE;
    static const Attribute  WaterUsageReading;
    static const Attribute  Voltage;
    static const Attribute  VoltagePhaseA;
    static const Attribute  VoltagePhaseB;
    static const Attribute  VoltagePhaseC;
    static const Attribute  MinimumVoltagePhaseA;
    static const Attribute  MinimumVoltagePhaseB;
    static const Attribute  MinimumVoltagePhaseC;
    static const Attribute  MaximumVoltagePhaseA;
    static const Attribute  MaximumVoltagePhaseB;
    static const Attribute  MaximumVoltagePhaseC;
    static const Attribute  VoltageProfile;
    static const Attribute  WattHourPulseFailure;
    static const Attribute  ZeroUsageFlag;
    static const Attribute  ZigBeeLinkStatus;
    static const Attribute  TerminalBlockCoverRemoval;
    static const Attribute  IndoorTemperature;
    static const Attribute  OutdoorTemperature;
    static const Attribute  CoolSetTemperature;
    static const Attribute  HeatSetTemperature;

    static const Attribute  DeliveredkWh;
    static const Attribute  ReceivedkWh;
    static const Attribute  ReceivedkWhFrozen;
    static const Attribute  ReceivedkWhRateA;
    static const Attribute  ReceivedkWhRateB;
    static const Attribute  ReceivedkWhRateC;
    static const Attribute  ReceivedkWhRateD;
    static const Attribute  ReceivedkWhRateE;
    static const Attribute  ReceivedkVAh;

    static const Attribute  NetkWh;
    static const Attribute  NetkWhRateA;
    static const Attribute  NetkWhRateB;
    static const Attribute  NetkWhRateC;
    static const Attribute  NetkWhRateD;
    static const Attribute  NetkWhRateE;

    static const Attribute  SumkWh;
    static const Attribute  SumkWhRateA;
    static const Attribute  SumkWhRateB;
    static const Attribute  SumkWhRateC;
    static const Attribute  SumkWhRateD;
    static const Attribute  SumkVAh;
    static const Attribute  SumkVArh;
    static const Attribute  SumkVArhRateA;
    static const Attribute  SumkVArhRateB;
    static const Attribute  SumkVArhRateC;
    static const Attribute  SumkVArhRateD;

    static const Attribute  UsageperInterval;
    static const Attribute  DeliveredkWhperInterval;
    static const Attribute  ReceivedkWhperInterval;
    static const Attribute  SumkWhperInterval;
    static const Attribute  NetkWhperInterval;
    static const Attribute  SumkVAhperInterval;
    static const Attribute  SumkVArhperInterval;
    static const Attribute  WaterUsageperInterval;
    static const Attribute  ForwardInductivekVArhperInterval;
    static const Attribute  ForwardCapacitivekVArhperInterval;
    static const Attribute  ReverseInductivekVArhperInterval;
    static const Attribute  ReverseCapacitivekVArhperInterval;

    static const Attribute  DeliveredkWLoadProfile;
    static const Attribute  DeliveredkVArLoadProfile;
    static const Attribute  ReceivedkWLoadProfile;
    static const Attribute  SumkWLoadProfile;
    static const Attribute  NetkWLoadProfile;
    static const Attribute  SumkVALoadProfile;
    static const Attribute  SumkVArLoadProfile;

    static const Attribute  NetDeliveredkVArh;
    static const Attribute  NetDeliveredkVArhRateA;
    static const Attribute  NetDeliveredkVArhRateB;
    static const Attribute  NetDeliveredkVArhRateC;
    static const Attribute  NetDeliveredkVArhRateD;

    static const Attribute  ReceivedkVArh;
    static const Attribute  ReceivedkVArhRateA;
    static const Attribute  ReceivedkVArhRateB;
    static const Attribute  ReceivedkVArhRateC;
    static const Attribute  ReceivedkVArhRateD;
    static const Attribute  NetReceivedkVArh;
    static const Attribute  NetReceivedkVArhRateA;
    static const Attribute  NetReceivedkVArhRateB;
    static const Attribute  NetReceivedkVArhRateC;
    static const Attribute  NetReceivedkVArhRateD;

    static const Attribute  AlternateModeEntry;
    static const Attribute  ANSISecurityFailed;
    static const Attribute  BadUpgradeSecurityParameter;
    static const Attribute  ConfigurationError;
    static const Attribute  ClockError;
    static const Attribute  CrystalOscillatorError;
    static const Attribute  CurrentLoss;
    static const Attribute  CurrentWaveformDistortion;
    static const Attribute  DemandOverload;
    static const Attribute  DemandReadsAndReset;
    static const Attribute  DemandThresholdExceededWarning;
    static const Attribute  DNP3AddressChanged;
    static const Attribute  DisplayLockedByWarning;
    static const Attribute  EepromAccessError;
    static const Attribute  EncryptionKeyTableCrcError;
    static const Attribute  EndOfCalendarWarning;
    static const Attribute  EnergyAccumulatedWhileInStandbyMode;
    static const Attribute  FailedUpgradeSignatureVerification;
    static const Attribute  ImproperMeterEngineOperationWarning;
    static const Attribute  InactivePhaseCurrentDiagnosticError;
    static const Attribute  InternalCommunicationError;
    static const Attribute  InternalErrorFlag;
    static const Attribute  InvalidService;
    static const Attribute  LineFrequencyWarning;
    static const Attribute  LoadSideVoltageIsMissing;
    static const Attribute  LossOfAllCurrent;
    static const Attribute  LossOfPhaseACurrent;
    static const Attribute  LossOfPhaseCCurrent;
    static const Attribute  LowBatteryWarning;
    static const Attribute  LowLossPotential;
    static const Attribute  MassMemoryError;
    static const Attribute  MeasurementError;
    static const Attribute  MeterReconfigure;
    static const Attribute  MetrologyCommunicationFailure;
    static const Attribute  NonVolatileMemFailure;
    static const Attribute  OutstationDNP3SerCommLocked;
    static const Attribute  PasswordTableCrcError;
    static const Attribute  PhaseAngleDisplacement;
    static const Attribute  PhaseLoss;
    static const Attribute  PolarityCrossPhaseAndEnergyFlowDiagnostic;
    static const Attribute  PotentialIndicatorWarning;
    static const Attribute  PowerFailDataSaveError;
    static const Attribute  PqmTestFailureWarning;
    static const Attribute  RamError;
    static const Attribute  RegisterFullScaleExceeded;
    static const Attribute  ReversedAggregate;
    static const Attribute  ReversedPhaseA;
    static const Attribute  ReversedPhaseC;
    static const Attribute  RfnBlinkCount;
    static const Attribute  RfnBlinkRestoreCount;
    static const Attribute  RFNHighTemperatureAlarm;
    static const Attribute  RfnOutageCount;
    static const Attribute  RfnOutageRestoreCount;
    static const Attribute  RomError;
    static const Attribute  SeasonChange;
    static const Attribute  SecurityConfigurationError;
    static const Attribute  SelfCheckError;
    static const Attribute  ServiceCurrentTestFailureWarning;
    static const Attribute  ServiceDisconnectSwitchError;
    static const Attribute  ServiceDisconnectSwitchOpen;
    static const Attribute  ServiceDisconnectSwitchSensorError;
    static const Attribute  SiteScanError;
    static const Attribute  StuckSwitch;
    static const Attribute  TableCrcError;
    static const Attribute  THDVorTDDIError;
    static const Attribute  TimeAdjustment;
    static const Attribute  TimeSyncFailed;
    static const Attribute  TOUScheduleError;
    static const Attribute  Unconfigured;
    static const Attribute  Unprogrammed;
    static const Attribute  UserProgrammableTemperatureThresholdExceeded;
    static const Attribute  VoltageAlerts;
    static const Attribute  VoltageLoss;
    static const Attribute  VoltagePhaseAOut;
    static const Attribute  VoltagePhaseBOut;
    static const Attribute  VoltagePhaseCOut;
    static const Attribute  VoltagePhaseError;
    static const Attribute  FirmwareVersion;
    static const Attribute  IgnoredControlReason;
    static const Attribute  IpAddress;
    static const Attribute  LastControlReason;
    static const Attribute  NeutralCurrentSensor;
    static const Attribute  SerialNumber;
    static const Attribute  UdpPort;
    static const Attribute  ConnectedLoad;
    static const Attribute  DiversifiedLoad;
    static const Attribute  MaxLoadReduction;
    static const Attribute  AvailableLoadReduction;

    static const Attribute PorterCpuUtilization;
    static const Attribute DispatchCpuUtilization;
    static const Attribute ScannerCpuUtilization;
    static const Attribute CalcCpuUtilization;
    static const Attribute CapcontrolCpuUtilization;
    static const Attribute FdrCpuUtilization;
    static const Attribute MacsCpuUtilization;

    static const Attribute NotificationServerCpuUtilization;
    static const Attribute ServiceManagerCpuUtilization;
    static const Attribute WebServiceCpuUtilization;

    static const Attribute PorterMemoryUtilization;
    static const Attribute DispatchMemoryUtilization;
    static const Attribute ScannerMemoryUtilization;
    static const Attribute CalcMemoryUtilization;
    static const Attribute CapcontrolMemoryUtilization;
    static const Attribute FdrMemoryUtilization;
    static const Attribute MacsMemoryUtilization;

    static const Attribute NotificationServerMemoryUtilization;
    static const Attribute ServiceManagerMemoryUtilization;
    static const Attribute WebServiceMemoryUtilization;

    static const Attribute LoadManagementCpuUtilization;
    static const Attribute LoadManagementMemoryUtilization;
};


struct IM_EX_CTIBASE AttributeNotFound : std::exception
{
    std::string desc;

    AttributeNotFound(const std::string &name);

    const char* what() const override;
};