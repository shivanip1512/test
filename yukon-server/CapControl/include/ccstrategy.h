
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

typedef struct
{
    long _secsFromMidnight;
    int  _percentToClose;

} CtiTimeOfDayController;
//For Sorted Vector, the vector will use this to determine position in the vector.
struct CtiTimeOfDayController_less 
{
    bool operator()( const CtiTimeOfDayController* _X , const CtiTimeOfDayController* _Y)
        { return ( _X->_secsFromMidnight < _Y->_secsFromMidnight ); }
};
//Typedef for Sanity using sorted vectors
typedef codeproject::sorted_vector<CtiTimeOfDayController*,true,CtiTimeOfDayController_less> CtiTODC_SVector;
     
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
    DOUBLE getPeakPFSetPoint() const;
    DOUBLE getOffPeakPFSetPoint() const;
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
    BOOL getIntegrateFlag() const;
    LONG getIntegratePeriod() const;
    BOOL getLikeDayFallBack() const;


    CtiTODC_SVector& getTimeOfDayControllers();
    void  dumpTimeOfDayControllers();



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
    CtiCCStrategy& setPeakPFSetPoint(DOUBLE peak);
    CtiCCStrategy& setOffPeakPFSetPoint(DOUBLE offpeak);
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
    CtiCCStrategy& setIntegrateFlag(BOOL flag);
    CtiCCStrategy& setIntegratePeriod(LONG period);
    CtiCCStrategy& setLikeDayFallBack(BOOL flag);

    void setTimeAndCloseValues(RWDBReader& rdr);


    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCStrategy& operator=(const CtiCCStrategy& right);

    int operator==(const CtiCCStrategy& right) const;
    int operator!=(const CtiCCStrategy& right) const;

    CtiCCStrategy* replicate() const;

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
    DOUBLE _peakPFSetPoint;
    DOUBLE _offpkPFSetPoint;
    BOOL  _integrateFlag;
    LONG _integratePeriod;
    BOOL _likeDayFallBack;

    CtiTODC_SVector _todc;

    void restore(RWDBReader& rdr);
};


typedef CtiCCStrategy* CtiCCStrategyPtr;
#endif
