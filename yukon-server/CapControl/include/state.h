/*---------------------------------------------------------------------------
        Filename:  state.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCState
                        CtiCCState is a copy of each entry in the state table

        Initial Date:  1/5/01
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCSTATEIMPL_H
#define CTICCSTATEIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "connection.h"
#include "observe.h"

class CtiCCState : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCState )

    CtiCCState();
    CtiCCState(RWDBReader& rdr);
    CtiCCState(const CtiCCState& state);

    virtual ~CtiCCState();

    const RWCString& Text() const;
    LONG ForegroundColor() const;
    LONG BackgroundColor() const;

    CtiCCState& setText(const RWCString& text);
    CtiCCState& setForegroundColor(LONG foregroundcolor);
    CtiCCState& setBackgroundColor(LONG backgroundcolor);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCState& operator=(const CtiCCState& right);

    CtiCCState* replicate() const;

private:

    RWCString _text;
    LONG _foregroundcolor;
    LONG _backgroundcolor;

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    void restore(RWDBReader& rdr);
};
#endif
