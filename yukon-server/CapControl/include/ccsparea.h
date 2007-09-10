
/*---------------------------------------------------------------------------
        Filename:  ccspecial.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCSpecial
                        CtiCCSpecial maintains the state and handles
                        the persistence of strategies for Cap Control.                             

        Initial Date:  8/27/2007
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCSPECIALIMPL_H
#define CTICCSPECIALIMPL_H

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
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "ccstrategy.h"
              
class CtiCCSpecial : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCSpecial )

    CtiCCSpecial();
    CtiCCSpecial(RWDBReader& rdr);
    CtiCCSpecial(const CtiCCSpecial& area);

    virtual ~CtiCCSpecial();

    LONG getPAOId() const;
    const string& getPAOCategory() const;
    const string& getPAOClass() const;
    const string& getPAOName() const;
    const string& getPAOType() const;
    const string& getPAODescription() const;
    BOOL getDisableFlag() const;
    LONG getStrategyId() const;

    CtiCCSpecial& setPAOId(LONG id);
    CtiCCSpecial& setPAOCategory(const string& category);
    CtiCCSpecial& setPAOClass(const string& pclass);
    CtiCCSpecial& setPAOName(const string& name);
    CtiCCSpecial& setPAOType(const string& type);
    CtiCCSpecial& setPAODescription(const string& description);
    CtiCCSpecial& setDisableFlag(BOOL disable);
    CtiCCSpecial& setStrategyId(LONG strategyId);
    void setStrategyValues(CtiCCStrategyPtr strategy);

    list <LONG>* getSubIds() {return &_subIds;};


    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(RWDBReader& rdr);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCSpecial& operator=(const CtiCCSpecial& right);

    int operator==(const CtiCCSpecial& right) const;
    int operator!=(const CtiCCSpecial& right) const;

    CtiCCSpecial* replicate() const;

    
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

    std::list <LONG> _subIds;
    BOOL _isSpecial;

   //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);
    
};


//typedef shared_ptr<CtiCCSpecial> CtiCCSpecialPtr;
typedef CtiCCSpecial* CtiCCSpecialPtr;
#endif

