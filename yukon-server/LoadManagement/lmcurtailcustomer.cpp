/*---------------------------------------------------------------------------
        Filename:  lmcurtailcustomer.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMCurtailCustomer.
                        CtiLMCurtailCustomer maintains the state and handles
                        the persistence of groups in Load Management.

        Initial Date:  3/26/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmcurtailcustomer.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "device.h"
#include "resolvers.h"

extern BOOL _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMCurtailCustomer, CTILMCURTAILCUSTOMER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer::CtiLMCurtailCustomer()
{
}

CtiLMCurtailCustomer::CtiLMCurtailCustomer(RWDBReader& rdr)
{
    restore(rdr);
}

CtiLMCurtailCustomer::CtiLMCurtailCustomer(const CtiLMCurtailCustomer& customer)
{
    operator=(customer);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer::~CtiLMCurtailCustomer()
{
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the unique id of the substation
---------------------------------------------------------------------------*/
ULONG CtiLMCurtailCustomer::getPAOId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getPAOCategory() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getPAOClass() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getPAOName() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the substation
---------------------------------------------------------------------------*/
ULONG CtiLMCurtailCustomer::getPAOType() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getPAODescription() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the customer
---------------------------------------------------------------------------*/
BOOL CtiLMCurtailCustomer::getDisableFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _disableflag;
}

/*---------------------------------------------------------------------------
    getCustomerOrder

    Returns the order of the customer in a program
---------------------------------------------------------------------------*/
ULONG CtiLMCurtailCustomer::getCustomerOrder() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _customerorder;
}

/*---------------------------------------------------------------------------
    getCustFPL

    Returns the customer pre-determined demand limit of the customer
---------------------------------------------------------------------------*/
DOUBLE CtiLMCurtailCustomer::getCustFPL() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _custfpl;
}

/*---------------------------------------------------------------------------
    getCustTimeZone

    Returns the customer time zone of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getCustTimeZone() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _custtimezone;
}

/*---------------------------------------------------------------------------
    getRequireAck

    Returns the require ack flag of the customer
---------------------------------------------------------------------------*/
BOOL CtiLMCurtailCustomer::getRequireAck() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _requireack;
}

/*---------------------------------------------------------------------------
    getCurtailReferenceId

    Returns the curtail reference id of the customer in a program
---------------------------------------------------------------------------*/
ULONG CtiLMCurtailCustomer::getCurtailReferenceId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _curtailreferenceid;
}

/*---------------------------------------------------------------------------
    getAcknowledgeStatus

    Returns the acknowledge status of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getAcknowledgeStatus() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _acknowledgestatus;
}

/*---------------------------------------------------------------------------
    getAckDateTime

    Returns the acknowledge date time of the customer
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMCurtailCustomer::getAckDateTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _ackdatetime;
}

/*---------------------------------------------------------------------------
    getIPAddressOfAckUser

    Returns the ip address of the ack user of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getIPAddressOfAckUser() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _ipaddressofackuser;
}

/*---------------------------------------------------------------------------
    getUserIdName

    Returns the user id name of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getUserIdName() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _useridname;
}

/*---------------------------------------------------------------------------
    getNameOfAckPerson

    Returns the name of ack person of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getNameOfAckPerson() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _nameofackperson;
}

/*---------------------------------------------------------------------------
    getCurtailmentNotes

    Returns the curtailment notes of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getCurtailmentNotes() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _curtailmentnotes;
}

/*---------------------------------------------------------------------------
    getAckLateFlag

    Returns the ack late flag of the customer
---------------------------------------------------------------------------*/
BOOL CtiLMCurtailCustomer::getAckLateFlag() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _acklateflag;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the substation - use with caution
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setPAOId(ULONG id)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoid = id;
    //do not notify observers of this!
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOCategory

    Sets the pao category of the substation
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setPAOCategory(const RWCString& category)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the substation
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setPAOClass(const RWCString& pclass)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the substation
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setPAOName(const RWCString& name)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the substation
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setPAOType(ULONG type)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the substation
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setPAODescription(const RWCString& description)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setDisableFlag(BOOL disable)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _disableflag = disable;
    return *this;
}

/*---------------------------------------------------------------------------
    setCustomerOrder

    Sets the order of the customer in a program
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCustomerOrder(ULONG order)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _customerorder = order;
    return *this;
}

/*---------------------------------------------------------------------------
    setCustFPL

    Sets the pre-determined demand limit of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCustFPL(DOUBLE fpl)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _custfpl = fpl;
    return *this;
}

/*---------------------------------------------------------------------------
    setCustTimeZone

    Sets the time zone of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCustTimeZone(const RWCString& timezone)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _custtimezone = timezone;
    return *this;
}

/*---------------------------------------------------------------------------
    setRequireAck

    Sets the require ack flag of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setRequireAck(BOOL reqack)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _requireack = reqack;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurtailReferenceId

    Sets the Curtail Reference Id of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCurtailReferenceId(ULONG refid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _curtailreferenceid = refid;
    return *this;
}

/*---------------------------------------------------------------------------
    setAcknowledgeStatus

    Sets the acknowledge status of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setAcknowledgeStatus(const RWCString& ackstatus)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _acknowledgestatus = ackstatus;
    return *this;
}

/*---------------------------------------------------------------------------
    setAckDateTime

    Sets the ack date time of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setAckDateTime(const RWDBDateTime& acktime)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _ackdatetime = acktime;
    return *this;
}

/*---------------------------------------------------------------------------
    setIPAddressOfAckUser

    Sets the ip address of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setIPAddressOfAckUser(const RWCString& ipaddress)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _ipaddressofackuser = ipaddress;
    return *this;
}

/*---------------------------------------------------------------------------
    setUserIdName

    Sets the user id name of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setUserIdName(const RWCString& username)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _useridname = username;
    return *this;
}

/*---------------------------------------------------------------------------
    setNameOfAckPerson

    Sets the name of the ack person of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setNameOfAckPerson(const RWCString& nameackperson)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _nameofackperson = nameackperson;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurtailmentNotes

    Sets the curtailment notes of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCurtailmentNotes(const RWCString& curtailnotes)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _curtailmentnotes = curtailnotes;
    return *this;
}

/*---------------------------------------------------------------------------
    setAckLateFlag

    Sets the ack late flag of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setAckLateFlag(BOOL acklate)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _acklateflag = acklate;
    return *this;
}


/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::restoreGuts(RWvistream& istrm)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWCollectable::restoreGuts( istrm );

    RWTime tempTime;
    istrm >> _paoid
    >> _paocategory
    >> _paoclass
    >> _paoname
    >> _paotype
    >> _paodescription
    >> _disableflag
    >> _customerorder
    >> _custfpl
    >> _custtimezone
    >> _requireack
    >> _curtailreferenceid
    >> _acknowledgestatus
    >> tempTime
    >> _ipaddressofackuser
    >> _useridname
    >> _nameofackperson
    >> _curtailmentnotes
    >> _acklateflag;

    _ackdatetime = RWDBDateTime(tempTime);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::saveGuts(RWvostream& ostrm ) const
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
    << _customerorder
    << _custfpl
    << _custtimezone
    << _requireack
    << _curtailreferenceid
    << _acknowledgestatus
    << _ackdatetime.rwtime()
    << _ipaddressofackuser
    << _useridname
    << _nameofackperson
    << _curtailmentnotes
    << _acklateflag;

    return;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer* CtiLMCurtailCustomer::replicate() const
{
    return(new CtiLMCurtailCustomer(*this));
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::operator=(const CtiLMCurtailCustomer& right)
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
        _customerorder = right._customerorder;
        _custfpl = right._custfpl;
        _custtimezone = right._custtimezone;
        _requireack = right._requireack;
        _curtailreferenceid = right._curtailreferenceid;
        _acknowledgestatus = right._acknowledgestatus;
        _ackdatetime = right._ackdatetime;
        _ipaddressofackuser = right._ipaddressofackuser;
        _useridname = right._useridname;
        _nameofackperson = right._nameofackperson;
        _curtailmentnotes = right._curtailmentnotes;
        _acklateflag = right._acklateflag;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMCurtailCustomer::operator==(const CtiLMCurtailCustomer& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return( (getPAOId() == right.getPAOId()) && (getCustomerOrder() == right.getCustomerOrder()) );
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMCurtailCustomer::operator!=(const CtiLMCurtailCustomer& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::restore(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWCString tempBoolString;
    RWCString tempTypeString;

    rdr["paobjectid"] >> _paoid;
    rdr["category"] >> _paocategory;
    rdr["paoclass"] >> _paoclass;
    rdr["paoname"] >> _paoname;
    rdr["type"] >> tempTypeString;
    _paotype = resolvePAOType(_paocategory,tempTypeString);
    rdr["description"] >> _paodescription;
    rdr["disableflag"] >> tempBoolString;
    tempBoolString.toLower();
    setDisableFlag(tempBoolString=="y"?TRUE:FALSE);
    rdr["customerorder"] >> _customerorder;
    rdr["custfpl"] >> _custfpl;
    rdr["custtimezone"] >> _custtimezone;
    rdr["requireack"] >> tempBoolString;
    tempBoolString.toLower();
    setRequireAck(tempBoolString=="y"?TRUE:FALSE);

    setCurtailReferenceId(0);
    setAcknowledgeStatus(CtiLMCurtailCustomer::NotRequiredAckStatus);
    setAckDateTime(RWDBDateTime(1990,1,1,0,0,0,0));
    setIPAddressOfAckUser("1.1.1.1");
    setUserIdName("Null");
    setNameOfAckPerson("Null");
    setCurtailmentNotes("Null");
    setAckLateFlag(FALSE);
}

/*---------------------------------------------------------------------------
    addLMCurtailCustomerActivity

    .
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::addLMCurtailCustomerActivityTable()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            if( _LM_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Inserted customer activity area into LMCurtailCustomerActivity: " << getPAOName() << endl;
            }

            RWDBDatabase db = getDatabase();
            RWDBTable lmCurtailCustomerActivityTable = db.table("lmcurtailcustomeractivity");

            RWDBInserter inserter = lmCurtailCustomerActivityTable.inserter();

            inserter << getPAOId()
            << getCurtailReferenceId()
            << getAcknowledgeStatus()
            << getAckDateTime()
            << getIPAddressOfAckUser()
            << getUserIdName()
            << getNameOfAckPerson()
            << getCurtailmentNotes()
            << getCustFPL()
            << RWCString( ( getAckLateFlag() ? 'Y': 'N' ) );

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << inserter.asString().data() << endl;
            }*/

            inserter.execute( conn );
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    updateLMCurtailCustomerActivity

    .
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::updateLMCurtailCustomerActivityTable()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmCurtailCustomerActivityTable = db.table("lmcurtailcustomeractivity");
            RWDBUpdater updater = lmCurtailCustomerActivityTable.updater();

            updater << lmCurtailCustomerActivityTable["acknowledgestatus"].assign(getAcknowledgeStatus())
            << lmCurtailCustomerActivityTable["ackdatetime"].assign(getAckDateTime())
            << lmCurtailCustomerActivityTable["ipaddressofackuser"].assign(getIPAddressOfAckUser())
            << lmCurtailCustomerActivityTable["useridname"].assign(getUserIdName())
            << lmCurtailCustomerActivityTable["nameofackperson"].assign(getNameOfAckPerson())
            << lmCurtailCustomerActivityTable["curtailmentnotes"].assign(getCurtailmentNotes())
            << lmCurtailCustomerActivityTable["currentpdl"].assign(getCustFPL())
            << lmCurtailCustomerActivityTable["acklateflag"].assign(RWCString( (getAckLateFlag() ? 'Y':'N') ));

            updater.where(lmCurtailCustomerActivityTable["customerid"]==getPAOId() &&
                          lmCurtailCustomerActivityTable["curtailreferenceid"]==getCurtailReferenceId());

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
            }*/

            updater.execute( conn );
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this customer.
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::dumpDynamicData()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    updateLMCurtailCustomerActivityTable();
}

/*---------------------------------------------------------------------------
    restoreDynamicData

    Restores self's dynamic data given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::restoreDynamicData(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmCurtailCustomerActivityTable = db.table("lmcurtailcustomeractivity");
            RWDBSelector selector = db.selector();
            selector << lmCurtailCustomerActivityTable["curtailreferenceid"]
            << lmCurtailCustomerActivityTable["acknowledgestatus"]
            << lmCurtailCustomerActivityTable["ackdatetime"]
            << lmCurtailCustomerActivityTable["ipaddressofackuser"]
            << lmCurtailCustomerActivityTable["useridname"]
            << lmCurtailCustomerActivityTable["nameofackperson"]
            << lmCurtailCustomerActivityTable["curtailmentnotes"]
            << lmCurtailCustomerActivityTable["acklateflag"];

            selector.from(lmCurtailCustomerActivityTable);

            selector.where(lmCurtailCustomerActivityTable["customerid"]==getPAOId());

            selector.orderByDescending(lmCurtailCustomerActivityTable["curtailreferenceid"]);

            /*if( _LM_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << selector.asString().data() << endl;
            }*/

            RWDBReader rdr = selector.reader(conn);

            if(rdr())
            {
                RWCString tempBoolString;
                rdr["curtailreferenceid"] >> _curtailreferenceid;
                rdr["acknowledgestatus"] >> _acknowledgestatus;
                rdr["ackdatetime"] >> _ackdatetime;
                rdr["ipaddressofackuser"] >> _ipaddressofackuser;
                rdr["useridname"] >> _useridname;
                rdr["nameofackperson"] >> _nameofackperson;
                rdr["curtailmentnotes"] >> _curtailmentnotes;
                rdr["acklateflag"] >> tempBoolString;
                tempBoolString.toLower();
                setAckLateFlag(tempBoolString=="y"?TRUE:FALSE);
            }

        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

// Static Members

// Possible acknowledge statuses
const RWCString CtiLMCurtailCustomer::UnAcknowledgedAckStatus = "UnAcknowledged";
const RWCString CtiLMCurtailCustomer::AcknowledgedAckStatus = "Acknowledged";
const RWCString CtiLMCurtailCustomer::NotRequiredAckStatus = "NotRequired";
const RWCString CtiLMCurtailCustomer::VerbalAckStatus = "Verbal";

