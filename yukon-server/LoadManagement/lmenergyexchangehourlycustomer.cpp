/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangehourlycustomer.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMEnergyExchangeHourlyCustomer.
                        CtiLMEnergyExchangeHourlyCustomer maintains the state and handles
                        the persistence of groups in Load Management.

        Initial Date:  5/15/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmenergyexchangehourlycustomer.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "device.h"
#include "resolvers.h"

extern BOOL _LM_DEBUG;

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
ULONG CtiLMEnergyExchangeHourlyCustomer::getCustomerId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _customerid;
}

/*---------------------------------------------------------------------------
    getOfferId

    Returns the offer id of the customer in a program
---------------------------------------------------------------------------*/
ULONG CtiLMEnergyExchangeHourlyCustomer::getOfferId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _offerid;
}

/*---------------------------------------------------------------------------
    getRevisionNumber

    Returns the offer revision number the customer has accepted or is
    currently being offered in a program
---------------------------------------------------------------------------*/
ULONG CtiLMEnergyExchangeHourlyCustomer::getRevisionNumber() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _revisionnumber;
}

/*---------------------------------------------------------------------------
    getHour

    Returns the hour number
---------------------------------------------------------------------------*/
ULONG CtiLMEnergyExchangeHourlyCustomer::getHour() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _hour;
}

/*---------------------------------------------------------------------------
    getAmountCommitted

    Returns the amount of kwh committed of the customer in a program
---------------------------------------------------------------------------*/
DOUBLE CtiLMEnergyExchangeHourlyCustomer::getAmountCommitted() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _amountcommitted;
}

/*---------------------------------------------------------------------------
    setCustomerId

    Sets the id of the customer - use with caution
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer& CtiLMEnergyExchangeHourlyCustomer::setCustomerId(ULONG custid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _customerid = custid;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setOfferId

    Sets the offer id of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer& CtiLMEnergyExchangeHourlyCustomer::setOfferId(ULONG offid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _offerid = offid;
    return *this;
}

/*---------------------------------------------------------------------------
    setRevisionNumber

    Sets the offer revision number of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer& CtiLMEnergyExchangeHourlyCustomer::setRevisionNumber(ULONG revnumber)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _revisionnumber = revnumber;
    return *this;
}

/*---------------------------------------------------------------------------
    setHour

    Sets the hour of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer& CtiLMEnergyExchangeHourlyCustomer::setHour(ULONG hour)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _hour = hour;
    return *this;
}

/*---------------------------------------------------------------------------
    setAmountCommitted

    Sets the amount committed of the customer
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyCustomer& CtiLMEnergyExchangeHourlyCustomer::setAmountCommitted(DOUBLE committed)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _amountcommitted = committed;
    return *this;
}


/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyCustomer::restoreGuts(RWvistream& istrm)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return( (getCustomerId() == right.getCustomerId()) &&
            (getOfferId() == right.getOfferId()) &&
            (getRevisionNumber() == right.getRevisionNumber()) );
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeHourlyCustomer::operator!=(const CtiLMEnergyExchangeHourlyCustomer& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyCustomer::restore(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            if( _LM_DEBUG )
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
    updateLMEnergyExchangeHourlyCustomerTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyCustomer::updateLMEnergyExchangeHourlyCustomerTable()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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

// Static Members

// Possible

