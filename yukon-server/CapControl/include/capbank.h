/*---------------------------------------------------------------------------
        Filename:  capbank.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCapBank
                        CtiCapBank maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/18/00
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2000
---------------------------------------------------------------------------*/

#ifndef CTICAPBANKIMPL_H
#define CTICAPBANKIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
                
class CtiCapBank : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCapBank )

    CtiCapBank();
    CtiCapBank(RWDBReader& rdr);
    CtiCapBank(const CtiCapBank& cap);

    virtual ~CtiCapBank();

    LONG Id() const;
    const RWCString& Name() const;
    const RWCString& Type() const;
    const RWCString& CurrentState() const;
    BOOL DisableFlag() const;
    BOOL AlarmInhibit() const;
    BOOL ControlInhibit() const;
    const RWCString& DeviceClass() const;
    const RWCString& BankAddress() const;
    const RWCString& OperationalState() const;
    LONG ControlPointId() const;
    DOUBLE BankSize() const;
    LONG ControlDeviceId() const;
    LONG StatusPointId() const;
    LONG ControlStatus() const;
    LONG OperationAnalogPointId() const;
    LONG Operations() const;
    const RWDBDateTime& LastStatusChangeTime() const;
    LONG TagsControlStatus() const;
    BOOL StatusReceivedFlag() const;

    CtiCapBank& setId(LONG id);
    CtiCapBank& setName(const RWCString& name);
    CtiCapBank& setType(const RWCString& type);
    CtiCapBank& setCurrentState(const RWCString& current);
    CtiCapBank& setDisableFlag(BOOL disable);
    CtiCapBank& setAlarmInhibit(BOOL alarm);
    CtiCapBank& setControlInhibit(BOOL control);
    CtiCapBank& setDeviceClass(const RWCString& deviceclass);
    CtiCapBank& setBankAddress(const RWCString& address);
    CtiCapBank& setOperationalState(const RWCString& operational);
    CtiCapBank& setControlPointId(LONG controlpoint);
    CtiCapBank& setBankSize(DOUBLE size);
    CtiCapBank& setControlDeviceId(LONG controldevice);
    CtiCapBank& setStatusPointId(LONG statuspoint);
    CtiCapBank& setControlStatus(LONG status);
    CtiCapBank& setOperationAnalogPointId(LONG operationpoint);
    CtiCapBank& setOperations(LONG operations);
    CtiCapBank& setLastStatusChangeTime(const RWDBDateTime& laststatuschangetime);
    CtiCapBank& setTagsControlStatus(LONG tags);
    CtiCapBank& setStatusReceivedFlag(BOOL statusreceived);

    void restoreOperationFields(RWDBReader& rdr);
    CtiCapBank* replicate() const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCapBank& operator=(const CtiCapBank& right);

    int operator==(const CtiCapBank& right) const;
    int operator!=(const CtiCapBank& right) const;

    /* Static Members */

    //Possible operational states
    static const RWCString Switched;
    static const RWCString Fixed;
    
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
    
    LONG _id;
    RWCString _name;
    RWCString _type;
    RWCString _currentstate;
    BOOL _disableflag;
    BOOL _alarminhibit;
    BOOL _controlinhibit;
    RWCString _deviceclass;
    RWCString _bankaddress;
    RWCString _operationalstate;
    LONG _controlpointid;
    DOUBLE _banksize;
    LONG _controldeviceid;
    LONG _statuspointid;
    LONG _controlstatus;
    LONG _operationanalogpointid;
    LONG _operations;
    RWDBDateTime _laststatuschangetime;
    LONG _tagscontrolstatus;
    BOOL _statusreceivedflag;

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    void restore(RWDBReader& rdr);

};
#endif

