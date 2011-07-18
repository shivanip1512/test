#pragma once
#include "yukon.h"

#include "dlldefs.h"
#include "netports.h"

#define  ERR_CTINEXUS_BASE                                   100000
#define  ERR_CTINEXUS_INVALID_HANDLE                         (ERR_CTINEXUS_BASE + 0)
#define  ERR_CTINEXUS_READTIMEOUT                            (ERR_CTINEXUS_BASE + 1)
#define  ERR_CTINEXUS_CANCELLATION                           (ERR_CTINEXUS_BASE + 2)

/* Nexus state flags.  */
#define  CTINEXUS_STATE_NULL                                 0x00000000    // Not connected.
#define  CTINEXUS_STATE_CONNECTED                            0x00000001
#define  CTINEXUS_STATE_WAITING                              0x00000002

/* Nexus definition flags */
#define  CTINEXUS_FLAG_READEXACTLY                           0x00000001    //  These are set ONLY on nexus creation by CTINexusOpen, and remain unchanged for the life of the nexus.
#define  CTINEXUS_FLAG_READANY                               0x00000002    //  These are set ONLY on nexus creation by CTINexusOpen, and remain unchanged for the life of the nexus.

/* Nexus Types */
#define  CTINEXUS_TYPE_SOCKTYPE                              0x00000010
#define  CTINEXUS_TYPE_UDPTYPE                               0x00000020

/* Nexus defines */
#define  CTINEXUS_INFINITE_TIMEOUT                          LONG_MIN    // MUST be negative to keep us from timing the thing!

class IM_EX_CTIBASE CtiConnect
{
public:
    virtual ~CtiConnect () {}; //The pure virtuals below make this an abstract class, the destructor needs a body.

    virtual ULONG CtiGetNexusState   () { return CTINEXUS_STATE_NULL; }
    virtual INT CTINexusClose        () { return 0; }
    virtual INT CTINexusWrite        (void *buf, ULONG len, PULONG BWritten, LONG TimeOut) { return 0; }
    virtual INT CTINexusRead         (void *buf, ULONG len, PULONG BRead, LONG TimeOut) { return 0; }
    virtual INT CTINexusPeek         (void *buf, ULONG len, PULONG BRead) { return 0; }

    virtual bool CTINexusValid() const { return false; }
};


