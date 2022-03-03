#include "precompiled.h"

#include "streamAmqConnection.h"
#include "millisecond_timer.h"
#include "win_helper.h"
#include "logger.h"

using std::endl;
using std::string;

namespace Cti {

namespace {

std::string getErrorMessage(int error)
{
    return StreamBuffer() <<"Error "<< error <<" -> "<< getSystemErrorMessage(error);
}

}

template <class Outbound, class Inbound>
StreamAmqConnection<Outbound, Inbound>::StreamAmqConnection(
        const Messaging::ActiveMQ::Queues::OutboundQueue &outbound,
        const Messaging::ActiveMQ::Queues::InboundQueue  &inbound) :
    _outbound(outbound)
{
    // enable manual reset
    _inboundEvent = CreateEvent(NULL, TRUE, FALSE, NULL);

    Messaging::ActiveMQConnectionManager::registerHandler(
            inbound,
            [this](const Messaging::ActiveMQConnectionManager::MessageDescriptor &md)
            {
                onMessage(md.msg);
            });
}

template <class Outbound, class Inbound>
StreamAmqConnection<Outbound, Inbound>::~StreamAmqConnection() 
{
    CloseHandle(_inboundEvent);
}

template <class Outbound, class Inbound>
bool StreamAmqConnection<Outbound, Inbound>::isValid() const
{
    return true;
}


template <class Outbound, class Inbound>
bool StreamAmqConnection<Outbound, Inbound>::write(const Outbound &out, const Chrono& timeout)
{
    return write(&out, sizeof(out),  timeout) == sizeof(out);
}

template <class Outbound, class Inbound>
size_t StreamAmqConnection<Outbound, Inbound>::write(const void *buf, int len, const Chrono& timeout)
{
    if( len != sizeof(Outbound) )
    {
        const std::string& desc = StreamBuffer() << "Write with len != sizeof(Outbound) ("<< len <<" != "<< sizeof(Outbound) <<")";
        CTILOG_ERROR(dout, desc);
        throw StreamConnectionException(desc);
    }

    const unsigned char *charBuf = reinterpret_cast<const unsigned char *>(buf);

    const Messaging::ActiveMQConnectionManager::SerializedMessage msg(charBuf, charBuf + len);

    Messaging::ActiveMQConnectionManager::enqueueMessage(_outbound, msg);

    return len;
}

template <class Outbound, class Inbound>
void StreamAmqConnection<Outbound, Inbound>::onMessage(const Messaging::ActiveMQConnectionManager::SerializedMessage &msg)
{
    std::lock_guard<std::mutex> guard(_inboundMux);

    if( msg.size() != sizeof(Inbound) )
    {
        CTILOG_ERROR(dout, "Inbound message received with len != sizeof(Inbound) (" << msg.size() << " != " << sizeof(Inbound) << ")");

        return;
    }

    _inbound.push(msg);

    if( ! SetEvent(_inboundEvent) )
    {
        const std::string desc = getErrorMessage(GetLastError());
        CTILOG_ERROR(dout, desc);
        //throw StreamConnectionException(desc);  //  don't throw - we're in the AMQ messenger thread
    }
}


template <class Outbound, class Inbound>
std::unique_ptr<Inbound> StreamAmqConnection<Outbound, Inbound>::read(const Chrono& timeout, const HANDLE *hAbort)
{
    auto in = std::make_unique<Inbound>();

    if( read(in.get(), sizeof(Inbound), timeout, hAbort) == sizeof(Inbound) )
    {
        return in;
    }

    return nullptr;
}

template <class Outbound, class Inbound>
size_t StreamAmqConnection<Outbound, Inbound>::read(void *buf, int len, const Chrono& timeout, const HANDLE *hAbort)
{
    if( len != sizeof(Inbound) )
    {
        const std::string& desc = StreamBuffer() <<"Read with len != sizeof(Inbound) ("<< len <<" != "<< sizeof(Inbound) <<")";
        CTILOG_ERROR(dout, desc)
        throw StreamConnectionException(desc);
    }

    std::vector<HANDLE> hEvents;

    if( hAbort )
    {
        hEvents.push_back(*hAbort);
    }

    hEvents.push_back(_inboundEvent);

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
                std::lock_guard<std::mutex> guard(_inboundMux);

                if( _inbound.empty() )
                {
                    CTILOG_ERROR(dout, "Data signaled, but no data in queue");

                    //  no data?  reset the event
                    if( ! ResetEvent(_inboundEvent) )
                    {
                        const std::string desc = getErrorMessage(GetLastError());
                        CTILOG_ERROR(dout, desc);
                        throw StreamConnectionException(desc);
                    }

                    return 0;
                }

                std::copy(_inbound.front().begin(), _inbound.front().end(), stdext::make_checked_array_iterator(reinterpret_cast<unsigned char *>(buf), len));

                _inbound.pop();

                if( _inbound.empty() && ! ResetEvent(_inboundEvent) )
                {
                    const std::string desc = getErrorMessage(GetLastError());
                    CTILOG_ERROR(dout, desc);
                    throw StreamConnectionException(desc);
                }

                return len;
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
size_t StreamAmqConnection<Outbound, Inbound>::peek(void *buf, int len)
{
    std::lock_guard<std::mutex> guard(_inboundMux);

    if( ! _inbound.empty() )
    {
        const auto &msg = _inbound.front();

        if( msg.size() >= len )
        {
            std::copy(msg.begin(), msg.begin() + len, stdext::make_checked_array_iterator(reinterpret_cast<unsigned char *>(buf), len));

            return len;
        }
    }

    return 0;
}

} // namespace Cti

#include "dsm2.h"

template class IM_EX_MSG Cti::StreamAmqConnection<CtiOutMessage, INMESS>;
template class IM_EX_MSG Cti::StreamAmqConnection<INMESS, CtiOutMessage>;

