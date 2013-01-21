#include "precompiled.h"

#include "interp_pool.h"

using namespace std;

CtiInterpreterPool::CtiInterpreterPool(const std::set<std::string> commandsToEscape) :
        _commandsToEscape(commandsToEscape)
{
}

CtiInterpreterPool::~CtiInterpreterPool()
{
    stopAndDestroyAllInterpreters();
}

/*----------------------------------------------------------------------------
  acquireInterpreter

  Returns a pointer to an available interpreter.
  If none are available it will create a new interpreter and return it.

----------------------------------------------------------------------------*/
CtiInterpreter* CtiInterpreterPool::acquireInterpreter()
{
    CtiLockGuard< CtiMutex > guard(_mux);

    CtiInterpreter* interp = NULL;

    if( _available_interps.size() > 0 )
    {
        interp_set_iter iter = _available_interps.begin();
        interp = *iter;
        _available_interps.erase( iter );
    }
    else
    {
        interp = createInterpreter();
    }

    pair< set< CtiInterpreter* >::iterator, bool > result =
        _active_interps.insert( interp );

    assert( result.second );

    return interp;
}

/*----------------------------------------------------------------------------
  releaseInterpreter

  Returns an interpreter to the pool so it can be used again.

----------------------------------------------------------------------------*/
void CtiInterpreterPool::releaseInterpreter(CtiInterpreter* interp)
{
    CtiLockGuard< CtiMutex > guard(_mux);

    interp_set_iter iter = _active_interps.find(interp);

    assert( iter != _active_interps.end() );

    _active_interps.erase(iter);

    pair< set< CtiInterpreter* >::iterator, bool > result =
         _available_interps.insert( interp );

    assert( result.second );

    return;
}

/*----------------------------------------------------------------------------
  stopAndDestroyAllInterpreters

  Stops and deletes all interpreters in the pool

----------------------------------------------------------------------------*/
void CtiInterpreterPool::stopAndDestroyAllInterpreters()
{
    CtiLockGuard< CtiMutex > guard(_mux);

    set< CtiInterpreter* >::iterator iter;

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
        (*iter)->interrupt(CtiThread::SHUTDOWN);
        (*iter)->join();
        delete *iter;
    }

    _active_interps.clear();
    _available_interps.clear();
}


/*----------------------------------------------------------------------------
  dumpPool

  Dumps out the contents of pool

----------------------------------------------------------------------------*/
void CtiInterpreterPool::dumpPool()
{
    // lock the whole funtion, least output will be
    // contiguous
    CtiLockGuard< CtiLogger > guard(dout);

    dout << CtiTime() << " Available interpreters:" << endl;

    interp_set_iter iter;
    for( iter = _available_interps.begin();
         iter != _available_interps.end();
         iter++ )
    {
        CtiInterpreter* interp = *iter;
        dout << CtiTime() << interp << endl;
    }

    dout << CtiTime() << " Active Interpreters: " << endl;

    for( iter = _active_interps.begin();
         iter != _active_interps.end();
         iter++ )
    {
        CtiInterpreter* interp = *iter;
        dout << CtiTime() << interp << endl;
    }
}

/*----------------------------------------------------------------------------
  evalOnInit

  This string will be evaluated every time a new interpreter is initialized

----------------------------------------------------------------------------*/
void CtiInterpreterPool::evalOnInit(const string& cmd)
{
  CtiLockGuard< CtiMutex > guard(_mux);
  _init_cmd = cmd;
}

/*----------------------------------------------------------------------------
  createInterpreter

  Creates and initializes a new CtiInterpreter

----------------------------------------------------------------------------*/
CtiInterpreter* CtiInterpreterPool::createInterpreter()
{
  CtiInterpreter* interp = new CtiInterpreter(_commandsToEscape);
  interp->start();
  if(_init_cmd.length() > 0)
  {
    interp->evaluate(_init_cmd, true);
  }

  return interp;
}
