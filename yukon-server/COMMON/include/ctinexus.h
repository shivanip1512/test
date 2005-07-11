#ifndef  __CTINEXUSS_H__
#define  __CTINEXUSS_H__

#include <windows.h>
#include <limits.h>

#include <rw\thr\mutex.h>

#include "dlldefs.h"
#include "netports.h"

#define  ERR_CTINEXUS_BASE                                   100000
#define  ERR_CTINEXUS_INVALID_HANDLE                         (ERR_CTINEXUS_BASE + 0)
#define  ERR_CTINEXUS_READTIMEOUT                            (ERR_CTINEXUS_BASE + 1)

/* Nexus state flags.  */
#define  CTINEXUS_STATE_NULL                                 0x00000000    // Not connected.
#define  CTINEXUS_STATE_CONNECTED                            0x00000001
#define  CTINEXUS_STATE_WAITING                              0x00000002

/* Nexus definition flags */
#define  CTINEXUS_FLAG_READEXACTLY                           0x00000001
#define  CTINEXUS_FLAG_READANY                               0x00000002

/* Nexus Types */
#define  CTINEXUS_TYPE_SOCKTYPE                              0x00000010
#define  CTINEXUS_TYPE_UDPTYPE                               0x00000020

/* Nexus defines */
#define  CTINEXUS_INFINITE_TIMEOUT                          LONG_MIN    // MUST be negative to keep us from timing the thing!

class IM_EX_CTIBASE CTINEXUS
{
public:
   ULONG          NexusType;        // What is this connection??
   CHAR           Name[64];         // Text Description of connection
   SOCKET         sockt;            // Nexuset ID/Handle
   SOCKADDR_IN    saServer;
   LPHOSTENT      lpHostEntry;

   ULONG          NexusFlags;
   ULONG          NexusState;

   CTINEXUS() :
      sockt(INVALID_SOCKET),
      NexusFlags(CTINEXUS_FLAG_READEXACTLY),
      NexusState(CTINEXUS_STATE_NULL)
   {}

   INT CTINexusCreate       (SHORT nPort);
   INT CTINexusClose        ();
   INT CTINexusConnect      (CTINEXUS *RemoteNexus, HANDLE *hAbort = NULL, LONG timeout = CTINEXUS_INFINITE_TIMEOUT, int flags = CTINEXUS_FLAG_READEXACTLY);
   INT CTINexusOpen         (CHAR *szServer, SHORT nPort, ULONG Flags);
   INT CTINexusWrite        (VOID *buf, ULONG len, PULONG BWritten, LONG TimeOut);
   INT CTINexusRead         (VOID *buf, ULONG len, PULONG BRead, LONG TimeOut);
   INT CTINexusPeek         (VOID *buf, ULONG len, PULONG BRead);
   INT CTINexusReportError  (CHAR *Label, INT Line, INT Error) const;
   INT CTINexusFlushInput   ();

   bool CTINexusValid() const;
   static bool CTINexusIsSocketError(INT Error);
   static bool CTINexusIsFatalSocketError(INT Error);

};

#endif   // #ifdef  __CTINEXUSS_H__

