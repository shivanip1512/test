#include "precompiled.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "cms/ConnectionFactory.h"
#include "activemq/library/activemqcpp.h"
#include "amq_util.h"

namespace Cti {
namespace Messaging {
namespace ActiveMQ {

namespace {

struct ActiveMQIntializer
{
    ActiveMQIntializer()
    {
        activemq::library::ActiveMQCPP::initializeLibrary(); // can throw std::runtime_exception
    }

    ~ActiveMQIntializer()
    {
        activemq::library::ActiveMQCPP::shutdownLibrary(); // can throw std::runtime_exception
    }
};

boost::scoped_ptr<ActiveMQIntializer> g_activeMQIntializer;

}


ConnectionFactory::ConnectionFactory() :
    _isInitialized(false)
{
    InitializeCriticalSection(&_cs);
}

ConnectionFactory::~ConnectionFactory()
{
}

/*-----------------------------------------------------------------------------
    Intialize activemq library
-----------------------------------------------------------------------------*/
void ConnectionFactory::initializeLib()
{
    EnterCriticalSection(&_cs);

    if( g_activeMQIntializer.get() == NULL )
    {
        g_activeMQIntializer.reset( new ActiveMQIntializer );
    }

    LeaveCriticalSection(&_cs);

    _isInitialized = true;
}

/*-----------------------------------------------------------------------------
    Intialize activemq library and create a new connection

    returns:
    pointer to the new connection that the caller owns
-----------------------------------------------------------------------------*/
cms::Connection* ConnectionFactory::createConnection( const std::string &brokerUri )
{
    if( !_isInitialized )
    {
        initializeLib();
    }

    boost::scoped_ptr<cms::ConnectionFactory> connectionFactory( cms::ConnectionFactory::createCMSConnectionFactory( brokerUri ));

    return connectionFactory->createConnection();
}

/*-----------------------------------------------------------------------------
    Singleton of connectionFactory
-----------------------------------------------------------------------------*/
IM_EX_MSG ConnectionFactory g_connectionFactory;


}
}
}
