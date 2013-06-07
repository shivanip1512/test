#pragma once

#include <string>
#include "dlldefs.h"
#include "readers_writer_lock.h"
#include "cms/Connection.h"
#include <activemq/commands/DestinationInfo.h>

namespace Cti {
namespace Messaging {
namespace ActiveMQ {

/*-----------------------------------------------------------------------------
  Initiliaze ActiveMQ Lib and create connection
-----------------------------------------------------------------------------*/
class IM_EX_MSG ConnectionFactory
{
    bool _isInitialized;

    CRITICAL_SECTION _cs;

    void initializeLib();

public:
    ConnectionFactory();
    virtual ~ConnectionFactory();

    cms::Connection* createConnection( const std::string &brokerUri );
};


/*-----------------------------------------------------------------------------
  Connection factory singleton
-----------------------------------------------------------------------------*/
IM_EX_MSG extern ConnectionFactory g_connectionFactory;


/*-----------------------------------------------------------------------------
  Message listener template
-----------------------------------------------------------------------------*/
template <class T>
class MessageListener : public cms::MessageListener
{
    typedef void (T::*onMessageFunc) ( const cms::Message* message );
    onMessageFunc _func;
    T* _caller;

public:
    MessageListener ( T* c, onMessageFunc f ) : _caller(c), _func( f ) {}
    virtual ~MessageListener () {}
    virtual void onMessage ( const cms::Message* message )
    {
        (_caller->*_func)( message );
    }
};


/*-----------------------------------------------------------------------------
  Exception listener template
-----------------------------------------------------------------------------*/
template <class T>
class ExceptionListener : public cms::ExceptionListener
{
    typedef void (T::*onExceptionFunc) ( const cms::CMSException& ex );
    onExceptionFunc _func;
    T* _caller;

public:
    ExceptionListener ( T* c, onExceptionFunc f ) : _caller(c), _func( f ) {}
    virtual ~ExceptionListener () {}
    virtual void onException ( const cms::CMSException& ex )
    {
        (_caller->*_func)( ex );
    }
};


/*-----------------------------------------------------------------------------
  Retrieve physical name from cms destination
-----------------------------------------------------------------------------*/
inline std::string destPhysicalName( const cms::Destination& dest )
{
    if( const activemq::commands::ActiveMQDestination* amqDest = dynamic_cast<const activemq::commands::ActiveMQDestination*>( &dest ))
    {
        return amqDest->getPhysicalName();
    }

    return std::string();
}


}
}
}
