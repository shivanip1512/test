#include "yukon.h"
#include "msg_notif_lmcontrol.h"
#include "rwutil.h"

RWDEFINE_COLLECTABLE( CtiNotifLMControlMsg, NOTIF_LMCONTROL_MSG_ID );

CtiNotifLMControlMsg::CtiNotifLMControlMsg() : _program_id(-1)
{ }

CtiNotifLMControlMsg::CtiNotifLMControlMsg(const vector<int>& group_ids, int notif_type, int program_id, const RWTime& start_time, const RWTime& stop_time)
    : _notif_group_ids(group_ids), _notif_type(notif_type),_program_id(program_id), _start_time(start_time), _stop_time(stop_time)
{ }
    
CtiNotifLMControlMsg::~CtiNotifLMControlMsg()
{ }

const vector<int>& CtiNotifLMControlMsg::getNotifGroupIDs() const
{
    return _notif_group_ids;
}

int CtiNotifLMControlMsg::getNotifType() const
{
    return _notif_type;
}

int CtiNotifLMControlMsg::getProgramID() const
{
    return _program_id;
}

const RWTime& CtiNotifLMControlMsg::getStartTime() const
{
    return _start_time;
}

const RWTime& CtiNotifLMControlMsg::getStopTime() const
{
    return _stop_time;
}

CtiNotifLMControlMsg& CtiNotifLMControlMsg::setNotifGroupIDs(const vector<int>& group_ids)
{
    _notif_group_ids = group_ids;
    return *this;
}

CtiNotifLMControlMsg& CtiNotifLMControlMsg::setNotifType(int type)
{
    _notif_type = type;
    return *this;
}

CtiNotifLMControlMsg& CtiNotifLMControlMsg::setProgramID(int program_id)
{
    _program_id = program_id;
    return *this;
}

CtiNotifLMControlMsg& CtiNotifLMControlMsg::setStartTime(const RWTime& start_time)
{
    _start_time = start_time;
    return *this;
}

CtiNotifLMControlMsg& CtiNotifLMControlMsg::setStopTime(const RWTime& stop_time)
{
    _stop_time = stop_time;
    return *this;
}
    
void CtiNotifLMControlMsg::saveGuts(RWvostream &aStream) const
{
    CtiMessage::saveGuts(aStream);

    aStream
	<< _notif_group_ids
	<< _notif_type
	<< _program_id
	<< _start_time
	<< _stop_time;
}  

void CtiNotifLMControlMsg::restoreGuts(RWvistream& aStream)
{
    CtiMessage::restoreGuts(aStream);

    aStream
	>> _notif_group_ids
	>> _notif_type
	>> _program_id
	>> _start_time
	>> _stop_time;
}
   
CtiMessage* CtiNotifLMControlMsg::replicateMessage() const
{
    CtiNotifLMControlMsg* ret = new CtiNotifLMControlMsg(*this);
    return (CtiMessage*) ret;
}
