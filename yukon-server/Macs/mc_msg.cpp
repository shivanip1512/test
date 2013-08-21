#include "precompiled.h"
#include "mc_msg.h"

/*-----------------------------------------------------------------------------*
*
* File:   mc_msg
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_msg.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:25:02 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

using namespace std;
/*=== CtiMCUpdateSchedule ===*/

DEFINE_COLLECTABLE( CtiMCUpdateSchedule, MSG_MC_UPDATE_SCHEDULE );

CtiMCUpdateSchedule::CtiMCUpdateSchedule()
{
}

CtiMCUpdateSchedule::CtiMCUpdateSchedule(const CtiMCUpdateSchedule& ref)
{
    *this = ref;
}

CtiMCUpdateSchedule::~CtiMCUpdateSchedule()
{
}

CtiMCUpdateSchedule&
CtiMCUpdateSchedule::operator=(const CtiMCUpdateSchedule& ref)
{
    CtiMessage::operator=( (CtiMessage&) ref );

    setSchedule( ref.getSchedule() );
    setScript( ref.getScript() );

    return *this;
}

CtiMessage* CtiMCUpdateSchedule::replicateMessage() const
{
    CtiMCUpdateSchedule* aCopy = new CtiMCUpdateSchedule();
    *aCopy = *this;
    return aCopy;
}

const CtiMCSchedule& CtiMCUpdateSchedule::getSchedule() const
{
    return _schedule;
}

CtiMCUpdateSchedule& CtiMCUpdateSchedule::setSchedule(const CtiMCSchedule& sched)
{
    _schedule = sched;
    return *this;
}

const string& CtiMCUpdateSchedule::getScript() const
{
    return _script;
}

CtiMCUpdateSchedule& CtiMCUpdateSchedule::setScript(const string& script)
{
    _script = script;
    return *this;
}


/*=== CtiMCAddSchedule ===*/

DEFINE_COLLECTABLE( CtiMCAddSchedule, MSG_MC_ADD_SCHEDULE );

CtiMCAddSchedule::CtiMCAddSchedule()
{
}

CtiMCAddSchedule::CtiMCAddSchedule(const CtiMCAddSchedule& ref)
{
    *this = ref;
}

CtiMCAddSchedule::~CtiMCAddSchedule()
{
}

CtiMCAddSchedule& CtiMCAddSchedule::operator=(const CtiMCAddSchedule& ref)
{
    CtiMessage::operator=( (CtiMessage&) ref );

    setSchedule( ref.getSchedule() );
    setScript( ref.getScript() );

    return *this;
}

CtiMessage* CtiMCAddSchedule::replicateMessage() const
{
    CtiMCAddSchedule* aCopy = new CtiMCAddSchedule();
    *aCopy = *this;
    return aCopy;
}

const CtiMCSchedule& CtiMCAddSchedule::getSchedule() const
{
    return _schedule;
}

CtiMCAddSchedule& CtiMCAddSchedule::setSchedule(const CtiMCSchedule& sched)
{
    _schedule = sched;
    return *this;
}

const string& CtiMCAddSchedule::getScript() const
{
    return _script;
}

CtiMCAddSchedule& CtiMCAddSchedule::setScript(const string& script)
{
    _script = script;
    return *this;
}


/*=== CtiMCDeleteSchedule ===*/

DEFINE_COLLECTABLE( CtiMCDeleteSchedule, MSG_MC_DELETE_SCHEDULE );

CtiMCDeleteSchedule::CtiMCDeleteSchedule()
    : _id(0)
{
}

CtiMCDeleteSchedule::CtiMCDeleteSchedule(const CtiMCDeleteSchedule& ref)
{
    *this = ref;
}

CtiMCDeleteSchedule::~CtiMCDeleteSchedule()
{
}

CtiMCDeleteSchedule& CtiMCDeleteSchedule::operator=(const CtiMCDeleteSchedule& ref)
{
    CtiMessage::operator=( (CtiMessage&) ref);
    setScheduleID( ref.getScheduleID() );
    return *this;
}

CtiMessage* CtiMCDeleteSchedule::replicateMessage() const
{
    CtiMCDeleteSchedule* aCopy = new CtiMCDeleteSchedule();
    *aCopy = *this;
    return aCopy;
}

long CtiMCDeleteSchedule::getScheduleID() const
{
    return _id;
}

CtiMCDeleteSchedule& CtiMCDeleteSchedule::setScheduleID(long id)
{
    _id = id;
    return *this;
}


/*=== CtiMCRetrieveSchedule ===*/

DEFINE_COLLECTABLE( CtiMCRetrieveSchedule, MSG_MC_RETRIEVE_SCHEDULE );

CtiMCRetrieveSchedule::CtiMCRetrieveSchedule()
    : _id(0)
{
}

CtiMCRetrieveSchedule::CtiMCRetrieveSchedule(const CtiMCRetrieveSchedule& ref)
{
    *this = ref;
}

CtiMCRetrieveSchedule::~CtiMCRetrieveSchedule()
{
}

CtiMCRetrieveSchedule&
CtiMCRetrieveSchedule::operator=(const CtiMCRetrieveSchedule& ref)
{
    CtiMessage::operator=( (CtiMessage&) ref );
    setScheduleID( ref.getScheduleID() );
    return *this;
}

CtiMessage* CtiMCRetrieveSchedule::replicateMessage() const
{
    CtiMCRetrieveSchedule* aCopy = new CtiMCRetrieveSchedule();
    *aCopy = *this;
    return aCopy;
}

long CtiMCRetrieveSchedule::getScheduleID() const
{
    return _id;
}

CtiMCRetrieveSchedule& CtiMCRetrieveSchedule::setScheduleID(long id)
{
    _id = id;
    return *this;
}


/*=== CtiMCRetrieveScript ===*/

DEFINE_COLLECTABLE( CtiMCRetrieveScript, MSG_MC_RETRIEVE_SCRIPT );

CtiMCRetrieveScript::CtiMCRetrieveScript()
{
}

CtiMCRetrieveScript::CtiMCRetrieveScript(const CtiMCRetrieveScript& ref)
{
    *this = ref;
}

CtiMCRetrieveScript::~CtiMCRetrieveScript()
{
}

CtiMCRetrieveScript&
CtiMCRetrieveScript::operator=(const CtiMCRetrieveScript& ref)
{
    CtiMessage::operator=( (CtiMessage&) ref );
    setScriptName( ref.getScriptName() );
    return *this;
}

CtiMessage* CtiMCRetrieveScript::replicateMessage() const
{
    CtiMCRetrieveScript* aCopy = new CtiMCRetrieveScript();
    *aCopy = *this;
    return aCopy;
}

const string& CtiMCRetrieveScript::getScriptName() const
{
    return _name;
}

CtiMCRetrieveScript&
CtiMCRetrieveScript::setScriptName(const string& name)
{
    _name = name;
    return *this;
}


/*=== CtiMCOverrideRequest ===*/

DEFINE_COLLECTABLE( CtiMCOverrideRequest, MSG_MC_OVERRIDE_REQUEST );

CtiMCOverrideRequest::CtiMCOverrideRequest()
    : _action(Start), _id(0)        // debug build initialized these to zero - in the enum: Start == 0
{
}

CtiMCOverrideRequest::CtiMCOverrideRequest(const CtiMCOverrideRequest& ref)
{
    *this = ref;
}

CtiMCOverrideRequest::~CtiMCOverrideRequest()
{
}

CtiMCOverrideRequest&
CtiMCOverrideRequest::operator=(const CtiMCOverrideRequest& ref)
{
    CtiMessage::operator=( (CtiMessage&) ref );
    setAction( ref.getAction() );
    setID( ref.getID() );
    return *this;
}

CtiMessage* CtiMCOverrideRequest::replicateMessage() const
{
    CtiMCOverrideRequest* aCopy = new CtiMCOverrideRequest();
    *aCopy = *this;
    return aCopy;
}

CtiMCOverrideRequest::Action CtiMCOverrideRequest::getAction() const
{
    return _action;
}

long CtiMCOverrideRequest::getID() const
{
    return _id;
}

CtiTime CtiMCOverrideRequest::getStartTime() const
{
    return _start_time;
}

CtiTime CtiMCOverrideRequest::getStopTime() const
{
    return _stop_time;
}

CtiMCOverrideRequest& CtiMCOverrideRequest::setAction(Action action)
{
    _action = action;
    return *this;
}

CtiMCOverrideRequest& CtiMCOverrideRequest::setID(long id)
{
    _id = id;
    return *this;
}

CtiMCOverrideRequest& CtiMCOverrideRequest::setStartTime(const CtiTime& aTime)
{
    _start_time = aTime;
    return *this;
}

CtiMCOverrideRequest& CtiMCOverrideRequest::setStopTime(const CtiTime& aTime)
{
    _stop_time = aTime;
    return *this;
}


/*=== CtiMCInfo ===*/

DEFINE_COLLECTABLE( CtiMCInfo, MSG_MC_INFO );

CtiMCInfo::CtiMCInfo()
    : _id(0)
{ }

CtiMCInfo::CtiMCInfo(const CtiMCInfo& ref)
{
    *this = ref;
}

CtiMCInfo::~CtiMCInfo()
{ }

CtiMCInfo& CtiMCInfo::operator=(const CtiMCInfo& ref)
{
    CtiMessage::operator=( (CtiMessage&) ref );
    setID( ref.getID() );
    setInfo( ref.getInfo() );
    return *this;
}

CtiMessage* CtiMCInfo::replicateMessage() const
{
    CtiMCInfo* aCopy = new CtiMCInfo();
    *aCopy = *this;
    return aCopy;
}

long CtiMCInfo::getID() const
{
    return _id;
}

const string& CtiMCInfo::getInfo() const
{
    return _info;
}

CtiMCInfo& CtiMCInfo::setID(long id)
{
    _id = id;
    return *this;
}

CtiMCInfo& CtiMCInfo::setInfo(const string& info)
{
    _info = info;
    return *this;
}

