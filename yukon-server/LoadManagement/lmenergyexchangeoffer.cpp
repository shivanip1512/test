#include "precompiled.h"

#include "dbaccess.h"
#include "lmid.h"
#include "logger.h"
#include "lmenergyexchangeoffer.h"
#include "utility.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"

using std::vector;
using std::string;
using std::endl;

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMEnergyExchangeOffer, CTILMENERGYEXCHANGEOFFER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMEnergyExchangeOffer::CtiLMEnergyExchangeOffer() :
_paoid(0),
_offerid(0)
{
}

CtiLMEnergyExchangeOffer::CtiLMEnergyExchangeOffer(Cti::RowReader &rdr)
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

const vector<CtiLMEnergyExchangeOfferRevision*>& CtiLMEnergyExchangeOffer::getLMEnergyExchangeOfferRevisions() const
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
    //need to get a new offer id from the database
    static const string sql =  "SELECT XPO.offerid "
                               "FROM lmenergyexchangeprogramoffer XPO "
                               "ORDER BY XPO.offerid DESC";

    Cti::Database::DatabaseConnection conn;
    Cti::Database::DatabaseReader rdr(conn);

    rdr.setCommandText(sql);
    rdr.execute();

    if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
    {
        CTILOG_DEBUG(dout, rdr.asString());
    }

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

    CTILOG_INFO(dout, "Inserting a program energy exchange entry into LMEnergyExchangeProgramOffer with offer id: " << getOfferId());

    {
        static const std::string sql_insert = "insert into lmenergyexchangeprogramoffer values (?, ?, ?, ?)";

        Cti::Database::DatabaseConnection   conn;
        Cti::Database::DatabaseWriter       inserter(conn, sql_insert);

        inserter
            << getPAOId()
            << getOfferId()
            << getRunStatus()
            << getOfferDate();

        if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
        {
            CTILOG_DEBUG(dout, inserter.asString());
        }

        inserter.execute();
    }
}

/*---------------------------------------------------------------------------
    updateLMEnergyExchangeProgramOfferTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::updateLMEnergyExchangeProgramOfferTable(CtiTime& currentDateTime)
{
    static const std::string sql_update = "update lmenergyexchangeprogramoffer"
                                           " set "
                                                "runstatus = ?, "
                                                "offerdate = ?"
                                           " where "
                                                "deviceid = ? and "
                                                "offerid = ?";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseWriter       updater(connection, sql_update);

    updater
        << getRunStatus()[0]
        << getOfferDate()
        << getPAOId()
        << getOfferId();

    if( ! Cti::Database::executeUpdater( updater, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) ))
    {
        // If update failed, we should try to insert the record because it means that there probably wasn't a entry for this object yet

        static const string sql =  "SELECT XPO.offerid "
                                   "FROM lmenergyexchangeprogramoffer XPO "
                                   "ORDER BY XPO.offerid DESC";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection);

        rdr.setCommandText(sql);
        rdr.execute();

        if( _LM_DEBUG & LM_DEBUG_DYNAMIC_DB )
        {
            CTILOG_DEBUG(dout, rdr.asString());
        }

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

        CTILOG_INFO(dout, "Inserting a program energy exchange entry into LMEnergyExchangeProgramOffer: with offer id: " << getOfferId());

        {
            static const std::string sql_insert = "insert into lmenergyexchangeprogramoffer values (?, ?, ?, ?)";

            Cti::Database::DatabaseConnection   connection;
            Cti::Database::DatabaseWriter       inserter(connection, sql_insert);

            inserter
                << getPAOId()
                << getOfferId()
                << getRunStatus()
                << getOfferDate();

            Cti::Database::executeCommand( inserter, CALLSITE, Cti::Database::LogDebug(_LM_DEBUG & LM_DEBUG_DYNAMIC_DB) );
        }
    }
}

/*---------------------------------------------------------------------------
    deleteLMEnergyExchangeProgramOfferTable

    .
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::deleteLMEnergyExchangeProgramOfferTable()
{
    static const std::string sql = "delete from lmenergyexchangeprogramoffer where deviceid = ? and offerid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter
        << getPAOId()
        << getOfferId();

    deleter.execute();
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

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::restore(Cti::RowReader &rdr)
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
    dumpDynamicData(CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this energy exchange offer.
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::dumpDynamicData(CtiTime& currentDateTime)
{
    updateLMEnergyExchangeProgramOfferTable(currentDateTime);
}

/*---------------------------------------------------------------------------
    restoreDynamicData

    Restores self's dynamic data given a Reader
---------------------------------------------------------------------------*/
void CtiLMEnergyExchangeOffer::restoreDynamicData(Cti::RowReader &rdr)
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

