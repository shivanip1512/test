#include "precompiled.h"

#include "logger.h"
#include "NetworkManagerMessaging.h"

#include <random>


namespace Cti       {
namespace Messaging {
namespace Rfn       {

AppSessionId::AppSessionId( const std::string appGuid )
    :   _sessionInfo{ appGuid, std::uniform_int_distribution<long long>()( std::mt19937_64( std::random_device()() ) ) },
        _messageId( _sessionInfo.sessionId )
{
    // empty
}

SessionInfo AppSessionId::getSessionInfo() const
{
    return _sessionInfo;
}

NetworkManagerRequestHeader AppSessionId::makeNmHeader( const long long groupId,
                                                        const CtiTime & expiration,
                                                        const char      priority )
{
    NetworkManagerRequestHeader header;

    header.clientGuid   = _sessionInfo.appGuid;
    header.sessionId    = _sessionInfo.sessionId;

    header.messageId    = ++_messageId;

    header.groupId      = groupId;
    header.expiration   = 1000 * expiration.seconds();
    header.priority     = priority;

    return header;
}

bool AppSessionId::isActive() const
{
    //  If we've generated a message, we're active
    return _sessionInfo.sessionId != _messageId;
}

std::unique_ptr<AppSessionId>    SessionInfoManager::_appSession;

void SessionInfoManager::setClientGuid( const std::string & clientGuid )
{
    _appSession = std::make_unique<AppSessionId>( clientGuid );
}

SessionInfo SessionInfoManager::getSessionInfo()
{
    if ( _appSession )
    {
        return _appSession->getSessionInfo();
    }

    CTILOG_ERROR( dout, "Failed to read Client Guid and session ID" );

    throw std::runtime_error( "Failed to read Client Guid and session ID" );
}

NetworkManagerRequestHeader SessionInfoManager::getNmHeader( const long long groupId,
                                                             const CtiTime & expiration,
                                                             const char      priority )
{
    if ( _appSession )
    {
        return _appSession->makeNmHeader( groupId, expiration, priority );
    }

    CTILOG_ERROR( dout, "Failed to generate Network Manager Request header" );

    throw std::runtime_error( "Failed to generate Network Manager Request header" );
}

bool SessionInfoManager::isSessionActive()
{
    return _appSession && _appSession->isActive();
}


}
}
}

