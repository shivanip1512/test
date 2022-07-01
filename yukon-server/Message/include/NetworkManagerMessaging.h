#pragma once

#include "dlldefs.h"
#include "ctitime.h"

#include <atomic>
#include <string>

namespace Cti {
namespace Messaging {
namespace Rfn {

struct NetworkManagerRequestHeader
{
    std::string              clientGuid;
    long long                sessionId;
    std::optional<long long> groupId;
    long long                messageId;
    std::optional<long long> expiration;
    char                     priority : 7;
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
                                              const CtiTime & expiration,
                                              const char      priority );

    NetworkManagerRequestHeader makeNmHeader( const char priority );

    bool isActive() const;

private:

    const SessionInfo   _sessionInfo;
    std::atomic_int64_t _messageId;
};


class IM_EX_MSG SessionInfoManager
{
public:

    static void setClientGuid( const std::string & clientGuid );

    static SessionInfo getSessionInfo();

    static NetworkManagerRequestHeader getNmHeader( const long long groupId,
                                                    const CtiTime & expiration,
                                                    const char      priority );

    static NetworkManagerRequestHeader getNmHeader( const char priority );

    static bool isSessionActive();

protected:

    static std::unique_ptr<AppSessionId>    _appSession;
};



}
}
}
