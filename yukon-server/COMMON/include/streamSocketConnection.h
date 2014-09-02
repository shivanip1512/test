#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include "streamConnection.h"
#include "dlldefs.h"
#include "netports.h"
#include "socket_helper.h"

#include <vector>

namespace Cti {

class StreamSocketListener;

//-----------------------------------------------------------------------------
//  Stream socket connection to read/write
//-----------------------------------------------------------------------------
class IM_EX_CTIBASE StreamSocketConnection : public StreamConnection
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    StreamSocketConnection(const StreamSocketConnection&);
    StreamSocketConnection& operator=(const StreamSocketConnection&);

public:

    // These are ONLY set when the connection is opened, and remain unchanged for the life of the connection.
    enum ConnectionModes
    {
        ReadExactly = 0,
        ReadAny
    };

    StreamSocketConnection();
    virtual ~StreamSocketConnection();

    // virtual methods inherited from StreamConnection
    virtual bool   isValid () const                                                          override;
    virtual size_t write   (const void *buf, int len, const Chrono& timeout)                 override;
    virtual size_t read    (void *buf, int len, const Chrono& timeout, const HANDLE *hAbort) override;
    virtual size_t peek    (void *buf, int len)                                              override;

    // methods added
    bool open       (const std::string &zServer, unsigned short nPort, ConnectionModes mode);
    void close      ();
    void flushInput ();
    void swap       (StreamSocketConnection& other);

    // allow accept method from listener to modify the connection
    friend class StreamSocketListener;

private:

    typedef std::vector<char> buffer_t;
    buffer_t _readBuffer;

    // locks are acquired in the following order
    // a) _socketMux only         (isValid, open, close)
    // b) _readMux  -> _socketMux (read, peek, flushInput, swap)
    // c) _writeMux -> _socketMux (write)
    mutable CtiCriticalSection _socketMux, _readMux, _writeMux;

    SOCKET               _socket;
    ConnectionModes      _connectionMode;
    SocketsEventsManager _socketsEventsManager;

    enum ReadOptions
    {
        MessageRead = 0,
        MessagePeek
    };

    size_t readFromSocket (void *buf, int len, const Chrono& timeout, const HANDLE *hAbort, ReadOptions option);
    SOCKET getSocket      (); // return the current socket handle using the _socketMux
};

} // namespace Cti
