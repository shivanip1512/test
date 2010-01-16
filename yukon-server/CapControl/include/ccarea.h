
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
#include "ControlStrategies.h"
#include "ccmonitorpoint.h"

typedef std::vector<CtiCCSubstationBusPtr> CtiCCSubstationBus_vec;

              
class CtiCCArea : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCArea )

    CtiCCArea();
    CtiCCArea(RWDBReader& rdr, StrategyPtr strategy);
    CtiCCArea(const CtiCCArea& area);

    virtual ~CtiCCArea();

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
    BOOL getReEnableAreaFlag() const;

    DOUBLE getPFactor() const;
    DOUBLE getEstPFactor() const;
    BOOL getChildVoltReductionFlag() const;
    std::list<long>* getSubStationList(){return &_subStationIds;};
    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();
    list <LONG>* getPointIds() {return &_pointIds;};
    BOOL getAreaUpdatedFlag() const;

    void deleteCCSubs(long subId);

    CtiCCArea& setPAOId(LONG id);
    CtiCCArea& setPAOCategory(const string& category);
    CtiCCArea& setPAOClass(const string& pclass);
    CtiCCArea& setPAOName(const string& name);
    CtiCCArea& setPAOType(const string& type);
    CtiCCArea& setPAODescription(const string& description);
    CtiCCArea& setDisableFlag(BOOL disable);
    CtiCCArea& setVoltReductionControlPointId(LONG pointId);
    CtiCCArea& setVoltReductionControlValue(BOOL flag);
    CtiCCArea& setOvUvDisabledFlag(BOOL flag);
    CtiCCArea& setReEnableAreaFlag(BOOL flag);

    CtiCCArea& setPFactor(DOUBLE pfactor);
    CtiCCArea& setEstPFactor(DOUBLE estPfactor);
    CtiCCArea& setChildVoltReductionFlag(BOOL flag);
    CtiCCArea& setAreaUpdatedFlag(BOOL flag);

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
    BOOL _reEnableAreaFlag;
    BOOL _childVoltReductionFlag;

    std::list<long> _subStationIds;
    std::list <long> _pointIds;

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;

       //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;
    BOOL _areaUpdatedFlag;

    void restore(RWDBReader& rdr);

   
};


//typedef shared_ptr<CtiCCArea> CtiCCAreaPtr;
typedef CtiCCArea* CtiCCAreaPtr;
#endif
