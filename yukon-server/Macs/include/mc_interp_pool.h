
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mc_interp_pool
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mc_interp_pool.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __MC_INTERP_POOL_H__
#define __MC_INTERP_POOL_H__

#pragma warning( disable : 4786 )

#include <set>

#include "logger.h"
#include "guard.h"

#include "mc_interp.h"

class CtiMCInterpreterPool
{
public:

    CtiMCInterpreterPool();
    virtual ~CtiMCInterpreterPool();

    CtiMCInterpreter* acquireInterpreter();
    void releaseInterpreter(CtiMCInterpreter* interp);

    void stopAndDestroyAllInterpreters();

    // dumps the active and available interpreters to dout
    void dumpPool();

private:
    typedef set< CtiMCInterpreter* >::iterator interp_set_iter;

    // Used to protect the interpreter sets
    CtiMutex _mux;

    set< CtiMCInterpreter* > _available_interps;
    set< CtiMCInterpreter* > _active_interps;
};

#endif

