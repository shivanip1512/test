/*---------------------------------------------------------------------------
        Filename:  capbank.cpp
        
        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiCapBank.
                        CtiCapBank maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/18/2000
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2000
---------------------------------------------------------------------------*/

#include "dbaccess.h"
#include "capbank.h"
#include "ccid.h"
#include "device.h"

RWDEFINE_COLLECTABLE( CtiCapBank, CTICAPBANK_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCapBank::CtiCapBank()
{   
}

CtiCapBank::CtiCapBank(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiCapBank::CtiCapBank(const CtiCapBank& cap)
{
    operator=( cap );
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCapBank::~CtiCapBank()
{
}

/*---------------------------------------------------------------------------
    Id
    
    Returns the unique id of the cap bank device
---------------------------------------------------------------------------*/
ULONG CtiCapBank::Id() const  
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _id;
}

/*---------------------------------------------------------------------------
    Name
    
    Returns the name of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCapBank::Name() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _name;
}

/*---------------------------------------------------------------------------
    Type

    Returns the type of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCapBank::Type() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _type;
}

/*---------------------------------------------------------------------------
    CurrentState
    
    Returns the current state of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCapBank::CurrentState() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _currentstate;
}

/*---------------------------------------------------------------------------
    DisableFlag

    Returns the disable flag of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCapBank::DisableFlag() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _disableflag;
}

/*---------------------------------------------------------------------------
    AlarmInhibit

    Returns the alarm inhibit of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCapBank::AlarmInhibit() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _alarminhibit;
}

/*---------------------------------------------------------------------------
    ControlInhibit

    Returns the control inhibit of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCapBank::ControlInhibit() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlinhibit;
}

/*---------------------------------------------------------------------------
    DeviceClass

    Returns the device class of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCapBank::DeviceClass() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _deviceclass;
}

/*---------------------------------------------------------------------------
    BankAddress

    Returns the bank address of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCapBank::BankAddress() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _bankaddress;
}

/*---------------------------------------------------------------------------
    OperationalState

    Returns the operational state of the cap bank
---------------------------------------------------------------------------*/
const RWCString& CtiCapBank::OperationalState() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _operationalstate;
}

/*---------------------------------------------------------------------------
    ControlPointId

    Returns the control point id of the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCapBank::ControlPointId() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlpointid;
}

/*---------------------------------------------------------------------------
    BankSize

    Returns the bank size of the cap bank
---------------------------------------------------------------------------*/
DOUBLE CtiCapBank::BankSize() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _banksize;
}

/*---------------------------------------------------------------------------
    ControlDeviceId

    Returns the control device id of the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCapBank::ControlDeviceId() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controldeviceid;
}

/*---------------------------------------------------------------------------
    StatusPointId

    Returns the status point id of the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCapBank::StatusPointId() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _statuspointid;
}

/*---------------------------------------------------------------------------
    ControlStatus

    Returns the control status of the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCapBank::ControlStatus() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _controlstatus;
}

/*---------------------------------------------------------------------------
    OperationAnalogPointId

    Returns the point id of the analog that holds the number of operations
    on the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCapBank::OperationAnalogPointId() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _operationanalogpointid;
}

/*---------------------------------------------------------------------------
    Operations

    Returns the number operations performed on the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCapBank::Operations() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _operations;
}

/*---------------------------------------------------------------------------
    LastStatusChangeTime

    Returns the timestamp of the last status point data msg sent by dispatch
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiCapBank::LastStatusChangeTime() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _laststatuschangetime;
}

/*---------------------------------------------------------------------------
    TagsControlStatus

    Returns the tags of control status on the cap bank
---------------------------------------------------------------------------*/
ULONG CtiCapBank::TagsControlStatus() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _tagscontrolstatus;
}

/*---------------------------------------------------------------------------
    StatusReceivedFlag

    Returns the boolean if the status on the cap bank has been received
---------------------------------------------------------------------------*/
BOOL CtiCapBank::StatusReceivedFlag() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _statusreceivedflag;
}

/*---------------------------------------------------------------------------
    setId
    
    Sets the id of the capbank - use with caution
---------------------------------------------------------------------------*/
CtiCapBank& CtiCapBank::setId(ULONG id)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _id = id;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setName
    
    Sets the name of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setName(const RWCString& name)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _name = name;

    return *this;
}

/*---------------------------------------------------------------------------
    setType
    
    Sets the type of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setType(const RWCString& type)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _type = type;

    return *this;
}

/*---------------------------------------------------------------------------
    setCurrentState
    
    Sets the current of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setCurrentState(const RWCString& current)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _currentstate = current;

    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag
    
    Sets the disable flag of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setDisableFlag(BOOL disable)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _disableflag = disable;

    return *this;
}

/*---------------------------------------------------------------------------
    setAlarmInhibit
    
    Sets the alarm inhibit of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setAlarmInhibit(BOOL alarm)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _alarminhibit = alarm;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlInhibit
    
    Sets the control inhibit of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setControlInhibit(BOOL control)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlinhibit = control;

    return *this;
}

/*---------------------------------------------------------------------------
    setDeviceClass
    
    Sets the device class of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setDeviceClass(const RWCString& deviceclass)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _deviceclass = deviceclass;

    return *this;
}

/*---------------------------------------------------------------------------
    setBankAddress
    
    Sets the bank address of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setBankAddress(const RWCString& address)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _bankaddress = address;

    return *this;
}

/*---------------------------------------------------------------------------
    setOperationalState
    
    Sets the operational state of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setOperationalState(const RWCString& operational)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _operationalstate = operational;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlPointId
    
    Sets the control point id of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setControlPointId(ULONG controlpoint)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controlpointid = controlpoint;

    return *this;
}

/*---------------------------------------------------------------------------
    setBankSize
    
    Sets the bank size of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setBankSize(DOUBLE size)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _banksize = size;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlDeviceId
    
    Sets the control device id of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setControlDeviceId(ULONG controldevice)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _controldeviceid = controldevice;

    return *this;
}

/*---------------------------------------------------------------------------
    setStatusPointId
    
    Sets the status point id of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setStatusPointId(ULONG statuspoint)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _statuspointid = statuspoint;

    return *this;
}

/*---------------------------------------------------------------------------
    setControlStatus
    
    Sets the control status of the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setControlStatus(ULONG status)
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
CtiCapBank& CtiCapBank::setOperationAnalogPointId(ULONG operationpointid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _operationanalogpointid = operationpointid;

    return *this;
}

/*---------------------------------------------------------------------------
    setOperations
    
    Sets the number of operations performed on this capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setOperations(ULONG operations)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _operations = operations;

    return *this;
}

/*---------------------------------------------------------------------------
    setLastStatusChangeTime
    
    Sets the timestamp of the last status point data msg sent by dispatch
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setLastStatusChangeTime(const RWDBDateTime& laststatuschangetime)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _laststatuschangetime = laststatuschangetime;

    return *this;
}

/*---------------------------------------------------------------------------
    setTagsControlStatus
    
    Sets the tags of control status on the capbank
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setTagsControlStatus(ULONG tags)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _tagscontrolstatus = tags;

    return *this;
}

/*---------------------------------------------------------------------------
    setStatusReceivedFlag
    
    Sets the boolean if the status on the cap bank has been received
---------------------------------------------------------------------------*/    
CtiCapBank& CtiCapBank::setStatusReceivedFlag(BOOL statusreceived)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _statusreceivedflag = statusreceived;

    return *this;
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCapBank::restoreGuts(RWvistream& istrm)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWCollectable::restoreGuts( istrm );

    RWCollectable* c1;
    RWCollectable* c2;

    RWCString cmdFileName;

    istrm >> _id
          >> _name
          >> _type
          >> _currentstate
          >> _disableflag
          >> _alarminhibit
          >> _controlinhibit
          >> _deviceclass
          >> _bankaddress
          >> _operationalstate
          >> _controlpointid
          >> _banksize
          >> _controldeviceid
          >> _statuspointid
          >> _controlstatus
          >> _operationanalogpointid
          >> _operations
          >> _laststatuschangetime.rwtime()
          >> _tagscontrolstatus;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCapBank::saveGuts(RWvostream& ostrm ) const  
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        
    RWCollectable::saveGuts( ostrm );

    ostrm << _id
          << _name
          << _type
          << _currentstate
          << _disableflag
          << _alarminhibit
          << _controlinhibit
          << _deviceclass
          << _bankaddress
          << _operationalstate
          << _controlpointid
          << _banksize
          << _controldeviceid
          << _statuspointid
          << _controlstatus
          << _operationanalogpointid
          << _operations
          << _laststatuschangetime.rwtime()
          << _tagscontrolstatus;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCapBank& CtiCapBank::operator=(const CtiCapBank& right)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        
    if( this != &right )
    {
        _id = right._id;
        _name = right._name;
        _type = right._type;
        _currentstate = right._currentstate;
        _disableflag = right._disableflag;
        _alarminhibit = right._alarminhibit;
        _controlinhibit = right._controlinhibit;
        _deviceclass = right._deviceclass;
        _bankaddress = right._bankaddress;
        _operationalstate = right._operationalstate;
        _controlpointid = right._controlpointid;
        _banksize = right._banksize;
        _controldeviceid = right._controldeviceid;
        _statuspointid = right._statuspointid;
        _controlstatus = right._controlstatus;
        _operationanalogpointid = right._operationanalogpointid;
        _operations = right._operations;
        _laststatuschangetime = right._laststatuschangetime;
        _tagscontrolstatus = right._tagscontrolstatus;
        _statusreceivedflag = right._statusreceivedflag;
    }

    return *this;

}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCapBank::operator==(const CtiCapBank& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return Id() == right.Id();
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCapBank::operator!=(const CtiCapBank& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return Id() != right.Id();
}


/*---------------------------------------------------------------------------
    restore
    
    Restores self's state given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCapBank::restore(RWDBReader& rdr)
{
    RWCString tempStr;
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    //cout << "starting impl restore..." << endl;

    RWDBSchema schema = rdr.table().schema();

    for ( UINT i = 0; i < schema.entries(); i++ )
    {
        RWCString col = schema[i].qualifiedName();
        col.toLower();

        RWDBNullIndicator isNull;

        //cout << "col is:  " << col << endl;
        if ( col == "deviceid" )
        {
            ULONG id;
            rdr[col] >> id;

            setId(id);
        }
        else if ( col == "name" )
        {
            RWCString name;
            rdr[col] >> name;

            setName(name);        
        }
        else if ( col == "type" )
        {
            RWCString type;
            rdr[col] >> type;

            setType(type);
        }
        else if ( col == "currentstate" )
        {
            RWCString current;
            rdr[col] >> current;

            setCurrentState(current);
        }
        else if ( col == "disableflag" )
        {
            rdr[col] >> tempStr;
            tempStr.toLower();

            setDisableFlag(tempStr=="y"?TRUE:FALSE);
        }
        else if ( col == "alarminhibit" )
        {
            rdr[col] >> tempStr;
            tempStr.toLower();

            setAlarmInhibit(tempStr=="y"?TRUE:FALSE);
        }
        else if ( col == "controlinhibit" )
        {
            rdr[col] >> tempStr;
            tempStr.toLower();

            setControlInhibit(tempStr=="y"?TRUE:FALSE);
        }
        else if ( col == "class" )
        {
            RWCString deviceclass;
            rdr[col] >> deviceclass;

            setDeviceClass(deviceclass);
        }
        else if ( col == "bankaddress" )
        {
            RWCString address;
            rdr[col] >> address;

            setBankAddress(address);
        }
        else if ( col == "operationalstate" )
        {
            RWCString operational;
            rdr[col] >> operational;

            setOperationalState(operational);
        }
        else if ( col == "controlpointid" )
        {
            ULONG controlpoint;
            rdr[col] >> controlpoint;

            setControlPointId(controlpoint);
        }
        else if ( col == "banksize" )
        {
            DOUBLE size;
            rdr[col] >> size;

            setBankSize(size);
        }
        else if ( col == "controldeviceid" )
        {
            ULONG controldevice;
            rdr[col] >> controldevice;

            setControlDeviceId(controldevice);
        }
        else if ( col == "pointid" )
        {
            ULONG statuspoint;
            rdr[col] >> statuspoint;

            setStatusPointId(statuspoint);
        }
    }
    //initialize other data members
    setOperationAnalogPointId(0);
    setOperations(0);
    setLastStatusChangeTime(RWDBDateTime(1990,1,1,0,0,0,0));
    setControlStatus(CtiCapBank::Open);
    setTagsControlStatus(0);
    setStatusReceivedFlag(FALSE);
}

/*---------------------------------------------------------------------------
    restoreOperationFields
    
    Restores self's operation fields given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCapBank::restoreOperationFields(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBSchema schema = rdr.table().schema();

    for ( UINT i = 0; i < schema.entries(); i++ )
    {
        RWCString col = schema[i].qualifiedName();
        col.toLower();

        RWDBNullIndicator isNull;

        if ( col == "pointid" )
        {
            ULONG id;
            rdr[col] >> id;

            setOperationAnalogPointId(id);
        }
    }
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCapBank* CtiCapBank::replicate() const
{
    return (new CtiCapBank(*this));
}

/* Public Static members */
const RWCString CtiCapBank::Switched = "Switched";
const RWCString CtiCapBank::Fixed = "Fixed";

int CtiCapBank::Open = STATEZERO;
int CtiCapBank::Close = STATEONE;
int CtiCapBank::OpenQuestionable = STATETWO;
int CtiCapBank::CloseQuestionable = STATETHREE;
int CtiCapBank::OpenFail = STATEFOUR;
int CtiCapBank::CloseFail = STATEFIVE;
int CtiCapBank::OpenPending = STATESIX;
int CtiCapBank::ClosePending = STATESEVEN;
