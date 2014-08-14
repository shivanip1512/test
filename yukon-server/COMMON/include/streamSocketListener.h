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
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    StreamSocketListener(const StreamSocketListener&);
    StreamSocketListener& operator=(const StreamSocketListener&);

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
