#include "precompiled.h"

#include "connection_client.h"
#include "dllbase.h"
#include "amq_constants.h"
#include "amq_util.h"
#include "amq_connection.h"
#include "GlobalSettings.h"

#include <proton/target.hpp>

#include <atomic>

using namespace std;
using namespace Cti::Messaging::Qpid;

namespace { // anonymous

template <typename MuxType>
struct InsideScope
{
    MuxType &_mux;
    bool    &_flag;

    InsideScope( MuxType& mux, bool &flag )
        :   _mux(mux),
            _flag(flag)
    {
        CtiLockGuard<MuxType> lock(_mux);
        _flag = true;
    }

    ~InsideScope()
    {
        CtiLockGuard<MuxType> lock(_mux);
        _flag = false;
    }
};

} // anonymous


static std::atomic_size_t clientConnectionCount = 0;

/**
 * class constructor
 * @param serverQueueName name of the queue to establish a connection with
 * @param inQ inbound queue containing messages received. if NULL, connection allocate its own queue
 * @param tt number of 1 sec iteration to allow the connection to send remaining messages
 */
CtiClientConnection::CtiClientConnection( const string &serverQueueName,
                                          Que_t *inQ,
                                          int termSeconds ) :
    CtiConnection( "Client Connection " + std::to_string(++clientConnectionCount), inQ, termSeconds ),
    _serverQueueName( serverQueueName ),
    _canAbortConn( false )
{

    // initialize the session
//    _session = Cti::Messaging::ActiveMQConnectionManager::getSession(*this);

}

/**
 * class destructor
 */
CtiClientConnection::~CtiClientConnection()
{
    CTILOG_DEBUG( dout, who() << "CtiClientConnection::~CtiClientConnection()" );

    try
    {
        cleanConnection();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() << " - error cleaning the connection");
    }
}

/**
 * Establish a new connection while _valid and _dontReconnect remains false.
 * uses failover with exponential backoff to connect with the broker
 * afterwards, sends a handshake message every 30 sec to the server listener connection
 * @return true if the connection has been establish, false otherwise
 */
bool CtiClientConnection::establishConnection()
{
    if ( ! Cti::Messaging::ActiveMQConnectionManager::getSession( *this )  )
    {
        return false;
    }


    // waiting for the session to be completely initialized and open
    {
        std::unique_lock<std::mutex> lock(_sessionMutex);

        if (_sessionCv.wait_for(
            lock,
            std::chrono::seconds{ 30 },         // jmoc -- how long? -- looks like below is an hour...?
            [this]()
            {
                return _connectionState == ConnectionState::Connected;
            }))
        {
            CTILOG_INFO(dout, who() << " - successfully connected"
                << "\ninbound  : " << _consumer->getDestination()
                << "\noutbound : " << _producer->getDestination()
            );

            return true;
        }
    }

    CTILOG_ERROR(dout, who() << " - Failed to establish connection.");

    return false;
}


void CtiClientConnection::on_session_open( proton::session & s )
{

    _consumer = createTempQueueConsumer(
        s,
        [this,&s](proton::message& m)
        {
            if (_handshakeProducer)
            {
                _handshakeProducer.reset();
            }

            if ( ! _producer )
            {
                std::unique_lock<std::mutex> lock(_sessionMutex);

                if ( getJmsType(m) == MessageType::serverResp )
                {
                    _producer = createQueueProducer(s, m.reply_to());

                    _connectionState = ConnectionState::Connected;

                    // send ClientAck ?? -- jmoc
                    proton::message ack;

              //      ack.to(_serverQueueName);
               //     ack.reply_to(r.target().address());
               //     setJmsType( ack, MessageType::clientAck );

                   
                    // writeRegistration();

                }
                else
                {
                    _connectionState = ConnectionState::Error;
                }
                
                _sessionCv.notify_one();
            }
            else
            {
                if ( _connectionState == ConnectionState::Connected )
                {
                    onMessage( m );
                }
            }
        },
        [this, &s](proton::receiver& r)
        {
            _handshakeProducer = createQueueProducer( s, _serverQueueName );

            proton::message m;

            m.to( _serverQueueName );
            m.reply_to( r.target().address() );
            setJmsType( m, MessageType::clientInit );

            _handshakeProducer->send( m );
        });
}


/**
 * Abort the connection attempt and disable reconnection
 */
void CtiClientConnection::abortConnection()
{
    try
    {
        CTILOCKGUARD(CtiMutex, lock, _abortConnMux);

        _dontReconnect = true;

//        if( _connection && _canAbortConn )
        {
            // Close the connection as well as any child session, consumer, producer
 // -- jmoc           _connection->close();
        }
    }
    catch(...)
    {
        // since we are shutting down, we dont care about exceptions
    }
}

/**
* Indicates whether the connection was re-established internally
*/
bool CtiClientConnection::hasReconnected()
{
    return _reconnected.exchange(false);
}

/**
 * send registration messages, if the connection was previously registered
 */
void CtiClientConnection::writeRegistration()
{
    try
    {
        if( _regMsg.get() ) // I know who I am....
        {
            CTILOG_DEBUG(dout, who() << " - re-registering connection.");

            sendMessage( *_regMsg );

            if( _ptRegMsg.get() ) // I know who I am....
            {
                sendMessage( *_ptRegMsg );
            }

            _reconnected.store(true);

            CTILOG_DEBUG(dout, who() << " - indicating reconnection.");
        }
    }
    catch( proton::error& e )
    {
        _valid = false; //sending data failed

        CTILOG_EXCEPTION_ERROR(dout, e, who() <<" - send registration message has failed");
    }
}

/**
 * copy and save a registration message
 * @param msg reference to the message to save
 */
void CtiClientConnection::recordRegistration( const CtiRegistrationMsg& msg )
{
    _regMsg = std::make_unique<CtiRegistrationMsg>(msg);
}

/**
 * copy and save a point registration message
 * @param msg reference to the message to save
 */
void CtiClientConnection::recordPointRegistration( const CtiPointRegistrationMsg& msg )
{
    //  Do not record incremental diff changes, since they are not valid to send alone
    if( msg.isAddingPoints() ||
        msg.isRemovingPoints() )
    {
        return;
    }
    _ptRegMsg = std::make_unique<CtiPointRegistrationMsg>(msg);
}

/**
 *  This method examines the message and records anything we care about
 * @param msg reference to the message to peek into
 */
void CtiClientConnection::messagePeek( const CtiMessage& msg )
{
    try
    {
        switch( msg.isA() )
        {
            case MSG_REGISTER:
            {
                if( auto regMsg = dynamic_cast<const CtiRegistrationMsg*>(&msg) )
                {
                    recordRegistration(*regMsg);
                }
                break;
            }
            case MSG_POINTREGISTRATION:
            {
                if( auto ptRegMsg = dynamic_cast<const CtiPointRegistrationMsg*>(&msg) )
                {
                    recordPointRegistration(*ptRegMsg);
                }
                break;
            }
            case MSG_MULTI:
            {
                if( auto pMulti = dynamic_cast<const CtiMultiMsg*>(&msg) )
                {
                    for( int i = 0; i < pMulti->getCount() && i < 3; i++ )    // Only look at the first three entries
                    {
                        messagePeek(*pMulti->getData()[i]);               // recurse.
                    }
                }
                break;
            }
        }
    }
    catch( const bad_cast& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, who() <<" - multi-message cast has failed");
    }
}
