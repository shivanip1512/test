#pragma once

#include "dlldefs.h"

#include <atomic>
#include <string>

namespace Cti {
namespace Messaging {
namespace Rfn {

struct NetworkManagerRequestHeader
{
    std::string clientGuid;
    long long sessionId;
    long long groupId;
    long long messageId;
    long long expiration;
    char priority : 7;
};


struct SessionInfo
{
    const std::string   appGuid;
    const long long     sessionId;
};


class IM_EX_MSG AppSessionId
{
public:

    AppSessionId( const std::string appGuid );

    SessionInfo getSessionInfo() const;

    NetworkManagerRequestHeader makeNmHeader( const long long groupId,
                                              const long long expiration_ms,
                                              const char      priority );

private:

    const SessionInfo   _sessionInfo;
    std::atomic_int64_t _messageId;
};


class IM_EX_MSG SessionInfoManager
{
public:

    static void setNmHeaderInfo( std::unique_ptr<AppSessionId> && appID ); 

    static SessionInfo getSessionInfo();

    static NetworkManagerRequestHeader getNmHeader( const long long groupId,
                                                    const long long expiration_ms,
                                                    const char      priority );

protected:

    static std::unique_ptr<AppSessionId>    _appSession;
};



}
}
}
