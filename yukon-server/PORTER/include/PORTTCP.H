
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   PORTTCP
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/INCLUDE/PORTTCP.H-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tcpsup.h"


#define TCPIPBASESOCKET         1024

#define TCPIPINTHREADSTACKSIZE  32768
#define TCPIPOUTTHREADSTACKSIZE 32768
#define TCPIPCHECKTHREADSTACKSIZE 32768


/* Defines for Flags Member 1/25/99 CGP  */
#define TCPIP_FLAG_NEW     0x00000001           // A new TCPIP request has come in, don't reply to last one...

#if 0    // Moved into tcpsup.h
/* Define the structure that will be used for in/out communications */
typedef struct {
    USHORT PortNumber;
    struct sockaddr_in TCPIPReceiveClient;
    int TCPIPServerNexuset;
    int TCPIPSocket;
    USHORT TCPIPFailed;
    CTINEXUS ReturnNexus;
    HEV TCPIPFailedSem;
    HEV CommSem;
    ULONG Busy;
    ULONG   Flags;
    NETCXPORTINFO MyPortInfo;
} TCPIPSTRUCT;
#endif

int PortTCPIPStart (USHORT);
VOID SES92TCPIPInThread (PVOID);
VOID SES92TCPIPOutThread (PVOID);
VOID TCPIPCheckThread (PVOID);
VOID CCU710TCPIPInThread (PVOID);
VOID CCU710TCPIPOutThread (PVOID);
VOID WelcoTCPIPInThread (PVOID);
VOID WelcoTCPIPOutThread (PVOID);



