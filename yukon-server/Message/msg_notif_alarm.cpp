#include "precompiled.h"

#include "logger.h"
#include "msg_notif_alarm.h"
#include "rwutil.h"

using namespace std;

RWDEFINE_COLLECTABLE( CtiNotifAlarmMsg, NOTIF_ALARM_MSG_ID );

CtiNotifAlarmMsg::CtiNotifAlarmMsg()
    : _category_id(0),
      _point_id(0),
      _condition(0),
      _value(0.0),
      _acknowledged(false),
      _abnormal(false)
{ }

CtiNotifAlarmMsg::CtiNotifAlarmMsg(const vector<int>& group_ids,
                                   int category_id,
                                   int point_id,
                                   int condition,
                                   double value,
                                   const CtiTime& alarm_timestamp,
                                   bool acknowledged,
                                   bool abnormal)
    : _notif_group_ids(group_ids),
      _category_id(category_id),
      _point_id(point_id),
      _condition(condition),
      _value(value),
      _alarm_timestamp(alarm_timestamp),
      _acknowledged(acknowledged),
      _abnormal(abnormal)
{ }

CtiNotifAlarmMsg::~CtiNotifAlarmMsg()
{ }

const vector<int>& CtiNotifAlarmMsg::getNotifGroupIDs() const
{
    return _notif_group_ids;
}

int CtiNotifAlarmMsg::getCategoryID() const
{
    return _category_id;
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

const CtiTime& CtiNotifAlarmMsg::getAlarmTimestamp() const
{
    return _alarm_timestamp;
}

bool CtiNotifAlarmMsg::isAcknowledged() const
{
    return _acknowledged;
}

bool CtiNotifAlarmMsg::isAbnormal() const
{
    return _abnormal;
}

CtiNotifAlarmMsg& CtiNotifAlarmMsg::setCategoryID(int category_id)
{
    _category_id = category_id;
    return *this;
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

CtiNotifAlarmMsg& CtiNotifAlarmMsg::setValue(double value)
{
    _value = value;
    return *this;
}

CtiNotifAlarmMsg& CtiNotifAlarmMsg::setAlarmTimestamp(const CtiTime& alarm_timestamp)
{
    _alarm_timestamp = alarm_timestamp;
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
        << _category_id
        << _point_id
        << _condition
        << _value
    << _alarm_timestamp
        << _acknowledged
        << _abnormal;
}

void CtiNotifAlarmMsg::restoreGuts(RWvistream& aStream)
{
    CtiMessage::restoreGuts(aStream);

    aStream
        >> _notif_group_ids
        >> _category_id
        >> _point_id
        >> _condition
        >> _value
    >> _alarm_timestamp
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
    dout << CtiTime() << " CtiNotifAlarmMsg -" << endl;
    for(vector<int>::const_iterator i = _notif_group_ids.begin(); i != _notif_group_ids.end(); i++)
    {
        dout << "  Notification Group ID: " <<  *i << endl;
    }

    dout << "  Alarm Category ID: " << _category_id << endl;
    dout << "  Point ID: " << _point_id << endl;
    dout << "  Condition:  " << _condition << endl;
    dout << "  Value:  " << _value << endl;
    dout << "  Alarm Timestamp:  " << _alarm_timestamp << endl;
    dout << "  Acknowledged:  " << _acknowledged << endl;
    dout << "  Abnormal:  " << _abnormal << endl;
}
