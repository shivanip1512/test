
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
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h>
#include <list>


#include "msg_cmd.h"
#include "msg_ptreg.h"
#include "dbaccess.h"
#include "observe.h"
#include "types.h"
#include "AttributeService.h"
#include "PointValueHolder.h"
#include "LitePoint.h"

namespace Cti {
namespace Database {
    class DatabaseConnection;
}
}

class CtiCCTwoWayPoints
{

public:

  //RWDECLARE_COLLECTABLE( CtiCCTwoWayPoints )

    CtiCCTwoWayPoints(long paoid);
    CtiCCTwoWayPoints(Cti::RowReader& rdr);
    CtiCCTwoWayPoints(const CtiCCTwoWayPoints& cap);

    virtual ~CtiCCTwoWayPoints();

    LONG getPAOId() const;

    string getLastControlText() ;
    LONG getLastControl() ;
    INT getLastControlReason() const;


    CtiCCTwoWayPoints& setPAOId(LONG paoId);

    CtiCCTwoWayPoints& setLastControlReason();


    LitePoint getPointByAttribute(const PointAttribute & attribute) const;
    double getPointValueByAttribute(PointAttribute pointAttribute);
    CtiTime getPointTimeStampByAttribute(PointAttribute attribute);


    BOOL setTwoWayPointId(CtiPointType_t pointtype, int offset, LONG pointId);
    BOOL setTwoWayStatusPointValue(LONG pointID, LONG value, CtiTime timestamp);
    BOOL setTwoWayAnalogPointValue(LONG pointID, LONG value, CtiTime timestamp);
    BOOL setTwoWayPulseAccumulatorPointValue(LONG pointID, LONG value, CtiTime timestamp);

    BOOL isLastControlReasonUpdated(LONG pointID, LONG reason );


    CtiCCTwoWayPoints& addAllCBCPointsToRegMsg(std::set<long>& pointList);
    BOOL isDirty();
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    void restore(Cti::RowReader& rdr);
    void setDynamicData(Cti::RowReader& rdr);
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

    typedef std::map<PointAttribute, LitePoint> AttributePoint;
    AttributePoint    _attributes;
    PointValueHolder  _pointValues;

    map <int, PointAttribute> _statusOffsetAttribute;
    map <int, PointAttribute> _analogOffsetAttribute;
    map <int, PointAttribute> _accumulatorOffsetAttribute;
    map <int, CtiPointType_t> _pointidPointtypeMap;

    PointAttribute getAttribute(int pointtype, int offset);
    PointAttribute getAnalogAttribute(int offset);
    PointAttribute getAccumulatorAttribute(int offset);
    PointAttribute getStatusAttribute(int offset);

    LONG _paoid;
/*
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
    long _voltageControlId;
    long _voltageControl;
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
    long _timeTempSeasonOneId;
    long _timeTempSeasonOne;
    long _timeTempSeasonTwoId;
    long _timeTempSeasonTwo;
    long _varControlId;
    long _varControl;
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
*/
    CtiTime _ovuvCountResetDate;
    CtiTime _lastOvUvDateTime;
    INT _lastControlReason;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

};

typedef CtiCCTwoWayPoints* CtiCCTwoWayPointsPtr;
#endif
