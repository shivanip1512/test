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
#include "device.h"
#include "resolvers.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMEnergyExchangeOfferRevision, CTILMENERGYEXCHANGEOFFERREVISION_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision::CtiLMEnergyExchangeOfferRevision()
{
}

CtiLMEnergyExchangeOfferRevision::CtiLMEnergyExchangeOfferRevision(RWDBReader& rdr)
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

    _lmenergyexchangehourlyoffers.clearAndDestroy();
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
const RWDBDateTime& CtiLMEnergyExchangeOfferRevision::getActionDateTime() const
{

    return _actiondatetime;
}

/*---------------------------------------------------------------------------
    getNotificationDateTime

    Returns the Notification datetime of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMEnergyExchangeOfferRevision::getNotificationDateTime() const
{

    return _notificationdatetime;
}

/*---------------------------------------------------------------------------
    getOfferExpirationDateTime

    Returns the expiration datetime of the current offer for the
    energy exchange program.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMEnergyExchangeOfferRevision::getOfferExpirationDateTime() const
{

    return _offerexpirationdatetime;
}

/*---------------------------------------------------------------------------
    getAdditionalInfo

    Returns the additional info of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeOfferRevision::getAdditionalInfo() const
{

    return _additionalinfo;
}

/*---------------------------------------------------------------------------
    getLMEnergyExchangeHourlyOffers

    Returns a list of the price offers for each hour in the revision.
---------------------------------------------------------------------------*/
RWOrdered& CtiLMEnergyExchangeOfferRevision::getLMEnergyExchangeHourlyOffers()
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
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::setActionDateTime(const RWDBDateTime& actiontime)
{

    _actiondatetime = actiontime;

    return *this;
}

/*---------------------------------------------------------------------------
    setNotificationDateTime

    Sets the notification datetime of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::setNotificationDateTime(const RWDBDateTime& notifytime)
{

    _notificationdatetime = notifytime;

    return *this;
}

/*---------------------------------------------------------------------------
    setOfferExpirationDateTime

    Sets the expirtation datetime of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::setOfferExpirationDateTime(const RWDBDateTime& expirationtime)
{

    _offerexpirationdatetime = expirationtime;

    return *this;
}

/*---------------------------------------------------------------------------
    setAdditionalInfo

    Sets the additional info of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::setAdditionalInfo(const RWCString& additional)
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

    for(long i=0;i<_lmenergyexchangehourlyoffers.entries();i++)
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

    for(long i=_lmenergyexchangehourlyoffers.entries()-1;i>=0;i--)
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

    RWTime tempTime1;
    RWTime tempTime2;
    RWTime tempTime3;

    istrm >> _offerid
    >> _revisionnumber
    >> tempTime1
    >> tempTime2
    >> tempTime3
    >> _additionalinfo
    >> _lmenergyexchangehourlyoffers;

    _actiondatetime = RWDBDateTime(tempTime1);
    _notificationdatetime = RWDBDateTime(tempTime2);
    _offerexpirationdatetime = RWDBDateTime(tempTime3);
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
    << _actiondatetime.rwtime()
    << _notificationdatetime.rwtime()
    << _offerexpirationdatetime.rwtime()
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
    return(new CtiLMEnergyExchangeOfferRevision(*this));
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

        _lmenergyexchangehourlyoffers.clearAndDestroy();
        for(LONG i=0;i<right._lmenergyexchangehourlyoffers.entries();i++)
        {
            _lmenergyexchangehourlyoffers.insert(((CtiLMEnergyExchangeHourlyOffer*)right._lmenergyexchangehourlyoffers[i])->replicate());
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

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::restore(RWDBReader& rdr)
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


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - Inserted offer revision into LMEnergyExchangeOfferRevision, offer id: " << getOfferId() << " revision number: " << getRevisionNumber() << endl;
            }

            RWDBDatabase db = getDatabase();
            RWDBTable lmEnergyExchangeOfferRevisionTable = db.table("lmenergyexchangeofferrevision");

            RWDBInserter inserter = lmEnergyExchangeOfferRevisionTable.inserter();

            inserter << getOfferId()
            << getRevisionNumber()
            << getActionDateTime()
            << getNotificationDateTime()
            << getOfferExpirationDateTime()
            << getAdditionalInfo();

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
    updateLMEnergyExchangeOfferRevisionTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::updateLMEnergyExchangeOfferRevisionTable()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmEnergyExchangeOfferRevisionTable = db.table("lmenergyexchangeofferrevision");
            RWDBUpdater updater = lmEnergyExchangeOfferRevisionTable.updater();

            updater << lmEnergyExchangeOfferRevisionTable["actiondatetime"].assign(getActionDateTime())
            << lmEnergyExchangeOfferRevisionTable["notificationdatetime"].assign(getNotificationDateTime())
            << lmEnergyExchangeOfferRevisionTable["offerexpirationdatetime"].assign(getOfferExpirationDateTime())
            << lmEnergyExchangeOfferRevisionTable["additionalinfo"].assign(getAdditionalInfo());

            updater.where(lmEnergyExchangeOfferRevisionTable["offerid"]==getOfferId() &&
                          lmEnergyExchangeOfferRevisionTable["revisionnumber"]==getRevisionNumber());

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
void CtiLMEnergyExchangeOfferRevision::dumpDynamicData()
{


    updateLMEnergyExchangeOfferRevisionTable();
}

/*---------------------------------------------------------------------------
    restoreDynamicData

    Restores self's dynamic data given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::restoreDynamicData(RWDBReader& rdr)
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmEnergyExchangeOfferRevisionTable = db.table("lmenergyexchangeofferrevision");
            RWDBSelector selector = db.selector();
            selector << lmEnergyExchangeOfferRevisionTable["offerid"]
            << lmEnergyExchangeOfferRevisionTable["revisionnumber"]
            << lmEnergyExchangeOfferRevisionTable["actiondatetime"]
            << lmEnergyExchangeOfferRevisionTable["notificationdatetime"]
            << lmEnergyExchangeOfferRevisionTable["offerexpirationdatetime"]
            << lmEnergyExchangeOfferRevisionTable["additionalinfo"];

            selector.from(lmEnergyExchangeOfferRevisionTable);

            selector.where(lmEnergyExchangeOfferRevisionTable["offerid"]==getOfferId());

            selector.orderByDescending(lmEnergyExchangeOfferRevisionTable["revisionnumber"]);

            if( _LM_DEBUG & LM_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << selector.asString().data() << endl;
            }

            RWDBReader rdr = selector.reader(conn);

            if(rdr())
            {
                RWCString tempBoolString;
                rdr["offerid"] >> _offerid;
                rdr["revisionnumber"] >> _revisionnumber;
                rdr["actiondatetime"] >> _actiondatetime;
                rdr["notificationdatetime"] >> _notificationdatetime;
                rdr["offerexpirationdatetime"] >> _offerexpirationdatetime;
                rdr["additionalinfo"] >> _additionalinfo;
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

// Possible  statuses

