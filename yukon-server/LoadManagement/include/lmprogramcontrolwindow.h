/*---------------------------------------------------------------------------
        Filename:  lmprogramcontrolwindow.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMProgramControlWindow
                        CtiLMProgramControlWindow 

        Initial Date:  2/13/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMPROGRAMCONTROLWINDOWIMPL_H
#define CTILMPROGRAMCONTROLWINDOWIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
                
class CtiLMProgramControlWindow : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMProgramControlWindow )

    CtiLMProgramControlWindow();
    CtiLMProgramControlWindow(RWDBReader& rdr);
    CtiLMProgramControlWindow(const CtiLMProgramControlWindow& lmprogcontwindow);

    virtual ~CtiLMProgramControlWindow();

    ULONG getPAOId() const;
    ULONG getWindowNumber() const;
    ULONG getAvailableStartTime() const;
    ULONG getAvailableStopTime() const;

    CtiLMProgramControlWindow& setPAOId(ULONG paoid);
    CtiLMProgramControlWindow& setWindowNumber(ULONG winnum);
    CtiLMProgramControlWindow& setAvailableStartTime(ULONG availstarttime);
    CtiLMProgramControlWindow& setAvailableStopTime(ULONG availstoptime);

    CtiLMProgramControlWindow* replicate() const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMProgramControlWindow& operator=(const CtiLMProgramControlWindow& right);

    int operator==(const CtiLMProgramControlWindow& right) const;
    int operator!=(const CtiLMProgramControlWindow& right) const;

    /* Static Members */

private:
    
    ULONG _paoid;
    ULONG _windownumber;
    ULONG _availablestarttime;
    ULONG _availablestoptime;

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    void restore(RWDBReader& rdr);
};
#endif

