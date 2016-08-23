#include "precompiled.h"

#include "dbaccess.h"
#include "cccapbank.h"
#include "ccid.h"
#include "pointdefs.h"
#include "logger.h"
#include "resolvers.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"
#include "utility.h"
#include "PointResponseManager.h"

using namespace Cti::CapControl;
using namespace std;

extern unsigned long _CC_DEBUG;
extern bool _USE_FLIP_FLAG;

DEFINE_COLLECTABLE( CtiCCCapBank, CTICCCAPBANK_ID )


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
    _ovuvSituationFlag = false;
}

CtiCCCapBank::CtiCCCapBank(Cti::RowReader& rdr) :
_parentId(0)
{
    restore(rdr);
     _monitorPoint.clear();
     _ovuvSituationFlag = false;
     _originalParent.setPAOId(getPaoId());
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

CtiCCTwoWayPoints & CtiCCCapBank::getTwoWayPoints()
{
    if ( ! _twoWayPoints )
    {
        _twoWayPoints.reset( CtiCCTwoWayPointsFactory::Create(_controldeviceid, _controlDeviceType) );
    }

    return *_twoWayPoints;
}

CtiCCOriginalParent& CtiCCCapBank::getOriginalParent()
{
    return _originalParent;
}

const CtiCCOriginalParent& CtiCCCapBank::getOriginalParent() const
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
void CtiCCCapBank::setParentId(long parentId)
{
    _parentId = parentId;
}

/*---------------------------------------------------------------------------
    setAlarmInhibitFlag

    Sets the alarm inhibit of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setAlarmInhibitFlag(bool alarminhibit)
{
    _alarminhibitflag = alarminhibit;
}

/*---------------------------------------------------------------------------
    setControlInhibitFlag

    Sets the control inhibit of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControlInhibitFlag(bool controlinhibit)
{
    _controlinhibitflag = controlinhibit;
}


/*---------------------------------------------------------------------------
    setMaxDailyOperation

    Sets the max daily operations of the cap bank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setMaxDailyOperation(long maxdailyops)
{
    _maxdailyops = maxdailyops;
}


/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the cap bank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setCurrentDailyOperations(long operations)
{
    _dirty |= setVariableIfDifferent(_currentdailyoperations, operations);
}

/*---------------------------------------------------------------------------
    setMaxOperationDisableFlag

    Sets the max operation disable flag for the cap bank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setMaxOpsDisableFlag(bool maxopsdisable)
{
    _maxopsdisableflag = maxopsdisable;
}


/*---------------------------------------------------------------------------
    setOperationalState

    Sets the operational state of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setOperationalState(const string& operational)
{
    _operationalstate = operational;
}

/*---------------------------------------------------------------------------
    setOperationalState

    Sets the operational state of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setOperationalState(int value)
{
    _operationalstate = convertOperationalState(value);
}
/*---------------------------------------------------------------------------
    setControllerType

    Sets the controller type of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControllerType(const string& controllertype)
{
    _controllertype = controllertype;
}

/*---------------------------------------------------------------------------
    setControlDeviceId

    Sets the control device id of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControlDeviceId(long controldevice)
{
    _controldeviceid = controldevice;
}

/*---------------------------------------------------------------------------
    setControlPointId

    Sets the control point id of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControlPointId(long controlpoint)
{
    _controlpointid = controlpoint;
}

void CtiCCCapBank::setControlDeviceType(const string& controlDeviceType)
{
    _controlDeviceType = controlDeviceType;

}
/*---------------------------------------------------------------------------
    setBankSize

    Sets the bank size of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setBankSize(long size)
{
    _banksize = size;
}

/*---------------------------------------------------------------------------
    setTypeOfSwitch

    Sets the type of switch of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setTypeOfSwitch(const string& switchtype)
{
    _typeofswitch = switchtype;
}

/*---------------------------------------------------------------------------
    setSwitchManufacture

    Sets the switch manufacture of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setSwitchManufacture(const string& manufacture)
{
    _switchmanufacture = manufacture;
}

/*---------------------------------------------------------------------------
    setMapLocationId

    Sets the map location id of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setMapLocationId(const string& maplocation)
{
    _maplocationid = maplocation;
}

/*---------------------------------------------------------------------------
    setRecloseDelay

    Sets the RecloseDelay of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setRecloseDelay(long reclose)
{
    _reclosedelay = reclose;
}

/*---------------------------------------------------------------------------
    setControlOrder

    Sets the control order of the capbank in the list of the parent feeder
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControlOrder(float order)
{
    _controlorder = order;
}

/*---------------------------------------------------------------------------
    setTripOrder

    Sets the trip order of the capbank in the list of the parent feeder
---------------------------------------------------------------------------*/
void CtiCCCapBank::setTripOrder(float order)
{
    _triporder = order;
}

/*---------------------------------------------------------------------------
    setCloseOrder

    Sets the close order of the capbank in the list of the parent feeder
---------------------------------------------------------------------------*/
void CtiCCCapBank::setCloseOrder(float order)
{
    _closeorder = order;
}

/*---------------------------------------------------------------------------
    setStatusPointId

    Sets the status point id of the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setStatusPointId(long statuspoint)
{
    _statuspointid = statuspoint;
}
/*---------------------------------------------------------------------------
    setVerificationFlag

    Sets the verification flag, b4 capbank is exercised in the verification routine
---------------------------------------------------------------------------*/
void CtiCCCapBank::setVerificationFlag(bool verificationFlag)
{
    if (verificationFlag)
    {
        setVerificationDoneFlag(false);
        setVCtrlIndex(0);
    }

    _dirty |= setVariableIfDifferent(_verificationFlag,verificationFlag);
}

void CtiCCCapBank::setSelectedForVerificationFlag(bool individualVerificationFlag)
{
    _selectedForVerificationFlag = individualVerificationFlag;
}
bool CtiCCCapBank::isSelectedForVerification( ) const
{
    return _selectedForVerificationFlag;
}


void CtiCCCapBank::setPerformingVerificationFlag(bool performingVerificationFlag)
{
    _dirty |= setVariableIfDifferent(_performingVerificationFlag, performingVerificationFlag);
}
void CtiCCCapBank::setVerificationDoneFlag(bool verificationDoneFlag)
{
    _dirty |= setVariableIfDifferent(_verificationDoneFlag,verificationDoneFlag);
}

void CtiCCCapBank::setPorterRetFailFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_porterRetFailFlag, flag);
}
void CtiCCCapBank::setUnsolicitedPendingFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_unsolicitedPendingFlag, flag);
}

/*---------------------------------------------------------------------------
    setRetryOpenFailedFlag

    Sets the retry failed flag, b4 if a capbank has been tried after a failed state..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setRetryOpenFailedFlag(bool retryOpenFailedFlag)
{
    _dirty |= setVariableIfDifferent(_retryOpenFailedFlag, retryOpenFailedFlag);
}
/*---------------------------------------------------------------------------
    setRetryCloseFailedFlag

    Sets the retry failed flag, b4 if a capbank has been tried after a failed state..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setRetryCloseFailedFlag(bool retryCloseFailedFlag)
{
    _dirty |= setVariableIfDifferent(_retryCloseFailedFlag, retryCloseFailedFlag);
}
/*---------------------------------------------------------------------------
    setOvUvDisabledFlag

    Sets the ovUvDisabledFlag ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setOvUvDisabledFlag(bool ovUvDisabledFlag)
{
    _dirty |= setVariableIfDifferent(_ovUvDisabledFlag, ovUvDisabledFlag);
}

/*---------------------------------------------------------------------------
    setLocalControlFlag

    Sets the LocalControlFlag ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setLocalControlFlag(bool localControlFlag)
{
    _dirty |= setVariableIfDifferent(_localControlFlag, localControlFlag);
}

/*---------------------------------------------------------------------------
    setOvUvDisabledFlag

    Sets the ovUvDisabledFlag ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setOvUvSituationFlag(bool ovUvSituationFlag)
{
    _dirty |= setVariableIfDifferent(_ovuvSituationFlag, ovUvSituationFlag);
}

/*---------------------------------------------------------------------------
    setMaxDailyOpsHitFlag

    Sets the maxDailyOpsHitFlag ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setMaxDailyOpsHitFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_maxDailyOpsHitFlag, flag);
}
/*---------------------------------------------------------------------------
    setControlStatusPartialFlag(

    Sets the ControlStatusPartialFlag( ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControlStatusPartialFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_controlStatusPartialFlag, flag);
}
/*---------------------------------------------------------------------------
    setControlStatusSignificantFlag

    Sets the ControlStatusPartialFlag ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControlStatusSignificantFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_controlStatusSignificantFlag, flag);
}

/*---------------------------------------------------------------------------
    setControlStatusAbnQualityFlag

    Sets the ControlStatusAbnQualityFlag ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControlStatusAbnQualityFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_controlStatusAbnQualityFlag, flag);
}
/*---------------------------------------------------------------------------
    setControlStatusFailFlag

    Sets the ControlStatusFailFlag ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControlStatusFailFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_controlStatusFailFlag, flag);
}
/*---------------------------------------------------------------------------
    setControlStatusCommFailFlag

    Sets the ControlStatusCommFailFlag ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControlStatusCommFailFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_controlStatusCommFailFlag, flag);
}
/*---------------------------------------------------------------------------
    setControlStatusNoControlFlag

    Sets the ControlStatusNoControlFlag ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControlStatusNoControlFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_controlStatusNoControlFlag, flag);
}

/*---------------------------------------------------------------------------
    setControlStatusNoControlFlag

    Sets the ControlStatusNoControlFlag ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControlStatusUnSolicitedFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_controlStatusUnSolicitedFlag, flag);
}

/*---------------------------------------------------------------------------
    setReEnableOvUvFlag

    Sets the ReEnableOvUvFlag ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setReEnableOvUvFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_reEnableOvUvFlag, flag);
}


/*---------------------------------------------------------------------------
    setControlStatusQuality

    Sets the ControlStatusQuality ..
---------------------------------------------------------------------------*/
void CtiCCCapBank::setControlStatusQuality(CtiCCControlStatusQaulity quality, string partialPhaseInfo)
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

/*
    The input 'value' to this function is REQUIRED to be in host byte order
*/
void CtiCCCapBank::setIpAddress(unsigned long value)
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
}
void CtiCCCapBank::setUDPPort(long value)
{
    _dirty |= setVariableIfDifferent(_udpPortNumber, value);
}

void CtiCCCapBank::setReportedCBCLastControlReason(long value)
{
    _dirty |= setVariableIfDifferent(_reportedCBCLastControlReason, value);
}

void CtiCCCapBank::setPartialPhaseInfo(const string& info)
{
    _dirty |= setVariableIfDifferent(_partialPhaseInfo, info);
}
void CtiCCCapBank::setReportedCBCState(long value)
{
    _dirty |= setVariableIfDifferent(_reportedCBCState, value);
}

void CtiCCCapBank::setReportedCBCStateTime(const CtiTime& timestamp)
{
    _dirty |= setVariableIfDifferent(_reportedCBCStateTime, timestamp);
}

void CtiCCCapBank::setIgnoreFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_ignoreFlag, flag);
}

void CtiCCCapBank::setIgnoredReason(long value)
{
    _dirty |= setVariableIfDifferent(_ignoreReason, value);
}
void CtiCCCapBank::setIgnoreIndicatorTimeUpdated(const CtiTime& timestamp)
{
    _ignoreIndicatorTimeUpdated = timestamp;
}
void CtiCCCapBank::setUnsolicitedChangeTimeUpdated(const CtiTime& timestamp)
{
    _unsolicitedChangeTimeUpdated = timestamp;
}

void CtiCCCapBank::setBeforeVarsString(const string& before)
{
    _dirty |= setVariableIfDifferent(_sBeforeVars, before);
}

void CtiCCCapBank::setAfterVarsString(const string& after)
{
    _dirty |= setVariableIfDifferent(_sAfterVars, after);
}

void CtiCCCapBank::setPercentChangeString(const string& percent)
{
    _dirty |= setVariableIfDifferent(_sPercentChange, percent);
}

void CtiCCCapBank::setActionId(long actionId)
{
    _dirty |= setVariableIfDifferent(_actionId, actionId);
}

bool CtiCCCapBank::updateVerificationState(void)
{

    int ctrlIdx = getVCtrlIndex();
    _verificationDoneFlag = false;
    if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
    {
        CTILOG_DEBUG(dout, "CB: "<<getPaoId()<<" vCtrlIdx: "<< getVCtrlIndex() <<" prevControlStatus: "<< _prevVerificationControlStatus <<"  ControlStatus: " << getControlStatus());
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
            CTILOG_WARN(dout, "Adjusting CapBank Verification Control Index = 5 and setting Verification Done Flag");
            _verificationDoneFlag = true;
            ctrlIdx = 5;
            _retryFlag = false;
        }
        break;
    }

    setVCtrlIndex(ctrlIdx);

    return _verificationDoneFlag;
}

void CtiCCCapBank::setVCtrlIndex(int vCtrlIndex)
{
    _dirty |= setVariableIfDifferent(_vCtrlIndex, vCtrlIndex);
}

void CtiCCCapBank::setPreviousVerificationControlStatus(long status)
{
    _dirty |= setVariableIfDifferent(_prevVerificationControlStatus, status);
}
void CtiCCCapBank::setAssumedOrigVerificationState(int assumedOrigCapBankPos)
{
    _dirty |= setVariableIfDifferent(_assumedOrigCapBankPos,assumedOrigCapBankPos);
}

void CtiCCCapBank::initVerificationControlStatus()
{
   _verificationControlStatus = getControlStatus();
}

bool CtiCCCapBank::handlePointResponseDeltaChange(long pointId, double newDelta, bool staticDelta)
{
    return _pointResponseManager.handlePointResponseDeltaChange(pointId,newDelta,staticDelta);
}

bool CtiCCCapBank::updatePointResponseDelta(const CtiCCMonitorPoint & point)
{
    return _pointResponseManager.updatePointResponseDelta(point);
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
void CtiCCCapBank::setControlStatus(long status)
{

    if (_verificationFlag)
    {
        _dirty |= setVariableIfDifferent(_verificationControlStatus, status);
        _controlstatus = status;       //temporarily here!!!

    }
    else
    {
        _dirty |= setVariableIfDifferent(_controlstatus, status);
    }
}

void CtiCCCapBank::setControlRecentlySentFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_controlRecentlySentFlag, flag);
}

/*---------------------------------------------------------------------------
    setOperationAnalogPointId

    Sets the point id for the analog point that hold the number of number of
    operations performed on the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setOperationAnalogPointId(long operationpointid)
{
    _operationanalogpointid = operationpointid;
}

/*---------------------------------------------------------------------------
    setTotalOperations

    Sets the number of operations performed on this capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setTotalOperations(long operations)
{
    _dirty |= setVariableIfDifferent(_totaloperations, operations);
    _totaloperations = operations;
}


/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the feeder
---------------------------------------------------------------------------*/
void CtiCCCapBank::setTotalOperationsAndSendMsg(long operations, CtiMultiMsg_vec& pointChanges)
{
    if( _totaloperations != operations )
    {
        if( getOperationAnalogPointId() > 0 )
        {
            pointChanges.push_back(new CtiPointDataMsg(getOperationAnalogPointId(),operations,NormalQuality,AnalogPointType));
        }
        _dirty = true;
    }
    _totaloperations = operations;
}

/*---------------------------------------------------------------------------
    setLastStatusChangeTime

    Sets the timestamp of the last status point data msg sent by dispatch
---------------------------------------------------------------------------*/
void CtiCCCapBank::setLastStatusChangeTime(const CtiTime& laststatuschangetime)
{
    _dirty |= setVariableIfDifferent(_laststatuschangetime, laststatuschangetime);
}

/*---------------------------------------------------------------------------
    setTagsControlStatus

    Sets the tags of control status on the capbank
---------------------------------------------------------------------------*/
void CtiCCCapBank::setTagsControlStatus(long tags)
{
    _dirty |= setVariableIfDifferent(_tagscontrolstatus, tags);
}


void CtiCCCapBank::addAllCapBankPointsToMsg(std::set<long>& pointAddMsg)
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
}

Cti::CapControl::PointResponse CtiCCCapBank::getPointResponse(const CtiCCMonitorPoint & point)
{
    return _pointResponseManager.getPointResponse(point.getPointId());
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

bool CtiCCCapBank::addMonitorPoint(CtiCCMonitorPointPtr monPoint)
{
    for ( int index = 0; index < _monitorPoint.size(); ++index )
    {
        if ( _monitorPoint[index]->getPointId() == monPoint->getPointId() )
        {
            _monitorPoint[index] = monPoint;
            return false;
        }
    }

    _monitorPoint.push_back(monPoint);
    return true;
}

/*
    Inserts a new monitor point if it doesn't exist -- updates the guts of an existing one if it
        already exists.
*/
bool CtiCCCapBank::updateExistingMonitorPoint(CtiCCMonitorPointPtr monPoint)
{
    for ( int index = 0; index < _monitorPoint.size(); ++index )
    {
        if ( _monitorPoint[index]->getPointId() == monPoint->getPointId() )
        {
            _monitorPoint[index]->updateNonDynamicData( *monPoint );
            return false;
        }
    }

    _monitorPoint.push_back(monPoint);
    return true;
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

        if ( rightObj._twoWayPoints )
        {
            _twoWayPoints.reset( CtiCCTwoWayPointsFactory::Create(rightObj._controldeviceid, rightObj._controlDeviceType) );
        }
        else
        {
            _twoWayPoints.reset();
        }

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
int CtiCCCapBank::compareTo(const CtiCCCapBank* rightObj) const
{
    return _controlorder == rightObj->getControlOrder() ? 0 : (_controlorder > rightObj->getControlOrder() ? 1 : -1);
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

            if( Cti::Database::executeCommand( updater, __FILE__, __LINE__ ))
            {
                _dirty = false; // No error occured!
            }
        }
        else
        {
            CTILOG_INFO(dout, "Inserted Cap Bank into DynamicCCCapBank: " << getPaoName());

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

            if( Cti::Database::executeCommand( dbInserter, __FILE__, __LINE__, Cti::Database::LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) ))
            {
                _insertDynamicDataFlag = false;
                _dirty = false; // No error occured!
            }
        }
    }

    getOriginalParent().dumpDynamicData(conn, currentDateTime);
    getOperationStats().dumpDynamicData(conn, currentDateTime);

    for each (CtiCCMonitorPointPtr monPoint in getMonitorPoint())
    {
        monPoint->dumpDynamicData(conn,currentDateTime);
    }

    try
    {
        if ( isControlDeviceTwoWay() )
        {
            getTwoWayPoints().dumpDynamicData(conn,currentDateTime);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiCCCapBank::dumpDynamicPointResponseData(Cti::Database::DatabaseConnection& conn)
{
    _pointResponseManager.serializeUpdatedPointResponses( conn );
}

void CtiCCCapBank::dumpDynamicPointResponseData()
{
    Cti::Database::DatabaseConnection   connection;

    dumpDynamicPointResponseData( connection );
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
    return ( stringContainsIgnoreCase( getControlDeviceType(),"CBC 702") ||
             stringContainsIgnoreCase( getControlDeviceType(),"CBC DNP") ||
             stringContainsIgnoreCase( getControlDeviceType(),"CBC 802") );
}

int CtiCCCapBank::getPointIdByAttribute(const PointAttribute & attribute)
{
    return getTwoWayPoints().getPointIdByAttribute(attribute);
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
