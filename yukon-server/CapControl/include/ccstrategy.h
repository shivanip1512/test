
/*---------------------------------------------------------------------------
        Filename:  ccstrategy.h
        
        Programmer:  Julie Richter
        
        Description:    Header file for CtiCCStrategy
                        CtiCCStrategy maintains the state and handles
                        the persistence of strategies for Cap Control.                             

        Initial Date:  8/27/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCSTRATEGY_H
#define CTICCSTRATEGY_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "dbaccess.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "msg_pcrequest.h"

class CtiCCStrategy : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCStrategy )

    CtiCCStrategy();
    CtiCCStrategy(RWDBReader& rdr);
    CtiCCStrategy(const CtiCCStrategy& strategy);

    virtual ~CtiCCStrategy();

    LONG getStrategyId() const;
    const RWCString& getStrategyName() const;
    const RWCString& getControlMethod() const;
    LONG getMaxDailyOperation() const;
    BOOL getMaxOperationDisableFlag() const;
    DOUBLE getPeakLag() const;
    DOUBLE getOffPeakLag() const;
    DOUBLE getPeakLead() const;
    DOUBLE getOffPeakLead() const;
    LONG getPeakStartTime() const;
    LONG getPeakStopTime() const;
    LONG getControlInterval() const;
    LONG getMaxConfirmTime() const;
    LONG getMinConfirmPercent() const;
    LONG getFailurePercent() const;
    const RWCString& getDaysOfWeek() const;
    const RWCString& getControlUnits() const;
    LONG getControlDelayTime() const;
    LONG getControlSendRetries() const;

    CtiCCStrategy& setStrategyId(LONG id);
    CtiCCStrategy& setStrategyName(const RWCString& strategyname);
    CtiCCStrategy& setControlMethod(const RWCString& method);
    CtiCCStrategy& setMaxDailyOperation(LONG max);
    CtiCCStrategy& setMaxOperationDisableFlag(BOOL maxopdisable);
    CtiCCStrategy& setPeakLag(DOUBLE peak);
    CtiCCStrategy& setOffPeakLag(DOUBLE offpeak);
    CtiCCStrategy& setPeakLead(DOUBLE peak);
    CtiCCStrategy& setOffPeakLead(DOUBLE offpeak);
    CtiCCStrategy& setPeakStartTime(LONG starttime);
    CtiCCStrategy& setPeakStopTime(LONG stoptime);
    CtiCCStrategy& setControlInterval(LONG interval);
    CtiCCStrategy& setMaxConfirmTime(LONG confirm);
    CtiCCStrategy& setMinConfirmPercent(LONG confirm);
    CtiCCStrategy& setFailurePercent(LONG failure);
    CtiCCStrategy& setDaysOfWeek(const RWCString& days);
    CtiCCStrategy& setControlUnits(const RWCString& contunit);
    CtiCCStrategy& setControlDelayTime(LONG delay);
    CtiCCStrategy& setControlSendRetries(LONG retries);




    BOOL isDirty() const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCStrategy& operator=(const CtiCCStrategy& right);

    int operator==(const CtiCCStrategy& right) const;
    int operator!=(const CtiCCStrategy& right) const;

    CtiCCStrategy* replicate() const;

    //Possible control methods
    /*static const RWCString SubstationBusControlMethod;
    static const RWCString IndividualFeederControlMethod;
    static const RWCString BusOptimizedFeederControlMethod;
    static const RWCString ManualOnlyControlMethod;

    static const RWCString KVARControlUnits;
    static const RWCString PF_BY_KVARControlUnits;
    static const RWCString PF_BY_KQControlUnits;
    */
    //static int PeakState;
    //static int OffPeakState;


    private:

    LONG _strategyid;
    RWCString _strategyname;
    RWCString _controlmethod;
    LONG _maxdailyoperation;
    BOOL _maxoperationdisableflag;
    LONG _peakstarttime;
    LONG _peakstoptime;
    LONG _controlinterval;
    LONG _maxconfirmtime;
    LONG _minconfirmpercent;
    LONG _failurepercent;
    RWCString _daysofweek;
    RWCString _controlunits;
    LONG _controldelaytime;
    LONG _controlsendretries;
    DOUBLE _peaklag;
    DOUBLE _offpklag;
    DOUBLE _peaklead;
    DOUBLE _offpklead;
    
    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    list <LONG> _subBusList;
    list <LONG> _feederList;

    void restore(RWDBReader& rdr);
};


typedef CtiCCStrategy* CtiCCStrategyPtr;
#endif
