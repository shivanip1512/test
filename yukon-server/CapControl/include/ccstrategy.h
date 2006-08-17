
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
    const string& getStrategyName() const;
    const string& getControlMethod() const;
    LONG getMaxDailyOperation() const;
    BOOL getMaxOperationDisableFlag() const;
    DOUBLE getPeakLag() const;
    DOUBLE getOffPeakLag() const;
    DOUBLE getPeakLead() const;
    DOUBLE getOffPeakLead() const;
    DOUBLE getPeakVARLag() const;
    DOUBLE getOffPeakVARLag() const;
    DOUBLE getPeakVARLead() const;
    DOUBLE getOffPeakVARLead() const;
    LONG getPeakStartTime() const;
    LONG getPeakStopTime() const;
    LONG getControlInterval() const;
    LONG getMaxConfirmTime() const;
    LONG getMinConfirmPercent() const;
    LONG getFailurePercent() const;
    const string& getDaysOfWeek() const;
    const string& getControlUnits() const;
    LONG getControlDelayTime() const;
    LONG getControlSendRetries() const;

    CtiCCStrategy& setStrategyId(LONG id);
    CtiCCStrategy& setStrategyName(const string& strategyname);
    CtiCCStrategy& setControlMethod(const string& method);
    CtiCCStrategy& setMaxDailyOperation(LONG max);
    CtiCCStrategy& setMaxOperationDisableFlag(BOOL maxopdisable);
    CtiCCStrategy& setPeakLag(DOUBLE peak);
    CtiCCStrategy& setOffPeakLag(DOUBLE offpeak);
    CtiCCStrategy& setPeakLead(DOUBLE peak);
    CtiCCStrategy& setOffPeakLead(DOUBLE offpeak);
    CtiCCStrategy& setPeakVARLag(DOUBLE peak);
    CtiCCStrategy& setOffPeakVARLag(DOUBLE offpeak);
    CtiCCStrategy& setPeakVARLead(DOUBLE peak);
    CtiCCStrategy& setOffPeakVARLead(DOUBLE offpeak);
    CtiCCStrategy& setPeakStartTime(LONG starttime);
    CtiCCStrategy& setPeakStopTime(LONG stoptime);
    CtiCCStrategy& setControlInterval(LONG interval);
    CtiCCStrategy& setMaxConfirmTime(LONG confirm);
    CtiCCStrategy& setMinConfirmPercent(LONG confirm);
    CtiCCStrategy& setFailurePercent(LONG failure);
    CtiCCStrategy& setDaysOfWeek(const string& days);
    CtiCCStrategy& setControlUnits(const string& contunit);
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
    /*static const string SubstationBusControlMethod;
    static const string IndividualFeederControlMethod;
    static const string BusOptimizedFeederControlMethod;
    static const string ManualOnlyControlMethod;

    static const string KVARControlUnits;
    static const string PF_BY_KVARControlUnits;
    static const string PF_BY_KQControlUnits;
    */
    //static int PeakState;
    //static int OffPeakState;


    private:

    LONG _strategyid;
    string _strategyname;
    string _controlmethod;
    LONG _maxdailyoperation;
    BOOL _maxoperationdisableflag;
    LONG _peakstarttime;
    LONG _peakstoptime;
    LONG _controlinterval;
    LONG _maxconfirmtime;
    LONG _minconfirmpercent;
    LONG _failurepercent;
    string _daysofweek;
    string _controlunits;
    LONG _controldelaytime;
    LONG _controlsendretries;
    DOUBLE _peaklag;
    DOUBLE _offpklag;
    DOUBLE _peaklead;
    DOUBLE _offpklead;
    DOUBLE _peakVARlag;
    DOUBLE _offpkVARlag;
    DOUBLE _peakVARlead;
    DOUBLE _offpkVARlead;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    std::list <LONG> _subBusList;
    std::list <LONG> _feederList;

    void restore(RWDBReader& rdr);
};


typedef CtiCCStrategy* CtiCCStrategyPtr;
#endif
