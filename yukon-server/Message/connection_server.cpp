#include "precompiled.h"

#include "connection_server.h"
#include "amq_constants.h"
#include "amq_util.h"
#include "amq_connection.h"

#include <proton/target.hpp>


#include <atomic>

using namespace std;
using namespace Cti::Messaging::Qpid;

static std::atomic<long> serverConnectionCount = 0;

/**
 * class constructor
 * @param listenerConnection reference to the listener connection
 * @param listenerConnection reference to the listener connection
 * @param inQ queue containing messages received. if NULL, connection allocate its own queue
 * @param tt number of 1 sec iteration to allow the connection to send remaining messages
 */
CtiServerConnection::CtiServerConnection( const std::string & clientReplyDestination,
                                          const std::string & serverQueueName,
                                          Que_t *inQ,
                                          int termSeconds )
    :   CtiConnection( "Server Connection " + std::to_string(++serverConnectionCount), inQ, termSeconds ),
        _replyDest( clientReplyDestination )
{
    CTILOG_DEBUG( dout, who() << " - CtiServerConnection::CtiServerConnection() @0x" << std::hex << this );

    if( inQ )
    {
        inQ->setName( serverQueueName );
    }


    // use the same managed connection as the listener
//-- jmoc    _connection = listenerConnection.getConnection();

    _peerName = clientReplyDestination;

    setName( serverQueueName + _peerName );

    // consider server connections valid by default
    _valid = true;

    // initialize the session
    // jmoc - gotta fix this too...
    //_session = Cti::Messaging::ActiveMQConnectionManager::getSession(*this);

}

/**
 * class destructor
 */
CtiServerConnection::~CtiServerConnection()
{
    CTILOG_DEBUG( dout, who() << "- CtiServerConnection::~CtiServerConnection() @0x" << std::hex << this );

    try
    {
        cleanConnection();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() <<" - error cleaning the connection.");
    }
}

/**
 * establish a new connection : creates sessions, consumer and destination and
 * sends a reply message to the client connection
 * @return true if the connection has been establish, false otherwise
 */
bool CtiServerConnection::establishConnection()
{
    // waiting for the session to be completely initialized and open
    {
        std::unique_lock<std::mutex> lock(_sessionMutex);

        if ( _sessionCv.wait_for(
                lock,
                std::chrono::seconds{ 30 },
                [this]()
                {
                    return _connectionState == ConnectionState::Connected;
                } ) )
        {
            CTILOG_INFO( dout, who() << " - successfully connected"
                                << "\ninbound  : " << _consumer->getDestination()
                                << "\noutbound : " << _producer->getDestination()
            );

            return true;
        }        
    }

    CTILOG_ERROR( dout, who() << " - Failed to establish connection." );

    return false;
}

void CtiServerConnection::on_session_open( proton::session & s )
{
    _consumer = createTempQueueConsumer(
        s,
        [this]( proton::message & m )
        {
            if ( _connectionState == ConnectionState::WaitingForAck )
            {
                std::unique_lock<std::mutex> lock(_sessionMutex);

                _connectionState =
                    ( getJmsType( m ) == MessageType::clientAck )
                        ? ConnectionState::Connected
                        : ConnectionState::Error;

                _sessionCv.notify_one();
            }
            else
            {
                if ( _connectionState == ConnectionState::Connected )
                {
                    onMessage(m);
                }
            }
        },
        [this,&s]( proton::receiver & r )
        {
            _producer = createQueueProducer( s, _replyDest );

            proton::message m;

            m.to( _replyDest );
            m.reply_to( r.target().address() );
            setJmsType( m, MessageType::serverResp );

            _producer->send( m );

            // jmoc -- hey what about the stuff in _outQueue
        } );
}
