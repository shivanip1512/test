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

#include "dbaccess.h"
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

    LONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    const RWCString& getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    DOUBLE getPeakSetPoint() const;
    DOUBLE getOffPeakSetPoint() const;
    DOUBLE getUpperBandwidth() const;
    LONG getCurrentVarLoadPointId() const;
    DOUBLE getCurrentVarLoadPointValue() const;
    LONG getCurrentWattLoadPointId() const;
    DOUBLE getCurrentWattLoadPointValue() const;
    LONG getMapLocationId() const;
    DOUBLE getLowerBandwidth() const;
    LONG getDisplayOrder() const;
    BOOL getNewPointDataReceivedFlag() const;
    const RWDBDateTime& getLastCurrentVarPointUpdateTime() const;
    LONG getEstimatedVarLoadPointId() const;
    DOUBLE getEstimatedVarLoadPointValue() const;
    LONG getDailyOperationsAnalogPointId() const;
    LONG getPowerFactorPointId() const;
    LONG getEstimatedPowerFactorPointId() const;
    LONG getCurrentDailyOperations() const;
    BOOL getRecentlyControlledFlag() const;
    const RWDBDateTime& getLastOperationTime() const;
    DOUBLE getVarValueBeforeControl() const;
    LONG getLastCapBankControlledDeviceId() const;
    LONG getBusOptimizedVarCategory() const;
    DOUBLE getBusOptimizedVarOffset() const;
    DOUBLE getPowerFactorValue() const;
    DOUBLE getKVARSolution() const;
    DOUBLE getEstimatedPowerFactorValue() const;
    LONG getCurrentVarPointQuality() const;
    BOOL getWaiveControlFlag() const;

    RWSortedVector& getCCCapBanks();

    CtiCCFeeder& setPAOId(LONG id);
    CtiCCFeeder& setPAOCategory(const RWCString& category);
    CtiCCFeeder& setPAOClass(const RWCString& pclass);
    CtiCCFeeder& setPAOName(const RWCString& name);
    CtiCCFeeder& setPAOType(const RWCString& type);
    CtiCCFeeder& setPAODescription(const RWCString& description);
    CtiCCFeeder& setDisableFlag(BOOL disable);
    CtiCCFeeder& setPeakSetPoint(DOUBLE peak);
    CtiCCFeeder& setOffPeakSetPoint(DOUBLE offpeak);
    CtiCCFeeder& setUpperBandwidth(DOUBLE bandwidth);
    CtiCCFeeder& setCurrentVarLoadPointId(LONG currentvarid);
    CtiCCFeeder& setCurrentVarLoadPointValue(DOUBLE currentvarval);
    CtiCCFeeder& setCurrentWattLoadPointId(LONG currentwattid);
    CtiCCFeeder& setCurrentWattLoadPointValue(DOUBLE currentwattval);
    CtiCCFeeder& setMapLocationId(LONG maplocation);
    CtiCCFeeder& setLowerBandwidth(DOUBLE bandwidth);
    CtiCCFeeder& setDisplayOrder(LONG order);
    CtiCCFeeder& setNewPointDataReceivedFlag(BOOL newpointdatareceived);
    CtiCCFeeder& setLastCurrentVarPointUpdateTime(const RWDBDateTime& lastpointupdate);
    CtiCCFeeder& setEstimatedVarLoadPointId(LONG estimatedvarid);
    CtiCCFeeder& setEstimatedVarLoadPointValue(DOUBLE estimatedvarval);
    CtiCCFeeder& setDailyOperationsAnalogPointId(LONG opspointid);
    CtiCCFeeder& setPowerFactorPointId(LONG pfpointid);
    CtiCCFeeder& setEstimatedPowerFactorPointId(LONG epfpointid);
    CtiCCFeeder& setCurrentDailyOperations(LONG operations);
    CtiCCFeeder& setRecentlyControlledFlag(BOOL recentlycontrolled);
    CtiCCFeeder& setLastOperationTime(const RWDBDateTime& lastoperation);
    CtiCCFeeder& setVarValueBeforeControl(DOUBLE oldvarval);
    CtiCCFeeder& setLastCapBankControlledDeviceId(LONG lastcapbank);
    //don't think we want public setters for these
    //CtiCCFeeder& setBusOptimizedVarCategory(LONG varcategory);
    //CtiCCFeeder& setBusOptimizedVarOffset(DOUBLE varoffset);
    CtiCCFeeder& setPowerFactorValue(DOUBLE pfval);
    CtiCCFeeder& setKVARSolution(DOUBLE solution);
    CtiCCFeeder& setEstimatedPowerFactorValue(DOUBLE epfval);
    CtiCCFeeder& setCurrentVarPointQuality(LONG cvpq);
    CtiCCFeeder& setWaiveControlFlag(BOOL waive);

    CtiCCCapBank* findCapBankToChangeVars(DOUBLE kvarSolution);
    CtiRequestMsg* createIncreaseVarRequest(CtiCCCapBank* capBank, RWOrdered& pointChanges, DOUBLE currentVarLoadPointValue, LONG decimalPlaces);
    CtiRequestMsg* createDecreaseVarRequest(CtiCCCapBank* capBank, RWOrdered& pointChanges, DOUBLE currentVarLoadPointValue, LONG decimalPlaces);
    BOOL capBankControlStatusUpdate(RWOrdered& pointChanges, LONG minConfirmPercent, LONG failurePercent, DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue, LONG currentVarPointQuality);
    //BOOL isPeakDay();
    BOOL isPastMaxConfirmTime(const RWDBDateTime& currentDateTime, LONG maxConfirmTime, LONG subBusRetries);
    BOOL checkForAndProvideNeededIndividualControl(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages, BOOL peakTimeFlag, LONG decimalPlaces, const RWCString& controlUnits);
    CtiCCFeeder& figureEstimatedVarLoadPointValue();
    BOOL isAlreadyControlled(LONG minConfirmPercent);
    void fillOutBusOptimizedInfo(BOOL peakTimeFlag);
    BOOL attemptToResendControl(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages, LONG maxConfirmTime);

    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);

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

    LONG _paoid;
    RWCString _paocategory;
    RWCString _paoclass;
    RWCString _paoname;
    RWCString _paotype;
    RWCString _paodescription;
    BOOL _disableflag;
    DOUBLE _peaksetpoint;
    DOUBLE _offpeaksetpoint;
    DOUBLE _upperbandwidth;
    LONG _currentvarloadpointid;
    DOUBLE _currentvarloadpointvalue;
    LONG _currentwattloadpointid;
    DOUBLE _currentwattloadpointvalue;
    LONG _maplocationid;
    DOUBLE _lowerbandwidth;
    LONG _displayorder;
    BOOL _newpointdatareceivedflag;
    RWDBDateTime _lastcurrentvarpointupdatetime;
    LONG _estimatedvarloadpointid;
    DOUBLE _estimatedvarloadpointvalue;
    LONG _dailyoperationsanalogpointid;
    LONG _powerfactorpointid;
    LONG _estimatedpowerfactorpointid;
    LONG _currentdailyoperations;
    BOOL _recentlycontrolledflag;
    RWDBDateTime _lastoperationtime;
    DOUBLE _varvaluebeforecontrol;
    LONG _lastcapbankcontrolleddeviceid;
    LONG _busoptimizedvarcategory;
    DOUBLE _busoptimizedvaroffset;
    DOUBLE _powerfactorvalue;
    DOUBLE _kvarsolution;
    DOUBLE _estimatedpowerfactorvalue;
    LONG _currentvarpointquality;
    BOOL _waivecontrolflag;

    RWSortedVector _cccapbanks;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);
    RWCString doubleToString(DOUBLE doubleVal);
};
#endif
