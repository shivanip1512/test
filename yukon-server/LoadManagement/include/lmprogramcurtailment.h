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

#include "lmprogrambase.h"
#include "observe.h"

class CtiLMProgramCurtailment : public CtiLMProgramBase
{

public:

RWDECLARE_COLLECTABLE( CtiLMProgramCurtailment )

    CtiLMProgramCurtailment();
    CtiLMProgramCurtailment(RWDBReader& rdr);
    CtiLMProgramCurtailment(const CtiLMProgramCurtailment& curtailprog);

    virtual ~CtiLMProgramCurtailment();

    LONG getMinNotifyTime() const;
    const RWCString& getHeading() const;
    const RWCString& getMessageHeader() const;
    const RWCString& getMessageFooter() const;
    LONG getAckTimeLimit() const;
    const RWCString& getCanceledMsg() const;
    const RWCString& getStoppedEarlyMsg() const;
    LONG getCurtailReferenceId() const;
    const RWDBDateTime& getActionDateTime() const;
    const RWDBDateTime& getNotificationDateTime() const;
    const RWDBDateTime& getCurtailmentStartTime() const;
    const RWDBDateTime& getCurtailmentStopTime() const;
    const RWCString& getRunStatus() const;
    const RWCString& getAdditionalInfo() const;
    RWOrdered& getLMProgramCurtailmentCustomers();

    CtiLMProgramCurtailment& setMinNotifyTime(LONG notifytime);
    CtiLMProgramCurtailment& setHeading(const RWCString& head);
    CtiLMProgramCurtailment& setMessageHeader(const RWCString& msgheader);
    CtiLMProgramCurtailment& setMessageFooter(const RWCString& msgfooter);
    CtiLMProgramCurtailment& setAckTimeLimit(LONG timelimit);
    CtiLMProgramCurtailment& setCanceledMsg(const RWCString& canceled);
    CtiLMProgramCurtailment& setStoppedEarlyMsg(const RWCString& stoppedearly);
    CtiLMProgramCurtailment& setCurtailReferenceId(LONG refid);
    CtiLMProgramCurtailment& setActionDateTime(const RWDBDateTime& actiontime);
    CtiLMProgramCurtailment& setNotificationDateTime(const RWDBDateTime& notificationtime);
    CtiLMProgramCurtailment& setCurtailmentStartTime(const RWDBDateTime& starttime);
    CtiLMProgramCurtailment& setCurtailmentStopTime(const RWDBDateTime& stoptime);
    CtiLMProgramCurtailment& setRunStatus(const RWCString& runstat);
    CtiLMProgramCurtailment& setAdditionalInfo(const RWCString& additional);

    //void restoreCurtailmentSpecificDatabaseEntries(RWDBReader& rdr);
    void notifyCustomers(CtiMultiMsg* multiDispatchMsg);
    void notifyCustomersOfStop(CtiMultiMsg* multiDispatchMsg);
    void addLMCurtailProgramActivityTable();
    void updateLMCurtailProgramActivityTable(RWDBConnection& conn, RWDBDateTime& currentDateTime);
    void deleteLMCurtailProgramActivityTable();
    void restoreDynamicData(RWDBReader& rdr);

    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);

    virtual CtiLMProgramBase* replicate() const;
    virtual DOUBLE reduceProgramLoad(DOUBLE loadReductionNeeded, LONG currentPriority, RWOrdered controlAreaTriggers, LONG secondsFromBeginningOfDay, ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, BOOL isTriggerCheckNeeded);
    virtual BOOL hasControlHoursAvailable();
    virtual BOOL stopProgramControl(CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg, ULONG secondsFrom1901);
    virtual BOOL handleManualControl(ULONG secondsFrom1901, CtiMultiMsg* multiPilMsg, CtiMultiMsg* multiDispatchMsg);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMProgramCurtailment& operator=(const CtiLMProgramCurtailment& right);

    int operator==(const CtiLMProgramCurtailment& right) const;
    int operator!=(const CtiLMProgramCurtailment& right) const;

    // Static Members

    // Possible run statuses
    static const RWCString NullRunStatus;
    static const RWCString ScheduledRunStatus;
    static const RWCString NotifiedRunStatus;
    static const RWCString CanceledRunStatus;
    static const RWCString ActiveRunStatus;
    static const RWCString StoppedEarlyRunStatus;
    static const RWCString CompletedRunStatus;

private:

    LONG _minnotifytime;
    RWCString _heading;
    RWCString _messageheader;
    RWCString _messagefooter;
    LONG _acktimelimit;
    RWCString _canceledmsg;
    RWCString _stoppedearlymsg;
    LONG _curtailreferenceid;
    RWDBDateTime _actiondatetime;
    RWDBDateTime _notificationdatetime;
    RWDBDateTime _curtailmentstarttime;
    RWDBDateTime _curtailmentstoptime;
    RWCString _runstatus;
    RWCString _additionalinfo;

    RWOrdered _lmprogramcurtailmentcustomers;

    void restore(RWDBReader& rdr);
};
#endif

