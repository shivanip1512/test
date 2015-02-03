
#pragma once

#include <memory>
#include <map>
#include <stdexcept>
#include <string>

#include <boost/make_shared.hpp>

#include "readers_writer_lock.h"
#include "VoltageRegulator.h"
#include "AttributeService.h"
#include "CapControlPointDataHandler.h"
#include "ccmessage.h"

namespace Cti           {
namespace CapControl    {

class VoltageRegulatorLoader;


class NoVoltageRegulator : public std::exception
{

public:

    NoVoltageRegulator(const long ID);

    virtual const char * what( ) const;

private:

    std::string _description;
};


class VoltageRegulatorManager
{

public:

    typedef boost::shared_ptr<VoltageRegulator> SharedPtr;
    typedef std::map<long, SharedPtr>           VoltageRegulatorMap;

    VoltageRegulatorManager( std::unique_ptr<VoltageRegulatorLoader> loader );

    void reload(const long Id);
    void reloadAll();

    void unload(const long Id);
    void unloadAll();

    SharedPtr getVoltageRegulator(const long Id) const;

    void setAttributeService(AttributeService * service);
    void setPointDataHandler(CapControlPointDataHandler * handler);

    VoltageRegulatorMessage * getVoltageRegulatorMessage(const bool sendAll);

private:

    typedef Cti::readers_writer_lock_t  Lock;
    typedef Lock::reader_lock_guard_t   ReaderGuard;
    typedef Lock::writer_lock_guard_t   WriterGuard;

    mutable Lock            _lock;

    VoltageRegulatorMap     _voltageRegulators;

    std::unique_ptr<VoltageRegulatorLoader>   _loader;

    AttributeService            * _service;
    CapControlPointDataHandler  * _handler;
};

}
}

