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
#include "device.h"
#include "resolvers.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMEnergyExchangeHourlyCustomer, CTILMENERGYEXCHANGEHOURLYCUSTOMER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer::CtiLMEnergyExchangeHourlyCustomer()
{
}

CtiLMEnergyExchangeHourlyCustomer::CtiLMEnergyExchangeHourlyCustomer(RWDBReader& rdr)
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
    return(new CtiLMEnergyExchangeHourlyCustomer(*this));
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

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyCustomer::restore(RWDBReader& rdr)
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


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Inserted customer activity into LMEnergyExchangeHourlyCustomer, customerid: " << getCustomerId() << ", offerid: " << getOfferId() << ", revisionnumber: " << getRevisionNumber() << endl;
            }

            RWDBDatabase db = getDatabase();
            RWDBTable lmEnergyExchangeHourlyCustomerTable = db.table("lmenergyexchangehourlycustomer");

            RWDBInserter inserter = lmEnergyExchangeHourlyCustomerTable.inserter();

            inserter << getCustomerId()
            << getOfferId()
            << getRevisionNumber()
            << getHour()
            << getAmountCommitted();

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
    updateLMEnergyExchangeHourlyCustomerTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyCustomer::updateLMEnergyExchangeHourlyCustomerTable()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmEnergyExchangeHourlyCustomerTable = db.table("lmenergyexchangehourlycustomer");
            RWDBUpdater updater = lmEnergyExchangeHourlyCustomerTable.updater();

            updater << lmEnergyExchangeHourlyCustomerTable["amountcommitted"].assign(getAmountCommitted());

            updater.where(lmEnergyExchangeHourlyCustomerTable["customerid"]==getCustomerId() &&
                          lmEnergyExchangeHourlyCustomerTable["offerid"]==getOfferId() &&
                          lmEnergyExchangeHourlyCustomerTable["revisionnumber"]==getRevisionNumber() &&
                          lmEnergyExchangeHourlyCustomerTable["hour"]==getHour());

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

// Static Members

// Possible

