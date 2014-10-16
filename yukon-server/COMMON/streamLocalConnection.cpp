#include "precompiled.h"

#include "logger.h"
#include "millisecond_timer.h"
#include "win_helper.h"
#include "streamLocalConnection.h"

using std::endl;
using std::string;

namespace Cti {

namespace {

std::string getErrorMessage(int error)
{
    return StreamBuffer() <<"Error "<< error <<" -> "<< getSystemErrorMessage(error);
}

} // namespace anonymous

template <class Outbound, class Inbound>
StreamLocalConnection<Outbound, Inbound>::StreamLocalConnection() :
    _directConnection(0)
{
    // enable manual reset
    _dataAvailableEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
    if( _dataAvailableEvent == (HANDLE)NULL )
    {
        CTILOG_FATAL(dout, getErrorMessage(GetLastError()));
        exit(-1);
    }
}

template <class Outbound, class Inbound>
StreamLocalConnection<Outbound, Inbound>::~StreamLocalConnection()
{
    CloseHandle(_dataAvailableEvent);
}

template <class Outbound, class Inbound>
size_t StreamLocalConnection<Outbound, Inbound>::write(const void *buf, int len, const Chrono& timeout)
{
    if( ! isValid() )
    {
        const char* desc = "Write attempted on invalid connection";
        CTILOG_ERROR(dout, desc);
        throw StreamConnectionException(desc);
    }

    if( len != sizeof(Outbound) )
    {
        const std::string& desc = StreamBuffer() << "Write with len != sizeof(Outbound) ("<< len <<" != "<< sizeof(Outbound) <<")";
        CTILOG_ERROR(dout, desc);
        throw StreamConnectionException(desc);
    }

    try
    {
        CtiLockGuard< CtiCriticalSection > guard(_outQueueMux);

        _outQueue.insert(new Outbound(*(static_cast<const Outbound*>(buf))));

        if( ! SetEvent(_dataAvailableEvent) )
        {
            const std::string desc = getErrorMessage(GetLastError());
            CTILOG_ERROR(dout, desc);
            throw StreamConnectionException(desc);
        }

        return len;
    }
    catch( const StreamConnectionException & )
    {
        throw;
    }
    catch(...)
    {
        const char* desc = "Unhandled exception caught";
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, desc);
        throw StreamConnectionException(desc);
    }
}

template <class Outbound, class Inbound>
size_t StreamLocalConnection<Outbound, Inbound>::read(void *buf, int len, const Chrono& timeout, const HANDLE *hAbort)
{
    if( ! isValid() )
    {
        const char* desc = "Read attempted on invalid connection";
        CTILOG_ERROR(dout, desc);
        throw StreamConnectionException(desc);
    }

    return _directConnection->readFromOutQueue(buf, len, timeout, hAbort, DirectConnectionType::MessageRead);
}

template <class Outbound, class Inbound>
size_t StreamLocalConnection<Outbound, Inbound>::peek(void *buf, int len)
{
    if( ! isValid() )
    {
        const char* desc = "Peek attempted on invalid connection";
        CTILOG_ERROR(dout, desc);
        throw StreamConnectionException(desc);
    }

    return _directConnection->readFromOutQueue(buf, len, Chrono::milliseconds(0), NULL, DirectConnectionType::MessagePeek);
}

template <class Outbound, class Inbound>
size_t StreamLocalConnection<Outbound, Inbound>::readFromOutQueue(void *buf, int len, const Chrono& timeout, const HANDLE *hAbort, ReadOptions option)
{
    if( len != sizeof(Outbound) )
    {
        const std::string& desc = StreamBuffer() <<"Read with len != sizeof(Outbound) ("<< len <<" != "<< sizeof(Outbound) <<")";
        CTILOG_ERROR(dout, desc)
        throw StreamConnectionException(desc);
    }

    std::vector<HANDLE> hEvents;

    if( hAbort )
    {
        hEvents.push_back(*hAbort);
    }

    hEvents.push_back(_dataAvailableEvent);

    const DWORD nCount = hEvents.size();
    DWORD waitMillis   = timeout ? timeout.milliseconds() : INFINITE;

    Timing::MillisecondTimer timer;

    for(;;)
    {
        const DWORD waitResult = WaitForMultipleObjects(nCount, &hEvents[0], false, waitMillis);

        switch(waitResult)
        {
        case WAIT_OBJECT_0:
            {
                if( hAbort )
                {
                    return 0; // aborted !
                }
            }
        case WAIT_OBJECT_0 + 1:
            {
                try
                {
                    CtiLockGuard<CtiCriticalSection> g(_outQueueMux);

                    queue_t::iterator itr = _outQueue.begin();

                    if( itr != _outQueue.end() )
                    {
                        memcpy(buf, &(*itr), len);

                        if( option == MessageRead )
                        {
                            _outQueue.erase(itr);

                            // reset event if outQueue is empty
                            if( _outQueue.empty() && ! ResetEvent(_dataAvailableEvent) )
                            {
                                const std::string desc = getErrorMessage(GetLastError());
                                CTILOG_ERROR(dout, desc);
                                throw StreamConnectionException(desc);
                            }
                        }

                        return len; // data found
                    }

                    break; // break-away from the switch
                }
                catch( const StreamConnectionException & )
                {
                    throw;
                }
                catch(...)
                {
                    const char* desc = "Unhandled exception caught";
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, desc);
                    throw StreamConnectionException(desc);
                }
            }
        case WAIT_TIMEOUT:
            {
                return 0; // timeout !
            }
        case WAIT_FAILED:
            {
                const std::string desc = getErrorMessage(GetLastError());
                CTILOG_ERROR(dout, desc);
                throw StreamConnectionException(desc);
            }
        default:
            {
                const char* desc = "WaitForMultipleObjects returned an unexpected value";
                CTILOG_ERROR(dout, desc);
                throw StreamConnectionException(desc);
            }
        }

        if( timeout )
        {
            // update the remaining time
            const unsigned long elapsed = timer.elapsed();
            waitMillis = elapsed < timeout.milliseconds() ? timeout.milliseconds() - elapsed : 0;
        }
    }
}

template <class Outbound, class Inbound>
bool StreamLocalConnection<Outbound, Inbound>::isValid() const
{
    return _directConnection;
}

template <class Outbound, class Inbound>
void StreamLocalConnection<Outbound, Inbound>::setMatchingConnection( StreamLocalConnection<Inbound, Outbound> &connection )
{
    _directConnection = &connection;
}

void StreamLocalConnection<OUTMESS, INMESS>::purgeRequest(int request)
{
    CtiLockGuard< CtiCriticalSection > guard(_outQueueMux);

    queue_t::iterator itr = _outQueue.begin();

    while( itr != _outQueue.end() )
    {
        if( itr->Request.GrpMsgID == request )
        {
            itr = _outQueue.erase(itr);
        }
        else
        {
            itr++;
        }
    }

    // reset event if outQueue is empty
    if( _outQueue.empty() && ! ResetEvent(_dataAvailableEvent) )
    {
        const std::string desc = getErrorMessage(GetLastError());
        CTILOG_ERROR(dout, desc);
        throw StreamConnectionException(desc);
    }
}

} // namespace Cti
