#include "precompiled.h"

#include "interp_pool.h"

#include "logger.h"

using namespace std;

CtiInterpreterPool::CtiInterpreterPool(CtiInterpreter::InitFunction initFunction, const std::set<std::string> commandsToEscape) :
    _initFunction(initFunction),
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
  toString()

  Dumps out the contents of pool

----------------------------------------------------------------------------*/
std::string CtiInterpreterPool::toString() const
{
    Cti::StreamBuffer out;

    out << endl <<"Available interpreters:";
    for each(const CtiInterpreter* interp in _available_interps)
    {
        out << endl << interp; // print the pointer
    }

    out << endl <<"Active Interpreters:";
    for each(const CtiInterpreter* interp in _active_interps)
    {
        out << endl << interp; // print the pointer
    }

    return out;
}

/*----------------------------------------------------------------------------
  createInterpreter

  Creates and initializes a new CtiInterpreter

----------------------------------------------------------------------------*/
CtiInterpreter* CtiInterpreterPool::createInterpreter()
{
  CtiInterpreter* interp = new CtiInterpreter(_initFunction, _commandsToEscape);
  interp->start();

  return interp;
}
