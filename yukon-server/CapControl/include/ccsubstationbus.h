/*---------------------------------------------------------------------------
        Filename:  ccsubstationbus.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCSubstationBus
                        CtiCCSubstationBus maintains the state and handles
                        the persistence of strategies for Cap Control.                             

        Initial Date:  8/27/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCSUBSTATIONBUSIMPL_H
#define CTICCSUBSTATIONBUSIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "connection.h"
#include "types.h"
#include "observe.h"
#include "ccfeeder.h"
#include "cccapbank.h"
#include "msg_pcrequest.h"

class CtiCCSubstationBus : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCSubstationBus )

    CtiCCSubstationBus();
    CtiCCSubstationBus(RWDBReader& rdr);
    CtiCCSubstationBus(const CtiCCSubstationBus& bus);

    virtual ~CtiCCSubstationBus();

    ULONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    const RWCString& getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    const RWCString& getControlMethod() const;
    ULONG getMaxDailyOperation() const;
    BOOL getMaxOperationDisableFlag() const;
    DOUBLE getPeakSetPoint() const;
    DOUBLE getOffPeakSetPoint() const;
    ULONG getPeakStartTime() const;
    ULONG getPeakStopTime() const;
    ULONG getCurrentVarLoadPointId() const;
    DOUBLE getCurrentVarLoadPointValue() const;
    ULONG getCurrentWattLoadPointId() const;
    DOUBLE getCurrentWattLoadPointValue() const;
    DOUBLE getUpperBandwidth() const;
    ULONG getControlInterval() const;
    ULONG getMinResponseTime() const;
    ULONG getMinConfirmPercent() const;
    ULONG getFailurePercent() const;
    const RWCString& getDaysOfWeek() const;
    ULONG getMapLocationId() const;
    DOUBLE getLowerBandwidth() const;
    const RWCString& getControlUnits() const;
    ULONG getDecimalPlaces() const;
    const RWDBDateTime& getNextCheckTime() const;
    BOOL getNewPointDataReceivedFlag() const;
    BOOL getBusUpdatedFlag() const;
    const RWDBDateTime& getLastCurrentVarPointUpdateTime() const;
    ULONG getEstimatedVarLoadPointId() const;
    DOUBLE getEstimatedVarLoadPointValue() const;
    ULONG getDailyOperationsAnalogPointId() const;
    ULONG getCurrentDailyOperations() const;
    BOOL getPeakTimeFlag() const;
    BOOL getRecentlyControlledFlag() const;
    const RWDBDateTime& getLastOperationTime() const;
    DOUBLE getVarValueBeforeControl() const;
    ULONG getLastFeederControlledPAOId() const;
    LONG getLastFeederControlledPosition() const;
    DOUBLE getPowerFactorValue() const;
    DOUBLE getKVARSolution() const;
    
    RWOrdered& getCCFeeders();

    CtiCCSubstationBus& setPAOId(ULONG id);
    CtiCCSubstationBus& setPAOCategory(const RWCString& category);
    CtiCCSubstationBus& setPAOClass(const RWCString& pclass);
    CtiCCSubstationBus& setPAOName(const RWCString& name);
    CtiCCSubstationBus& setPAOType(const RWCString& type);
    CtiCCSubstationBus& setPAODescription(const RWCString& description);
    CtiCCSubstationBus& setDisableFlag(BOOL disable);
    CtiCCSubstationBus& setControlMethod(const RWCString& method);
    CtiCCSubstationBus& setMaxDailyOperation(ULONG max);
    CtiCCSubstationBus& setMaxOperationDisableFlag(BOOL maxopdisable);
    CtiCCSubstationBus& setPeakSetPoint(DOUBLE peak);
    CtiCCSubstationBus& setOffPeakSetPoint(DOUBLE offpeak);
    CtiCCSubstationBus& setPeakStartTime(ULONG starttime);
    CtiCCSubstationBus& setPeakStopTime(ULONG stoptime);
    CtiCCSubstationBus& setCurrentVarLoadPointId(ULONG currentvarid);
    CtiCCSubstationBus& setCurrentVarLoadPointValue(DOUBLE currentvarval);
    CtiCCSubstationBus& setCurrentWattLoadPointId(ULONG currentwattid);
    CtiCCSubstationBus& setCurrentWattLoadPointValue(DOUBLE currentwattval);
    CtiCCSubstationBus& setUpperBandwidth(DOUBLE bandwidth);
    CtiCCSubstationBus& setControlInterval(ULONG interval);
    CtiCCSubstationBus& setMinResponseTime(ULONG response);
    CtiCCSubstationBus& setMinConfirmPercent(ULONG confirm);
    CtiCCSubstationBus& setFailurePercent(ULONG failure);
    CtiCCSubstationBus& setDaysOfWeek(const RWCString& days);
    CtiCCSubstationBus& setMapLocationId(ULONG maplocation);
    CtiCCSubstationBus& setLowerBandwidth(DOUBLE bandwidth);
    CtiCCSubstationBus& setControlUnits(const RWCString& contunit);
    CtiCCSubstationBus& setDecimalPlaces(ULONG places);
    CtiCCSubstationBus& figureNextCheckTime();
    CtiCCSubstationBus& setNewPointDataReceivedFlag(BOOL newpointdatareceived);
    CtiCCSubstationBus& setBusUpdatedFlag(BOOL busupdated);
    CtiCCSubstationBus& setLastCurrentVarPointUpdateTime(const RWDBDateTime& lastpointupdate);
    CtiCCSubstationBus& setEstimatedVarLoadPointId(ULONG estimatedvarid);
    CtiCCSubstationBus& setEstimatedVarLoadPointValue(DOUBLE estimatedvarval);
    CtiCCSubstationBus& setDailyOperationsAnalogPointId(ULONG opanalogpointid);
    CtiCCSubstationBus& setCurrentDailyOperations(ULONG operations);
    CtiCCSubstationBus& setPeakTimeFlag(ULONG peaktime);
    CtiCCSubstationBus& setRecentlyControlledFlag(BOOL recentlycontrolled);
    CtiCCSubstationBus& setLastOperationTime(const RWDBDateTime& lastoperation);
    CtiCCSubstationBus& setVarValueBeforeControl(DOUBLE oldvarval);
    CtiCCSubstationBus& setLastFeederControlledPAOId(ULONG lastfeederpao);
    CtiCCSubstationBus& setLastFeederControlledPosition(LONG lastfeederposition);
    CtiCCSubstationBus& setPowerFactorValue(DOUBLE pfval);
    CtiCCSubstationBus& setKVARSolution(DOUBLE solution);

    BOOL isPastResponseTime(const RWDBDateTime& currentDateTime);
    BOOL isVarCheckNeeded(const RWDBDateTime& currentDateTime);
    BOOL capBankControlStatusUpdate(RWOrdered& pointChanges);
    DOUBLE figureCurrentSetPoint(const RWDBDateTime& currentDateTime);
    BOOL isPeakDay();
    void clearOutNewPointReceivedFlags();
    CtiCCSubstationBus& checkForAndProvideNeededControl(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    void regularSubstationBusControl(DOUBLE setpoint, const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    void optimizedSubstationBusControl(DOUBLE setpoint, const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    CtiCCSubstationBus& figureEstimatedVarLoadPointValue();
    BOOL isAlreadyControlled();
    DOUBLE calculatePowerFactor(DOUBLE kvar, DOUBLE kw);
    DOUBLE convertKQToKVAR(DOUBLE kq, DOUBLE kw);
    DOUBLE convertKVARToKQ(DOUBLE kvar, DOUBLE kw);
    static DOUBLE calculateKVARSolution(const RWCString& controlUnits, DOUBLE setPoint, DOUBLE varValue, DOUBLE wattValue);
    void dumpDynamicData();

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCSubstationBus& operator=(const CtiCCSubstationBus& right);

    int operator==(const CtiCCSubstationBus& right) const;
    int operator!=(const CtiCCSubstationBus& right) const;

    CtiCCSubstationBus* replicate() const;

    //Possible control methods
    static const RWCString IndividualFeederControlMethod;
    static const RWCString SubstationBusControlMethod;
    static const RWCString BusOptimizedFeederControlMethod;

    static const RWCString KVARControlUnits;
    static const RWCString PF_BY_KVARControlUnits;
    static const RWCString PF_BY_KQControlUnits;
    //static int PeakState;
    //static int OffPeakState;

private:

    ULONG _paoid;
    RWCString _paocategory;
    RWCString _paoclass;
    RWCString _paoname;
    RWCString _paotype;
    RWCString _paodescription;
    BOOL _disableflag;
    RWCString _controlmethod;
    ULONG _maxdailyoperation;
    BOOL _maxoperationdisableflag;
    DOUBLE _peaksetpoint;
    DOUBLE _offpeaksetpoint;
    ULONG _peakstarttime;
    ULONG _peakstoptime;
    ULONG _currentvarloadpointid;
    DOUBLE _currentvarloadpointvalue;
    ULONG _currentwattloadpointid;
    DOUBLE _currentwattloadpointvalue;
    DOUBLE _upperbandwidth;
    ULONG _controlinterval;
    ULONG _minresponsetime;
    ULONG _minconfirmpercent;
    ULONG _failurepercent;
    RWCString _daysofweek;
    ULONG _maplocationid;
    DOUBLE _lowerbandwidth;
    RWCString _controlunits;
    ULONG _decimalplaces;
    RWDBDateTime _nextchecktime;
    BOOL _newpointdatareceivedflag;
    BOOL _busupdatedflag;
    RWDBDateTime _lastcurrentvarpointupdatetime;
    ULONG _estimatedvarloadpointid;
    DOUBLE _estimatedvarloadpointvalue;
    ULONG _dailyoperationsanalogpointid;
    ULONG _currentdailyoperations;
    BOOL _peaktimeflag;
    BOOL _recentlycontrolledflag;
    RWDBDateTime _lastoperationtime;
    DOUBLE _varvaluebeforecontrol;
    ULONG _lastfeedercontrolledpaoid;
    LONG _lastfeedercontrolledposition;
    DOUBLE _powerfactorvalue;
    DOUBLE _kvarsolution;

    RWOrdered _ccfeeders;

    //don't stream
    BOOL _insertDynamicDataFlag;

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    void restore(RWDBReader& rdr);
    RWCString doubleToString(DOUBLE doubleVal);
};
#endif
