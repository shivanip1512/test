#pragma once

#include "dlldefs.h"
#include "socket_helper.h"
#include "streamSocketConnection.h"

namespace Cti {

//-----------------------------------------------------------------------------
//  Support listening to incoming TCP stream socket
//-----------------------------------------------------------------------------
class IM_EX_CTIBASE StreamSocketListener : private boost::noncopyable
{
    typedef Timing::Chrono Chrono;
    typedef StreamSocketConnection::ConnectionModes ConnectionModes;

    ServerSockets listeningSockets;

public:

    std::string Name;

    StreamSocketListener();
    virtual ~StreamSocketListener();

    bool isValid () const;
    bool create  (unsigned short nPort);
    void close   ();

    std::auto_ptr<StreamSocketConnection> accept (ConnectionModes mode, const Chrono &timeout, const HANDLE *hAbort);
};

} // namespace Cti
