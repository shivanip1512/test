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
LONG CtiLMCurtailCustomer::getPAOId() const
{

    return _paoid;
}

/*---------------------------------------------------------------------------
    getPAOCategory

    Returns the pao category of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getPAOCategory() const
{

    return _paocategory;
}

/*---------------------------------------------------------------------------
    getPAOClass

    Returns the pao class of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getPAOClass() const
{

    return _paoclass;
}

/*---------------------------------------------------------------------------
    getPAOName

    Returns the pao name of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getPAOName() const
{

    return _paoname;
}

/*---------------------------------------------------------------------------
    getPAOType

    Returns the pao type of the substation
---------------------------------------------------------------------------*/
LONG CtiLMCurtailCustomer::getPAOType() const
{

    return _paotype;
}

/*---------------------------------------------------------------------------
    getPAODescription

    Returns the pao description of the substation
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getPAODescription() const
{

    return _paodescription;
}

/*---------------------------------------------------------------------------
    getDisableFlag

    Returns the disable flag of the customer
---------------------------------------------------------------------------*/
BOOL CtiLMCurtailCustomer::getDisableFlag() const
{

    return _disableflag;
}

/*---------------------------------------------------------------------------
    getCustomerOrder

    Returns the order of the customer in a program
---------------------------------------------------------------------------*/
LONG CtiLMCurtailCustomer::getCustomerOrder() const
{

    return _customerorder;
}

/*---------------------------------------------------------------------------
    getCustFPL

    Returns the customer pre-determined demand limit of the customer
---------------------------------------------------------------------------*/
DOUBLE CtiLMCurtailCustomer::getCustFPL() const
{

    return _custfpl;
}

/*---------------------------------------------------------------------------
    getCustTimeZone

    Returns the customer time zone of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getCustTimeZone() const
{

    return _custtimezone;
}

/*---------------------------------------------------------------------------
    getRequireAck

    Returns the require ack flag of the customer
---------------------------------------------------------------------------*/
BOOL CtiLMCurtailCustomer::getRequireAck() const
{

    return _requireack;
}

/*---------------------------------------------------------------------------
    getCurtailReferenceId

    Returns the curtail reference id of the customer in a program
---------------------------------------------------------------------------*/
LONG CtiLMCurtailCustomer::getCurtailReferenceId() const
{

    return _curtailreferenceid;
}

/*---------------------------------------------------------------------------
    getAcknowledgeStatus

    Returns the acknowledge status of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getAcknowledgeStatus() const
{

    return _acknowledgestatus;
}

/*---------------------------------------------------------------------------
    getAckDateTime

    Returns the acknowledge date time of the customer
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMCurtailCustomer::getAckDateTime() const
{

    return _ackdatetime;
}

/*---------------------------------------------------------------------------
    getIPAddressOfAckUser

    Returns the ip address of the ack user of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getIPAddressOfAckUser() const
{

    return _ipaddressofackuser;
}

/*---------------------------------------------------------------------------
    getUserIdName

    Returns the user id name of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getUserIdName() const
{

    return _useridname;
}

/*---------------------------------------------------------------------------
    getNameOfAckPerson

    Returns the name of ack person of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getNameOfAckPerson() const
{

    return _nameofackperson;
}

/*---------------------------------------------------------------------------
    getCurtailmentNotes

    Returns the curtailment notes of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMCurtailCustomer::getCurtailmentNotes() const
{

    return _curtailmentnotes;
}

/*---------------------------------------------------------------------------
    getAckLateFlag

    Returns the ack late flag of the customer
---------------------------------------------------------------------------*/
BOOL CtiLMCurtailCustomer::getAckLateFlag() const
{

    return _acklateflag;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the unique id of the substation - use with caution
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setPAOId(LONG id)
{

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

    _paocategory = category;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOClass

    Sets the pao class of the substation
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setPAOClass(const RWCString& pclass)
{

    _paoclass = pclass;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOName

    Sets the pao name of the substation
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setPAOName(const RWCString& name)
{

    _paoname = name;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAOType

    Sets the pao type of the substation
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setPAOType(LONG type)
{

    _paotype = type;
    return *this;
}

/*---------------------------------------------------------------------------
    setPAODescription

    Sets the pao description of the substation
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setPAODescription(const RWCString& description)
{

    _paodescription = description;
    return *this;
}

/*---------------------------------------------------------------------------
    setDisableFlag

    Sets the disable flag of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setDisableFlag(BOOL disable)
{

    _disableflag = disable;
    return *this;
}

/*---------------------------------------------------------------------------
    setCustomerOrder

    Sets the order of the customer in a program
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCustomerOrder(LONG order)
{

    _customerorder = order;
    return *this;
}

/*---------------------------------------------------------------------------
    setCustFPL

    Sets the pre-determined demand limit of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCustFPL(DOUBLE fpl)
{

    _custfpl = fpl;
    return *this;
}

/*---------------------------------------------------------------------------
    setCustTimeZone

    Sets the time zone of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCustTimeZone(const RWCString& timezone)
{

    _custtimezone = timezone;
    return *this;
}

/*---------------------------------------------------------------------------
    setRequireAck

    Sets the require ack flag of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setRequireAck(BOOL reqack)
{

    _requireack = reqack;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurtailReferenceId

    Sets the Curtail Reference Id of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCurtailReferenceId(LONG refid)
{

    _curtailreferenceid = refid;
    return *this;
}

/*---------------------------------------------------------------------------
    setAcknowledgeStatus

    Sets the acknowledge status of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setAcknowledgeStatus(const RWCString& ackstatus)
{

    _acknowledgestatus = ackstatus;
    return *this;
}

/*---------------------------------------------------------------------------
    setAckDateTime

    Sets the ack date time of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setAckDateTime(const RWDBDateTime& acktime)
{

    _ackdatetime = acktime;
    return *this;
}

/*---------------------------------------------------------------------------
    setIPAddressOfAckUser

    Sets the ip address of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setIPAddressOfAckUser(const RWCString& ipaddress)
{

    _ipaddressofackuser = ipaddress;
    return *this;
}

/*---------------------------------------------------------------------------
    setUserIdName

    Sets the user id name of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setUserIdName(const RWCString& username)
{

    _useridname = username;
    return *this;
}

/*---------------------------------------------------------------------------
    setNameOfAckPerson

    Sets the name of the ack person of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setNameOfAckPerson(const RWCString& nameackperson)
{

    _nameofackperson = nameackperson;
    return *this;
}

/*---------------------------------------------------------------------------
    setCurtailmentNotes

    Sets the curtailment notes of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCurtailmentNotes(const RWCString& curtailnotes)
{

    _curtailmentnotes = curtailnotes;
    return *this;
}

/*---------------------------------------------------------------------------
    setAckLateFlag

    Sets the ack late flag of the customer
---------------------------------------------------------------------------*/
CtiLMCurtailCustomer& CtiLMCurtailCustomer::setAckLateFlag(BOOL acklate)
{

    _acklateflag = acklate;
    return *this;
}


/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::restoreGuts(RWvistream& istrm)
{



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

    return( (getPAOId() == right.getPAOId()) && (getCustomerOrder() == right.getCustomerOrder()) );
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMCurtailCustomer::operator!=(const CtiLMCurtailCustomer& right) const
{

    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::restore(RWDBReader& rdr)
{


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
void CtiLMCurtailCustomer::updateLMCurtailCustomerActivityTable(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{
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

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,RWDBDateTime());
}
/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this customer.
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime)
{
    updateLMCurtailCustomerActivityTable(conn, currentDateTime);
}

/*---------------------------------------------------------------------------
    restoreDynamicData

    Restores self's dynamic data given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::restoreDynamicData(RWDBReader& rdr)
{


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

