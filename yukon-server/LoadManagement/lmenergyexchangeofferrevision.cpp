/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangeofferrevision.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMEnergyExchangeOfferRevision.
                        CtiLMEnergyExchangeOfferRevision maintains the state and handles
                        the persistence of groups in Load Management.

        Initial Date:  5/9/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "lmenergyexchangeofferrevision.h"
#include "lmenergyexchangehourlyoffer.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "resolvers.h"
#include "utility.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"

using std::vector;

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMEnergyExchangeOfferRevision, CTILMENERGYEXCHANGEOFFERREVISION_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision::CtiLMEnergyExchangeOfferRevision() : 
_offerid(0),
_revisionnumber(0)
{
}

CtiLMEnergyExchangeOfferRevision::CtiLMEnergyExchangeOfferRevision(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMEnergyExchangeOfferRevision::CtiLMEnergyExchangeOfferRevision(const CtiLMEnergyExchangeOfferRevision& revision)
{
    operator=(revision);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision::~CtiLMEnergyExchangeOfferRevision()
{

    delete_container(_lmenergyexchangehourlyoffers);
    _lmenergyexchangehourlyoffers.clear();
}

/*---------------------------------------------------------------------------
    getOfferId

    Returns the reference id of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeOfferRevision::getOfferId() const
{

    return _offerid;
}

/*---------------------------------------------------------------------------
    getRevisionNumber

    Returns the reference id of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeOfferRevision::getRevisionNumber() const
{

    return _revisionnumber;
}

/*---------------------------------------------------------------------------
    getActionDateTime

    Returns the action datetime of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMEnergyExchangeOfferRevision::getActionDateTime() const
{

    return _actiondatetime;
}

/*---------------------------------------------------------------------------
    getNotificationDateTime

    Returns the Notification datetime of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMEnergyExchangeOfferRevision::getNotificationDateTime() const
{

    return _notificationdatetime;
}

/*---------------------------------------------------------------------------
    getOfferExpirationDateTime

    Returns the expiration datetime of the current offer for the
    energy exchange program.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMEnergyExchangeOfferRevision::getOfferExpirationDateTime() const
{

    return _offerexpirationdatetime;
}

/*---------------------------------------------------------------------------
    getAdditionalInfo

    Returns the additional info of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
const string& CtiLMEnergyExchangeOfferRevision::getAdditionalInfo() const
{

    return _additionalinfo;
}

/*---------------------------------------------------------------------------
    getLMEnergyExchangeHourlyOffers

    Returns a list of the price offers for each hour in the revision.
---------------------------------------------------------------------------*/
vector<CtiLMEnergyExchangeHourlyOffer*>& CtiLMEnergyExchangeOfferRevision::getLMEnergyExchangeHourlyOffers()
{

    return _lmenergyexchangehourlyoffers;
}

/*---------------------------------------------------------------------------
    setOfferId

    Sets the reference id of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::setOfferId(LONG offid)
{

    _offerid = offid;

    return *this;
}

/*---------------------------------------------------------------------------
    setRevisionNumber

    Sets the reference id of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::setRevisionNumber(LONG revnum)
{

    _revisionnumber = revnum;

    return *this;
}

/*---------------------------------------------------------------------------
    setActionDateTime

    Sets the action datetime of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::setActionDateTime(const CtiTime& actiontime)
{

    _actiondatetime = actiontime;

    return *this;
}

/*---------------------------------------------------------------------------
    setNotificationDateTime

    Sets the notification datetime of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::setNotificationDateTime(const CtiTime& notifytime)
{

    _notificationdatetime = notifytime;

    return *this;
}

/*---------------------------------------------------------------------------
    setOfferExpirationDateTime

    Sets the expirtation datetime of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::setOfferExpirationDateTime(const CtiTime& expirationtime)
{

    _offerexpirationdatetime = expirationtime;

    return *this;
}

/*---------------------------------------------------------------------------
    setAdditionalInfo

    Sets the additional info of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::setAdditionalInfo(const string& additional)
{

    _additionalinfo = additional;

    return *this;
}


/*---------------------------------------------------------------------------
    getFirstCurtailHour

    Returns .
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeOfferRevision::getFirstCurtailHour() const
{
    LONG returnLONG = 0;

    for(long i=0;i<_lmenergyexchangehourlyoffers.size();i++)
    {
        CtiLMEnergyExchangeHourlyOffer* currentHourlyOffer = (CtiLMEnergyExchangeHourlyOffer*)_lmenergyexchangehourlyoffers[i];
        if( currentHourlyOffer->getAmountRequested() > 0.0 &&
            currentHourlyOffer->getPrice() > 0 )
        {
            returnLONG = currentHourlyOffer->getHour();
            break;
        }
    }

    return returnLONG;
}

/*---------------------------------------------------------------------------
    getLastCurtailHour

    Returns .
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeOfferRevision::getLastCurtailHour() const
{
    LONG returnLONG = 0;

    for(long i=_lmenergyexchangehourlyoffers.size()-1;i>=0;i--)
    {
        CtiLMEnergyExchangeHourlyOffer* currentHourlyOffer = (CtiLMEnergyExchangeHourlyOffer*)_lmenergyexchangehourlyoffers[i];
        if( currentHourlyOffer->getAmountRequested() > 0.0 &&
            currentHourlyOffer->getPrice() > 0 )
        {
            returnLONG = currentHourlyOffer->getHour();
            break;
        }
    }

    return returnLONG;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::restoreGuts(RWvistream& istrm)
{



    RWCollectable::restoreGuts( istrm );

    CtiTime tempTime1;
    CtiTime tempTime2;
    CtiTime tempTime3;

    istrm >> _offerid
    >> _revisionnumber
    >> tempTime1
    >> tempTime2
    >> tempTime3
    >> _additionalinfo
    >> _lmenergyexchangehourlyoffers;

    _actiondatetime = CtiTime(tempTime1);
    _notificationdatetime = CtiTime(tempTime2);
    _offerexpirationdatetime = CtiTime(tempTime3);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::saveGuts(RWvostream& ostrm ) const
{



    RWCollectable::saveGuts( ostrm );

    ostrm << _offerid
    << _revisionnumber
    << _actiondatetime
    << _notificationdatetime
    << _offerexpirationdatetime
    << _additionalinfo
    << _lmenergyexchangehourlyoffers;

    return;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision* CtiLMEnergyExchangeOfferRevision::replicate() const
{
    return(CTIDBG_new CtiLMEnergyExchangeOfferRevision(*this));
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::operator=(const CtiLMEnergyExchangeOfferRevision& right)
{


    if( this != &right )
    {
        _offerid = right._offerid;
        _revisionnumber = right._revisionnumber;
        _actiondatetime = right._actiondatetime;
        _notificationdatetime = right._notificationdatetime;
        _offerexpirationdatetime = right._offerexpirationdatetime;
        _additionalinfo = right._additionalinfo;

        delete_container(_lmenergyexchangehourlyoffers);
        _lmenergyexchangehourlyoffers.clear();
        for(LONG i=0;i<right._lmenergyexchangehourlyoffers.size();i++)
        {
            _lmenergyexchangehourlyoffers.push_back(((CtiLMEnergyExchangeHourlyOffer*)right._lmenergyexchangehourlyoffers[i])->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeOfferRevision::operator==(const CtiLMEnergyExchangeOfferRevision& right) const
{

    return( (getOfferId() == right.getOfferId()) && (getRevisionNumber() == right.getRevisionNumber()) );
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeOfferRevision::operator!=(const CtiLMEnergyExchangeOfferRevision& right) const
{

    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::restore(Cti::RowReader &rdr)
{


    rdr["offerid"] >> _offerid;
    rdr["revisionnumber"] >> _revisionnumber;
    rdr["actiondatetime"] >> _actiondatetime;
    rdr["notificationdatetime"] >> _notificationdatetime;
    rdr["offerexpirationdatetime"] >> _offerexpirationdatetime;
    rdr["additionalinfo"] >> _additionalinfo;
}

/*---------------------------------------------------------------------------
    addLMEnergyExchangeOfferRevisionTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::addLMEnergyExchangeOfferRevisionTable()
{
    static const std::string sql = "insert into lmenergyexchangeofferrevision values (?, ?, ?, ?, ?, ?)";

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Inserted offer revision into LMEnergyExchangeOfferRevision, offer id: " << getOfferId() << " revision number: " << getRevisionNumber() << endl;
    }

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getOfferId()
        << getRevisionNumber()
        << getActionDateTime()
        << getNotificationDateTime()
        << getOfferExpirationDateTime()
        << getAdditionalInfo();

    if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - " << inserter.asString() << endl;
    }

    inserter.execute();
}

/*---------------------------------------------------------------------------
    updateLMEnergyExchangeOfferRevisionTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::updateLMEnergyExchangeOfferRevisionTable()
{
    static const std::string sql = "update lmenergyexchangeofferrevision"
                                   " set "
                                        "actiondatetime = ?, "
                                        "notificationdatetime = ?, "
                                        "offerexpirationdatetime = ?, "
                                        "additionalinfo = ?"
                                   " where "
                                        "offerid = ? and "
                                        "revisionnumber = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getActionDateTime()
        << getNotificationDateTime()
        << getOfferExpirationDateTime()
        << getAdditionalInfo()[0]
        << getOfferId()
        << getRevisionNumber();

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
void CtiLMEnergyExchangeOfferRevision::dumpDynamicData()
{


    updateLMEnergyExchangeOfferRevisionTable();
}

/*---------------------------------------------------------------------------
    restoreDynamicData

    Restores self's dynamic data
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::restoreDynamicData()
{
    static const string sql =  "SELECT XOR.offerid, XOR.revisionnumber, XOR.actiondatetime, XOR.notificationdatetime, "
                                   "XOR.offerexpirationdatetime, XOR.additionalinfo "
                               "FROM lmenergyexchangeofferrevision XOR "
                               "WHERE XOR.offerid = ? "
                               "ORDER BY XOR.revisionnumber DESC";

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
        rdr["actiondatetime"] >> _actiondatetime;
        rdr["notificationdatetime"] >> _notificationdatetime;
        rdr["offerexpirationdatetime"] >> _offerexpirationdatetime;
        rdr["additionalinfo"] >> _additionalinfo;
    }
}

// Static Members

// Possible  statuses

