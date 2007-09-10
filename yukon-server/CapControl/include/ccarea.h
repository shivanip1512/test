
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

typedef std::vector<CtiCCSubstationBus*> CtiCCSubstationBus_vec;

              
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
    BOOL getOvUvDisabledFlag() const;

    CtiCCSubstationBus_vec& getCCSubs();
    void deleteCCSubs(long subId);

    CtiCCArea& setPAOId(LONG id);
    CtiCCArea& setPAOCategory(const string& category);
    CtiCCArea& setPAOClass(const string& pclass);
    CtiCCArea& setPAOName(const string& name);
    CtiCCArea& setPAOType(const string& type);
    CtiCCArea& setPAODescription(const string& description);
    CtiCCArea& setDisableFlag(BOOL disable);
    CtiCCArea& setStrategyId(LONG strategyId);
    CtiCCArea& setOvUvDisabledFlag(BOOL flag);
    void setStrategyValues(CtiCCStrategyPtr strategy);

    list <LONG>* getPointIds() {return &_pointIds;};


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

    string _additionalFlags;
    BOOL _ovUvDisabledFlag;

    std::vector <CtiCCSubstationBus*> _ccsubs;
    BOOL _isSpecial;

   //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);


    std::list <long> _pointIds;
    
};


//typedef shared_ptr<CtiCCArea> CtiCCAreaPtr;
typedef CtiCCArea* CtiCCAreaPtr;
#endif
