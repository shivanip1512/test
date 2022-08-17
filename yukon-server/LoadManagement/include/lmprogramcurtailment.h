#pragma once

#include "lmcurtailcustomer.h"
#include "lmprogrambase.h"
#include "database_connection.h"

class CtiLMProgramCurtailment : public CtiLMProgramBase
{

public:

DECLARE_COLLECTABLE( CtiLMProgramCurtailment );

    CtiLMProgramCurtailment();
    CtiLMProgramCurtailment(Cti::RowReader &rdr);
    CtiLMProgramCurtailment(const CtiLMProgramCurtailment& curtailprog);

    virtual ~CtiLMProgramCurtailment();

    LONG getMinNotifyTime() const;
    const std::string& getHeading() const;
    const std::string& getMessageHeader() const;
    const std::string& getMessageFooter() const;
    LONG getAckTimeLimit() const;
    const std::string& getCanceledMsg() const;
    const std::string& getStoppedEarlyMsg() const;
    LONG getCurtailReferenceId() const;
    const CtiTime& getActionDateTime() const;
    const CtiTime& getNotificationDateTime() const;
    const CtiTime& getCurtailmentStartTime() const;
    const CtiTime& getCurtailmentStopTime() const;
    const std::string& getRunStatus() const;
    const std::string& getAdditionalInfo() const;
    std::vector<CtiLMCurtailCustomer*>& getLMProgramCurtailmentCustomers();
    const std::vector<CtiLMCurtailCustomer*>& getLMProgramCurtailmentCustomers() const;

    CtiLMProgramCurtailment& setMinNotifyTime(LONG notifytime);
    CtiLMProgramCurtailment& setHeading(const std::string& head);
    CtiLMProgramCurtailment& setMessageHeader(const std::string& msgheader);
    CtiLMProgramCurtailment& setMessageFooter(const std::string& msgfooter);
    CtiLMProgramCurtailment& setAckTimeLimit(LONG timelimit);
    CtiLMProgramCurtailment& setCanceledMsg(const std::string& canceled);
    CtiLMProgramCurtailment& setStoppedEarlyMsg(const std::string& stoppedearly);
    CtiLMProgramCurtailment& setCurtailReferenceId(LONG refid);
    CtiLMProgramCurtailment& setActionDateTime(const CtiTime& actiontime);
    CtiLMProgramCurtailment& setNotificationDateTime(const CtiTime& notificationtime);
    CtiLMProgramCurtailment& setCurtailmentStartTime(const CtiTime& starttime);
    CtiLMProgramCurtailment& setCurtailmentStopTime(const CtiTime& stoptime);
    CtiLMProgramCurtailment& setRunStatus(const std::string& runstat);
    CtiLMProgramCurtailment& setAdditionalInfo(const std::string& additional);

    //void restoreCurtailmentSpecificDatabaseEntries(Cti::RowReader &rdr);
    void notifyCustomers(CtiMultiMsg* multiDispatchMsg);
    void notifyCustomersOfStop(CtiMultiMsg* multiDispatchMsg);
    void addLMCurtailProgramActivityTable();
    void updateLMCurtailProgramActivityTable(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    void deleteLMCurtailProgramActivityTable();
    void restoreDynamicData();

    void dumpDynamicData();
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    virtual CtiLMProgramBaseSPtr replicate() const;
    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, std::vector<CtiLMControlAreaTrigger*> controlAreaTriggers, LONG secondsFromBeginningOfDay, CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded);
    virtual BOOL hasControlHoursAvailable();
    virtual BOOL stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, CtiTime currentTime);
    virtual BOOL handleManualControl(CtiTime currentTime, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);

    CtiLMProgramCurtailment& operator=(const CtiLMProgramCurtailment& right);

    int operator==(const CtiLMProgramCurtailment& right) const;
    int operator!=(const CtiLMProgramCurtailment& right) const;

    // Static Members

    // Possible run statuses
    static const std::string NullRunStatus;
    static const std::string ScheduledRunStatus;
    static const std::string NotifiedRunStatus;
    static const std::string CanceledRunStatus;
    static const std::string ActiveRunStatus;
    static const std::string StoppedEarlyRunStatus;
    static const std::string CompletedRunStatus;

private:

    LONG _minnotifytime;
    std::string _heading;
    std::string _messageheader;
    std::string _messagefooter;
    LONG _acktimelimit;
    std::string _canceledmsg;
    std::string _stoppedearlymsg;
    LONG _curtailreferenceid;
    CtiTime _actiondatetime;
    CtiTime _notificationdatetime;
    CtiTime _curtailmentstarttime;
    CtiTime _curtailmentstoptime;
    std::string _runstatus;
    std::string _additionalinfo;

    std::vector<CtiLMCurtailCustomer*> _lmprogramcurtailmentcustomers;

    void restore(Cti::RowReader &rdr);
};

typedef boost::shared_ptr< CtiLMProgramCurtailment > CtiLMProgramCurtailmentSPtr;
