

#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:32 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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
_currentSeasonalTime(season), _currentAnnualTime(annual), _activeRestore(restore),
_reductionValue(reduce), _reductionRatio(100), _prevLogTime(start), _prevStopReportTime(start)
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
        _soeTag              = aRef.getSoeTag();
        _controlDuration     = aRef.getControlDuration();
        _controlType         = aRef.getControlType();
        _currentDailyTime    = aRef.getCurrentDailyTime();
        _currentMonthlyTime  = aRef.getCurrentMonthlyTime();
        _currentSeasonalTime = aRef.getCurrentSeasonalTime();
        _currentAnnualTime   = aRef.getCurrentAnnualTime();
        _activeRestore       = aRef.getActiveRestore();
        _reductionValue      = aRef.getReductionValue();

        _prevLogTime         = aRef.getPreviousLogTime();
        _prevStopReportTime  = aRef.getPreviousStopReportTime();
        _reductionRatio      = aRef.getReductionRatio();
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

const RWTime& CtiTableLMControlHistory::getStartTime() const
{
    return _startDateTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setStartTime( const RWTime& start )
{
    setDirty();
    _startDateTime = start;
    setPreviousLogTime(start);
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
    return _activeRestore;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setActiveRestore( const RWCString& ar )
{
    setDirty();
    _activeRestore = ar;
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

void CtiTableLMControlHistory::DecodeDatabaseReader(RWDBReader &rdr)
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["paobjectid"]          >> _paoID;
    rdr["lmctrlhistid"]        >> _lmControlHistID;
    rdr["startdatetime"]       >> _startDateTime;
    rdr["stopdatetime"]        >> _stopDateTime;
    rdr["soe_tag"]             >> _soeTag;
    rdr["controlduration"]     >> _controlDuration;
    rdr["controltype"]         >> _controlType;

    DecodeControlTimes(rdr);

    rdr["activerestore"]       >> _activeRestore;
    rdr["reductionvalue"]      >> _reductionValue;

    setUpdatedFlag();
}

void CtiTableLMControlHistory::DecodeControlTimes(RWDBReader &rdr)
{
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["stopdatetime"]        >> _prevLogTime;
    rdr["currentdailytime"]    >> _currentDailyTime;
    rdr["currentmonthlytime"]  >> _currentMonthlyTime;
    rdr["currentseasonaltime"] >> _currentSeasonalTime;
    rdr["currentannualtime"]   >> _currentAnnualTime;

    _prevStopReportTime = _prevLogTime;

    setUpdatedFlag();
}

RWDBStatus CtiTableLMControlHistory::RestoreControlTimes()
{
    long maxid = GetMaxLMControl( getPAOID() );

    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["stopdatetime"] <<
    table["currentdailytime"] <<
    table["currentmonthlytime"] <<
    table["currentseasonaltime"] <<
    table["currentannualtime"];

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

    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);

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

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << selector.asString() << endl;
    }
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
    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);

    return Insert(conn);
}

RWDBStatus CtiTableLMControlHistory::Insert(RWDBConnection &conn)
{
    setLMControlHistoryID( LMControlHistoryIdGen() );


    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getLMControlHistoryID() <<
    getPAOID() <<
    RWDBDateTime(getStartTime()) <<
    getSoeTag() <<
    getControlDuration() <<
    getControlType() <<
    getCurrentDailyTime() <<
    getCurrentMonthlyTime() <<
    getCurrentSeasonalTime() <<
    getCurrentAnnualTime() <<
    getActiveRestore() <<
    getReductionValue() <<
    RWDBDateTime(getStopTime());

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setPreviousLogTime( getStopTime() );
        setDirty(false);
    }
    else
    {
        LONG newcid = LMControlHistoryIdGen(true);

        if(newcid != getLMControlHistoryID())
        {
            RWTime Now;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << Now << " Insert collision occurred in table " << getTableName() << "." << endl;
                dout << Now << "   LMCtrlHistId has been re-initialized.  There may be two copies of dispatch inserting into this DB" << endl;
            }

            setLMControlHistoryID( newcid );

            if( inserter.execute( conn ).status().errorCode() != RWDBStatus::ok )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Unable to insert LM Control History for PAO id " << getPAOID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "   " << inserter.asString() << endl;
            }
            else
            {
                setPreviousLogTime( getStopTime() );
                setDirty(false);
            }
        }
    }
    return inserter.status();
}


RWDBStatus CtiTableLMControlHistory::Update()
{
    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["paobjectid"] == getPAOID() );

    updater <<
    table["paobjectid"].assign(getPAOID() ) <<
    table["lmctrlhistid"].assign(getLMControlHistoryID() ) <<
    table["startdatetime"].assign(getStartTime() ) <<
    table["stopdatetime"].assign(getStopTime() ) <<
    table["soe_tag"].assign( getSoeTag() ) <<
    table["controlduration"].assign( getControlDuration() ) <<
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
    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["lmctrlhistid"] == getLMControlHistoryID() );
    deleter.execute( conn );
    return deleter.status();
}

CtiTableLMControlHistory& CtiTableLMControlHistory::incrementTimes(const RWTime &now, const LONG increment )
{
    RWDate prevdate(getPreviousLogTime());
    RWDate today(now);

    bool newday = false;
    bool newmonth = false;
    bool newyear = false;
    bool newseason = false;

    if(today.day() != prevdate.day())       newday = true;
    if(today.month() != prevdate.month())   newmonth = true;
    if(today.year() != prevdate.year())     newyear = true;

    LONG newincrement = ( (double)getReductionRatio() * (double)increment / 100.0);

    setCurrentDailyTime( newday         ? newincrement : getCurrentDailyTime() + newincrement );
    setCurrentMonthlyTime( newmonth     ? newincrement : getCurrentMonthlyTime() + newincrement );
    setCurrentSeasonalTime( newseason   ? newincrement : getCurrentSeasonalTime() + newincrement );
    setCurrentAnnualTime( newyear       ? newincrement : getCurrentAnnualTime() + newincrement );

    setStopTime( now );

    return *this;
}


CtiMutex CtiTableLMControlHistory::_soeMux;
LONG CtiTableLMControlHistory::getNextSOE()
{
    static LONG nextsoe = 0;
    CtiLockGuard< CtiMutex > gd(_soeMux);
    return ++nextsoe;
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

