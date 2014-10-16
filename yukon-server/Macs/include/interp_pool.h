#pragma once

#include "interp.h"

#include <set>

class CtiInterpreterPool : public Cti::Loggable
{
public:

    CtiInterpreterPool(CtiInterpreter::InitFunction initFunction, const std::set<std::string> commandsToEscape);
    virtual ~CtiInterpreterPool();

    CtiInterpreter* acquireInterpreter();
    void releaseInterpreter(CtiInterpreter* interp);

    void stopAndDestroyAllInterpreters();

    // dumps the active and available interpreters to dout
    std::string toString() const override;

private:
    typedef std::set< CtiInterpreter* >::iterator interp_set_iter;

    // Used to protect the interpreter sets
    CtiMutex _mux;

    const std::set<std::string> _commandsToEscape;

    std::set< CtiInterpreter* > _available_interps;
    std::set< CtiInterpreter* > _active_interps;

    CtiInterpreter::InitFunction _initFunction;

    CtiInterpreter* createInterpreter();
};
