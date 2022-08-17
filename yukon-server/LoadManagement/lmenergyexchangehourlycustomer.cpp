#include "precompiled.h"

#include "dbaccess.h"
#include "lmenergyexchangehourlycustomer.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "resolvers.h"
#include "database_connection.h"
#include "database_writer.h"
#include "database_util.h"

using std::endl;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMEnergyExchangeHourlyCustomer, CTILMENERGYEXCHANGEHOURLYCUSTOMER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer::CtiLMEnergyExchangeHourlyCustomer() :
_customerid(0),
_offerid(0),
_revisionnumber(0),
_hour(0),
_amountcommitted(0)
{
}

CtiLMEnergyExchangeHourlyCustomer::CtiLMEnergyExchangeHourlyCustomer(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMEnergyExchangeHourlyCustomer::CtiLMEnergyExchangeHourlyCustomer(const CtiLMEnergyExchangeHourlyCustomer& customer)
{
    operator=(customer);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer::~CtiLMEnergyExchangeHourlyCustomer()
{
}

/*---------------------------------------------------------------------------
    getCustomerId

    Returns the unique id of the customer
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeHourlyCustomer::getCustomerId() const
{

    return _customerid;
}

/*---------------------------------------------------------------------------
    getOfferId

    Returns the offer id of the customer in a program
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeHourlyCustomer::getOfferId() const
{

    return _offerid;
}

/*---------------------------------------------------------------------------
    getRevisionNumber

    Returns the offer revision number the customer has accepted or is
    currently being offered in a program
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeHourlyCustomer::getRevisionNumber() const
{

    return _revisionnumber;
}

/*---------------------------------------------------------------------------
    getHour

    Returns the hour number
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeHourlyCustomer::getHour() const
{

    return _hour;
}

/*---------------------------------------------------------------------------
    getAmountCommitted

    Returns the amount of kwh committed of the customer in a program
---------------------------------------------------------------------------*/
DOUBLE CtiLMEnergyExchangeHourlyCustomer::getAmountCommitted() const
{

    return _amountcommitted;
}

/*---------------------------------------------------------------------------
    setCustomerId

    Sets the id of the customer - use with caution
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer& CtiLMEnergyExchangeHourlyCustomer::setCustomerId(LONG custid)
{

    _customerid = custid;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setOfferId

    Sets the offer id of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer& CtiLMEnergyExchangeHourlyCustomer::setOfferId(LONG offid)
{

    _offerid = offid;
    return *this;
}

/*---------------------------------------------------------------------------
    setRevisionNumber

    Sets the offer revision number of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer& CtiLMEnergyExchangeHourlyCustomer::setRevisionNumber(LONG revnumber)
{

    _revisionnumber = revnumber;
    return *this;
}

/*---------------------------------------------------------------------------
    setHour

    Sets the hour of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer& CtiLMEnergyExchangeHourlyCustomer::setHour(LONG hour)
{

    _hour = hour;
    return *this;
}

/*---------------------------------------------------------------------------
    setAmountCommitted

    Sets the amount committed of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer& CtiLMEnergyExchangeHourlyCustomer::setAmountCommitted(DOUBLE committed)
{

    _amountcommitted = committed;
    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer* CtiLMEnergyExchangeHourlyCustomer::replicate() const
{
    return(CTIDBG_new CtiLMEnergyExchangeHourlyCustomer(*this));
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer& CtiLMEnergyExchangeHourlyCustomer::operator=(const CtiLMEnergyExchangeHourlyCustomer& right)
{


    if( this != &right )
    {
        _customerid = right._customerid;
        _offerid = right._offerid;
        _revisionnumber = right._revisionnumber;
        _hour = right._hour;
        _amountcommitted = right._amountcommitted;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeHourlyCustomer::operator==(const CtiLMEnergyExchangeHourlyCustomer& right) const
{

    return( (getCustomerId() == right.getCustomerId()) &&
            (getOfferId() == right.getOfferId()) &&
            (getRevisionNumber() == right.getRevisionNumber()) );
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeHourlyCustomer::operator!=(const CtiLMEnergyExchangeHourlyCustomer& right) const
{

    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyCustomer::restore(Cti::RowReader &rdr)
{


    rdr["customerid"] >> _customerid;
    rdr["offerid"] >> _offerid;
    rdr["revisionnumber"] >> _revisionnumber;
    rdr["hour"] >> _hour;
    rdr["amountcommitted"] >> _amountcommitted;
}

/*---------------------------------------------------------------------------
    addLMEnergyExchangeHourlyCustomerTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyCustomer::addLMEnergyExchangeHourlyCustomerTable()
{
    static const std::string sql = "insert into lmenergyexchangehourlycustomer values (?, ?, ?, ?, ?)";

    CTILOG_INFO(dout, "Inserted customer activity into LMEnergyExchangeHourlyCustomer, customerid: " << getCustomerId() << ", offerid: " << getOfferId() << ", revisionnumber: " << getRevisionNumber());

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getCustomerId()
        << getOfferId()
        << getRevisionNumber()
        << getHour()
        << getAmountCommitted();

    Cti::Database::executeCommand( inserter, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) );
}

/*---------------------------------------------------------------------------
    updateLMEnergyExchangeHourlyCustomerTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyCustomer::updateLMEnergyExchangeHourlyCustomerTable()
{
    static const std::string sql = "update lmenergyexchangehourlycustomer"
                                   " set "
                                        "amountcommitted = ?"
                                   " where "
                                        "customerid = ? and "
                                        "offerid = ? and "
                                        "revisionnumber = ? and "
                                        "hour = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getAmountCommitted()
        << getCustomerId()
        << getOfferId()
        << getRevisionNumber()
        << getHour();

    Cti::Database::executeCommand( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) );
}

// Static Members

// Possible

