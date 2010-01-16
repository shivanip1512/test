
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
#include "ControlStrategies.h"
#include "ccOperationStats.h"
#include "ccConfirmationStats.h"
              
class CtiCCSpecial : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCSpecial )

    CtiCCSpecial();
    CtiCCSpecial(RWDBReader& rdr, StrategyPtr strategy);
    CtiCCSpecial(const CtiCCSpecial& area);

    virtual ~CtiCCSpecial();

    LONG getPAOId() const;
    const string& getPAOCategory() const;
    const string& getPAOClass() const;
    const string& getPAOName() const;
    const string& getPAOType() const;
    const string& getPAODescription() const;
    BOOL getDisableFlag() const;
    LONG getVoltReductionControlPointId() const;
    BOOL getVoltReductionControlValue() const;

    BOOL getOvUvDisabledFlag() const;
    DOUBLE getPFactor() const;
    DOUBLE getEstPFactor() const;

    CtiCCSpecial& setPAOId(LONG id);
    CtiCCSpecial& setPAOCategory(const string& category);
    CtiCCSpecial& setPAOClass(const string& pclass);
    CtiCCSpecial& setPAOName(const string& name);
    CtiCCSpecial& setPAOType(const string& type);
    CtiCCSpecial& setPAODescription(const string& description);
    CtiCCSpecial& setDisableFlag(BOOL disable);
    CtiCCSpecial& setVoltReductionControlPointId(LONG pointId);
    CtiCCSpecial& setVoltReductionControlValue(BOOL flag);

    CtiCCSpecial& setOvUvDisabledFlag(BOOL flag);
    CtiCCSpecial& setPFactor(DOUBLE pfactor);
    CtiCCSpecial& setEstPFactor(DOUBLE estpfactor);

    list <LONG>* getSubstationIds() {return &_substationIds;};
    list <LONG>* getPointIds() {return &_pointIds;};
    CtiCCOperationStats& getOperationStats(); 
    CtiCCConfirmationStats& getConfirmationStats();

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

    void setStrategy(StrategyPtr strategy);
    StrategyPtr getStrategy() const;
            
private:

    StrategyPtr    _strategy;

    LONG _paoid;
    string _paocategory;
    string _paoclass;
    string _paoname;
    string _paotype;
    string _paodescription;
    BOOL _disableflag;
    LONG _voltReductionControlPointId;
    BOOL _voltReductionControlValue;

    DOUBLE _pfactor;
    DOUBLE _estPfactor;

    string _additionalFlags;
    BOOL _ovUvDisabledFlag;

    std::list <LONG> _substationIds;
    BOOL _isSpecial;

    std::list <long> _pointIds;

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;

   //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);
    
};


//typedef shared_ptr<CtiCCSpecial> CtiCCSpecialPtr;
typedef CtiCCSpecial* CtiCCSpecialPtr;
#endif

