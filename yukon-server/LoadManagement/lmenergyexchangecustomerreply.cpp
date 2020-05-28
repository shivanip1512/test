#include "precompiled.h"

#include "dbaccess.h"
#include "lmenergyexchangecustomerreply.h"
#include "lmenergyexchangehourlycustomer.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "resolvers.h"
#include "utility.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"

using std::string;
using std::endl;
using std::vector;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMEnergyExchangeCustomerReply, CTILMENERGYEXCHANGECUSTOMERREPLY_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply::CtiLMEnergyExchangeCustomerReply() :
_customerid(0),
_offerid(0),
_revisionnumber(0)
{
}

CtiLMEnergyExchangeCustomerReply::CtiLMEnergyExchangeCustomerReply(Cti::RowReader &rdr)
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
    delete_container( _lmenergyexchangehourlycustomers );
    _lmenergyexchangehourlycustomers.clear();
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
const string& CtiLMEnergyExchangeCustomerReply::getAcceptStatus() const
{

    return _acceptstatus;
}

/*---------------------------------------------------------------------------
    getAcceptDateTime

    Returns the accept date time of the customer
---------------------------------------------------------------------------*/
const CtiTime& CtiLMEnergyExchangeCustomerReply::getAcceptDateTime() const
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
const string& CtiLMEnergyExchangeCustomerReply::getIPAddressOfAcceptUser() const
{

    return _ipaddressofacceptuser;
}

/*---------------------------------------------------------------------------
    getUserIdName

    Returns the user id name of the customer
---------------------------------------------------------------------------*/
const string& CtiLMEnergyExchangeCustomerReply::getUserIdName() const
{

    return _useridname;
}

/*---------------------------------------------------------------------------
    getNameOfAcceptPerson

    Returns the name of accept person of the customer
---------------------------------------------------------------------------*/
const string& CtiLMEnergyExchangeCustomerReply::getNameOfAcceptPerson() const
{

    return _nameofacceptperson;
}

/*---------------------------------------------------------------------------
    getEnergyExchangeNotes

    Returns the energy exchange notes of the customer
---------------------------------------------------------------------------*/
const string& CtiLMEnergyExchangeCustomerReply::getEnergyExchangeNotes() const
{

    return _energyexchangenotes;
}

/*---------------------------------------------------------------------------
    getLMEnergyExchangeHourlyCustomers

    Returns a list of each hours offer for the customer reply
---------------------------------------------------------------------------*/
vector<CtiLMEnergyExchangeHourlyCustomer*>& CtiLMEnergyExchangeCustomerReply::getLMEnergyExchangeHourlyCustomers()
{

    return _lmenergyexchangehourlycustomers;
}

const vector<CtiLMEnergyExchangeHourlyCustomer*>& CtiLMEnergyExchangeCustomerReply::getLMEnergyExchangeHourlyCustomers() const
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
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setAcceptStatus(const string& accstatus)
{

    _acceptstatus = accstatus;
    return *this;
}

/*---------------------------------------------------------------------------
    setAcceptDateTime

    Sets the accept date time of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setAcceptDateTime(const CtiTime& acctime)
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
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setIPAddressOfAcceptUser(const string& ipaddress)
{

    _ipaddressofacceptuser = ipaddress;
    return *this;
}

/*---------------------------------------------------------------------------
    setUserIdName

    Sets the user id name of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setUserIdName(const string& username)
{

    _useridname = username;
    return *this;
}

/*---------------------------------------------------------------------------
    setNameOfAcceptPerson

    Sets the name of the accept person of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setNameOfAcceptPerson(const string& nameaccperson)
{

    _nameofacceptperson = nameaccperson;
    return *this;
}

/*---------------------------------------------------------------------------
    setEnergyExchangeNotes

    Sets the energy exchange notes of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply& CtiLMEnergyExchangeCustomerReply::setEnergyExchangeNotes(const string& exchangenotes)
{

    _energyexchangenotes = exchangenotes;
    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeCustomerReply* CtiLMEnergyExchangeCustomerReply::replicate() const
{
    return(CTIDBG_new CtiLMEnergyExchangeCustomerReply(*this));
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

        delete_container( _lmenergyexchangehourlycustomers );
        _lmenergyexchangehourlycustomers.clear();
        for(LONG i=0;i<right._lmenergyexchangehourlycustomers.size();i++)
        {
            _lmenergyexchangehourlycustomers.push_back(((CtiLMEnergyExchangeHourlyCustomer*)right._lmenergyexchangehourlycustomers[i])->replicate());
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

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomerReply::restore(Cti::RowReader &rdr)
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
    static const std::string sql = "insert into lmenergyexchangecustomerreply values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    CTILOG_INFO(dout, "Inserted customer activity into LMEnergyExchangeCustomerReply, customerid: " << getCustomerId() << ", offerid: " << getOfferId() << ", revision: " << getRevisionNumber());

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getCustomerId()
        << getOfferId()
        << getAcceptStatus()
        << getAcceptDateTime()
        << getRevisionNumber()
        << getIPAddressOfAcceptUser()
        << getUserIdName()
        << getNameOfAcceptPerson()
        << getEnergyExchangeNotes();

    Cti::Database::executeCommand( inserter, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) );
}

/*---------------------------------------------------------------------------
    updateLMEnergyExchangeCustomerReplyTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomerReply::updateLMEnergyExchangeCustomerReplyTable()
{
    static const std::string sql = "update lmenergyexchangecustomerreply"
                                   " set "
                                        "acceptstatus = ?, "
                                        "acceptdatetime = ?, "
                                        "ipaddressofacceptuser = ?, "
                                        "useridname = ?, "
                                        "nameofacceptperson = ?, "
                                        "energyexchangenotes = ?"
                                   " where "
                                        "customerid = ? and "
                                        "offerid = ? and "
                                        "revisionnumber = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getAcceptStatus()[0]
        << getAcceptDateTime()
        << getIPAddressOfAcceptUser()[0]
        << getUserIdName()[0]
        << getNameOfAcceptPerson()[0]
        << getEnergyExchangeNotes()[0]
        << getCustomerId()
        << getOfferId()
        << getRevisionNumber();

    Cti::Database::executeCommand( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) );
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

    Restores self's dynamic data given a Reader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeCustomerReply::restoreDynamicData(Cti::RowReader &rdr)
{
/*    static const string sql =  "SELECT XCR.offerid, XCR.acceptstatus, XCR.acceptdatetime, XCR.revisionnumber, "
                                   "XCR.ipaddressofacceptuser, XCR.useridname, XCR.nameofacceptperson, "
                                   "XCR.energyexchangenotes "
                               "FROM lmenergyexchangecustomerreply XCR "
                               "WHERE XCR.deviceid = ? "
                               "ORDER BY XCR.offerid DESC";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    rdr << getDeviceId();

    rdr.execute();

    if( _LM_DEBUG )
    {
        CTILOG_DEBUG(dout, rdr.asString().c_str());
    }

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
    }*/
}

std::size_t CtiLMEnergyExchangeCustomerReply::getMemoryConsumption() const
{
    // the fixed object size
    std::size_t sz = sizeof( *this );

    // the local dynamic size
    sz  +=  dynamic_sizeof( _acceptstatus )
        +   dynamic_sizeof( _ipaddressofacceptuser )
        +   dynamic_sizeof( _useridname )
        +   dynamic_sizeof( _nameofacceptperson )
        +   dynamic_sizeof( _energyexchangenotes );

    sz += _lmenergyexchangehourlycustomers.capacity() * sizeof( CtiLMEnergyExchangeHourlyCustomer* );
    for ( const auto & customer : _lmenergyexchangehourlycustomers )
    {
        sz += customer->getMemoryConsumption();
    }

    return sz;
}


// Static Members

// Possible accept statuses
const string CtiLMEnergyExchangeCustomerReply::NoResponseAcceptStatus = "NoResponse";
const string CtiLMEnergyExchangeCustomerReply::AcceptedAcceptStatus = "Accepted";
const string CtiLMEnergyExchangeCustomerReply::DeclinedAcceptStatus = "Declined";

