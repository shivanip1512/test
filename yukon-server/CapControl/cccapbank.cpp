/*---------------------------------------------------------------------------
        Filename:  cccapbank.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCCapBank.
                        CtiCCCapBank maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "cccapbank.h"
#include "ccid.h"
#include "pointdefs.h"
#include "device.h"
#include "logger.h"
#include "resolvers.h"

extern ULONG _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCCapBank, CTICCCAPBANK_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCCapBank::CtiCCCapBank()
{
}

CtiCCCapBank::CtiCCCapBank(RWDBReader& rdr)
{
    restoreCapBankTableValues(rdr);
    //restore(rdr);
}

CtiCCCapBank::CtiCCCapBank(const CtiCCCapBank& cap)
{
    operator=(cap);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCCapBank::~CtiCCCapBank()
{
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the cap bank device
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getPAOId() const
{
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getPAOCategory() const
{
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getPAOName() const
{
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getPAOType() const
{
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getPAODescription() const
{
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCCapBank::getDisableFlag() const
{
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getParentId

    Returns the parentID (feederID) of the cap bank
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getParentId() const
{
    return _parentId;
}


/*---------------------------------------------------------------------------
    getAlarmInhibitFlag

    Returns the alarm inhibit of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCCapBank::getAlarmInhibitFlag() const
{
    return _alarminhibitflag;
}

/*---------------------------------------------------------------------------
    getControlInhibitFlag

    Returns the control inhibit of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCCapBank::getControlInhibitFlag() const
{
    return _controlinhibitflag;
}

/*---------------------------------------------------------------------------
    getOperationalState

    Returns the operational state of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getOperationalState() const
{
    return _operationalstate;
}

/*---------------------------------------------------------------------------
    getControllerType

    Returns the controller type of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getControllerType() const
{
    return _controllertype;
}

/*---------------------------------------------------------------------------
    getControlDeviceId

    Returns the control device id of the cap bank
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getControlDeviceId() const
{
    return _controldeviceid;
}

/*---------------------------------------------------------------------------
    getControlPointId

    Returns the control point id of the cap bank
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getControlPointId() const
{
    return _controlpointid;
}

/*---------------------------------------------------------------------------
    getBankSize

    Returns the bank size of the cap bank
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getBankSize() const
{
    return _banksize;
}

/*---------------------------------------------------------------------------
    getTypeOfSwitch

    Returns the type of switch of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getTypeOfSwitch() const
{
    return _typeofswitch;
}

/*---------------------------------------------------------------------------
    getSwitchManufacture

    Returns the switch manufacture of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getSwitchManufacture() const
{
    return _switchmanufacture;
}

/*---------------------------------------------------------------------------
    getMapLocationId

    Returns the map location id of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getMapLocationId() const
{
    return _maplocationid;
}

/*---------------------------------------------------------------------------
    getRecloseDelay

    Returns the Reclose Delay of the cap bank
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getRecloseDelay() const
{
    return _reclosedelay;
}

/*---------------------------------------------------------------------------
    getControlOrder

    Returns the control order of the cap bank in the list of a feeder
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getControlOrder() const
{
    return _controlorder;
}

/*---------------------------------------------------------------------------
    getStatusPointId

    Returns the status point id of the cap bank
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getStatusPointId() const
{
    return _statuspointid;
}

/*---------------------------------------------------------------------------
    getVerificationFlag
    
    Returns the verification flag of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCCapBank::getVerificationFlag() const
{
    return _verificationFlag;
}

BOOL CtiCCCapBank::getPerformingVerificationFlag() const
{
    return _performingVerificationFlag;
}

BOOL CtiCCCapBank::getVerificationDoneFlag() const
{
    return _verificationDoneFlag;
}

/*---------------------------------------------------------------------------
    getVCtrlIndex
    
    Returns the VerificationCtrlIndex of the cap bank
---------------------------------------------------------------------------*/
int  CtiCCCapBank::getVCtrlIndex() const
{
    return _vCtrlIndex;
}

/*---------------------------------------------------------------------------
    getCurrVCmdResult
        
    Returns the CurrVCmdResult of the cap bank
---------------------------------------------------------------------------*/
CCBANKVRESULT CtiCCCapBank::getCurrVCmdResult() const
{
    return _currCmdResult;
}

/*---------------------------------------------------------------------------
    getPrevVCmdResult
        
    Returns the PrevVCmdResult of the cap bank
---------------------------------------------------------------------------*/
CCBANKVRESULT CtiCCCapBank::getPrevVCmdResult() const
{
    return _prevCmdResult;
}

/*---------------------------------------------------------------------------
    getVerificationState
        
    Returns the VerificationState of the cap bank
---------------------------------------------------------------------------*/
CCBANKVSTATE CtiCCCapBank::getVerificationState() const
{
    return _verificationState;
}

/*---------------------------------------------------------------------------
    getAssumedOrigVerificationState
        
    Returns the AssumedOrigVerificationState of the cap bank
---------------------------------------------------------------------------*/
int CtiCCCapBank::getAssumedOrigVerificationState() const
{
    LONG controlStatus = getControlStatus();

    if ( controlStatus == CtiCCCapBank::Open ||
         controlStatus == CtiCCCapBank::OpenQuestionable ||
         controlStatus == CtiCCCapBank::OpenFail )
    {
        return CtiCCCapBank::Open;
    }
    else if ( controlStatus == CtiCCCapBank::Close ||
              controlStatus == CtiCCCapBank::CloseQuestionable ||
              controlStatus == CtiCCCapBank::CloseFail )
    {
        return CtiCCCapBank::Close;
    }
    else  // CtiCCCapBank::OpenPending ||  CtiCCCapBank::ClosePending
    {
        return CtiCCCapBank::Open;
    }
    
    return controlStatus;
}

/*---------------------------------------------------------------------------
    getControlStatus

    Returns the control status of the cap bank
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getControlStatus() const
{

   /* if (_verificationFlag)
    {
        return _verificationControlStatus;
    }
    else */
    {
        return _controlstatus;
    }
}

/*---------------------------------------------------------------------------
    getOperationAnalogPointId

    Returns the point id of the analog that holds the number of operations
    on the cap bank
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getOperationAnalogPointId() const
{
    return _operationanalogpointid;
}

/*---------------------------------------------------------------------------
    getTotalOperations

    Returns the number operations performed on the cap bank
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getTotalOperations() const
{
    return _totaloperations;
}

/*---------------------------------------------------------------------------
    getLastStatusChangeTime

    Returns the timestamp of the last status point data msg sent by dispatch
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCCapBank::getLastStatusChangeTime() const
{
    return _laststatuschangetime;
}

/*---------------------------------------------------------------------------
    getTagsControlStatus

    Returns the tags of control status on the cap bank
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getTagsControlStatus() const
{
    return _tagscontrolstatus;
}

/*---------------------------------------------------------------------------
    getOriginalFeederId

    Returns the original feeder id on the cap bank used for temp cap bank moves
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getOriginalFeederId() const
{
    return _originalfeederid;
}

/*---------------------------------------------------------------------------
    getOriginalSwitchingOrder

    Returns the original switching order on the cap bank used for temp cap bank moves 
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getOriginalSwitchingOrder() const
{
    return _originalswitchingorder;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the id of the capbank - use with caution
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAOId(LONG id)
{
    _paoid = id;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAOCategory(const RWCString& category)
{
    _paocategory = category;

    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAOClass(const RWCString& pclass)
{
    _paoclass = pclass;

    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAOName(const RWCString& name)
{
    _paoname = name;

    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAOType(const RWCString& type)
{
    _paotype = type;

    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAODescription(const RWCString& description)
{
    _paodescription = description;

    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setDisableFlag(BOOL disable)
{
    _disableflag = disable;

    return *this;
}

/*---------------------------------------------------------------------------
    setParentId

    Sets the parentId (feederId) of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setParentId(LONG parentId)
{
    _parentId = parentId;

    return *this;
}

/*---------------------------------------------------------------------------
    setAlarmInhibitFlag

    Sets the alarm inhibit of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setAlarmInhibitFlag(BOOL alarminhibit)
{
    _alarminhibitflag = alarminhibit;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlInhibitFlag

    Sets the control inhibit of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlInhibitFlag(BOOL controlinhibit)
{
    _controlinhibitflag = controlinhibit;

    return *this;
}

/*---------------------------------------------------------------------------
    setOperationalState

    Sets the operational state of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOperationalState(const RWCString& operational)
{
    _operationalstate = operational;

    return *this;
}

/*---------------------------------------------------------------------------
    setControllerType

    Sets the controller type of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControllerType(const RWCString& controllertype)
{
    _controllertype = controllertype;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlDeviceId

    Sets the control device id of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlDeviceId(LONG controldevice)
{
    _controldeviceid = controldevice;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlPointId

    Sets the control point id of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlPointId(LONG controlpoint)
{
    _controlpointid = controlpoint;

    return *this;
}

/*---------------------------------------------------------------------------
    setBankSize

    Sets the bank size of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setBankSize(LONG size)
{
    _banksize = size;

    return *this;
}

/*---------------------------------------------------------------------------
    setTypeOfSwitch

    Sets the type of switch of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setTypeOfSwitch(const RWCString& switchtype)
{
    _typeofswitch = switchtype;

    return *this;
}

/*---------------------------------------------------------------------------
    setSwitchManufacture

    Sets the switch manufacture of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setSwitchManufacture(const RWCString& manufacture)
{
    _switchmanufacture = manufacture;

    return *this;
}

/*---------------------------------------------------------------------------
    setMapLocationId

    Sets the map location id of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setMapLocationId(const RWCString& maplocation)
{
    _maplocationid = maplocation;

    return *this;
}

/*---------------------------------------------------------------------------
    setRecloseDelay

    Sets the RecloseDelay of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setRecloseDelay(LONG reclose)
{
    _reclosedelay = reclose;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlOrder

    Sets the control order of the capbank in the list of the parent feeder
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlOrder(LONG order)
{
    _controlorder = order;

    return *this;
}

/*---------------------------------------------------------------------------
    setStatusPointId

    Sets the status point id of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setStatusPointId(LONG statuspoint)
{
    _statuspointid = statuspoint;

    return *this;
}
/*---------------------------------------------------------------------------
    setVerificationFlag
    
    Sets the verification flag, b4 capbank is exercised in the verification routine
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setVerificationFlag(BOOL verificationFlag)
{

    if (_verificationFlag != verificationFlag)
    {
        _dirty = TRUE;
    }
    _verificationFlag = verificationFlag;

    return *this;
}

CtiCCCapBank& CtiCCCapBank::setPerformingVerificationFlag(BOOL performingVerificationFlag)
{
    if (_performingVerificationFlag != performingVerificationFlag)
        _dirty = TRUE;
    _performingVerificationFlag = performingVerificationFlag;

    return *this;
}
CtiCCCapBank& CtiCCCapBank::setVerificationDoneFlag(BOOL verificationDoneFlag)
{
    if(_verificationDoneFlag != verificationDoneFlag)
        _dirty = TRUE;
    _verificationDoneFlag = verificationDoneFlag;

    return *this;
}



BOOL CtiCCCapBank::updateVerificationState(void)
{

    int ctrlIdx = getVCtrlIndex();
    _verificationDoneFlag = FALSE;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " CB: "<<getPAOId()<<" vCtrlIdx: "<< getVCtrlIndex() <<" prevControlStatus: "<< _prevVerificationControlStatus <<"  ControlStatus: " << getControlStatus() << endl;
    }
    switch (ctrlIdx)
    {
    case 1:
        {
            setPreviousVerificationControlStatus(getControlStatus());
            //_prevVerificationControlStatus = getControlStatus();
            _verificationDoneFlag = FALSE;
            ctrlIdx++;
            break;
        }
    case 2:
        {
            if ( (getControlStatus() == Open || getControlStatus() == Close) &&
                  getControlStatus() != _assumedOrigCapBankPos )
            {
                _verificationDoneFlag = TRUE;
                ctrlIdx = 5;
                _retryFlag = FALSE;
            }
            else
            {
                if ( _prevVerificationControlStatus == OpenFail ||
                     //_prevVerificationControlStatus == OpenQuestionable ||
                     _prevVerificationControlStatus == CloseFail /*||
                     _prevVerificationControlStatus == CloseQuestionable */)
                {
                    if (getControlStatus() == OpenFail ||
                        //getControlStatus() == OpenQuestionable ||
                        getControlStatus() == CloseFail /*||       
                        getControlStatus() == CloseQuestionable */) 
                    {
                        _verificationDoneFlag = TRUE;
                        ctrlIdx = 5;
                        _retryFlag = FALSE;
                    }
                    else
                    {
                        ctrlIdx++;
                        _retryFlag = FALSE;
                        //_prevVerificationControlStatus = getControlStatus();
                        setPreviousVerificationControlStatus(getControlStatus());
                    }
                }
                else //_prevVerificationControlStatus == Questionable, Open or Close (Success)!!!
                {
                    if (getControlStatus() == OpenFail ||
                        //getControlStatus() == OpenQuestionable ||
                        getControlStatus() == CloseFail /*||       
                        getControlStatus() == CloseQuestionable*/ ) 
                    {
                        if (_retryFlag)
                        {
                            _retryFlag = FALSE;
                            ctrlIdx = 5;
                            _verificationDoneFlag = TRUE;
                        }
                        else
                        {
                            _retryFlag = TRUE;

                        }
                    }
                    else // getControlStatus() == Open or Close (Success)!!   MUST HAVE BEEN A RETRY!
                    {
                         _retryFlag = FALSE; 
                         ctrlIdx = 5;
                         _verificationDoneFlag = TRUE;
                         /*{
                             CtiLockGuard<CtiLogger> doubt_guard(dout);
                             dout << RWTime() << " ***JULIE*** Check This $#!&* out, Shouldn't get HERE ==> " << __FILE__ << " (" << __LINE__ << ")" << endl;
                         }*/
                    }
                }
            }

            break;
        }
    case 3:
        {
            if (getControlStatus() == OpenFail ||
                //getControlStatus() == OpenQuestionable ||
                getControlStatus() == CloseFail /*||       
                getControlStatus() == CloseQuestionable*/ ) 
            {
                if (_retryFlag)
                {
                    _retryFlag = FALSE;
                    ctrlIdx = 5;
                    _verificationDoneFlag = TRUE;
                }
                else
                {
                    _retryFlag = TRUE;

                }
            }
            else  // getControlStatus() == Open or Close (Success!!!)
            {
                _retryFlag = FALSE;
                ctrlIdx = 5;
                _verificationDoneFlag = TRUE;
            }

            break;
        }
    default:
        {
                             CtiLockGuard<CtiLogger> doubt_guard(dout);
                             dout << RWTime() << " ***JULIE*** Check This $#!&* out, Shouldn't get HERE ==> " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        break;
    }

    setVCtrlIndex(ctrlIdx);

    return _verificationDoneFlag;
}

CtiCCCapBank& CtiCCCapBank::setVCtrlIndex(int vCtrlIndex)
{
    if (vCtrlIndex != _vCtrlIndex)
    {
        _dirty = TRUE;
    }
    _vCtrlIndex = vCtrlIndex;
    return *this;
}
CtiCCCapBank& CtiCCCapBank::setCurrVCmdResult(CCBANKVRESULT CmdResult)
{
    _currCmdResult = CmdResult;
    return *this;
}
CtiCCCapBank& CtiCCCapBank::setPrevVCmdResult(CCBANKVRESULT CmdResult)
{
    _prevCmdResult = CmdResult;
    return *this;
}
CtiCCCapBank& CtiCCCapBank::setVerificationState(CCBANKVSTATE verificationState)
{
    _verificationState = verificationState;
    return *this;
}


CtiCCCapBank& CtiCCCapBank::setPreviousVerificationControlStatus(LONG status)
{
    if (status != _prevVerificationControlStatus)
    {
        _dirty = TRUE;
    }
    _prevVerificationControlStatus = status;
    return *this;

}
CtiCCCapBank& CtiCCCapBank::setAssumedOrigVerificationState(int assumedOrigCapBankPos)
{
    if (assumedOrigCapBankPos != _assumedOrigCapBankPos)
    {
        _dirty = TRUE;
    }
    _assumedOrigCapBankPos = assumedOrigCapBankPos;
    return *this;
}



CtiCCCapBank& CtiCCCapBank::initVerificationControlStatus()
{
   _verificationControlStatus = getControlStatus();
   return *this;
}

/*---------------------------------------------------------------------------
    setControlStatus

    Sets the control status of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatus(LONG status)
{

    if (_verificationFlag)
    {
        if( _verificationControlStatus != status )
        {
            /*{
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }*/
            _dirty = TRUE;
        }
        _verificationControlStatus = status;
        _controlstatus = status;       //temporarily here!!!

    }
    else
    {
        if( _controlstatus != status )
        {
            /*{
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }*/
            _dirty = TRUE;
        }
        _controlstatus = status;
    }

    if (_controlstatus == CtiCCCapBank::OpenPending ||
        _controlstatus == CtiCCCapBank::ClosePending)
    {
        {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " CB: " << getPAOId()<< " _controlStatus = ----Pending  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

    }
    return *this;
}

/*---------------------------------------------------------------------------
    setOperationAnalogPointId

    Sets the point id for the analog point that hold the number of number of
    operations performed on the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOperationAnalogPointId(LONG operationpointid)
{
    _operationanalogpointid = operationpointid;

    return *this;
}

/*---------------------------------------------------------------------------
    setTotalOperations

    Sets the number of operations performed on this capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setTotalOperations(LONG operations)
{
    if( _totaloperations != operations )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _totaloperations = operations;
    return *this;
}

/*---------------------------------------------------------------------------
    setLastStatusChangeTime

    Sets the timestamp of the last status point data msg sent by dispatch
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setLastStatusChangeTime(const RWDBDateTime& laststatuschangetime)
{
    if( _laststatuschangetime != laststatuschangetime )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _laststatuschangetime = laststatuschangetime;
    return *this;
}

/*---------------------------------------------------------------------------
    setTagsControlStatus

    Sets the tags of control status on the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setTagsControlStatus(LONG tags)
{
    if( _tagscontrolstatus != tags )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _tagscontrolstatus = tags;
    return *this;
}

/*---------------------------------------------------------------------------
    setOriginalFeederId

    Sets the original feeder id on the capbank for temp cap bank moves
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOriginalFeederId(LONG origfeeder)
{
    if( _originalfeederid != origfeeder )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _originalfeederid = origfeeder;
    return *this;
}

/*---------------------------------------------------------------------------
    setOriginalSwitchingOrder

    Sets the switching order on the capbank for temp cap bank moves
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOriginalSwitchingOrder(LONG origorder)
{
    if( _originalswitchingorder != origorder )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _originalswitchingorder = origorder;
    return *this;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCCapBank::restoreGuts(RWvistream& istrm)
{
    RWTime tempTime1;
    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag
    >> _parentId
    >> _alarminhibitflag
    >> _controlinhibitflag
    >> _operationalstate
    >> _controllertype
    >> _controldeviceid
    >> _controlpointid
    >> _banksize
    >> _typeofswitch
    >> _switchmanufacture
    >> _maplocationid
    >> _reclosedelay
    >> _controlorder
    >> _statuspointid
    >> _controlstatus
    >> _operationanalogpointid
    >> _totaloperations
    >> tempTime1
    >> _tagscontrolstatus
    >> _originalfeederid;
    //>> _originalswitchingorder;
    /*>> _assumedOrigCapBankPos
    >> _prevVerificationControlStatus
    >> _vCtrlIndex
    >> _additionalFlags;

    _verificationFlag = (_additionalFlags.data()[0]=='y'?TRUE:FALSE);
    _performingVerificationFlag = (_additionalFlags.data()[1]=='y'?TRUE:FALSE);
    _verificationDoneFlag = (_additionalFlags.data()[2]=='y'?TRUE:FALSE);
     */
    _laststatuschangetime = RWDBDateTime(tempTime1);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCCapBank::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
    << _paocategory
    << _paoclass
    << _paoname
    << _paotype
    << _paodescription
    << _disableflag
    << _parentId
    << _alarminhibitflag
    << _controlinhibitflag
    << _operationalstate
    << _controllertype
    << _controldeviceid
    << _controlpointid
    << _banksize
    << _typeofswitch
    << _switchmanufacture
    << _maplocationid
    << _reclosedelay
    << _controlorder
    << _statuspointid
    << _controlstatus
    << _operationanalogpointid
    << _totaloperations
    << _laststatuschangetime.rwtime()
    << _tagscontrolstatus
    << _originalfeederid;
    /*<< _originalswitchingorder;/*
    << _assumedOrigCapBankPos
    << _prevVerificationControlStatus
    << _vCtrlIndex
    << RWCString(_additionalFlags);

  */
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::operator=(const CtiCCCapBank& right)
{
    if( this != &right )
    {
        _paoid = right._paoid;
        _paocategory = right._paocategory;
        _paoclass = right._paoclass;
        _paoname = right._paoname;
        _paotype = right._paotype;
        _paodescription = right._paodescription;
        _disableflag = right._disableflag;
   //     _parentId = right._parentId;
        _alarminhibitflag = right._alarminhibitflag;
        _controlinhibitflag = right._controlinhibitflag;
        _operationalstate = right._operationalstate;
        _controllertype = right._controllertype;
        _controldeviceid = right._controldeviceid;
        _controlpointid = right._controlpointid;
        _banksize = right._banksize;
        _typeofswitch = right._typeofswitch;
        _switchmanufacture = right._switchmanufacture;
        _maplocationid = right._maplocationid;
        _reclosedelay = right._reclosedelay;
        _controlorder = right._controlorder;
        _statuspointid = right._statuspointid;
        _controlstatus = right._controlstatus;
        _operationanalogpointid = right._operationanalogpointid;
        _totaloperations = right._totaloperations;
        _laststatuschangetime = right._laststatuschangetime;
        _tagscontrolstatus = right._tagscontrolstatus;
        _originalfeederid = right._originalfeederid;
        _originalswitchingorder = right._originalswitchingorder;
        _assumedOrigCapBankPos = right._assumedOrigCapBankPos;
        _prevVerificationControlStatus = right._prevVerificationControlStatus;
        _vCtrlIndex = right._vCtrlIndex;
        _additionalFlags = right._additionalFlags;

    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCCapBank::operator==(const CtiCCCapBank& right) const
{
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCCapBank::operator!=(const CtiCCCapBank& right) const
{
    return getPAOId() != right.getPAOId();
}


/*---------------------------------------------------------------------------
    restore

    Restores self's state given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCCapBank::restore(RWDBReader& rdr)
{
    RWDBNullIndicator isNull;
    RWDBDateTime currentDateTime = RWDBDateTime();
    RWDBDateTime dynamicTimeStamp;
    RWCString tempBoolString;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> _paotype;
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    tempBoolString.toLower();
    setDisableFlag(tempBoolString=="y"?TRUE:FALSE);
    //rdr["parentid"] >> _parentId;
    rdr["alarminhibit"] >> tempBoolString;
    tempBoolString.toLower();
    setAlarmInhibitFlag(tempBoolString=="y"?TRUE:FALSE);
    rdr["controlinhibit"] >> tempBoolString;
    tempBoolString.toLower();
    setControlInhibitFlag(tempBoolString=="y"?TRUE:FALSE);
    rdr["operationalstate"] >> _operationalstate;
    rdr["controllertype"] >> _controllertype;
    rdr["controldeviceid"] >> _controldeviceid;
    rdr["controlpointid"] >> _controlpointid;
    rdr["banksize"] >> _banksize;
    rdr["typeofswitch"] >> _typeofswitch;
    rdr["switchmanufacture"] >> _switchmanufacture;
    rdr["maplocationid"] >> _maplocationid;
    rdr["reclosedelay"] >> _reclosedelay;
    rdr["controlorder"] >> _controlorder;

    setStatusPointId(0);
    setOperationAnalogPointId(0);

    rdr["controlstatus"] >> isNull;
    if( !isNull )
    {
        rdr["controlstatus"] >> _controlstatus;
        rdr["currentdailyoperations"] >> _totaloperations;
        rdr["laststatuschangetime"] >> _laststatuschangetime;
        rdr["tagscontrolstatus"] >> _tagscontrolstatus;
        rdr["ctitimestamp"] >> dynamicTimeStamp;
        rdr["originalfeederid"] >> _originalfeederid;
        rdr["originalswitchingorder"] >> _originalswitchingorder;
        rdr["assumedstartverificationstatus"] >> _assumedOrigCapBankPos;
        rdr["prevverificationcontrolstatus"] >> _prevVerificationControlStatus;
        rdr["verificationcontrolindex"] >> _vCtrlIndex;
        rdr["additionalflags"] >> _additionalFlags;
        _additionalFlags.toLower();

        _verificationFlag = (_additionalFlags.data()[0]=='y'?TRUE:FALSE);
        _performingVerificationFlag = (_additionalFlags.data()[1]=='y'?TRUE:FALSE);
        _verificationDoneFlag = (_additionalFlags.data()[2]=='y'?TRUE:FALSE);

        _insertDynamicDataFlag = FALSE;
        _dirty = FALSE;
    }
    else
    {
        //initialize dynamic data members
        setTotalOperations(0);
        setLastStatusChangeTime(gInvalidRWDBDateTime);
        setControlStatus(CtiCCCapBank::Open);
        setTagsControlStatus(0);
        setOriginalFeederId(0);
        setOriginalSwitchingOrder(0);
        setAssumedOrigVerificationState(CtiCCCapBank::Open);
        setPreviousVerificationControlStatus(CtiCCCapBank::Open);
        _additionalFlags = RWCString("NNNNNNNNNNNNNNNNNNNN");

        setVerificationFlag(FALSE);
        setPerformingVerificationFlag(FALSE);
        setVerificationDoneFlag(FALSE);
        

        _insertDynamicDataFlag = TRUE;
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }

    rdr["pointid"] >> isNull;
    if( !isNull )
    {
        LONG tempPointId = -1000;
        LONG tempPointOffset = -1000;
        RWCString tempPointType = "(none)";
        rdr["pointid"] >> tempPointId;
        rdr["pointoffset"] >> tempPointOffset;
        rdr["pointtype"] >> tempPointType;
        if( tempPointOffset == 1 )
        {
            if( resolvePointType(tempPointType) == StatusPointType )
            {//control status point
                setStatusPointId(tempPointId);
            }
            else if( resolvePointType(tempPointType) == AnalogPointType )
            {//daily operations point
                setOperationAnalogPointId(tempPointId);
            }
            else
            {//undefined cap bank point
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Undefined Cap Bank point type: " << tempPointType << " in: " << __FILE__ << " at: " << __LINE__ << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Undefined Cap Bank point offset: " << tempPointOffset << " in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}
void CtiCCCapBank::restoreCapBankTableValues(RWDBReader& rdr)
{
    RWDBNullIndicator isNull;
    RWDBDateTime currentDateTime = RWDBDateTime();
    RWDBDateTime dynamicTimeStamp;
    RWCString tempBoolString;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> _paotype;
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    tempBoolString.toLower();
    setDisableFlag(tempBoolString=="y"?TRUE:FALSE);
    //rdr["alarminhibit"] >> tempBoolString;
    //tempBoolString.toLower();
    //setAlarmInhibitFlag(tempBoolString=="y"?TRUE:FALSE);
    //rdr["controlinhibit"] >> tempBoolString;
    //tempBoolString.toLower();
    //setControlInhibitFlag(tempBoolString=="y"?TRUE:FALSE);
    rdr["operationalstate"] >> _operationalstate;
    rdr["controllertype"] >> _controllertype;
    rdr["controldeviceid"] >> _controldeviceid;
    rdr["controlpointid"] >> _controlpointid;
    rdr["banksize"] >> _banksize;
    rdr["typeofswitch"] >> _typeofswitch;
    rdr["switchmanufacture"] >> _switchmanufacture;
    rdr["maplocationid"] >> _maplocationid;
    rdr["reclosedelay"] >> _reclosedelay;
    //rdr["controlorder"] >> _controlorder;



    //setAlarmInhibitFlag(false);
  //  setControlInhibitFlag(false);
  //  _controlorder = 0;
  //  setStatusPointId(0);
   // setOperationAnalogPointId(0);

 //initialize dynamic data members
  //  setTotalOperations(0);
  //  setLastStatusChangeTime(gInvalidRWDBDateTime);
   // setControlStatus(CtiCCCapBank::Open);
  //  setTagsControlStatus(0);
   // setOriginalFeederId(0);
 //   setOriginalSwitchingOrder(0);

  //  setVerificationFlag(FALSE);
  //  _additionalFlags = RWCString("NNNNNNNNNNNNNNNNNNNN");
 //


    _insertDynamicDataFlag = FALSE;
    /*{
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }*/
    _dirty = TRUE;

}

void CtiCCCapBank::setDynamicData(RWDBReader& rdr)
{

    RWDBDateTime dynamicTimeStamp;
    rdr["controlstatus"] >> _controlstatus;
    rdr["currentdailyoperations"] >> _totaloperations;
    rdr["laststatuschangetime"] >> _laststatuschangetime;
    rdr["tagscontrolstatus"] >> _tagscontrolstatus;
    rdr["ctitimestamp"] >> dynamicTimeStamp;
    rdr["originalfeederid"] >> _originalfeederid;
    rdr["originalswitchingorder"] >> _originalswitchingorder;
    rdr["assumedstartverificationstatus"] >> _assumedOrigCapBankPos;
    rdr["prevverificationcontrolstatus"] >> _prevVerificationControlStatus;
    rdr["verificationcontrolindex"] >> _vCtrlIndex;

    rdr["additionalflags"] >> _additionalFlags;
    _additionalFlags.toLower();
    _verificationFlag = (_additionalFlags.data()[0]=='y'?TRUE:FALSE);
    _performingVerificationFlag = (_additionalFlags.data()[1]=='y'?TRUE:FALSE);
    _verificationDoneFlag = (_additionalFlags.data()[2]=='y'?TRUE:FALSE);

    _insertDynamicDataFlag = FALSE;
    _dirty = FALSE;

}


/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields.
---------------------------------------------------------------------------*/
CtiCCCapBank* CtiCCCapBank::replicate() const
{
    return(new CtiCCCapBank(*this));
}

/*---------------------------------------------------------------------------
    compareTo

    Used for ordering cap banks within a feeder by control order.
---------------------------------------------------------------------------*/
int CtiCCCapBank::compareTo(const RWCollectable* right) const
{
    return _controlorder == ((CtiCCCapBank*)right)->getControlOrder() ? 0 : (_controlorder > ((CtiCCCapBank*)right)->getControlOrder() ? 1 : -1);
}

/*---------------------------------------------------------------------------
    isDirty

    Returns the dirty flag of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCCapBank::isDirty() const
{
    return _dirty;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCCapBank::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,RWDBDateTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc cap bank.
---------------------------------------------------------------------------*/
void CtiCCCapBank::dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{
    {
        RWDBTable dynamicCCCapBankTable = getDatabase().table( "dynamiccccapbank" );
        if( !_insertDynamicDataFlag )
        {

            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (_verificationFlag?'Y':'N');
            addFlags[1] = (_performingVerificationFlag?'Y':'N');
            addFlags[2] = (_verificationDoneFlag?'Y':'N');
            _additionalFlags = RWCString(RWCString(*addFlags) + RWCString(*(addFlags+1)) + RWCString(*(addFlags+2)) + RWCString(*(addFlags + 3), 17));

            RWDBUpdater updater = dynamicCCCapBankTable.updater();

            updater.where(dynamicCCCapBankTable["capbankid"]==_paoid);

            updater << dynamicCCCapBankTable["controlstatus"].assign( _controlstatus )
            << dynamicCCCapBankTable["currentdailyoperations"].assign( _totaloperations )
            << dynamicCCCapBankTable["laststatuschangetime"].assign( (RWDBDateTime)_laststatuschangetime )
            << dynamicCCCapBankTable["tagscontrolstatus"].assign( _tagscontrolstatus )
            << dynamicCCCapBankTable["ctitimestamp"].assign((RWDBDateTime)currentDateTime)
            << dynamicCCCapBankTable["originalfeederid"].assign( _originalfeederid )
            << dynamicCCCapBankTable["originalswitchingorder"].assign( _originalswitchingorder )
            << dynamicCCCapBankTable["assumedstartverificationstatus"].assign(_assumedOrigCapBankPos)
            << dynamicCCCapBankTable["prevverificationcontrolstatus"].assign(_prevVerificationControlStatus)
            << dynamicCCCapBankTable["verificationcontrolindex"].assign(_vCtrlIndex)
            << dynamicCCCapBankTable["additionalflags"].assign(_additionalFlags);




            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
            }*/
            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Inserted Cap Bank into DynamicCCCapBank: " << getPAOName() << endl;
            }
            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};

            RWDBInserter inserter = dynamicCCCapBankTable.inserter();

            inserter << _paoid
            << _controlstatus
            << _totaloperations
            << _laststatuschangetime
            << _tagscontrolstatus
            << currentDateTime
            << _originalfeederid
            << _originalswitchingorder
            << _assumedOrigCapBankPos
            << _prevVerificationControlStatus
            << _vCtrlIndex
            << RWCString(*addFlags, 20);

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << inserter.asString().data() << endl;
            }

            inserter.execute( conn );

            if(inserter.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _insertDynamicDataFlag = FALSE;
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << inserter.asString() << endl;
                }
            }
        }
    }
}

/* Public Static members */
const RWCString CtiCCCapBank::SwitchedOperationalState = "Switched";
const RWCString CtiCCCapBank::FixedOperationalState = "Fixed";
const RWCString CtiCCCapBank::UninstalledState = "Uninstalled";

int CtiCCCapBank::Open = STATEZERO;
int CtiCCCapBank::Close = STATEONE;
int CtiCCCapBank::OpenQuestionable = STATETWO;
int CtiCCCapBank::CloseQuestionable = STATETHREE;
int CtiCCCapBank::OpenFail = STATEFOUR;
int CtiCCCapBank::CloseFail = STATEFIVE;
int CtiCCCapBank::OpenPending = STATESIX;
int CtiCCCapBank::ClosePending = STATESEVEN;
