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
#include "logger.h"
#include "resolvers.h"

#include "utility.h"
//#include "rwutil.h"
using namespace std;
extern ULONG _CC_DEBUG;
extern BOOL _USE_FLIP_FLAG;

RWDEFINE_COLLECTABLE( CtiCCCapBank, CTICCCAPBANK_ID )


/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCCapBank::CtiCCCapBank() 
{
    _twoWayPoints = NULL;
    _ovuvSituationFlag = false;
}

CtiCCCapBank::CtiCCCapBank(RWDBReader& rdr) 
{
    restore(rdr);
     _monitorPoint.clear();
     _pointResponses.clear();
     _twoWayPoints = NULL;
     _ovuvSituationFlag = false;
     _operationStats.setPAOId(_paoid);
     _confirmationStats.setPAOId(_paoid);
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
    _pointIds.clear();
    if (!_monitorPoint.empty()) 
    {
        for (int i = 0; i < _monitorPoint.size(); i++)
        {
            CtiCCMonitorPointPtr monPoint = (CtiCCMonitorPointPtr)_monitorPoint[i];
            delete monPoint;
        }
        _monitorPoint.clear();
    }

    if (!_pointResponses.empty()) 
    {
        for (int i = 0; i < _pointResponses.size(); i++)
        {
            CtiCCPointResponsePtr point = (CtiCCPointResponsePtr)_pointResponses[i];
            delete point;
        }
        _pointResponses.clear();
    }
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
          _twoWayPoints = new CtiCCTwoWayPoints(_controldeviceid);
    
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
const string& CtiCCCapBank::getPAOCategory() const
{
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the cap bank
---------------------------------------------------------------------------*/
const string& CtiCCCapBank::getPAOClass() const
{
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the cap bank
---------------------------------------------------------------------------*/
const string& CtiCCCapBank::getPAOName() const
{
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the cap bank
---------------------------------------------------------------------------*/
const string& CtiCCCapBank::getPAOType() const
{
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the cap bank
---------------------------------------------------------------------------*/
const string& CtiCCCapBank::getPAODescription() const
{
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getIPAddress

    Returns the pao description of the cap bank
---------------------------------------------------------------------------*/
const string& CtiCCCapBank::getIpAddress() const
{
    return _ipAddress;
}
LONG CtiCCCapBank::getUDPPort() const
{
    return _udpPortNumber;
}

LONG CtiCCCapBank::getReportedCBCState() const
{
    return _reportedCBCState;
}

LONG CtiCCCapBank::getReportedCBCLastControlReason() const
{
    return _reportedCBCLastControlReason;
}


const CtiTime& CtiCCCapBank::getReportedCBCStateTime() const
{
    return _reportedCBCStateTime;
}

BOOL CtiCCCapBank::getIgnoreFlag() const
{
    return _ignoreFlag;
}


LONG CtiCCCapBank::getIgnoredReason() const
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
    getMaxDailyOperation

    Returns the max daily operations of the cap bank
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getMaxDailyOps() const
{
    return _maxdailyops;
}
/*---------------------------------------------------------------------------
    getCurrentDailyOperations

    Returns the current daily operations of the cap bank
---------------------------------------------------------------------------*/
LONG CtiCCCapBank::getCurrentDailyOperations() const
{
    return _currentdailyoperations;
}

/*---------------------------------------------------------------------------
    getMaxOperationDisableFlag

    Returns the max operation disable flag for the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCCapBank::getMaxOpsDisableFlag() const
{
    return _maxopsdisableflag;
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


const string& CtiCCCapBank::getControlDeviceType() const
{
    return _controlDeviceType;
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
LONG CtiCCCapBank::getRecloseDelay() const
{
    return _reclosedelay;
}

/*---------------------------------------------------------------------------
    getControlOrder

    Returns the control order of the cap bank in the list of a feeder
---------------------------------------------------------------------------*/
FLOAT CtiCCCapBank::getControlOrder() const
{
    return _controlorder;
}

/*---------------------------------------------------------------------------
    getTripOrder

    Returns the trip order of the cap bank in the list of a feeder
---------------------------------------------------------------------------*/
FLOAT CtiCCCapBank::getTripOrder() const
{
    return _triporder;
}

/*---------------------------------------------------------------------------
    getCloseOrder

    Returns the close order of the cap bank in the list of a feeder
---------------------------------------------------------------------------*/
FLOAT CtiCCCapBank::getCloseOrder() const
{
    return _closeorder;
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

BOOL CtiCCCapBank::getPorterRetFailFlag() const
{
    return _porterRetFailFlag;
}

BOOL CtiCCCapBank::getUnsolicitedPendingFlag() const
{
    return _unsolicitedPendingFlag;
}

BOOL CtiCCCapBank::getRetryOpenFailedFlag() const
{
    return _retryOpenFailedFlag;
}

BOOL CtiCCCapBank::getRetryCloseFailedFlag() const
{
    return _retryCloseFailedFlag;
}
BOOL CtiCCCapBank::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}
BOOL CtiCCCapBank::getLocalControlFlag() const
{
    return _localControlFlag;
}
BOOL CtiCCCapBank::getControlRecentlySentFlag() const
{
    return _controlRecentlySentFlag;
}

BOOL CtiCCCapBank::getMaxDailyOpsHitFlag() const
{
    return _maxDailyOpsHitFlag;
}
BOOL CtiCCCapBank::getOvUvSituationFlag() const
{
    return _ovuvSituationFlag;
}
BOOL CtiCCCapBank::getControlStatusPartialFlag() const
{
    return _controlStatusPartialFlag;
}
BOOL CtiCCCapBank::getControlStatusSignificantFlag() const
{
    return _controlStatusSignificantFlag;
}
BOOL CtiCCCapBank::getControlStatusAbnQualityFlag() const
{
    return _controlStatusAbnQualityFlag;
}
BOOL CtiCCCapBank::getControlStatusFailFlag() const
{
    return _controlStatusFailFlag;
}
BOOL CtiCCCapBank::getControlStatusCommFailFlag() const
{
    return _controlStatusCommFailFlag;
}
BOOL CtiCCCapBank::getControlStatusNoControlFlag() const
{
    return _controlStatusNoControlFlag;
}
BOOL CtiCCCapBank::getControlStatusUnSolicitedFlag() const
{
    return _controlStatusUnSolicitedFlag;
}


LONG CtiCCCapBank::getControlStatusQuality() const
{
    return _controlStatusQuality;
}
BOOL CtiCCCapBank::getReEnableOvUvFlag() const
{
    return _reEnableOvUvFlag;
}

BOOL CtiCCCapBank::getSendAllCommandFlag() const
{
    return _sendAllCommandFlag;
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
   return _controlstatus;
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
const CtiTime& CtiCCCapBank::getLastStatusChangeTime() const
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
float CtiCCCapBank::getOriginalSwitchingOrder() const
{
    return _originalswitchingorder;
}

float CtiCCCapBank::getOriginalCloseOrder() const
{
    return _originalcloseorder;
}

float CtiCCCapBank::getOriginalTripOrder() const
{
    return _originaltriporder;
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
CtiCCCapBank& CtiCCCapBank::setPAOCategory(const string& category)
{
    _paocategory = category;

    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAOClass(const string& pclass)
{
    _paoclass = pclass;

    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAOName(const string& name)
{
    _paoname = name;

    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAOType(const string& _type)
{
    _paotype = _type;

    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAODescription(const string& description)
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
    setMaxDailyOperation

    Sets the max daily operations of the cap bank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setMaxDailyOperation(LONG maxdailyops)
{
    _maxdailyops = maxdailyops;

    return *this;
}


/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the current daily operations of the cap bank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setCurrentDailyOperations(LONG operations)
{
    if( _currentdailyoperations != operations )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _currentdailyoperations = operations;
    return *this;
}

/*---------------------------------------------------------------------------
    setMaxOperationDisableFlag

    Sets the max operation disable flag for the cap bank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setMaxOpsDisableFlag(BOOL maxopsdisable)
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



CtiCCCapBank& CtiCCCapBank::setControlDeviceType(const string& controlDeviceType)
{
    _controlDeviceType = controlDeviceType;

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
CtiCCCapBank& CtiCCCapBank::setRecloseDelay(LONG reclose)
{
    _reclosedelay = reclose;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlOrder

    Sets the control order of the capbank in the list of the parent feeder
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlOrder(FLOAT order)
{
    _controlorder = order;

    return *this;
}

/*---------------------------------------------------------------------------
    setTripOrder

    Sets the trip order of the capbank in the list of the parent feeder
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setTripOrder(FLOAT order)
{
    _triporder = order;

    return *this;
}

/*---------------------------------------------------------------------------
    setCloseOrder

    Sets the close order of the capbank in the list of the parent feeder
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setCloseOrder(FLOAT order)
{
    _closeorder = order;

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

CtiCCCapBank& CtiCCCapBank::setPorterRetFailFlag(BOOL flag)
{
    if (_porterRetFailFlag != flag)
    {
        _dirty = TRUE;
    }
    _porterRetFailFlag = flag;
    return *this;
}
CtiCCCapBank& CtiCCCapBank::setUnsolicitedPendingFlag(BOOL flag)
{
    if (_unsolicitedPendingFlag != flag)
    {
        _dirty = TRUE;
    }
    _unsolicitedPendingFlag = flag;
    return *this;
}

/*---------------------------------------------------------------------------
    setRetryOpenFailedFlag
    
    Sets the retry failed flag, b4 if a capbank has been tried after a failed state..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setRetryOpenFailedFlag(BOOL retryOpenFailedFlag)
{

    if (_retryOpenFailedFlag != retryOpenFailedFlag)
    {
        _dirty = TRUE;
    }
    _retryOpenFailedFlag = retryOpenFailedFlag;

    return *this;
}
/*---------------------------------------------------------------------------
    setRetryCloseFailedFlag
    
    Sets the retry failed flag, b4 if a capbank has been tried after a failed state..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setRetryCloseFailedFlag(BOOL retryCloseFailedFlag)
{

    if (_retryCloseFailedFlag != retryCloseFailedFlag)
    {
        _dirty = TRUE;
    }
    _retryCloseFailedFlag = retryCloseFailedFlag;

    return *this;
}
/*---------------------------------------------------------------------------
    setOvUvDisabledFlag
    
    Sets the ovUvDisabledFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOvUvDisabledFlag(BOOL ovUvDisabledFlag)
{

    if (_ovUvDisabledFlag != ovUvDisabledFlag)
    {
        _dirty = TRUE;
    }
    _ovUvDisabledFlag = ovUvDisabledFlag;

    return *this;
}

/*---------------------------------------------------------------------------
    setLocalControlFlag
    
    Sets the LocalControlFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setLocalControlFlag(BOOL localControlFlag)
{

    if (_localControlFlag != localControlFlag)
    {
        _dirty = TRUE;
    }
    _localControlFlag = localControlFlag;

    return *this;
}

/*---------------------------------------------------------------------------
    setOvUvDisabledFlag
    
    Sets the ovUvDisabledFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOvUvSituationFlag(BOOL ovUvSituationFlag)
{

    if (_ovuvSituationFlag != ovUvSituationFlag)
    {
        _dirty = TRUE;
    }
    _ovuvSituationFlag = ovUvSituationFlag;

    return *this;
}

/*---------------------------------------------------------------------------
    setMaxDailyOpsHitFlag
    
    Sets the maxDailyOpsHitFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setMaxDailyOpsHitFlag(BOOL flag)
{

    if (_maxDailyOpsHitFlag != flag)
    {
        _dirty = TRUE;
    }
    _maxDailyOpsHitFlag = flag;

    return *this;
}
/*---------------------------------------------------------------------------
    setControlStatusPartialFlag(
    
    Sets the ControlStatusPartialFlag( ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusPartialFlag(BOOL flag)
{

    if (_controlStatusPartialFlag != flag)
    {
        _dirty = TRUE;
    }
    _controlStatusPartialFlag = flag;

    return *this;
}
/*---------------------------------------------------------------------------
    setControlStatusSignificantFlag
    
    Sets the ControlStatusPartialFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusSignificantFlag(BOOL flag)
{

    if (_controlStatusSignificantFlag != flag)
    {
        _dirty = TRUE;
    }
    _controlStatusSignificantFlag = flag;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlStatusAbnQualityFlag
    
    Sets the ControlStatusAbnQualityFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusAbnQualityFlag(BOOL flag)
{

    if (_controlStatusAbnQualityFlag != flag)
    {
        _dirty = TRUE;
    }
    _controlStatusAbnQualityFlag = flag;

    return *this;
}
/*---------------------------------------------------------------------------
    setControlStatusFailFlag
    
    Sets the ControlStatusFailFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusFailFlag(BOOL flag)
{

    if (_controlStatusFailFlag != flag)
    {
        _dirty = TRUE;
    }
    _controlStatusFailFlag = flag;

    return *this;
}
/*---------------------------------------------------------------------------
    setControlStatusCommFailFlag
    
    Sets the ControlStatusCommFailFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusCommFailFlag(BOOL flag)
{

    if (_controlStatusCommFailFlag != flag)
    {
        _dirty = TRUE;
    }
    _controlStatusCommFailFlag = flag;

    return *this;
}
/*---------------------------------------------------------------------------
    setControlStatusNoControlFlag
    
    Sets the ControlStatusNoControlFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusNoControlFlag(BOOL flag)
{

    if (_controlStatusNoControlFlag != flag)
    {
        _dirty = TRUE;
    }
    _controlStatusNoControlFlag = flag;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlStatusNoControlFlag
    
    Sets the ControlStatusNoControlFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusUnSolicitedFlag(BOOL flag)
{

    if (_controlStatusUnSolicitedFlag != flag)
    {
        _dirty = TRUE;
    }
    _controlStatusUnSolicitedFlag = flag;

    return *this;
}

/*---------------------------------------------------------------------------
    setReEnableOvUvFlag
    
    Sets the ReEnableOvUvFlag ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setReEnableOvUvFlag(BOOL flag)
{

    if (_reEnableOvUvFlag != flag)
    {
        _dirty = TRUE;
    }
    _reEnableOvUvFlag = flag;

    return *this;
}


/*---------------------------------------------------------------------------
    setControlStatusQuality
    
    Sets the ControlStatusQuality ..
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatusQuality(CtiCCControlStatusQaulity quality)
{

    _controlStatusQuality = quality;
    switch (quality)
    {
        case CC_Partial:
        {
            setControlStatusPartialFlag(TRUE);
            setControlStatusSignificantFlag(FALSE);
            setControlStatusAbnQualityFlag(FALSE);
            setControlStatusFailFlag(FALSE);
            setControlStatusCommFailFlag(FALSE);
            setControlStatusNoControlFlag(FALSE);
            setControlStatusUnSolicitedFlag(FALSE);
            break;
        }
       
        case CC_Significant:
        {
            setControlStatusPartialFlag(FALSE);
            setControlStatusSignificantFlag(TRUE);
            setControlStatusAbnQualityFlag(FALSE);
            setControlStatusFailFlag(FALSE);
            setControlStatusCommFailFlag(FALSE);
            setControlStatusNoControlFlag(FALSE);
            setControlStatusUnSolicitedFlag(FALSE);
            break;
       
        }
        case CC_AbnormalQuality:
        {
            setControlStatusPartialFlag(FALSE);
            setControlStatusSignificantFlag(FALSE);
            setControlStatusAbnQualityFlag(TRUE);
            setControlStatusFailFlag(FALSE);
            setControlStatusCommFailFlag(FALSE);
            setControlStatusNoControlFlag(FALSE);
            setControlStatusUnSolicitedFlag(FALSE);
            break;
        }
        case CC_Fail:
        {
            setControlStatusPartialFlag(FALSE);
            setControlStatusSignificantFlag(FALSE);
            setControlStatusAbnQualityFlag(FALSE);
            setControlStatusFailFlag(TRUE);
            setControlStatusCommFailFlag(FALSE);
            setControlStatusNoControlFlag(FALSE);
            setControlStatusUnSolicitedFlag(FALSE);
            break;
        }
        case CC_CommFail:
        {
            setControlStatusPartialFlag(FALSE);
            setControlStatusSignificantFlag(FALSE);
            setControlStatusAbnQualityFlag(FALSE);
            setControlStatusFailFlag(FALSE);
            setControlStatusCommFailFlag(TRUE);
            setControlStatusNoControlFlag(FALSE);
            setControlStatusUnSolicitedFlag(FALSE);
            break;
        }
        case CC_NoControl:
        {
            setControlStatusPartialFlag(FALSE);
            setControlStatusSignificantFlag(FALSE);
            setControlStatusAbnQualityFlag(FALSE);
            setControlStatusFailFlag(FALSE);
            setControlStatusCommFailFlag(FALSE);
            setControlStatusNoControlFlag(TRUE);
            setControlStatusUnSolicitedFlag(FALSE);
            break;
        }
        case CC_UnSolicited:
        {
            setControlStatusPartialFlag(FALSE);
            setControlStatusSignificantFlag(FALSE);
            setControlStatusAbnQualityFlag(FALSE);
            setControlStatusFailFlag(FALSE);
            setControlStatusCommFailFlag(FALSE);
            setControlStatusNoControlFlag(FALSE);
            setControlStatusUnSolicitedFlag(TRUE);
            break;
        }
        case CC_Normal:
        default:
        {
            setControlStatusPartialFlag(FALSE);
            setControlStatusSignificantFlag(FALSE);
            setControlStatusAbnQualityFlag(FALSE);
            setControlStatusFailFlag(FALSE);
            setControlStatusCommFailFlag(FALSE);
            setControlStatusNoControlFlag(FALSE);
            setControlStatusUnSolicitedFlag(FALSE);
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



CtiCCCapBank& CtiCCCapBank::setIpAddress(ULONG value)
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
CtiCCCapBank& CtiCCCapBank::setUDPPort(LONG value)
{
    if (_udpPortNumber != value)
    {
        _dirty = TRUE;
    }
    _udpPortNumber = value;

    return *this;

}

CtiCCCapBank& CtiCCCapBank::setReportedCBCLastControlReason(LONG value)
{
    if (_reportedCBCLastControlReason != value)
    {
        _dirty = TRUE;
    }
    _reportedCBCLastControlReason = value;

    return *this;

}


CtiCCCapBank& CtiCCCapBank::setReportedCBCState(LONG value)
{
    if (_reportedCBCState != value)
    {
        _dirty = TRUE;
    }
    _reportedCBCState = value;

    return *this;

}

CtiCCCapBank& CtiCCCapBank::setReportedCBCStateTime(const CtiTime& timestamp)
{
    if (_reportedCBCStateTime != timestamp)
    {
        _dirty = TRUE;
    }
    _reportedCBCStateTime = timestamp;

    return *this;

}

CtiCCCapBank& CtiCCCapBank::setIgnoreFlag(BOOL flag)
{
    if (_ignoreFlag != flag)
    {
        _dirty = TRUE;
    }
    _ignoreFlag = flag;

    return *this;

}

CtiCCCapBank& CtiCCCapBank::setIgnoredReason(LONG value)
{
    if (_ignoreReason != value)
    {
        _dirty = TRUE;
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
        _dirty = TRUE;
    }
    _sBeforeVars = before;
    return *this;
}

CtiCCCapBank& CtiCCCapBank::setAfterVarsString(const string& after)
{
    if (_sAfterVars != after)
    {
        _dirty = TRUE;
    }
    _sAfterVars = after;
    return *this;
}

CtiCCCapBank& CtiCCCapBank::setPercentChangeString(const string& percent)
{
    if (_sPercentChange != percent)
    {
        _dirty = TRUE;
    }
    _sPercentChange = percent;
    return *this;
}

CtiCCCapBank& CtiCCCapBank::setSendAllCommandFlag(BOOL flag)
{
    _sendAllCommandFlag = flag;
    return *this;
}

BOOL CtiCCCapBank::updateVerificationState(void)
{

    int ctrlIdx = getVCtrlIndex();
    _verificationDoneFlag = FALSE;
    if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " CB: "<<getPAOId()<<" vCtrlIdx: "<< getVCtrlIndex() <<" prevControlStatus: "<< _prevVerificationControlStatus <<"  ControlStatus: " << getControlStatus() << endl;
    }
    switch (ctrlIdx)
    {
    case 1:
        {
            setPreviousVerificationControlStatus(getControlStatus());
            if ( stringContainsIgnoreCase(getControlDeviceType(),"CBC 701") &&
                  _USE_FLIP_FLAG == TRUE &&
                  (getControlStatus() == OpenFail || getControlStatus() == CloseFail) )
            {  
                _verificationDoneFlag = TRUE;
                ctrlIdx = 5;
            }
            else
            {
                _verificationDoneFlag = FALSE;
                _retryFlag = FALSE;
                ctrlIdx++;
            }
            break;
        }
    case 2:
        {
            if (!stringContainsIgnoreCase(getControlDeviceType(),"CBC 701") && 
                _USE_FLIP_FLAG == TRUE)
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
                         _prevVerificationControlStatus == CloseFail )
                    {
                        if (getControlStatus() == OpenFail ||
                            getControlStatus() == CloseFail ) 
                        {
                            _verificationDoneFlag = TRUE;
                            ctrlIdx = 5;
                            _retryFlag = FALSE;
                        }
                        else
                        {
                            ctrlIdx++;
                            _retryFlag = FALSE;
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
                        }
                    }
                }
            }
            else // CBC 7000 Flip Verification...
            {
                /*if ( (getControlStatus() == Open || getControlStatus() == Close) &&
                      getControlStatus() != _assumedOrigCapBankPos )
                { */
                    _verificationDoneFlag = TRUE;
                    ctrlIdx = 5;
                    _retryFlag = FALSE;
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
                if (!stringContainsIgnoreCase(getControlDeviceType(),"CBC 701") && 
                    _USE_FLIP_FLAG == TRUE)
                {
                    ctrlIdx = 5; 
                    _verificationDoneFlag = TRUE;
                    _retryFlag = FALSE;
                }
                else if (_retryFlag)
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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " ***WARNING*** Adjusting CapBank Verification Control Index = 5 and setting Verification Done Flag ==> " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            _verificationDoneFlag = TRUE;
            ctrlIdx = 5;
            _retryFlag = FALSE;
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

CtiCCCapBank& CtiCCCapBank::updatePointResponseDeltas(CtiCCMonitorPoint* point)
{
    for (LONG j=0; j<getPointResponse().size(); j++)
    {
        CtiCCPointResponse* pResponse = (CtiCCPointResponse*)getPointResponse()[j];

        if (point->getPointId() == pResponse->getPointId())
        {
            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " MULTIVOLT: Bank ID: " <<getPAOName()<<" Point ID: "<<pResponse->getPointId()<<" preOpValue: "<<pResponse->getPreOpValue() <<" currentValue: "<<point->getValue()<< endl;
            }

            //if (pResponse->getDelta() != 0) 
            DOUBLE nInAvg = (point->getNInAvg()!=0?point->getNInAvg():1);
            DOUBLE fabsy = fabs(pResponse->getPreOpValue() - point->getValue());  
            DOUBLE delta = ( (pResponse->getDelta()*(nInAvg -1)) + 
                                  fabs(pResponse->getPreOpValue() - point->getValue()) ) / 
                                  nInAvg;
            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " MULTIVOLT: Bank ID: " <<getPAOName()<<" Point ID: "<<pResponse->getPointId()<<" fabs: "<<fabsy <<" delta: "<<delta<< endl;
            }
            {
                pResponse->setDelta( ( (pResponse->getDelta()*(nInAvg -1.0)) + 
                                  fabs(pResponse->getPreOpValue() - point->getValue()) ) / 
                                  nInAvg);
            }
            
            break;
        }
    }
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
                dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }*/
            _dirty = TRUE;
        }
        _controlstatus = status;
    }

    return *this;
}

CtiCCCapBank& CtiCCCapBank::setControlRecentlySentFlag(BOOL flag)
{
    if (_controlRecentlySentFlag != flag)
    {
        _dirty = TRUE;
    }
    _controlRecentlySentFlag = flag;

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
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
CtiCCCapBank& CtiCCCapBank::setLastStatusChangeTime(const CtiTime& laststatuschangetime)
{
    if( _laststatuschangetime != laststatuschangetime )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
CtiCCCapBank& CtiCCCapBank::setOriginalSwitchingOrder(float origorder)
{
    if( _originalswitchingorder != origorder )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _originalswitchingorder = origorder;
    return *this;
}
CtiCCCapBank& CtiCCCapBank::setOriginalCloseOrder(float origorder)
{
    if( _originalcloseorder != origorder )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _originalcloseorder = origorder;
    return *this;
}
CtiCCCapBank& CtiCCCapBank::setOriginalTripOrder(float origorder)
{
    if( _originaltriporder != origorder )
    {
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }*/
        _dirty = TRUE;
    }
    _originaltriporder = origorder;
    return *this;
}

CtiCCCapBank& CtiCCCapBank::addAllCapBankPointsToMsg(CtiCommandMsg *pointAddMsg)
{

    if( getStatusPointId() > 0 )
    {
        pointAddMsg->insert(getStatusPointId());
    }
    if( getOperationAnalogPointId() > 0 )
    {
        pointAddMsg->insert(getOperationAnalogPointId());
    }
 
    return *this;
}


CtiCCPointResponse* CtiCCCapBank::getPointResponse(CtiCCMonitorPoint* point)
{
    for (LONG j=0; j<getPointResponse().size(); j++)
    {
        CtiCCPointResponse* pResponse = (CtiCCPointResponse*)getPointResponse()[j];

        if (point->getPointId() == pResponse->getPointId())
        {
            return pResponse;
        }
    }
    return NULL;

}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCCapBank::restoreGuts(RWvistream& istrm)
{
    CtiTime tempTime1;
    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag
    >> _parentId
    >> _maxdailyops
    >> _maxopsdisableflag
    >> _alarminhibitflag
    >> _controlinhibitflag
    >> _operationalstate
    >> _controllertype
    >> _controldeviceid
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
    >> _originalfeederid
    >> _currentdailyoperations
    >> _ignoreFlag
    >> _ignoreReason
    >> _ovUvDisabledFlag
    >> _triporder
    >> _closeorder
    >> _controlDeviceType;
    istrm >> _sBeforeVars;
    istrm >> _sAfterVars;
    istrm >> _sPercentChange;
    istrm >> _maxDailyOpsHitFlag;
    istrm >> _ovuvSituationFlag;
    istrm >> _controlStatusQuality;
    istrm >> _localControlFlag;
    _laststatuschangetime = CtiTime(tempTime1);
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
    << _maxdailyops
    << _maxopsdisableflag
    << _alarminhibitflag
    << _controlinhibitflag
    << _operationalstate;

    ostrm << _controllertype;

    ostrm << _controldeviceid
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
    << _laststatuschangetime
    << _tagscontrolstatus
    << _originalfeederid
    << _currentdailyoperations
    << _ignoreFlag   
    << _ignoreReason
    << _ovUvDisabledFlag;
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
        _parentId = right._parentId;
        _alarminhibitflag = right._alarminhibitflag;
        _controlinhibitflag = right._controlinhibitflag;
        _maxdailyops = right._maxdailyops;
        _currentdailyoperations = right._currentdailyoperations;
        _maxopsdisableflag = right._maxopsdisableflag;
        _operationalstate = right._operationalstate;
        _controllertype = right._controllertype;
        _controldeviceid = right._controldeviceid;
        _controlpointid = right._controlpointid;
        _controlDeviceType = right._controlDeviceType;
        _banksize = right._banksize;
        _typeofswitch = right._typeofswitch;
        _switchmanufacture = right._switchmanufacture;
        _maplocationid = right._maplocationid;
        _reclosedelay = right._reclosedelay;
        _controlorder = right._controlorder;
        _triporder = right._triporder;
        _closeorder = right._closeorder;
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
        _verificationFlag = right._verificationFlag;          
        _performingVerificationFlag = right._performingVerificationFlag;
        _verificationDoneFlag = right._verificationDoneFlag;      
        _retryOpenFailedFlag = right._retryOpenFailedFlag;
        _retryCloseFailedFlag = right._retryCloseFailedFlag;           
        _ovUvDisabledFlag = right._ovUvDisabledFlag;
        _maxDailyOpsHitFlag = right._maxDailyOpsHitFlag;
        _controlStatusPartialFlag =     right._controlStatusPartialFlag;   
        _controlStatusSignificantFlag = right._controlStatusSignificantFlag;
        _controlStatusAbnQualityFlag = right._controlStatusAbnQualityFlag;
        _controlStatusFailFlag = right._controlStatusFailFlag;
        _controlStatusCommFailFlag = right._controlStatusCommFailFlag;
        _controlStatusNoControlFlag = right._controlStatusNoControlFlag;
        _controlStatusUnSolicitedFlag = right._controlStatusUnSolicitedFlag;
        _controlStatusQuality = right._controlStatusQuality;
        _reEnableOvUvFlag = right._reEnableOvUvFlag;
        _localControlFlag = right._localControlFlag;
        _controlRecentlySentFlag = right._controlRecentlySentFlag;
        _porterRetFailFlag = right._porterRetFailFlag;
        _unsolicitedPendingFlag = right._unsolicitedPendingFlag;

        _sendAllCommandFlag = right._sendAllCommandFlag;
        _ignoreReasonTimeUpdated = right._ignoreReasonTimeUpdated;
        _ignoreIndicatorTimeUpdated = right._ignoreIndicatorTimeUpdated;
        _unsolicitedChangeTimeUpdated = right._unsolicitedChangeTimeUpdated;

        _ipAddress = right._ipAddress;
        _udpPortNumber = right._udpPortNumber;
        _reportedCBCLastControlReason = right._reportedCBCLastControlReason;
        _reportedCBCState = right._reportedCBCState;
        _reportedCBCStateTime = right._reportedCBCStateTime;

        _ignoreFlag = right._ignoreFlag;
        _ignoreReason = right._ignoreReason;
        _sAfterVars = right._sAfterVars;
        _sBeforeVars = right._sBeforeVars;
        _sPercentChange = right._sPercentChange;
        _ovuvSituationFlag = right._ovuvSituationFlag;

        _operationStats = right._operationStats;
        if (right._twoWayPoints != NULL)
        {
            _twoWayPoints = new CtiCCTwoWayPoints(*right._twoWayPoints);

        }
        else
            _twoWayPoints = NULL;
        
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
    CtiTime currentDateTime = CtiTime();
    CtiTime dynamicTimeStamp;
    string tempBoolString;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> _paotype;
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setDisableFlag(tempBoolString=="y"?TRUE:FALSE);
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
    setMaxOpsDisableFlag(tempBoolString=="y"?TRUE:FALSE);
            

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
    setOriginalFeederId(0);
    setOriginalSwitchingOrder(0.0);
    setOriginalCloseOrder(0.0);
    setOriginalTripOrder(0.0);
    setAssumedOrigVerificationState(CtiCCCapBank::Open);
    setPreviousVerificationControlStatus(CtiCCCapBank::Open);
    setVCtrlIndex(-1);
    setVerificationFlag(FALSE);
    setRetryOpenFailedFlag(FALSE);
    setRetryCloseFailedFlag(FALSE);
    setOvUvDisabledFlag(FALSE);
    setMaxDailyOpsHitFlag(FALSE);
    setControlStatusPartialFlag(FALSE);     
    setControlStatusSignificantFlag(FALSE);
    setControlStatusAbnQualityFlag(FALSE);
    setControlStatusQuality(CC_Normal); 
    setReEnableOvUvFlag(FALSE);
    setLocalControlFlag(FALSE);
    setControlRecentlySentFlag(FALSE);
    setPorterRetFailFlag(FALSE);
    setUnsolicitedPendingFlag(FALSE);

    setOvUvSituationFlag(FALSE);
    _additionalFlags = string("NNNNNNNNNNNNNNNNNNNN");
    setCurrentDailyOperations(0);

    setIpAddress(0);
    setUDPPort(0);
    setReportedCBCLastControlReason(0);
    setReportedCBCState(-1);
    setReportedCBCStateTime(gInvalidCtiTime);

    setIgnoreFlag(FALSE);
    setIgnoredReason(0);
    setBeforeVarsString("none");
    setAfterVarsString("none");
    setPercentChangeString("none");

    _sendAllCommandFlag = FALSE;
    setIgnoreReasonTimeUpdated(gInvalidCtiTime);
    setIgnoreIndicatorTimeUpdated(gInvalidCtiTime);
    setUnsolicitedChangeTimeUpdated(gInvalidCtiTime);
    _insertDynamicDataFlag = TRUE;
    _dirty = TRUE;

}

BOOL CtiCCCapBank::getInsertDynamicDataFlag() const
{
    return _insertDynamicDataFlag;
}

void CtiCCCapBank::setDynamicData(RWDBReader& rdr)
{

    CtiTime dynamicTimeStamp;
    rdr["controlstatus"] >> _controlstatus;
    rdr["totaloperations"] >> _totaloperations;
    rdr["laststatuschangetime"] >> _laststatuschangetime;
    rdr["tagscontrolstatus"] >> _tagscontrolstatus;
    rdr["ctitimestamp"] >> dynamicTimeStamp;
    rdr["originalfeederid"] >> _originalfeederid;
    rdr["originalswitchingorder"] >> _originalswitchingorder;
    rdr["assumedstartverificationstatus"] >> _assumedOrigCapBankPos;
    rdr["prevverificationcontrolstatus"] >> _prevVerificationControlStatus;
    rdr["verificationcontrolindex"] >> _vCtrlIndex;

    rdr["additionalflags"] >> _additionalFlags;
    std::transform(_additionalFlags.begin(), _additionalFlags.end(), _additionalFlags.begin(), tolower);
    _verificationFlag = (_additionalFlags[0]=='y'?TRUE:FALSE);
    _performingVerificationFlag = (_additionalFlags[1]=='y'?TRUE:FALSE);
    _verificationDoneFlag = (_additionalFlags[2]=='y'?TRUE:FALSE);
    _retryOpenFailedFlag = (_additionalFlags[3]=='y'?TRUE:FALSE);
    _retryCloseFailedFlag = (_additionalFlags[4]=='y'?TRUE:FALSE);
    _ovUvDisabledFlag = (_additionalFlags[5]=='y'?TRUE:FALSE);
    _maxDailyOpsHitFlag = (_additionalFlags[6]=='y'?TRUE:FALSE);
    _ovuvSituationFlag = (_additionalFlags[7]=='y'?TRUE:FALSE);
    _controlStatusPartialFlag = (_additionalFlags[8]=='y'?TRUE:FALSE);   
    _controlStatusSignificantFlag = (_additionalFlags[9]=='y'?TRUE:FALSE);
    _controlStatusAbnQualityFlag = (_additionalFlags[10]=='y'?TRUE:FALSE);
    _controlStatusFailFlag = (_additionalFlags[11]=='y'?TRUE:FALSE);
    _controlStatusCommFailFlag = (_additionalFlags[12]=='y'?TRUE:FALSE);
    _controlStatusNoControlFlag = (_additionalFlags[13]=='y'?TRUE:FALSE);
    _controlStatusUnSolicitedFlag = (_additionalFlags[14]=='y'?TRUE:FALSE);
    _reEnableOvUvFlag = (_additionalFlags[15]=='y'?TRUE:FALSE);
    _localControlFlag = (_additionalFlags[16]=='y'?TRUE:FALSE);
    _controlRecentlySentFlag = (_additionalFlags[17]=='y'?TRUE:FALSE);
    _porterRetFailFlag = (_additionalFlags[18]=='y'?TRUE:FALSE);
    _unsolicitedPendingFlag = (_additionalFlags[19]=='y'?TRUE:FALSE);

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

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc cap bank.
---------------------------------------------------------------------------*/
void CtiCCCapBank::dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime)
{
    {

        RWDBTable dynamicCCCapBankTable = getDatabase().table( "dynamiccccapbank" );
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
            
            RWDBUpdater updater = dynamicCCCapBankTable.updater();

            updater.where(dynamicCCCapBankTable["capbankid"]==_paoid);

            updater << dynamicCCCapBankTable["controlstatus"].assign( _controlstatus )
            << dynamicCCCapBankTable["totaloperations"].assign( _totaloperations )
            << dynamicCCCapBankTable["laststatuschangetime"].assign( toRWDBDT((CtiTime)_laststatuschangetime) )
            << dynamicCCCapBankTable["tagscontrolstatus"].assign( _tagscontrolstatus )
            << dynamicCCCapBankTable["ctitimestamp"].assign(toRWDBDT((CtiTime)currentDateTime))
            << dynamicCCCapBankTable["originalfeederid"].assign( _originalfeederid )
            << dynamicCCCapBankTable["originalswitchingorder"].assign( _originalswitchingorder )
            << dynamicCCCapBankTable["assumedstartverificationstatus"].assign(_assumedOrigCapBankPos)
            << dynamicCCCapBankTable["prevverificationcontrolstatus"].assign(_prevVerificationControlStatus)
            << dynamicCCCapBankTable["verificationcontrolindex"].assign(_vCtrlIndex)
            << dynamicCCCapBankTable["additionalflags"].assign(string2RWCString(_additionalFlags))
            << dynamicCCCapBankTable["currentdailyoperations"].assign( _currentdailyoperations )
            << dynamicCCCapBankTable["twowaycbcstate"].assign(_reportedCBCState)
            << dynamicCCCapBankTable["twowaycbcstatetime"].assign( toRWDBDT((CtiTime)_reportedCBCStateTime) )
            << dynamicCCCapBankTable["beforevar"].assign( string2RWCString(_sBeforeVars) )
            << dynamicCCCapBankTable["aftervar"].assign( string2RWCString(_sAfterVars) )
            << dynamicCCCapBankTable["changevar"].assign( string2RWCString(_sPercentChange) )
            << dynamicCCCapBankTable["twowaycbclastcontrol"].assign( _reportedCBCLastControlReason);

            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted Cap Bank into DynamicCCCapBank: " << getPAOName() << endl;
            }

            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

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
            << string2RWCString(addFlags)
            << _currentdailyoperations
            << _reportedCBCState
            << _reportedCBCStateTime
            << _sBeforeVars
            << _sAfterVars
            << _sPercentChange
            << _reportedCBCLastControlReason;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << inserter.asString().data() << endl;
            }

            inserter.execute( conn );

            if(inserter.status().errorCode() == RWDBStatus::ok)    // No error occured!
            {
                _insertDynamicDataFlag = FALSE;
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << inserter.asString() << endl;
                }
            }
        }

        if (getOperationStats().isDirty())
            getOperationStats().dumpDynamicData(conn, currentDateTime);
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
