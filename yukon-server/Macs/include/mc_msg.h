
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mc_msg
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mc_msg.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 19:35:38 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------
        Filename:  mc_msg.h

        Programmer:  Aaron Lauinger

        Description:    Header file for the following classes:
                            CtiMCUpdateSchedule
                            CtiMCAddSchedule
                            CtiMCRetrieveSchedule
                            CtiMCOverrideRequest

        Messages the server will send to it's clients:

        CtiMCSchedule (see mc_sched.h)
        -   A CtiMCSchedule will be sent whenever a significant state
            change in a schedule occures, or when a client requests
            a CtiMCSchedule be sent via the CtiMCRetrieveSchedule
            message.

        CtiMCDeleteSchedule
        -   A CtiMCDeleteSchedule will be sent whenever a schedule is
            deleted.

        CtiMCInfo
        -   A CtiMCInfo will be sent when significant events occur,
            i.e. something started or stopped, or an error occured.
            Most likely only the text string will be of interest to
            the client.

        Messages the client will send to the server:

        CtiMCUpdateSchedule
        CtiMCAddSchedule
        CtiMCDeleteSchedule
        CtiMCRetrieveSchedule
        CtiMCOverrideRequest
        CtiMCInfo

        Initial Date:  4/7/99
                       1/11/01

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 1999, 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786)

#ifndef __MSG_MCCMD_H__
#define __MSG_MCCMD_H__

#include "message.h"
#include "mc_sched.h"

/*
    CtiMCUpdateSchedule
    Requests that the server update the given schedule.
*/
class CtiMCUpdateSchedule : public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiMCUpdateSchedule )

    CtiMCUpdateSchedule();
    CtiMCUpdateSchedule(const CtiMCUpdateSchedule& ref);

    virtual ~CtiMCUpdateSchedule();

    virtual CtiMCUpdateSchedule& operator=(const CtiMCUpdateSchedule& ref);

    virtual CtiMessage* replicateMessage() const;

    const CtiMCSchedule& getSchedule() const;
    CtiMCUpdateSchedule& setSchedule(const CtiMCSchedule& sched);

    // tcl script file, set to 0 length if there is none
    const std::string& getScript() const;
    CtiMCUpdateSchedule& setScript(const std::string& script);

    void saveGuts(RWvostream &aStream) const;
    void restoreGuts(RWvistream& aStream);

private:

    CtiMCSchedule  _schedule;
    std::string _script;
};

/*
    CtiMCAddSchedule
    Requests that the server add the given schedule.
*/
class CtiMCAddSchedule : public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiMCAddSchedule )

    CtiMCAddSchedule();
    CtiMCAddSchedule(const CtiMCAddSchedule& ref);

    virtual ~CtiMCAddSchedule();

    virtual CtiMCAddSchedule& operator=(const CtiMCAddSchedule& ref);

    virtual CtiMessage* replicateMessage() const;

    const CtiMCSchedule& getSchedule() const;
    CtiMCAddSchedule& setSchedule(const CtiMCSchedule& sched);

    // tcl script file, set to 0 length if there is none
    const std::string& getScript() const;
    CtiMCAddSchedule& setScript(const std::string& script);

    void saveGuts(RWvostream &aStream) const;
    void restoreGuts(RWvistream& aStream);

private:

    CtiMCSchedule  _schedule;
    std::string _script;
};

/*
    CtiMCDeleteSchedule
    Indicates that the schedule with the given id should be
    deleted if sent from client to server.
    Indicates that the schedule with the given id has been
    deleted if sent from the server to client.
*/
class CtiMCDeleteSchedule : public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiMCDeleteSchedule )

    CtiMCDeleteSchedule();
    CtiMCDeleteSchedule(const CtiMCDeleteSchedule& ref);

    virtual ~CtiMCDeleteSchedule();

    virtual CtiMCDeleteSchedule& operator=(const CtiMCDeleteSchedule& ref);

    virtual CtiMessage* replicateMessage() const;

    long getScheduleID() const;
    CtiMCDeleteSchedule& setScheduleID(long id);

    void saveGuts(RWvostream &aStream) const;
    void restoreGuts(RWvistream& aStream);

private:

    long _id;

};


/*
    CtiMCRetrieveSchedule
    Requests a single schedule by id, or all the schedules.
    Use CtiMCRetrieveSchedule::AllSchedules for the id to
    requests all schedules.
*/
class CtiMCRetrieveSchedule : public CtiMessage
{
public:

    enum
    {
        AllSchedules = 0
    };

    RWDECLARE_COLLECTABLE( CtiMCRetrieveSchedule )

    CtiMCRetrieveSchedule();
    CtiMCRetrieveSchedule(const CtiMCRetrieveSchedule& ref);

    virtual ~CtiMCRetrieveSchedule();

    virtual CtiMCRetrieveSchedule& operator=(const CtiMCRetrieveSchedule& ref);

    virtual CtiMessage* replicateMessage() const;

    long getScheduleID() const;
    CtiMCRetrieveSchedule& setScheduleID(long id);

    void saveGuts(RWvostream &aStream) const;
    void restoreGuts(RWvistream& aStream);

private:
    long _id;
};

/*
    CtiMCRetrieveScript
    Requests a script by name.
*/
class CtiMCRetrieveScript : public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiMCRetrieveScript )

    CtiMCRetrieveScript();
    CtiMCRetrieveScript(const CtiMCRetrieveScript& ref);

    virtual ~CtiMCRetrieveScript();

    virtual CtiMCRetrieveScript& operator=(const CtiMCRetrieveScript& ref);

    virtual CtiMessage* replicateMessage() const;

    const std::string& getScriptName() const;
    CtiMCRetrieveScript& setScriptName(const std::string& script_name);

    void saveGuts(RWvostream &aStream) const;
    void restoreGuts(RWvistream& aStream);
private:

    std::string _name;
};

class CtiMCVerifyScript : public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiMCVerifyScript )

    CtiMCVerifyScript();
    CtiMCVerifyScript(const CtiMCVerifyScript& ref);

    virtual ~CtiMCVerifyScript();

    virtual CtiMCVerifyScript& operator=(const CtiMCVerifyScript& ref);

    virtual CtiMessage* replicateMessage() const;

    const std::string& getScriptName() const;
    CtiMCVerifyScript& setScriptName(const std::string& script_name);

    void saveGuts(RWvostream &aStream) const;
    void restoreGuts(RWvistream& aStream);

private:

    std::string _name;

};

/*
    CtiMCOverrideRequest
    Requests that the server override a schedules start and/or
    stop policies.  A value of 0 for either start or stop time
    indicates that it should be ignored.
*/
class CtiMCOverrideRequest : public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiMCOverrideRequest )

    enum Action
    {
        Start = 0,
        StartNow,
        Stop,
        StopNow,
        Enable,
        Disable
    };

    CtiMCOverrideRequest();
    CtiMCOverrideRequest(const CtiMCOverrideRequest& ref);

    virtual ~CtiMCOverrideRequest();

    virtual CtiMCOverrideRequest& operator=(const CtiMCOverrideRequest& ref);

    virtual CtiMessage* replicateMessage() const;

    Action getAction() const;
    long getID() const;
    CtiTime getStartTime() const;
    CtiTime getStopTime() const;

    CtiMCOverrideRequest& setAction(Action action);
    CtiMCOverrideRequest& setID(long id);
    CtiMCOverrideRequest& setStartTime(const CtiTime& time);
    CtiMCOverrideRequest& setStopTime(const CtiTime& time);

    void saveGuts(RWvostream &aStream) const;
    void restoreGuts(RWvistream& aStream);

private:

    Action _action;
    long _id;
    CtiTime _start_time;
    CtiTime _stop_time;
};

/*
    CtiMCInfo
    Contains information about a schedule state change/error/etc.
    A schedule id is included in the event that the information
    refers to that particular schedule
*/
class CtiMCInfo : public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiMCInfo )

    CtiMCInfo();
    CtiMCInfo(const CtiMCInfo& ref);

    virtual ~CtiMCInfo();

    virtual CtiMCInfo& operator=(const CtiMCInfo& ref);

    virtual CtiMessage* replicateMessage() const;

    //value of 0 refers to no schedule
    long getID() const;
    const std::string& getInfo() const;

    CtiMCInfo& setID(long id);
    CtiMCInfo& setInfo(const std::string& info);

    void saveGuts(RWvostream &aStream) const;
    void restoreGuts(RWvistream& aStream);

private:

    long _id;
    std::string _info;
};
#endif
