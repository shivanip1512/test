#include "yukon.h"

#include "logger.h"
#include "msg_notif_alarm.h"
#include "rwutil.h"

RWDEFINE_COLLECTABLE( CtiNotifAlarmMsg, NOTIF_ALARM_MSG_ID );

CtiNotifAlarmMsg::CtiNotifAlarmMsg()
{ }

CtiNotifAlarmMsg::CtiNotifAlarmMsg(const vector<int>& group_ids, int point_id, int condition, double value, bool acknowledged, bool abnormal)
    : _notif_group_ids(group_ids), _point_id(point_id), _condition(condition), _value(value), _acknowledged(acknowledged), _abnormal(abnormal)
{ }
    
CtiNotifAlarmMsg::~CtiNotifAlarmMsg()
{ }

const vector<int>& CtiNotifAlarmMsg::getNotifGroupIDs() const
{
    return _notif_group_ids;
}

int CtiNotifAlarmMsg::getPointID() const
{
    return _point_id;
}

int CtiNotifAlarmMsg::getCondition() const
{
    return _condition;
}

double CtiNotifAlarmMsg::getValue() const
{
    return _value;
}

bool CtiNotifAlarmMsg::isAcknowledged() const
{
    return _acknowledged;
}

bool CtiNotifAlarmMsg::isAbnormal() const
{
    return _abnormal;
}

CtiNotifAlarmMsg& CtiNotifAlarmMsg::setNotifGroupIDs(const vector<int>& group_ids)
{
    _notif_group_ids = group_ids;
    return *this;
}

CtiNotifAlarmMsg& CtiNotifAlarmMsg:: setPointID(int point_id)
{
    _point_id = point_id;
    return *this;    
}

CtiNotifAlarmMsg& CtiNotifAlarmMsg:: setCondition(int condition)
{
    _condition = condition;
    return *this;    
}

CtiNotifAlarmMsg& CtiNotifAlarmMsg:: setAcknowledged(bool acknowledged)
{
    _acknowledged = acknowledged;
    return *this;
}

CtiNotifAlarmMsg& CtiNotifAlarmMsg:: setAbnormal(bool abnormal)
{
    _abnormal = abnormal;
    return *this;
}

void CtiNotifAlarmMsg::saveGuts(RWvostream &aStream) const
{
    CtiMessage::saveGuts(aStream);

    aStream
	<< _notif_group_ids
	<< _point_id
	<< _condition
	<< _value
	<< _acknowledged
	<< _abnormal;
}  

void CtiNotifAlarmMsg::restoreGuts(RWvistream& aStream)
{
    CtiMessage::restoreGuts(aStream);

    aStream
	>> _notif_group_ids
	>> _point_id
	>> _condition
	>> _value
	>> _acknowledged
	>> _abnormal;
}
   
CtiMessage* CtiNotifAlarmMsg::replicateMessage() const
{
    CtiNotifAlarmMsg* ret = new CtiNotifAlarmMsg(*this);
    return (CtiMessage*) ret;
}

void CtiNotifAlarmMsg::dump() const
{
    dout << RWTime() << " CtiNotifAlarmMsg -" << endl;
    for(vector<int>::const_iterator i = _notif_group_ids.begin(); i != _notif_group_ids.end(); i++)
    {
	dout << "  Notification Group ID: " <<  *i << endl;
    }
    dout << "  Point ID: " << _point_id << endl;
    dout << "  Condition:  " << _condition << endl;
    dout << "  Value:  " << _value << endl;
    dout << "  Acknowledged:  " << _acknowledged << endl;
    dout << "  Abnormal:  " << _abnormal << endl;
}
