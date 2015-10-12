#pragma once

#include "dlldefs.h"
#include "message.h" 
/*
 * CtiNotifLMControlMsg is used to tell the notification server
 * about a loadmanagement event that will happen or has happened.
 */

class IM_EX_MSG CtiNotifLMControlMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiNotifLMControlMsg )

public:

    typedef CtiMessage Inherited;

    enum NotificationType { STARTING = 1, STARTING_NEVER_STOP, UPDATING, FINISHING, SCHEDULING };
    
    CtiNotifLMControlMsg();
    CtiNotifLMControlMsg(const std::vector<int>& group_ids, int notif_type, int program_id, const CtiTime& start_time, const CtiTime& stop_time);
    
    virtual ~CtiNotifLMControlMsg();

    const std::vector<int>& getNotifGroupIDs() const;
    int getNotifType() const;
    int getProgramID() const;
    const CtiTime& getStartTime() const;
    const CtiTime& getStopTime() const;

    CtiNotifLMControlMsg& setNotifGroupIDs(const std::vector<int>& group_ids);
    CtiNotifLMControlMsg& setNotifType(int type);
    CtiNotifLMControlMsg& setProgramID(int program_id);
    CtiNotifLMControlMsg& setStartTime(const CtiTime& start_time);
    CtiNotifLMControlMsg& setStopTime(const CtiTime& stop_time);

    virtual CtiMessage* replicateMessage() const;

public:
    std::vector<int> _notif_group_ids;
    int _notif_type;
    int _program_id;
    CtiTime _start_time;
    CtiTime _stop_time;
};
