/*---------------------------------------------------------------------------
        Filename:  cccapbank.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCCapBank.
                        CtiCCCapBank maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#include "dbaccess.h"
#include "cccapbank.h"
#include "ccid.h"
#include "pointdefs.h"
#include "device.h"
#include "logger.h"
#include "resolvers.h"

RWDEFINE_COLLECTABLE( CtiCCCapBank, CTICCCAPBANK_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCCapBank::CtiCCCapBank()
{
}

CtiCCCapBank::CtiCCCapBank(RWDBReader& rdr)
{
    restore(rdr);
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
ULONG CtiCCCapBank::getPAOId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getPAOCategory() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getPAOClass() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getPAOName() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getPAOType() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getPAODescription() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCCapBank::getDisableFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getAlarmInhibitFlag

    Returns the alarm inhibit of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCCapBank::getAlarmInhibitFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _alarminhibitflag;
}

/*---------------------------------------------------------------------------
    getControlInhibitFlag

    Returns the control inhibit of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCCapBank::getControlInhibitFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlinhibitflag;
}

/*---------------------------------------------------------------------------
    getOperationalState

    Returns the operational state of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getOperationalState() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _operationalstate;
}

/*---------------------------------------------------------------------------
    getControllerType

    Returns the controller type of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getControllerType() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controllertype;
}

/*---------------------------------------------------------------------------
    getControlDeviceId

    Returns the control device id of the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCCCapBank::getControlDeviceId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controldeviceid;
}

/*---------------------------------------------------------------------------
    getControlPointId

    Returns the control point id of the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCCCapBank::getControlPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlpointid;
}

/*---------------------------------------------------------------------------
    getBankSize

    Returns the bank size of the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCCCapBank::getBankSize() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _banksize;
}

/*---------------------------------------------------------------------------
    getTypeOfSwitch

    Returns the type of switch of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getTypeOfSwitch() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _typeofswitch;
}

/*---------------------------------------------------------------------------
    getSwitchManufacture

    Returns the switch manufacture of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCCCapBank::getSwitchManufacture() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _switchmanufacture;
}

/*---------------------------------------------------------------------------
    getMapLocationId

    Returns the map location id of the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCCCapBank::getMapLocationId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _maplocationid;
}

/*---------------------------------------------------------------------------
    getControlOrder

    Returns the control order of the cap bank in the list of a feeder
---------------------------------------------------------------------------*/
ULONG CtiCCCapBank::getControlOrder() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlorder;
}

/*---------------------------------------------------------------------------
    getStatusPointId

    Returns the status point id of the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCCCapBank::getStatusPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _statuspointid;
}

/*---------------------------------------------------------------------------
    getControlStatus

    Returns the control status of the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCCCapBank::getControlStatus() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlstatus;
}

/*---------------------------------------------------------------------------
    getOperationAnalogPointId

    Returns the point id of the analog that holds the number of operations
    on the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCCCapBank::getOperationAnalogPointId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _operationanalogpointid;
}

/*---------------------------------------------------------------------------
    getCurrentDailyOperations

    Returns the number operations performed on the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCCCapBank::getCurrentDailyOperations() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentdailyoperations;
}

/*---------------------------------------------------------------------------
    getLastStatusChangeTime

    Returns the timestamp of the last status point data msg sent by dispatch
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCCCapBank::getLastStatusChangeTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _laststatuschangetime;
}

/*---------------------------------------------------------------------------
    getTagsControlStatus

    Returns the tags of control status on the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCCCapBank::getTagsControlStatus() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _tagscontrolstatus;
}

/*---------------------------------------------------------------------------
    getStatusReceivedFlag

    Returns the boolean if the status on the cap bank has been received
---------------------------------------------------------------------------*/
BOOL CtiCCCapBank::getStatusReceivedFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _statusreceivedflag;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the id of the capbank - use with caution
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAOId(ULONG id)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paocategory = category;

    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAOClass(const RWCString& pclass)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoclass = pclass;

    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAOName(const RWCString& name)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoname = name;

    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAOType(const RWCString& type)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paotype = type;

    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setPAODescription(const RWCString& description)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paodescription = description;

    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setDisableFlag(BOOL disable)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _disableflag = disable;

    return *this;
}

/*---------------------------------------------------------------------------
    setAlarmInhibitFlag

    Sets the alarm inhibit of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setAlarmInhibitFlag(BOOL alarminhibit)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _alarminhibitflag = alarminhibit;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlInhibitFlag

    Sets the control inhibit of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlInhibitFlag(BOOL controlinhibit)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlinhibitflag = controlinhibit;

    return *this;
}

/*---------------------------------------------------------------------------
    setOperationalState

    Sets the operational state of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOperationalState(const RWCString& operational)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _operationalstate = operational;

    return *this;
}

/*---------------------------------------------------------------------------
    setControllerType

    Sets the controller type of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControllerType(const RWCString& controllertype)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controllertype = controllertype;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlDeviceId

    Sets the control device id of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlDeviceId(ULONG controldevice)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controldeviceid = controldevice;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlPointId

    Sets the control point id of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlPointId(ULONG controlpoint)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlpointid = controlpoint;

    return *this;
}

/*---------------------------------------------------------------------------
    setBankSize

    Sets the bank size of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setBankSize(ULONG size)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _banksize = size;

    return *this;
}

/*---------------------------------------------------------------------------
    setTypeOfSwitch

    Sets the type of switch of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setTypeOfSwitch(const RWCString& switchtype)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _typeofswitch = switchtype;

    return *this;
}

/*---------------------------------------------------------------------------
    setSwitchManufacture

    Sets the switch manufacture of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setSwitchManufacture(const RWCString& manufacture)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _switchmanufacture = manufacture;

    return *this;
}

/*---------------------------------------------------------------------------
    setMapLocationId

    Sets the map location id of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setMapLocationId(ULONG maplocation)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _maplocationid = maplocation;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlOrder

    Sets the control order of the capbank in the list of the parent feeder
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlOrder(ULONG order)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlorder = order;

    return *this;
}

/*---------------------------------------------------------------------------
    setStatusPointId

    Sets the status point id of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setStatusPointId(ULONG statuspoint)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _statuspointid = statuspoint;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlStatus

    Sets the control status of the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setControlStatus(ULONG status)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlstatus = status;

    return *this;
}

/*---------------------------------------------------------------------------
    setOperationAnalogPointId

    Sets the point id for the analog point that hold the number of number of
    operations performed on the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setOperationAnalogPointId(ULONG operationpointid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _operationanalogpointid = operationpointid;

    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentDailyOperations

    Sets the number of operations performed on this capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setCurrentDailyOperations(ULONG operations)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentdailyoperations = operations;

    return *this;
}

/*---------------------------------------------------------------------------
    setLastStatusChangeTime

    Sets the timestamp of the last status point data msg sent by dispatch
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setLastStatusChangeTime(const RWDBDateTime& laststatuschangetime)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _laststatuschangetime = laststatuschangetime;

    return *this;
}

/*---------------------------------------------------------------------------
    setTagsControlStatus

    Sets the tags of control status on the capbank
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setTagsControlStatus(ULONG tags)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _tagscontrolstatus = tags;

    return *this;
}

/*---------------------------------------------------------------------------
    setStatusReceivedFlag

    Sets the boolean if the status on the cap bank has been received
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::setStatusReceivedFlag(BOOL statusreceived)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _statusreceivedflag = statusreceived;

    return *this;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCCapBank::restoreGuts(RWvistream& istrm)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWTime tempTime1;

    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag
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
    >> _controlorder
    >> _statuspointid
    >> _controlstatus
    >> _operationanalogpointid
    >> _currentdailyoperations
    >> tempTime1
    >> _tagscontrolstatus
    >> _statusreceivedflag;

    _laststatuschangetime = RWDBDateTime(tempTime1);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCCapBank::saveGuts(RWvostream& ostrm ) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
    << _paocategory
    << _paoclass
    << _paoname
    << _paotype
    << _paodescription
    << _disableflag
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
    << _controlorder
    << _statuspointid
    << _controlstatus
    << _operationanalogpointid
    << _currentdailyoperations
    << _laststatuschangetime.rwtime()
    << _tagscontrolstatus
    << _statusreceivedflag;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCCapBank& CtiCCCapBank::operator=(const CtiCCCapBank& right)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( this != &right )
    {
        _paoid = right._paoid;
        _paocategory = right._paocategory;
        _paoclass = right._paoclass;
        _paoname = right._paoname;
        _paotype = right._paotype;
        _paodescription = right._paodescription;
        _disableflag = right._disableflag;
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
        _controlorder = right._controlorder;
        _statuspointid = right._statuspointid;
        _controlstatus = right._controlstatus;
        _operationanalogpointid = right._operationanalogpointid;
        _currentdailyoperations = right._currentdailyoperations;
        _laststatuschangetime = right._laststatuschangetime;
        _tagscontrolstatus = right._tagscontrolstatus;
        _statusreceivedflag = right._statusreceivedflag;
    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCCapBank::operator==(const CtiCCCapBank& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return getPAOId() == right.getPAOId();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCCapBank::operator!=(const CtiCCCapBank& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return getPAOId() != right.getPAOId();
}


/*---------------------------------------------------------------------------
    restore

    Restores self's state given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCCapBank::restore(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    rdr["controlorder"] >> _controlorder;

    setStatusPointId(0);
    setOperationAnalogPointId(0);
    setStatusReceivedFlag(FALSE);

    rdr["controlstatus"] >> isNull;
    if( !isNull )
    {
        rdr["controlstatus"] >> _controlstatus;
        rdr["currentdailyoperations"] >> _currentdailyoperations;
        rdr["laststatuschangetime"] >> _laststatuschangetime;
        rdr["tagscontrolstatus"] >> _tagscontrolstatus;
        rdr["ctitimestamp"] >> dynamicTimeStamp;

        _insertDynamicDataFlag = FALSE;
    }
    else
    {
        //initialize dynamic data members
        setCurrentDailyOperations(0);
        setLastStatusChangeTime(RWDBDateTime(1990,1,1,0,0,0,0));
        setControlStatus(CtiCCCapBank::Open);
        setTagsControlStatus(0);

        _insertDynamicDataFlag = TRUE;
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
    dumpDynamicData

    Writes out the dynamic information for this cc cap bank.
---------------------------------------------------------------------------*/
void CtiCCCapBank::dumpDynamicData()
{
    RWDBDateTime currentDateTime = RWDBDateTime();

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        RWDBTable dynamicCCCapBankTable = getDatabase().table( "dynamiccccapbank" );
        if( !_insertDynamicDataFlag )
        {
            RWDBUpdater updater = dynamicCCCapBankTable.updater();

            updater << dynamicCCCapBankTable["controlstatus"].assign( getControlStatus() )
            << dynamicCCCapBankTable["currentdailyoperations"].assign( getCurrentDailyOperations() )
            << dynamicCCCapBankTable["laststatuschangetime"].assign( (RWDBDateTime)getLastStatusChangeTime() )
            << dynamicCCCapBankTable["tagscontrolstatus"].assign( getTagsControlStatus() )
            << dynamicCCCapBankTable["ctitimestamp"].assign((RWDBDateTime)currentDateTime);

            updater.where(dynamicCCCapBankTable["capbankid"]==getPAOId());

            updater.execute( conn );
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Inserted Cap Bank into DynamicCCCapBank: " << getPAOName() << endl;
            }

            RWDBInserter inserter = dynamicCCCapBankTable.inserter();

            inserter << getPAOId()
            << getControlStatus()
            << getCurrentDailyOperations()
            << getLastStatusChangeTime()
            << getTagsControlStatus()
            << currentDateTime;

            /*if( _CC_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << inserter.asString().data() << endl;
            }*/

            inserter.execute( conn );

            _insertDynamicDataFlag = FALSE;
        }
    }
}

/* Public Static members */
const RWCString CtiCCCapBank::SwitchedOperationalState = "Switched";
const RWCString CtiCCCapBank::FixedOperationalState = "Fixed";

int CtiCCCapBank::Open = STATEZERO;
int CtiCCCapBank::Close = STATEONE;
int CtiCCCapBank::OpenQuestionable = STATETWO;
int CtiCCCapBank::CloseQuestionable = STATETHREE;
int CtiCCCapBank::OpenFail = STATEFOUR;
int CtiCCCapBank::CloseFail = STATEFIVE;
int CtiCCCapBank::OpenPending = STATESIX;
int CtiCCCapBank::ClosePending = STATESEVEN;
