/*---------------------------------------------------------------------------
        Filename:  lmcurtailcustomer.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMCurtailCustomer.
                        CtiLMCurtailCustomer maintains the state and handles
                        the persistence of groups in Load Management.

        Initial Date:  3/26/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "lmcurtailcustomer.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "device.h"
#include "resolvers.h"

extern ULONG _LM_DEBUG;

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

BOOL CtiLMCurtailCustomer::getRequireAck() const
{
    return _requireack;
}

LONG CtiLMCurtailCustomer::getCurtailReferenceId() const
{
    return _curtailreferenceid;
}

const RWCString& CtiLMCurtailCustomer::getAcknowledgeStatus() const
{
    return _acknowledgestatus;
}

const RWDBDateTime& CtiLMCurtailCustomer::getAckDateTime() const
{
    return _ackdatetime;
}

const RWCString& CtiLMCurtailCustomer::getIPAddressOfAckUser() const
{
    return _ipaddressofackuser;
}

const RWCString& CtiLMCurtailCustomer::getUserIdName() const
{
    return _useridname;
}

const RWCString& CtiLMCurtailCustomer::getNameOfAckPerson() const
{
    return _nameofackperson;
}

const RWCString& CtiLMCurtailCustomer::getCurtailmentNotes() const
{
    return _curtailmentnotes;
}

BOOL CtiLMCurtailCustomer::getAckLateFlag() const
{
    return _acklateflag;
}



CtiLMCurtailCustomer& CtiLMCurtailCustomer::setRequireAck(BOOL reqack)
{
    _requireack = reqack;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCurtailReferenceId(LONG refid)
{
    _curtailreferenceid = refid;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setAcknowledgeStatus(const RWCString& ackstatus)
{
    _acknowledgestatus = ackstatus;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setAckDateTime(const RWDBDateTime& acktime)
{
    _ackdatetime = acktime;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setIPAddressOfAckUser(const RWCString& ipaddress)
{
    _ipaddressofackuser = ipaddress;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setUserIdName(const RWCString& username)
{
    _useridname = username;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setNameOfAckPerson(const RWCString& nameackperson)
{
    _nameofackperson = nameackperson;
    return *this;
}

CtiLMCurtailCustomer& CtiLMCurtailCustomer::setCurtailmentNotes(const RWCString& curtailnotes)
{
    _curtailmentnotes = curtailnotes;
    return *this;
}

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
    CtiLMCICustomerBase::restoreGuts( istrm );

    RWTime tempTime;
    istrm >> _requireack
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
    CtiLMCICustomerBase::saveGuts( ostrm );

    ostrm << _requireack
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
        CtiLMCICustomerBase::operator=(right);
        _requireack             = right._requireack;
        _curtailreferenceid     = right._curtailreferenceid;
        _acknowledgestatus      = right._acknowledgestatus;
        _ackdatetime            = right._ackdatetime;
        _ipaddressofackuser     = right._ipaddressofackuser;
        _useridname             = right._useridname;
        _nameofackperson        = right._nameofackperson;
        _curtailmentnotes       = right._curtailmentnotes;
        _acklateflag            = right._acklateflag;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMCurtailCustomer::restore(RWDBReader& rdr)
{
    CtiLMCICustomerBase::restore(rdr);
    RWCString tempBoolString;

    rdr["requireack"] >> tempBoolString;
    tempBoolString.toLower();
    setRequireAck(tempBoolString=="y"?TRUE:FALSE);

    setCurtailReferenceId(0);
    setAcknowledgeStatus(CtiLMCurtailCustomer::NotRequiredAckStatus);
    setAckDateTime(gInvalidRWDBDateTime);
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
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Inserted customer activity area into LMCurtailCustomerActivity: " << getCompanyName() << endl;
            }

            RWDBDatabase db = getDatabase();
            RWDBTable lmCurtailCustomerActivityTable = db.table("lmcurtailcustomeractivity");

            RWDBInserter inserter = lmCurtailCustomerActivityTable.inserter();

            inserter << getCustomerId()
                     << getCurtailReferenceId()
                     << getAcknowledgeStatus()
                     << getAckDateTime()
                     << getIPAddressOfAckUser()
                     << getUserIdName()
                     << getNameOfAckPerson()
                     << getCurtailmentNotes()
                     << getCustomerDemandLevel()
                     << RWCString( ( getAckLateFlag() ? 'Y': 'N' ) );

            if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << inserter.asString().data() << endl;
            }

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
                    << lmCurtailCustomerActivityTable["currentpdl"].assign(getCustomerDemandLevel())
                    << lmCurtailCustomerActivityTable["acklateflag"].assign(RWCString( (getAckLateFlag() ? 'Y':'N') ));

            updater.where(lmCurtailCustomerActivityTable["customerid"]==getCustomerId() &&
                          lmCurtailCustomerActivityTable["curtailreferenceid"]==getCurtailReferenceId());

            if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << updater.asString().data() << endl;
            }

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

            selector.where(lmCurtailCustomerActivityTable["customerid"]==getCustomerId());

            selector.orderByDescending(lmCurtailCustomerActivityTable["curtailreferenceid"]);

            if( _LM_DEBUG & LM_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << selector.asString().data() << endl;
            }

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
const RWCString CtiLMCurtailCustomer::NotRequiredAckStatus = "Not Required";
const RWCString CtiLMCurtailCustomer::VerbalAckStatus = "Verbal";

