#ifndef __MSG_NOTIF_ALARM_H_
#define __MSG_NOTIF_ALARM_H_

#include <rw/collect.h>

#include "dlldefs.h"
#include "message.h"

/*
 * CtiNotifAlarmMsg is used to tell the notification server
 * that an alarm occurred and it should send out a notification
 */

class IM_EX_MSG CtiNotifAlarmMsg : public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiNotifAlarmMsg );

    CtiNotifAlarmMsg();
    CtiNotifAlarmMsg(const vector<int>& group_ids, int point_id, int condition, double value, bool acknowledged, bool abnormal);
    
    virtual ~CtiNotifAlarmMsg();

    const vector<int>& getNotifGroupIDs() const;
    int getPointID() const;
    int getCondition() const;
    double getValue() const;
    bool isAcknowledged() const;
    bool isAbnormal() const;
    
    CtiNotifAlarmMsg& setNotifGroupIDs(const vector<int>& group_ids);
    CtiNotifAlarmMsg& setPointID(int point_id);
    CtiNotifAlarmMsg& setCondition(int condition);
    CtiNotifAlarmMsg& setAcknowledged(bool acknowledged);
    CtiNotifAlarmMsg& setAbnormal(bool abnormal);
	
    virtual void saveGuts(RWvostream &aStream) const;
    virtual void restoreGuts(RWvistream& aStream);
    virtual CtiMessage* replicateMessage() const;

    virtual void dump() const;

    
private:
    vector<int> _notif_group_ids;
    int _point_id;
    int _condition;
    double _value;
    bool _acknowledged;
    bool _abnormal;
};
#endif
