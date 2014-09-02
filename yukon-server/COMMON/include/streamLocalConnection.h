#pragma once

#include "streamConnection.h"
#include "critical_section.h"
#include "dlldefs.h"

#include <boost/ptr_container/ptr_set.hpp>

namespace Cti {

//-----------------------------------------------------------------------------
//  Stream local connection (inter-thread)
//-----------------------------------------------------------------------------
template <class Outbound, class Inbound>
class IM_EX_CTIBASE StreamLocalConnection : public StreamConnection
{
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    StreamLocalConnection(const StreamLocalConnection&);
    StreamLocalConnection& operator=(const StreamLocalConnection&);

    typedef boost::ptr_multiset<Outbound, priority_sort<Outbound> > queue_t;
    queue_t _outQueue;

    typedef StreamLocalConnection<Inbound, Outbound> DirectConnectionType;
    DirectConnectionType *_directConnection;

    mutable CtiCriticalSection _outQueueMux;

    HANDLE _dataAvailableEvent;

    enum ReadOptions
    {
        MessageRead = 0,
        MessagePeek
    };

    size_t readFromOutQueue (void *buf, int len, const Chrono& timeout, const HANDLE *hAbort, ReadOptions option);

public:

    StreamLocalConnection();
    virtual ~StreamLocalConnection();

    // virtual methods inherited from StreamConnection
    virtual bool   isValid () const                                                          override;
    virtual size_t write   (const void *buf, int len, const Chrono& timeout)                 override;
    virtual size_t read    (void *buf, int len, const Chrono& timeout, const HANDLE *hAbort) override;
    virtual size_t peek    (void *buf, int len)                                              override;

    void setMatchingConnection (StreamLocalConnection<Inbound, Outbound> &connection);
    void purgeRequest          (int request);

    // these function will access the matching connection readFromOutQueue()
    friend size_t DirectConnectionType::read (void *buf, int len, const Chrono& timeout, const HANDLE *hAbort);
    friend size_t DirectConnectionType::peek (void *buf, int len);
};

} // namespace Cti

#include "dsm2.h"

#pragma warning(disable:4661)

template class IM_EX_CTIBASE Cti::StreamLocalConnection<CtiOutMessage, INMESS>;
template class IM_EX_CTIBASE Cti::StreamLocalConnection<INMESS, CtiOutMessage>;
