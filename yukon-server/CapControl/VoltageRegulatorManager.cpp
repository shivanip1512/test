

#include "precompiled.h"

#include "VoltageRegulatorManager.h"
#include "VoltageRegulatorLoader.h"


namespace Cti           {
namespace CapControl    {

NoVoltageRegulator::NoVoltageRegulator(const long ID)
    : std::exception(),
      _description("No Voltage Regulator found with ID: ")
{
    _description += CtiNumStr(ID);
}


const char * NoVoltageRegulator::what( ) const
{
    return _description.c_str();
}



VoltageRegulatorManager::VoltageRegulatorManager( std::unique_ptr<VoltageRegulatorLoader> loader )
    : _loader( std::move(loader) ),
      _handler(0)
{

}


void VoltageRegulatorManager::reload(const long Id)
{
    VoltageRegulatorMap results = _loader->load(Id);

    // update the mapping with the results of the loading
    {
        WriterGuard guard(_lock);

        for ( VoltageRegulatorMap::const_iterator b = results.begin(), e = results.end(); b != e; ++b )
        {
            unload( b->first );

            if ( _service )
            {
                results[ b->first ]->loadAttributes( _service );
            }

            _voltageRegulators[ b->first ] = results[ b->first ];

            if ( _handler )
            {
                VoltageRegulator::IDSet points = b->second->getRegistrationPoints();
                for each ( VoltageRegulator::IDSet::value_type ID in points )
                {
                    _handler->addPoint( ID, b->first );
                }
            }
        }
    }
}


void VoltageRegulatorManager::reloadAll()
{
    reload(-1);
}


void VoltageRegulatorManager::unload(const long Id)
{
    WriterGuard guard(_lock);

    if ( _handler )
    {
        VoltageRegulatorMap::const_iterator iter = _voltageRegulators.find(Id);
        if ( iter != _voltageRegulators.end() )
        {
            _handler->removeAllPointsForPao(Id);
        }
    }

    _voltageRegulators.erase(Id);
}


void VoltageRegulatorManager::unloadAll()
{
    WriterGuard guard(_lock);

    if ( _handler )
    {
        for each ( VoltageRegulatorMap::value_type x in _voltageRegulators )
        {
            _handler->removeAllPointsForPao(x.first);
        }
    }

    _voltageRegulators.clear();
}


VoltageRegulatorManager::SharedPtr VoltageRegulatorManager::getVoltageRegulator(const long Id) const
{
    ReaderGuard guard(_lock);

    VoltageRegulatorMap::const_iterator iter = _voltageRegulators.find(Id);

    if ( iter == _voltageRegulators.end() )
    {
        throw NoVoltageRegulator(Id);
    }

    return iter->second;
}


void VoltageRegulatorManager::setAttributeService(AttributeService * service)
{
    _service = service;
}


void VoltageRegulatorManager::setPointDataHandler(CapControlPointDataHandler * handler)
{
    _handler = handler;
}


VoltageRegulatorMessage * VoltageRegulatorManager::getVoltageRegulatorMessage(const bool sendAll)
{
    VoltageRegulatorMessage * message = 0;

    for each ( VoltageRegulatorMap::value_type x in _voltageRegulators )
    {
        SharedPtr   regulator = x.second;

        if ( regulator->isUpdated() || sendAll )
        {
            if ( message == 0 )
            {
                message = new VoltageRegulatorMessage();
            }
            regulator->setUpdated(false);
            message->insert( regulator.get() );
        }
    }
    return message;
}

}
}

