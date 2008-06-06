
/*---------------------------------------------------------------------------
        Filename:  cctwowaycbcpoints.h
        
        Programmer:  Julie Richter
                
        Description:    Header file for CtiCCTwoWayPoints
                        CtiCCTwoWayPoints maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CtiCCTwoWayPointsIMPL_H
#define CtiCCTwoWayPointsIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 
#include <list>


#include "msg_cmd.h"
#include "dbaccess.h"
#include "observe.h"
#include "types.h"


                
class CtiCCTwoWayPoints  
{

public:

  //RWDECLARE_COLLECTABLE( CtiCCTwoWayPoints )


    CtiCCTwoWayPoints(LONG paoid);
    CtiCCTwoWayPoints(RWDBReader& rdr);
    CtiCCTwoWayPoints(const CtiCCTwoWayPoints& cap);

    virtual ~CtiCCTwoWayPoints();

    LONG getPAOId() const;
    LONG getCapacitorBankStateId() const;
    LONG getCapacitorBankState() const;
    LONG getReCloseBlockedId() const;
    LONG getReCloseBlocked() const;
    LONG getControlModeId() const;
    LONG getControlMode() const;
    LONG getAutoVoltControlId() const;
    LONG getAutoVoltControl() const;
    LONG getLastControlLocalId() const;
    LONG getLastControlLocal() const;
    LONG getLastControlRemoteId() const;
    LONG getLastControlRemote() const;
    LONG getLastControlOvUvId() const;
    LONG getLastControlOvUv() const;
    LONG getLastControlNeutralFaultId() const;
    LONG getLastControlNeutralFault() const;
    LONG getLastControlScheduledId() const;
    LONG getLastControlScheduled() const;
    LONG getLastControlDigitalId() const;
    LONG getLastControlDigital() const;
    LONG getLastControlAnalogId() const;
    LONG getLastControlAnalog() const;
    LONG getLastControlTemperatureId() const;
    LONG getLastControlTemperature() const;
    LONG getOvConditionId() const;
    LONG getOvCondition() const;
    LONG getUvConditionId() const;
    LONG getUvCondition() const;
    LONG getOpFailedNeutralCurrentId() const;
    LONG getOpFailedNeutralCurrent() const;
    LONG getNeutralCurrentFaultId() const;
    LONG getNeutralCurrentFault() const;
    LONG getBadRelayId() const;
    LONG getBadRelay() const;
    LONG getDailyMaxOpsId() const;
    LONG getDailyMaxOps() const;
    LONG getVoltageDeltaAbnormalId() const;
    LONG getVoltageDeltaAbnormal() const;
    LONG getTempAlarmId() const;
    LONG getTempAlarm() const;
    LONG getDSTActiveId() const;
    LONG getDSTActive() const;
    LONG getNeutralLockoutId() const;
    LONG getNeutralLockout() const;
    LONG getRSSIId() const;
    LONG getRSSI() const;
    LONG getIgnoredIndicatorId() const;
    LONG getIgnoredIndicator() const;
    LONG getVoltageId() const;       
    LONG getVoltage() const;         
    LONG getHighVoltageId() const;   
    LONG getHighVoltage() const;     
    LONG getLowVoltageId() const;    
    LONG getLowVoltage() const;      
    LONG getDeltaVoltageId() const;  
    LONG getDeltaVoltage() const;    
    LONG getAnalogInput1Id() const;  
    LONG getAnalogInput1() const;    
    LONG getTemperatureId() const;   
    LONG getTemperature() const; 

    LONG getIgnoredReasonId() const;
    LONG getIgnoredReason() const;
    LONG getUvSetPointId() const;
    LONG getUvSetPoint() const;
    LONG getOvSetPointId() const;
    LONG getOvSetPoint() const;
    LONG getOVUVTrackTimeId() const;
    LONG getOVUVTrackTime() const;
    LONG getNeutralCurrentSensorId() const;
    LONG getNeutralCurrentSensor() const;
    LONG getNeutralCurrentAlarmSetPointId() const;
    LONG getNeutralCurrentAlarmSetPoint() const;
    LONG getUDPIpAddressId() const;
    ULONG getUDPIpAddress() const;
    LONG getUDPPortNumberId() const;
    LONG getUDPPortNumber() const;
    LONG getTotalOpCountId() const;  
    LONG getTotalOpCount() const;    
    LONG getOvCountId() const;       
    LONG getOvCount() const;         
    LONG getUvCountId() const;       
    LONG getUvCount() const; 
    const CtiTime& getOvUvCountResetDate() const;
    const CtiTime& getLastOvUvDateTime() const;
    string getLastControlText() const;
    LONG getLastControl() const;
    INT getLastControlReason() const;
    
    
    CtiCCTwoWayPoints& setPAOId(LONG paoId);
    CtiCCTwoWayPoints& setCapacitorBankStateId(LONG pointId);
    CtiCCTwoWayPoints& setCapacitorBankState(LONG value);
    CtiCCTwoWayPoints& setReCloseBlockedId(LONG pointId);
    CtiCCTwoWayPoints& setReCloseBlocked(LONG value);
    CtiCCTwoWayPoints& setControlModeId(LONG pointId);
    CtiCCTwoWayPoints& setControlMode(LONG value);
    CtiCCTwoWayPoints& setAutoVoltControlId(LONG pointId);
    CtiCCTwoWayPoints& setAutoVoltControl(LONG value);
    CtiCCTwoWayPoints& setLastControlLocalId(LONG pointId);
    CtiCCTwoWayPoints& setLastControlLocal(LONG value);
    CtiCCTwoWayPoints& setLastControlRemoteId(LONG pointId);
    CtiCCTwoWayPoints& setLastControlRemote(LONG value);
    CtiCCTwoWayPoints& setLastControlOvUvId(LONG pointId);
    CtiCCTwoWayPoints& setLastControlOvUv(LONG value);
    CtiCCTwoWayPoints& setLastControlNeutralFaultId(LONG pointId);
    CtiCCTwoWayPoints& setLastControlNeutralFault(LONG value);
    CtiCCTwoWayPoints& setLastControlScheduledId(LONG pointId);
    CtiCCTwoWayPoints& setLastControlScheduled(LONG value);
    CtiCCTwoWayPoints& setLastControlDigitalId(LONG pointId);
    CtiCCTwoWayPoints& setLastControlDigital(LONG value);
    CtiCCTwoWayPoints& setLastControlAnalogId(LONG pointId);
    CtiCCTwoWayPoints& setLastControlAnalog(LONG value);
    CtiCCTwoWayPoints& setLastControlTemperatureId(LONG pointId);
    CtiCCTwoWayPoints& setLastControlTemperature(LONG value);
    CtiCCTwoWayPoints& setOvConditionId(LONG pointId);
    CtiCCTwoWayPoints& setOvCondition(LONG value);
    CtiCCTwoWayPoints& setUvConditionId(LONG pointId);
    CtiCCTwoWayPoints& setUvCondition(LONG value);
    CtiCCTwoWayPoints& setOpFailedNeutralCurrentId(LONG pointId);
    CtiCCTwoWayPoints& setOpFailedNeutralCurrent(LONG value);
    CtiCCTwoWayPoints& setNeutralCurrentFaultId(LONG pointId);
    CtiCCTwoWayPoints& setNeutralCurrentFault(LONG value);
    CtiCCTwoWayPoints& setBadRelayId(LONG pointId);
    CtiCCTwoWayPoints& setBadRelay(LONG value);
    CtiCCTwoWayPoints& setDailyMaxOpsId(LONG pointId);
    CtiCCTwoWayPoints& setDailyMaxOps(LONG value);
    CtiCCTwoWayPoints& setVoltageDeltaAbnormalId(LONG pointId);
    CtiCCTwoWayPoints& setVoltageDeltaAbnormal(LONG value);
    CtiCCTwoWayPoints& setTempAlarmId(LONG pointId);
    CtiCCTwoWayPoints& setTempAlarm(LONG value);
    CtiCCTwoWayPoints& setDSTActiveId(LONG pointId);
    CtiCCTwoWayPoints& setDSTActive(LONG value);
    CtiCCTwoWayPoints& setNeutralLockoutId(LONG pointId);
    CtiCCTwoWayPoints& setNeutralLockout(LONG value);
    CtiCCTwoWayPoints& setRSSIId(LONG pointId);
    CtiCCTwoWayPoints& setRSSI(LONG value);
    CtiCCTwoWayPoints& setIgnoredIndicatorId(LONG pointId);
    CtiCCTwoWayPoints& setIgnoredIndicator(LONG value);
    CtiCCTwoWayPoints& setVoltageId(LONG pointId);
    CtiCCTwoWayPoints& setVoltage(LONG value);    
    CtiCCTwoWayPoints& setHighVoltageId(LONG pointId);
    CtiCCTwoWayPoints& setHighVoltage(LONG value);    
    CtiCCTwoWayPoints& setLowVoltageId(LONG pointId);
    CtiCCTwoWayPoints& setLowVoltage(LONG value);    
    CtiCCTwoWayPoints& setDeltaVoltageId(LONG pointId);
    CtiCCTwoWayPoints& setDeltaVoltage(LONG value);    
    CtiCCTwoWayPoints& setAnalogInput1Id(LONG pointId);
    CtiCCTwoWayPoints& setAnalogInput1(LONG value);    
    CtiCCTwoWayPoints& setTemperatureId(LONG pointId);
    CtiCCTwoWayPoints& setTemperature(LONG value); 
    CtiCCTwoWayPoints& setIgnoredReasonId(LONG pointId);
    CtiCCTwoWayPoints& setIgnoredReason(LONG value); 
    CtiCCTwoWayPoints& setUvSetPointId(LONG pointId);
    CtiCCTwoWayPoints& setUvSetPoint(LONG value); 
    CtiCCTwoWayPoints& setOvSetPointId(LONG pointId);
    CtiCCTwoWayPoints& setOvSetPoint(LONG value); 
    CtiCCTwoWayPoints& setOVUVTrackTimeId(LONG pointId);
    CtiCCTwoWayPoints& setOVUVTrackTime(LONG value);
    CtiCCTwoWayPoints& setNeutralCurrentSensorId(LONG pointId);
    CtiCCTwoWayPoints& setNeutralCurrentSensor(LONG value); 
    CtiCCTwoWayPoints& setNeutralCurrentAlarmSetPointId(LONG pointId);
    CtiCCTwoWayPoints& setNeutralCurrentAlarmSetPoint(LONG value); 
    CtiCCTwoWayPoints& setUDPIpAddressId(LONG pointId);
    CtiCCTwoWayPoints& setUDPIpAddress(ULONG value);
    CtiCCTwoWayPoints& setUDPPortNumberId(LONG pointId);
    CtiCCTwoWayPoints& setUDPPortNumber(LONG value);
    CtiCCTwoWayPoints& setTotalOpCountId(LONG pointId);
    CtiCCTwoWayPoints& setTotalOpCount(LONG value);    
    CtiCCTwoWayPoints& setOvCountId(LONG pointId);
    CtiCCTwoWayPoints& setOvCount(LONG value);    
    CtiCCTwoWayPoints& setUvCountId(LONG pointId);
    CtiCCTwoWayPoints& setUvCount(LONG value);  
    CtiCCTwoWayPoints& setOvUvCountResetDate(const CtiTime eventTime);
    CtiCCTwoWayPoints& setLastOvUvDateTime(const CtiTime eventTime);

    CtiCCTwoWayPoints& setLastControlReason(); 

   
    BOOL setTwoWayPointId(int pointtype, int offset, LONG pointId);
    BOOL setTwoWayStatusPointValue(LONG pointID, LONG value);
    BOOL setTwoWayAnalogPointValue(LONG pointID, LONG value);
    BOOL setTwoWayPulseAccumulatorPointValue(LONG pointID, LONG value);

    BOOL isLastControlReasonUpdated(LONG pointID, LONG reason );



    CtiCCTwoWayPoints& addAllCBCPointsToMsg(CtiCommandMsg *pointAddMsg);
    BOOL isDirty();
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);

    void restore(RWDBReader& rdr);
    void setDynamicData(RWDBReader& rdr);
    CtiCCTwoWayPoints* replicate() const;

    //Members inherited from RWCollectable
    //void restoreGuts(RWvistream& );
    //void saveGuts(RWvostream& ) const;

    CtiCCTwoWayPoints& operator=(const CtiCCTwoWayPoints& right);

    int operator==(const CtiCCTwoWayPoints& right) const;
    int operator!=(const CtiCCTwoWayPoints& right) const;

   
    //Possible states
    static int Open;
    static int Closed;   
        
private:

    LONG _paoid;

    LONG _capacitorBankStateId;
    LONG _capacitorBankState;
    LONG _reCloseBlockedId; 
    LONG _reCloseBlocked;   
    LONG _controlModeId;    
    LONG _controlMode;      
    LONG _autoVoltControlId;
    LONG _autoVoltControl; 
    LONG _lastControlLocalId;
    LONG _lastControlLocal; 
    LONG _lastControlRemoteId;
    LONG _lastControlRemote;
    LONG _lastControlOvUvId;
    LONG _lastControlOvUv;  
    LONG _lastControlNeutralFaultId;
    LONG _lastControlNeutralFault; 
    LONG _lastControlScheduledId;
    LONG _lastControlScheduled;
    LONG _lastControlDigitalId;
    LONG _lastControlDigital;
    LONG _lastControlAnalogId;
    LONG _lastControlAnalog;
    LONG _lastControlTemperatureId;
    LONG _lastControlTemperature;
    LONG _ovConditionId;    
    LONG _ovCondition;      
    LONG _uvConditionId;    
    LONG _uvCondition;      
    LONG _opFailedNeutralCurrentId;
    LONG _opFailedNeutralCurrent;
    LONG _neutralCurrentFaultId;
    LONG _neutralCurrentFault;
    LONG _badRelayId;       
    LONG _badRelay;         
    LONG _dailyMaxOpsId;    
    LONG _dailyMaxOps;      
    LONG _voltageDeltaAbnormalId;
    LONG _voltageDeltaAbnormal;
    LONG _tempAlarmId;      
    LONG _tempAlarm;        
    LONG _DSTActiveId;      
    LONG _DSTActive;        
    LONG _neutralLockoutId; 
    LONG _neutralLockout;   
    LONG _ignoredIndicatorId;
    LONG _ignoredIndicator;

    //analog inputs
    LONG _voltageId;
    LONG _voltage;
    LONG _highVoltageId;
    LONG _highVoltage;
    LONG _lowVoltageId;
    LONG _lowVoltage;
    LONG _deltaVoltageId;
    LONG _deltaVoltage;
    LONG _analogInput1Id;
    LONG _analogInput1;
    LONG _temperatureId;
    LONG _temperature;
    LONG _rssiId; 
    LONG _rssi;
    LONG _ignoredReasonId;
    LONG _ignoredReason;

   
    //analog outputs
    LONG _ovSetPointId;
    LONG _ovSetPoint;
    LONG _uvSetPointId;
    LONG _uvSetPoint;
    LONG _ovuvTrackTimeId;
    LONG _ovuvTrackTime;
    LONG _neutralCurrentSensorId;
    LONG _neutralCurrentSensor;
    LONG _neutralCurrentAlarmSetPointId;
    LONG _neutralCurrentAlarmSetPoint;
    LONG _udpIpAddressId;
    ULONG _udpIpAddress;
    LONG _udpPortNumberId;
    LONG _udpPortNumber;

     
    LONG _totalOpCountId;
    LONG _totalOpCount;
    LONG _ovCountId;
    LONG _ovCount;
    LONG _uvCountId;
    LONG _uvCount;

    CtiTime _ovuvCountResetDate;
    CtiTime _lastOvUvDateTime;
    INT _lastControlReason;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

};

typedef CtiCCTwoWayPoints* CtiCCTwoWayPointsPtr;
#endif
