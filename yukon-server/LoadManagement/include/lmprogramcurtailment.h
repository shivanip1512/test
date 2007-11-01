/*---------------------------------------------------------------------------
        Filename:  lmprogramcurtailment.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMProgramCurtailment

        Initial Date:  3/23/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMPROGRAMCURTAILMENTIMPL_H
#define CTILMPROGRAMCURTAILMENTIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "lmcurtailcustomer.h"
#include "lmprogrambase.h"
#include "observe.h"

using std::vector;

class CtiLMProgramCurtailment : public CtiLMProgramBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMProgramCurtailment )

    CtiLMProgramCurtailment();
    CtiLMProgramCurtailment(RWDBReader& rdr);
    CtiLMProgramCurtailment(const CtiLMProgramCurtailment& curtailprog);

    virtual ~CtiLMProgramCurtailment();

    LONG getMinNotifyTime() const;
    const string& getHeading() const;
    const string& getMessageHeader() const;
    const string& getMessageFooter() const;
    LONG getAckTimeLimit() const;
    const string& getCanceledMsg() const;
    const string& getStoppedEarlyMsg() const;
    LONG getCurtailReferenceId() const;
    const CtiTime& getActionDateTime() const;
    const CtiTime& getNotificationDateTime() const;
    const CtiTime& getCurtailmentStartTime() const;
    const CtiTime& getCurtailmentStopTime() const;
    const string& getRunStatus() const;
    const string& getAdditionalInfo() const;
    vector<CtiLMCurtailCustomer*>& getLMProgramCurtailmentCustomers();

    CtiLMProgramCurtailment& setMinNotifyTime(LONG notifytime);
    CtiLMProgramCurtailment& setHeading(const string& head);
    CtiLMProgramCurtailment& setMessageHeader(const string& msgheader);
    CtiLMProgramCurtailment& setMessageFooter(const string& msgfooter);
    CtiLMProgramCurtailment& setAckTimeLimit(LONG timelimit);
    CtiLMProgramCurtailment& setCanceledMsg(const string& canceled);
    CtiLMProgramCurtailment& setStoppedEarlyMsg(const string& stoppedearly);
    CtiLMProgramCurtailment& setCurtailReferenceId(LONG refid);
    CtiLMProgramCurtailment& setActionDateTime(const CtiTime& actiontime);
    CtiLMProgramCurtailment& setNotificationDateTime(const CtiTime& notificationtime);
    CtiLMProgramCurtailment& setCurtailmentStartTime(const CtiTime& starttime);
    CtiLMProgramCurtailment& setCurtailmentStopTime(const CtiTime& stoptime);
    CtiLMProgramCurtailment& setRunStatus(const string& runstat);
    CtiLMProgramCurtailment& setAdditionalInfo(const string& additional);

    //void restoreCurtailmentSpecificDatabaseEntries(RWDBReader& rdr);
    void notifyCustomers(CtiMultiMsg* multiDispatchMsg);
    void notifyCustomersOfStop(CtiMultiMsg* multiDispatchMsg);
    void addLMCurtailProgramActivityTable();
    void updateLMCurtailProgramActivityTable(RWDBConnection& conn, CtiTime& currentDateTime);
    void deleteLMCurtailProgramActivityTable();
    void restoreDynamicData(RWDBReader& rdr);

    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);

    virtual CtiLMProgramBaseSPtr replicate() const;
    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, vector<CtiLMControlAreaTrigger*> controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, BOOL isTriggerCheckNeeded);
    virtual BOOL hasControlHoursAvailable();
    virtual BOOL stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg, ULONG secondsFrom1901);
    virtual BOOL handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, CtiMultiMsg* multiNotifMsg);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMProgramCurtailment& operator=(const CtiLMProgramCurtailment& right);

    int operator==(const CtiLMProgramCurtailment& right) const;
    int operator!=(const CtiLMProgramCurtailment& right) const;

    // Static Members

    // Possible run statuses
    static const string NullRunStatus;
    static const string ScheduledRunStatus;
    static const string NotifiedRunStatus;
    static const string CanceledRunStatus;
    static const string ActiveRunStatus;
    static const string StoppedEarlyRunStatus;
    static const string CompletedRunStatus;

private:

    LONG _minnotifytime;
    string _heading;
    string _messageheader;
    string _messagefooter;
    LONG _acktimelimit;
    string _canceledmsg;
    string _stoppedearlymsg;
    LONG _curtailreferenceid;
    CtiTime _actiondatetime;
    CtiTime _notificationdatetime;
    CtiTime _curtailmentstarttime;
    CtiTime _curtailmentstoptime;
    string _runstatus;
    string _additionalinfo;

    vector<CtiLMCurtailCustomer*> _lmprogramcurtailmentcustomers;

    void restore(RWDBReader& rdr);
};

#if VSLICK_TAG_WORKAROUND
typedef CtiLMProgramCurtailment * CtiLMProgramCurtailmentSPtr;
#else
typedef shared_ptr< CtiLMProgramCurtailment > CtiLMProgramCurtailmentSPtr;
#endif

#endif

