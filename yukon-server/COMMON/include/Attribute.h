#pragma once

#include "yukon.h"
#include "dlldefs.h"

#include "loggable.h"

#include <string>
#include <map>

class IM_EX_CTIBASE Attribute : public Cti::Loggable
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

    std::string toString() const override { return getName(); }

    bool operator==( const Attribute & rhs ) const;

    bool operator<( const Attribute & rhs ) const;

    //  this ordering should match BuiltInAttribute.java
    static const Attribute  Unknown;
    static const Attribute  BlinkCount;
    static const Attribute  CommunicationStatus;
    static const Attribute  ControlStatus;
    static const Attribute  CurrentNeutral;
    static const Attribute  Current;
    static const Attribute  CurrentPhaseA;
    static const Attribute  CurrentPhaseB;
    static const Attribute  CurrentPhaseC;
    static const Attribute  CurrentAngle;
    static const Attribute  CurrentAnglePhaseA;
    static const Attribute  CurrentAnglePhaseB;
    static const Attribute  CurrentAnglePhaseC;
    static const Attribute  CurrentWithoutVoltage;
    static const Attribute  Demand;
    static const Attribute  DeliveredDemand;
    static const Attribute  ReceivedDemand;
    static const Attribute  DemandPeakkVACoin;
    static const Attribute  InstantaneouskW;
    static const Attribute  SumkW;
    static const Attribute  NetkW;
    static const Attribute  DisconnectStatus;
    static const Attribute  FaultStatus;
    static const Attribute  ForwardInductivekVArh;
    static const Attribute  GeneralAlarmFlag;
    static const Attribute  IEDDemandResetCount;
    static const Attribute  kVAh;
    static const Attribute  kVAhQ124;
    static const Attribute  kVAhQ124RateA;
    static const Attribute  kVAhQ124RateB;
    static const Attribute  kVAhQ124RateC;
    static const Attribute  kVAhQ124RateD;
    static const Attribute  kVAhRateA;
    static const Attribute  kVAhRateB;
    static const Attribute  kVAhRateC;
    static const Attribute  kVAhRateD;
    static const Attribute  NetkVAh;
    static const Attribute  NetkVAhRateA;
    static const Attribute  NetkVAhRateB;
    static const Attribute  NetkVAhRateC;
    static const Attribute  NetkVAhRateD;
    static const Attribute  kVAhQ234;
    static const Attribute  kVAhQ234RateA;
    static const Attribute  kVAhQ234RateB;
    static const Attribute  kVAhQ234RateC;
    static const Attribute  kVAhQ234RateD;
    static const Attribute  ReceivedkVAhRateA;
    static const Attribute  ReceivedkVAhRateB;
    static const Attribute  ReceivedkVAhRateC;
    static const Attribute  ReceivedkVAhRateD;
    static const Attribute  SumkVAhRateA;
    static const Attribute  SumkVAhRateB;
    static const Attribute  SumkVAhRateC;
    static const Attribute  SumkVAhRateD;
    static const Attribute  kVAr;
    static const Attribute  DeliveredkVAr;
    static const Attribute  ReceivedkVAr;
    static const Attribute  kVArh;
    static const Attribute  kVArhRateA;
    static const Attribute  kVArhRateB;
    static const Attribute  kVArhRateC;
    static const Attribute  kVArhRateD;
    static const Attribute  LoadProfile;
    static const Attribute  LoadSideVoltageDetected;
    static const Attribute  MeterBoxCoverRemoval;
    static const Attribute  MaximumVoltage;
    static const Attribute  MaximumVoltageDaily;
    static const Attribute  MaximumVoltageFrozen;
    static const Attribute  MinimumVoltage;
    static const Attribute  MinimumVoltageDaily;
    static const Attribute  MinimumVoltageFrozen;
    static const Attribute  OutageLog;
    static const Attribute  OutageStatus;
    static const Attribute  OutofVoltage;
    static const Attribute  VoltageOutofLimits;
    static const Attribute  OverVoltage;
    static const Attribute  OverVoltageThreshold;
    static const Attribute  PeakDemand;
    static const Attribute  PeakDemandDaily;
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
    static const Attribute  kVA;
    static const Attribute  DeliveredkVA;
    static const Attribute  ReceivedkVA;
    static const Attribute  SumkVA;
    static const Attribute  NetkVA;
    static const Attribute  KvaPeakDemandCoin;
    static const Attribute  PeakkVA;
    static const Attribute  PeakkVARateA;
    static const Attribute  PeakkVARateB;
    static const Attribute  PeakkVARateC;
    static const Attribute  PeakkVARateD;
    static const Attribute  PeakkVAQ12;
    static const Attribute  PeakkVAQ12RateA;
    static const Attribute  PeakkVAQ12RateB;
    static const Attribute  PeakkVAQ12RateC;
    static const Attribute  PeakkVAQ12RateD;
    static const Attribute  PeakkVAFrozen;
    static const Attribute  PeakkVAFrozenRateA;
    static const Attribute  PeakkVAFrozenRateB;
    static const Attribute  PeakkVAFrozenRateC;
    static const Attribute  PeakkVAFrozenRateD;
    static const Attribute  PeakkVACoincidental;
    static const Attribute  PeakkVAQ124;
    static const Attribute  PeakkVAQ124RateA;
    static const Attribute  PeakkVAQ124RateB;
    static const Attribute  PeakkVAQ124RateC;
    static const Attribute  PeakkVAQ124RateD;
    static const Attribute  ReceivedPeakkVA;
    static const Attribute  ReceivedPeakkVARateA;
    static const Attribute  ReceivedPeakkVARateB;
    static const Attribute  ReceivedPeakkVARateC;
    static const Attribute  ReceivedPeakkVARateD;
    static const Attribute  ReceivedPeakkVAFrozen;
    static const Attribute  ReceivedPeakkVAFrozenRateA;
    static const Attribute  ReceivedPeakkVAFrozenRateB;
    static const Attribute  ReceivedPeakkVAFrozenRateC;
    static const Attribute  ReceivedPeakkVAFrozenRateD;
    static const Attribute  SumPeakkVA;
    static const Attribute  SumPeakkVARateA;
    static const Attribute  SumPeakkVARateB;
    static const Attribute  SumPeakkVARateC;
    static const Attribute  SumPeakkVARateD;
    static const Attribute  kVAQ12;
    static const Attribute  kVAQ34;
    static const Attribute  kVAQ13;
    static const Attribute  kVAQ24;
    static const Attribute  kVAQ124;
    static const Attribute  PeakkVAQ124Frozen;
    static const Attribute  PeakkVAQ124FrozenRateA;
    static const Attribute  PeakkVAQ124FrozenRateB;
    static const Attribute  PeakkVAQ124FrozenRateC;
    static const Attribute  PeakkVAQ124FrozenRateD;
    static const Attribute  SumPeakkVAFrozen;
    static const Attribute  SumkVAr;
    static const Attribute  NetkVAr;
    static const Attribute  PeakkVAr;
    static const Attribute  PeakkVArCoincidental;
    static const Attribute  PeakkVArRateA;
    static const Attribute  PeakkVArRateB;
    static const Attribute  PeakkVArRateC;
    static const Attribute  PeakkVArRateD;
    static const Attribute  PeakkVArQ14;
    static const Attribute  PeakkVArQ23;
    static const Attribute  CoincidentPeakkVArQ23;
    static const Attribute  CoincidentCumulativePeakkVArQ14;
    static const Attribute  CoincidentCumulativePeakkVArQ23;
    static const Attribute  ReceivedPeakkVAr;
    static const Attribute  ReceivedPeakkVArRateA;
    static const Attribute  ReceivedPeakkVArRateB;
    static const Attribute  ReceivedPeakkVArRateC;
    static const Attribute  ReceivedPeakkVArRateD;
    static const Attribute  ReceivedCoincidentPeakkVAr;
    static const Attribute  SumPeakkVAr;
    static const Attribute  SumPeakkVArRateA;
    static const Attribute  SumPeakkVArRateB;
    static const Attribute  SumPeakkVArRateC;
    static const Attribute  SumPeakkVArRateD;
    static const Attribute  kVArQ13;
    static const Attribute  kVArQ24;
    static const Attribute  kVArQ14;
    static const Attribute  kVArQ23;
    static const Attribute  SumPeakkVArFrozen;
    static const Attribute  DeliveredPeakkVArFrozen;
    static const Attribute  DeliveredPeakkVArFrozenRateA;
    static const Attribute  DeliveredPeakkVArFrozenRateB;
    static const Attribute  DeliveredPeakkVArFrozenRateC;
    static const Attribute  DeliveredPeakkVArFrozenRateD;
    static const Attribute  PeakkVArh;
    static const Attribute  PeakkVArhCoincidental;
    static const Attribute  Phase;
    static const Attribute  PowerFactorAvgDelivered;
    static const Attribute  PowerFactorAvgReceived;
    static const Attribute  PowerFactor;
    static const Attribute  PowerFactorCoincidental;
    static const Attribute  MinimumPowerFactor;
    static const Attribute  MinimumPowerFactorFrozen;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakKVA;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakkVARateA;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakkVARateB;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakkVARateC;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakkVARateD;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakkVAr;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakkVArRateA;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakkVArRateB;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakkVArRateC;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakkVArRateD;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakKw;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakKwRateA;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakKwRateB;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakKwRateC;
    static const Attribute  CoincidentPowerFactorAtDeliveredPeakKwRateD;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakKVA;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakkVARateA;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakkVARateB;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakkVARateC;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakkVARateD;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakkVAr;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakkVArRateA;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakkVArRateB;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakkVArRateC;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakkVArRateD;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakKw;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakKwRateA;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakKwRateB;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakKwRateC;
    static const Attribute  CoincidentPowerFactorAtReceivedPeakKwRateD;
    static const Attribute  CoincidentPowerFactorAtSumPeakKVA;
    static const Attribute  CoincidentPowerFactorAtSumPeakkVARateA;
    static const Attribute  CoincidentPowerFactorAtSumPeakkVARateB;
    static const Attribute  CoincidentPowerFactorAtSumPeakkVARateC;
    static const Attribute  CoincidentPowerFactorAtSumPeakkVARateD;
    static const Attribute  CoincidentPowerFactorAtSumPeakkVAr;
    static const Attribute  CoincidentPowerFactorAtSumPeakkVArRateA;
    static const Attribute  CoincidentPowerFactorAtSumPeakkVArRateB;
    static const Attribute  CoincidentPowerFactorAtSumPeakkVArRateC;
    static const Attribute  CoincidentPowerFactorAtSumPeakkVArRateD;
    static const Attribute  CoincidentPowerFactorAtSumPeakKwRateA;
    static const Attribute  CoincidentPowerFactorAtSumPeakKwRateB;
    static const Attribute  CoincidentPowerFactorAtSumPeakKwRateC;
    static const Attribute  CoincidentPowerFactorAtSumPeakKwRateD;
    static const Attribute  PowerFactorAvgQ124;
    static const Attribute  PowerFactorAvgQ234;
    static const Attribute  PowerFactorAvgQ1234;
    static const Attribute  PowerFactorAverage;
    static const Attribute  PowerFactorAverageFrozen;
    static const Attribute  PowerFactorAnglePhaseA;
    static const Attribute  PowerFactorAnglePhaseB;
    static const Attribute  PowerFactorAnglePhaseC;
    static const Attribute  PowerFactorPhaseA;
    static const Attribute  PowerFactorPhaseB;
    static const Attribute  PowerFactorPhaseC;
    static const Attribute  PowerFailFlag;
    static const Attribute  ProfileChannel2;
    static const Attribute  ProfileChannel3;
    static const Attribute  RecordingInterval;
    static const Attribute  RelativeHumidity;
    static const Attribute  Relay1CallForCool;
    static const Attribute  Relay1kWLoadSize;
    static const Attribute  Relay1RemainingControlTime;
    static const Attribute  Relay1RunTime;
	static const Attribute  Relay1RunTime5Min;
	static const Attribute  Relay1RunTime15Min;
	static const Attribute  Relay1RunTime30Min;
    static const Attribute  Relay1ShedStatus;
    static const Attribute  Relay1ShedTime;
    static const Attribute  Relay1ShedTime5Min;
    static const Attribute  Relay1ShedTime15Min;
    static const Attribute  Relay1ShedTime30Min;
    static const Attribute  Relay1LoadState;
    static const Attribute  Relay2CallForCool;
    static const Attribute  Relay2kWLoadSize;
    static const Attribute  Relay2RemainingControlTime;
    static const Attribute  Relay2RunTime;
	static const Attribute  Relay2RunTime5Min;
	static const Attribute  Relay2RunTime15Min;
	static const Attribute  Relay2RunTime30Min;
    static const Attribute  Relay2ShedStatus;
    static const Attribute  Relay2ShedTime;
    static const Attribute  Relay2ShedTime5Min;
    static const Attribute  Relay2ShedTime15Min;
    static const Attribute  Relay2ShedTime30Min;
    static const Attribute  Relay2LoadState;
    static const Attribute  Relay3CallForCool;
    static const Attribute  Relay3kWLoadSize;
    static const Attribute  Relay3RemainingControlTime;
    static const Attribute  Relay3RunTime;
	static const Attribute  Relay3RunTime5Min;
	static const Attribute  Relay3RunTime15Min;
	static const Attribute  Relay3RunTime30Min;
    static const Attribute  Relay3ShedStatus;
    static const Attribute  Relay3ShedTime;
    static const Attribute  Relay3ShedTime5Min;
    static const Attribute  Relay3ShedTime15Min;
    static const Attribute  Relay3ShedTime30Min;
    static const Attribute  Relay3LoadState;
    static const Attribute  Relay4CallForCool;
    static const Attribute  Relay4kWLoadSize;
    static const Attribute  Relay4RemainingControlTime;
    static const Attribute  Relay4RunTime;
	static const Attribute  Relay4RunTime5Min;
	static const Attribute  Relay4RunTime15Min;
	static const Attribute  Relay4RunTime30Min;
    static const Attribute  Relay4ShedStatus;
    static const Attribute  Relay4ShedTime;
    static const Attribute  Relay4ShedTime5Min;
    static const Attribute  Relay4ShedTime15Min;
    static const Attribute  Relay4ShedTime30Min;
    static const Attribute  Relay4LoadState;
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
    static const Attribute  TotalLOFEventCount;
    static const Attribute  TotalLOVEventCount;
    static const Attribute  LOFTrigger;
    static const Attribute  LOFRestore;
    static const Attribute  LOFTriggerTime;
    static const Attribute  LOFRestoreTime;
    static const Attribute  LOFStartRandomTime;
    static const Attribute  LOFEndRandomTime;
    static const Attribute  LOFMinEventDuration;
    static const Attribute  LOFMaxEventDuration;
    static const Attribute  LOVTrigger;
    static const Attribute  LOVRestore;
    static const Attribute  LOVTriggerTime;
    static const Attribute  LOVRestoreTime;
    static const Attribute  LOVStartRandomTime;
    static const Attribute  LOVEndRandomTime;
    static const Attribute  LOVMinEventDuration;
    static const Attribute  LOVMaxEventDuration;
    static const Attribute  MinimumEventSeparation;
    static const Attribute  PowerQualityResponseEnabled;
    static const Attribute  UnderVoltage;
    static const Attribute  UnderVoltageThreshold;
    static const Attribute  UsageReading;
    static const Attribute  UsageFrozen;
    static const Attribute  UsageRateA;
    static const Attribute  UsageRateB;
    static const Attribute  UsageRateC;
    static const Attribute  UsageRateD;
    static const Attribute  UsageRateE;
    static const Attribute  WaterUsageReading;
    static const Attribute  GasUsageReading;
    static const Attribute  Voltage;
    static const Attribute  VoltagePhaseA;
    static const Attribute  VoltagePhaseB;
    static const Attribute  VoltagePhaseC;
    static const Attribute  BatteryVoltage;
    static const Attribute  MinimumVoltagePhaseA;
    static const Attribute  MinimumVoltagePhaseB;
    static const Attribute  MinimumVoltagePhaseC;
    static const Attribute  AverageVoltage;
    static const Attribute  AverageVoltagePhaseA;
    static const Attribute  AverageVoltagePhaseB;
    static const Attribute  AverageVoltagePhaseC;
    static const Attribute  MaximumVoltagePhaseA;
    static const Attribute  MaximumVoltagePhaseB;
    static const Attribute  MaximumVoltagePhaseC;
    static const Attribute  VoltageProfile;
    static const Attribute  WattHourPulseFailure;
    static const Attribute  WiFiApConnectionFailure;
    static const Attribute  WiFiBssidChange;
    static const Attribute  WiFiPassphraseChange;
    static const Attribute  WiFiSsidChange;
    static const Attribute  CellularConnectionFailed;
    static const Attribute  CellularModemDisabled;
    static const Attribute  CellularApnChanged;
    static const Attribute  CellularSimCardInsertedRemoved;
    static const Attribute  ZeroUsageFlag;
    static const Attribute  ZigBeeLinkStatus;
    static const Attribute  TerminalBlockCoverRemoval;
    static const Attribute  IndoorTemperature;
    static const Attribute  OutdoorTemperature;
    static const Attribute  CoolSetTemperature;
    static const Attribute  HeatSetTemperature;

    static const Attribute  DeliveredkWh;
    static const Attribute  DeliveredkWhRateA;
    static const Attribute  DeliveredkWhRateB;
    static const Attribute  DeliveredkWhRateC;
    static const Attribute  DeliveredkWhRateD;
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
    static const Attribute  DeliveredkWhRateAperInterval;
    static const Attribute  DeliveredkWhRateBperInterval;
    static const Attribute  DeliveredkWhRateCperInterval;
    static const Attribute  DeliveredkWhRateDperInterval;
    static const Attribute  ReceivedkWhperInterval;
    static const Attribute  ReceivedkWhRateAperInterval;
    static const Attribute  ReceivedkWhRateBperInterval;
    static const Attribute  ReceivedkWhRateCperInterval;
    static const Attribute  ReceivedkWhRateDperInterval;
    static const Attribute  SumkWhperInterval;
    static const Attribute  SumkWhRateAperInterval;
    static const Attribute  SumkWhRateBperInterval;
    static const Attribute  SumkWhRateCperInterval;
    static const Attribute  SumkWhRateDperInterval;
    static const Attribute  NetkWhperInterval;
    static const Attribute  NetkWhRateAperInterval;
    static const Attribute  NetkWhRateBperInterval;
    static const Attribute  NetkWhRateCperInterval;
    static const Attribute  NetkWhRateDperInterval;
    static const Attribute  SumkVAhperInterval;
    static const Attribute  DeliveredkVArhperInterval;
    static const Attribute  SumkVArhperInterval;
    static const Attribute  WaterUsageperInterval;
    static const Attribute  GasUsageperInterval;
    static const Attribute  ForwardInductivekVArhperInterval;
    static const Attribute  ForwardCapacitivekVArhperInterval;
    static const Attribute  ReverseInductivekVArhperInterval;
    static const Attribute  ReverseCapacitivekVArhperInterval;
    static const Attribute  DeliveredkVAhperInterval;
    static const Attribute  ReceivedkVAhperInterval;

    static const Attribute  DeliveredkWLoadProfile;
    static const Attribute  DeliveredkWRateALoadProfile;
    static const Attribute  DeliveredkWRateBLoadProfile;
    static const Attribute  DeliveredkWRateCLoadProfile;
    static const Attribute  DeliveredkWRateDLoadProfile;
    static const Attribute  DeliveredkVArLoadProfile;
    static const Attribute  ReceivedkWLoadProfile;
    static const Attribute  ReceivedkWRateALoadProfile;
    static const Attribute  ReceivedkWRateBLoadProfile;
    static const Attribute  ReceivedkWRateCLoadProfile;
    static const Attribute  ReceivedkWRateDLoadProfile;
    static const Attribute  SumkWLoadProfile;
    static const Attribute  SumkWRateALoadProfile;
    static const Attribute  SumkWRateBLoadProfile;
    static const Attribute  SumkWRateCLoadProfile;
    static const Attribute  SumkWRateDLoadProfile;
    static const Attribute  NetkWLoadProfile;
    static const Attribute  NetkWRateALoadProfile;
    static const Attribute  NetkWRateBLoadProfile;
    static const Attribute  NetkWRateCLoadProfile;
    static const Attribute  NetkWRateDLoadProfile;
    static const Attribute  SumkVALoadProfile;
    static const Attribute  SumkVArLoadProfile;
    static const Attribute  kVALoadProfile;

    static const Attribute  NetkVArh;
    static const Attribute  NetkVArhRateA;
    static const Attribute  NetkVArhRateB;
    static const Attribute  NetkVArhRateC;
    static const Attribute  NetkVArhRateD;
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
    static const Attribute  BatteryEndOfLife;
    static const Attribute  ConfigurationError;
    static const Attribute  ClockError;
    static const Attribute  CRCFailureMemoryCorrupt;
    static const Attribute  CrystalOscillatorError;
    static const Attribute  CurrentLoss;
    static const Attribute  CurrentWaveformDistortion;
    static const Attribute  DemandOverload;
    static const Attribute  DemandReadsAndReset;
    static const Attribute  DemandThresholdExceededWarning;
    static const Attribute  DNP3AddressChanged;
    static const Attribute  DisplayLockedByWarning;
    static const Attribute  EepromAccessError;
    static const Attribute  EmptyPipe;
    static const Attribute  Encoder;
    static const Attribute  EncryptionKeyTableCrcError;
    static const Attribute  EndOfCalendarWarning;
    static const Attribute  EnergyAccumulatedWhileInStandbyMode;
    static const Attribute  ExceedingMaximumFlow;
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
    static const Attribute  MeterFunctioningCorrectly;
    static const Attribute  MeterReconfigure;
    static const Attribute  MeterProgrammingAttempted;
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
    static const Attribute  ReverseFlow;
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
    static const Attribute  SensorError;
    static const Attribute  ServiceCurrentTestFailureWarning;
    static const Attribute  ServiceDisconnectSwitchError;
    static const Attribute  ServiceDisconnectSwitchOpen;
    static const Attribute  ServiceDisconnectSwitchSensorError;
    static const Attribute  SiteScanError;
    static const Attribute  StorageMode;
    static const Attribute  StuckSwitch;
    static const Attribute  SuspectedLeak;
    static const Attribute  TableCrcError;
    static const Attribute  TemperatureOutOfRange;
    static const Attribute  THDVorTDDIError;
    static const Attribute  ThirtyDaysNoUsage;
    static const Attribute  TimeAdjustment;
    static const Attribute  TimeSyncFailed;
    static const Attribute  TOUScheduleChange;
    static const Attribute  TOUScheduleError;
    static const Attribute  UltraCapacitorBad;
    static const Attribute  Unconfigured;
    static const Attribute  Unprogrammed;
    static const Attribute  UserProgrammableTemperatureThresholdExceeded;
    static const Attribute  VibrationTiltTamperDetected;
    static const Attribute  VoltageAlerts;
    static const Attribute  VoltageLoss;
    static const Attribute  VoltagePhaseAOut;
    static const Attribute  VoltagePhaseBOut;
    static const Attribute  VoltagePhaseCOut;
    static const Attribute  VoltagePhaseError;

    static const Attribute  NoEncoderFound;
    static const Attribute  ParityError;
    static const Attribute  NoEOFDetected;
    static const Attribute  UndeterminedProtocol;
    static const Attribute  FieldExceededMaximumDigits;
    static const Attribute  SerialNumberReadError;
    static const Attribute  ChecksumError;
    static const Attribute  TamperCableCut;
    static const Attribute  DebugEvent;

    static const Attribute  RegisterRemoval;
    static const Attribute  MagnetTampering;
    static const Attribute  Pulse1SwitchFailure;
    static const Attribute  Pulse2SwitchFailure;
    static const Attribute  NodeCommissionEvent;
    static const Attribute  LostAssociation;
    static const Attribute  FieldExceededMaximumDigits;

    static const Attribute  TamperNoUsageOver24Hours;
    static const Attribute  TamperReverseWhDetected;
    static const Attribute  TamperLargeIncreaseAfterOutage;
    static const Attribute  TamperLargeDecreaseAfterOutage;

    static const Attribute  SecurityAlarm;
    static const Attribute  PowerFailure;
    static const Attribute  RadioFailure;
    static const Attribute  DoorOpen;
    static const Attribute  NodeCountExceeded;
    static const Attribute  UpsBatteryVoltageLow;
    static const Attribute  CertificateExpiration;
    static const Attribute  HighDiskUsage;
    static const Attribute  RTCBatteryFailure;
    static const Attribute  ACPowerFailure;

    static const Attribute  StreamingCapableDeviceCount;
    static const Attribute  StreamingActiveDeviceCount;
    static const Attribute  DataStreamingLoad;
    static const Attribute  ReadyNodes;

    static const Attribute  FirmwareVersion;
    static const Attribute  FirmwareVersionMajor;
    static const Attribute  FirmwareVersionMinor;
    static const Attribute  IgnoredControlReason;
    static const Attribute  IpAddress;
    static const Attribute  LastControlReason;
    static const Attribute  NeutralCurrentSensor;
    static const Attribute  SerialNumber;
    static const Attribute  UdpPort;
    
    static const Attribute  LMGroupStatus;
    static const Attribute  LMDailyHistory;
    static const Attribute  LMMonthHistory;
    static const Attribute  LMSeasonHistory;
    static const Attribute  LMAnnualHistory;
    static const Attribute  LMControlCountdown;
    
    static const Attribute  ConnectedLoad;
    static const Attribute  DiversifiedLoad;
    static const Attribute  MaxLoadReduction;
    static const Attribute  AvailableLoadReduction;

    static const Attribute  PorterCpuUtilization;
    static const Attribute  DispatchCpuUtilization;
    static const Attribute  ScannerCpuUtilization;
    static const Attribute  CalcCpuUtilization;
    static const Attribute  CapcontrolCpuUtilization;
    static const Attribute  FdrCpuUtilization;
    static const Attribute  MacsCpuUtilization;

    static const Attribute  NotificationServerCpuUtilization;
    static const Attribute  ServiceManagerCpuUtilization;
    static const Attribute  WebServiceCpuUtilization;
    static const Attribute  MessageBrokerCpuUtilization;

    static const Attribute  PorterMemoryUtilization;
    static const Attribute  DispatchMemoryUtilization;
    static const Attribute  ScannerMemoryUtilization;
    static const Attribute  CalcMemoryUtilization;
    static const Attribute  CapcontrolMemoryUtilization;
    static const Attribute  FdrMemoryUtilization;
    static const Attribute  MacsMemoryUtilization;

    static const Attribute  NotificationServerMemoryUtilization;
    static const Attribute  ServiceManagerMemoryUtilization;
    static const Attribute  WebServiceMemoryUtilization;
    static const Attribute  MessageBrokerMemoryUtilization;

    static const Attribute  LoadManagementCpuUtilization;
    static const Attribute  LoadManagementMemoryUtilization;

    static const Attribute  ThermostatRelayState;

    static const Attribute  AnalogInputOne;
    static const Attribute  AutoBlockEnable;
    static const Attribute  AutoRemoteControl;
    static const Attribute  AutoVoltageControl;
    static const Attribute  BadRelay;
    static const Attribute  CloseOperationCount;
    static const Attribute  ControlMode;
    static const Attribute  ControlPoint;
    static const Attribute  DailyMaxOperations;
    static const Attribute  DeltaVoltage;
    static const Attribute  DSTActive;
    static const Attribute  EnableOvuvControl;
    static const Attribute  EnableVarControl;
    static const Attribute  EnableTemperatureControl;
    static const Attribute  EnableTimeControl;
    static const Attribute  ForwardBandwidth;
    static const Attribute  ForwardSetPoint;
    static const Attribute  HeartbeatTimerConfig;
    static const Attribute  HighVoltage;
    static const Attribute  IgnoredIndicator;
    static const Attribute  KeepAlive;
    static const Attribute  LastControlReasonAnalog;
    static const Attribute  LastControlReasonDigital;
    static const Attribute  LastControlReasonLocal;
    static const Attribute  LastControlReasonNeutralFault;
    static const Attribute  LastControlReasonOvUv;
    static const Attribute  LastControlReasonRemote;
    static const Attribute  LastControlReasonScheduled;
    static const Attribute  LastControlReasonTemperature;
    static const Attribute  LowVoltage;
    static const Attribute  NeutralCurrentAlarmThreshold;
    static const Attribute  NeutralCurrentFault;
    static const Attribute  NeutralLockout;
    static const Attribute  OpenOperationCount;
    static const Attribute  OperationFailedNeutralCurrent;
    static const Attribute  OverUnderVoltageTrackTime;
    static const Attribute  OverVoltageCount;
    static const Attribute  ReferenceSignalReceivedPower;
    static const Attribute  ReferenceSignalReceivedQuality;
    static const Attribute  RadioSignalStrengthIndicator;
    static const Attribute  RecloseBlocked;
    static const Attribute  ScadaOverrideClear;
    static const Attribute  ScadaOverrideControlPoint;
    static const Attribute  ScadaOverrideCountdownTimer;
    static const Attribute  ScadaOverrideEnable;
    static const Attribute  ScadaOverrideHeartbeat;
    static const Attribute  ScadaOverrideMode;
    static const Attribute  SignalToInterferencePlusNoiseRatio;
    static const Attribute  SourceVoltage;
    static const Attribute  TapDown;
    static const Attribute  TapPosition;
    static const Attribute  TapUp;
    static const Attribute  TemperatureAlarm;
    static const Attribute  Terminate;
    static const Attribute  TimeTempControlSeasonOne;
    static const Attribute  TimeTempControlSeasonTwo;
    static const Attribute  TotalOperationCount;
    static const Attribute  UnderVoltageCount;
    static const Attribute  VarControl;
    static const Attribute  VoltageControl;
    static const Attribute  VoltageDeltaAbnormal;
    static const Attribute  PortQueueCount;

    static const Attribute  ReverseBandwidth;
    static const Attribute  ReverseSetPoint;
    static const Attribute  ReverseFlowIndicator;
    static const Attribute  PowerFlowIndeterminate;
    static const Attribute  ControlPowerFlowReverse;

    static const Attribute  ConfigurationUpdatedHash;
    static const Attribute  EventCancelled;
    static const Attribute  EventReceived;
    static const Attribute  EventStarted;
    static const Attribute  EventStopped;
    static const Attribute  EventSuperseded;
    static const Attribute  MemoryMapLost;
    static const Attribute  RadioLinkQuality;

    static const Attribute  CommsLossCount;
    static const Attribute  FirmwareUpdateStatus;
    static const Attribute  Frequency;
    static const Attribute  Relay1ActivationStatus;
    static const Attribute  Relay1ColdLoadPickupTime;
    static const Attribute  Relay2ActivationStatus;
    static const Attribute  Relay2ColdLoadPickupTime;
    static const Attribute  Relay3ActivationStatus;
    static const Attribute  Relay3ColdLoadPickupTime;
    static const Attribute  Relay4ActivationStatus;
    static const Attribute  Relay4ColdLoadPickupTime;
};


struct IM_EX_CTIBASE AttributeNotFound : std::exception
{
    std::string desc;

    AttributeNotFound(const std::string &name);

    const char* what() const override;
};
