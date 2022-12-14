#pragma once

#include "message.h"
#include "mc_sched.h"


/*
    CtiMCUpdateSchedule
    Requests that the server update the given schedule.
*/
class CtiMCUpdateSchedule : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiMCUpdateSchedule );

public:

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
    DECLARE_COLLECTABLE( CtiMCAddSchedule );

private:
    typedef CtiMessage Inherited;

public:

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
    DECLARE_COLLECTABLE( CtiMCDeleteSchedule );

private:
    typedef CtiMessage Inherited;

public:

    CtiMCDeleteSchedule();
    CtiMCDeleteSchedule(const CtiMCDeleteSchedule& ref);

    virtual ~CtiMCDeleteSchedule();

    virtual CtiMCDeleteSchedule& operator=(const CtiMCDeleteSchedule& ref);

    virtual CtiMessage* replicateMessage() const;

    long getScheduleID() const;
    CtiMCDeleteSchedule& setScheduleID(long id);

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
    DECLARE_COLLECTABLE( CtiMCRetrieveSchedule );

private:
    typedef CtiMessage Inherited;

public:

    enum
    {
        AllSchedules = 0
    };

    CtiMCRetrieveSchedule();
    CtiMCRetrieveSchedule(const CtiMCRetrieveSchedule& ref);

    virtual ~CtiMCRetrieveSchedule();

    virtual CtiMCRetrieveSchedule& operator=(const CtiMCRetrieveSchedule& ref);

    virtual CtiMessage* replicateMessage() const;

    long getScheduleID() const;
    CtiMCRetrieveSchedule& setScheduleID(long id);

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
    DECLARE_COLLECTABLE( CtiMCRetrieveScript );

private:
    typedef CtiMessage Inherited;

public:

    CtiMCRetrieveScript();
    CtiMCRetrieveScript(const CtiMCRetrieveScript& ref);

    virtual ~CtiMCRetrieveScript();

    virtual CtiMCRetrieveScript& operator=(const CtiMCRetrieveScript& ref);

    virtual CtiMessage* replicateMessage() const;

    const std::string& getScriptName() const;
    CtiMCRetrieveScript& setScriptName(const std::string& script_name);

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
    DECLARE_COLLECTABLE( CtiMCOverrideRequest );

private:
    typedef CtiMessage Inherited;

public:

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
    DECLARE_COLLECTABLE( CtiMCInfo );

private:
    typedef CtiMessage Inherited;

public:

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

private:

    long _id;
    std::string _info;
};
