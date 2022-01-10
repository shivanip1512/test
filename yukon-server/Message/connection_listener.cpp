#include "precompiled.h"

using namespace std;

#include <winsock2.h>
#include <ws2tcpip.h>
#include <windows.h>

#include <wtypes.h>

#include <stdlib.h>
#include <stdio.h>

#include <algorithm>
#include <iomanip>
#include <functional>

#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <set>
#include <chrono>

#include "connection_listener.h"
#include "dllbase.h"
#include "amq_constants.h"
#include "amq_util.h"
#include "logger.h"
#include "GlobalSettings.h"
#include "amq_connection.h"

#include <proton/types.hpp>
#include <proton/session_options.hpp>

#include <atomic>

using namespace Cti::Messaging::Qpid;


static std::atomic<long> listenerConnectionCount = 0;

/**
 * class constructor
 * @param serverQueueName name of the queue
 */
CtiListenerConnection::CtiListenerConnection( const string &serverQueueName ) :
    _serverQueueName( serverQueueName ),
    _title( "Listener Connection " + std::to_string(++listenerConnectionCount) + " \"" + _serverQueueName + "\"" ),
    _ready( false )
{
    // initialize the session

    // jmoc - gotta fix this
   //_session = Cti::Messaging::ActiveMQConnectionManager::getSession( *this );
}

void CtiListenerConnection::on_session_open( proton::session & session )
{
    // create the consumer
    _consumer = createQueueConsumer(
        session,
        _serverQueueName,
        [this](proton::message& m)
        {
            std::string jms_type;
            if (m.message_annotations().exists("x-opt-jms-type"))
            {
                jms_type = proton::get<std::string>(m.message_annotations().get("x-opt-jms-type"));
            }

            if (jms_type == MessageType::clientInit)
            {
                std::unique_lock<std::mutex> lock(_clientMux);

                _clients.push(m.reply_to());
                _clientCond.notify_one();       // signal acceptClient() that we got a valid response
            }
            else
            {
                CTILOG_ERROR(dout, "invalid queue consumer response message type: " << jms_type << " - replyTo: " << m.reply_to() );
            }
        });
    
    // signal acceptClient() that we are ready
    {
        std::unique_lock<std::mutex> lock(_clientMux);

        _ready = true;
        _clientCond.notify_one();
    }
}

/**
 * class destructor
 */
CtiListenerConnection::~CtiListenerConnection()
{
    try
    {
        // the consumer destructor calls close on the receiver
        
        // close the session
        _session.close();        
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, who() <<" - Error closing the connection.");
    }
}

/**
 * This is blocking function that waits for a client handshake message
 * @return the reply address
 */
std::string CtiListenerConnection::acceptClient()
{
    const auto timeOut = std::chrono::seconds(30);

    // waiting for the session to be completely initialized and open
    {
        std::unique_lock<std::mutex> lock(_clientMux);

        _clientCond.wait(
            lock,
            [this]()
            {
                return _ready;
            } );
    }

    // waiting for the client initialization response
    {
        std::unique_lock<std::mutex> lock(_clientMux);

        if (_clientCond.wait_for(
            lock,
            timeOut,
            [this]()
            {
                return !_clients.empty();
            } ) )
        {
            const auto clientReplyDest = _clients.front();
            _clients.pop();

            if (validateRequest(clientReplyDest))
            {
                return clientReplyDest;
            }
        }
        else
        {
            CTILOG_ERROR(dout, "timed out while waiting for reply");
        }
    }

    return "";
}

/**
 * check if request received is not a duplicate
 * @param replyTo physical name of reply destination
 * @return true if request is consider valid, false otherwise
 */
bool CtiListenerConnection::validateRequest( const string &replyTo )
{
    const CtiTime now;
    const CtiTime expired = now - 30; // 30 seconds - filter duplicate request younger then this

    // clean up expired
    DestTimeMap::iterator itr = requestTimeMap.begin();
    while( itr != requestTimeMap.end() )
    {
        if( itr->second <= expired )
        {
            requestTimeMap.erase(itr++);
        }
        else
        {
            ++itr;
        }
    }

    // try to insert
    const auto [iter, result] = requestTimeMap.emplace(replyTo, now);
    
    if ( ! result )
    {
        CTILOG_ERROR( dout, "Already seen destination: " << replyTo << " at: " << iter->second );
    }

    return result;
}

/**
 * name of the listener connection : title and server queue name
 * @return string with the name
 */
string CtiListenerConnection::who() const
{
    return _title;
}
