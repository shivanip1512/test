#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include "cticonnect.h"
#include "dlldefs.h"
#include "netports.h"

#include <windows.h>
#include <limits.h>
#include <rw\thr\mutex.h>
#include <vector>

class IM_EX_CTIBASE CTINEXUS : public CtiConnect
{
private:

    typedef std::vector<char> buffer_t;
    buffer_t read_buffer;

public:
   ULONG          NexusType;        // What is this connection??
   CHAR           Name[64];         // Text Description of connection
   SOCKET         sockt;            // Nexuset ID/Handle
   SOCKADDR_IN    saServer;
   LPHOSTENT      lpHostEntry;

   ULONG          NexusState;
   ULONG          NexusFlags;       // set ONLY by CTINexusOpen

   CTINEXUS() :
      sockt(INVALID_SOCKET),
      NexusFlags(CTINEXUS_FLAG_READEXACTLY),
      NexusState(CTINEXUS_STATE_NULL)
   {}

   ~CTINEXUS()
   {}

   INT CTINexusCreate       (SHORT nPort);
   INT CTINexusClose        ();
   INT CTINexusConnect      (CTINEXUS *RemoteNexus, HANDLE *hAbort = NULL, LONG timeout = CTINEXUS_INFINITE_TIMEOUT, int flags = CTINEXUS_FLAG_READEXACTLY);
   INT CTINexusOpen         (CHAR *szServer, SHORT nPort, ULONG Flags);
   INT CTINexusWrite        (VOID *buf, ULONG len, PULONG BWritten, LONG TimeOut);
   INT CTINexusRead         (void *buf, ULONG len, PULONG BRead, LONG TimeOut);
   INT CTINexusPeek         (void *buf, ULONG len, PULONG BRead);
   INT CTINexusReportError  (CHAR *Label, INT Line, INT Error) const;
   INT CTINexusFlushInput   ();
   ULONG CtiGetNexusState   ();

   bool CTINexusValid() const;
   static bool CTINexusIsSocketError(INT Error);
   static bool CTINexusIsFatalSocketError(INT Error);

};


