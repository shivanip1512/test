#include "precompiled.h"

#include "cparms.h"
#include "logger.h"
#include "millisecond_timer.h"
#include "win_helper.h"
#include "streamLocalConnection.h"

using std::endl;
using std::string;

namespace Cti {

namespace {

void logError(const char *label, int line, const std::string &desc)
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime() << " **** ERROR **** " << label << " (" << line << "): " << desc << endl;
}

void logErrorAndThrowException(const char *label, int line, const std::string &desc)
{
    logError(label, line, desc);
    throw StreamConnectionException(desc);
}

std::string getErrorMessage(int error)
{
    std::ostringstream desc;
    desc << "Error " << error << " -> " << getSystemErrorMessage(error);
    return desc.str();
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
        logError(__FILE__, __LINE__, getErrorMessage(GetLastError()));
        exit(-1);
    }
}

template <class Outbound, class Inbound>
StreamLocalConnection<Outbound, Inbound>::~StreamLocalConnection()
{
    CloseHandle(_dataAvailableEvent);
}

template <class Outbound, class Inbound>
int StreamLocalConnection<Outbound, Inbound>::write(void *buf, int len, const Chrono& timeout)
{
    if( ! isValid() )
    {
        logErrorAndThrowException(__FILE__, __LINE__, "Write attempted on invalid connection");
    }

    if( len != sizeof(Outbound) )
    {
        std::ostringstream desc;
        desc << "Write with len != sizeof(Outbound) (" << len << " != " << sizeof(Outbound) << ")";
        logErrorAndThrowException(__FILE__, __LINE__, desc.str());
    }

    try
    {
        CtiLockGuard< CtiCriticalSection > guard(_outQueueMux);

        _outQueue.insert(new Outbound(*(static_cast<Outbound*>(buf))));

        if( ! SetEvent(_dataAvailableEvent) )
        {
            logErrorAndThrowException(__FILE__, __LINE__, getErrorMessage(GetLastError()));
        }

        return len;
    }
    catch( const StreamConnectionException & )
    {
        throw;
    }
    catch(...)
    {
        logErrorAndThrowException(__FILE__, __LINE__, "Unhandled exception caught");
    }
}

template <class Outbound, class Inbound>
int StreamLocalConnection<Outbound, Inbound>::read(void *buf, int len, const Chrono& timeout, const HANDLE *hAbort)
{
    if( ! isValid() )
    {
        logErrorAndThrowException(__FILE__, __LINE__, "Read attempted on invalid connection");
    }

    return _directConnection->readFromOutQueue(buf, len, timeout, hAbort, DirectConnectionType::MessageRead);
}

template <class Outbound, class Inbound>
int StreamLocalConnection<Outbound, Inbound>::peek(void *buf, int len)
{
    if( ! isValid() )
    {
        logErrorAndThrowException(__FILE__, __LINE__, "Peek attempted on invalid connection");
    }

    return _directConnection->readFromOutQueue(buf, len, Chrono::milliseconds(0), NULL, DirectConnectionType::MessagePeek);
}

template <class Outbound, class Inbound>
int StreamLocalConnection<Outbound, Inbound>::readFromOutQueue(void *buf, int len, const Chrono& timeout, const HANDLE *hAbort, ReadOptions option)
{
    if( len != sizeof(Outbound) )
    {
        std::ostringstream desc;
        desc << "Read with len != sizeof(Outbound) (" << len << " != " << sizeof(Outbound) << ")";
        logErrorAndThrowException(__FILE__, __LINE__, desc.str());
    }

    std::vector<HANDLE> hEvents;

    if( hAbort )
    {
        hEvents.push_back(*hAbort);
    }

    hEvents.push_back(_dataAvailableEvent);

    DWORD waitMillis = timeout ? timeout.milliseconds() : INFINITE;

    Timing::MillisecondTimer timer;

    for(;;)
    {
        const DWORD waitResult = WaitForMultipleObjects(hEvents.size(), &hEvents[0], false, waitMillis);

        if( hAbort && waitResult == WAIT_OBJECT_0 )
        {
            return 0; // aborted
        }

        if( waitResult == WAIT_FAILED )
        {
            logErrorAndThrowException(__FILE__, __LINE__, getErrorMessage(GetLastError()));
        }

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
                        logErrorAndThrowException(__FILE__, __LINE__, getErrorMessage(GetLastError()));
                    }
                }

                return len; // data found
            }
        }
        catch( const StreamConnectionException & )
        {
            throw;
        }
        catch(...)
        {
            logErrorAndThrowException(__FILE__, __LINE__, "Unhandled exception caught");
        }

        // update the remaining time
        if( timeout )
        {
            const unsigned long elapsed = timer.elapsed();
            waitMillis = elapsed < timeout.milliseconds() ? timeout.milliseconds() - elapsed : 0;
            if( ! waitMillis )
            {
                return 0; // timeout !
            }
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
        logErrorAndThrowException(__FILE__, __LINE__, getErrorMessage(GetLastError()));
    }
}

} // namespace Cti
