
/*---------------------------------------------------------------------------
        Filename:  ccsubstationbus.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCSubstationBus
                        CtiCCSubstationBus maintains the state and handles
                        the persistence of strategies for Cap Control.                             

        Initial Date:  8/27/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCAREAIMPL_H
#define CTICCAREAIMPL_H

#include <list>
using std::list;

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h>  
#include <list> 
#include <vector>

#include "dbaccess.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "ccsubstationbus.h"
#include "ccfeeder.h"
#include "cccapbank.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "ccstrategy.h"
#include "ccmonitorpoint.h"

typedef std::vector<CtiCCSubstationBusPtr> CtiCCSubstationBus_vec;

              
class CtiCCArea : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCArea )

    CtiCCArea();
    CtiCCArea(RWDBReader& rdr);
    CtiCCArea(const CtiCCArea& area);

    virtual ~CtiCCArea();

    LONG getPAOId() const;
    const string& getPAOCategory() const;
    const string& getPAOClass() const;
    const string& getPAOName() const;
    const string& getPAOType() const;
    const string& getPAODescription() const;
    BOOL getDisableFlag() const;
    LONG getStrategyId() const;
    LONG getVoltReductionControlPointId() const;
    BOOL getVoltReductionControlValue() const;
    BOOL getOvUvDisabledFlag() const;
    BOOL getReEnableAreaFlag() const;

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
    DOUBLE getPFactor() const;
    DOUBLE getEstPFactor() const;
    BOOL getChildVoltReductionFlag() const;
    std::list<long>* getSubStationList(){return &_subStationIds;};
    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();
    list <LONG>* getPointIds() {return &_pointIds;};

    void deleteCCSubs(long subId);

    CtiCCArea& setPAOId(LONG id);
    CtiCCArea& setPAOCategory(const string& category);
    CtiCCArea& setPAOClass(const string& pclass);
    CtiCCArea& setPAOName(const string& name);
    CtiCCArea& setPAOType(const string& type);
    CtiCCArea& setPAODescription(const string& description);
    CtiCCArea& setDisableFlag(BOOL disable);
    CtiCCArea& setStrategyId(LONG strategyId);
    CtiCCArea& setVoltReductionControlPointId(LONG pointId);
    CtiCCArea& setVoltReductionControlValue(BOOL flag);
    CtiCCArea& setOvUvDisabledFlag(BOOL flag);
    CtiCCArea& setReEnableAreaFlag(BOOL flag);

    CtiCCArea& setStrategyName(const string& strategyname);
    CtiCCArea& setControlMethod(const string& method);
    CtiCCArea& setMaxDailyOperation(LONG max);
    CtiCCArea& setMaxOperationDisableFlag(BOOL maxopdisable);
    CtiCCArea& setPeakLag(DOUBLE peak);
    CtiCCArea& setOffPeakLag(DOUBLE offpeak);
    CtiCCArea& setPeakLead(DOUBLE peak);
    CtiCCArea& setOffPeakLead(DOUBLE offpeak);
    CtiCCArea& setPeakVARLag(DOUBLE peak);
    CtiCCArea& setOffPeakVARLag(DOUBLE offpeak);
    CtiCCArea& setPeakVARLead(DOUBLE peak);
    CtiCCArea& setOffPeakVARLead(DOUBLE offpeak);
    CtiCCArea& setPeakPFSetPoint(DOUBLE peak);
    CtiCCArea& setOffPeakPFSetPoint(DOUBLE offpeak);
    CtiCCArea& setPeakStartTime(LONG starttime);
    CtiCCArea& setPeakStopTime(LONG stoptime);
    CtiCCArea& setControlInterval(LONG interval);
    CtiCCArea& setMaxConfirmTime(LONG confirm);
    CtiCCArea& setMinConfirmPercent(LONG confirm);
    CtiCCArea& setFailurePercent(LONG failure);
    CtiCCArea& setDaysOfWeek(const string& days);
    CtiCCArea& setControlUnits(const string& contunit);
    CtiCCArea& setControlDelayTime(LONG delay);
    CtiCCArea& setControlSendRetries(LONG retries);
    CtiCCArea& setIntegrateFlag(BOOL flag);
    CtiCCArea& setIntegratePeriod(LONG period);
    CtiCCArea& setPFactor(DOUBLE pfactor);
    CtiCCArea& setEstPFactor(DOUBLE estPfactor);
    CtiCCArea& setChildVoltReductionFlag(BOOL flag);

    void setStrategyValues(CtiCCStrategyPtr strategy);
    void checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages);
    CtiCCArea& checkAndUpdateChildVoltReductionFlags();

    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(RWDBReader& rdr);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCArea& operator=(const CtiCCArea& right);

    int operator==(const CtiCCArea& right) const;
    int operator!=(const CtiCCArea& right) const;

    CtiCCArea* replicate() const;

    
    private:

    LONG _paoid;
    string _paocategory;
    string _paoclass;
    string _paoname;
    string _paotype;
    string _paodescription;
    BOOL _disableflag;
    LONG _strategyId;

    LONG _voltReductionControlPointId;
    BOOL _voltReductionControlValue;

    string _strategyName;
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
    BOOL _integrateflag;
    LONG _integrateperiod;

    DOUBLE _pfactor;
    DOUBLE _estPfactor;

    string _additionalFlags;
    BOOL _ovUvDisabledFlag;
    BOOL _reEnableAreaFlag;
    BOOL _childVoltReductionFlag;

    std::list<long> _subStationIds;
    std::list <long> _pointIds;

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;

       //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);

   
};


//typedef shared_ptr<CtiCCArea> CtiCCAreaPtr;
typedef CtiCCArea* CtiCCAreaPtr;
#endif
