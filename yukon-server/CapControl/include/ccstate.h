/*---------------------------------------------------------------------------
        Filename:  ccstate.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCState
                        CtiCCState is a copy of each entry in the state table

        Initial Date:  9/04/2001
        
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

using boost::shared_ptr;

class CtiCCState : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCState )

    CtiCCState();
    CtiCCState(RWDBReader& rdr);
    CtiCCState(const CtiCCState& state);

    virtual ~CtiCCState();

    const string& getText() const;
    LONG getForegroundColor() const;
    LONG getBackgroundColor() const;

    CtiCCState& setText(const string& text);
    CtiCCState& setForegroundColor(LONG foregroundcolor);
    CtiCCState& setBackgroundColor(LONG backgroundcolor);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCState& operator=(const CtiCCState& right);

    CtiCCState* replicate() const;

private:

    string _text;
    LONG _foregroundcolor;
    LONG _backgroundcolor;

    void restore(RWDBReader& rdr);
};

//typedef shared_ptr<CtiCCState> CtiCCStatePtr; 
typedef CtiCCState* CtiCCStatePtr; 
#endif
