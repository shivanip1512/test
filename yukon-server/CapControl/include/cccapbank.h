/*---------------------------------------------------------------------------
        Filename:  cccapbank.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCCapBank
                        CtiCCCapBank maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCCAPBANKIMPL_H
#define CTICCCAPBANKIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
                
class CtiCCCapBank : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCCapBank )

    CtiCCCapBank();
    CtiCCCapBank(RWDBReader& rdr);
    CtiCCCapBank(const CtiCCCapBank& cap);

    virtual ~CtiCCCapBank();

    ULONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    const RWCString& getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    BOOL getAlarmInhibitFlag() const;
    BOOL getControlInhibitFlag() const;
    const RWCString& getOperationalState() const;
    const RWCString& getControllerType() const;
    ULONG getControlDeviceId() const;
    ULONG getControlPointId() const;
    ULONG getBankSize() const;
    const RWCString& getTypeOfSwitch() const;
    const RWCString& getSwitchManufacture() const;
    ULONG getMapLocationId() const;
    ULONG getControlOrder() const;
    ULONG getStatusPointId() const;
    ULONG getControlStatus() const;
    ULONG getOperationAnalogPointId() const;
    ULONG getCurrentDailyOperations() const;
    const RWDBDateTime& getLastStatusChangeTime() const;
    ULONG getTagsControlStatus() const;
    BOOL getStatusReceivedFlag() const;

    CtiCCCapBank& setPAOId(ULONG id);
    CtiCCCapBank& setPAOCategory(const RWCString& category);
    CtiCCCapBank& setPAOClass(const RWCString& pclass);
    CtiCCCapBank& setPAOName(const RWCString& name);
    CtiCCCapBank& setPAOType(const RWCString& type);
    CtiCCCapBank& setPAODescription(const RWCString& description);
    CtiCCCapBank& setDisableFlag(BOOL disable);
    CtiCCCapBank& setAlarmInhibitFlag(BOOL alarminhibit);
    CtiCCCapBank& setControlInhibitFlag(BOOL controlinhibit);
    CtiCCCapBank& setDeviceClass(const RWCString& deviceclass);
    CtiCCCapBank& setOperationalState(const RWCString& operational);
    CtiCCCapBank& setControllerType(const RWCString& controllertype);
    CtiCCCapBank& setControlDeviceId(ULONG controldevice);
    CtiCCCapBank& setControlPointId(ULONG controlpoint);
    CtiCCCapBank& setBankSize(ULONG size);
    CtiCCCapBank& setTypeOfSwitch(const RWCString& switchtype);
    CtiCCCapBank& setSwitchManufacture(const RWCString& manufacture);
    CtiCCCapBank& setMapLocationId(ULONG maplocation);
    CtiCCCapBank& setControlOrder(ULONG order);
    CtiCCCapBank& setStatusPointId(ULONG statuspoint);
    CtiCCCapBank& setControlStatus(ULONG status);
    CtiCCCapBank& setOperationAnalogPointId(ULONG operationpoint);
    CtiCCCapBank& setCurrentDailyOperations(ULONG operations);
    CtiCCCapBank& setLastStatusChangeTime(const RWDBDateTime& laststatuschangetime);
    CtiCCCapBank& setTagsControlStatus(ULONG tags);
    CtiCCCapBank& setStatusReceivedFlag(BOOL statusreceived);

    CtiCCCapBank* replicate() const;
    virtual int compareTo(const RWCollectable* right) const;

    void dumpDynamicData();

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCCapBank& operator=(const CtiCCCapBank& right);

    int operator==(const CtiCCCapBank& right) const;
    int operator!=(const CtiCCCapBank& right) const;

    /* Static Members */

    //Possible operational states
    static const RWCString SwitchedOperationalState;
    static const RWCString FixedOperationalState;
    
    //Possible states
    static int Open;
    static int Close;
    static int OpenQuestionable;
    static int CloseQuestionable;
    static int OpenFail;
    static int CloseFail;
    static int OpenPending;
    static int ClosePending;
    
private:

    ULONG _paoid;
    RWCString _paocategory;
    RWCString _paoclass;
    RWCString _paoname;
    RWCString _paotype;
    RWCString _paodescription;
    BOOL _disableflag;
    BOOL _alarminhibitflag;
    BOOL _controlinhibitflag;
    RWCString _operationalstate;
    RWCString _controllertype;
    ULONG _controldeviceid;
    ULONG _controlpointid;
    ULONG _banksize;
    RWCString _typeofswitch;
    RWCString _switchmanufacture;
    ULONG _maplocationid;
    ULONG _controlorder;
    ULONG _statuspointid;
    ULONG _controlstatus;
    ULONG _operationanalogpointid;
    ULONG _currentdailyoperations;
    RWDBDateTime _laststatuschangetime;
    ULONG _tagscontrolstatus;
    BOOL _statusreceivedflag;

    //don't stream
    BOOL _insertDynamicDataFlag;

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    void restore(RWDBReader& rdr);
};
#endif
