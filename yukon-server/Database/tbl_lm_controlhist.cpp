#include "precompiled.h"

#include "tbl_lm_controlhist.h"
#include "logger.h"
#include "numstr.h"
#include "utility.h"
#include "ctidate.h"
#include "database_reader.h"
#include "database_writer.h"

#include <iostream>
#include <sstream>

using namespace std;
using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;

//CtiTableLMControlHistory::CtiTableLMControlHistory() {}

CtiTableLMControlHistory::CtiTableLMControlHistory(LONG paoid, const CtiTime& start, LONG soe,
                                                   INT dur, const string& type, LONG daily,
                                                   LONG month, LONG season, LONG annual,
                                                   const string& restore, DOUBLE reduce,
                                                   LONG lmchid) :
_lmControlHistID(lmchid), _paoID(paoid), _startDateTime(start), _soeTag(soe),
_controlDuration(dur), _controlType(type), _currentDailyTime(daily), _currentMonthlyTime(month),
_currentSeasonalTime(season), _currentAnnualTime(annual), _defaultActiveRestore(restore),
_reductionValue(reduce), _reductionRatio(100), _prevLogTime(start), _prevStopReportTime(start),
_isNewControl(true), _loadedActiveRestore(LMAR_NEWCONTROL), _activeRestore(LMAR_MANUAL_RESTORE), _controlPriority(0)
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
        _controlPriority     = aRef.getControlPriority();
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

const CtiTime& CtiTableLMControlHistory::getControlCompleteTime() const
{
    return _controlCompleteTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setControlCompleteTime( const CtiTime& cst )
{
    _controlCompleteTime = cst;
    return *this;
}

const CtiTime& CtiTableLMControlHistory::getStartTime() const
{
    return _startDateTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setStartTime( const CtiTime& start )
{
    setDirty();
    _startDateTime = start;
    return *this;
}

const CtiTime& CtiTableLMControlHistory::getStopTime() const
{
    return _stopDateTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setStopTime( const CtiTime& stop )
{
    setDirty();
    _stopDateTime = stop;
    return *this;
}

const CtiTime& CtiTableLMControlHistory::getPreviousLogTime() const
{
    return _prevLogTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setPreviousLogTime( const CtiTime& start )
{
    _prevLogTime = start;
    return *this;
}

const CtiTime& CtiTableLMControlHistory::getPreviousStopReportTime() const
{
    return _prevStopReportTime;
}
CtiTableLMControlHistory& CtiTableLMControlHistory::setPreviousStopReportTime( const CtiTime& start )
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

INT CtiTableLMControlHistory::getControlPriority() const
{
    return _controlPriority;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setControlPriority( const INT priority )
{
    setDirty();
    _controlPriority = priority;
    return *this;
}

const string& CtiTableLMControlHistory::getControlType() const
{

    return _controlType;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setControlType( const string& ct )
{
    setDirty();
    _controlType = ct;
    if(_controlType.length() > 128) _controlType.resize(128);       // Make certain the table can be written.

    return *this;
}

LONG CtiTableLMControlHistory::getCurrentDailyTime() const
{
    return _currentDailyTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setCurrentDailyTime( const LONG dt )
{
    setDirty();
    _currentDailyTime = dt >= 0 ? dt : 0;
    return *this;
}

LONG CtiTableLMControlHistory::getCurrentSeasonalTime() const
{
    return _currentSeasonalTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setCurrentSeasonalTime( const LONG st )
{
    setDirty();
    _currentSeasonalTime = st >= 0 ? st : 0;
    return *this;
}

LONG CtiTableLMControlHistory::getCurrentMonthlyTime() const
{
    return _currentMonthlyTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setCurrentMonthlyTime( const LONG mt )
{
    setDirty();
    _currentMonthlyTime = mt >= 0 ? mt : 0;
    return *this;
}

LONG CtiTableLMControlHistory::getCurrentAnnualTime() const
{

    return _currentAnnualTime;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setCurrentAnnualTime( const LONG at )
{
    setDirty();
    _currentAnnualTime = at >= 0 ? at : 0;
    return *this;
}

const string& CtiTableLMControlHistory::getActiveRestore() const
{
    if(_isNewControl && _activeRestore == string(LMAR_LOGTIMER) )
    {
        _activeRestore = string( LMAR_NEWCONTROL );
    }

    return _activeRestore;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setActiveRestore( const string& ar )
{
    setDirty();
    _activeRestore = ar;
    return *this;
}

const string& CtiTableLMControlHistory::getDefaultActiveRestore() const
{
    return _defaultActiveRestore;
}

const string& CtiTableLMControlHistory::getLoadedActiveRestore() const
{
    return _loadedActiveRestore;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::setDefaultActiveRestore( const string& ar )
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

string CtiTableLMControlHistory::getTableName()
{
    return "LMControlHistory";
}

string CtiTableLMControlHistory::getDynamicTableName()
{
    return "DynamicLMControlHistory";
}

string CtiTableLMControlHistory::getSQLCoreStatement()
{
    static const string sql =  "SELECT YP.paobjectid, LM.lmctrlhistid, LM.startdatetime, LM.stopdatetime, "
                                 "LM.soe_tag, LM.controlduration, LM.controltype, LM.currentdailytime, "
                                 "LM.currentmonthlytime, LM.currentseasonaltime, LM.currentannualtime, LM.activerestore, "
                                 "LM.reductionvalue, LM.protocolpriority "
                               "FROM YukonPAObject YP, LMControlHistory LM "
                               "WHERE YP.paobjectid = LM.paobjectid";

    return sql;
}

string CtiTableLMControlHistory::getSQLCoreStatementOutstanding()
{
    static const string sql = "SELECT LM.paobjectid, LM.lmctrlhistid, LM.startdatetime, LM.stopdatetime, "
                                "LM.soe_tag, LM.controlduration, LM.controltype, LM.currentdailytime, "
                                "LM.currentmonthlytime, LM.currentseasonaltime, LM.currentannualtime, LM.activerestore, "
                                "LM.reductionvalue, LM.protocolpriority "
                              "FROM DynamicLMControlHistory LM "
                              "WHERE LM.activerestore = 'S'";

    return sql;
}

string CtiTableLMControlHistory::getSQLCoreStatementIncomplete()
{
    static const string sql =  "SELECT LM.paobjectid, LM.lmctrlhistid, LM.startdatetime, LM.stopdatetime, "
                                 "LM.soe_tag, LM.controlduration, LM.controltype, LM.currentdailytime, "
                                 "LM.currentmonthlytime, LM.currentseasonaltime, LM.currentannualtime, LM.activerestore, "
                                 "LM.reductionvalue, LM.protocolpriority "
                               "FROM LMControlHistory LM "
                               "WHERE (LM.activerestore = 'N' OR LM.activerestore = 'T' OR LM.activerestore = 'M' "
                                 "OR LM.activerestore = 'C' OR LM.activerestore = 'S') AND LM.startdatetime > ?";

    return sql;
}

string CtiTableLMControlHistory::getSQLCoreStatementDynamic()
{
    static const string sql =  "SELECT YP.paobjectid, DH.lmctrlhistid, DH.startdatetime, DH.stopdatetime, "
                                 "DH.soe_tag, DH.controlduration, DH.controltype, DH.currentdailytime, "
                                 "DH.currentmonthlytime, DH.currentseasonaltime, DH.currentannualtime, DH.activerestore, "
                                 "DH.reductionvalue, DH.protocolpriority "
                               "FROM YukonPAObject YP, DynamicLMControlHistory DH "
                               "WHERE YP.paobjectid = DH.paobjectid";

    return sql;
}

bool CtiTableLMControlHistory::deleteOutstandingControls()
{
    static const std::string sql = "delete from " + getDynamicTableName() + " where activerestore = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << LMAR_DISPATCH_SHUTDOWN;

    return deleter.execute();
}

/*
 *  This method will update any outstanding controls which have completed prior to dispatch having restarted.
 */
bool CtiTableLMControlHistory::updateCompletedOutstandingControls()
{
    static const std::string sql = "update " + getDynamicTableName() +
                                   " set "
                                        "activerestore = ?"
                                   " where "
                                        "activerestore = ? and "
                                        "stopdatetime <= ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << LMAR_DISPATCH_MISSED_COMPLETION
        << LMAR_DISPATCH_SHUTDOWN
        << CtiTime();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << updater.asString() << endl;
    }

    bool success = executeUpdater(updater);

    if( ! success )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** CtiTableLMControlHistory::updateCompletedOutstandingControls update not ok **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return success;
}




/*
select * from lmcontrolhistory where activerestore='N' and
soe_tag not in (select soe_tag from lmcontrolhistory where activerestore='M' or activerestore='T')
*/

void CtiTableLMControlHistory::DecodeDatabaseReader(Cti::RowReader &rdr)
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
    rdr["protocolpriority"]     >> _controlPriority;

    setUpdatedFlag();
}

void CtiTableLMControlHistory::DecodeControlTimes(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["stopdatetime"]        >> _prevLogTime;
    rdr["currentdailytime"]    >> _currentDailyTime;
    rdr["currentmonthlytime"]  >> _currentMonthlyTime;
    rdr["currentseasonaltime"] >> _currentSeasonalTime;
    rdr["currentannualtime"]   >> _currentAnnualTime;
    rdr["activerestore"]       >> _loadedActiveRestore;

    _prevStopReportTime = _prevLogTime;
    _controlCompleteTime = _stopDateTime;

    setUpdatedFlag();
}

bool CtiTableLMControlHistory::Restore()
{
    long maxid = GetMaxLMControl(getPAOID());

    static const string sql =  "SELECT LCH.paobjectid, LCH.lmctrlhistid, LCH.startdatetime, LCH.stopdatetime, LCH.soe_tag, "
                                   "LCH.controlduration, LCH.controltype, LCH.currentdailytime, LCH.currentmonthlytime, "
                                   "LCH.currentseasonaltime, LCH.currentannualtime, LCH.activerestore, LCH.reductionvalue, "
                                   "LCH.protocolpriority "
                               "FROM LMControlHistory LCH "
                               "WHERE LCH.paobjectid = ? AND LCH.lmctrlhistid = ?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader reader(connection, sql);

    reader << getPAOID()
           << maxid;

    reader.execute();

    if( reader() )
    {
        DecodeDatabaseReader( reader );
        setDirty( false );
        return true;
    }
    else
    {
        setDirty( true );
        return false;
    }
}

bool CtiTableLMControlHistory::Insert()
{
    Cti::Database::DatabaseConnection   conn;

    return Insert(conn);
}

bool CtiTableLMControlHistory::Insert(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    if(!getLMControlHistoryID()) setLMControlHistoryID( LMControlHistoryIdGen() );

    Cti::Database::DatabaseWriter   inserter(conn, sql);

    inserter <<
        getLMControlHistoryID() <<
        getPAOID() <<
        CtiTime(getStartTime()) <<
        getSoeTag() <<
        (LONG )( getStopTime().seconds() - getStartTime().seconds() ) <<     // getControlDuration() <<
        (getControlType().empty() ? "(none)" : getControlType()) <<
        getCurrentDailyTime() <<
        getCurrentMonthlyTime() <<
        getCurrentSeasonalTime() <<
        getCurrentAnnualTime() <<
        (getActiveRestore().empty() ? "U" : getActiveRestore()) <<
        getReductionValue() <<
        CtiTime(getStopTime()) <<
        getControlPriority();

    bool success = true;

    if(getStopTime().seconds() >= getStartTime().seconds())
    {
        success = inserter.execute();

        if( success )
        {
            setDirty(false);
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Unable to insert LM Control History for PAO id " << getPAOID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << inserter.asString() << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** LMControlHistory cannot record negative control times. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << inserter.asString() << endl;
        dump();
    }

    return success;
}


bool CtiTableLMControlHistory::Update()
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "lmctrlhistid = ?, "
                                        "startdatetime = ?, "
                                        "stopdatetime = ?, "
                                        "soe_tag = ?, "
                                        "controlduration = ?, "
                                        "controltype = ?, "
                                        "currentdailytime = ?, "
                                        "currentmonthlytime = ?, "
                                        "currentseasonaltime = ?, "
                                        "currentannualtime = ?, "
                                        "activerestore = ?, "
                                        "reductionvalue = ?, "
                                        "protocolpriority = ?"
                                   " where "
                                        "paobjectid = ?";


    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       updater(conn, sql);

    updater
        << getLMControlHistoryID()
        << getStartTime()
        << getStopTime()
        << getSoeTag()
        << (LONG) (getStopTime().seconds() - getStartTime().seconds())
        << getControlType()
        << getCurrentDailyTime()
        << getCurrentMonthlyTime()
        << getCurrentSeasonalTime()
        << getCurrentAnnualTime()
        << getActiveRestore()
        << getReductionValue()
        << getControlPriority()
        << getPAOID();

    bool success = executeUpdater(updater);

    if( success )
    {
        setDirty(false);
    }

    return success;
}

CtiTableLMControlHistory& CtiTableLMControlHistory::incrementTimes(const CtiTime &logTime, const LONG increment, bool season_reset )
{
    CtiDate prevdate(getPreviousLogTime());
    CtiDate today(logTime);

    bool newday = false;
    bool newmonth = false;
    bool newyear = false;
    bool newseason = season_reset;

    LONG partialIncrement = 0;              // This is the component of the log that should have been allocated into yesterday, and last month and last year.

    if(getPreviousLogTime() > logTime && increment < 0)
    {
        // This is quite likely a backup from the shutdown process.
        /* !!!!! May need to add code here in the future !!!!! */
    }
    else
    {
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
                CtiTime lastTimePrevious(prevdate, 23,59,59);
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
        {
            DatabaseConnection conn;
            DatabaseReader rdr(conn, sql);
            rdr.execute();

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
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        dout << " protocolpriority      " << getControlPriority() << endl;
    }

    return;
}

void CtiTableLMControlHistory::DecodeOutstandingControls(Cti::RowReader &rdr)
{
    CtiTime now;

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
    rdr["protocolpriority"]     >> _controlPriority;

    if(_loadedActiveRestore == LMAR_DISPATCH_SHUTDOWN && now < _stopDateTime)
    {
        ULONG secToStop = _stopDateTime.seconds() - now.seconds();  // This is the number of mis allocated seconds...

        _currentDailyTime -= secToStop;
        _currentMonthlyTime -= secToStop;
        _currentSeasonalTime -= secToStop;
        _currentAnnualTime -= secToStop;
    }

    _controlCompleteTime    = _stopDateTime;
    _activeRestore          = string(LMAR_LOGTIMER);
    _defaultActiveRestore   = string(LMAR_TIMED_RESTORE);        // Assume this is a timed in control since it had a stop time in the log???
    _prevLogTime            = _stopDateTime < now ? _stopDateTime : now;
    _prevStopReportTime     = _stopDateTime < now ? _stopDateTime : now;

    return;
}

bool CtiTableLMControlHistory::UpdateDynamic()
{
    Cti::Database::DatabaseConnection   conn;

    return UpdateDynamic(conn);
}

bool CtiTableLMControlHistory::UpdateDynamic(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "update " + getDynamicTableName() +
                                   " set "
                                        "lmctrlhistid = ?, "
                                        "startdatetime = ?, "
                                        "stopdatetime = ?, "
                                        "soe_tag = ?, "
                                        "controlduration = ?, "
                                        "controltype = ?, "
                                        "currentdailytime = ?, "
                                        "currentmonthlytime = ?, "
                                        "currentseasonaltime = ?, "
                                        "currentannualtime = ?, "
                                        "activerestore = ?, "
                                        "reductionvalue = ?, "
                                        "protocolpriority = ?"
                                   " where "
                                        "paobjectid = ?";

    Cti::Database::DatabaseWriter   updater(conn, sql);

    updater
        << getLMControlHistoryID()
        << getStartTime()
        << getStopTime()
        << getSoeTag()
        << ((LONG) (getStopTime().seconds() - getStartTime().seconds()))
        << (getControlType().empty() ? std::string("(none)") : getControlType())
        << getCurrentDailyTime()
        << getCurrentMonthlyTime()
        << getCurrentSeasonalTime()
        << getCurrentAnnualTime()
        << getActiveRestore()
        << getReductionValue()
        << getControlPriority()
        << getPAOID();

    bool success = executeUpdater(updater);

    if( ! success )
    {
        InsertDynamic(conn);        // Try a vanilla insert if the update failed!
    }

    return success;
}


bool CtiTableLMControlHistory::InsertDynamic(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "insert into " + getDynamicTableName() +
                                   " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseWriter   inserter(conn, sql);

    inserter <<
        getPAOID() <<
        getLMControlHistoryID() <<
        CtiTime(getStartTime()) <<
        getSoeTag() <<
        ((LONG )( getStopTime().seconds() - getStartTime().seconds()) ) <<
        (getControlType().empty() ? "(none)" : getControlType()) <<
        getCurrentDailyTime() <<
        getCurrentMonthlyTime() <<
        getCurrentSeasonalTime() <<
        getCurrentAnnualTime() <<
        getActiveRestore() <<
        getReductionValue() <<
        CtiTime(getStopTime()) <<
        getControlPriority();

    bool success = true;

    if(getStopTime().seconds() >= getStartTime().seconds())
    {
        success = inserter.execute();

        if( ! success )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Unable to insert Dynamic LM Control History for PAO id " << getPAOID() << ". " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << inserter.asString() << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** DynamicLMControlHistory cannot record negative control times. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << inserter.asString() << endl;
        dump();
    }

    return success;
}


