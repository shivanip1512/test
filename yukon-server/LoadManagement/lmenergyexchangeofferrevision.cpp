/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangeofferrevision.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMEnergyExchangeOfferRevision.
                        CtiLMEnergyExchangeOfferRevision maintains the state and handles
                        the persistence of groups in Load Management.

        Initial Date:  5/9/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmenergyexchangeofferrevision.h"
#include "lmenergyexchangehourlyoffer.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"
#include "device.h"
#include "resolvers.h"

extern BOOL _LM_DEBUG;

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
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    _lmenergyexchangehourlyoffers.clearAndDestroy();
}

/*---------------------------------------------------------------------------
    getOfferId
    
    Returns the reference id of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
ULONG CtiLMEnergyExchangeOfferRevision::getOfferId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _offerid;
}

/*---------------------------------------------------------------------------
    getRevisionNumber
    
    Returns the reference id of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
ULONG CtiLMEnergyExchangeOfferRevision::getRevisionNumber() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _revisionnumber;
}

/*---------------------------------------------------------------------------
    getActionDateTime
    
    Returns the action datetime of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMEnergyExchangeOfferRevision::getActionDateTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _actiondatetime;
}

/*---------------------------------------------------------------------------
    getNotificationDateTime
    
    Returns the Notification datetime of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMEnergyExchangeOfferRevision::getNotificationDateTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _notificationdatetime;
}

/*---------------------------------------------------------------------------
    getOfferExpirationDateTime
    
    Returns the expiration datetime of the current offer for the
    energy exchange program.
---------------------------------------------------------------------------*/
const RWDBDateTime& CtiLMEnergyExchangeOfferRevision::getOfferExpirationDateTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _offerexpirationdatetime;
}

/*---------------------------------------------------------------------------
    getAdditionalInfo
    
    Returns the additional info of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/
const RWCString& CtiLMEnergyExchangeOfferRevision::getAdditionalInfo() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _additionalinfo;
}

/*---------------------------------------------------------------------------
    getLMEnergyExchangeHourlyOffers
    
    Returns a list of the price offers for each hour in the revision.
---------------------------------------------------------------------------*/
RWOrdered& CtiLMEnergyExchangeOfferRevision::getLMEnergyExchangeHourlyOffers()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _lmenergyexchangehourlyoffers;
}

/*---------------------------------------------------------------------------
    setOfferId
    
    Sets the reference id of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/    
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::setOfferId(ULONG offid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _offerid = offid;

    return *this;
}

/*---------------------------------------------------------------------------
    setRevisionNumber
    
    Sets the reference id of the current control for the
    energy exchange program.
---------------------------------------------------------------------------*/    
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::setRevisionNumber(ULONG revnum)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _additionalinfo = additional;

    return *this;
}


/*---------------------------------------------------------------------------
    getFirstCurtailHour
    
    Returns .
---------------------------------------------------------------------------*/    
ULONG CtiLMEnergyExchangeOfferRevision::getFirstCurtailHour() const
{
    ULONG returnULong = 0;

    for(long i=0;i<_lmenergyexchangehourlyoffers.entries();i++)
    {
        CtiLMEnergyExchangeHourlyOffer* currentHourlyOffer = (CtiLMEnergyExchangeHourlyOffer*)_lmenergyexchangehourlyoffers[i];
        if( currentHourlyOffer->getAmountRequested() > 0.0 &&
            currentHourlyOffer->getPrice() > 0 )
        {
            returnULong = currentHourlyOffer->getHour();
            break;
        }
    }

    return returnULong;
}

/*---------------------------------------------------------------------------
    getLastCurtailHour
    
    Returns .
---------------------------------------------------------------------------*/    
ULONG CtiLMEnergyExchangeOfferRevision::getLastCurtailHour() const
{
    ULONG returnULong = 0;

    for(long i=_lmenergyexchangehourlyoffers.entries()-1;i>=0;i--)
    {
        CtiLMEnergyExchangeHourlyOffer* currentHourlyOffer = (CtiLMEnergyExchangeHourlyOffer*)_lmenergyexchangehourlyoffers[i];
        if( currentHourlyOffer->getAmountRequested() > 0.0 &&
            currentHourlyOffer->getPrice() > 0 )
        {
            returnULong = currentHourlyOffer->getHour();
            break;
        }
    }

    return returnULong;
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::restoreGuts(RWvistream& istrm)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        
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
    return (new CtiLMEnergyExchangeOfferRevision(*this));
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision& CtiLMEnergyExchangeOfferRevision::operator=(const CtiLMEnergyExchangeOfferRevision& right)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( this != &right )
    {
        _offerid = right._offerid;
        _revisionnumber = right._revisionnumber;
        _actiondatetime = right._actiondatetime;
        _notificationdatetime = right._notificationdatetime;
        _offerexpirationdatetime = right._offerexpirationdatetime;
        _additionalinfo = right._additionalinfo;

        _lmenergyexchangehourlyoffers.clearAndDestroy();
        for(UINT i=0;i<right._lmenergyexchangehourlyoffers.entries();i++)
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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return ( (getOfferId() == right.getOfferId()) && (getRevisionNumber() == right.getRevisionNumber()) );
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeOfferRevision::operator!=(const CtiLMEnergyExchangeOfferRevision& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::restore(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBConnection conn = getConnection();
    {
        RWLockGuard<RWDBConnection> conn_guard(conn);

        if ( conn.isValid() )
        {
            if( _LM_DEBUG )
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
    updateLMEnergyExchangeOfferRevisionTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::updateLMEnergyExchangeOfferRevisionTable()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBConnection conn = getConnection();
    {
        RWLockGuard<RWDBConnection> conn_guard(conn);

        if ( conn.isValid() )
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

/*---------------------------------------------------------------------------
    dumpDynamicData
    
    Writes out the dynamic information for this customer.
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::dumpDynamicData()
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    updateLMEnergyExchangeOfferRevisionTable();
}

/*---------------------------------------------------------------------------
    restoreDynamicData
    
    Restores self's dynamic data given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOfferRevision::restoreDynamicData(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWDBConnection conn = getConnection();
    {
        RWLockGuard<RWDBConnection> conn_guard(conn);

        if ( conn.isValid() )
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

            /*if( _LM_DEBUG )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << RWTime() << " - " << selector.asString().data() << endl;
            }*/

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

