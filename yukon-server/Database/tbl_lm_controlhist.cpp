/*-----------------------------------------------------------------------------*
*
* File:   tbl_lm_controlhist
*
* Date:   9/24/2001
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_lm_controlhist.cpp-arc  $
* REVISION     :  $Revision: 1.29 $
* DATE         :  $Date: 2005/05/24 00:40:04 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_lm_controlhist.h"
#include "logger.h"
#include "numstr.h"
#include "utility.h"

//CtiTableLMControlHistory::CtiTableLMControlHistory() {}

CtiTableLMControlHistory::CtiTableLMControlHistory(LONG paoid, const RWTime& start, LONG soe,
                                                   INT dur, const RWCString& type, LONG daily,
                                                   LONG month, LONG season, LONG annual,
                                                   const RWCString& restore, DOUBLE reduce,
                                                   LONG lmchid) :
_lmControlHistID(lmchid), _paoID(paoid), _startDateTime(start), _soeTag(soe),
_controlDuration(dur), _controlType(type), _currentDailyTime(daily), _currentMonthlyTime(month),
_currentSeasonalTime(season), _currentAnnualTime(annual), _defaultActiveRestore(restore),
_reductionValue(reduce), _reductionRatio(100), _prevLogTime(start), _prevStopReportTime(start),
_isNewControl(true), _loadedActiveRestore(LMAR_NEWCONTROL), _activeRestore("U")
{
}

CtiTableLMControlHistory::CtiTableLMControlHistory(const CtiTableLMControlHistory& aRef)
{
    this->operator=(aRef);
}

CtiTableLMControlHistory::~CtiTableLMControlHistory()
{
}

bool CtiTableLMControlHistory::operator<(const CtiTableLMControlHistory& aRef) const
{
    return(getLMControlHistoryID() < aRef.getLMControlHistoryID());
}

CtiTableLMControlHistory& CtiTableLMControlHistory::operator=(const CtiTableLMControlHistory& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        _lmControlHistID     = aRef.getLMControlHistoryID();
        _paoID               = aRef.getPAOID();
        _startDateTime       = aRef.getStartTime();
        _stopDateTime        = aRef.getStopTime();
        _controlCompleteTime = aRef.getControlCompleteTime();
        _soeTag              = aRef.getSoeTag();
        _controlDuration     = aRef.getControlDuration();
        _controlType         = aRef.getControlType();
        _currentDailyTime    = aRef.getCurrentDailyTime();
        _currentMonthlyTime  = aRef.getCurrentMonthlyTime();
        _currentSeasonalTime = aRef.getCurrentSeasonalTime();
        _currentAnnualTime   = aRef.getCurrentAnnualTime();
        _activeRestore       = aRef.getActiveRestore();
        _reductionValue      = aRef.getReductionValue();

        _defaultActiveRestore= aRef.getDefaultActiveRestore();
        _prevLogTime         = aRef.getPreviousLogTime();
        _prevStopReportTime  = aRef.getPreviousStopReportTime();
        _reductionRatio      = aRef.getReductionRatio();

        _isNewControl        = aRef._isNewControl;
    }
    return *this;
}

LONG CtiTableLMControlHistory::getLMControlHistoryID() const
{

    return _lmControlHistID;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setLMControlHistoryID( const LONG contHist )
{

    setDirty();
    _lmControlHistID = contHist;
    return *this;
}

LONG CtiTableLMControlHistory::getPAOID() const
{
    return _paoID;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setPAOID( const LONG pao )
{
    setDirty();
    _paoID = pao;
    return *this;
}

const RWTime& CtiTableLMControlHistory::getControlCompleteTime() const
{
    return _controlCompleteTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setControlCompleteTime( const RWTime& cst )
{
    _controlCompleteTime = cst;
    return *this;
}

const RWTime& CtiTableLMControlHistory::getStartTime() const
{
    return _startDateTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setStartTime( const RWTime& start )
{
    setDirty();
    _startDateTime = start;
    return *this;
}

const RWTime& CtiTableLMControlHistory::getStopTime() const
{
    return _stopDateTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setStopTime( const RWTime& stop )
{
    setDirty();
    _stopDateTime = stop;
    return *this;
}

const RWTime& CtiTableLMControlHistory::getPreviousLogTime() const
{
    return _prevLogTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setPreviousLogTime( const RWTime& start )
{
    _prevLogTime = start;
    return *this;
}

const RWTime& CtiTableLMControlHistory::getPreviousStopReportTime() const
{
    return _prevStopReportTime;
}
CtiTableLMControlHistory& CtiTableLMControlHistory::setPreviousStopReportTime( const RWTime& start )
{
    _prevStopReportTime = start;
    return *this;
}

LONG CtiTableLMControlHistory::getSoeTag() const
{
    return _soeTag;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setSoeTag( const LONG soet )
{
    setDirty();
    _soeTag = soet;
    return *this;
}

INT CtiTableLMControlHistory::getControlDuration() const
{
    return _controlDuration;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setControlDuration( const INT cd )
{
    setDirty();
    _controlDuration = cd;
    return *this;
}

const RWCString& CtiTableLMControlHistory::getControlType() const
{

    return _controlType;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setControlType( const RWCString& ct )
{
    setDirty();
    _controlType = ct;
    return *this;
}

LONG CtiTableLMControlHistory::getCurrentDailyTime() const
{
    return _currentDailyTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setCurrentDailyTime( const LONG dt )
{
    setDirty();
    _currentDailyTime = dt;
    return *this;
}

LONG CtiTableLMControlHistory::getCurrentSeasonalTime() const
{
    return _currentSeasonalTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setCurrentSeasonalTime( const LONG st )
{
    setDirty();
    _currentSeasonalTime = st;
    return *this;
}

LONG CtiTableLMControlHistory::getCurrentMonthlyTime() const
{
    return _currentMonthlyTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setCurrentMonthlyTime( const LONG mt )
{
    setDirty();
    _currentMonthlyTime = mt;
    return *this;
}

LONG CtiTableLMControlHistory::getCurrentAnnualTime() const
{

    return _currentAnnualTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setCurrentAnnualTime( const LONG at )
{
    setDirty();
    _currentAnnualTime = at;
    return *this;
}

const RWCString& CtiTableLMControlHistory::getActiveRestore() const
{
    if(_isNewControl && _activeRestore == RWCString(LMAR_LOGTIMER) )
    {
        _activeRestore = RWCString( LMAR_NEWCONTROL );
    }

    return _activeRestore;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setActiveRestore( const RWCString& ar )
{
    setDirty();
    _activeRestore = ar;
    return *this;
}

const RWCString& CtiTableLMControlHistory::getDefaultActiveRestore() const
{
    return _defaultActiveRestore;
}

const RWCString& CtiTableLMControlHistory::getLoadedActiveRestore() const
{
    return _loadedActiveRestore;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setDefaultActiveRestore( const RWCString& ar )
{
    _defaultActiveRestore = ar;
    return *this;
}

DOUBLE CtiTableLMControlHistory::getReductionValue() const
{
    return _reductionValue;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setReductionValue( const DOUBLE rv )
{
    setDirty();
    _reductionValue = rv;
    return *this;
}

RWCString CtiTableLMControlHistory::getTableName()
{
    return "LMControlHistory";
}

RWCString CtiTableLMControlHistory::getDynamicTableName()
{
    return "DynamicLMControlHistory";
}

void CtiTableLMControlHistory::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table("YukonPAObject");                  //yes or no?
    RWDBTable devTbl = db.table(getTableName());

    selector <<
    keyTable["paobjectid"] <<
    devTbl["lmctrlhistid"] <<
    devTbl["startdatetime"] <<
    devTbl["stopdatetime"] <<
    devTbl["soe_tag"] <<
    devTbl["controlduration"] <<
    devTbl["controltype"] <<
    devTbl["currentdailytime"] <<
    devTbl["currentmonthlytime"] <<
    devTbl["currentseasonaltime"] <<
    devTbl["currentannualtime"] <<
    devTbl["activerestore"] <<
    devTbl["reductionvalue"];

    selector.from(keyTable);
    selector.from(devTbl);

    selector.where( selector.where() && keyTable["paobjectid"] == devTbl["paobjectid"] );
}

void CtiTableLMControlHistory::getDynamicSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table("YukonPAObject");                  //yes or no?
    RWDBTable devTbl = db.table(getDynamicTableName());

    selector <<
    keyTable["paobjectid"] <<
    devTbl["lmctrlhistid"] <<
    devTbl["startdatetime"] <<
    devTbl["stopdatetime"] <<
    devTbl["soe_tag"] <<
    devTbl["controlduration"] <<
    devTbl["controltype"] <<
    devTbl["currentdailytime"] <<
    devTbl["currentmonthlytime"] <<
    devTbl["currentseasonaltime"] <<
    devTbl["currentannualtime"] <<
    devTbl["activerestore"] <<
    devTbl["reductionvalue"];

    selector.from(keyTable);
    selector.from(devTbl);

    selector.where( selector.where() && keyTable["paobjectid"] == devTbl["paobjectid"] );
}

void CtiTableLMControlHistory::getSQLForOutstandingControls(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table(getDynamicTableName());

    selector <<
    keyTable["paobjectid"] <<
    keyTable["lmctrlhistid"] <<
    keyTable["startdatetime"] <<
    keyTable["stopdatetime"] <<
    keyTable["soe_tag"] <<
    keyTable["controlduration"] <<
    keyTable["controltype"] <<
    keyTable["currentdailytime"] <<
    keyTable["currentmonthlytime"] <<
    keyTable["currentseasonaltime"] <<
    keyTable["currentannualtime"] <<
    keyTable["activerestore"] <<
    keyTable["reductionvalue"];

    selector.from(keyTable);

    selector.where( selector.where() && keyTable["activerestore"] == LMAR_DISPATCH_SHUTDOWN );
}

RWDBStatus CtiTableLMControlHistory::deleteOutstandingControls()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getDynamicTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["activerestore"] == LMAR_DISPATCH_SHUTDOWN );
    deleter.execute( conn );
    return deleter.status();
}

/*
 *  This method will update any outstanding controls which have completed prior to dispatch having restarted.
 */
RWDBStatus CtiTableLMControlHistory::updateCompletedOutstandingControls()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getDynamicTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["activerestore"] == LMAR_DISPATCH_SHUTDOWN && table["stopdatetime"] <= RWDBDateTime() );
    updater << table["activerestore"].assign( LMAR_DISPATCH_MISSED_COMPLETION );


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << updater.asString() << endl;
    }

    if( updater.execute( conn ).status().errorCode() != RWDBStatus::ok)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** CtiTableLMControlHistory::updateCompletedOutstandingControls update not ok **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return updater.status();
}




/*
select * from lmcontrolhistory where activerestore='N' and
soe_tag not in (select soe_tag from lmcontrolhistory where activerestore='M' or activerestore='T')
*/

void CtiTableLMControlHistory::DecodeDatabaseReader(RWDBReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["paobjectid"]          >> _paoID;
    rdr["lmctrlhistid"]        >> _lmControlHistID;
    rdr["startdatetime"]       >> _startDateTime;
    rdr["stopdatetime"]        >> _stopDateTime;
    rdr["soe_tag"]             >> _soeTag;
    rdr["controlduration"]     >> _controlDuration;
    rdr["controltype"]         >> _controlType;

    DecodeControlTimes(rdr);

    rdr["activerestore"]       >> _defaultActiveRestore;
    rdr["reductionvalue"]      >> _reductionValue;

    setUpdatedFlag();
}

void CtiTableLMControlHistory::DecodeControlTimes(RWDBReader &rdr)
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & DEBUGLEVEL_DATABASE) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["stopdatetime"]        >> _prevLogTime;
    rdr["currentdailytime"]    >> _currentDailyTime;
    rdr["currentmonthlytime"]  >> _currentMonthlyTime;
    rdr["currentseasonaltime"] >> _currentSeasonalTime;
    rdr["currentannualtime"]   >> _currentAnnualTime;
    rdr["activerestore"]       >> _loadedActiveRestore;

    _prevStopReportTime = _prevLogTime;

    setUpdatedFlag();
}

RWDBStatus CtiTableLMControlHistory::RestoreControlTimes()
{
    long maxid = GetMaxLMControl( getPAOID() );

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["stopdatetime"] <<
    table["currentdailytime"] <<
    table["currentmonthlytime"] <<
    table["currentseasonaltime"] <<
    table["currentannualtime"] <<
    table["activerestore"];

    selector.where( table["paobjectid"] == getPAOID() && table["lmctrlhistid"] == maxid);

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeControlTimes( reader );
    }

    return reader.status();
}

RWDBStatus CtiTableLMControlHistory::Restore()
{
    long maxid = GetMaxLMControl(getPAOID());

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["paobjectid"] <<
    table["lmctrlhistid"] <<
    table["startdatetime"] <<
    table["stopdatetime"] <<
    table["soe_tag"] <<
    table["controlduration"] <<
    table["controltype"] <<
    table["currentdailytime"] <<
    table["currentmonthlytime"] <<
    table["currentseasonaltime"] <<
    table["currentannualtime"] <<
    table["activerestore"] <<
    table["reductionvalue"];

    selector.where( table["paobjectid"] == getPAOID() && table["lmctrlhistid"] == maxid);

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader );
        setDirty( false );
    }
    else
    {
        setDirty( true );
    }
    return reader.status();
}

RWDBStatus CtiTableLMControlHistory::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    return Insert(conn);
}

RWDBStatus CtiTableLMControlHistory::Insert(RWDBConnection &conn)
{
    RWDBStatus dbstat( RWDBStatus::ok );

    setLMControlHistoryID( LMControlHistoryIdGen() );

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getLMControlHistoryID() <<
    getPAOID() <<
    RWDBDateTime(getStartTime()) <<
    getSoeTag() <<
    (getStopTime().seconds() - getStartTime().seconds()) <<     // getControlDuration() <<
    (getControlType().isNull() ? "(none)" : getControlType()) <<
    getCurrentDailyTime() <<
    getCurrentMonthlyTime() <<
    getCurrentSeasonalTime() <<
    getCurrentAnnualTime() <<
    (getActiveRestore().isNull() ? "U" : getActiveRestore()) <<
    getReductionValue() <<
    RWDBDateTime(getStopTime());

    if(getStopTime().seconds() >= getStartTime().seconds())
    {
        if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
        {
            setDirty(false);
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Unable to insert LM Control History for PAO id " << getPAOID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "   " << inserter.asString() << endl;
        }

        dbstat = inserter.status();
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** LMControlHistory cannot record negative control times. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << inserter.asString() << endl;
            dump();
        }
    }

    return dbstat;
}


RWDBStatus CtiTableLMControlHistory::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["paobjectid"] == getPAOID() );

    updater <<
    table["paobjectid"].assign(getPAOID() ) <<
    table["lmctrlhistid"].assign(getLMControlHistoryID() ) <<
    table["startdatetime"].assign(getStartTime() ) <<
    table["stopdatetime"].assign(getStopTime() ) <<
    table["soe_tag"].assign( getSoeTag() ) <<
    table["controlduration"].assign( (getStopTime().seconds() - getStartTime().seconds()) ) <<
    table["controltype"].assign( getControlType() ) <<
    table["currentdailytime"].assign( getCurrentDailyTime() ) <<
    table["currentmonthlytime"].assign( getCurrentMonthlyTime() ) <<
    table["currentseasonaltime"].assign( getCurrentSeasonalTime() ) <<
    table["currentannualtime"].assign( getCurrentAnnualTime() ) <<
    table["activerestore"].assign( getActiveRestore() ) <<
    table["reductionvalue"].assign( getReductionValue() );

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableLMControlHistory::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["lmctrlhistid"] == getLMControlHistoryID() );
    deleter.execute( conn );
    return deleter.status();
}

CtiTableLMControlHistory& CtiTableLMControlHistory::incrementTimes(const RWTime &logTime, const LONG increment, bool season_reset )
{
    RWDate prevdate(getPreviousLogTime());
    RWDate today(logTime);

    bool newday = false;
    bool newmonth = false;
    bool newyear = false;
    bool newseason = season_reset;

    LONG partialIncrement = 0;              // This is the component of the log that should have been allocated into yesterday, and last month and last year.

    if(today.day() != prevdate.day())
    {
        if( isNewControl() )
        {
            // These controls did not continue into the next day.
            partialIncrement = 0;
            _loadedActiveRestore = "N";
        }
        else
        {
            // we must evaluate the day crossing.
            RWTime lastTimePrevious(prevdate, 23,59,59);
            ULONG todaysSeconds = logTime.seconds() - lastTimePrevious.seconds() - 1;
            partialIncrement = lastTimePrevious.seconds() - getPreviousLogTime().seconds();

            if(partialIncrement <= increment )    // Be cautious since the last log may be many days ago.. We only want continuous controls to be recorded.
            {
                LONG offtime = ( (double)getReductionRatio() * (double)(partialIncrement) / 100.0 );

                CtiTableLMControlHistory closeLog(*this);       // make a copy
                closeLog.setActiveRestore(LMAR_PERIOD_TRANSITION);
                closeLog.setCurrentDailyTime   ( closeLog.getCurrentDailyTime()    + offtime );
                closeLog.setCurrentMonthlyTime ( closeLog.getCurrentMonthlyTime()  + offtime );
                closeLog.setCurrentSeasonalTime( closeLog.getCurrentSeasonalTime() + offtime );
                closeLog.setCurrentAnnualTime  ( closeLog.getCurrentAnnualTime()   + offtime );
                closeLog.setStopTime( lastTimePrevious );
                closeLog.Insert();
            }
        }

        newday = true;
    }
    if(today.month() != prevdate.month())
    {
        newmonth = true;
    }
    if(today.year() != prevdate.year())
    {
        newyear = true;
    }

    LONG newincrement = ( (double)getReductionRatio() * (double)(increment - partialIncrement) / 100.0 );

    setCurrentDailyTime   ( newday      ? newincrement : getCurrentDailyTime()    + newincrement );
    setCurrentMonthlyTime ( newmonth    ? newincrement : getCurrentMonthlyTime()  + newincrement );
    setCurrentSeasonalTime( newseason   ? newincrement : getCurrentSeasonalTime() + newincrement );
    setCurrentAnnualTime  ( newyear     ? newincrement : getCurrentAnnualTime()   + newincrement );

    setStopTime( logTime );

    return *this;
}

CtiMutex CtiTableLMControlHistory::_soeMux;

LONG CtiTableLMControlHistory::getNextSOE()
{
    static LONG nextsoe = 0;
    CtiLockGuard< CtiMutex > gd(_soeMux);

    static BOOL init_id = FALSE;
    static LONG id = 0;
    static const CHAR sql[] = "SELECT MAX(SOE_TAG) FROM LMCONTROLHISTORY";

    if(gd.isAcquired())
    {
        if(!init_id)
        {   // Make sure all objects that that store results
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            // are out of scope when the release is called
            RWDBReader  rdr = ExecuteQuery( conn, sql );

            if(rdr() && rdr.isValid())
            {
                rdr >> nextsoe;
            }
            else
            {
                RWMutexLock::LockGuard  guard(coutMux);
                cout << "**** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            init_id = TRUE;
        }   // Temporary results are destroyed to free the connection

        ++nextsoe;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "Unable to acquire mutex for LMControlHistoryGen" << endl;
        }
    }

    return nextsoe;
}

int CtiTableLMControlHistory::getReductionRatio() const
{
    return _reductionRatio;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setReductionRatio( int redrat )
{
    _reductionRatio = redrat;
    return *this;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setNotNewControl()
{
    _isNewControl = false;
    return *this;
}
bool CtiTableLMControlHistory::isNewControl() const
{
    return _isNewControl;
}

void CtiTableLMControlHistory::dump() const
{

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " paobjectid            " << getPAOID() << endl;
        dout << " lmctrlhistid          " << getLMControlHistoryID() << endl;
        dout << " startdatetime         " << getStartTime() << endl;
        dout << " stopdatetime          " << getStopTime() << endl;
        dout << " controlcompletetime   " << getControlCompleteTime() << endl;
        dout << " previouslogtime       " << getPreviousLogTime() << endl;
        dout << " soe_tag               " << getSoeTag() << endl;
        dout << " controlduration       " << (INT)(getControlCompleteTime().seconds() - getStartTime().seconds()) << endl;
        dout << " controltype           " << getControlType() << endl;
        dout << " currentdailytime      " << getCurrentDailyTime() << endl;
        dout << " currentmonthlytime    " << getCurrentMonthlyTime() << endl;
        dout << " currentseasonaltime   " << getCurrentSeasonalTime() << endl;
        dout << " currentannualtime     " << getCurrentAnnualTime() << endl;
        dout << " activerestore         " << getActiveRestore() << endl;
        dout << " reductionvalue        " << getReductionValue() << endl;
    }

    return;
}

void CtiTableLMControlHistory::DecodeOutstandingControls(RWDBReader &rdr)
{
    RWTime now;

    setUpdatedFlag();
    _isNewControl        = false;

    rdr["paobjectid"]           >> _paoID;
    rdr["lmctrlhistid"]         >> _lmControlHistID;
    rdr["startdatetime"]        >> _startDateTime;
    rdr["stopdatetime"]         >> _stopDateTime;
    rdr["soe_tag"]              >> _soeTag;
    rdr["controlduration"]      >> _controlDuration;
    rdr["controltype"]          >> _controlType;
    rdr["currentdailytime"]     >> _currentDailyTime;
    rdr["currentmonthlytime"]   >> _currentMonthlyTime;
    rdr["currentseasonaltime"]  >> _currentSeasonalTime;
    rdr["currentannualtime"]    >> _currentAnnualTime;
    rdr["activerestore"]        >> _loadedActiveRestore;
    rdr["reductionvalue"]       >> _reductionValue;

    if(_loadedActiveRestore == LMAR_DISPATCH_SHUTDOWN && now < _stopDateTime)
    {
        ULONG secToStop = _stopDateTime.seconds() - now.seconds();  // This is the number of mis allocated seconds...

        _currentDailyTime -= secToStop;
        _currentMonthlyTime -= secToStop;
        _currentSeasonalTime -= secToStop;
        _currentAnnualTime -= secToStop;
    }

    _controlCompleteTime    = _stopDateTime;
    _activeRestore          = RWCString(LMAR_LOGTIMER);
    _defaultActiveRestore   = RWCString(LMAR_TIMED_RESTORE);        // Assume this is a timed in control since it had a stop time in the log???
    _prevLogTime            = _stopDateTime < now ? _stopDateTime : now;
    _prevStopReportTime     = _stopDateTime < now ? _stopDateTime : now;

    // dump();

    return;
}

void CtiTableLMControlHistory::getSQLForIncompleteControls(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBDateTime todaynow;
    RWDBDateTime thepast = todaynow.addDays( -5 );      // Go back this many days?

    keyTable = db.table(getTableName());

    selector <<
    keyTable["paobjectid"] <<
    keyTable["lmctrlhistid"] <<
    keyTable["startdatetime"] <<
    keyTable["stopdatetime"] <<
    keyTable["soe_tag"] <<
    keyTable["controlduration"] <<
    keyTable["controltype"] <<
    keyTable["currentdailytime"] <<
    keyTable["currentmonthlytime"] <<
    keyTable["currentseasonaltime"] <<
    keyTable["currentannualtime"] <<
    keyTable["activerestore"] <<
    keyTable["reductionvalue"];

    selector.from(keyTable);

    RWDBExpr stdtXpr("startdatetime", FALSE);
    RWDBExpr anXpr("soe_tag", FALSE);
    // activerestore='N' and soe_tag not in (select soe_tag from lmcontrolhistory where activerestore='M' or activerestore='T')

    selector.where( selector.where() &&
                    (keyTable["activerestore"] == LMAR_NEWCONTROL ||
                    keyTable["activerestore"] == LMAR_TIMED_RESTORE ||
                    keyTable["activerestore"] == LMAR_MANUAL_RESTORE ||
                    keyTable["activerestore"] == LMAR_CONT_CONTROL ||
                    keyTable["activerestore"] == LMAR_DISPATCH_SHUTDOWN) &&
                    keyTable["startdatetime"] > thepast);
}


RWDBStatus CtiTableLMControlHistory::UpdateDynamic()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    return UpdateDynamic(conn);
}

RWDBStatus CtiTableLMControlHistory::UpdateDynamic(RWDBConnection &conn)
{
    RWDBTable table = getDatabase().table( getDynamicTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["paobjectid"] == getPAOID() );

    updater <<
    table["paobjectid"].assign(getPAOID() ) <<
    table["lmctrlhistid"].assign(getLMControlHistoryID()) <<
    table["startdatetime"].assign(getStartTime() ) <<
    table["stopdatetime"].assign(getStopTime() ) <<
    table["soe_tag"].assign( getSoeTag() ) <<
    table["controlduration"].assign( (getStopTime().seconds() - getStartTime().seconds()) ) <<
    table["controltype"].assign( getControlType() ) <<
    table["currentdailytime"].assign( getCurrentDailyTime() ) <<
    table["currentmonthlytime"].assign( getCurrentMonthlyTime() ) <<
    table["currentseasonaltime"].assign( getCurrentSeasonalTime() ) <<
    table["currentannualtime"].assign( getCurrentAnnualTime() ) <<
    table["activerestore"].assign( getActiveRestore() ) <<
    table["reductionvalue"].assign( getReductionValue() );

    RWDBResult myResult = updater.execute( conn );
    RWDBStatus stat = myResult.status();
    RWDBStatus::ErrorCode ec = stat.errorCode();

    RWDBTable myTable = myResult.table();
    long rowsAffected = myResult.rowCount();

    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << updater.asString() << endl << endl;
    }

    if( ec != RWDBStatus::ok || rowsAffected <= 0)
    {
        stat = InsertDynamic(conn);        // Try a vanilla insert if the update failed!
    }

    return updater.status();
}


RWDBStatus CtiTableLMControlHistory::InsertDynamic(RWDBConnection &conn)
{
    RWDBStatus dbstat( RWDBStatus::ok );

    RWDBTable table = getDatabase().table( getDynamicTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getPAOID() <<
    getLMControlHistoryID() <<
    RWDBDateTime(getStartTime()) <<
    getSoeTag() <<
    (getStopTime().seconds() - getStartTime().seconds()) <<
    (getControlType().isNull() ? "(none)" : getControlType()) <<
    getCurrentDailyTime() <<
    getCurrentMonthlyTime() <<
    getCurrentSeasonalTime() <<
    getCurrentAnnualTime() <<
    getActiveRestore() <<
    getReductionValue() <<
    RWDBDateTime(getStopTime());

    if(getStopTime().seconds() >= getStartTime().seconds())
    {
        if( inserter.execute( conn ).status().errorCode() != RWDBStatus::ok)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Unable to insert Dynamic LM Control History for PAO id " << getPAOID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "   " << inserter.asString() << endl;
        }

        dbstat = inserter.status();
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** DynamicLMControlHistory cannot record negative control times. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << inserter.asString() << endl;
            dump();
        }
    }

    return dbstat;
}


