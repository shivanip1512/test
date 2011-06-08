/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangehourlycustomer.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMEnergyExchangeHourlyCustomer.
                        CtiLMEnergyExchangeHourlyCustomer maintains the state and handles
                        the persistence of groups in Load Management.

        Initial Date:  5/15/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "lmenergyexchangehourlycustomer.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "resolvers.h"
#include "database_connection.h"
#include "database_writer.h"

using std::endl;

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMEnergyExchangeHourlyCustomer, CTILMENERGYEXCHANGEHOURLYCUSTOMER_ID )

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


/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyCustomer::restoreGuts(RWvistream& istrm)
{



    RWCollectable::restoreGuts( istrm );

    istrm >> _customerid
    >> _offerid
    >> _revisionnumber
    >> _hour
    >> _amountcommitted;
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyCustomer::saveGuts(RWvostream& ostrm ) const
{



    RWCollectable::saveGuts( ostrm );

    ostrm << _customerid
    << _offerid
    << _revisionnumber
    << _hour
    << _amountcommitted;
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

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Inserted customer activity into LMEnergyExchangeHourlyCustomer, customerid: " << getCustomerId() << ", offerid: " << getOfferId() << ", revisionnumber: " << getRevisionNumber() << endl;
    }

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getCustomerId()
        << getOfferId()
        << getRevisionNumber()
        << getHour()
        << getAmountCommitted();

    if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << inserter.asString() << endl;
    }

    inserter.execute();
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

    if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << updater.asString() << endl;
    }

    updater.execute();
}

// Static Members

// Possible

