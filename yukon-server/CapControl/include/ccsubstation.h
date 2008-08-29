

/*---------------------------------------------------------------------------
        Filename:  ccsubstation.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCSubstationBus
                        CtiCCSubstationBus maintains the state and handles
                        the persistence of strategies for Cap Control.                             

        Initial Date:  8/27/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCSUBSTATIONIMPL_H
#define CTICCSUBSTATIONIMPL_H

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
#include "ccfeeder.h"
#include "cccapbank.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "ccstrategy.h"
#include "ccmonitorpoint.h"
#include "ccsubstationbus.h"


              
class CtiCCSubstation : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCSubstation )

    CtiCCSubstation();
    CtiCCSubstation(RWDBReader& rdr);
    CtiCCSubstation(const CtiCCSubstation& substation);

    virtual ~CtiCCSubstation();

    LONG getPAOId() const;
    const string& getPAOCategory() const;
    const string& getPAOClass() const;
    const string& getPAOName() const;
    const string& getPAOType() const;
    const string& getPAODescription() const;
    BOOL getDisableFlag() const;
    BOOL getOvUvDisabledFlag() const;
    BOOL getVoltReductionFlag() const;
    const string& getParentName() const;
    LONG getParentId() const;
    LONG getDisplayOrder() const;
    DOUBLE getPFactor() const;
    DOUBLE getEstPFactor() const;
    BOOL getSaEnabledFlag() const;
    LONG getSaEnabledId() const;
    LONG getVoltReductionControlId() const;

    
    list <LONG>* getCCSubIds(){return &_subBusIds;};
    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();

    list <LONG>* getPointIds() {return &_pointIds;};

    CtiCCSubstation& setPAOId(LONG id);
    CtiCCSubstation& setPAOCategory(const string& category);
    CtiCCSubstation& setPAOClass(const string& pclass);
    CtiCCSubstation& setPAOName(const string& name);
    CtiCCSubstation& setPAOType(const string& type);
    CtiCCSubstation& setPAODescription(const string& description);
    CtiCCSubstation& setDisableFlag(BOOL disable);
    CtiCCSubstation& setOvUvDisabledFlag(BOOL flag);
    CtiCCSubstation& setVoltReductionFlag(BOOL flag);
    CtiCCSubstation& setParentName(const string& name);
    CtiCCSubstation& setParentId(LONG parentId);
    CtiCCSubstation& setDisplayOrder(LONG displayOrder);
    CtiCCSubstation& setPFactor(DOUBLE pfactor);
    CtiCCSubstation& setEstPFactor(DOUBLE estpfactor);
    CtiCCSubstation& setSaEnabledFlag(BOOL flag);
    CtiCCSubstation& setSaEnabledId(LONG saId);
    CtiCCSubstation& setVoltReductionControlId(LONG pointid);
    
    DOUBLE calculatePowerFactor(DOUBLE kvar, DOUBLE kw);
    void checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages);
    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(RWDBReader& rdr);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCSubstation& operator=(const CtiCCSubstation& right);

    int operator==(const CtiCCSubstation& right) const;
    int operator!=(const CtiCCSubstation& right) const;

    CtiCCSubstation* replicate() const;

    
    private:

    LONG _paoid;
    string _paocategory;
    string _paoclass;
    string _paoname;
    string _paotype;
    string _paodescription;
    BOOL _disableflag;
    string _parentName;
    LONG _parentId;
    LONG _displayOrder;

    string _additionalFlags;
    BOOL _ovUvDisabledFlag;
    BOOL _voltReductionFlag;

    DOUBLE _pfactor;
    DOUBLE _estPfactor;
    BOOL _saEnabledFlag;
    LONG _saEnabledId;

    LONG _voltReductionControlId;
    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    std::list <long> _subBusIds;
    std::list <long> _pointIds;
    
    void restore(RWDBReader& rdr);
  
    
};

//typedef shared_ptr<CtiCCSubstation> CtiCCSubstationPtr;
typedef CtiCCSubstation* CtiCCSubstationPtr;
#endif
