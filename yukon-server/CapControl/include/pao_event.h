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

#include <rw/db/reader.h>

#include "ctibase.h"
#include "logger.h"
#include "pao_event.h"
#include "dbmemobject.h"
                       
class CtiPAOEvent  : public CtiMemDBObject
{
public:

    CtiPAOEvent();
    CtiPAOEvent(long eventId, long schedId, long paoId, const string& command, BOOL disableOvUv);
    CtiPAOEvent(RWDBReader& rdr);
    virtual ~CtiPAOEvent();

    long getEventId()const;
    long getScheduleId()const;
    long getPAOId()const;
    const string& getEventCommand() const;
    BOOL getDisableOvUvFlag() const;

    void setEventId(long eventId);
    void setScheduleId(long schedId);
    void setPAOId(long paoId);
    void setEventCommand(const string& eventCommand);
    void setDisableOvUvFlag(BOOL flag);

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
    string       _eventCommand;
    BOOL         _disableOvUvFlag;
    
};
#endif
