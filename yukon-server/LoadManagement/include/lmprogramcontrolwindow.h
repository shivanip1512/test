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

    LONG getPAOId() const;
    LONG getWindowNumber() const;
    LONG getAvailableStartTime() const;
    LONG getAvailableStopTime() const;

    CtiLMProgramControlWindow& setPAOId(LONG paoid);
    CtiLMProgramControlWindow& setWindowNumber(LONG winnum);
    CtiLMProgramControlWindow& setAvailableStartTime(LONG availstarttime);
    CtiLMProgramControlWindow& setAvailableStopTime(LONG availstoptime);

    CtiLMProgramControlWindow* replicate() const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMProgramControlWindow& operator=(const CtiLMProgramControlWindow& right);

    int operator==(const CtiLMProgramControlWindow& right) const;
    int operator!=(const CtiLMProgramControlWindow& right) const;

    /* Static Members */

private:
    
    LONG _paoid;
    LONG _windownumber;
    LONG _availablestarttime;
    LONG _availablestoptime;

    void restore(RWDBReader& rdr);
};
#endif

