#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "ctidate.h"
#include "row_reader.h"

class CtiLMProgramControlWindow : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiLMProgramControlWindow )

    CtiLMProgramControlWindow();
    CtiLMProgramControlWindow(Cti::RowReader &rdr);
    CtiLMProgramControlWindow(const CtiLMProgramControlWindow& lmprogcontwindow);

    virtual ~CtiLMProgramControlWindow();

    LONG getPAOId() const;
    LONG getWindowNumber() const;
    CtiTime getAvailableStartTime(CtiDate &defaultDate = CtiDate::now()) const;
    CtiTime getAvailableStopTime(CtiDate &defaultDate = CtiDate::now()) const;

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

    void restore(Cti::RowReader &rdr);
};
