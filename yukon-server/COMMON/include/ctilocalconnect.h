#pragma once

#include "cticonnect.h"

#include "critical_section.h"
#include "dlldefs.h"

template <class Outbound, class Inbound>
class IM_EX_CTIBASE CtiLocalConnect : public CtiConnect
{
private:

    typedef std::multiset<Outbound *, ptr_priority_sort<Outbound> > queue_t;

    queue_t _outQueue;
    CtiLocalConnect<Inbound, Outbound> *_directConnection;
    ULONG _nexusState;

    CtiCriticalSection _crit;

    ULONG                   NexusType;        // What is this connection??
    CHAR                    Name[64];         // Text Description of connection

public:

    CtiLocalConnect() {};

    ~CtiLocalConnect();

    enum ReadFlags
    {
        NOFLAG = 0,
        MESSAGE_PEEK
    };

    ULONG CtiGetNexusState ();
    INT   CTINexusClose    ();
    INT   CTINexusWrite    (VOID *buf, ULONG len, PULONG BWritten, LONG TimeOut);
    INT   CTINexusRead     (void *buf, ULONG len, PULONG BRead, LONG TimeOut);
    INT   CTINexusPeek     (void *buf, ULONG len, PULONG BRead);

    bool  CTINexusValid    () const;

    int   CtiLocalConnectOpen ();
    INT   CtiLocalConnectRead (void *buf, ULONG len, PULONG BRead, LONG TimeOut, int flags = NOFLAG);

    bool setMatchingConnection( CtiLocalConnect<Inbound, Outbound> &connection );

    void purgeRequest(int request);
};

#include "dsm2.h"

#pragma warning(disable:4661)

template class IM_EX_CTIBASE CtiLocalConnect<CtiOutMessage, INMESS>;
template class IM_EX_CTIBASE CtiLocalConnect<INMESS, CtiOutMessage>;
