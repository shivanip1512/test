/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangecustomerreply.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMEnergyExchangeCustomerReply.
                        CtiLMEnergyExchangeCustomerReply maintains the state and handles
                        the persistence of groups in Load Management.

        Initial Date:  5/15/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "lmenergyexchangecustomerreply.h"
#include "lmenergyexchangehourlycustomer.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "device.h"
#include "resolvers.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMEnergyExchangeCustomerReply, CTILMENERGYEXCHANGECUSTOMERREPLY_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply::CtiLMEnergyExchangeCustomerReply()
{
}

CtiLMEnergyExchangeCustomerReply::CtiLMEnergyExchangeCustomerReply(RWDBReader& rdr)
{
    restore(rdr);
}

CtiLMEnergyExchangeCustomerReply::CtiLMEnergyExchangeCustomerReply(const CtiLMEnergyExchangeCustomerReply& customerreply)
{
    operator=(customerreply);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply::~CtiLMEnergyExchangeCustomerReply()
{

    _lmenergyexchangehourlycustomers.clearAndDestroy();
}

/*---------------------------------------------------------------------------
    getCustomerId

    Returns the unique id of the customer
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeCustomerReply::getCustomerId() const
{

    return _customerid;
}

/*---------------------------------------------------------------------------
    getOfferId

    Returns the offer id of the customer in a program
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeCustomerReply::getOfferId() const
{

    return _offerid;
}

/*---------------------------------------------------------------------------
    getAcceptStatus

    Returns the accept status of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeCustomerReply::getAcceptStatus() const
{

    return _acceptstatus;
}

/*---------------------------------------------------------------------------
    getAcceptDateTime

    Returns the accept date time of the customer
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMEnergyExchangeCustomerReply::getAcceptDateTime() const
{

    return _acceptdatetime;
}

/*---------------------------------------------------------------------------
    getRevisionNumber

    Returns the offer revision number the customer has accepted or is
    currently being offered in a program
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeCustomerReply::getRevisionNumber() const
{

    return _revisionnumber;
}

/*---------------------------------------------------------------------------
    getIPAddressOfAcceptUser

    Returns the ip address of the accept user of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeCustomerReply::getIPAddressOfAcceptUser() const
{

    return _ipaddressofacceptuser;
}

/*---------------------------------------------------------------------------
    getUserIdName

    Returns the user id name of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeCustomerReply::getUserIdName() const
{

    return _useridname;
}

/*---------------------------------------------------------------------------
    getNameOfAcceptPerson

    Returns the name of accept person of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeCustomerReply::getNameOfAcceptPerson() const
{

    return _nameofacceptperson;
}

/*---------------------------------------------------------------------------
    getEnergyExchangeNotes

    Returns the energy exchange notes of the customer
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeCustomerReply::getEnergyExchangeNotes() const
{

    return _energyexchangenotes;
}

/*---------------------------------------------------------------------------
    getLMEnergyExchangeHourlyCustomers

    Returns a list of each hours offer for the customer reply
---------------------------------------------------------------------------*/
RWOrdered& CtiLMEnergyExchangeCustomerReply::getLMEnergyExchangeHourlyCustomers()
{

    return _lmenergyexchangehourlycustomers;
}

/*---------------------------------------------------------------------------
    setCustomerId

    Sets the id of the customer - use with caution
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setCustomerId(LONG custid)
{

    _customerid = custid;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setOfferId

    Sets the offer id of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setOfferId(LONG offid)
{

    _offerid = offid;
    return *this;
}

/*---------------------------------------------------------------------------
    setAcceptStatus

    Sets the accept status of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setAcceptStatus(const RWCString& accstatus)
{

    _acceptstatus = accstatus;
    return *this;
}

/*---------------------------------------------------------------------------
    setAcceptDateTime

    Sets the accept date time of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setAcceptDateTime(const RWDBDateTime& acctime)
{

    _acceptdatetime = acctime;
    return *this;
}

/*---------------------------------------------------------------------------
    setRevisionNumber

    Sets the offer revision number of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setRevisionNumber(LONG revnumber)
{

    _revisionnumber = revnumber;
    return *this;
}

/*---------------------------------------------------------------------------
    setIPAddressOfAcceptUser

    Sets the ip address of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setIPAddressOfAcceptUser(const RWCString& ipaddress)
{

    _ipaddressofacceptuser = ipaddress;
    return *this;
}

/*---------------------------------------------------------------------------
    setUserIdName

    Sets the user id name of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setUserIdName(const RWCString& username)
{

    _useridname = username;
    return *this;
}

/*---------------------------------------------------------------------------
    setNameOfAcceptPerson

    Sets the name of the accept person of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setNameOfAcceptPerson(const RWCString& nameaccperson)
{

    _nameofacceptperson = nameaccperson;
    return *this;
}

/*---------------------------------------------------------------------------
    setEnergyExchangeNotes

    Sets the energy exchange notes of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setEnergyExchangeNotes(const RWCString& exchangenotes)
{

    _energyexchangenotes = exchangenotes;
    return *this;
}


/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomerReply::restoreGuts(RWvistream& istrm)
{



    RWCollectable::restoreGuts( istrm );

    RWTime tempTime;
    istrm >> _customerid
    >> _offerid
    >> _acceptstatus
    >> tempTime
    >> _revisionnumber
    >> _ipaddressofacceptuser
    >> _useridname
    >> _nameofacceptperson
    >> _energyexchangenotes
    >> _lmenergyexchangehourlycustomers;

    _acceptdatetime = RWDBDateTime(tempTime);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomerReply::saveGuts(RWvostream& ostrm ) const
{



    RWCollectable::saveGuts( ostrm );

    ostrm << _customerid
    << _offerid
    << _acceptstatus
    << _acceptdatetime.rwtime()
    << _revisionnumber
    << _ipaddressofacceptuser
    << _useridname
    << _nameofacceptperson
    << _energyexchangenotes
    << _lmenergyexchangehourlycustomers;

    return;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply* CtiLMEnergyExchangeCustomerReply::replicate() const
{
    return(new CtiLMEnergyExchangeCustomerReply(*this));
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::operator=(const CtiLMEnergyExchangeCustomerReply& right)
{


    if( this != &right )
    {
        _customerid = right._customerid;
        _offerid = right._offerid;
        _acceptstatus = right._acceptstatus;
        _acceptdatetime = right._acceptdatetime;
        _revisionnumber = right._revisionnumber;
        _ipaddressofacceptuser = right._ipaddressofacceptuser;
        _useridname = right._useridname;
        _nameofacceptperson = right._nameofacceptperson;
        _energyexchangenotes = right._energyexchangenotes;

        _lmenergyexchangehourlycustomers.clearAndDestroy();
        for(LONG i=0;i<right._lmenergyexchangehourlycustomers.entries();i++)
        {
            _lmenergyexchangehourlycustomers.insert(((CtiLMEnergyExchangeHourlyCustomer*)right._lmenergyexchangehourlycustomers[i])->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeCustomerReply::operator==(const CtiLMEnergyExchangeCustomerReply& right) const
{

    return( (getCustomerId() == right.getCustomerId()) &&
            (getOfferId() == right.getOfferId()) &&
            (getRevisionNumber() == right.getRevisionNumber()) );
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeCustomerReply::operator!=(const CtiLMEnergyExchangeCustomerReply& right) const
{

    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomerReply::restore(RWDBReader& rdr)
{


    rdr["customerid"] >> _customerid;
    rdr["offerid"] >> _offerid;
    rdr["acceptstatus"] >> _acceptstatus;
    rdr["acceptdatetime"] >> _acceptdatetime;
    rdr["revisionnumber"] >> _revisionnumber;
    rdr["ipaddressofacceptuser"] >> _ipaddressofacceptuser;
    rdr["useridname"] >> _useridname;
    rdr["nameofacceptperson"] >> _nameofacceptperson;
    rdr["energyexchangenotes"] >> _energyexchangenotes;
}

/*---------------------------------------------------------------------------
    addLMEnergyExchangeCustomerReplyTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomerReply::addLMEnergyExchangeCustomerReplyTable()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Inserted customer activity into LMEnergyExchangeCustomerReply, customerid: " << getCustomerId() << ", offerid: " << getOfferId() << ", revision: " << getRevisionNumber() << endl;
            }

            RWDBDatabase db = getDatabase();
            RWDBTable lmEnergyExchangeCustomerReplyTable = db.table("lmenergyexchangecustomerreply");

            RWDBInserter inserter = lmEnergyExchangeCustomerReplyTable.inserter();

            inserter << getCustomerId()
            << getOfferId()
            << getAcceptStatus()
            << getAcceptDateTime()
            << getRevisionNumber()
            << getIPAddressOfAcceptUser()
            << getUserIdName()
            << getNameOfAcceptPerson()
            << getEnergyExchangeNotes();

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
    updateLMEnergyExchangeCustomerReplyTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomerReply::updateLMEnergyExchangeCustomerReplyTable()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmEnergyExchangeCustomerReplyTable = db.table("lmenergyexchangecustomerreply");
            RWDBUpdater updater = lmEnergyExchangeCustomerReplyTable.updater();

            updater << lmEnergyExchangeCustomerReplyTable["acceptstatus"].assign(getAcceptStatus())
            << lmEnergyExchangeCustomerReplyTable["acceptdatetime"].assign(getAcceptDateTime())
            << lmEnergyExchangeCustomerReplyTable["ipaddressofacceptuser"].assign(getIPAddressOfAcceptUser())
            << lmEnergyExchangeCustomerReplyTable["useridname"].assign(getUserIdName())
            << lmEnergyExchangeCustomerReplyTable["nameofacceptperson"].assign(getNameOfAcceptPerson())
            << lmEnergyExchangeCustomerReplyTable["energyexchangenotes"].assign(getEnergyExchangeNotes());

            updater.where(lmEnergyExchangeCustomerReplyTable["customerid"]==getCustomerId() &&
                          lmEnergyExchangeCustomerReplyTable["offerid"]==getOfferId() &&
                          lmEnergyExchangeCustomerReplyTable["revisionnumber"]==getRevisionNumber());

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

    Writes out the dynamic information for this customer.
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomerReply::dumpDynamicData()
{


    updateLMEnergyExchangeCustomerReplyTable();
}

/*---------------------------------------------------------------------------
    restoreDynamicData

    Restores self's dynamic data given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomerReply::restoreDynamicData(RWDBReader& rdr)
{


    /*CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if ( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmEnergyExchangeCustomerReplyTable = db.table("lmenergyexchangecustomerreply");
            RWDBSelector selector = db.selector();
            selector << lmEnergyExchangeCustomerReplyTable["offerid"]
                     << lmEnergyExchangeCustomerReplyTable["acceptstatus"]
                     << lmEnergyExchangeCustomerReplyTable["acceptdatetime"]
                     << lmEnergyExchangeCustomerReplyTable["revisionnumber"]
                     << lmEnergyExchangeCustomerReplyTable["ipaddressofacceptuser"]
                     << lmEnergyExchangeCustomerReplyTable["useridname"]
                     << lmEnergyExchangeCustomerReplyTable["nameofacceptperson"]
                     << lmEnergyExchangeCustomerReplyTable["energyexchangenotes"];

            selector.from(lmEnergyExchangeCustomerReplyTable);

            selector.where(lmEnergyExchangeCustomerReplyTable["deviceid"]==getDeviceId());

            selector.orderByDescending(lmEnergyExchangeCustomerReplyTable["offerid"]);

            if( _LM_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << selector.asString().data() << endl;
            }

            RWDBReader rdr = selector.reader(conn);

            if(rdr())
            {
                rdr["offerid"] >> _offerid;
                rdr["acceptstatus"] >> _acceptstatus;
                rdr["acceptdatetime"] >> _acceptdatetime;
                rdr["revisionnumber"] >> _revisionnumber;
                rdr["ipaddressofacceptuser"] >> _ipaddressofacceptuser;
                rdr["useridname"] >> _useridname;
                rdr["nameofacceptperson"] >> _nameofacceptperson;
                rdr["energyexchangenotes"] >> _energyexchangenotes;
            }

        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << RWTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }*/
}

// Static Members

// Possible accept statuses
const RWCString CtiLMEnergyExchangeCustomerReply::NoResponseAcceptStatus = "NoResponse";
const RWCString CtiLMEnergyExchangeCustomerReply::AcceptedAcceptStatus = "Accepted";
const RWCString CtiLMEnergyExchangeCustomerReply::DeclinedAcceptStatus = "Declined";

