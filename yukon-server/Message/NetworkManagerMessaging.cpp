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
                                                        const long long expiration_ms,
                                                        const char      priority )
{
    NetworkManagerRequestHeader header;

    header.clientGuid   = _sessionInfo.appGuid;
    header.sessionId    = _sessionInfo.sessionId;

    header.messageId    = ++_messageId;

    header.groupId      = groupId;
    header.expiration   = expiration_ms;
    header.priority     = priority;

    return header;
}

std::unique_ptr<AppSessionId>    SessionInfoManager::_appSession;

void SessionInfoManager::setNmHeaderInfo( std::unique_ptr<Messaging::Rfn::AppSessionId> && appID )
{
    _appSession = std::move( appID );
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
                                                             const long long expiration_ms,
                                                             const char      priority )
{
    if ( _appSession )
    {
        return _appSession->makeNmHeader( groupId, expiration_ms, priority );
    }

    CTILOG_ERROR( dout, "Failed to generate Network Manager Request header" );

    throw std::runtime_error( "Failed to generate Network Manager Request header" );
}

}
}
}

