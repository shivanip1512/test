/*---------------------------------------------------------------------------
        Filename:  strategy.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCStrategy
                        CtiCCStrategy maintains the state and handles
                        the persistence of strategies for Cap Control.                             

        Initial Date:  8/18/00
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2000
---------------------------------------------------------------------------*/

#ifndef CTICCSTRATEGYIMPL_H
#define CTICCSTRATEGYIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "connection.h"
#include "types.h"
#include "observe.h"
#include "capbank.h"
#include "msg_pcrequest.h"

class CtiCCStrategy : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCStrategy )

    CtiCCStrategy();
    CtiCCStrategy(RWDBReader& rdr);
    CtiCCStrategy(const CtiCCStrategy& strat);

    virtual ~CtiCCStrategy();

    LONG Id() const;
    const RWCString& Name() const;
    const RWCString& District() const;
    LONG ActualVarPointId() const;
    DOUBLE ActualVarPointValue() const;
    LONG MaxDailyOperation() const;
    DOUBLE PeakSetPoint() const;
    DOUBLE OffPeakSetPoint() const;
    const RWDBDateTime& PeakStartTime() const;
    const RWDBDateTime& PeakStopTime() const;
    LONG CalculatedVarPointId() const;
    DOUBLE CalculatedVarPointValue() const;
    DOUBLE Bandwidth() const;
    LONG ControlInterval() const;
    LONG MinResponseTime() const;
    LONG MinConfirmPercent() const;
    LONG FailurePercent() const;
    const RWDBDateTime& NextCheckTime() const;
    BOOL NewPointDataReceived() const;
    RWOrdered& CapBankList();
    const RWCString& Status() const;
    LONG Operations() const;
    const RWDBDateTime& LastOperation() const;
    LONG LastCapBankControlled() const;
    const RWCString& DaysOfWeek() const;
    LONG PeakOrOffPeak() const;
    BOOL RecentlyControlled() const;
    DOUBLE CalculatedValueBeforeControl() const;
    BOOL StrategyUpdated() const;
    const RWDBDateTime& LastPointUpdate() const;
    LONG DecimalPlaces() const;
    BOOL StatusesReceivedFlag() const;

    CtiCCStrategy& setId(LONG id);
    CtiCCStrategy& setName(const RWCString& name);
    CtiCCStrategy& setDistrict(const RWCString& district);
    CtiCCStrategy& setActualVarPointId(LONG actualid);
    CtiCCStrategy& setActualVarPointValue(DOUBLE actualval);
    CtiCCStrategy& setMaxDailyOperation(LONG max);
    CtiCCStrategy& setPeakSetPoint(DOUBLE peak);
    CtiCCStrategy& setOffPeakSetPoint(DOUBLE offpeak);
    CtiCCStrategy& setPeakStartTime(const RWDBDateTime& start);
    CtiCCStrategy& setPeakStopTime(const RWDBDateTime& stop);
    CtiCCStrategy& setCalculatedVarPointId(LONG calculatedid);
    CtiCCStrategy& setCalculatedVarPointValue(DOUBLE calculatedval);
    CtiCCStrategy& setBandwidth(DOUBLE bandwidth);
    CtiCCStrategy& setControlInterval(LONG interval);
    CtiCCStrategy& setMinResponseTime(LONG response);
    CtiCCStrategy& setMinConfirmPercent(LONG confirm);
    CtiCCStrategy& setFailurePercent(LONG failure);
    CtiCCStrategy& figureNextCheckTime();
    CtiCCStrategy& setNewPointDataReceived(BOOL received);
    CtiCCStrategy& insertCapBank(CtiCapBank* capbank);
    CtiCCStrategy& setStatus(const RWCString& status);
    CtiCCStrategy& setOperations(LONG operations);
    CtiCCStrategy& setLastOperation(const RWDBDateTime& lastoperation);
    CtiCCStrategy& setLastCapBankControlled(LONG lastcapbank);
    CtiCCStrategy& setDaysOfWeek(const RWCString& days);
    CtiCCStrategy& setPeakOrOffPeak(LONG lastcapbank);
    CtiCCStrategy& setRecentlyControlled(BOOL controlled);
    CtiCCStrategy& setCalculatedValueBeforeControl(DOUBLE oldcalcval);
    CtiCCStrategy& setStrategyUpdated(BOOL updated);
    CtiCCStrategy& setLastPointUpdate(const RWDBDateTime& lastpointupdate);
    CtiCCStrategy& setDecimalPlaces(LONG lastcapbank);
    CtiCCStrategy& setStatusesReceivedFlag(BOOL statusesreceived);

    CtiRequestMsg* createIncreaseVarRequest(RWOrdered& pointChanges);
    CtiRequestMsg* createDecreaseVarRequest(RWOrdered& pointChanges);
    CtiCCStrategy& figureActualVarPointValue();
    BOOL capBankControlStatusUpdate(RWOrdered& pointChanges);
    DOUBLE figureCurrentSetPoint(unsigned nowInSeconds);
    BOOL isPeakDay();
    BOOL areAllCapBankStatusesReceived();
    void restoreDynamicData(RWDBReader& rdr);
    void dumpDynamicData();
    BOOL isAlreadyControlled();

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCStrategy& operator=(const CtiCCStrategy& right);

    int operator==(const CtiCCStrategy& right) const;
    int operator!=(const CtiCCStrategy& right) const;

    CtiCCStrategy* replicate() const;

    //Possible states
    static const RWCString Enabled;
    static const RWCString Disabled;

    static int PeakState;
    static int OffPeakState;

private:
    
    LONG _id;
    RWCString _name;
    RWCString _district;
    LONG _actualid;
    DOUBLE _actualval;
    LONG _max;
    DOUBLE _peak;
    DOUBLE _offpeak;
    RWDBDateTime _start;
    RWDBDateTime _stop;
    LONG _calculatedid;
    DOUBLE _calculatedval;
    DOUBLE _bandwidth;
    LONG _interval;
    LONG _response;
    LONG _confirm;
    LONG _failure;
    RWDBDateTime _nextcheck;
    BOOL _newpointdatareceived;
    RWCString _status;
    LONG _operations;
    RWDBDateTime _lastoperation;
    LONG _lastcapbank;
    RWCString _daysofweek;
    LONG _peakoroffpeak;
    BOOL _recentlycontrolled;
    DOUBLE _calculatedvaluebeforecontrol;
    BOOL _strategyupdated;
    RWDBDateTime _lastpointupdate;
    LONG _decimalplaces;
    BOOL _statusesreceivedflag;

    RWOrdered _capbanks;

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    void restore(RWDBReader& rdr);
    RWCString doubleToString(DOUBLE doubleVal);
};
#endif
