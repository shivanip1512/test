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

#include "dbaccess.h"
#include "observe.h"

typedef enum
{
    uninit = 0,
    pending1stCmd, 
    pending2ndCmd,
    pending2ndCmdRetry,
    pending3rdCmd,
    pending3rdCmdRetry,
    success,
    fail
} CCBANKVSTATE;  

typedef enum
{
    verificationFail = 0,
    verificationSuccess,
    verificationRetry
} CCBANKVRESULT;
                
class CtiCCCapBank : public RWCollectable
{

public:

  RWDECLARE_COLLECTABLE( CtiCCCapBank )

    CtiCCCapBank(RWDBReader& rdr);
    CtiCCCapBank(const CtiCCCapBank& cap);

    virtual ~CtiCCCapBank();

    LONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    const RWCString& getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    LONG getParentId() const;
    BOOL getAlarmInhibitFlag() const;
    BOOL getControlInhibitFlag() const;
    const RWCString& getOperationalState() const;
    const RWCString& getControllerType() const;
    LONG getControlDeviceId() const;
    LONG getControlPointId() const;
    const RWCString& getControlDeviceType() const;
    LONG getBankSize() const;
    const RWCString& getTypeOfSwitch() const;
    const RWCString& getSwitchManufacture() const;
    const RWCString& getMapLocationId() const;
    LONG getRecloseDelay() const;
    LONG getControlOrder() const;
    LONG getStatusPointId() const;
    LONG getControlStatus() const;
    LONG getOperationAnalogPointId() const;
    LONG getTotalOperations() const;
    const RWDBDateTime& getLastStatusChangeTime() const;
    LONG getTagsControlStatus() const;
    LONG getOriginalFeederId() const;
    LONG getOriginalSwitchingOrder() const;
    BOOL getVerificationFlag() const;
    BOOL getPerformingVerificationFlag() const;
    BOOL getVerificationDoneFlag() const;

    int  getVCtrlIndex() const;
    CCBANKVRESULT getCurrVCmdResult() const;
    CCBANKVRESULT getPrevVCmdResult() const;
    CCBANKVSTATE getVerificationState() const;
    int getAssumedOrigVerificationState() const;

    CtiCCCapBank& setPAOId(LONG id);
    CtiCCCapBank& setPAOCategory(const RWCString& category);
    CtiCCCapBank& setPAOClass(const RWCString& pclass);
    CtiCCCapBank& setPAOName(const RWCString& name);
    CtiCCCapBank& setPAOType(const RWCString& type);
    CtiCCCapBank& setPAODescription(const RWCString& description);
    CtiCCCapBank& setDisableFlag(BOOL disable);
    CtiCCCapBank& setParentId(LONG parentId);
    CtiCCCapBank& setAlarmInhibitFlag(BOOL alarminhibit);
    CtiCCCapBank& setControlInhibitFlag(BOOL controlinhibit);
    CtiCCCapBank& setDeviceClass(const RWCString& deviceclass);
    CtiCCCapBank& setOperationalState(const RWCString& operational);
    CtiCCCapBank& setControllerType(const RWCString& controllertype);
    CtiCCCapBank& setControlDeviceId(LONG controldevice);
    CtiCCCapBank& setControlPointId(LONG controlpoint);
    CtiCCCapBank& setControlDeviceType(const RWCString& controlDeviceType);
    CtiCCCapBank& setBankSize(LONG size);
    CtiCCCapBank& setTypeOfSwitch(const RWCString& switchtype);
    CtiCCCapBank& setSwitchManufacture(const RWCString& manufacture);
    CtiCCCapBank& setMapLocationId(const RWCString& maplocation);
    CtiCCCapBank& setRecloseDelay(LONG reclose);
    CtiCCCapBank& setControlOrder(LONG order);
    CtiCCCapBank& setStatusPointId(LONG statuspoint);
    CtiCCCapBank& setControlStatus(LONG status);
    CtiCCCapBank& setOperationAnalogPointId(LONG operationpoint);
    CtiCCCapBank& setTotalOperations(LONG operations);
    CtiCCCapBank& setLastStatusChangeTime(const RWDBDateTime& laststatuschangetime);
    CtiCCCapBank& setTagsControlStatus(LONG tags);
    CtiCCCapBank& setOriginalFeederId(LONG origfeeder);
    CtiCCCapBank& setOriginalSwitchingOrder(LONG origorder);

    CtiCCCapBank& setVerificationFlag(BOOL verificationFlag);
    CtiCCCapBank& setPerformingVerificationFlag(BOOL performingVerificationFlag);
    CtiCCCapBank& setVerificationDoneFlag(BOOL verificationDoneFlag);

    CtiCCCapBank& setVCtrlIndex(int vCtrlIndex);
    CtiCCCapBank& setCurrVCmdResult(CCBANKVRESULT CmdResult);
    CtiCCCapBank& setPrevVCmdResult(CCBANKVRESULT CmdResult);
    CtiCCCapBank& setVerificationState(CCBANKVSTATE verificationState);
    CtiCCCapBank& setAssumedOrigVerificationState(int assumedOrigCapBankPos);
    CtiCCCapBank& setPreviousVerificationControlStatus(LONG status);
     BOOL updateVerificationState(void);

    //int getAssumedOrigVerificationState();
    CtiCCCapBank& initVerificationControlStatus();

    CtiCCCapBank* replicate() const;
    virtual int compareTo(const RWCollectable* right) const;

    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);

    void setDynamicData(RWDBReader& rdr);

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
    static const RWCString UninstalledState;
    
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

    LONG _paoid;
    RWCString _paocategory;
    RWCString _paoclass;
    RWCString _paoname;
    RWCString _paotype;
    RWCString _paodescription;
    BOOL _disableflag;
    LONG _parentId; //feederId
    BOOL _alarminhibitflag;
    BOOL _controlinhibitflag;
    RWCString _operationalstate;
    RWCString _controllertype;
    LONG _controldeviceid;
    LONG _controlpointid;
    RWCString _controlDeviceType;
    LONG _banksize;
    RWCString _typeofswitch;
    RWCString _switchmanufacture;
    RWCString _maplocationid;
    LONG _reclosedelay;
    LONG _controlorder;
    LONG _statuspointid;
    LONG _controlstatus;
    LONG _operationanalogpointid;
    LONG _totaloperations;
    RWDBDateTime _laststatuschangetime;
    LONG _tagscontrolstatus;
    LONG _originalfeederid;
    LONG _originalswitchingorder;


    //verification info
    RWCString _additionalFlags;


    LONG _verificationControlStatus;

    int _vCtrlIndex; //1,2, or 3
    BOOL _retryFlag;

    LONG _prevVerificationControlStatus;
    int _assumedOrigCapBankPos;
    BOOL _verificationFlag;
    BOOL _performingVerificationFlag;
    BOOL _verificationDoneFlag;


    CCBANKVRESULT _currCmdResult; //0 = fail, 1 = success, 2 = retry????
    CCBANKVRESULT _prevCmdResult;
    CCBANKVSTATE _verificationState;
    

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);
    void restoreCapBankTableValues(RWDBReader& rdr);
    CtiCCCapBank();
};

//typedef shared_ptr<CtiCCCapBank> CtiCCCapBankPtr;
typedef CtiCCCapBank* CtiCCCapBankPtr;
#endif
