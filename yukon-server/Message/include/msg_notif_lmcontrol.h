#ifndef __MSG_NOTIF_LMCONTROL_H_
#define __MSG_NOTIF_LMCONTROL_H_

#include <rw/collect.h>
#include <rw/rwtime.h>

#include "dlldefs.h"
#include "message.h"

/*
 * CtiNotifLMControlMsg is used to tell the notification server
 * about a loadmanagement event that will happen or has happened.
 */

class IM_EX_MSG CtiNotifLMControlMsg : public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiNotifLMControlMsg );

    enum NotificationType { STARTING = 1, UPDATING, FINISHING };
    
    CtiNotifLMControlMsg();
    CtiNotifLMControlMsg(const vector<int>& group_ids, int notif_type, int program_id, const RWTime& start_time, const RWTime& stop_time);
    
    virtual ~CtiNotifLMControlMsg();

    const vector<int>& getNotifGroupIDs() const;
    int getNotifType() const;
    int getProgramID() const;
    const RWTime& getStartTime() const;
    const RWTime& getStopTime() const;

    CtiNotifLMControlMsg& setNotifGroupIDs(const vector<int>& group_ids);
    CtiNotifLMControlMsg& setNotifType(int type);
    CtiNotifLMControlMsg& setProgramID(int program_id);
    CtiNotifLMControlMsg& setStartTime(const RWTime& start_time);
    CtiNotifLMControlMsg& setStopTime(const RWTime& stop_time);

    virtual void saveGuts(RWvostream &aStream) const;
    virtual void restoreGuts(RWvistream& aStream);
    virtual CtiMessage* replicateMessage() const;

private:
    vector<int> _notif_group_ids;
    int _notif_type;
    int _program_id;
    RWTime _start_time;
    RWTime _stop_time;
};


#endif
