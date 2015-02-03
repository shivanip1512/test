#pragma once

#include "row_reader.h"

#include "dbmemobject.h"
#include "dbaccess.h"
#include "yukon.h"
#include "dbmemobject.h"
#include "utility.h"
#include "database_connection.h"
#include "loggable.h"

/*
    #define LMAR_NEWCONTROL         "N"             // This is the first entry for any new control.
    #define LMAR_LOGTIMER           "L"             // This is a timed log entry.  Nothing exciting happened in this interval.
    #define LMAR_CONT_CONTROL       "C"             // Previous command was repeated extending the current control interval.

    #define LMAR_TIMED_RESTORE      "T"             // Control terminated based on time set in load group.
    #define LMAR_MANUAL_RESTORE     "M"             // Control terminated because of an active restore or terminate command being sent.
    #define LMAR_OVERRIDE_CONTROL   "O"             // Control terminated because a new command of a different nature was sent to this group.
    #define LMAR_CONTROLACCT_ADJUST "A"             // Control accounting was adjusted by user.
    #define LMAR_PERIOD_TRANSITION  "P"             // Control was active as we crossed a control history boundary.  This log denotes the last log in the previos interval.
    #define LMAR_DISPATCH_SHUTDOWN  "S"             // Control was active as dispatch shutdown.  This entry will be used to resume control.
 */

#define LMAR_DISPATCH_MISSED_COMPLETION  LMAR_TIMED_RESTORE        // Control was active as dispatch shutdown.  It completed before restart.

class IM_EX_CTIYUKONDB CtiTableLMControlHistory: public CtiMemDBObject, public Cti::Loggable
{
protected:

    LONG        _lmControlHistID;
    LONG        _paoID;
    CtiTime     _startDateTime;          // represents the time at which control was begun
    CtiTime     _controlCompleteTime;    // represents the time at which control was last sent
    CtiTime     _stopDateTime;           // represents the time at which the current log interval completed.
    LONG        _soeTag;
    INT         _controlDuration;
    std::string _controlType;
    LONG        _currentDailyTime;
    LONG        _currentMonthlyTime;
    LONG        _currentSeasonalTime;
    LONG        _currentAnnualTime;
    mutable std::string   _activeRestore;
    DOUBLE      _reductionValue;
    INT         _controlPriority;

    // Values below are note stored in the DB.
    std::string  _defaultActiveRestore;
    CtiTime      _prevLogTime;            // Not stored, but used to determine relative positions of controls
    CtiTime      _prevStopReportTime;
    int          _reductionRatio;         // Needed to compute the contribution of cycles

private:

    bool _isNewControl;
    static CtiMutex    _soeMux;
    std::string        _loadedActiveRestore;

public:

    typedef CtiMemDBObject Inherited;

    // CtiTableLMControlHistory();

    CtiTableLMControlHistory(LONG               paoid   = 0,
                             const CtiTime&     start   = CtiTime(),
                             LONG               soe     = 0,
                             INT                dur     = 0,
                             const std::string& type    = std::string("Unavailable"),
                             LONG               daily   = 0,
                             LONG               month   = 0,
                             LONG               season  = 0,
                             LONG               annual  = 0,
                             const std::string& restore = std::string(LMAR_NEWCONTROL),
                             DOUBLE             reduce  = 0.0,
                             LONG               lmchid  = 0L);

    CtiTableLMControlHistory(const CtiTableLMControlHistory& aRef);

    virtual ~CtiTableLMControlHistory();

    CtiTableLMControlHistory& operator=(const CtiTableLMControlHistory& aRef);
    bool operator<(const CtiTableLMControlHistory& aRef) const;

    LONG getLMControlHistoryID() const;
    CtiTableLMControlHistory& setLMControlHistoryID( const LONG lmHID );

    LONG getPAOID() const;
    CtiTableLMControlHistory& setPAOID( const LONG paoID );

    const CtiTime& getStartTime() const;
    CtiTableLMControlHistory& setStartTime( const CtiTime& st );

    const CtiTime& getControlCompleteTime() const;
    CtiTableLMControlHistory& setControlCompleteTime( const CtiTime& cst );

    const CtiTime& getStopTime() const;
    CtiTableLMControlHistory& setStopTime( const CtiTime& st );

    const CtiTime& getPreviousLogTime() const;
    CtiTableLMControlHistory& setPreviousLogTime( const CtiTime& st );

    const CtiTime& getPreviousStopReportTime() const;
    CtiTableLMControlHistory& setPreviousStopReportTime( const CtiTime& st );

    LONG getSoeTag() const;
    CtiTableLMControlHistory& setSoeTag( const LONG soe );

    INT getControlDuration() const;
    CtiTableLMControlHistory& setControlDuration( const INT cd );

    INT getControlPriority() const;
    CtiTableLMControlHistory& setControlPriority( const INT cd );

    const std::string& getControlType() const;
    CtiTableLMControlHistory& setControlType( const std::string& ct );

    LONG getCurrentDailyTime() const;
    CtiTableLMControlHistory& setCurrentDailyTime( const LONG dt );

    LONG getCurrentMonthlyTime() const;
    CtiTableLMControlHistory& setCurrentMonthlyTime( const LONG mt );

    LONG getCurrentSeasonalTime() const;
    CtiTableLMControlHistory& setCurrentSeasonalTime( const LONG seat );

    LONG getCurrentAnnualTime() const;
    CtiTableLMControlHistory& setCurrentAnnualTime( const LONG at );

    const std::string& getActiveRestore() const;
    CtiTableLMControlHistory& setActiveRestore( const std::string& ar );

    const std::string& getDefaultActiveRestore() const;
    CtiTableLMControlHistory& setDefaultActiveRestore( const std::string& ar );

    const std::string& getLoadedActiveRestore() const;

    DOUBLE getReductionValue() const;
    CtiTableLMControlHistory& setReductionValue( const DOUBLE rv );

    int getReductionRatio() const;
    CtiTableLMControlHistory& setReductionRatio( int redrat );

    CtiTableLMControlHistory& incrementTimes( const CtiTime &now, const LONG increment, bool season_reset = false );

    bool isNewControl() const;
    CtiTableLMControlHistory& setNotNewControl( );

    static LONG getNextSOE();
    static std::string getTableName();
    static std::string getDynamicTableName();

    void DecodeControlTimes(Cti::RowReader &rdr);
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual bool Restore();
    virtual bool Insert();
    virtual bool Insert(Cti::Database::DatabaseConnection &conn);
    virtual bool Update();

    virtual std::string toString() const override;
    void DecodeOutstandingControls(Cti::RowReader &rdr);

    static bool deleteOutstandingControls();
    static bool updateCompletedOutstandingControls();
 
    static std::string getSQLCoreStatement();
    static std::string getSQLCoreStatementIncomplete();
    static std::string getSQLCoreStatementOutstanding();
    static std::string getSQLCoreStatementDynamic();

    static void decodeDynamicControls(Cti::RowReader &rdr);

    bool UpdateDynamic();
    bool UpdateDynamic(Cti::Database::DatabaseConnection &conn);
    bool InsertDynamic(Cti::Database::DatabaseConnection &conn);

    virtual int getAssociationId() {return 0;};

};

