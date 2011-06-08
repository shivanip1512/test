/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangehourlyoffer.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMEnergyExchangeHourlyOffer.
                        CtiLMEnergyExchangeHourlyOffer maintains the state and handles
                        the persistence of groups in Load Management.

        Initial Date:  5/14/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "lmenergyexchangehourlyoffer.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "resolvers.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"

using std::string;
using std::endl;

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMEnergyExchangeHourlyOffer, CTILMENERGYEXCHANGEHOURLYOFFER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyOffer::CtiLMEnergyExchangeHourlyOffer() :
_offerid(0),
_revisionnumber(0),
_hour(0),
_price(0),
_amountrequested(0)
{
}

CtiLMEnergyExchangeHourlyOffer::CtiLMEnergyExchangeHourlyOffer(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMEnergyExchangeHourlyOffer::CtiLMEnergyExchangeHourlyOffer(const CtiLMEnergyExchangeHourlyOffer& hourly)
{
    operator=(hourly);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyOffer::~CtiLMEnergyExchangeHourlyOffer()
{
}

/*---------------------------------------------------------------------------
    getOfferId

    Returns the reference id of the current hour for the
    energy exchange offer.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeHourlyOffer::getOfferId() const
{

    return _offerid;
}

/*---------------------------------------------------------------------------
    getRevisionNumber

    Returns the reference id of the current hour for the
    energy exchange offer.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeHourlyOffer::getRevisionNumber() const
{

    return _revisionnumber;
}

/*---------------------------------------------------------------------------
    getHour

    Returns the hour for the energy exchange offer.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeHourlyOffer::getHour() const
{

    return _hour;
}

/*---------------------------------------------------------------------------
    getPrice

    Returns the price of the current offer for the
    energy exchange offer.
---------------------------------------------------------------------------*/
DOUBLE CtiLMEnergyExchangeHourlyOffer::getPrice() const
{

    return _price;
}

/*---------------------------------------------------------------------------
    getAmountRequested

    Returns the price of the current offer for the
    energy exchange offer.
---------------------------------------------------------------------------*/
DOUBLE CtiLMEnergyExchangeHourlyOffer::getAmountRequested() const
{

    return _amountrequested;
}

/*---------------------------------------------------------------------------
    setOfferId

    Sets the reference id of the current hour for the
    energy exchange offer.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyOffer& CtiLMEnergyExchangeHourlyOffer::setOfferId(LONG offid)
{

    _offerid = offid;

    return *this;
}

/*---------------------------------------------------------------------------
    setRevisionNumber

    Sets the revision number for the energy exchange offer.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyOffer& CtiLMEnergyExchangeHourlyOffer::setRevisionNumber(LONG revnum)
{

    _revisionnumber = revnum;

    return *this;
}

/*---------------------------------------------------------------------------
    setHour

    Sets the hour for the energy exchange offer.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyOffer& CtiLMEnergyExchangeHourlyOffer::setHour(LONG hour)
{

    _hour = hour;

    return *this;
}

/*---------------------------------------------------------------------------
    setPrice

    Sets the price of the current hour for the energy exchange offer.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyOffer& CtiLMEnergyExchangeHourlyOffer::setPrice(DOUBLE price)
{

    _price = price;

    return *this;
}

/*---------------------------------------------------------------------------
    setAmountRequested

    Sets the amount requested of the current hour for the energy exchange
    offer.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyOffer& CtiLMEnergyExchangeHourlyOffer::setAmountRequested(DOUBLE amtrequested)
{

    _amountrequested = amtrequested;

    return *this;
}


/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyOffer::restoreGuts(RWvistream& istrm)
{



    RWCollectable::restoreGuts( istrm );

    istrm >> _offerid
    >> _revisionnumber
    >> _hour
    >> _price
    >> _amountrequested;
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyOffer::saveGuts(RWvostream& ostrm ) const
{



    RWCollectable::saveGuts( ostrm );

    ostrm << _offerid
    << _revisionnumber
    << _hour
    << _price
    << _amountrequested;

    return;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyOffer* CtiLMEnergyExchangeHourlyOffer::replicate() const
{
    return(CTIDBG_new CtiLMEnergyExchangeHourlyOffer(*this));
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeHourlyOffer& CtiLMEnergyExchangeHourlyOffer::operator=(const CtiLMEnergyExchangeHourlyOffer& right)
{


    if( this != &right )
    {
        _offerid = right._offerid;
        _revisionnumber = right._revisionnumber;
        _hour = right._hour;
        _price = right._price;
        _amountrequested = right._amountrequested;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeHourlyOffer::operator==(const CtiLMEnergyExchangeHourlyOffer& right) const
{

    return( (getOfferId() == right.getOfferId()) &&
            (getRevisionNumber() == right.getRevisionNumber()) &&
            (getHour() == right.getHour()) );
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeHourlyOffer::operator!=(const CtiLMEnergyExchangeHourlyOffer& right) const
{

    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyOffer::restore(Cti::RowReader &rdr)
{


    rdr["offerid"] >> _offerid;
    rdr["revisionnumber"] >> _revisionnumber;
    rdr["hour"] >> _hour;
    rdr["price"] >> _price;
    rdr["amountrequested"] >> _amountrequested;
}

/*---------------------------------------------------------------------------
    addLMEnergyExchangeHourlyOfferTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyOffer::addLMEnergyExchangeHourlyOfferTable()
{
    static const std::string sql = "insert into lmenergyexchangehourlyoffer values (?, ?, ?, ?, ?)";

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Inserted hourly offer into LMEnergyExchangeHourlyOffer, offer id: " << getOfferId() << " revision number: " << getRevisionNumber() << endl;
    }

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getOfferId()
        << getRevisionNumber()
        << getHour()
        << getPrice()
        << getAmountRequested();

    if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << inserter.asString() << endl;
    }

    inserter.execute();
}

/*---------------------------------------------------------------------------
    updateLMEnergyExchangeHourlyOfferTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyOffer::updateLMEnergyExchangeHourlyOfferTable()
{
    static const std::string sql = "update lmenergyexchangehourlyoffer"
                                   " set "
                                        "price = ?, "
                                        "amountrequested = ?"
                                   " where "
                                        "offerid = ? and "
                                        "revisionnumber = ? and "
                                        "hour = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getPrice()
        << getAmountRequested()
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

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this customer.
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyOffer::dumpDynamicData()
{


    updateLMEnergyExchangeHourlyOfferTable();
}

/*---------------------------------------------------------------------------
    restoreDynamicData

    Restores self's dynamic data given a Reader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeHourlyOffer::restoreDynamicData()
{
    static const string sql =  "SELECT XHO.price, XHO.amountrequested "
                               "FROM lmenergyexchangehourlyoffer XHO "
                               "WHERE XHO.offerid = ? "
                               "ORDER BY XHO.revisionnumber DESC";

    Cti::Database::DatabaseConnection conn;
    Cti::Database::DatabaseReader rdr(conn, sql);

    rdr << getOfferId();

    rdr.execute();

    if( _LM_DEBUG & LM_DEBUG_DATABASE )
    {
        string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - " << loggedSQLstring << endl;
        }
    }

    if(rdr())
    {
        string tempBoolString;
        rdr["offerid"] >> _offerid;
        rdr["revisionnumber"] >> _revisionnumber;
        rdr["hour"] >> _hour;
        rdr["price"] >> _price;
        rdr["amountrequested"] >> _amountrequested;
    }

    if( !rdr.isValid() )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
    }
}

// Static Members

// Possible  statuses

