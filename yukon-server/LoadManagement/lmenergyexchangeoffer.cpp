/*---------------------------------------------------------------------------
        Filename:  lmenergyexchangeoffer.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiLMEnergyExchangeOffer.
                        CtiLMEnergyExchangeOffer maintains the state and handles
                        the persistence of programs for Load Management.

        Initial Date:  5/7/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "lmid.h"
#include "logger.h"
#include "lmenergyexchangeoffer.h"
#include "rwutil.h"
#include "utility.h"

using std::vector;

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMEnergyExchangeOffer, CTILMENERGYEXCHANGEOFFER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOffer::CtiLMEnergyExchangeOffer()
{
}

CtiLMEnergyExchangeOffer::CtiLMEnergyExchangeOffer(RWDBReader& rdr)
{
    restore(rdr);
}

CtiLMEnergyExchangeOffer::CtiLMEnergyExchangeOffer(const CtiLMEnergyExchangeOffer& energyexchangeoffer)
{
    operator=(energyexchangeoffer);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOffer::~CtiLMEnergyExchangeOffer()
{

    delete_container(_lmenergyexchangeofferrevisions);
    _lmenergyexchangeofferrevisions.clear();
}

/*---------------------------------------------------------------------------
    getPAOId

    Returns the pao id of the energy exchange program associated with the
    offer.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeOffer::getPAOId() const
{

    return _paoid;
}

/*---------------------------------------------------------------------------
    getOfferId

    Returns the reference id of the energy exchange offer.
---------------------------------------------------------------------------*/
LONG CtiLMEnergyExchangeOffer::getOfferId() const
{

    return _offerid;
}

/*---------------------------------------------------------------------------
    getRunStatus

    Returns the run status of the energy exchange offer.
---------------------------------------------------------------------------*/
const string& CtiLMEnergyExchangeOffer::getRunStatus() const
{

    return _runstatus;
}

/*---------------------------------------------------------------------------
    getOfferDate

    Returns the date of the energy exchange offer.
---------------------------------------------------------------------------*/
const CtiTime& CtiLMEnergyExchangeOffer::getOfferDate() const
{

    return _offerdate;
}

/*---------------------------------------------------------------------------
    getLMEnergyExchangeOfferRevisions

    Returns a list of offer revisions for this energy exchange offer.
---------------------------------------------------------------------------*/
vector<CtiLMEnergyExchangeOfferRevision*>& CtiLMEnergyExchangeOffer::getLMEnergyExchangeOfferRevisions()
{

    return _lmenergyexchangeofferrevisions;
}

/*---------------------------------------------------------------------------
    setPAOId

    Sets the pao id of the energy exchange program associated with the
    offer.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOffer& CtiLMEnergyExchangeOffer::setPAOId(LONG devid)
{

    _paoid = devid;

    return *this;
}

/*---------------------------------------------------------------------------
    setOfferId

    Sets the reference id of the energy exchange offer.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOffer& CtiLMEnergyExchangeOffer::setOfferId(LONG offid)
{

    _offerid = offid;

    return *this;
}

/*---------------------------------------------------------------------------
    setRunStatus

    Sets the run status of the energy exchange offer.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOffer& CtiLMEnergyExchangeOffer::setRunStatus(const string& runstat)
{

    _runstatus = runstat;

    return *this;
}

/*---------------------------------------------------------------------------
    setOfferDate

    Sets the date of the energy exchange offer.
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOffer& CtiLMEnergyExchangeOffer::setOfferDate(const CtiTime& offdate)
{

    _offerdate = offdate;

    return *this;
}


/*---------------------------------------------------------------------------
    getCurrentOfferRevision

    Returns .
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOfferRevision* CtiLMEnergyExchangeOffer::getCurrentOfferRevision()
{


    CtiLMEnergyExchangeOfferRevision* currentOfferRevision = NULL;

    if( _lmenergyexchangeofferrevisions.size() > 0 )
    {
        currentOfferRevision = (CtiLMEnergyExchangeOfferRevision*)_lmenergyexchangeofferrevisions[_lmenergyexchangeofferrevisions.size()-1];
    }

    return currentOfferRevision;
}

/*---------------------------------------------------------------------------
    addLMEnergyExchangeProgramOfferTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::addLMEnergyExchangeProgramOfferTable()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmEnergyExchangeProgramOfferTable = db.table("lmenergyexchangeprogramoffer");

            {//need to get a new offer id from the database
                RWDBSelector selector = db.selector();
                selector << lmEnergyExchangeProgramOfferTable["offerid"];

                selector.from(lmEnergyExchangeProgramOfferTable);

                selector.orderByDescending(lmEnergyExchangeProgramOfferTable["offerid"]);

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << selector.asString().data() << endl;
                }

                RWDBReader rdr = selector.reader(conn);

                if( rdr() )
                {
                    LONG tempLONG = 0;
                    rdr["offerid"] >> tempLONG;
                    setOfferId(tempLONG+1);
                }
                else
                {
                    setOfferId(1);
                }

                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Inserting a program energy exchange entry into LMEnergyExchangeProgramOffer with offer id: " << getOfferId() << endl;
                }
            }

            {
                RWDBInserter inserter = lmEnergyExchangeProgramOfferTable.inserter();

                inserter << getPAOId()
                << getOfferId()
                << string(getRunStatus())
                << getOfferDate();

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << inserter.asString().data() << endl;
                }

                inserter.execute( conn );
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    updateLMEnergyExchangeProgramOfferTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::updateLMEnergyExchangeProgramOfferTable(RWDBConnection& conn, CtiTime& currentDateTime)
{
    {

        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmEnergyExchangeProgramOfferTable = db.table("lmenergyexchangeprogramoffer");
            RWDBUpdater updater = lmEnergyExchangeProgramOfferTable.updater();

            updater << lmEnergyExchangeProgramOfferTable["runstatus"].assign(getRunStatus()[0])
            << lmEnergyExchangeProgramOfferTable["offerdate"].assign(toRWDBDT(getOfferDate()));

            updater.where(lmEnergyExchangeProgramOfferTable["deviceid"]==getPAOId() &&//will be paobjectid
                          lmEnergyExchangeProgramOfferTable["offerid"]==getOfferId());

            if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << updater.asString().data() << endl;
            }

            updater.execute( conn );

            if( updater.status().errorCode() != RWDBStatus::ok )
            {// If update failed, we should try to insert the record because it means that there probably wasn't a entry for this object yet
                RWDBSelector selector = db.selector();
                selector << lmEnergyExchangeProgramOfferTable["offerid"];

                selector.from(lmEnergyExchangeProgramOfferTable);

                selector.orderByDescending(lmEnergyExchangeProgramOfferTable["offerid"]);

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << selector.asString().data() << endl;
                }

                RWDBReader rdr = selector.reader(conn);

                if( rdr() )
                {
                    LONG tempLONG = 0;
                    rdr["offerid"] >> tempLONG;
                    setOfferId(tempLONG+1);
                }
                else
                {
                    setOfferId(1);
                }

                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - Inserting a program energy exchange entry into LMEnergyExchangeProgramOffer: with offer id: " << getOfferId() << endl;
                }

                RWDBInserter inserter = lmEnergyExchangeProgramOfferTable.inserter();

                inserter << getPAOId()
                << getOfferId()
                << getRunStatus()
                << getOfferDate();

                if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << inserter.asString().data() << endl;
                }

                inserter.execute( conn );
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*---------------------------------------------------------------------------
    deleteLMEnergyExchangeProgramOfferTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::deleteLMEnergyExchangeProgramOfferTable()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    {

        if( conn.isValid() )
        {
            RWDBDatabase db = getDatabase();
            RWDBTable lmEnergyExchangeProgramOfferTable = db.table("lmenergyexchangeprogramoffer");
            RWDBDeleter deleter = lmEnergyExchangeProgramOfferTable.deleter();

            deleter.where(lmEnergyExchangeProgramOfferTable["deviceid"]==getPAOId() &&//will be paobjectid
                          lmEnergyExchangeProgramOfferTable["offerid"]==getOfferId());

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << deleter.asString().c_str() << endl;
            }*/

            deleter.execute( conn );
        }
        else
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Invalid DB Connection in: " << __FILE__ << " at: " << __LINE__ << endl;
        }
    }
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::restoreGuts(RWvistream& istrm)
{



    RWCollectable::restoreGuts( istrm );

    CtiTime tempTime;

    istrm >> _paoid
    >> _offerid
    >> _runstatus
    >> tempTime
    >> _lmenergyexchangeofferrevisions;

    _offerdate = CtiTime(tempTime);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::saveGuts(RWvostream& ostrm ) const
{



    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
    << _offerid
    << _runstatus
    << _offerdate
    << _lmenergyexchangeofferrevisions;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOffer& CtiLMEnergyExchangeOffer::operator=(const CtiLMEnergyExchangeOffer& right)
{


    if( this != &right )
    {
        _paoid = right._paoid;
        _offerid = right._offerid;
        _runstatus = right._runstatus;
        _offerdate = right._offerdate;

        delete_container(_lmenergyexchangeofferrevisions);
        _lmenergyexchangeofferrevisions.clear();
        for(LONG i=0;i<right._lmenergyexchangeofferrevisions.size();i++)
        {
            _lmenergyexchangeofferrevisions.push_back(((CtiLMEnergyExchangeOfferRevision*)right._lmenergyexchangeofferrevisions[i])->replicate());
        }
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeOffer::operator==(const CtiLMEnergyExchangeOffer& right) const
{

    return( (getPAOId() == right.getPAOId()) && (getOfferId() == right.getOfferId()) );
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMEnergyExchangeOffer::operator!=(const CtiLMEnergyExchangeOffer& right) const
{

    return !(operator==(right));
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOffer* CtiLMEnergyExchangeOffer::replicate() const
{
    return(CTIDBG_new CtiLMEnergyExchangeOffer(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::restore(RWDBReader& rdr)
{


    rdr["deviceid"] >> _paoid;//will be paobjectid
    rdr["offerid"] >> _offerid;
    rdr["runstatus"] >> _runstatus;
    rdr["offerdate"] >> _offerdate;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information.
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this energy exchange offer.
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime)
{
    updateLMEnergyExchangeProgramOfferTable(conn, currentDateTime);
}

/*---------------------------------------------------------------------------
    restoreDynamicData

    Restores self's dynamic data given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::restoreDynamicData(RWDBReader& rdr)
{
}

// Static Members

// Possible run statuses
const string CtiLMEnergyExchangeOffer::NullRunStatus = "Null";
const string CtiLMEnergyExchangeOffer::ScheduledRunStatus = "Scheduled";
const string CtiLMEnergyExchangeOffer::OpenRunStatus = "Open";
const string CtiLMEnergyExchangeOffer::ClosingRunStatus = "Closing";
const string CtiLMEnergyExchangeOffer::CurtailmentPendingRunStatus = "CurtailmentPending";
const string CtiLMEnergyExchangeOffer::CurtailmentActiveRunStatus = "CurtailmentActive";
const string CtiLMEnergyExchangeOffer::CompletedRunStatus = "Completed";
const string CtiLMEnergyExchangeOffer::CanceledRunStatus = "Canceled";
//const string CtiLMEnergyExchangeOffer::RunStatus = "";

