/*---------------------------------------------------------------------------
        Filename:  ccfeeder.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCFeeder
                        CtiCCFeeder maintains the state and handles
                        the persistence of strategies for Cap Control.                             

        Initial Date:  8/27/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCFEEDERIMPL_H
#define CTICCFEEDERIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 
#include <rw/sortvec.h> 

#include "connection.h"
#include "types.h"
#include "observe.h"
#include "cccapbank.h"
#include "msg_pcrequest.h"

template<class T>
struct FeederVARComparison
{
	bool operator()(const T& x, const T& y) const
	{
        bool returnBoolean = false;

        // if the feeders are in different var categories,
        // we don't need the offset to determine which feeder should be first
		if( x.getBusOptimizedVarCategory() != y.getBusOptimizedVarCategory() )
        {
            returnBoolean = x.getBusOptimizedVarCategory() > y.getBusOptimizedVarCategory();
        }
        // if the feeders are in the same var categories,
        // we need to figure out which has the lower var offset
        else
        {
            returnBoolean = x.getBusOptimizedVarOffset() > y.getBusOptimizedVarOffset();
        }

        return returnBoolean;
	}
};

class CtiCCFeeder : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCFeeder )

    CtiCCFeeder();
    CtiCCFeeder(RWDBReader& rdr);
    CtiCCFeeder(const CtiCCFeeder& feeder);

    virtual ~CtiCCFeeder();

    ULONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    const RWCString& getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    DOUBLE getPeakSetPoint() const;
    DOUBLE getOffPeakSetPoint() const;
    DOUBLE getUpperBandwidth() const;
    ULONG getCurrentVarLoadPointId() const;
    DOUBLE getCurrentVarLoadPointValue() const;
    ULONG getCurrentWattLoadPointId() const;
    DOUBLE getCurrentWattLoadPointValue() const;
    ULONG getMapLocationId() const;
    DOUBLE getLowerBandwidth() const;
    ULONG getDisplayOrder() const;
    BOOL getNewPointDataReceivedFlag() const;
    const RWDBDateTime& getLastCurrentVarPointUpdateTime() const;
    ULONG getEstimatedVarLoadPointId() const;
    DOUBLE getEstimatedVarLoadPointValue() const;
    BOOL getStatusesReceivedFlag() const;
    ULONG getDailyOperationsAnalogPointId() const;
    ULONG getCurrentDailyOperations() const;
    BOOL getRecentlyControlledFlag() const;
    const RWDBDateTime& getLastOperationTime() const;
    DOUBLE getVarValueBeforeControl() const;
    ULONG getLastCapBankControlledDeviceId() const;
    ULONG getBusOptimizedVarCategory() const;
    DOUBLE getBusOptimizedVarOffset() const;
    DOUBLE getPowerFactorValue() const;
    DOUBLE getKVARSolution() const;
    
    RWSortedVector& getCCCapBanks();

    CtiCCFeeder& setPAOId(ULONG id);
    CtiCCFeeder& setPAOCategory(const RWCString& category);
    CtiCCFeeder& setPAOClass(const RWCString& pclass);
    CtiCCFeeder& setPAOName(const RWCString& name);
    CtiCCFeeder& setPAOType(const RWCString& type);
    CtiCCFeeder& setPAODescription(const RWCString& description);
    CtiCCFeeder& setDisableFlag(BOOL disable);
    CtiCCFeeder& setPeakSetPoint(DOUBLE peak);
    CtiCCFeeder& setOffPeakSetPoint(DOUBLE offpeak);
    CtiCCFeeder& setUpperBandwidth(DOUBLE bandwidth);
    CtiCCFeeder& setCurrentVarLoadPointId(ULONG currentvarid);
    CtiCCFeeder& setCurrentVarLoadPointValue(DOUBLE currentvarval);
    CtiCCFeeder& setCurrentWattLoadPointId(ULONG currentwattid);
    CtiCCFeeder& setCurrentWattLoadPointValue(DOUBLE currentwattval);
    CtiCCFeeder& setMapLocationId(ULONG maplocation);
    CtiCCFeeder& setLowerBandwidth(DOUBLE bandwidth);
    CtiCCFeeder& setDisplayOrder(ULONG order);
    CtiCCFeeder& setNewPointDataReceivedFlag(BOOL newpointdatareceived);
    CtiCCFeeder& setLastCurrentVarPointUpdateTime(const RWDBDateTime& lastpointupdate);
    CtiCCFeeder& setEstimatedVarLoadPointId(ULONG estimatedvarid);
    CtiCCFeeder& setEstimatedVarLoadPointValue(DOUBLE estimatedvarval);
    CtiCCFeeder& setStatusesReceivedFlag(BOOL statusesreceived);
    CtiCCFeeder& setDailyOperationsAnalogPointId(ULONG opspointid);
    CtiCCFeeder& setCurrentDailyOperations(ULONG operations);
    CtiCCFeeder& setRecentlyControlledFlag(BOOL recentlycontrolled);
    CtiCCFeeder& setLastOperationTime(const RWDBDateTime& lastoperation);
    CtiCCFeeder& setVarValueBeforeControl(DOUBLE oldvarval);
    CtiCCFeeder& setLastCapBankControlledDeviceId(ULONG lastcapbank);
    //don't think we want public setters for these
    //CtiCCFeeder& setBusOptimizedVarCategory(ULONG varcategory);
    //CtiCCFeeder& setBusOptimizedVarOffset(DOUBLE varoffset);
    CtiCCFeeder& setPowerFactorValue(DOUBLE pfval);
    CtiCCFeeder& setKVARSolution(DOUBLE solution);


    CtiCCCapBank* findCapBankToChangeVars(DOUBLE kvarSolution);
    CtiRequestMsg* createIncreaseVarRequest(CtiCCCapBank* capBank, RWOrdered& pointChanges, DOUBLE currentVarLoadPointValue, ULONG decimalPlaces);
    CtiRequestMsg* createDecreaseVarRequest(CtiCCCapBank* capBank, RWOrdered& pointChanges, DOUBLE currentVarLoadPointValue, ULONG decimalPlaces);
    BOOL capBankControlStatusUpdate(RWOrdered& pointChanges, ULONG minConfirmPercent, ULONG failurePercent, DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue);
    //BOOL isPeakDay();
    BOOL isPastResponseTime(const RWDBDateTime& currentDateTime, ULONG minResponseTime);
    BOOL checkForAndProvideNeededIndividualControl(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages, BOOL peakTimeFlag, ULONG decimalPlaces, const RWCString& controlUnits);
    CtiCCFeeder& figureEstimatedVarLoadPointValue();
    BOOL isAlreadyControlled(ULONG minConfirmPercent);
    BOOL areAllCapBankStatusesReceived();
    void fillOutBusOptimizedInfo(BOOL peakTimeFlag);
    void figureKVARSolution(const RWCString& controlUnits, DOUBLE setPoint);
    void dumpDynamicData();

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCFeeder& operator=(const CtiCCFeeder& right);

    int operator==(const CtiCCFeeder& right) const;
    int operator!=(const CtiCCFeeder& right) const;

    CtiCCFeeder* replicate() const;

    //Possible states
    /*static const RWCString Enabled;
    static const RWCString Disabled;*/

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
    DOUBLE _peaksetpoint;
    DOUBLE _offpeaksetpoint;
    DOUBLE _upperbandwidth;
    ULONG _currentvarloadpointid;
    DOUBLE _currentvarloadpointvalue;
    ULONG _currentwattloadpointid;
    DOUBLE _currentwattloadpointvalue;
    ULONG _maplocationid;
    DOUBLE _lowerbandwidth;
    ULONG _displayorder;
    BOOL _newpointdatareceivedflag;
    RWDBDateTime _lastcurrentvarpointupdatetime;
    ULONG _estimatedvarloadpointid;
    DOUBLE _estimatedvarloadpointvalue;
    BOOL _statusesreceivedflag;
    ULONG _dailyoperationsanalogpointid;
    ULONG _currentdailyoperations;
    BOOL _recentlycontrolledflag;
    RWDBDateTime _lastoperationtime;
    DOUBLE _varvaluebeforecontrol;
    ULONG _lastcapbankcontrolleddeviceid;
    ULONG _busoptimizedvarcategory;
    DOUBLE _busoptimizedvaroffset;
    DOUBLE _powerfactorvalue;
    DOUBLE _kvarsolution;

    RWSortedVector _cccapbanks;

    //don't stream
    BOOL _insertDynamicDataFlag;

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    void restore(RWDBReader& rdr);
    RWCString doubleToString(DOUBLE doubleVal);
};
#endif
