/*-----------------------------------------------------------------------------
    Filename:  pao_event.h
    
    Programmer:  Julie Richter
        
    Description:    
                        
    Initial Date:  1/27/2005
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2005
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
 
#ifndef CTIPAOEVENT_H
#define CTIPAOEVENT_H

#include <rw/thr/thrfunc.h>
#include <rw/thr/runfunc.h>
#include <rw/thr/srvpool.h>
#include <rw/thr/thrutil.h>
#include <rw/thr/countptr.h> 
#include <rw/collect.h>

#include "dbaccess.h"
#include "connection.h"
#include "ctibase.h"
#include "logger.h"
#include "yukon.h"
#include "pao_event.h"
#include "dbmemobject.h"
                       
class CtiPAOEvent  : public CtiMemDBObject, public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
public:

    CtiPAOEvent();
    CtiPAOEvent(long eventId, long schedId, long paoId, RWCString command);
    CtiPAOEvent(RWDBReader& rdr);
    virtual ~CtiPAOEvent();

    long getEventId();
    long getScheduleId();
    long getPAOId();
    RWCString getEventCommand();

    void setEventId(long eventId);
    void setScheduleId(long schedId);
    void setPAOId(long paoId);
    void setEventCommand(RWCString eventCommand);

    CtiPAOEvent& operator=(const CtiPAOEvent& right);
    int operator==(const CtiPAOEvent& right) const;
    int operator!=(const CtiPAOEvent& right) const;

    BOOL isDirty();
    void setDirty(BOOL flag);

private:
    
    BOOL _isValid;
    BOOL _dirty;

    long         _eventId;
    long         _scheduleId;
    long         _paoId;
    RWCString    _eventCommand;
    
};
#endif
