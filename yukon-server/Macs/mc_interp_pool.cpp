
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mc_interp_pool
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_interp_pool.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:02 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "mc_interp_pool.h"

CtiMCInterpreterPool::CtiMCInterpreterPool()
{
}

CtiMCInterpreterPool::~CtiMCInterpreterPool()
{
    stopAndDestroyAllInterpreters();
}

/*----------------------------------------------------------------------------
  acquireInterpreter

  Returns a pointer to an available interpreter.
  If none are available it will create a new interpreter and return it.

----------------------------------------------------------------------------*/
CtiMCInterpreter* CtiMCInterpreterPool::acquireInterpreter()
{
    CtiLockGuard< CtiMutex > guard(_mux);

    CtiMCInterpreter* interp = NULL;

    if( _available_interps.size() > 0 )
    {
        interp_set_iter iter = _available_interps.begin();
        interp = *iter;
        _available_interps.erase( iter );
    }
    else
    {
        interp = new CtiMCInterpreter();
        interp->start();
    }

    pair< set< CtiMCInterpreter* >::iterator, bool > result =
        _active_interps.insert( interp );

    assert( result.second );

    return interp;
}

/*----------------------------------------------------------------------------
  releaseInterpreter

  Returns an interpreter to the pool so it can be used again.

----------------------------------------------------------------------------*/
void CtiMCInterpreterPool::releaseInterpreter(CtiMCInterpreter* interp)
{
    CtiLockGuard< CtiMutex > guard(_mux);

    interp_set_iter iter = _active_interps.find(interp);

    assert( iter != _active_interps.end() );

    _active_interps.erase(iter);

    pair< set< CtiMCInterpreter* >::iterator, bool > result =
         _available_interps.insert( interp );

    assert( result.second );

    return;
}

/*----------------------------------------------------------------------------
  stopAndDestroyAllInterpreters

  Stops and deletes all interpreters in the pool

----------------------------------------------------------------------------*/
void CtiMCInterpreterPool::stopAndDestroyAllInterpreters()
{
    CtiLockGuard< CtiMutex > guard(_mux);

    set< CtiMCInterpreter* >::iterator iter;

    for( iter = _active_interps.begin();
         iter != _active_interps.end();
         iter++ )
    {
        (*iter)->stopEval();

        delete *iter;
    }

    for( iter = _available_interps.begin();
         iter != _available_interps.end();
         iter++ )
    {
        delete *iter;
    }

    _active_interps.clear();
    _available_interps.clear();
}

/*----------------------------------------------------------------------------
  dumpPool

  Dumps out the contents of pool

----------------------------------------------------------------------------*/
void CtiMCInterpreterPool::dumpPool()
{
    // lock the whole funtion, least output will be
    // contiguous
    CtiLockGuard< CtiLogger > guard(dout);

    dout << RWTime() << " Available interpreters:" << endl;

    interp_set_iter iter;
    for( iter = _available_interps.begin();
         iter != _available_interps.end();
         iter++ )
    {
        CtiMCInterpreter* interp = *iter;
        dout << RWTime() << interp << endl;
    }

    dout << RWTime() << " Active Interpreters: " << endl;

    for( iter = _active_interps.begin();
         iter != _active_interps.end();
         iter++ )
    {
        CtiMCInterpreter* interp = *iter;
        dout << RWTime() << interp << endl;
    }
}
