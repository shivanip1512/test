#include "yukon.h"
#include "mc_msg.h"

/*-----------------------------------------------------------------------------*
*
* File:   mc_msg
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_msg.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:23:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


/*=== CtiMCUpdateSchedule ===*/

RWDEFINE_COLLECTABLE( CtiMCUpdateSchedule, MSG_MC_UPDATE_SCHEDULE );

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
    CtiMCUpdateSchedule* copy = new CtiMCUpdateSchedule();
    *copy = *this;
    return copy;
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

void CtiMCUpdateSchedule::saveGuts(RWvostream &aStream) const
{
    CtiMessage::saveGuts(aStream);
    aStream     <<  _schedule
                <<  RWCString( (const char*) _script.data() );
}

void CtiMCUpdateSchedule::restoreGuts(RWvistream& aStream)
{
    RWCString temp_str;
    CtiMessage::restoreGuts(aStream);
    aStream     >>  _schedule
                >>  temp_str;

    _script = (const char*) temp_str.data();
}

/*=== CtiMCAddSchedule ===*/

RWDEFINE_COLLECTABLE( CtiMCAddSchedule, MSG_MC_ADD_SCHEDULE );

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
    CtiMCAddSchedule* copy = new CtiMCAddSchedule();
    *copy = *this;
    return copy;
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

void CtiMCAddSchedule::saveGuts(RWvostream &aStream) const
{
    CtiMessage::saveGuts(aStream);
    aStream     << _schedule
                << RWCString( (const char*) _script.data() );
}

void CtiMCAddSchedule::restoreGuts(RWvistream& aStream)
{
    RWCString temp_str;

    CtiMessage::restoreGuts(aStream);
    aStream     >> _schedule
                >> temp_str;

    _script = (const char*) temp_str.data();
}

/*=== CtiMCDeleteSchedule ===*/

RWDEFINE_COLLECTABLE( CtiMCDeleteSchedule, MSG_MC_DELETE_SCHEDULE );

CtiMCDeleteSchedule::CtiMCDeleteSchedule()
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
    CtiMCDeleteSchedule* copy = new CtiMCDeleteSchedule();
    *copy = *this;
    return copy;
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

void CtiMCDeleteSchedule::saveGuts(RWvostream &aStream) const
{
    CtiMessage::saveGuts(aStream);
    aStream << _id;
}

void CtiMCDeleteSchedule::restoreGuts(RWvistream& aStream)
{
    CtiMessage::restoreGuts(aStream);
    aStream >> _id;
}

/*=== CtiMCRetrieveSchedule ===*/

RWDEFINE_COLLECTABLE( CtiMCRetrieveSchedule, MSG_MC_RETRIEVE_SCHEDULE );

CtiMCRetrieveSchedule::CtiMCRetrieveSchedule()
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
    CtiMCRetrieveSchedule* copy = new CtiMCRetrieveSchedule();
    *copy = *this;
    return copy;
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

void CtiMCRetrieveSchedule::saveGuts(RWvostream &aStream) const
{
    CtiMessage::saveGuts(aStream);
    aStream << _id;
}

void CtiMCRetrieveSchedule::restoreGuts(RWvistream& aStream)
{
    CtiMessage::restoreGuts(aStream);
    aStream >> _id;
}

/*=== CtiMCRetrieveScript ===*/

RWDEFINE_COLLECTABLE( CtiMCRetrieveScript, MSG_MC_RETRIEVE_SCRIPT );

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
    CtiMCRetrieveScript* copy = new CtiMCRetrieveScript();
    *copy = *this;
    return copy;
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

void CtiMCRetrieveScript::saveGuts(RWvostream &aStream) const
{
    CtiMessage::saveGuts(aStream);
    aStream  <<  RWCString( (char*) _name.data() );

}

void CtiMCRetrieveScript::restoreGuts(RWvistream& aStream)
{
    RWCString _rw_name;

    CtiMessage::restoreGuts(aStream);
    aStream  >>  _rw_name;

    _name = _rw_name;
}

/*=== CtiMCVerifyScript ===*/

RWDEFINE_COLLECTABLE( CtiMCVerifyScript, MSG_MC_VERIFY_SCRIPT );

CtiMCVerifyScript::CtiMCVerifyScript() 
{
}

CtiMCVerifyScript::CtiMCVerifyScript(const CtiMCVerifyScript& ref)
{
    *this = ref;
}

CtiMCVerifyScript::~CtiMCVerifyScript()
{
}

CtiMCVerifyScript&
CtiMCVerifyScript::operator=(const CtiMCVerifyScript& ref) 
{
    CtiMessage::operator=( (CtiMessage&) ref);
    setScriptName(ref.getScriptName());
    return *this;
}

CtiMessage* CtiMCVerifyScript::replicateMessage() const
{
    CtiMCVerifyScript* copy = new CtiMCVerifyScript();
    *copy = *this;
    return copy;
}

const string& CtiMCVerifyScript::getScriptName() const
{
    return _name;
}

CtiMCVerifyScript&
CtiMCVerifyScript::setScriptName(const string& name)
{
    _name = name;
    return *this;
}

void CtiMCVerifyScript::saveGuts(RWvostream &aStream) const
{
    CtiMessage::saveGuts(aStream);
    aStream  <<  RWCString( (char*) _name.data() );

}

void CtiMCVerifyScript::restoreGuts(RWvistream& aStream) 
{
    RWCString _rw_name;

    CtiMessage::restoreGuts(aStream);
    aStream >> _rw_name;

    _name = _rw_name;
}

/*=== CtiMCOverrideRequest ===*/

RWDEFINE_COLLECTABLE( CtiMCOverrideRequest, MSG_MC_OVERRIDE_REQUEST );

CtiMCOverrideRequest::CtiMCOverrideRequest()
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
    CtiMCOverrideRequest* copy = new CtiMCOverrideRequest();
    *copy = *this;
    return copy;
}

CtiMCOverrideRequest::Action CtiMCOverrideRequest::getAction() const
{
    return _action;
}

long CtiMCOverrideRequest::getID() const
{
    return _id;
}

RWTime CtiMCOverrideRequest::getStartTime() const
{
    return _start_time;
}

RWTime CtiMCOverrideRequest::getStopTime() const
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

CtiMCOverrideRequest& CtiMCOverrideRequest::setStartTime(const RWTime& time)
{
    _start_time = time;
    return *this;
}

CtiMCOverrideRequest& CtiMCOverrideRequest::setStopTime(const RWTime& time)
{
    _stop_time = time;
    return *this;
}

void CtiMCOverrideRequest::saveGuts(RWvostream &aStream) const
{
    CtiMessage::saveGuts(aStream);
    aStream     <<  (unsigned int) _action
                <<  _id
                <<  _start_time
                <<  _stop_time;
}

void CtiMCOverrideRequest::restoreGuts(RWvistream& aStream)
{
    unsigned int uint_action;

    CtiMessage::restoreGuts(aStream);
    aStream     >>  uint_action
                >>  _id
                >>  _start_time
                >>  _stop_time;

    _action = (Action) uint_action;
}


/*=== CtiMCInfo ===*/

RWDEFINE_COLLECTABLE( CtiMCInfo, MSG_MC_INFO )

CtiMCInfo::CtiMCInfo()
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
    CtiMCInfo* copy = new CtiMCInfo();
    *copy = *this;
    return copy;
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

void CtiMCInfo::saveGuts(RWvostream &aStream) const
{
    CtiMessage::saveGuts(aStream);
    aStream     <<  _id
                <<  RWCString( (char*) _info.data() );

}

void CtiMCInfo::restoreGuts(RWvistream& aStream)
{
    RWCString _rw_info;

    CtiMessage::restoreGuts(aStream);
    aStream     >>  _id
                >>  _rw_info;

    _info = _rw_info;
}

