/*-----------------------------------------------------------------------------
    Filename:  interp_pool.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiInterpreterPool

    Initial Date:  4/7/99
                   9/4/03

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999, 2003
-----------------------------------------------------------------------------*/

#ifndef __INTERP_POOL_H__
#define __INTERP_POOL_H__

#pragma warning( disable : 4786 )

#include <set>

#include "logger.h"
#include "guard.h"

#include "interp.h"

class IM_EX_INTERP CtiInterpreterPool
{
public:

    CtiInterpreterPool();
    virtual ~CtiInterpreterPool();

    void evalOnInit(const string& command);

    CtiInterpreter* acquireInterpreter();
    void releaseInterpreter(CtiInterpreter* interp);

    void stopAndDestroyAllInterpreters();

    // dumps the active and available interpreters to dout
    void dumpPool();

private:
    typedef set< CtiInterpreter* >::iterator interp_set_iter;

    // Used to protect the interpreter sets
    CtiMutex _mux;

    set< CtiInterpreter* > _available_interps;
    set< CtiInterpreter* > _active_interps;
    
    string _init_cmd;

    CtiInterpreter* createInterpreter();
};

#endif

