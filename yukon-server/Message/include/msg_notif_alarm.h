#pragma once

#include <vector>

#include "dlldefs.h"
#include "message.h"

/*
 * CtiNotifAlarmMsg is used to tell the notification server
 * that an alarm occurred and it should send out a notification
 */

class IM_EX_MSG CtiNotifAlarmMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiNotifAlarmMsg )

public:

    typedef CtiMessage Inherited;

    CtiNotifAlarmMsg();
    CtiNotifAlarmMsg(const std::vector<int>& group_ids,
                     int category_id,
                     int point_id,
                     int condition,
                     double value,
                     const CtiTime& alarm_timestamp,
                     bool acknowledged,
                     bool abnormal);
    
    virtual ~CtiNotifAlarmMsg();

    const std::vector<int>& getNotifGroupIDs() const;    
    int getCategoryID() const;    
    int getPointID() const;
    int getCondition() const;
    double getValue() const;
    const CtiTime& getAlarmTimestamp() const;
    bool isAcknowledged() const;
    bool isAbnormal() const;

    CtiNotifAlarmMsg& setNotifGroupIDs(const std::vector<int>& group_ids);    
    CtiNotifAlarmMsg& setCategoryID(int category);
    CtiNotifAlarmMsg& setPointID(int point_id);
    CtiNotifAlarmMsg& setCondition(int condition);
    CtiNotifAlarmMsg& setValue(double value);
    CtiNotifAlarmMsg& setAlarmTimestamp(const CtiTime& alarm_timestamp);
    CtiNotifAlarmMsg& setAcknowledged(bool acknowledged);
    CtiNotifAlarmMsg& setAbnormal(bool abnormal);

    virtual CtiMessage* replicateMessage() const;

    virtual std::string toString() const override;


private:
    std::vector<int> _notif_group_ids;    
    int _category_id;
    int _point_id;
    int _condition;
    double _value;
    CtiTime _alarm_timestamp;
    bool _acknowledged;
    bool _abnormal;
};
