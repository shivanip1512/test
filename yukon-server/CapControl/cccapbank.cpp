/*---------------------------------------------------------------------------
        Filename:  cccapbank.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCCapBank.
                        CtiCCCapBank maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "precompiled.h"

#include "dbaccess.h"
#include "cccapbank.h"
#include "ccid.h"
#include "pointdefs.h"
#include "logger.h"
#include "resolvers.h"
#include "database_reader.h"
#include "database_writer.h"
#include "utility.h"
#include "PointResponseDao.h"
#include "DatabaseDaoFactory.h"
#include "PointResponseManager.h"

using namespace Cti::CapControl;
using namespace std;

using Database::DatabaseDaoFactory;

extern unsigned long _CC_DEBUG;
extern bool _USE_FLIP_FLAG;

RWDEFINE_COLLECTABLE( CtiCCCapBank, CTICCCAPBANK_ID )


/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCCapBank::CtiCCCapBank() :
_parentId(0),
_alarminhibitflag(false),
_controlinhibitflag(false),
_maxdailyops(0),
_currentdailyoperations(0),
_maxopsdisableflag(false),
_controldeviceid(0),
_controlpointid(0),
_banksize(0),
_reclosedelay(0),
_controlorder(0),
_triporder(0),
_closeorder(0),
_statuspointid(0),
_controlstatus(0),
_operationanalogpointid(0),
_totaloperations(0),
_tagscontrolstatus(0),
_originalfeederid(0),
_originalswitchingorder(0),
_originalcloseorder(0),
_originaltriporder(0),
_verificationControlStatus(0),
_selectedForVerificationFlag(false),
_vCtrlIndex(0),
_retryFlag(false),
_prevVerificationControlStatus(0),
_assumedOrigCapBankPos(0),
_verificationFlag(false),
_performingVerificationFlag(false),
_verificationDoneFlag(false),
_retryOpenFailedFlag(false),
_retryCloseFailedFlag(false),
_ovUvDisabledFlag(false),
_maxDailyOpsHitFlag(false),
_controlStatusPartialFlag(false),
_controlStatusSignificantFlag(false),
_controlStatusAbnQualityFlag(false),
_controlStatusFailFlag(false),
_controlStatusCommFailFlag(false),
_controlStatusNoControlFlag(false),
_controlStatusUnSolicitedFlag(false),
_reEnableOvUvFlag(false),
_localControlFlag(false),
_controlRecentlySentFlag(false),
_porterRetFailFlag(false),
_unsolicitedPendingFlag(false),
_udpPortNumber(0),
_reportedCBCLastControlReason(0),
_reportedCBCState(0),
_ignoreFlag(false),
_ignoreReason(0),
_controlStatusQuality(false),
_actionId(0),
_insertDynamicDataFlag(true),
_dirty(true)
{
    _twoWayPoints = NULL;
    _ovuvSituationFlag = false;
}

CtiCCCapBank::CtiCCCapBank(Cti::RowReader& rdr) :
_parentId(0)
{
    restore(rdr);
     _monitorPoint.clear();
     _twoWayPoints = NULL;
     _ovuvSituationFlag = false;
     _operationStats.setPAOId(getPaoId());
     _confirmationStats.setPAOId(getPaoId());
     _originalParent.setPAOId(getPaoId());
}

CtiCCCapBank::CtiCCCapBank(const CtiCCCapBank& cap)
{
    operator=(cap);
    //_twoWayPoints = NULL;
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCCapBank::~CtiCCCapBank()
{
    try
    {
        if (_twoWayPoints != NULL)
        {
            delete _twoWayPoints;
            _twoWayPoints = NULL;
        }
    }
    catch (...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}
CtiCCTwoWayPoints* CtiCCCapBank::getTwoWayPoints()
{
    if ( _twoWayPoints == NULL )
          _twoWayPoints = new CtiCCTwoWayPoints(_controldeviceid, _controlDeviceType);

    return _twoWayPoints;

}

CtiCCOperationStats& CtiCCCapBank::getOperationStats()
{
    return _operationStats;
}

CtiCCConfirmationStats& CtiCCCapBank::getConfirmationStats()
{
    return _confirmationStats;
}

CtiCCOriginalParent& CtiCCCapBank::getOriginalParent()
{
    return _originalParent;
}

/*---------------------------------------------------------------------------
    getIPAddress

    Returns the pao description of the cap bank
---------------------------------------------------------------------------*/
const string& CtiCCCapBank::getIpAddress() const
{
    return _ipAddress;
}
long CtiCCCapBank::getUDPPort() const
{
    return _udpPortNumber;
}

long CtiCCCapBank::getReportedCBCState() const
{
    return _reportedCBCState;
}

long CtiCCCapBank::getReportedCBCLastControlReason() const
{
    return _reportedCBCLastControlReason;
}

const string&  CtiCCCapBank::getPartialPhaseInfo() const
{
    return _partialPhaseInfo;
}

const CtiTime& CtiCCCapBank::getReportedCBCStateTime() const
{
    return _reportedCBCStateTime;
}

bool CtiCCCapBank::getIgnoreFlag() const
{
    return _ignoreFlag;
}


long CtiCCCapBank::getIgnoredReason() const
{
    return _ignoreReason;
}

const CtiTime& CtiCCCapBank::getIgnoreReasonTimeUpdated() const
{
    return _ignoreReasonTimeUpdated;
}

const CtiTime& CtiCCCapBank::getIgnoreIndicatorTimeUpdated() const
{
    return _ignoreIndicatorTimeUpdated;
}

const CtiTime& CtiCCCapBank::getUnsolicitedChangeTimeUpdated() const
{
    return _unsolicitedChangeTimeUpdated;
}

const string& CtiCCCapBank::getBeforeVarsString() const
{
    return _sBeforeVars;
}

const string& CtiCCCapBank::getAfterVarsString() const
{
    return _sAfterVars;
}

const string& CtiCCCapBank::getPercentChangeString() const
{
    return _sPercentChange;
}
long CtiCCCapBank::getActionId() const
{
    if (_actionId != -1)
        return _actionId;
    else
        return CCEventActionIdGen(getStatusPointId());
}

/*---------------------------------------------------------------------------
    getParentId

    Returns the parentID (feederID) of the cap bank
---------------------------------------------------------------------------*/
long CtiCCCapBank::getParentId() const
{
    return _parentId;
}

/*---------------------------------------------------------------------------
    getMaxDailyOperation

    Returns the max daily operations of the cap bank
---------------------------------------------------------------------------*/
long CtiCCCapBank::getMaxDailyOps() const
{
    return _maxdailyops;
}
/*---------------------------------------------------------------------------
    getCurrentDailyOperations

    Returns the current daily operations of the cap bank
---------------------------------------------------------------------------*/
long CtiCCCapBank::getCurrentDailyOperations() const
{
    return _currentdailyoperations;
}

/*---------------------------------------------------------------------------
    getMaxOperationDisableFlag

    Returns the max operation disable flag for the cap bank
---------------------------------------------------------------------------*/
bool CtiCCCapBank::getMaxOpsDisableFlag() const
{
    return _maxopsdisableflag;
}

/*---------------------------------------------------------------------------
    getAlarmInhibitFlag

    Returns the alarm inhibit of the cap bank
---------------------------------------------------------------------------*/
bool CtiCCCapBank::getAlarmInhibitFlag() const
{
    return _alarminhibitflag;
}

/*---------------------------------------------------------------------------
    getControlInhibitFlag

    Returns the control inhibit of the cap bank
---------------------------------------------------------------------------*/
bool CtiCCCapBank::getControlInhibitFlag() const
{
    return _controlinhibitflag;
}

/*---------------------------------------------------------------------------
    getOperationalState

    Returns the operational state of the cap bank
---------------------------------------------------------------------------*/
const string& CtiCCCapBank::getOperationalState() const
{
    return _operationalstate;
}

/*---------------------------------------------------------------------------
    getControllerType

    Returns the controller type of the cap bank
---------------------------------------------------------------------------*/
const string& CtiCCCapBank::getControllerType() const
{
    return _controllertype;
}

/*---------------------------------------------------------------------------
    getControlDeviceId

    Returns the control device id of the cap bank
---------------------------------------------------------------------------*/
long CtiCCCapBank::getControlDeviceId() const
{
    return _controldeviceid;
}

/*---------------------------------------------------------------------------
    getControlPointId

    Returns the control point id of the cap bank
---------------------------------------------------------------------------*/
long CtiCCCapBank::getControlPointId() const
{
    return _controlpointid;
}


const string& CtiCCCapBank::getControlDeviceType() const
{
    return _controlDeviceType;
}
/*---------------------------------------------------------------------------
    getBankSize

    Returns the bank size of the cap bank
---------------------------------------------------------------------------*/
long CtiCCCapBank::getBankSize() const
{
    return _banksize;
}

/*---------------------------------------------------------------------------
    getTypeOfSwitch

    Returns the type of switch of the cap bank
---------------------------------------------------------------------------*/
const string& CtiCCCapBank::getTypeOfSwitch() const
{
    return _typeofswitch;
}

/*---------------------------------------------------------------------------
    getSwitchManufacture

    Returns the switch manufacture of the cap bank
---------------------------------------------------------------------------*/
const string& CtiCCCapBank::getSwitchManufacture() const
{
    return _switchmanufacture;
}

/*---------------------------------------------------------------------------
    getMapLocationId

    Returns the map location id of the cap bank
---------------------------------------------------------------------------*/
const string& CtiCCCapBank::getMapLocationId() const
{
    return _maplocationid;
}

/*---------------------------------------------------------------------------
    getRecloseDelay

    Returns the Reclose Delay of the cap bank
---------------------------------------------------------------------------*/
long CtiCCCapBank::getRecloseDelay() const
{
    return _reclosedelay;
}

/*---------------------------------------------------------------------------
    getControlOrder

    Returns the control order of the cap bank in the list of a feeder
---------------------------------------------------------------------------*/
float CtiCCCapBank::getControlOrder() const
{
    return _controlorder;
}

/*---------------------------------------------------------------------------
    getTripOrder

    Returns the trip order of the cap bank in the list of a feeder
---------------------------------------------------------------------------*/
float CtiCCCapBank::getTripOrder() const
{
    return _triporder;
}

/*---------------------------------------------------------------------------
    getCloseOrder

    Returns the close order of the cap bank in the list of a feeder
---------------------------------------------------------------------------*/
float CtiCCCapBank::getCloseOrder() const
{
    return _closeorder;
}

/*---------------------------------------------------------------------------
    getStatusPointId

    Returns the status point id of the cap bank
---------------------------------------------------------------------------*/
long CtiCCCapBank::getStatusPointId() const
{
    return _statuspointid;
}

/*---------------------------------------------------------------------------
    getVerificationFlag

    Returns the verification flag of the cap bank
---------------------------------------------------------------------------*/
bool CtiCCCapBank::getVerificationFlag() const
{
    return _verificationFlag;
}

bool CtiCCCapBank::getPerformingVerificationFlag() const
{
    return _performingVerificationFlag;
}

bool CtiCCCapBank::getVerificationDoneFlag() const
{
    return _verificationDoneFlag;
}

bool CtiCCCapBank::getPorterRetFailFlag() const
{
    return _porterRetFailFlag;
}

bool CtiCCCapBank::getUnsolicitedPendingFlag() const
{
    return _unsolicitedPendingFlag;
}

bool CtiCCCapBank::getRetryOpenFailedFlag() const
{
    return _retryOpenFailedFlag;
}

bool CtiCCCapBank::getRetryCloseFailedFlag() const
{
    return _retryCloseFailedFlag;
}
bool CtiCCCapBank::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}
bool CtiCCCapBank::getLocalControlFlag() const
{
    return _localControlFlag;
}
bool CtiCCCapBank::getControlRecentlySentFlag() const
{
    return _controlRecentlySentFlag;
}

bool CtiCCCapBank::getMaxDailyOpsHitFlag() const
{
    return _maxDailyOpsHitFlag;
}
bool CtiCCCapBank::getOvUvSituationFlag() const
{
    return _ovuvSituationFlag;
}
bool CtiCCCapBank::getControlStatusPartialFlag() const
{
    return _controlStatusPartialFlag;
}
bool CtiCCCapBank::getControlStatusSignificantFlag() const
{
    return _controlStatusSignificantFlag;
}
bool CtiCCCapBank::getControlStatusAbnQualityFlag() const
{
    return _controlStatusAbnQualityFlag;
}
bool CtiCCCapBank::getControlStatusFailFlag() const
{
    return _controlStatusFailFlag;
}
bool CtiCCCapBank::getControlStatusCommFailFlag() const
{
    return _controlStatusCommFailFlag;
}
bool CtiCCCapBank::getControlStatusNoControlFlag() const
{
    return _controlStatusNoControlFlag;
}
bool CtiCCCapBank::getControlStatusUnSolicitedFlag() const
{
    return _controlStatusUnSolicitedFlag;
}


long CtiCCCapBank::getControlStatusQuality() const
{
    return _controlStatusQuality;
}
bool CtiCCCapBank::getReEnableOvUvFlag() const
{
    return _reEnableOvUvFlag;
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
    getAssumedOrigVerificationState

    Returns the AssumedOrigVerificationState of the cap bank
---------------------------------------------------------------------------*/
int CtiCCCapBank::getAssumedOrigVerificationState() const
{
    long controlStatus = getControlStatus();

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
long CtiCCCapBank::getControlStatus() const
{
   return _controlstatus;
}

bool CtiCCCapBank::isPendingStatus()
{
    return (getControlStatus() == CtiCCCapBank::OpenPending ||
            getControlStatus() == CtiCCCapBank::ClosePending);
}

bool CtiCCCapBank::isFailedOrQuestionableStatus()
{
    return (isQuestionableStatus() || isFailedStatus());
}


bool CtiCCCapBank::isQuestionableStatus()
{
    return ( getControlStatus() ==  CtiCCCapBank::OpenQuestionable ||
             getControlStatus() ==  CtiCCCapBank::CloseQuestionable);

}
bool CtiCCCapBank::isFailedStatus()
{
    return (getControlStatus() == CtiCCCapBank::OpenFail ||
            getControlStatus() ==  CtiCCCapBank::CloseFail );

}
/*---------------------------------------------------------------------------
    getControlStatus

    Returns the control status of the cap bank
---------------------------------------------------------------------------*/
string CtiCCCapBank::getControlStatusText() const
{
    string retVal = "Undefined";
     if (_controlstatus == CtiCCCapBank::Open )
         retVal = "Open";
     else if (_controlstatus == CtiCCCapBank::OpenQuestionable )
         retVal = "OpenQuestionable";
     else if (_controlstatus == CtiCCCapBank::OpenFail )
         retVal = "OpenFail";
     else if (_controlstatus == CtiCCCapBank::OpenPending )
         retVal = "OpenPending";
     else if (_controlstatus == CtiCCCapBank::Close )
         retVal = "Close";
     else if (_controlstatus == CtiCCCapBank::CloseQuestionable )
         retVal = "CloseQuestionable";
     else if (_controlstatus == CtiCCCapBank::CloseFail )
         retVal = "CloseFail";
     else if (_controlstatus == CtiCCCapBank::ClosePending )
         retVal = "ClosePending";

    return retVal;
}

/*---------------------------------------------------------------------------
    getOperationAnalogPointId

    Returns the point id of the analog that holds the number of operations
    on the cap bank
---------------------------------------------------------------------------*/
long CtiCCCapBank::getOperationAnalogPointId() const
{
    return _operationanalogpointid;
}

/*---------------------------------------------------------------------------
    getTotalOperations

    Returns the number operations performed on the cap bank
---------------------------------------------------------------------------*/
long CtiCCCapBank::getTotalOperations() const
{
    return _totaloperations;
}

/*---------------------------------------------------------------------------
    getLastStatusChangeTime

    Returns the timestamp of the last status point data msg sent by dispatch
---------------------------------------------------------------------------*/
const CtiTime& CtiCCCapBank::getLastStatusChangeTime() const
{
    return _laststatuschangetime;
}

/*---------------------------------------------------------------------------
    getTagsControlStatus

    Returns the tags of control status on the cap bank
---------------------------------------------------------------------------*/
long CtiCCCapBank::getTagsControlStatus() const
{
    return _tagscontrolstatus;
}

/*---------------------------------------------------------------------------
    setParentId

    Sets the parentId (feederId) of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setParentId(long parentId)
{
    _parentId = parentId;

    return *this;
}

/*---------------------------------------------------------------------------
    setAlarmInhibitFlag

    Sets the alarm inhibit of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setAlarmInhibitFlag(bool alarminhibit)
{
    _alarminhibitflag = alarminhibit;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlInhibitFlag

    Sets the control inhibit of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlInhibitFlag(bool controlinhibit)
{
    _controlinhibitflag = controlinhibit;

    return *this;
}


/*---------------------------------------------------------------------------
    setMaxDailyOperation

    Sets the max daily operations of the cap bank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setMaxDailyOperation(long maxdailyops)
{
    _maxdailyops = maxdailyops;

    return *this;
}


/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the cap bank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setCurrentDailyOperations(long operations)
{
    if( _currentdailyoperations != operations )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _currentdailyoperations = operations;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxOperationDisableFlag

    Sets the max operation disable flag for the cap bank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setMaxOpsDisableFlag(bool maxopsdisable)
{
    _maxopsdisableflag = maxopsdisable;

    return *this;
}


/*---------------------------------------------------------------------------
    setOperationalState

    Sets the operational state of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOperationalState(const string& operational)
{
    _operationalstate = operational;

    return *this;
}

/*---------------------------------------------------------------------------
    setOperationalState

    Sets the operational state of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOperationalState(int value)
{
    _operationalstate = convertOperationalState(value);

    return *this;
}
/*---------------------------------------------------------------------------
    setControllerType

    Sets the controller type of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControllerType(const string& controllertype)
{
    _controllertype = controllertype;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlDeviceId

    Sets the control device id of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlDeviceId(long controldevice)
{
    _controldeviceid = controldevice;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlPointId

    Sets the control point id of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlPointId(long controlpoint)
{
    _controlpointid = controlpoint;

    return *this;
}



CtiCCCapBank& CtiCCCapBank::setControlDeviceType(const string& controlDeviceType)
{
    _controlDeviceType = controlDeviceType;

    return *this;

}
/*---------------------------------------------------------------------------
    setBankSize

    Sets the bank size of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setBankSize(long size)
{
    _banksize = size;

    return *this;
}

/*---------------------------------------------------------------------------
    setTypeOfSwitch

    Sets the type of switch of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setTypeOfSwitch(const string& switchtype)
{
    _typeofswitch = switchtype;

    return *this;
}

/*---------------------------------------------------------------------------
    setSwitchManufacture

    Sets the switch manufacture of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setSwitchManufacture(const string& manufacture)
{
    _switchmanufacture = manufacture;

    return *this;
}

/*---------------------------------------------------------------------------
    setMapLocationId

    Sets the map location id of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setMapLocationId(const string& maplocation)
{
    _maplocationid = maplocation;

    return *this;
}

/*---------------------------------------------------------------------------
    setRecloseDelay

    Sets the RecloseDelay of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setRecloseDelay(long reclose)
{
    _reclosedelay = reclose;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlOrder

    Sets the control order of the capbank in the list of the parent feeder
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlOrder(float order)
{
    _controlorder = order;

    return *this;
}

/*---------------------------------------------------------------------------
    setTripOrder

    Sets the trip order of the capbank in the list of the parent feeder
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setTripOrder(float order)
{
    _triporder = order;

    return *this;
}

/*---------------------------------------------------------------------------
    setCloseOrder

    Sets the close order of the capbank in the list of the parent feeder
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setCloseOrder(float order)
{
    _closeorder = order;

    return *this;
}



/*---------------------------------------------------------------------------
    setStatusPointId

    Sets the status point id of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setStatusPointId(long statuspoint)
{
    _statuspointid = statuspoint;

    return *this;
}
/*---------------------------------------------------------------------------
    setVerificationFlag

    Sets the verification flag, b4 capbank is exercised in the verification routine
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setVerificationFlag(bool verificationFlag)
{
    if (verificationFlag)
    {
        setVerificationDoneFlag(false);
        setVCtrlIndex(0);
    }

    if (_verificationFlag != verificationFlag)
    {
        _dirty = true;
    }
    _verificationFlag = verificationFlag;

    return *this;
}


CtiCCCapBank& CtiCCCapBank::setSelectedForVerificationFlag(bool individualVerificationFlag)
{
    _selectedForVerificationFlag = individualVerificationFlag;
    return *this;
}
bool CtiCCCapBank::isSelectedForVerification( ) const
{
    return _selectedForVerificationFlag;
}


CtiCCCapBank& CtiCCCapBank::setPerformingVerificationFlag(bool performingVerificationFlag)
{
    if (_performingVerificationFlag != performingVerificationFlag)
        _dirty = true;
    _performingVerificationFlag = performingVerificationFlag;

    return *this;
}
CtiCCCapBank& CtiCCCapBank::setVerificationDoneFlag(bool verificationDoneFlag)
{
    if(_verificationDoneFlag != verificationDoneFlag)
        _dirty = true;
    _verificationDoneFlag = verificationDoneFlag;

    return *this;
}

CtiCCCapBank& CtiCCCapBank::setPorterRetFailFlag(bool flag)
{
    if (_porterRetFailFlag != flag)
    {
        _dirty = true;
    }
    _porterRetFailFlag = flag;
    return *this;
}
CtiCCCapBank& CtiCCCapBank::setUnsolicitedPendingFlag(bool flag)
{
    if (_unsolicitedPendingFlag != flag)
    {
        _dirty = true;
    }
    _unsolicitedPendingFlag = flag;
    return *this;
}

/*---------------------------------------------------------------------------
    setRetryOpenFailedFlag

    Sets the retry failed flag, b4 if a capbank has been tried after a failed state..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setRetryOpenFailedFlag(bool retryOpenFailedFlag)
{

    if (_retryOpenFailedFlag != retryOpenFailedFlag)
    {
        _dirty = true;
    }
    _retryOpenFailedFlag = retryOpenFailedFlag;

    return *this;
}
/*---------------------------------------------------------------------------
    setRetryCloseFailedFlag

    Sets the retry failed flag, b4 if a capbank has been tried after a failed state..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setRetryCloseFailedFlag(bool retryCloseFailedFlag)
{

    if (_retryCloseFailedFlag != retryCloseFailedFlag)
    {
        _dirty = true;
    }
    _retryCloseFailedFlag = retryCloseFailedFlag;

    return *this;
}
/*---------------------------------------------------------------------------
    setOvUvDisabledFlag

    Sets the ovUvDisabledFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOvUvDisabledFlag(bool ovUvDisabledFlag)
{

    if (_ovUvDisabledFlag != ovUvDisabledFlag)
    {
        _dirty = true;
    }
    _ovUvDisabledFlag = ovUvDisabledFlag;

    return *this;
}

/*---------------------------------------------------------------------------
    setLocalControlFlag

    Sets the LocalControlFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setLocalControlFlag(bool localControlFlag)
{

    if (_localControlFlag != localControlFlag)
    {
        _dirty = true;
    }
    _localControlFlag = localControlFlag;

    return *this;
}

/*---------------------------------------------------------------------------
    setOvUvDisabledFlag

    Sets the ovUvDisabledFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOvUvSituationFlag(bool ovUvSituationFlag)
{

    if (_ovuvSituationFlag != ovUvSituationFlag)
    {
        _dirty = true;
    }
    _ovuvSituationFlag = ovUvSituationFlag;

    return *this;
}

/*---------------------------------------------------------------------------
    setMaxDailyOpsHitFlag

    Sets the maxDailyOpsHitFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setMaxDailyOpsHitFlag(bool flag)
{

    if (_maxDailyOpsHitFlag != flag)
    {
        _dirty = true;
    }
    _maxDailyOpsHitFlag = flag;

    return *this;
}
/*---------------------------------------------------------------------------
    setControlStatusPartialFlag(

    Sets the ControlStatusPartialFlag( ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusPartialFlag(bool flag)
{

    if (_controlStatusPartialFlag != flag)
    {
        _dirty = true;
    }
    _controlStatusPartialFlag = flag;

    return *this;
}
/*---------------------------------------------------------------------------
    setControlStatusSignificantFlag

    Sets the ControlStatusPartialFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusSignificantFlag(bool flag)
{

    if (_controlStatusSignificantFlag != flag)
    {
        _dirty = true;
    }
    _controlStatusSignificantFlag = flag;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlStatusAbnQualityFlag

    Sets the ControlStatusAbnQualityFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusAbnQualityFlag(bool flag)
{

    if (_controlStatusAbnQualityFlag != flag)
    {
        _dirty = true;
    }
    _controlStatusAbnQualityFlag = flag;

    return *this;
}
/*---------------------------------------------------------------------------
    setControlStatusFailFlag

    Sets the ControlStatusFailFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusFailFlag(bool flag)
{

    if (_controlStatusFailFlag != flag)
    {
        _dirty = true;
    }
    _controlStatusFailFlag = flag;

    return *this;
}
/*---------------------------------------------------------------------------
    setControlStatusCommFailFlag

    Sets the ControlStatusCommFailFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusCommFailFlag(bool flag)
{

    if (_controlStatusCommFailFlag != flag)
    {
        _dirty = true;
    }
    _controlStatusCommFailFlag = flag;

    return *this;
}
/*---------------------------------------------------------------------------
    setControlStatusNoControlFlag

    Sets the ControlStatusNoControlFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusNoControlFlag(bool flag)
{

    if (_controlStatusNoControlFlag != flag)
    {
        _dirty = true;
    }
    _controlStatusNoControlFlag = flag;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlStatusNoControlFlag

    Sets the ControlStatusNoControlFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusUnSolicitedFlag(bool flag)
{

    if (_controlStatusUnSolicitedFlag != flag)
    {
        _dirty = true;
    }
    _controlStatusUnSolicitedFlag = flag;

    return *this;
}

/*---------------------------------------------------------------------------
    setReEnableOvUvFlag

    Sets the ReEnableOvUvFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setReEnableOvUvFlag(bool flag)
{

    if (_reEnableOvUvFlag != flag)
    {
        _dirty = true;
    }
    _reEnableOvUvFlag = flag;

    return *this;
}


/*---------------------------------------------------------------------------
    setControlStatusQuality

    Sets the ControlStatusQuality ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusQuality(CtiCCControlStatusQaulity quality, string partialPhaseInfo)
{

    _controlStatusQuality = quality;
    _partialPhaseInfo = partialPhaseInfo;
    switch (quality)
    {
        case CC_Partial:
        {
            setControlStatusPartialFlag(true);
            setControlStatusSignificantFlag(false);
            setControlStatusAbnQualityFlag(false);
            setControlStatusFailFlag(false);
            setControlStatusCommFailFlag(false);
            setControlStatusNoControlFlag(false);
            setControlStatusUnSolicitedFlag(false);
            break;
        }

        case CC_Significant:
        {
            setControlStatusPartialFlag(false);
            setControlStatusSignificantFlag(true);
            setControlStatusAbnQualityFlag(false);
            setControlStatusFailFlag(false);
            setControlStatusCommFailFlag(false);
            setControlStatusNoControlFlag(false);
            setControlStatusUnSolicitedFlag(false);
            break;

        }
        case CC_AbnormalQuality:
        {
            setControlStatusPartialFlag(false);
            setControlStatusSignificantFlag(false);
            setControlStatusAbnQualityFlag(true);
            setControlStatusFailFlag(false);
            setControlStatusCommFailFlag(false);
            setControlStatusNoControlFlag(false);
            setControlStatusUnSolicitedFlag(false);
            break;
        }
        case CC_Fail:
        {
            setControlStatusPartialFlag(false);
            setControlStatusSignificantFlag(false);
            setControlStatusAbnQualityFlag(false);
            setControlStatusFailFlag(true);
            setControlStatusCommFailFlag(false);
            setControlStatusNoControlFlag(false);
            setControlStatusUnSolicitedFlag(false);
            break;
        }
        case CC_CommFail:
        {
            setControlStatusPartialFlag(false);
            setControlStatusSignificantFlag(false);
            setControlStatusAbnQualityFlag(false);
            setControlStatusFailFlag(false);
            setControlStatusCommFailFlag(true);
            setControlStatusNoControlFlag(false);
            setControlStatusUnSolicitedFlag(false);
            break;
        }
        case CC_NoControl:
        {
            setControlStatusPartialFlag(false);
            setControlStatusSignificantFlag(false);
            setControlStatusAbnQualityFlag(false);
            setControlStatusFailFlag(false);
            setControlStatusCommFailFlag(false);
            setControlStatusNoControlFlag(true);
            setControlStatusUnSolicitedFlag(false);
            break;
        }
        case CC_UnSolicited:
        {
            setControlStatusPartialFlag(false);
            setControlStatusSignificantFlag(false);
            setControlStatusAbnQualityFlag(false);
            setControlStatusFailFlag(false);
            setControlStatusCommFailFlag(false);
            setControlStatusNoControlFlag(false);
            setControlStatusUnSolicitedFlag(true);
            break;
        }
        case CC_Normal:
        default:
        {
            setControlStatusPartialFlag(false);
            setControlStatusSignificantFlag(false);
            setControlStatusAbnQualityFlag(false);
            setControlStatusFailFlag(false);
            setControlStatusCommFailFlag(false);
            setControlStatusNoControlFlag(false);
            setControlStatusUnSolicitedFlag(false);
            break;
        }
    }

    return *this;
}

string CtiCCCapBank::getControlStatusQualityString()
{
    string retString = "Normal";
    switch (_controlStatusQuality)
    {
        case CC_Partial:
        {
            retString = "Partial";
            break;
        }

        case CC_Significant:
        {
            retString = "Significant";
            break;

        }
        case CC_AbnormalQuality:
        {
            retString = "Abnormal Quality";
            break;
        }
        case CC_Fail:
        {
            retString = "Fail";
            break;
        }
        case CC_CommFail:
        {
            retString = "Comm Fail";
            break;
        }
        case CC_NoControl:
        {
            retString = "No Control";
            break;
        }
        case CC_UnSolicited:
        {
            retString = "UnSolicited";
            break;
        }
        case CC_Normal:
        default:
        {
            retString = "Normal";
            break;
        }
    }

    return retString;


}

string CtiCCCapBank::getIgnoreReasonText() const
{
    string retVal = "";

    switch (_ignoreReason)
    {
        case 0:  retVal = "Local"; break;
        case 1:  retVal = "FaultCurrent"; break;
        case 2:  retVal = "EmVolt"; break;
        case 3:  retVal = "Time"; break;
        case 4:  retVal = "Voltage"; break;
        case 5:  retVal = "Digital1"; break;
        case 6:  retVal = "Analog1"; break;
        case 7:  retVal = "Digital2"; break;
        case 8:  retVal = "Analog2"; break;
        case 9:  retVal = "Digital3"; break;
        case 10: retVal = "Analog3"; break;
        case 11: retVal = "Digital4"; break;
        case 12: retVal = "Temp"; break;
        case 13: retVal = "Remote"; break;
        case 14: retVal = "NtrlLockOut"; break;
        case 15: retVal = "BrownOut"; break;
        case 16: retVal = "BadActRelay"; break;
        default: retVal = "unknown"; break;
    }

    return retVal;
}



CtiCCCapBank& CtiCCCapBank::setIpAddress(unsigned long value)
{
    _ipAddress = "(none)";
    if (value > 0)
    {
        char tempchar[4];
        //char* tempchar = NULL;
        BYTE temp;
        temp = (value >> 24) & 0xFF;
        _ipAddress = itoa(temp,tempchar,10);
        _ipAddress += ".";
        temp = (value >> 16) & 0xFF;
        _ipAddress += itoa(temp,tempchar,10);
        _ipAddress += ".";
        temp = (value >> 8 ) & 0xFF;
        _ipAddress += itoa(temp,tempchar,10);
        _ipAddress += ".";
        temp = (value & 0xFF);
        _ipAddress += itoa(temp,tempchar,10);
    }
    return *this;
}
CtiCCCapBank& CtiCCCapBank::setUDPPort(long value)
{
    if (_udpPortNumber != value)
    {
        _dirty = true;
    }
    _udpPortNumber = value;

    return *this;

}

CtiCCCapBank& CtiCCCapBank::setReportedCBCLastControlReason(long value)
{
    if (_reportedCBCLastControlReason != value)
    {
        _dirty = true;
    }
    _reportedCBCLastControlReason = value;

    return *this;

}

CtiCCCapBank& CtiCCCapBank::setPartialPhaseInfo(const string& info)
{
    if (ciStringEqual(_partialPhaseInfo,info))
    {
        _dirty = true;
    }
    _partialPhaseInfo = info;

    return *this;

}
CtiCCCapBank& CtiCCCapBank::setReportedCBCState(long value)
{
    if (_reportedCBCState != value)
    {
        _dirty = true;
    }
    _reportedCBCState = value;

    return *this;

}

CtiCCCapBank& CtiCCCapBank::setReportedCBCStateTime(const CtiTime& timestamp)
{
    if (_reportedCBCStateTime != timestamp)
    {
        _dirty = true;
    }
    _reportedCBCStateTime = timestamp;

    return *this;

}

CtiCCCapBank& CtiCCCapBank::setIgnoreFlag(bool flag)
{
    if (_ignoreFlag != flag)
    {
        _dirty = true;
    }
    _ignoreFlag = flag;

    return *this;

}

CtiCCCapBank& CtiCCCapBank::setIgnoredReason(long value)
{
    if (_ignoreReason != value)
    {
        _dirty = true;
    }
    _ignoreReason = value;

    return *this;

}
CtiCCCapBank& CtiCCCapBank::setIgnoreReasonTimeUpdated(const CtiTime& timestamp)
{
    _ignoreReasonTimeUpdated = timestamp;
    return *this;
}
CtiCCCapBank& CtiCCCapBank::setIgnoreIndicatorTimeUpdated(const CtiTime& timestamp)
{
    _ignoreIndicatorTimeUpdated = timestamp;
    return *this;
}
CtiCCCapBank& CtiCCCapBank::setUnsolicitedChangeTimeUpdated(const CtiTime& timestamp)
{
    _unsolicitedChangeTimeUpdated = timestamp;
    return *this;
}

CtiCCCapBank& CtiCCCapBank::setBeforeVarsString(const string& before)
{
    if (_sBeforeVars != before)
    {
        _dirty = true;
    }
    _sBeforeVars = before;
    return *this;
}

CtiCCCapBank& CtiCCCapBank::setAfterVarsString(const string& after)
{
    if (_sAfterVars != after)
    {
        _dirty = true;
    }
    _sAfterVars = after;
    return *this;
}

CtiCCCapBank& CtiCCCapBank::setPercentChangeString(const string& percent)
{
    if (_sPercentChange != percent)
    {
        _dirty = true;
    }
    _sPercentChange = percent;
    return *this;
}

CtiCCCapBank& CtiCCCapBank::setActionId(long actionId)
{
    _actionId = actionId;
    return *this;
}

bool CtiCCCapBank::updateVerificationState(void)
{

    int ctrlIdx = getVCtrlIndex();
    _verificationDoneFlag = false;
    if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " CB: "<<getPaoId()<<" vCtrlIdx: "<< getVCtrlIndex() <<" prevControlStatus: "<< _prevVerificationControlStatus <<"  ControlStatus: " << getControlStatus() << endl;
    }
    switch (ctrlIdx)
    {
    case 1:
        {
            setPreviousVerificationControlStatus(getControlStatus());
            if ( stringContainsIgnoreCase(getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                  (getControlStatus() == OpenFail || getControlStatus() == CloseFail) )
            {
                _verificationDoneFlag = true;
                ctrlIdx = 5;
            }
            else
            {
                _verificationDoneFlag = false;
                _retryFlag = false;
                ctrlIdx++;
            }
            break;
        }
    case 2:
        {
            if ( !( stringContainsIgnoreCase(getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG ))
            {
                if ( (getControlStatus() == Open || getControlStatus() == Close) &&
                      getControlStatus() != _assumedOrigCapBankPos )
                {
                    _verificationDoneFlag = true;
                    ctrlIdx = 5;
                    _retryFlag = false;
                }
                else
                {
                    if ( _prevVerificationControlStatus == OpenFail ||
                         _prevVerificationControlStatus == CloseFail )
                    {
                        if (getControlStatus() == OpenFail ||
                            getControlStatus() == CloseFail )
                        {
                            _verificationDoneFlag = true;
                            ctrlIdx = 5;
                            _retryFlag = false;
                        }
                        else
                        {
                            ctrlIdx++;
                            _retryFlag = false;
                            setPreviousVerificationControlStatus(getControlStatus());
                        }
                    }
                    else //_prevVerificationControlStatus == Questionable, Open or Close (Success)!!!
                    {
                        if (getControlStatus() == OpenFail ||
                            getControlStatus() == CloseFail )
                        {
                            if (_retryFlag)
                            {
                                _retryFlag = false;
                                ctrlIdx = 5;
                                _verificationDoneFlag = true;
                            }
                            else
                            {
                                _retryFlag = true;

                            }
                        }
                        else // getControlStatus() == Open or Close (Success)!!   MUST HAVE BEEN A RETRY!
                        {
                             _retryFlag = false;
                             ctrlIdx = 5;
                             _verificationDoneFlag = true;
                        }
                    }
                }
            }
            else // CBC 7000 Flip Verification...
            {
                /*if ( (getControlStatus() == Open || getControlStatus() == Close) &&
                      getControlStatus() != _assumedOrigCapBankPos )
                { */
                    _verificationDoneFlag = true;
                    ctrlIdx = 5;
                    _retryFlag = false;
                /*}
                else
                {
                } */
            }
            break;
        }
    case 3:
        {
            if (getControlStatus() == OpenFail ||
                getControlStatus() == CloseFail )
            {
                if ( (stringContainsIgnoreCase(getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG ) || _retryFlag )
                {
                    ctrlIdx = 5;
                    _verificationDoneFlag = true;
                    _retryFlag = false;
                }
                else
                {
                    _retryFlag = true;

                }
            }
            else  // getControlStatus() == Open or Close (Success!!!)
            {
                _retryFlag = false;
                ctrlIdx = 5;
                _verificationDoneFlag = true;
            }

            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " ***WARNING*** Adjusting CapBank Verification Control Index = 5 and setting Verification Done Flag ==> " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            _verificationDoneFlag = true;
            ctrlIdx = 5;
            _retryFlag = false;
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
        _dirty = true;
    }
    _vCtrlIndex = vCtrlIndex;
    return *this;
}

CtiCCCapBank& CtiCCCapBank::setPreviousVerificationControlStatus(long status)
{
    if (status != _prevVerificationControlStatus)
    {
        _dirty = true;
    }
    _prevVerificationControlStatus = status;
    return *this;

}
CtiCCCapBank& CtiCCCapBank::setAssumedOrigVerificationState(int assumedOrigCapBankPos)
{
    if (assumedOrigCapBankPos != _assumedOrigCapBankPos)
    {
        _dirty = true;
    }
    _assumedOrigCapBankPos = assumedOrigCapBankPos;
    return *this;
}



CtiCCCapBank& CtiCCCapBank::initVerificationControlStatus()
{
   _verificationControlStatus = getControlStatus();
   return *this;
}

bool CtiCCCapBank::handlePointResponseDeltaChange(long pointId, double newDelta, bool staticDelta)
{
    return _pointResponseManager.handlePointResponseDeltaChange(pointId,newDelta,staticDelta);
}

bool CtiCCCapBank::updatePointResponseDelta(CtiCCMonitorPointPtr point)
{
    return _pointResponseManager.updatePointResponseDelta(*point);
}

bool CtiCCCapBank::updatePointResponsePreOpValue(long pointId, double value)
{
    return _pointResponseManager.updatePointResponsePreOpValue(pointId,value);
}

bool CtiCCCapBank::isExpresscom()
{
    std::string devType = getControlDeviceType();

    return ( stringContainsIgnoreCase( devType, "cbc 701" ) ||
             stringContainsIgnoreCase( devType, "cbc expresscom" ) );
}

/*---------------------------------------------------------------------------
    setControlStatus

    Sets the control status of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatus(long status)
{

    if (_verificationFlag)
    {
        if( _verificationControlStatus != status )
        {
            /*{
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }*/
            _dirty = true;
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
                dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }*/
            _dirty = true;
        }
        _controlstatus = status;
    }

    return *this;
}

CtiCCCapBank& CtiCCCapBank::setControlRecentlySentFlag(bool flag)
{
    if (_controlRecentlySentFlag != flag)
    {
        _dirty = true;
    }
    _controlRecentlySentFlag = flag;

    return *this;
}

/*---------------------------------------------------------------------------
    setOperationAnalogPointId

    Sets the point id for the analog point that hold the number of number of
    operations performed on the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOperationAnalogPointId(long operationpointid)
{
    _operationanalogpointid = operationpointid;

    return *this;
}

/*---------------------------------------------------------------------------
    setTotalOperations

    Sets the number of operations performed on this capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setTotalOperations(long operations)
{
    if( _totaloperations != operations )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _totaloperations = operations;
    return *this;
}


/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the feeder
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setTotalOperationsAndSendMsg(long operations, CtiMultiMsg_vec& pointChanges)
{
    if( _totaloperations != operations )
    {
        if( getOperationAnalogPointId() > 0 )
        {
            pointChanges.push_back(new CtiPointDataMsg(getOperationAnalogPointId(),operations,NormalQuality,AnalogPointType));
        }
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _totaloperations = operations;

    return *this;
}

/*---------------------------------------------------------------------------
    setLastStatusChangeTime

    Sets the timestamp of the last status point data msg sent by dispatch
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setLastStatusChangeTime(const CtiTime& laststatuschangetime)
{
    if( _laststatuschangetime != laststatuschangetime )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _laststatuschangetime = laststatuschangetime;
    return *this;
}

/*---------------------------------------------------------------------------
    setTagsControlStatus

    Sets the tags of control status on the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setTagsControlStatus(long tags)
{
    if( _tagscontrolstatus != tags )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = true  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = true;
    }
    _tagscontrolstatus = tags;
    return *this;
}


CtiCCCapBank& CtiCCCapBank::addAllCapBankPointsToMsg(std::set<long>& pointAddMsg)
{

    if( getStatusPointId() > 0 )
    {
        pointAddMsg.insert(getStatusPointId());
    }
    if( getOperationAnalogPointId() > 0 )
    {
        pointAddMsg.insert(getOperationAnalogPointId());
    }
    if (getOperationStats().getUserDefOpSuccessPercentId() > 0)
    {
        pointAddMsg.insert(getOperationStats().getUserDefOpSuccessPercentId());
    }
    if (getOperationStats().getDailyOpSuccessPercentId() > 0)
    {
        pointAddMsg.insert(getOperationStats().getDailyOpSuccessPercentId());
    }
    if (getOperationStats().getWeeklyOpSuccessPercentId() > 0)
    {
        pointAddMsg.insert(getOperationStats().getWeeklyOpSuccessPercentId());
    }
    if (getOperationStats().getMonthlyOpSuccessPercentId() > 0)
    {
        pointAddMsg.insert(getOperationStats().getMonthlyOpSuccessPercentId());
    }
    return *this;
}

Cti::CapControl::PointResponse CtiCCCapBank::getPointResponse(CtiCCMonitorPointPtr point)
{
    return _pointResponseManager.getPointResponse(point->getPointId());
}

std::vector<Cti::CapControl::PointResponse> CtiCCCapBank::getPointResponses()
{
    return _pointResponseManager.getPointResponses();
}

void CtiCCCapBank::addPointResponse(Cti::CapControl::PointResponse pointResponse)
{
    bool inserted = _pointResponseManager.addPointResponse(pointResponse);
    if ( ! inserted)
    {
        _pointResponseManager.handlePointResponseDeltaChange(pointResponse.getPointId(),pointResponse.getDelta(),pointResponse.getStaticDelta());
        _pointResponseManager.updatePointResponsePreOpValue(pointResponse.getPointId(),pointResponse.getPreOpValue());
    }
}

PointResponseManager& CtiCCCapBank::getPointResponseManager()
{
    return _pointResponseManager;
}

bool CtiCCCapBank::addMonitorPoint(CtiCCMonitorPointPtr monPoint)
{
    for each (CtiCCMonitorPointPtr x in _monitorPoint)
    {
		if (x->getPointId() == monPoint->getPointId())
			return false;
    }
	_monitorPoint.push_back(monPoint);
    return true;
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCCapBank::saveGuts(RWvostream& ostrm ) const
{
    long tempParentId = _originalParent.getOriginalParentId();

    RWCollectable::saveGuts(ostrm);
    CapControlPao::saveGuts(ostrm);

    ostrm << _parentId;
    ostrm << _maxdailyops;
    ostrm << _maxopsdisableflag;
    ostrm << _alarminhibitflag;
    ostrm << _controlinhibitflag;
    ostrm << _operationalstate;

    ostrm << _controllertype;

    ostrm << _controldeviceid;
    ostrm << _banksize;
    ostrm << _typeofswitch;
    ostrm << _switchmanufacture;
    ostrm << _maplocationid;
    ostrm <<  _reclosedelay;
    ostrm <<  _controlorder;
    ostrm <<  _statuspointid;
    ostrm <<  _controlstatus;
    ostrm <<  _operationanalogpointid;
    ostrm <<  _totaloperations;
    ostrm <<  _laststatuschangetime;
    ostrm <<  _tagscontrolstatus;
    ostrm <<  tempParentId;
    ostrm <<  _currentdailyoperations;
    ostrm <<  _ignoreFlag;
    ostrm << _ignoreReason;
    ostrm << _ovUvDisabledFlag;
    ostrm << _triporder;
    ostrm << _closeorder;
    ostrm << _controlDeviceType;
    ostrm << _sBeforeVars;
    ostrm << _sAfterVars;
    ostrm << _sPercentChange;
    ostrm << _maxDailyOpsHitFlag;
    ostrm << _ovuvSituationFlag;
    ostrm << _controlStatusQuality;
    ostrm << _localControlFlag;
    ostrm << _partialPhaseInfo;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::operator=(const CtiCCCapBank& rightObj)
{
    if( this != &rightObj )
    {
        CapControlPao::operator=(rightObj);
        _parentId = rightObj._parentId;
        _alarminhibitflag = rightObj._alarminhibitflag;
        _controlinhibitflag = rightObj._controlinhibitflag;
        _maxdailyops = rightObj._maxdailyops;
        _currentdailyoperations = rightObj._currentdailyoperations;
        _maxopsdisableflag = rightObj._maxopsdisableflag;
        _operationalstate = rightObj._operationalstate;
        _controllertype = rightObj._controllertype;
        _controldeviceid = rightObj._controldeviceid;
        _controlpointid = rightObj._controlpointid;
        _controlDeviceType = rightObj._controlDeviceType;
        _banksize = rightObj._banksize;
        _typeofswitch = rightObj._typeofswitch;
        _switchmanufacture = rightObj._switchmanufacture;
        _maplocationid = rightObj._maplocationid;
        _reclosedelay = rightObj._reclosedelay;
        _controlorder = rightObj._controlorder;
        _triporder = rightObj._triporder;
        _closeorder = rightObj._closeorder;
        _statuspointid = rightObj._statuspointid;
        _controlstatus = rightObj._controlstatus;
        _operationanalogpointid = rightObj._operationanalogpointid;
        _totaloperations = rightObj._totaloperations;
        _laststatuschangetime = rightObj._laststatuschangetime;
        _tagscontrolstatus = rightObj._tagscontrolstatus;
        _assumedOrigCapBankPos = rightObj._assumedOrigCapBankPos;
        _prevVerificationControlStatus = rightObj._prevVerificationControlStatus;
        _vCtrlIndex = rightObj._vCtrlIndex;
        _selectedForVerificationFlag = rightObj._selectedForVerificationFlag;
        _additionalFlags = rightObj._additionalFlags;
        _verificationFlag = rightObj._verificationFlag;
        _performingVerificationFlag = rightObj._performingVerificationFlag;
        _verificationDoneFlag = rightObj._verificationDoneFlag;
        _retryOpenFailedFlag = rightObj._retryOpenFailedFlag;
        _retryCloseFailedFlag = rightObj._retryCloseFailedFlag;
        _ovUvDisabledFlag = rightObj._ovUvDisabledFlag;
        _maxDailyOpsHitFlag = rightObj._maxDailyOpsHitFlag;
        _controlStatusPartialFlag =     rightObj._controlStatusPartialFlag;
        _controlStatusSignificantFlag = rightObj._controlStatusSignificantFlag;
        _controlStatusAbnQualityFlag = rightObj._controlStatusAbnQualityFlag;
        _controlStatusFailFlag = rightObj._controlStatusFailFlag;
        _controlStatusCommFailFlag = rightObj._controlStatusCommFailFlag;
        _controlStatusNoControlFlag = rightObj._controlStatusNoControlFlag;
        _controlStatusUnSolicitedFlag = rightObj._controlStatusUnSolicitedFlag;
        _controlStatusQuality = rightObj._controlStatusQuality;
        _reEnableOvUvFlag = rightObj._reEnableOvUvFlag;
        _localControlFlag = rightObj._localControlFlag;
        _controlRecentlySentFlag = rightObj._controlRecentlySentFlag;
        _porterRetFailFlag = rightObj._porterRetFailFlag;
        _unsolicitedPendingFlag = rightObj._unsolicitedPendingFlag;

        _ignoreReasonTimeUpdated = rightObj._ignoreReasonTimeUpdated;
        _ignoreIndicatorTimeUpdated = rightObj._ignoreIndicatorTimeUpdated;
        _unsolicitedChangeTimeUpdated = rightObj._unsolicitedChangeTimeUpdated;

        _ipAddress = rightObj._ipAddress;
        _udpPortNumber = rightObj._udpPortNumber;
        _reportedCBCLastControlReason = rightObj._reportedCBCLastControlReason;
        _reportedCBCState = rightObj._reportedCBCState;
        _reportedCBCStateTime = rightObj._reportedCBCStateTime;
        _partialPhaseInfo = rightObj._partialPhaseInfo;

        _ignoreFlag = rightObj._ignoreFlag;
        _ignoreReason = rightObj._ignoreReason;
        _sAfterVars = rightObj._sAfterVars;
        _sBeforeVars = rightObj._sBeforeVars;
        _sPercentChange = rightObj._sPercentChange;
        _ovuvSituationFlag = rightObj._ovuvSituationFlag;

        _originalParent = rightObj._originalParent;

        _operationStats = rightObj._operationStats;
        if (rightObj._twoWayPoints != NULL)
        {
            _twoWayPoints = new CtiCCTwoWayPoints(*rightObj._twoWayPoints);

        }
        else
            _twoWayPoints = NULL;

        _insertDynamicDataFlag = rightObj._insertDynamicDataFlag;
        _dirty = rightObj._dirty;

    }
    return *this;
}

/*---------------------------------------------------------------------------
    restore

    Restores self's state given a Reader
---------------------------------------------------------------------------*/
void CtiCCCapBank::restore(Cti::RowReader& rdr)
{
    CtiTime currentDateTime = CtiTime();
    CtiTime dynamicTimeStamp;
    string tempBoolString;

    CapControlPao::restore(rdr);

    rdr["operationalstate"] >> _operationalstate;
    rdr["controllertype"] >> _controllertype;
    rdr["controldeviceid"] >> _controldeviceid;
    rdr["controlpointid"] >> _controlpointid;
    rdr["banksize"] >> _banksize;
    rdr["typeofswitch"] >> _typeofswitch;
    rdr["switchmanufacture"] >> _switchmanufacture;
    rdr["maplocationid"] >> _maplocationid;
    rdr["reclosedelay"] >> _reclosedelay;
    rdr["maxdailyops"] >> _maxdailyops;
    rdr["maxopdisable"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setMaxOpsDisableFlag(tempBoolString=="y");


    setAlarmInhibitFlag(false);
    setControlInhibitFlag(false);
    _controlorder = 0;
    setStatusPointId(0);
    setOperationAnalogPointId(0);

 //initialize dynamic data members
    setTotalOperations(0);
    setLastStatusChangeTime(gInvalidCtiTime);
    setControlStatus(CtiCCCapBank::Open);
    setTagsControlStatus(0);
    setAssumedOrigVerificationState(CtiCCCapBank::Open);
    setPreviousVerificationControlStatus(CtiCCCapBank::Open);
    setVCtrlIndex(-1);
    setSelectedForVerificationFlag(false);
    setVerificationFlag(false);
    setRetryOpenFailedFlag(false);
    setRetryCloseFailedFlag(false);
    setOvUvDisabledFlag(false);
    setMaxDailyOpsHitFlag(false);
    setControlStatusPartialFlag(false);
    setControlStatusSignificantFlag(false);
    setControlStatusAbnQualityFlag(false);
    setControlStatusQuality(CC_Normal);
    setReEnableOvUvFlag(false);
    setLocalControlFlag(false);
    setControlRecentlySentFlag(false);
    setPorterRetFailFlag(false);
    setUnsolicitedPendingFlag(false);

    setOvUvSituationFlag(false);
    _additionalFlags = string("NNNNNNNNNNNNNNNNNNNN");
    setCurrentDailyOperations(0);

    setIpAddress(0);
    setUDPPort(0);
    setReportedCBCLastControlReason(0);
    setReportedCBCState(-1);
    setReportedCBCStateTime(gInvalidCtiTime);

    setIgnoreFlag(false);
    setIgnoredReason(0);
    setBeforeVarsString("none");
    setAfterVarsString("none");
    setPercentChangeString("none");
    setPartialPhaseInfo("(none)");

    setIgnoreReasonTimeUpdated(gInvalidCtiTime);
    setIgnoreIndicatorTimeUpdated(gInvalidCtiTime);
    setUnsolicitedChangeTimeUpdated(gInvalidCtiTime);

    _originalParent.setPAOId(getPaoId());

    _insertDynamicDataFlag = true;
    _dirty = true;

}

bool CtiCCCapBank::getInsertDynamicDataFlag() const
{
    return _insertDynamicDataFlag;
}

void CtiCCCapBank::setDynamicData(Cti::RowReader& rdr)
{

    CtiTime dynamicTimeStamp;
    rdr["controlstatus"] >> _controlstatus;
    rdr["totaloperations"] >> _totaloperations;
    rdr["laststatuschangetime"] >> _laststatuschangetime;
    rdr["tagscontrolstatus"] >> _tagscontrolstatus;
    rdr["ctitimestamp"] >> dynamicTimeStamp;
    rdr["assumedstartverificationstatus"] >> _assumedOrigCapBankPos;
    rdr["prevverificationcontrolstatus"] >> _prevVerificationControlStatus;
    rdr["verificationcontrolindex"] >> _vCtrlIndex;

    rdr["additionalflags"] >> _additionalFlags;
    std::transform(_additionalFlags.begin(), _additionalFlags.end(), _additionalFlags.begin(), tolower);
    _verificationFlag = (_additionalFlags[0]=='y');
    _performingVerificationFlag = (_additionalFlags[1]=='y');
    _verificationDoneFlag = (_additionalFlags[2]=='y');
    _retryOpenFailedFlag = (_additionalFlags[3]=='y');
    _retryCloseFailedFlag = (_additionalFlags[4]=='y');
    _ovUvDisabledFlag = (_additionalFlags[5]=='y');
    _maxDailyOpsHitFlag = (_additionalFlags[6]=='y');
    _ovuvSituationFlag = (_additionalFlags[7]=='y');
    _controlStatusPartialFlag = (_additionalFlags[8]=='y');
    _controlStatusSignificantFlag = (_additionalFlags[9]=='y');
    _controlStatusAbnQualityFlag = (_additionalFlags[10]=='y');
    _controlStatusFailFlag = (_additionalFlags[11]=='y');
    _controlStatusCommFailFlag = (_additionalFlags[12]=='y');
    _controlStatusNoControlFlag = (_additionalFlags[13]=='y');
    _controlStatusUnSolicitedFlag = (_additionalFlags[14]=='y');
    _reEnableOvUvFlag = (_additionalFlags[15]=='y');
    _localControlFlag = (_additionalFlags[16]=='y');
    _controlRecentlySentFlag = (_additionalFlags[17]=='y');
    _porterRetFailFlag = (_additionalFlags[18]=='y');
    _unsolicitedPendingFlag = (_additionalFlags[19]=='y');

    if (_controlStatusPartialFlag)
        _controlStatusQuality = CC_Partial;
    else if(_controlStatusPartialFlag)
        _controlStatusQuality = CC_Significant;
    else if(_controlStatusAbnQualityFlag)
        _controlStatusQuality = CC_AbnormalQuality;
    else if(_controlStatusFailFlag)
        _controlStatusQuality = CC_Fail;
    else if(_controlStatusCommFailFlag)
        _controlStatusQuality = CC_CommFail;
    else if(_controlStatusNoControlFlag)
        _controlStatusQuality = CC_NoControl;
    else if(_controlStatusUnSolicitedFlag)
        _controlStatusQuality = CC_UnSolicited;
    else
        _controlStatusQuality = CC_Normal;

    rdr["currentdailyoperations"] >> _currentdailyoperations;
    rdr["twowaycbcstate"] >> _reportedCBCState;
    rdr["twowaycbcstatetime"] >> _reportedCBCStateTime;
    rdr["beforevar"] >> _sBeforeVars;
    rdr["aftervar"] >> _sAfterVars;
    rdr["changevar"] >> _sPercentChange;
    rdr["twowaycbclastcontrol"] >> _reportedCBCLastControlReason;
    rdr["partialphaseinfo"] >> _partialPhaseInfo;

    _originalParent.restore(rdr);

    _actionId = -1;

    _insertDynamicDataFlag = false;
    _dirty = false;

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
int CtiCCCapBank::compareTo(const RWCollectable* rightObj) const
{
    return _controlorder == ((CtiCCCapBank*)rightObj)->getControlOrder() ? 0 : (_controlorder > ((CtiCCCapBank*)rightObj)->getControlOrder() ? 1 : -1);
}

/*---------------------------------------------------------------------------
    isDirty

    Returns the dirty flag of the cap bank
---------------------------------------------------------------------------*/
bool CtiCCCapBank::isDirty() const
{
    return _dirty;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc cap bank.
---------------------------------------------------------------------------*/
void CtiCCCapBank::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( _dirty )
    {
        if( !_insertDynamicDataFlag )
        {

            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (_verificationFlag?'Y':'N');
            addFlags[1] = (_performingVerificationFlag?'Y':'N');
            addFlags[2] = (_verificationDoneFlag?'Y':'N');
            addFlags[3] = (_retryOpenFailedFlag?'Y':'N');
            addFlags[4] = (_retryCloseFailedFlag?'Y':'N');
            addFlags[5] = (_ovUvDisabledFlag?'Y':'N');
            addFlags[6] = (_maxDailyOpsHitFlag?'Y':'N');
            addFlags[7] = (_ovuvSituationFlag?'Y':'N');
            addFlags[8] = (_controlStatusPartialFlag?'Y':'N');
            addFlags[9] = (_controlStatusSignificantFlag?'Y':'N');
            addFlags[10] = (_controlStatusAbnQualityFlag?'Y':'N');
            addFlags[11] = (_controlStatusFailFlag?'Y':'N');
            addFlags[12] = (_controlStatusCommFailFlag?'Y':'N');
            addFlags[13] = (_controlStatusNoControlFlag?'Y':'N');
            addFlags[14] = (_controlStatusUnSolicitedFlag?'Y':'N');
            addFlags[15] = (_reEnableOvUvFlag?'Y':'N');
            addFlags[16] = (_localControlFlag?'Y':'N');
            addFlags[17] = (_controlRecentlySentFlag?'Y':'N');
            addFlags[18] = (_porterRetFailFlag?'Y':'N');
            addFlags[19] = (_unsolicitedPendingFlag?'Y':'N');
            _additionalFlags = char2string(*addFlags);
            _additionalFlags.append(char2string(*(addFlags+1)));
            _additionalFlags.append(char2string(*(addFlags+2)));
            _additionalFlags.append(char2string(*(addFlags+3)));
            _additionalFlags.append(char2string(*(addFlags+4)));
            _additionalFlags.append(char2string(*(addFlags+5)));
            _additionalFlags.append(char2string(*(addFlags+6)));
            _additionalFlags.append(char2string(*(addFlags+7)));
            _additionalFlags.append(char2string(*(addFlags+8)));
            _additionalFlags.append(char2string(*(addFlags+9)));
            _additionalFlags.append(char2string(*(addFlags+10)));
            _additionalFlags.append(char2string(*(addFlags+11)));
            _additionalFlags.append(char2string(*(addFlags+12)));
            _additionalFlags.append(char2string(*(addFlags+13)));
            _additionalFlags.append(char2string(*(addFlags+14)));
            _additionalFlags.append(char2string(*(addFlags+15)));
            _additionalFlags.append(char2string(*(addFlags+16)));
            _additionalFlags.append(char2string(*(addFlags+17)));
            _additionalFlags.append(char2string(*(addFlags+18)));
            _additionalFlags.append(char2string(*(addFlags+19)));

            static const string updaterSql = "update dynamiccccapbank set "
                                             "controlstatus = ?, "
                                             "totaloperations = ?, "
                                             "laststatuschangetime = ?, "
                                             "tagscontrolstatus = ?, "
                                             "ctitimestamp = ?, "
                                             "assumedstartverificationstatus = ?, "
                                             "prevverificationcontrolstatus = ?, "
                                             "verificationcontrolindex = ?, "
                                             "additionalflags = ?, "
                                             "currentdailyoperations = ?, "
                                             "twowaycbcstate = ?, "
                                             "twowaycbcstatetime = ?, "
                                             "beforevar = ?, "
                                             "aftervar = ?, "
                                             "changevar = ?, "
                                             "twowaycbclastcontrol = ?, "
                                             "partialphaseinfo = ? "
                                             " where capbankid = ?";

            Cti::Database::DatabaseWriter updater(conn, updaterSql);

            updater << _controlstatus
            << _totaloperations
            << _laststatuschangetime
            << _tagscontrolstatus
            << currentDateTime
            << _assumedOrigCapBankPos
            << _prevVerificationControlStatus
            << _vCtrlIndex
            << _additionalFlags
            << _currentdailyoperations
            << _reportedCBCState
            << _reportedCBCStateTime
            << _sBeforeVars
            << _sAfterVars
            << _sPercentChange
            << _reportedCBCLastControlReason
            << _partialPhaseInfo
            << getPaoId();

            if(updater.execute())    // No error occured!
            {
                _dirty = false;
            }
            else
            {
                _dirty = true;
                {
                    string loggedSQLstring = updater.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << loggedSQLstring << endl;
                    }
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted Cap Bank into DynamicCCCapBank: " << getPaoName() << endl;
            }

            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            static const string inserterSql = "insert into dynamiccccapbank values ( "
                                             "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                             "?, ?, ?, ?, ?, ?, ?, ?)";

            Cti::Database::DatabaseWriter dbInserter(conn, inserterSql);

            dbInserter << getPaoId()
            << _controlstatus
            << _totaloperations
            << _laststatuschangetime
            << _tagscontrolstatus
            << currentDateTime
            << _assumedOrigCapBankPos
            << _prevVerificationControlStatus
            << _vCtrlIndex
            << addFlags
            << _currentdailyoperations
            << _reportedCBCState
            << _reportedCBCStateTime
            << _sBeforeVars
            << _sAfterVars
            << _sPercentChange
            << _reportedCBCLastControlReason
            << _partialPhaseInfo;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = dbInserter.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            if(dbInserter.execute())    // No error occured!
            {
                _insertDynamicDataFlag = false;
                _dirty = false;
            }
            else
            {
                _dirty = true;
                {
                    string loggedSQLstring = dbInserter.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << loggedSQLstring << endl;
                    }
                }
            }
        }
        getOriginalParent().dumpDynamicData(conn, currentDateTime);
        getOperationStats().dumpDynamicData(conn, currentDateTime);

        for each (CtiCCMonitorPointPtr monPoint in getMonitorPoint())
        {
            monPoint->dumpDynamicData(conn,currentDateTime);
        }
    }
    try
    {
        if (stringContainsIgnoreCase(getControlDeviceType(), "CBC 702") )
        {
            CtiCCTwoWayPoints* twoWayPts = getTwoWayPoints();
            twoWayPts->dumpDynamicData(conn,currentDateTime);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

void CtiCCCapBank::dumpDynamicPointResponseData(Cti::Database::DatabaseConnection& conn)
{
    //Building Dao manually here because this updating should be handled in the store anyways.
    PointResponseDaoPtr pointResponseDao = DatabaseDaoFactory().getPointResponseDao();

    for each (PointResponse pointResponse in getPointResponses())
    {
        bool ret = pointResponseDao->save(conn,pointResponse);

        if( (ret == false) && (_CC_DEBUG & CC_DEBUG_DATABASE) )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Point Response save failed. " << endl;
        }
    }
}

void CtiCCCapBank::dumpDynamicPointResponseData()
{
    //Building Dao manually here because this updating should be handled in the store anyways.
    PointResponseDaoPtr pointResponseDao = DatabaseDaoFactory().getPointResponseDao();

    for each (PointResponse pointResponse in getPointResponses())
    {
        bool ret = pointResponseDao->save(pointResponse);

        if( (ret == false) && (_CC_DEBUG & CC_DEBUG_DATABASE) )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Point Response save failed. " << endl;
        }
    }
}


const string& CtiCCCapBank::convertOperationalState( int num )
{
    switch( num )
    {
        case capcontrol::FIXED:
        {
            return FixedOperationalState;
        }
        case capcontrol::SWITCHED:
        {
            return SwitchedOperationalState;
        }
        case capcontrol::UNINSTALLED:
        {
            return UninstalledState;
        }
        case capcontrol::STANDALONE:
        {
            return StandAloneState;
        }
        default:
        {
            return SwitchedOperationalState;
        }
    }
}

bool CtiCCCapBank::isControlDeviceTwoWay()
{
    if (stringContainsIgnoreCase( getControlDeviceType(),"CBC 702") ||
        stringContainsIgnoreCase( getControlDeviceType(),"CBC DNP") ||
        stringContainsIgnoreCase( getControlDeviceType(),"CBC 802") )
    {
        return true;
    }
    else
    {
        return false;
    }
}

int CtiCCCapBank::getPointIdByAttribute(const PointAttribute & attribute)
{
    return getTwoWayPoints()->getPointIdByAttribute(attribute);
}


void CtiCCCapBank::setDirty(const bool flag)
{
    _dirty = flag;
}


/* Public Static members */
const string CtiCCCapBank::SwitchedOperationalState = "Switched";
const string CtiCCCapBank::FixedOperationalState = "Fixed";
const string CtiCCCapBank::UninstalledState = "Uninstalled";
const string CtiCCCapBank::StandAloneState = "StandAlone";

const int CtiCCCapBank::Open = STATEZERO;
const int CtiCCCapBank::Close = STATEONE;
const int CtiCCCapBank::OpenQuestionable = STATETWO;
const int CtiCCCapBank::CloseQuestionable = STATETHREE;
const int CtiCCCapBank::OpenFail = STATEFOUR;
const int CtiCCCapBank::CloseFail = STATEFIVE;
const int CtiCCCapBank::OpenPending = STATESIX;
const int CtiCCCapBank::ClosePending = STATESEVEN;
